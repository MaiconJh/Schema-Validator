const DEFAULTS = Object.freeze({
  allowedOrigins: [
    'https://maiconjh.github.io',
    'http://localhost:4000',
    'http://localhost:3000'
  ],
  docsBaseUrl: 'https://maiconjh.github.io/Schema-Validator/',
  docsSearchUrl: 'https://maiconjh.github.io/Schema-Validator/search.json',
  upstreamApiUrl: 'https://api.kilo.ai/api/gateway/chat/completions',
  upstreamModel: 'kilo-auto/free',
  upstreamTimeoutMs: 25000,
  maxInputChars: 6000,
  maxHistoryMessages: 8,
  maxContextSections: 4,
  maxDocMatches: 5,
  maxOutputTokens: 1800,
  temperature: 0.3,
  searchCacheTtl: 300,
  cookieName: 'sv_chat_session',
  cookieMaxAge: 60 * 60 * 24 * 30,
  burstLimit: 5,
  burstWindowSec: 60,
  sustainedLimit: 20,
  sustainedWindowSec: 60 * 15,
  dailyLimit: 80,
  dailyWindowSec: 60 * 60 * 24,
  maxConcurrency: 2,
  softCooldownSec: 60,
  hardCooldownSec: 60 * 15,
  banDurationSec: 60 * 60 * 24,
  violationWindowSec: 60 * 60,
  violationLimit: 5,
  offTopicWindowSec: 60 * 30,
  offTopicLimit: 5,
  challengeAfterViolations: 2,
  challengeAfterOffTopic: 3,
  turnstileEnforced: false,
  requireChallengeForSuspicious: true,
  logSampleRate: 1
});

const STOP_WORDS = new Set([
  'a', 'about', 'after', 'again', 'all', 'also', 'an', 'and', 'any', 'are', 'as', 'at',
  'be', 'been', 'but', 'by', 'can', 'com', 'como', 'da', 'das', 'de', 'do', 'dos', 'em',
  'for', 'from', 'get', 'has', 'help', 'how', 'isso', 'isto', 'its', 'let', 'me', 'more',
  'na', 'nas', 'need', 'nos', 'not', 'num', 'numa', 'o', 'of', 'on', 'or', 'os', 'para',
  'por', 'que', 'se', 'sem', 'ser', 'sobre', 'some', 'the', 'this', 'to', 'uma', 'um',
  'with', 'you'
]);

const DOMAIN_HINTS = [
  'schema-validator',
  'schema validator',
  'json schema',
  'schema keyword',
  'validation',
  'validator',
  'skript',
  'draft 2020-12',
  'draft-2020-12',
  'config',
  'documentation',
  'docs'
];

const RESPONSE_HEADERS = {
  'Cache-Control': 'no-store',
  'Content-Type': 'application/json; charset=utf-8',
  'Referrer-Policy': 'same-origin',
  'X-Content-Type-Options': 'nosniff'
};

const SESSION_HEADER_NAME = 'X-Chat-Session';

let inMemorySearchCache = {
  url: '',
  expiresAt: 0,
  data: null
};

