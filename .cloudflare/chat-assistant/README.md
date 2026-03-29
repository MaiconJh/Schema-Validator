# Schema-Validator AI Chat Worker

Worker dedicado ao chat da documentacao, separado do worker de feedback.

## Objetivos

- expor um endpoint isolado para o AI Assistant;
- aplicar protecao server-side contra abuso por sessao e IP;
- limitar o escopo a documentacao do Schema-Validator;
- suportar cooldown, bloqueio temporario e challenge;
- manter CORS restrito e sessao segura por token assinado, com cookie como fallback.

## Arquivos

- `worker.js`: runtime do Worker e Durable Object de controle de abuso.
- `wrangler.toml`: configuracao do Worker, variaveis padrao e migrations do Durable Object.

## Endpoints

- `GET /v1/chat/health`
- `POST /v1/chat`
- `OPTIONS /v1/chat`

## Segredos recomendados

Configure pelo menos:

- `SESSION_HMAC_SECRET`
- `ABUSE_KEY_SALT`
- `UPSTREAM_API_KEY` se o provedor exigir autenticacao
- `TURNSTILE_SECRET_KEY` se for habilitar challenge

Exemplos:

```powershell
npx wrangler secret put SESSION_HMAC_SECRET --config .cloudflare/chat-assistant/wrangler.toml
npx wrangler secret put ABUSE_KEY_SALT --config .cloudflare/chat-assistant/wrangler.toml
npx wrangler secret put TURNSTILE_SECRET_KEY --config .cloudflare/chat-assistant/wrangler.toml
npx wrangler secret put UPSTREAM_API_KEY --config .cloudflare/chat-assistant/wrangler.toml
```

Para `staging` e `production`, adicione `--env staging` ou `--env production`.

## Deploy

Dry-run:

```powershell
npx wrangler deploy --config .cloudflare/chat-assistant/wrangler.toml --env staging --dry-run
```

Staging:

```powershell
npx wrangler deploy --config .cloudflare/chat-assistant/wrangler.toml --env staging
```

Producao:

```powershell
npx wrangler deploy --config .cloudflare/chat-assistant/wrangler.toml --env production
```

## Integracao com a docs

Depois do deploy, configure em `docs/pages/_config.yml`:

- `ai_assistant_worker_url`
- `ai_assistant_turnstile_site_key` se Turnstile estiver habilitado

## Operacao

- `npx wrangler tail --config .cloudflare/chat-assistant/wrangler.toml --env production`
- o Worker devolve `X-Request-Id` e `Retry-After`
- o Worker devolve `X-Chat-Session` para o frontend persistir a sessao assinada mesmo fora de cookie
- o frontend usa `credentials: include`, mas o controle de abuso nao depende exclusivamente de cookie

## Ajustes de capacidade

Os limites padrao sao conservadores. Ajuste conforme a carga:

- `BURST_LIMIT`
- `SUSTAINED_LIMIT`
- `DAILY_LIMIT`
- `MAX_CONCURRENCY`
- `SOFT_COOLDOWN_SEC`
- `HARD_COOLDOWN_SEC`
- `BAN_DURATION_SEC`

Para cargas mais altas, mantenha:

- `DOCS_SEARCH_URL` em origem estatica estavel;
- `SEARCH_CACHE_TTL` maior;
- amostragem de logs com `LOG_SAMPLE_RATE`;
- challenge apenas para trafego suspeito.
