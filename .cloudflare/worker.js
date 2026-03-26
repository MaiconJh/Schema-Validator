/**
 * Cloudflare Worker - Feedback Handler (privado)
 * 
 * Recebe avaliações (1 a 5 estrelas) e atualiza o arquivo JSON no repositório GitHub.
 * Não armazena dados pessoais (IP, userAgent) no repositório público.
 * 
 * Configuração:
 * - Variáveis de ambiente: GITHUB_TOKEN, REPO_OWNER, REPO_NAME, ALLOWED_ORIGIN
 * - KV namespace: RATE_LIMIT_KV (para controle de duplicatas e rate limit)
 */

export default {
  async fetch(request, env, ctx) {
    // ========== 1. CORS e pré‑flight ==========
    const origin = request.headers.get('Origin');
    const allowedOrigins = env.ALLOWED_ORIGIN ? [env.ALLOWED_ORIGIN] : [];
    const isAllowed = origin && allowedOrigins.includes(origin);
    const corsHeaders = {
      'Access-Control-Allow-Methods': 'POST, GET, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type',
    };
    if (isAllowed) {
      corsHeaders['Access-Control-Allow-Origin'] = origin;
    }

    if (request.method === 'OPTIONS') {
      return new Response(null, { headers: corsHeaders });
    }

    // ========== 2. Rota GET /stats ==========
    const url = new URL(request.url);
    if (request.method === 'GET' && url.pathname === '/stats') {
      const page = url.searchParams.get('page');
      if (!page) {
        return new Response('Missing page', { status: 400, headers: corsHeaders });
      }
      const stats = await getStats(page, env);
      return new Response(JSON.stringify(stats), {
        status: 200,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      });
    }

    // ========== 3. Apenas POST ==========
    if (request.method !== 'POST') {
      return new Response('Method not allowed', { status: 405, headers: corsHeaders });
    }

    // Validação de origem
    if (!isAllowed) {
      return new Response('Forbidden', { status: 403, headers: corsHeaders });
    }

    // ========== 4. Parse e validação do corpo ==========
    let body;
    try {
      body = await request.json();
    } catch {
      return new Response(JSON.stringify({ error: 'Invalid JSON' }), {
        status: 400,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      });
    }

    const { rating, page, token } = body;
    if (!rating || !Number.isInteger(rating) || rating < 1 || rating > 5) {
      return new Response(JSON.stringify({ error: 'Rating must be 1-5' }), {
        status: 400,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      });
    }
    if (!page || typeof page !== 'string') {
      return new Response(JSON.stringify({ error: 'Missing page' }), {
        status: 400,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      });
    }
    if (!token || typeof token !== 'string') {
      return new Response(JSON.stringify({ error: 'Missing token' }), {
        status: 400,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      });
    }

    // ========== 5. Rate limit por IP (KV) ==========
    const clientIp = request.headers.get('CF-Connecting-IP') || 'unknown';
    const limitKey = `ratelimit:${clientIp}:${page}`;
    const rateLimit = await env.RATE_LIMIT_KV.get(limitKey, 'json');
    if (rateLimit && rateLimit.count >= 5) { // 5 avaliações por IP/página em 24h
      return new Response(JSON.stringify({ error: 'Too many requests' }), {
        status: 429,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      });
    }
    // Incrementa contador
    const newCount = (rateLimit?.count || 0) + 1;
    await env.RATE_LIMIT_KV.put(limitKey, JSON.stringify({ count: newCount }), { expirationTtl: 86400 });

    // ========== 6. Verificar duplicata por token ==========
    const voteKey = `vote:${page}:${token}`;
    const existingVote = await env.RATE_LIMIT_KV.get(voteKey);
    if (existingVote) {
      return new Response(JSON.stringify({ error: 'Already voted' }), {
        status: 409,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      });
    }
    // Registra o voto por 30 dias
    await env.RATE_LIMIT_KV.put(voteKey, '1', { expirationTtl: 2592000 });

    // ========== 7. Atualizar arquivo no GitHub ==========
    const { REPO_OWNER, REPO_NAME, GITHUB_TOKEN } = env;
    if (!REPO_OWNER || !REPO_NAME || !GITHUB_TOKEN) {
      console.error('Missing environment variables');
      return new Response(JSON.stringify({ error: 'Server config error' }), {
        status: 500,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      });
    }

    const filePath = '_data/feedbacks.json';
    const apiUrl = `https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/contents/${filePath}`;

    let currentData = { ratings: [], aggregates: {} };
    let sha = null;

    try {
      const getRes = await fetch(apiUrl, {
        headers: {
          Authorization: `Bearer ${GITHUB_TOKEN}`,
          Accept: 'application/vnd.github.v3+json',
          'User-Agent': 'Feedback-Worker',
        },
      });
      if (getRes.ok) {
        const data = await getRes.json();
        sha = data.sha;
        const content = data.content;
        // Decodifica base64 (suporta Unicode)
        const decoded = new TextDecoder('utf-8').decode(Uint8Array.from(atob(content), c => c.charCodeAt(0)));
        currentData = JSON.parse(decoded);
      } else if (getRes.status !== 404) {
        throw new Error('GitHub API error');
      }
    } catch (err) {
      console.error('Error reading file:', err);
      return new Response(JSON.stringify({ error: 'Failed to read feedback data' }), {
        status: 500,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      });
    }

    // Adiciona nova avaliação (sem dados pessoais)
    currentData.ratings.push({
      page,
      rating,
      timestamp: new Date().toISOString(),
    });

    // Atualiza agregados
    if (!currentData.aggregates[page]) {
      currentData.aggregates[page] = { total_ratings: 0, sum_ratings: 0, average: 0 };
    }
    const agg = currentData.aggregates[page];
    agg.total_ratings += 1;
    agg.sum_ratings += rating;
    agg.average = parseFloat((agg.sum_ratings / agg.total_ratings).toFixed(2));

    // Escreve de volta no GitHub
    const newContent = JSON.stringify(currentData, null, 2);
    const encoder = new TextEncoder();
    const contentBytes = encoder.encode(newContent);
    const contentBase64 = btoa(String.fromCharCode(...contentBytes));

    const payload = {
      message: `Rating: ${rating} stars for ${page}`,
      content: contentBase64,
      sha,
      branch: 'main',
    };

    const putRes = await fetch(apiUrl, {
      method: 'PUT',
      headers: {
        Authorization: `Bearer ${GITHUB_TOKEN}`,
        Accept: 'application/vnd.github.v3+json',
        'Content-Type': 'application/json',
        'User-Agent': 'Feedback-Worker',
      },
      body: JSON.stringify(payload),
    });

    if (!putRes.ok) {
      const errorText = await putRes.text();
      console.error('GitHub commit error:', errorText);
      return new Response(JSON.stringify({ error: 'Failed to save feedback' }), {
        status: 500,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' },
      });
    }

    // ========== 8. Resposta de sucesso ==========
    return new Response(JSON.stringify({
      success: true,
      average: agg.average,
      total_ratings: agg.total_ratings,
    }), {
      status: 200,
      headers: { ...corsHeaders, 'Content-Type': 'application/json' },
    });
  },
};

// Função auxiliar para buscar estatísticas (usada no endpoint GET /stats)
async function getStats(page, env) {
  const { REPO_OWNER, REPO_NAME, GITHUB_TOKEN } = env;
  const filePath = '_data/feedbacks.json';
  const apiUrl = `https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/contents/${filePath}`;

  try {
    const res = await fetch(apiUrl, {
      headers: {
        Authorization: `Bearer ${GITHUB_TOKEN}`,
        Accept: 'application/vnd.github.v3+json',
      },
    });
    if (!res.ok) {
      return { total_ratings: 0, sum_ratings: 0, average: 0 };
    }
    const data = await res.json();
    const content = data.content;
    const decoded = new TextDecoder('utf-8').decode(Uint8Array.from(atob(content), c => c.charCodeAt(0)));
    const feedbacks = JSON.parse(decoded);
    const agg = feedbacks.aggregates[page] || { total_ratings: 0, sum_ratings: 0, average: 0 };
    return agg;
  } catch {
    return { total_ratings: 0, sum_ratings: 0, average: 0 };
  }
}