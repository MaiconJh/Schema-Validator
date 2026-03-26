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
  async fetch(request, env, ctx) {
    console.log('Worker received:', request.method, request.url);
    
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
      const { feedback, page, userAgent } = body;

      // Validate required fields
      if (!feedback || !['yes', 'no'].includes(feedback)) {
        return new Response(JSON.stringify({ error: 'Invalid feedback value' }), {
          status: 400,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
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
        feedback,
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
            'Accept': 'application/vnd.github.v3+json'
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
        message: `Add user feedback: ${feedback}`,
        content: contentEncoded,
        sha: sha
      };

      // Commit to GitHub
      const commitResponse = await fetch(apiUrl, {
        method: 'PUT',
        headers: {
          'Authorization': `token ${token}`,
          'Accept': 'application/vnd.github.v3+json',
          'Content-Type': 'application/json'
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