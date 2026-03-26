# Cloudflare Worker - Feedback Handler

This Worker receives star ratings from documentation pages and stores them in Cloudflare KV, with an asynchronous backup to the GitHub repository.

## Setup

### 1. Create a GitHub Personal Access Token (PAT)

1. Go to: https://github.com/settings/tokens
2. Click **"Generate new token (classic)"**
3. **Note**: `Feedback API`
4. **Expiration**: Choose "No expiration"
5. **Scopes**: Check `repo` (required to write to the repository)
6. Copy the generated token

### 2. Create the KV Namespaces on Cloudflare

1. Go to: https://dash.cloudflare.com
2. Navigate to **Workers & Pages** → **KV**
3. Click **Create namespace**
4. Create two namespaces:
   - `RATINGS_KV` (development)
   - `RATINGS_KV_PROD` (production)
5. Copy both namespace IDs

### 3. Configure GitHub Secrets

Go to your repository → **Settings** → **Secrets and variables** → **Actions** → **New repository secret**

| Secret | Value |
|---|---|
| `CLOUDFLARE_API_TOKEN` | Create at Cloudflare → My Profile → API Tokens → Create Token → "Edit Cloudflare Workers" template |
| `RATINGS_KV_ID` | Your development KV namespace ID |
| `RATINGS_KV_PROD_ID` | Your production KV namespace ID |

### 4. Configure Cloudflare Environment Variables

Go to **Workers & Pages** → **feedback-handler** → **Settings** → **Variables and Secrets**

| Variable | Value |
|---|---|
| `GITHUB_TOKEN` | Your GitHub PAT |
| `REPO_INFO` | `Maiconjh/Schema-Validator` (or your repo) |

### 5. Configure the Jekyll Site

After deploying, you will have a URL like:
`https://feedback-handler.your-subdomain.workers.dev`

Add it to `docs/pages/_config.yml`:

```yaml
feedback_worker_url: "https://feedback-handler.your-subdomain.workers.dev"
```

### 6. Deploy

Push to `main`. The GitHub Actions workflow (`.github/workflows/deploy.yml`) will automatically inject the KV namespace IDs and deploy the Worker to Cloudflare.

## How it works

1. User clicks a star rating on a documentation page
2. JavaScript sends a POST request to the Worker with `{ rating, page }`
3. Worker validates the request, checks rate limiting via KV
4. Rating is saved to Cloudflare KV (fast, no race conditions)
5. An asynchronous backup is committed to `docs/pages/feedbacks.json` via GitHub API
6. The `GET /stats?page=` endpoint returns the average rating and total count for a page

## Data structure

Each page has its own KV key (`ratings:<page>`):

```json
[
  {
    "rating": 5,
    "timestamp": "2026-03-26T00:00:00.000Z",
    "page": "/Schema-Validator/installation"
  }
]
```

## Limits

- **Cloudflare Workers**: 100,000 requests/day (free tier)
- **Cloudflare KV**: 100,000 reads/day, 1,000 writes/day (free tier)
- **GitHub API**: 5,000 requests/hour per PAT (backup only, non-blocking)
- **Rate limiting**: 1 rating per IP per page per hour (enforced via KV TTL)

## Troubleshooting

If ratings are not working:

1. Check that `GITHUB_TOKEN` has the `repo` scope
2. Check that `REPO_INFO` is in the `owner/repo` format
3. Check that both KV namespaces are correctly bound in `wrangler.toml`
4. Check the Worker logs at Cloudflare Dashboard → Workers & Pages → feedback-handler → Logs
5. Check the browser console for errors