export default {
  async fetch(request, env, ctx) {
    const config = getConfig(env);
    const requestId = crypto.randomUUID();
    const startedAt = Date.now();
    const url = new URL(request.url);

    if (url.pathname === '/v1/chat/health' && request.method === 'GET') {
      return json({
        ok: true,
        service: 'schema-validator-ai-chat',
        version: 'v1',
        requestId,
        time: new Date().toISOString()
      }, 200, buildResponseHeaders({ requestId }));
    }

    if (url.pathname !== '/v1/chat') {
      return json({
        error: {
          code: 'not_found',
          message: 'Not found'
        },
        requestId
      }, 404, buildResponseHeaders({ requestId }));
    }

    const origin = request.headers.get('Origin') || '';
    const corsDecision = getCorsDecision(origin, config.allowedOrigins);
    if (request.method === 'OPTIONS') {
      return new Response(null, {
        status: corsDecision.allowed ? 204 : 403,
        headers: buildCorsHeaders(corsDecision, requestId)
      });
    }

    if (!corsDecision.allowed) {
      return json({
        error: {
          code: 'origin_not_allowed',
          message: 'Origin is not allowed for this worker.'
        },
        requestId
      }, 403, buildCorsHeaders(corsDecision, requestId));
    }

    if (request.method !== 'POST') {
      return json({
        error: {
          code: 'method_not_allowed',
          message: 'Use POST /v1/chat.'
        },
        requestId
      }, 405, buildCorsHeaders(corsDecision, requestId));
    }

    const sessionInfo = await ensureSessionIdentity(request, config);
    const clientIp = request.headers.get('CF-Connecting-IP') || request.headers.get('X-Forwarded-For') || 'unknown';
    const country = request.cf && request.cf.country ? request.cf.country : 'unknown';
    const ipKey = `ip:${await stableHash(`${clientIp}|${config.abuseKeySalt}`)}`;
    const sessionKey = `session:${sessionInfo.sessionId}`;

    let requestBody;
    try {
      requestBody = await request.json();
    } catch (error) {
      return json({
        error: {
          code: 'invalid_json',
          message: 'Request body must be valid JSON.'
        },
        requestId
      }, 400, buildCorsHeaders(corsDecision, requestId, sessionInfo, undefined));
    }

    const normalizedPayload = normalizeChatRequest(requestBody, config);
    if (!normalizedPayload.ok) {
      return json({
        error: normalizedPayload.error,
        requestId
      }, normalizedPayload.status, buildCorsHeaders(corsDecision, requestId, sessionInfo, undefined));
    }

    const payload = normalizedPayload.value;
    const guardContext = {
      requestId,
      now: Date.now(),
      questionLength: payload.question.length,
      hasChallengeToken: Boolean(payload.challengeToken)
    };

    const [sessionReserve, ipReserve] = await Promise.all([
      reserveGuard(env, sessionKey, guardContext, config),
      reserveGuard(env, ipKey, guardContext, config)
    ]);

    const guardDecision = mergeGuardDecisions(sessionReserve, ipReserve);
    if (!guardDecision.allowed) {
      ctx.waitUntil(logEvent(config, 'chat_blocked', {
        requestId,
        origin,
        sessionKey,
        ipKey,
        action: guardDecision.action,
        retryAfter: guardDecision.retryAfter,
        country
      }));

      return json({
        error: {
          code: guardDecision.code,
          message: localizedRateLimitMessage(payload.locale, guardDecision.action),
          retryAfter: guardDecision.retryAfter,
          challengeRequired: guardDecision.challengeRequired
        },
        requestId
      }, guardDecision.status, buildCorsHeaders(corsDecision, requestId, sessionInfo, guardDecision.retryAfter));
    }

    let completionRecorded = false;
    try {
      if (guardDecision.challengeRequired && config.turnstileEnforced) {
        const validation = await validateTurnstileIfNeeded(payload.challengeToken, clientIp, config);
        if (!validation.success) {
          await Promise.all([
            completeGuard(env, sessionKey, requestId, 'challenge_failed', config),
            completeGuard(env, ipKey, requestId, 'challenge_failed', config)
          ]);
          completionRecorded = true;

          ctx.waitUntil(logEvent(config, 'chat_challenge_failed', {
            requestId,
            origin,
            sessionKey,
            ipKey,
            errors: validation.errors,
            country
          }));

          return json({
            error: {
              code: 'challenge_required',
              message: localizedChallengeMessage(payload.locale),
              challengeRequired: true
            },
            requestId
          }, 403, buildCorsHeaders(corsDecision, requestId, sessionInfo, undefined));
        }
      }

      const docsIndex = await loadDocsIndex(config);
      const retrieval = retrieveRelevantDocs(docsIndex, payload, config);
      if (isOutOfScopeQuestion(payload, retrieval)) {
        await Promise.all([
          completeGuard(env, sessionKey, requestId, 'offtopic', config),
          completeGuard(env, ipKey, requestId, 'offtopic', config)
        ]);
        completionRecorded = true;

        ctx.waitUntil(logEvent(config, 'chat_offtopic_refused', {
          requestId,
          origin,
          sessionKey,
          ipKey,
          country,
          question: excerptText(payload.question, 140)
        }));

        return json({
          reply: buildOutOfScopeReply(payload.locale),
          requestId,
          references: []
        }, 200, buildCorsHeaders(corsDecision, requestId, sessionInfo, undefined));
      }

      const upstreamPayload = buildUpstreamPayload(payload, retrieval, config);
      const upstreamResult = await callUpstream(upstreamPayload, config);
      const reply = ensureReferencesInReply(upstreamResult.reply, retrieval.matches);

      await Promise.all([
        completeGuard(env, sessionKey, requestId, 'success', config),
        completeGuard(env, ipKey, requestId, 'success', config)
      ]);
      completionRecorded = true;

      ctx.waitUntil(logEvent(config, 'chat_success', {
        requestId,
        origin,
        sessionKey,
        ipKey,
        country,
        durationMs: Date.now() - startedAt,
        docsMatches: retrieval.matches.length,
        upstreamStatus: upstreamResult.status
      }));

      return json({
        reply,
        requestId,
        references: retrieval.matches.map((match) => ({
          title: match.title,
          url: match.url,
          excerpt: match.excerpt
        }))
      }, 200, buildCorsHeaders(corsDecision, requestId, sessionInfo, undefined));
    } catch (error) {
      if (!completionRecorded) {
        await Promise.all([
          completeGuard(env, sessionKey, requestId, 'error', config),
          completeGuard(env, ipKey, requestId, 'error', config)
        ]);
      }

      ctx.waitUntil(logEvent(config, 'chat_error', {
        requestId,
        origin,
        sessionKey,
        ipKey,
        country,
        durationMs: Date.now() - startedAt,
        error: cleanText(error.message || 'Unknown error')
      }));

      return json({
        error: {
          code: 'upstream_unavailable',
          message: localizedUpstreamError(payload.locale)
        },
        requestId
      }, 502, buildCorsHeaders(corsDecision, requestId, sessionInfo, undefined));
    }
  }
};

export class ChatAbuseCoordinator {
  constructor(state) {
    this.state = state;
    this.snapshot = {
      active: {},
      shortHits: [],
      mediumHits: [],
      dailyHits: [],
      violationHits: [],
      offTopicHits: [],
      challengeFailures: [],
      cooldownUntil: 0,
      banUntil: 0,
      challengeUntil: 0
    };
    this.loaded = false;
  }

  async fetch(request) {
    if (!this.loaded) {
      await this.load();
    }

    const url = new URL(request.url);
    const body = request.method === 'POST' ? await request.json() : {};
    const config = body.config || {};
    const now = Number(body.now || Date.now());
    this.prune(now, config);

    if (url.pathname === '/reserve') {
      const result = this.reserve(body, now, config);
      await this.persist();
      return Response.json(result);
    }

    if (url.pathname === '/complete') {
      const result = this.complete(body, now, config);
      await this.persist();
      return Response.json(result);
    }

    if (url.pathname === '/peek') {
      return Response.json(this.peek(now));
    }

    return Response.json({ error: 'not_found' }, { status: 404 });
  }

