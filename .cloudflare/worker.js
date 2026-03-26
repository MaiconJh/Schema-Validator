/**
 * Cloudflare Worker - Feedback Handler
 * 
 * Recebe feedbacks das páginas de documentação e os adiciona ao arquivo JSON
 * no repositório GitHub via API REST.
 * 
 * Configuração no Cloudflare Dashboard:
 * 1. Workers & Pages → Create application → Worker
 * 2. Settings → Variables → Add GITHUB_TOKEN e REPO_INFO
 * 3. Adicionar route: docs/* (se quiser limitar ao domínio das docs)
 */

export default {
  // In-memory rate limiting store (resets on worker restart)
  rateLimitStore: new Map(),
  
  async fetch(request, env, ctx) {
    console.log('Worker received:', request.method, request.url);
    
    // Get client IP from Cloudflare headers
    const clientIP = request.headers.get('cf-connecting-ip') || 'unknown';
    
    // CORS headers
    const corsHeaders = {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'POST, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type',
    };

    // Handle preflight
    if (request.method === 'OPTIONS') {
      console.log('Handling OPTIONS preflight');
      return new Response(null, { headers: corsHeaders });
    }

    // Only accept POST requests
    if (request.method !== 'POST') {
      console.log('Method not allowed:', request.method);
      return new Response('Method not allowed', { status: 405, headers: corsHeaders });
    }

    try {
      // Parse the request body
      const body = await request.json();
      const { rating, page, userAgent } = body;

      // Validate required fields (rating 1-5)
      if (!rating || isNaN(rating) || rating < 1 || rating > 5) {
        return new Response(JSON.stringify({ error: 'Invalid rating value. Must be 1-5.' }), {
          status: 400,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      }

      // Rate limiting: 1 rating per IP + page combination per hour
      const rateLimitKey = `${clientIP}:${page || 'unknown'}`;
      const lastRatingTime = this.rateLimitStore.get(rateLimitKey);
      const now = Date.now();
      const oneHour = 60 * 60 * 1000;
      
      if (lastRatingTime && (now - lastRatingTime) < oneHour) {
        console.log('Rate limited:', rateLimitKey);
        return new Response(JSON.stringify({ error: 'You have already rated this page recently. Please try again later.' }), {
          status: 429,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      }
      
      // Store the rating time
      this.rateLimitStore.set(rateLimitKey, now);

      // Clean up old entries (simple cleanup every 100 requests)
      if (this.rateLimitStore.size > 100) {
        const cutoff = now - oneHour;
        for (const [key, time] of this.rateLimitStore) {
          if (time < cutoff) this.rateLimitStore.delete(key);
        }
      }

      // Get configuration from environment variables
      const repoInfo = env.REPO_INFO || 'Maiconjh/Schema-Validator';
      const token = env.GITHUB_TOKEN;

      if (!token) {
        console.error('GITHUB_TOKEN not configured');
        return new Response(JSON.stringify({ error: 'Server configuration error' }), {
          status: 500,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      }

      const [owner, repo] = repoInfo.split('/');
      const filePath = 'docs/pages/feedbacks.json';
      const apiUrl = `https://api.github.com/repos/${owner}/${repo}/contents/${filePath}`;

      // Prepare feedback data
      const feedbackData = {
        rating,
        timestamp: new Date().toISOString(),
        page: page || 'unknown',
        userAgent: userAgent || 'unknown',
        referrer: request.headers.get('referer') || 'unknown'
      };

      // Get current file content to preserve existing data
      let existingFeedbacks = [];
      let sha = null;

      try {
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
            existingFeedbacks = JSON.parse(atob(data.content));
          }
        }
      } catch (err) {
        console.log('File does not exist yet, creating new');
      }

      // Add new feedback
      existingFeedbacks.push(feedbackData);

      // Prepare the commit
      const fileContent = JSON.stringify(existingFeedbacks, null, 2);
      const contentEncoded = btoa(fileContent);

      const commitBody = {
        message: `Add user rating: ${rating} stars`,
        content: contentEncoded,
        sha: sha
      };

      // Commit to GitHub
      const commitResponse = await fetch(apiUrl, {
        method: 'PUT',
        headers: {
          'Authorization': `token ${token}`,
          'Accept': 'application/vnd.github.v3+json',
          'Content-Type': 'application/json',
          'User-Agent': 'Schema-Validator-Feedback-Worker'
        },
        body: JSON.stringify(commitBody)
      });

      if (!commitResponse.ok) {
        const errorText = await commitResponse.text();
        console.error('GitHub API error:', errorText);
        return new Response(JSON.stringify({ error: 'Failed to save feedback' }), {
          status: 500,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      }

      const result = await commitResponse.json();

      // Success response
      return new Response(JSON.stringify({ 
        success: true, 
        message: 'Feedback saved successfully',
        commit: result.commit?.sha
      }), {
        status: 200,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      });

    } catch (error) {
      console.error('Worker error:', error);
      return new Response(JSON.stringify({ error: 'Internal server error' }), {
        status: 500,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      });
    }
  }
};