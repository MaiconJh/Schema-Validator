---
title: Privacy Policy
layout: default
permalink: /privacy-policy/
description: How your feedback data is collected, used, and stored
toc: false
nav_exclude: true
---

# Privacy Policy

Last updated: March 26, 2026

This Privacy Policy explains how the Schema Validator documentation site ("we", "our", or "the site") handles information when you use the rating feature.

## Information We Collect

When you rate a page using the star rating system, we collect the following information:

- **Rating value** (1 to 5 stars)
- **Page URL** (the specific documentation page you rated)
- **Timestamp** (date and time of your rating)

We do not collect your IP address, user agent, or referrer in the stored data. Your IP is used temporarily for rate limiting (to prevent multiple votes in a short time) but is never saved permanently.

## How We Use This Information

The feedback helps us understand which pages are most helpful and identify areas for improvement. The collected data is used solely to:

- Calculate average ratings per page
- Improve the documentation based on user feedback
- Provide transparency about how the documentation is received

## Where Your Data Is Stored

Rating data is stored in two places:

- **Cloudflare KV** – the primary storage, where ratings are saved immediately after submission. This is a private key-value store managed by Cloudflare and not publicly accessible.
- **GitHub repository** – an asynchronous backup is committed periodically to a public repository:

  <https://github.com/MaiconJh/Schema-Validator/blob/main/docs/pages/feedbacks.json>

  Because the repository is public, anyone can view the backed-up feedback. Please do not include any personal, sensitive, or confidential information in your ratings – only your rating value and page context are recorded.

## Data Retention

Ratings are retained for a maximum of 90 days. After this period, older ratings are automatically removed. Additionally, the system limits storage to the most recent 1,000 ratings per page.

If you wish to have your rating removed, you may open an issue or pull request on the GitHub repository. We will consider reasonable requests to delete data. Note that data stored in Cloudflare KV is private and will be deleted directly upon request.

## Third-Party Services

- **Cloudflare Workers** – processes rating submissions and enforces rate limiting. Your IP is used temporarily for rate limiting but is never stored permanently.
- **Cloudflare KV** – stores rating data as the primary database. Data is private and accessible only through the Worker.
- **GitHub API** – receives asynchronous backups of the rating data committed to the repository. Authentication is handled via a token that never leaves Cloudflare Workers.

## Your Rights

You have the right to:

- Request information about what data we hold about you
- Request correction or deletion of your data

To exercise these rights, please contact us via GitHub Issues or email (if provided).

## Changes to This Policy

We may update this Privacy Policy from time to time. Any changes will be posted on this page with an updated revision date.

## Contact

For questions about this Privacy Policy, please open an issue in the GitHub repository.

---

*This Privacy Policy is provided for informational purposes and does not constitute legal advice. If you have specific concerns, consult a legal professional.*