  async load() {
    this.snapshot = (await this.state.storage.get('snapshot')) || this.snapshot;
    this.loaded = true;
  }

  async persist() {
    await this.state.storage.put('snapshot', this.snapshot);
  }

  prune(now, config) {
    this.snapshot.shortHits = this.snapshot.shortHits.filter((value) => value > now - config.burstWindowSec * 1000);
    this.snapshot.mediumHits = this.snapshot.mediumHits.filter((value) => value > now - config.sustainedWindowSec * 1000);
    this.snapshot.dailyHits = this.snapshot.dailyHits.filter((value) => value > now - config.dailyWindowSec * 1000);
    this.snapshot.violationHits = this.snapshot.violationHits.filter((value) => value > now - config.violationWindowSec * 1000);
    this.snapshot.offTopicHits = this.snapshot.offTopicHits.filter((value) => value > now - config.offTopicWindowSec * 1000);
    this.snapshot.challengeFailures = this.snapshot.challengeFailures.filter((value) => value > now - config.violationWindowSec * 1000);

    Object.keys(this.snapshot.active).forEach((key) => {
      if (this.snapshot.active[key] < now - 3 * 60 * 1000) {
        delete this.snapshot.active[key];
      }
    });
  }

  reserve(body, now, config) {
    if (now < this.snapshot.banUntil) {
      return buildGuardResult('banned', Math.ceil((this.snapshot.banUntil - now) / 1000));
    }

    if (now < this.snapshot.cooldownUntil) {
      return buildGuardResult('cooldown', Math.ceil((this.snapshot.cooldownUntil - now) / 1000));
    }

    if (Object.keys(this.snapshot.active).length >= config.maxConcurrency) {
      this.snapshot.violationHits.push(now);
      this.snapshot.cooldownUntil = Math.max(this.snapshot.cooldownUntil, now + config.softCooldownSec * 1000);
      return buildGuardResult('cooldown', config.softCooldownSec);
    }

    if (this.snapshot.dailyHits.length >= config.dailyLimit) {
      this.snapshot.banUntil = Math.max(this.snapshot.banUntil, now + config.banDurationSec * 1000);
      return buildGuardResult('banned', config.banDurationSec);
    }

    if (this.snapshot.shortHits.length >= config.burstLimit) {
      this.snapshot.violationHits.push(now);
      const hits = this.snapshot.violationHits.length;
      if (hits >= config.violationLimit) {
        this.snapshot.banUntil = Math.max(this.snapshot.banUntil, now + config.banDurationSec * 1000);
        return buildGuardResult('banned', config.banDurationSec);
      }

      this.snapshot.cooldownUntil = Math.max(this.snapshot.cooldownUntil, now + config.softCooldownSec * 1000);
      if (config.requireChallengeForSuspicious && config.turnstileEnforced && hits >= config.challengeAfterViolations) {
        this.snapshot.challengeUntil = Math.max(this.snapshot.challengeUntil, now + config.hardCooldownSec * 1000);
        return buildGuardResult('challenge', config.softCooldownSec);
      }

      return buildGuardResult('cooldown', config.softCooldownSec);
    }

    if (this.snapshot.mediumHits.length >= config.sustainedLimit) {
      this.snapshot.violationHits.push(now);
      this.snapshot.cooldownUntil = Math.max(this.snapshot.cooldownUntil, now + config.hardCooldownSec * 1000);
      if (this.snapshot.violationHits.length >= config.violationLimit) {
        this.snapshot.banUntil = Math.max(this.snapshot.banUntil, now + config.banDurationSec * 1000);
        return buildGuardResult('banned', config.banDurationSec);
      }

      return buildGuardResult('cooldown', config.hardCooldownSec);
    }

    this.snapshot.shortHits.push(now);
    this.snapshot.mediumHits.push(now);
    this.snapshot.dailyHits.push(now);
    this.snapshot.active[body.requestId] = now;

    if (
      config.requireChallengeForSuspicious &&
      config.turnstileEnforced &&
      (now < this.snapshot.challengeUntil ||
        this.snapshot.violationHits.length >= config.challengeAfterViolations ||
        this.snapshot.offTopicHits.length >= config.challengeAfterOffTopic)
    ) {
      return {
        allowed: true,
        action: 'allow',
        retryAfter: 0,
        status: 200,
        code: 'ok',
        challengeRequired: true
      };
    }

    return {
      allowed: true,
      action: 'allow',
      retryAfter: 0,
      status: 200,
      code: 'ok',
      challengeRequired: false
    };
  }

  complete(body, now, config) {
    delete this.snapshot.active[body.requestId];

    if (body.outcome === 'offtopic') {
      this.snapshot.offTopicHits.push(now);
      if (this.snapshot.offTopicHits.length >= config.offTopicLimit) {
        this.snapshot.cooldownUntil = Math.max(this.snapshot.cooldownUntil, now + config.softCooldownSec * 1000);
      }
      if (config.requireChallengeForSuspicious && config.turnstileEnforced && this.snapshot.offTopicHits.length >= config.challengeAfterOffTopic) {
        this.snapshot.challengeUntil = Math.max(this.snapshot.challengeUntil, now + config.hardCooldownSec * 1000);
      }
    }

    if (body.outcome === 'challenge_failed') {
      this.snapshot.challengeFailures.push(now);
      this.snapshot.violationHits.push(now);
      this.snapshot.cooldownUntil = Math.max(this.snapshot.cooldownUntil, now + config.softCooldownSec * 1000);
      if (this.snapshot.violationHits.length >= config.violationLimit) {
        this.snapshot.banUntil = Math.max(this.snapshot.banUntil, now + config.banDurationSec * 1000);
      }
    }

    if (body.outcome === 'error') {
      this.snapshot.cooldownUntil = Math.max(this.snapshot.cooldownUntil, now + 5 * 1000);
    }

    return this.peek(now);
  }

