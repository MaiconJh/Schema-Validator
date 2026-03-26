# Cloudflare Worker - Feedback Handler

Este Worker recebe feedbacks das páginas de documentação e os salva no arquivo JSON do repositório GitHub.

## Configuração

### 1. Criar o PAT (Personal Access Token) no GitHub

1. Acesse: https://github.com/settings/tokens
2. Clique em **"Generate new token (classic)"**
3. **Note**: "Feedback API"
4. **Expiration**: Escolha "No expiration"
5. **Scopes**: Marque `repo` (necessário para escrever no repositório)
6. Copie o token gerado

### 2. Deploy do Worker no Cloudflare

#### Opção A: Via Dashboard Cloudflare

1. Acesse: https://dash.cloudflare.com
2. Vá para **Workers & Pages** → **Create application** → **Worker**
3. Clique em **Deploy**
4. Vá para **Settings** → **Variables**
5. Adicione duas variáveis:
   - `GITHUB_TOKEN` = Seu token do GitHub (PAT)
   - `REPO_INFO` = `Maiconjh/Schema-Validator` (ou seu repo)
6. Salve

#### Opção B: Via Wrangler CLI

```bash
# Instalar wrangler
npm install -g wrangler

# Fazer login
wrangler login

# Deploy
wrangler deploy .cloudflare/worker.js
```

### 3. Atualizar a URL no Jekyll Config

Após o deploy, você terá uma URL como:
`https://feedback-handler.your-account.workers.dev`

Adicione esta URL em `docs/pages/_config.yml`:

```yaml
feedback_worker_url: "https://seu-worker.seu-subdomain.workers.dev"
```

## Como funciona

1. Usuário clica "Yes" ou "No" nas páginas de documentação
2. JavaScript envia POST para o Worker com os dados
3. Worker faz commit no arquivo `docs/pages/feedbacks.json` via GitHub API
4. Feedback fica versionado no GitHub

## Estrutura dos dados

```json
[
  {
    "feedback": "yes",
    "timestamp": "2026-03-26T00:00:00.000Z",
    "page": "/Schema-Validator/installation",
    "userAgent": "Mozilla/5.0...",
    "referrer": "https://..."
  }
]
```

## Limites

- **Cloudflare Workers**: 100,000 requests/day gratuito
- **GitHub API**: 5,000 requests/hour para PAT

## Rate Limiting (Opcional)

O Cloudflare tem rate limiting integrado. Para ativar:
1. Vá no dashboard do Cloudflare → seu Worker
2. Settings → Rate Limiting
3. Configure limite por IP

## Troubleshooting

Se o feedback não estiver funcionando:
1. Verifique se o token tem permissão `repo`
2. Verifique se a variável `REPO_INFO` está no formato `owner/repo`
3. Verifique os logs do Worker no Cloudflare Dashboard
4. Verifique o console do navegador para erros