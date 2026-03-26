/**
 * Cloudflare Worker - Rating Handler
 * 
 * Handles star ratings for documentation pages.
 * - POST / : saves a rating for a page
 * - GET /stats?page=... : returns average rating and total count for a page
 * 
 * Environment variables:
 * - GITHUB_TOKEN : GitHub personal access token (repo scope)
 * - REPO_INFO : owner/repo name, e.g., "Maiconjh/Schema-Validator"
 */

// Configuration constants
const MAX_PAYLOAD_SIZE = 1024; // 1KB max request body
const MAX_ENTRIES = 1000; // Keep only last 1000 ratings to prevent infinite growth
const MAX_RETENTION_DAYS = 90; // Keep ratings for 90 days

// Allowed origins for CORS (restrictive by default)
const ALLOWED_ORIGINS = [
  'https://maiconjh.github.io',
  'http://localhost:4000',
  'http://localhost:3000'
];

export default {
  // In-memory rate limiting store (resets on worker restart)
  rateLimitStore: new Map(),
  lastCleanup: Date.now(),
  
  async fetch(request, env, ctx) {
    const url = new URL(request.url);
    const clientIP = request.headers.get('cf-connecting-ip') || 'unknown';
    
    // Restrictive CORS - only allow specific origins
    const origin = request.headers.get('Origin');
    const corsHeaders = {
      'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type',
    };
    
    // Only allow specific origins (restrictive CORS)
    if (origin && ALLOWED_ORIGINS.includes(origin)) {
      corsHeaders['Access-Control-Allow-Origin'] = origin;
    } else if (!origin) {
      // Same-origin request
      corsHeaders['Access-Control-Allow-Origin'] = '*';
    } else {
      // Origin not allowed - return 403
      return new Response('Forbidden', { 
        status: 403, 
        headers: { 'Content-Type': 'application/json' }
      });
    }

    // Handle preflight
    if (request.method === 'OPTIONS') {
      return new Response(null, { headers: corsHeaders });
    }

    // Periodic rate limit cleanup every 5 minutes
    await this.cleanupRateLimit();

    // ========== GET /stats ==========
    if (request.method === 'GET' && url.pathname === '/stats') {
      const page = url.searchParams.get('page');
      if (!page) {
        return new Response(JSON.stringify({ error: 'Missing page parameter' }), {
          status: 400,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      }

      // Validate page parameter
      if (!isValidPage(page)) {
        return new Response(JSON.stringify({ error: 'Invalid page parameter' }), {
          status: 400,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      }

      try {
        const { owner, repo, token } = getRepoConfig(env);
        if (!token) {
          return new Response(JSON.stringify({ error: 'Server configuration error' }), { status: 500, headers: corsHeaders });
        }

        const filePath = 'docs/pages/feedbacks.json';
        const apiUrl = `https://api.github.com/repos/${owner}/${repo}/contents/${filePath}`;

        // Fetch current feedbacks
        const getResponse = await fetch(apiUrl, {
          headers: {
            'Authorization': `token ${token}`,
            'Accept': 'application/vnd.github.v3+json',
            'User-Agent': 'Schema-Validator-Feedback-Worker'
          }
        });

        let ratings = [];
        if (getResponse.ok) {
          const data = await getResponse.json();
          if (data.content) {
            const allFeedbacks = JSON.parse(decodeBase64(data.content));
            // Filter ratings for this page
            ratings = allFeedbacks.filter(f => f.page === page);
          }
        }

        const total = ratings.length;
        const average = total > 0 ? ratings.reduce((sum, r) => sum + r.rating, 0) / total : 0;

        return new Response(JSON.stringify({ average, total_ratings: total }), {
          status: 200,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      } catch (err) {
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
      // Parse request body
      const body = await request.json();
      
      // Validate actual payload size after parsing (prevent JSON bomb attacks)
      const bodyString = JSON.stringify(body);
      if (bodyString.length > MAX_PAYLOAD_SIZE) {
        return new Response(JSON.stringify({ error: 'Payload too large' }), {
          status: 413,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      }

      const { rating, page } = body;

      // Validate rating
      if (!rating || isNaN(rating) || rating < 1 || rating > 5) {
        return new Response(JSON.stringify({ error: 'Invalid rating. Must be 1-5.' }), {
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

      // Validate page parameter with regex (letters, numbers, hyphen, underscore, slash only)
      if (!isValidPage(page)) {
        return new Response(JSON.stringify({ error: 'Invalid page format. Only letters, numbers, hyphen, underscore and slash allowed.' }), {
          status: 400,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      }

      // Rate limiting: 1 rating per IP + page per hour
      const rateLimitKey = `${clientIP}:${page}`;
      const now = Date.now();
      const oneHour = 60 * 60 * 1000;
      const lastRatingTime = this.rateLimitStore.get(rateLimitKey);
      if (lastRatingTime && (now - lastRatingTime) < oneHour) {
        return new Response(JSON.stringify({ error: 'You have already rated this page recently. Please try again later.' }), {
          status: 429,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      }
      this.rateLimitStore.set(rateLimitKey, now);
      
      // Get repo config
      const { owner, repo, token } = getRepoConfig(env);
      if (!token) {
        return new Response(JSON.stringify({ error: 'Server configuration error' }), {
          status: 500,
          headers: { ...corsHeaders, 'Content-Type': 'application/json' }
        });
      }

      const filePath = 'docs/pages/feedbacks.json';
      const apiUrl = `https://api.github.com/repos/${owner}/${repo}/contents/${filePath}`;

      // Prepare feedback data (without userAgent and referrer for privacy)
      const feedbackData = {
        rating,
        timestamp: new Date().toISOString(),
        page
      };

      // Retry logic for race conditions (up to 3 attempts with backoff)
      let attempts = 0;
      const maxAttempts = 3;
      let lastError = null;
      
      while (attempts < maxAttempts) {
        attempts++;
        
        // Get current file content
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
              existingFeedbacks = JSON.parse(decodeBase64(data.content));
            }
          }
        } catch (err) {
          // File doesn't exist, will create new
        }

        // Apply retention policy: remove entries older than MAX_RETENTION_DAYS
        const cutoffDate = new Date();
        cutoffDate.setDate(cutoffDate.getDate() - MAX_RETENTION_DAYS);
        existingFeedbacks = existingFeedbacks.filter(f => {
          const entryDate = new Date(f.timestamp);
          return entryDate >= cutoffDate;
        });

        // Limit total entries to prevent infinite growth
        if (existingFeedbacks.length > MAX_ENTRIES) {
          existingFeedbacks = existingFeedbacks.slice(-MAX_ENTRIES);
        }

        // Add new feedback
        existingFeedbacks.push(feedbackData);

        // Sanitize page string for commit message (remove problematic characters)
        const sanitizedPage = sanitizePage(page);

        // Commit to GitHub
        const commitBody = {
          message: `Add rating ${rating} stars for ${sanitizedPage}`,
          content: encodeBase64(JSON.stringify(existingFeedbacks, null, 2)),
          sha: sha
        };

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

        if (commitResponse.ok) {
          // Success - compute updated stats for this page
          const pageRatings = existingFeedbacks.filter(f => f.page === page);
          const total = pageRatings.length;
          const average = total > 0 ? pageRatings.reduce((sum, r) => sum + r.rating, 0) / total : 0;

          return new Response(JSON.stringify({
            success: true,
            average,
            total_ratings: total
          }), {
            status: 200,
            headers: { ...corsHeaders, 'Content-Type': 'application/json' }
          });
        }

        // Handle 409 Conflict (concurrent updates) - retry with backoff
        if (commitResponse.status === 409) {
          lastError = 'Conflict detected, retrying...';
          // Exponential backoff: 100ms, 200ms, 300ms
          await new Promise(r => setTimeout(r, 100 * attempts));
          continue;
        }

        // For other errors, break the loop
        const errorText = await commitResponse.text();
        lastError = 'Failed to save rating';
        break;
      }

      // All retries exhausted
      return new Response(JSON.stringify({ error: lastError || 'Failed to save rating' }), {
        status: 500,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      });

    } catch (error) {
      return new Response(JSON.stringify({ error: 'Internal server error' }), {
        status: 500,
        headers: { ...corsHeaders, 'Content-Type': 'application/json' }
      });
    }
  },

  // Periodic cleanup every 5 minutes
  async cleanupRateLimit() {
    const now = Date.now();
    const fiveMinutes = 5 * 60 * 1000;
    
    if (now - this.lastCleanup > fiveMinutes) {
      const oneHour = 60 * 60 * 1000;
      const cutoff = now - oneHour;
      
      for (const [key, time] of this.rateLimitStore) {
        if (time < cutoff) {
          this.rateLimitStore.delete(key);
        }
      }
      
      this.lastCleanup = now;
    }
  }
};

function getRepoConfig(env) {
  const repoInfo = env.REPO_INFO || 'Maiconjh/Schema-Validator';
  const [owner, repo] = repoInfo.split('/');
  return { owner, repo, token: env.GITHUB_TOKEN };
}

// Validate page parameter - only allow letters, numbers, hyphen, underscore, and slash
function isValidPage(page) {
  const pageRegex = /^[a-zA-Z0-9\-_/]+$/;
  return pageRegex.test(page);
}

// Sanitize page string for GitHub commit message
function sanitizePage(page) {
  // Remove or replace characters that could cause issues in commit messages
  return page
    .replace(/[\r\n\t]/g, ' ')  // Replace newlines and tabs with spaces
    .replace(/"/g, "'")         // Replace double quotes with single quotes
    .replace(/\\/g, '/')       // Replace backslashes with forward slashes
    .substring(0, 100);        // Limit length to 100 characters
}

// Base64 encoding using TextEncoder (supports Unicode)
function encodeBase64(str) {
  const encoder = new TextEncoder();
  const data = encoder.encode(str);
  const binString = Array.from(data, (byte) => String.fromCharCode(byte)).join('');
  return btoa(binString);
}

// Base64 decoding using TextDecoder (supports Unicode)
function decodeBase64(base64) {
  const binString = atob(base64);
  const bytes = Uint8Array.from(binString, (c) => c.charCodeAt(0));
  const decoder = new TextDecoder();
  return decoder.decode(bytes);
}