  peek(now) {
    return {
      activeCount: Object.keys(this.snapshot.active).length,
      cooldownUntil: this.snapshot.cooldownUntil,
      banUntil: this.snapshot.banUntil,
      challengeUntil: this.snapshot.challengeUntil,
      now
    };
  }
}

function getConfig(env) {
  return {
    allowedOrigins: splitCsv(env.ALLOWED_ORIGINS, DEFAULTS.allowedOrigins),
    docsBaseUrl: env.DOCS_BASE_URL || DEFAULTS.docsBaseUrl,
    docsSearchUrl: env.DOCS_SEARCH_URL || DEFAULTS.docsSearchUrl,
    upstreamApiUrl: env.UPSTREAM_API_URL || DEFAULTS.upstreamApiUrl,
    upstreamModel: env.UPSTREAM_MODEL || DEFAULTS.upstreamModel,
    upstreamTimeoutMs: intFromEnv(env.UPSTREAM_TIMEOUT_MS, DEFAULTS.upstreamTimeoutMs),
    maxInputChars: intFromEnv(env.MAX_INPUT_CHARS, DEFAULTS.maxInputChars),
    maxHistoryMessages: intFromEnv(env.MAX_HISTORY_MESSAGES, DEFAULTS.maxHistoryMessages),
    maxContextSections: intFromEnv(env.MAX_CONTEXT_SECTIONS, DEFAULTS.maxContextSections),
    maxDocMatches: intFromEnv(env.MAX_DOC_MATCHES, DEFAULTS.maxDocMatches),
    maxOutputTokens: intFromEnv(env.MAX_OUTPUT_TOKENS, DEFAULTS.maxOutputTokens),
    temperature: floatFromEnv(env.TEMPERATURE, DEFAULTS.temperature),
    searchCacheTtl: intFromEnv(env.SEARCH_CACHE_TTL, DEFAULTS.searchCacheTtl),
    cookieName: env.COOKIE_NAME || DEFAULTS.cookieName,
    cookieMaxAge: intFromEnv(env.COOKIE_MAX_AGE, DEFAULTS.cookieMaxAge),
    burstLimit: intFromEnv(env.BURST_LIMIT, DEFAULTS.burstLimit),
    burstWindowSec: intFromEnv(env.BURST_WINDOW_SEC, DEFAULTS.burstWindowSec),
    sustainedLimit: intFromEnv(env.SUSTAINED_LIMIT, DEFAULTS.sustainedLimit),
    sustainedWindowSec: intFromEnv(env.SUSTAINED_WINDOW_SEC, DEFAULTS.sustainedWindowSec),
    dailyLimit: intFromEnv(env.DAILY_LIMIT, DEFAULTS.dailyLimit),
    dailyWindowSec: intFromEnv(env.DAILY_WINDOW_SEC, DEFAULTS.dailyWindowSec),
    maxConcurrency: intFromEnv(env.MAX_CONCURRENCY, DEFAULTS.maxConcurrency),
    softCooldownSec: intFromEnv(env.SOFT_COOLDOWN_SEC, DEFAULTS.softCooldownSec),
    hardCooldownSec: intFromEnv(env.HARD_COOLDOWN_SEC, DEFAULTS.hardCooldownSec),
    banDurationSec: intFromEnv(env.BAN_DURATION_SEC, DEFAULTS.banDurationSec),
    violationWindowSec: intFromEnv(env.VIOLATION_WINDOW_SEC, DEFAULTS.violationWindowSec),
    violationLimit: intFromEnv(env.VIOLATION_LIMIT, DEFAULTS.violationLimit),
    offTopicWindowSec: intFromEnv(env.OFFTOPIC_WINDOW_SEC, DEFAULTS.offTopicWindowSec),
    offTopicLimit: intFromEnv(env.OFFTOPIC_LIMIT, DEFAULTS.offTopicLimit),
    challengeAfterViolations: intFromEnv(env.CHALLENGE_AFTER_VIOLATIONS, DEFAULTS.challengeAfterViolations),
    challengeAfterOffTopic: intFromEnv(env.CHALLENGE_AFTER_OFFTOPIC, DEFAULTS.challengeAfterOffTopic),
    turnstileEnforced: boolFromEnv(env.TURNSTILE_ENFORCED, DEFAULTS.turnstileEnforced),
    requireChallengeForSuspicious: boolFromEnv(env.REQUIRE_CHALLENGE_FOR_SUSPICIOUS, DEFAULTS.requireChallengeForSuspicious),
    logSampleRate: floatFromEnv(env.LOG_SAMPLE_RATE, DEFAULTS.logSampleRate),
    sessionSecret: env.SESSION_HMAC_SECRET || '',
    abuseKeySalt: env.ABUSE_KEY_SALT || env.SESSION_HMAC_SECRET || 'schema-validator-abuse-key',
    turnstileSecretKey: env.TURNSTILE_SECRET_KEY || '',
    upstreamApiKey: env.UPSTREAM_API_KEY || '',
    upstreamAuthScheme: env.UPSTREAM_AUTH_SCHEME || 'Bearer'
  };
}

