/**
 * Cloudflare Worker - Rating Handler
 *
 * POST /       : salva um rating para uma página
 * GET /stats   : retorna média e total de ratings de uma página
 *
 * Bindings necessários (configurados via wrangler.toml):
 * - RATINGS_KV : Cloudflare KV Namespace
 *
 * Variáveis de ambiente (configuradas no Cloudflare Dashboard):
 * - GITHUB_TOKEN : token do GitHub com escopo repo (opcional, só para backup)
 * - REPO_INFO    : "owner/repo", ex: "Maiconjh/Schema-Validator"
 */

const MAX_PAYLOAD_SIZE = 1024;
const MAX_ENTRIES = 1000;
const MAX_RETENTION_DAYS = 90;
const RATE_LIMIT_TTL = 3600;

const ALLOWED_ORIGINS = [
  'https://maiconjh.github.io',
  'http://localhost:4000',
  'http://localhost:3000'
];

export default {
  async fetch(request, env, ctx) {
    const url = new URL(request.url);
    const clientIP = request.headers.get('cf-connecting-ip') || 'unknown';

    const origin = request.headers.get('Origin');
    const corsHeaders = {
      'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type',
    };

    if (origin && ALLOWED_ORIGINS.includes(origin)) {
      corsHeaders['Access-Control-Allow-Origin'] = origin;
    } else if (!origin) {
      corsHeaders['Access-Control-Allow-Origin'] = '*';
    } else {
      return new Response('Forbidden', {
        status: 403,
        headers: { 'Content-Type': 'application/json' }
      });
    }

    if (request.method === 'OPTIONS') {
      return new Response(null, { headers: corsHeaders });
    }

    // ========== GET /stats ==========
    if (request.method === 'GET' && url.pathname === '/stats') {
      const page = url.searchParams.get('page');

      if (!page) {
        return new Response(JSON.stringify({ error: 'Missing page parameter' }), {
          status: 400,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      }

      if (!isValidPage(page)) {
        return new Response(JSON.stringify({ error: 'Invalid page parameter' }), {
          status: 400,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      }

      try {
        const ratings = await getRatingsFromKV(env, page);
        const total = ratings.length;
        const average = total > 0
          ? ratings.reduce((sum, r) => sum + r.rating, 0) / total
          : 0;

        return new Response(JSON.stringify({ average, total_ratings: total }), {
          status: 200,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      } catch {
        return new Response(JSON.stringify({ error: 'Failed to fetch stats' }), {
          status: 500,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      }
    }

    // ========== POST / ==========
    if (request.method !== 'POST') {
      return new Response('Method not allowed', { status: 405, headers: corsHeaders });
    }

    try {
      // Verifica Content-Length antes de ler o body
      const contentLength = parseInt(request.headers.get('content-length') || '0');
      if (contentLength > MAX_PAYLOAD_SIZE) {
        return new Response(JSON.stringify({ error: 'Payload too large' }), {
          status: 413,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      }

      const body = await request.json();

      // Verificação secundária pós-parse (Content-Length pode ser omitido)
      if (JSON.stringify(body).length > MAX_PAYLOAD_SIZE) {
        return new Response(JSON.stringify({ error: 'Payload too large' }), {
          status: 413,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      }

      const { rating, page } = body;

      if (
        typeof rating !== 'number' ||
        !Number.isInteger(rating) ||
        rating < 1 ||
        rating > 5
      ) {
        return new Response(JSON.stringify({ error: 'Invalid rating. Must be an integer between 1 and 5.' }), {
          status: 400,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      }

      if (!page) {
        return new Response(JSON.stringify({ error: 'Missing page' }), {
          status: 400,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      }

      if (!isValidPage(page)) {
        return new Response(JSON.stringify({ error: 'Invalid page format. Only letters, numbers, hyphen, underscore and slash allowed.' }), {
          status: 400,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      }

      // Rate limiting via KV com TTL — funciona entre todos os isolates
      const rateLimitKey = `ratelimit:${clientIP}:${page}`;
      const rateLimitHit = await env.RATINGS_KV.get(rateLimitKey);
      if (rateLimitHit) {
        return new Response(JSON.stringify({ error: 'You have already rated this page recently. Please try again later.' }), {
          status: 429,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      }

      await env.RATINGS_KV.put(rateLimitKey, '1', { expirationTtl: RATE_LIMIT_TTL });

      // Lê ratings existentes do KV
      const existingRatings = await getRatingsFromKV(env, page);

      // Aplica política de retenção
      const cutoffDate = new Date();
      cutoffDate.setDate(cutoffDate.getDate() - MAX_RETENTION_DAYS);
      let filteredRatings = existingRatings.filter(r => new Date(r.timestamp) >= cutoffDate);

      if (filteredRatings.length >= MAX_ENTRIES) {
        filteredRatings = filteredRatings.slice(-MAX_ENTRIES + 1);
      }

      filteredRatings.push({
        rating,
        timestamp: new Date().toISOString(),
        page
      });

      // Salva no KV
      await env.RATINGS_KV.put(`ratings:${page}`, JSON.stringify(filteredRatings));

      // Backup assíncrono para GitHub — não bloqueia a resposta
      if (env.GITHUB_TOKEN) {
        ctx.waitUntil(backupToGitHub(page, filteredRatings, env));
      }

      const total = filteredRatings.length;
      const average = total > 0
        ? filteredRatings.reduce((sum, r) => sum + r.rating, 0) / total
        : 0;

      return new Response(JSON.stringify({ success: true, average, total_ratings: total }), {
        status: 200,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      });

    } catch {
      return new Response(JSON.stringify({ error: 'Internal server error' }), {
        status: 500,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      });
    }
  }
};

async function getRatingsFromKV(env, page) {
  try {
    const raw = await env.RATINGS_KV.get(`ratings:${page}`);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

async function backupToGitHub(page, ratings, env) {
  try {
    const { owner, repo, token } = getRepoConfig(env);
    if (!token) return;

    const filePath = 'docs/pages/feedbacks.json';
    const apiUrl = `https://api.github.com/repos/${owner}/${repo}/contents/${filePath}`;

    let sha = null;
    let allFeedbacks = [];

    const getResponse = await fetch(apiUrl, {
      headers: {
        'Authorization': `token ${token}`,
        'Accept': 'application/vnd.github.v3+json',
        'User-Agent': 'Schema-Validator-Feedback-Worker'
      }
    });

    if (getResponse.ok) {
      const data = await getResponse.json();
      sha = data.sha;
      if (data.content) {
        allFeedbacks = JSON.parse(decodeBase64(data.content));
      }
    }

    allFeedbacks = allFeedbacks.filter(f => f.page !== page);
    allFeedbacks = allFeedbacks.concat(ratings);

    const commitBody = {
      message: `Backup ratings for ${sanitizePage(page)}`,
      content: encodeBase64(JSON.stringify(allFeedbacks, null, 2)),
      ...(sha && { sha })
    };

    await fetch(apiUrl, {
      method: 'PUT',
      headers: {
        'Authorization': `token ${token}`,
        'Accept': 'application/vnd.github.v3+json',
        'Content-Type': 'application/json',
        'User-Agent': 'Schema-Validator-Feedback-Worker'
      },
      body: JSON.stringify(commitBody)
    });
  } catch {
    // Falha silenciosa — KV já tem os dados, backup é opcional
  }
}

function getRepoConfig(env) {
  const repoInfo = env.REPO_INFO || 'Maiconjh/Schema-Validator';
  const [owner, repo] = repoInfo.split('/');
  return { owner, repo, token: env.GITHUB_TOKEN };
}

function isValidPage(page) {
  return /^[a-zA-Z0-9\-_./]+$/.test(page);
}

function sanitizePage(page) {
  return page
    .replace(/[\r\n\t]/g, ' ')
    .replace(/"/g, "'")
    .replace(/\\/g, '/')
    .substring(0, 100);
}

function encodeBase64(str) {
  const encoder = new TextEncoder();
  const data = encoder.encode(str);
  return btoa(Array.from(data, b => String.fromCharCode(b)).join(''));
}

function decodeBase64(base64) {
  const binString = atob(base64);
  const bytes = Uint8Array.from(binString, c => c.charCodeAt(0));
  return new TextDecoder().decode(bytes);
}