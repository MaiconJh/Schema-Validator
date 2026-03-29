---
title: Privacy Policy
layout: default
permalink: /privacy-policy/
description: How feedback data and AI assistant interactions are collected, used, and stored
toc: false
nav_exclude: true
---

# Privacy Policy

Last updated: March 29, 2026

This Privacy Policy explains how the Schema Validator documentation site ("we", "our", or "the site") handles information when you use the rating feature and the AI Assistant chat.

The AI Assistant is intentionally limited to Schema-Validator documentation topics. It is designed to answer questions about the documentation, current page context, and related sections, and to refuse unrelated requests.

## Rating Feedback

When you rate a page using the star rating system, we collect the following information:

- **Rating value** (1 to 5 stars)
- **Page URL** (the specific documentation page you rated)
- **Timestamp** (date and time of your rating)

We do not collect your IP address, user agent, or referrer in the stored rating data. Your IP is used temporarily for rate limiting and abuse prevention, but it is not intentionally stored as part of the feedback record.

The feedback helps us understand which pages are most helpful and identify areas for improvement. The collected data is used solely to:

- Calculate average ratings per page
- Improve the documentation based on user feedback
- Provide transparency about how the documentation is received

Rating data is stored in two places:

- **Cloudflare KV** - the primary storage, where ratings are saved immediately after submission. This is a private key-value store managed by Cloudflare and not publicly accessible.
- **GitHub repository** - an asynchronous backup is committed periodically to a public repository:

  <https://github.com/MaiconJh/Schema-Validator/blob/main/docs/pages/feedbacks.json>

  Because the repository is public, anyone can view the backed-up feedback. Please do not include any personal, sensitive, or confidential information in your ratings. Only the rating value and page context are recorded.

Ratings are retained for a maximum of 90 days. After this period, older ratings are automatically removed. Additionally, the system limits storage to the most recent 1,000 ratings per page.

If you want a rating removed, you may open an issue or pull request on the GitHub repository. We will consider reasonable deletion requests. Data stored in Cloudflare KV can also be deleted directly on request.

## AI Assistant Chat

The AI Assistant chat helps with questions about installation, configuration, schema construction, validation workflow, keywords, and page-specific documentation topics.

Typical interactions include:

- "How do I configure the schema directory?"
- "What does this validation keyword do on the current page?"
- "Can you summarize the visible section and point me to the related references?"

Unless you explicitly consent to another arrangement, the assistant is designed to retain conversational content only for the active browser session and a short continuity window for the current tab. We do not keep a long-term first-party transcript archive for routine chat use. Separate technical identifiers and abuse-prevention records may remain temporarily beyond that session to protect the service.

### 1. Data Collection

When you use the AI Assistant, the service may collect and process:

- **Messages you send** through the chat box.
- **Recent conversation history** needed to preserve continuity for the current session.
- **Documentation context** from the page you are viewing, including page URL, page title, page description or excerpt, breadcrumbs, visible sections, and sections you explicitly mention with `@`.
- **Technical metadata** such as request ID, language or locale inference, response status, duration, number of documentation matches used, origin, and coarse country information provided by Cloudflare.
- **Session and abuse-prevention identifiers** such as a signed session token, a secure session cookie, and a salted or hashed key derived from the IP address for rate limiting and protection against misuse.
- **Verification data** such as a Turnstile challenge token if human verification is enabled.

For security and operational monitoring, the service may also generate short technical logs. These logs are intended to record events such as successful requests, blocked requests, temporary cooldowns, or upstream errors. In normal operation, successful requests log technical status data rather than full transcripts. Some refused or suspicious requests may include a short excerpt of the submitted question so abuse or misclassification can be reviewed.

### 2. Processing

The AI Assistant uses natural language processing and retrieval-based grounding. In practical terms, the flow is:

1. The submitted question is sanitized and checked for size, format, and anti-abuse rules.
2. The service looks up relevant Schema-Validator documentation using the current page context and the local documentation index.
3. A grounded prompt is assembled with the relevant documentation snippets, recent chat history, and current page metadata.
4. That grounded prompt is sent to the configured upstream AI service to generate a response.
5. The response is returned to the chat UI together with references to the documentation pages used.

The assistant is instructed to stay inside the Schema-Validator documentation scope. If the documentation does not support an answer, it should say so instead of guessing.

### 3. Storage and Security

The AI Assistant uses a mix of browser-side and server-side storage:

- **In your browser session**: the current tab may store a limited local copy of the chat state in `sessionStorage`, including whether the widget is open, draft input, rendered chat history, recent conversation history, layout state, and a short-lived session token. This data is used to preserve continuity as you move between documentation pages in the same tab.
- **Session identifier**: the chat service issues a signed session identifier that may be stored as a secure, `HttpOnly`, `SameSite=None` cookie for continuity and abuse prevention.
- **Temporary abuse-prevention state**: Cloudflare Durable Objects store rolling counters and temporary cooldown or block windows to protect the service from excessive traffic, repeated off-topic use, or other misuse.
- **Documentation index cache**: Cloudflare may cache the public documentation search index for a short period to improve performance. This cache contains documentation content, not a first-party archive of chat transcripts.

Current continuity settings are intentionally limited:

- Local chat state stored in the browser is automatically expired after up to 12 hours of inactivity in the current tab.
- The chat cookie is limited in lifetime, currently up to 30 days, and is used primarily for continuity and abuse prevention rather than profiling.
- Abuse-prevention counters operate on rolling windows such as 1 minute, 15 minutes, and 24 hours, with temporary cooldowns or blocks that can last from seconds to up to 24 hours depending on the detected pattern.

Security controls include HTTPS transport, origin-restricted CORS, signed session tokens, request size limits, rate limiting, cooldowns, temporary blocks, optional Turnstile verification, and server-side grounding rules that restrict the assistant to documentation-only responses.

### 4. Use and Sharing

Chat data is used to:

- Generate documentation-grounded answers
- Preserve short in-session continuity between documentation pages
- Enforce abuse prevention and protect service availability
- Diagnose failures and improve the documentation or assistant behavior

We do not use chat data to build advertising profiles.

Chat data may be processed by the following third parties strictly to operate the service:

- **Cloudflare Workers and Durable Objects** - to receive requests, apply security controls, maintain temporary session and abuse-prevention state, and return the response.
- **Cloudflare Turnstile** - only if challenge mode is enabled, to help verify that traffic appears human.
- **Configured upstream AI provider** - the grounded prompt is sent to the currently configured upstream model service to generate the response. At the time of this update, the service is configured through the Kilo AI gateway endpoint.

The site owner does not intentionally publish chat transcripts to the public repository. However, any third-party service involved in processing the request may apply its own infrastructure-level logging or retention under its own terms.

### 5. User Rights and Controls

Depending on the laws that apply to you, you may have rights to access, correct, or delete personal data associated with your use of the site.

For the AI Assistant specifically:

- You can **stop using the assistant at any time** by closing the chat and not sending messages.
- You can **clear the current tab conversation** using the "Start new chat" action in the widget.
- You can **remove local continuity data** by clearing your browser session data for this site or by closing the tab or session.
- You can **request access, correction, or deletion** of server-side records by contacting us through GitHub Issues or another published contact channel. Because the chat does not require an account, we may need details such as the approximate time, page, or request ID to help identify the relevant records.

Generated answers are informational outputs, not authoritative records. If a message contains incorrect information, the most effective correction is usually to submit a corrected follow-up question or consult the referenced documentation page directly.

### 6. Legal Compliance and Transparency

We aim to operate the site in a way that is consistent with applicable privacy and data protection laws, including:

- The **General Data Protection Regulation (GDPR)** where applicable: <https://eur-lex.europa.eu/eli/reg/2016/679/oj/eng>
- Brazil's **Lei Geral de Protecao de Dados Pessoais (LGPD)** where applicable: <https://www.planalto.gov.br/ccivil_03/_ato2015-2018/2018/lei/l13709.htm>

In practice, this means we try to follow principles such as data minimization, purpose limitation, security, and transparency. We also try to limit the assistant to documentation-relevant inputs and to avoid keeping more conversational data than is needed for the current session and service protection.

## Third-Party Services

The site may rely on the following services to operate feedback and AI assistant features:

- **Cloudflare Workers** - processes rating submissions and chat requests, applies security controls, and returns responses.
- **Cloudflare KV** - stores rating data as the primary database. Data is private and accessible only through the Worker.
- **Cloudflare Durable Objects** - stores temporary chat abuse-prevention counters and cooldown or block state.
- **Cloudflare Turnstile** - may be used for additional chat verification when enabled.
- **GitHub API** - receives asynchronous backups of rating data committed to the repository. Authentication is handled via a token that never leaves Cloudflare Workers.
- **Upstream AI provider** - generates chat responses from the grounded prompt assembled by the Worker.

## Your Rights

You have the right to:

- Request information about what data we hold about you
- Request correction or deletion of your data
- Ask how the AI Assistant operates on your data

To exercise these rights, please contact us via GitHub Issues or email (if provided).

## Changes to This Policy

We may update this Privacy Policy from time to time. Any changes will be posted on this page with an updated revision date.

## Contact

For questions about this Privacy Policy, please open an issue in the GitHub repository.

---

*This Privacy Policy is provided for informational purposes and does not constitute legal advice. If you have specific concerns, consult a legal professional.*