async function ensureSessionIdentity(request, config) {
  const existing = parseCookies(request.headers.get('Cookie') || '');
  const rawCookie = existing[config.cookieName];
  const rawHeader = request.headers.get(SESSION_HEADER_NAME) || '';
  const verifiedHeader = rawHeader ? await verifySignedCookie(rawHeader, config.sessionSecret) : null;
  const verifiedCookie = rawCookie ? await verifySignedCookie(rawCookie, config.sessionSecret) : null;
  const existingSessionId = verifiedHeader || verifiedCookie;

  if (existingSessionId) {
    return {
      sessionId: existingSessionId,
      sessionToken: rawHeader && verifiedHeader ? rawHeader : await signCookieValue(existingSessionId, config.sessionSecret),
      setCookie: ''
    };
  }

  const sessionId = crypto.randomUUID();
  const signedValue = await signCookieValue(sessionId, config.sessionSecret);
  const setCookie = [
    `${config.cookieName}=${signedValue}`,
    'Path=/',
    `Max-Age=${config.cookieMaxAge}`,
    'HttpOnly',
    'Secure',
    'SameSite=None'
  ].join('; ');

  return {
    sessionId,
    sessionToken: signedValue,
    setCookie
  };
}

function normalizeChatRequest(body, config) {
  if (!body || typeof body !== 'object') {
    return {
      ok: false,
      status: 400,
      error: {
        code: 'invalid_payload',
        message: 'Request body is required.'
      }
    };
  }

  const question = cleanText(String(body.question || ''));
  if (!question) {
    return {
      ok: false,
      status: 400,
      error: {
        code: 'missing_question',
        message: 'Question is required.'
      }
    };
  }

  if (question.length > config.maxInputChars) {
    return {
      ok: false,
      status: 413,
      error: {
        code: 'question_too_large',
        message: `Question exceeds the ${config.maxInputChars}-character limit.`
      }
    };
  }

  return {
    ok: true,
    value: {
      question,
      locale: normalizeLocale(body.locale),
      challengeToken: cleanText(String(body.challengeToken || '')),
      history: sanitizeHistory(body.history, config.maxHistoryMessages),
      context: sanitizeContext(body.context, config.maxContextSections)
    }
  };
}

function sanitizeHistory(history, limit) {
  if (!Array.isArray(history)) {
    return [];
  }

  return history
    .filter((entry) => entry && (entry.role === 'user' || entry.role === 'assistant'))
    .slice(-limit)
    .map((entry) => ({
      role: entry.role,
      content: excerptText(cleanText(entry.content), 1600)
    }))
    .filter((entry) => entry.content);
}

function sanitizeContext(context, limit) {
  const safeContext = context && typeof context === 'object' ? context : {};
  return {
    pageUrl: normalizeUrl(safeContext.pageUrl || ''),
    pageTitle: cleanText(safeContext.pageTitle),
    pageDescription: excerptText(cleanText(safeContext.pageDescription), 240),
    pageExcerpt: excerptText(cleanText(safeContext.pageExcerpt), 360),
    breadcrumbs: Array.isArray(safeContext.breadcrumbs)
      ? safeContext.breadcrumbs.map((item) => cleanText(item)).filter(Boolean).slice(0, 8)
      : [],
    visibleSections: sanitizeSections(safeContext.visibleSections, limit),
    mentionedSections: sanitizeSections(safeContext.mentionedSections, limit)
  };
}

function sanitizeSections(sections, limit) {
  if (!Array.isArray(sections)) {
    return [];
  }

  return sections
    .map((section) => ({
      id: cleanText(section && section.id),
      title: cleanText(section && section.title),
      excerpt: excerptText(cleanText(section && section.excerpt), 220)
    }))
    .filter((section) => section.title)
    .slice(0, limit);
}

function retrieveRelevantDocs(docsIndex, payload, config) {
  const queryTokens = tokenizeText([
    payload.question,
    payload.context.pageTitle,
    payload.context.visibleSections.map((section) => section.title).join(' '),
    payload.context.mentionedSections.map((section) => section.title).join(' ')
  ].join(' '));

  const pageUrl = payload.context.pageUrl;
  const sectionHints = dedupeById(
    payload.context.visibleSections.concat(payload.context.mentionedSections),
    (entry) => entry.id || entry.title
  );

  const matches = docsIndex
    .map((entry) => scoreDocument(entry, queryTokens, pageUrl))
    .filter((entry) => entry.score > 0)
    .sort((left, right) => right.score - left.score)
    .slice(0, config.maxDocMatches);

  return {
    matches,
    sectionHints
  };
}

function scoreDocument(entry, queryTokens, currentPageUrl) {
  let score = 0;
  const title = normalizeText(entry.title);
  const description = normalizeText(entry.description);
  const content = normalizeText(entry.content);

  for (const token of new Set(queryTokens)) {
    if (title.includes(token)) {
      score += 6;
    }
    if (description.includes(token)) {
      score += 3;
    }
    if (content.includes(token)) {
      score += 1;
    }
  }

  if (currentPageUrl && normalizeUrl(entry.url) === currentPageUrl) {
    score += 2;
  }

  return {
    ...entry,
    score,
    excerpt: buildExcerpt(entry.content || entry.description, queryTokens)
  };
}

function isOutOfScopeQuestion(payload, retrieval) {
  const normalizedQuestion = normalizeText(payload.question);
  const questionTokens = tokenizeText(payload.question);
  if (!questionTokens.length || questionTokens.length <= 2) {
    return false;
  }

  if (DOMAIN_HINTS.some((hint) => normalizedQuestion.includes(normalizeText(hint)))) {
    return false;
  }

  if (payload.context.pageTitle || payload.context.visibleSections.length || payload.context.mentionedSections.length) {
    const localContext = normalizeText([
      payload.context.pageTitle,
      payload.context.pageDescription,
      payload.context.visibleSections.map((section) => section.title).join(' '),
      payload.context.mentionedSections.map((section) => section.title).join(' ')
    ].join(' '));
    if (questionTokens.some((token) => localContext.includes(token))) {
      return false;
    }
  }

  return !(retrieval.matches.length && retrieval.matches[0].score >= 4);
}

function buildUpstreamPayload(payload, retrieval, config) {
  const docsLines = retrieval.matches.length
    ? retrieval.matches
      .map((match, index) => `${index + 1}. ${match.title} (${match.url})\nDescription: ${match.description || 'No description'}\nExcerpt: ${match.excerpt || 'No excerpt'}`)
      .join('\n')
    : 'None';

  const sectionLines = retrieval.sectionHints.length
    ? retrieval.sectionHints.map((section) => `- ${section.title}: ${section.excerpt || 'No excerpt available.'}`).join('\n')
    : '- None';

  const systemPrompt = [
    'You are the Schema-Validator documentation assistant.',
    'Answer only using Schema-Validator documentation context provided by the server.',
    'Refuse unrelated questions and do not improvise beyond the grounded context.',
    'Be concise, technical, and respond in the same language as the user whenever possible.',
    'End the answer with a "References" section naming the documentation pages or sections used.'
  ].join(' ');

  const contextPrompt = [
    '[Grounded documentation context]',
    `Current page: ${payload.context.pageTitle || 'Unknown'}`,
    `Current URL: ${payload.context.pageUrl || 'Unknown'}`,
    `Breadcrumbs: ${payload.context.breadcrumbs.join(' > ') || 'Docs'}`,
    `Current page excerpt: ${payload.context.pageExcerpt || 'Unavailable'}`,
    `Current sections:\n${sectionLines}`,
    `Relevant documentation matches:\n${docsLines}`,
    '[Restrictions]',
    '- Stay inside Schema-Validator documentation scope.',
    '- If the docs do not support the answer, say so clearly.',
    '- Prefer terminology and examples from the retrieved snippets.'
  ].join('\n');

  const messages = [
    { role: 'system', content: systemPrompt },
    { role: 'system', content: contextPrompt }
  ];

  payload.history.forEach((entry) => {
    messages.push(entry);
  });

  messages.push({
    role: 'user',
    content: payload.question
  });

  return {
    model: config.upstreamModel,
    messages,
    temperature: config.temperature,
    max_tokens: config.maxOutputTokens
  };
}

async function callUpstream(payload, config) {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort('timeout'), config.upstreamTimeoutMs);
  try {
    const headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    };

    if (config.upstreamApiKey) {
      headers.Authorization = `${config.upstreamAuthScheme} ${config.upstreamApiKey}`;
    }

    const response = await fetch(config.upstreamApiUrl, {
      method: 'POST',
      headers,
      body: JSON.stringify(payload),
      signal: controller.signal
    });

    const data = await response.json().catch(() => ({}));
    const reply =
      data && data.choices && data.choices[0] && data.choices[0].message && data.choices[0].message.content
        ? data.choices[0].message.content
        : '';

    if (!response.ok || !reply) {
      throw new Error(`Upstream error ${response.status}`);
    }

    return {
      status: response.status,
      reply
    };
  } finally {
    clearTimeout(timeoutId);
  }
}

async function loadDocsIndex(config) {
  const cacheUrl = config.docsSearchUrl || new URL('/search.json', config.docsBaseUrl).toString();
  if (inMemorySearchCache.url === cacheUrl && inMemorySearchCache.expiresAt > Date.now() && Array.isArray(inMemorySearchCache.data)) {
    return inMemorySearchCache.data;
  }

  const cacheKey = new Request(cacheUrl, { method: 'GET' });
  const cached = await caches.default.match(cacheKey);
  if (cached) {
    const data = await cached.json();
    inMemorySearchCache = {
      url: cacheUrl,
      expiresAt: Date.now() + config.searchCacheTtl * 1000,
      data
    };
    return data;
  }

  const response = await fetch(cacheUrl, {
    cf: {
      cacheTtl: config.searchCacheTtl,
      cacheEverything: true
    }
  });

  if (!response.ok) {
    throw new Error('search_index_unavailable');
  }

  const rawText = await response.text();
  const parsed = JSON.parse(rawText);
  const data = parsed.map((item) => ({
    title: cleanText(item.title),
    url: normalizeUrl(item.url),
    description: cleanText(item.description),
    content: cleanText(item.content)
  }));

  const cacheResponse = new Response(JSON.stringify(data), {
    headers: {
      'Content-Type': 'application/json',
      'Cache-Control': `public, max-age=${config.searchCacheTtl}`
    }
  });

  await caches.default.put(cacheKey, cacheResponse.clone());
  inMemorySearchCache = {
    url: cacheUrl,
    expiresAt: Date.now() + config.searchCacheTtl * 1000,
    data
  };

  return data;
}

async function reserveGuard(env, key, context, config) {
  const id = env.CHAT_GUARD.idFromName(key);
  const stub = env.CHAT_GUARD.get(id);
  const response = await stub.fetch('https://guard/reserve', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      requestId: context.requestId,
      now: context.now,
      questionLength: context.questionLength,
      hasChallengeToken: context.hasChallengeToken,
      config: extractGuardConfig(config)
    })
  });
  return response.json();
}

async function completeGuard(env, key, requestId, outcome, config) {
  const id = env.CHAT_GUARD.idFromName(key);
  const stub = env.CHAT_GUARD.get(id);
  const response = await stub.fetch('https://guard/complete', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      requestId,
      outcome,
      now: Date.now(),
      config: extractGuardConfig(config)
    })
  });
  return response.json();
}

function extractGuardConfig(config) {
  return {
    burstLimit: config.burstLimit,
    burstWindowSec: config.burstWindowSec,
    sustainedLimit: config.sustainedLimit,
    sustainedWindowSec: config.sustainedWindowSec,
    dailyLimit: config.dailyLimit,
    dailyWindowSec: config.dailyWindowSec,
    maxConcurrency: config.maxConcurrency,
    softCooldownSec: config.softCooldownSec,
    hardCooldownSec: config.hardCooldownSec,
    banDurationSec: config.banDurationSec,
    violationWindowSec: config.violationWindowSec,
    violationLimit: config.violationLimit,
    offTopicWindowSec: config.offTopicWindowSec,
    offTopicLimit: config.offTopicLimit,
    challengeAfterViolations: config.challengeAfterViolations,
    challengeAfterOffTopic: config.challengeAfterOffTopic,
    requireChallengeForSuspicious: config.requireChallengeForSuspicious,
    turnstileEnforced: config.turnstileEnforced
  };
}

function mergeGuardDecisions(...decisions) {
  const blocked = decisions.find((decision) => !decision.allowed);
  if (blocked) {
    return blocked;
  }

  const challengeRequired = decisions.some((decision) => decision.challengeRequired);
  return {
    allowed: true,
    action: 'allow',
    retryAfter: 0,
    status: 200,
    code: 'ok',
    challengeRequired
  };
}

function buildGuardResult(action, retryAfter) {
  if (action === 'challenge') {
    return {
      allowed: false,
      action,
      retryAfter,
      status: 403,
      code: 'challenge_required',
      challengeRequired: true
    };
  }

  if (action === 'banned') {
    return {
      allowed: false,
      action,
      retryAfter,
      status: 429,
      code: 'temporarily_blocked',
      challengeRequired: false
    };
  }

  return {
    allowed: false,
    action: 'cooldown',
    retryAfter,
    status: 429,
    code: 'rate_limited',
    challengeRequired: false
  };
}

async function validateTurnstileIfNeeded(token, remoteip, config) {
  if (!config.turnstileEnforced) {
    return { success: true, errors: [] };
  }

  if (!config.turnstileSecretKey) {
    return { success: false, errors: ['missing-turnstile-secret'] };
  }

  if (!token) {
    return { success: false, errors: ['missing-turnstile-token'] };
  }

  const response = await fetch('https://challenges.cloudflare.com/turnstile/v0/siteverify', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      secret: config.turnstileSecretKey,
      response: token,
      remoteip,
      idempotency_key: crypto.randomUUID()
    })
  });

  const data = await response.json().catch(() => ({}));
  return {
    success: Boolean(data && data.success),
    errors: Array.isArray(data && data['error-codes']) ? data['error-codes'] : []
  };
}

function buildResponseHeaders({ requestId }) {
  return new Headers({
    ...RESPONSE_HEADERS,
    'X-Request-Id': requestId
  });
}

function buildCorsHeaders(corsDecision, requestId, sessionInfo, retryAfter) {
  const headers = buildResponseHeaders({ requestId });
  headers.set('Access-Control-Allow-Methods', 'POST, OPTIONS, GET');
  headers.set('Access-Control-Allow-Headers', `Content-Type, ${SESSION_HEADER_NAME}`);
  headers.set('Access-Control-Expose-Headers', `Retry-After, X-Request-Id, ${SESSION_HEADER_NAME}`);
  headers.set('Access-Control-Allow-Credentials', 'true');
  headers.set('Vary', 'Origin');
  if (corsDecision.origin) {
    headers.set('Access-Control-Allow-Origin', corsDecision.origin);
  }
  if (sessionInfo && sessionInfo.sessionToken) {
    headers.set(SESSION_HEADER_NAME, sessionInfo.sessionToken);
  }
  if (sessionInfo && sessionInfo.setCookie) {
    headers.append('Set-Cookie', sessionInfo.setCookie);
  }
  if (retryAfter) {
    headers.set('Retry-After', String(retryAfter));
  }
  return headers;
}

function getCorsDecision(origin, allowedOrigins) {
  if (!origin) {
    return {
      allowed: false,
      origin: ''
    };
  }

  const normalizedOrigin = origin.replace(/\/$/, '');
  const allowed = allowedOrigins.some((candidate) => candidate.replace(/\/$/, '') === normalizedOrigin);
  return {
    allowed,
    origin: allowed ? normalizedOrigin : ''
  };
}

function json(payload, status, headers) {
  return new Response(JSON.stringify(payload), {
    status,
    headers
  });
}

function splitCsv(value, fallback) {
  if (!value) {
    return [...fallback];
  }
  return String(value).split(',').map((item) => item.trim()).filter(Boolean);
}

function intFromEnv(value, fallback) {
  const parsed = Number.parseInt(String(value || ''), 10);
  return Number.isFinite(parsed) ? parsed : fallback;
}

function floatFromEnv(value, fallback) {
  const parsed = Number.parseFloat(String(value || ''));
  return Number.isFinite(parsed) ? parsed : fallback;
}

function boolFromEnv(value, fallback) {
  if (value === undefined || value === null || value === '') {
    return fallback;
  }
  return String(value).toLowerCase() === 'true';
}

function cleanText(value) {
  return String(value || '')
    .replace(/\s+/g, ' ')
    .trim();
}

function excerptText(value, maxLength) {
  const clean = cleanText(value);
  if (clean.length <= maxLength) {
    return clean;
  }
  return `${clean.slice(0, Math.max(0, maxLength - 1)).trimEnd()}...`;
}

function normalizeText(value) {
  return cleanText(value)
    .normalize('NFKD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase();
}

function tokenizeText(value) {
  return normalizeText(value)
    .split(/[^a-z0-9]+/g)
    .filter((token) => token.length > 1 && !STOP_WORDS.has(token));
}

function buildExcerpt(text, queryTokens) {
  const clean = cleanText(text);
  if (!clean) {
    return '';
  }

  const normalized = normalizeText(clean);
  const match = queryTokens.find((token) => normalized.includes(token));
  if (!match) {
    return excerptText(clean, 220);
  }

  const index = normalized.indexOf(match);
  return excerptText(clean.slice(Math.max(0, index - 90), Math.min(clean.length, index + 180)), 240);
}

function dedupeById(entries, selector) {
  const seen = new Set();
  return entries.filter((entry) => {
    const key = selector(entry);
    if (!key || seen.has(key)) {
      return false;
    }
    seen.add(key);
    return true;
  });
}

function parseCookies(header) {
  return header
    .split(';')
    .map((part) => part.trim())
    .filter(Boolean)
    .reduce((accumulator, chunk) => {
      const separatorIndex = chunk.indexOf('=');
      if (separatorIndex === -1) {
        return accumulator;
      }
      const key = chunk.slice(0, separatorIndex).trim();
      const value = chunk.slice(separatorIndex + 1).trim();
      accumulator[key] = value;
      return accumulator;
    }, {});
}

async function signCookieValue(value, secret) {
  if (!secret) {
    return value;
  }

  const signature = await signHmac(value, secret);
  return `${value}.${signature}`;
}

async function verifySignedCookie(value, secret) {
  if (!secret) {
    return cleanText(value);
  }

  const parts = String(value || '').split('.');
  if (parts.length !== 2) {
    return null;
  }

  const expected = await signHmac(parts[0], secret);
  return timingSafeEqual(parts[1], expected) ? parts[0] : null;
}

async function signHmac(value, secret) {
  const key = await crypto.subtle.importKey(
    'raw',
    new TextEncoder().encode(secret),
    { name: 'HMAC', hash: 'SHA-256' },
    false,
    ['sign']
  );
  const signature = await crypto.subtle.sign('HMAC', key, new TextEncoder().encode(value));
  return bufferToHex(signature);
}

async function stableHash(value) {
  const digest = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(value));
  return bufferToHex(digest);
}

function bufferToHex(buffer) {
  return Array.from(new Uint8Array(buffer))
    .map((value) => value.toString(16).padStart(2, '0'))
    .join('');
}

function timingSafeEqual(left, right) {
  if (left.length !== right.length) {
    return false;
  }

  let mismatch = 0;
  for (let index = 0; index < left.length; index += 1) {
    mismatch |= left.charCodeAt(index) ^ right.charCodeAt(index);
  }
  return mismatch === 0;
}

function normalizeUrl(value) {
  const clean = cleanText(value);
  if (!clean) {
    return '';
  }

  try {
    return new URL(clean, DEFAULTS.docsBaseUrl).toString();
  } catch (error) {
    return clean;
  }
}

function normalizeLocale(value) {
  const normalized = cleanText(value).toLowerCase();
  return normalized.startsWith('pt') ? 'pt' : 'en';
}

function localizedRateLimitMessage(locale, action) {
  if (locale === 'pt') {
    if (action === 'banned') {
      return 'O acesso ao chat foi bloqueado temporariamente devido a sinais de abuso.';
    }
    if (action === 'challenge') {
      return 'Precisamos de uma verificacao adicional antes de continuar no chat.';
    }
    return 'O chat esta em modo de protecao temporaria. Aguarde alguns instantes antes de tentar novamente.';
  }

  if (action === 'banned') {
    return 'Chat access was temporarily blocked due to abuse signals.';
  }
  if (action === 'challenge') {
    return 'Additional verification is required before continuing in chat.';
  }
  return 'The chat is temporarily protected. Wait a moment before trying again.';
}

function localizedChallengeMessage(locale) {
  return locale === 'pt'
    ? 'Precisamos confirmar que voce e humano antes de continuar no chat.'
    : 'We need to verify that you are human before continuing in chat.';
}

function localizedUpstreamError(locale) {
  return locale === 'pt'
    ? 'O servico do assistente nao conseguiu responder no momento. Tente novamente em instantes.'
    : 'The assistant service could not respond right now. Try again in a moment.';
}

function buildOutOfScopeReply(locale) {
  return locale === 'pt'
    ? 'Este assistente responde apenas sobre a documentacao do Schema-Validator. Pergunte sobre configuracao, keywords, arquitetura, exemplos ou mencione uma secao da pagina com `@`.\n\nReferences\n- Current page context'
    : 'This assistant answers only about Schema-Validator documentation. Ask about configuration, keywords, architecture, examples, or mention a page section with `@`.\n\nReferences\n- Current page context';
}

function ensureReferencesInReply(reply, matches) {
  const cleanReply = cleanText(reply);
  if (!matches.length) {
    return cleanReply;
  }

  if (/references/i.test(cleanReply)) {
    return reply;
  }

  const references = matches
    .slice(0, 4)
    .map((match) => `- ${match.title} (${match.url})`)
    .join('\n');

  return `${reply.trim()}\n\nReferences\n${references}`;
}

async function logEvent(config, type, payload) {
  if (config.logSampleRate <= 0) {
    return;
  }

  if (config.logSampleRate < 1 && Math.random() > config.logSampleRate) {
    return;
  }

  console.log(JSON.stringify({
    ts: new Date().toISOString(),
    service: 'schema-validator-ai-chat',
    type,
    ...payload
  }));
}
