---
title: Pages architecture
description: Structural and implementation model for the docs/pages Jekyll site.
nav_exclude: true
permalink: /pages-architecture.html
---

# Documentation UX Architecture (`docs/pages/`)

## Scope

This architecture is isolated to `docs/pages/` and defines how the published docs site is built with Jekyll.

## Source of truth

The published documentation pages are Markdown files in `docs/pages/*.md`.

Shared rendering and behavior are implemented by:

- `docs/pages/_layouts/default.html`
- `docs/pages/_includes/*`
- `docs/pages/assets/css/modern-docs.css`
- `docs/pages/assets/js/modern-docs.js`

Legacy static HTML pages are not source of truth.

## Information architecture (Diataxis)

Navigation is grouped by `doc_type` metadata in page front matter:

- `tutorial`
- `how-to`
- `reference`
- `explanation`

Each page also defines `order` for stable sorting and `permalink` to preserve public URLs.

## Page metadata contract

Each navigable page must declare:

- `title`
- `description`
- `doc_type`
- `order`
- `sequence`
- `permalink`

Optional keys:

- `toc` (default `true`)
- `nav_exclude` (default `false`)

## Layout model

`_layouts/default.html` renders:

1. Sticky header with search, theme toggle, and mobile sidebar toggle
2. Left sidebar grouped by Diataxis sections
3. Main article container with page title and lead
4. Auto-generated TOC from `h2` and `h3`

`_includes/sidebar.html` builds navigation from `site.pages` filtered by `doc_type`.

## Client behavior model

`modern-docs.js` provides:

- Theme toggle (light/dark)
- Mobile sidebar open/close
- Active nav detection with icon injection
- TOC generation and active heading tracking
- Sidebar link filtering by search query
- Code block copy button enhancement and language labels
- GitHub-style markdown callout rendering (`[!NOTE]`, `[!TIP]`, etc.)

## URL stability

Permalinks preserve historical routes and add new reference pages:

- `/index.html`
- `/getting-started.html`
- `/installation.html`
- `/quickstart.html`
- `/configuration.html`
- `/schema-keywords.html`
- `/validation-behavior.html`
- `/skript-api.html`
- `/format-reference.html`
- `/examples.html`
- `/architecture.html`

## Version signaling

Global version settings are defined in `_config.yml`:

- `docs_version`
- `latest_docs_version`
- `latest_docs_url`
- `plugin_version_range`

UI behavior:

- Header shows current docs version badge.
- Article header shows `Applies to` plugin range.
- A warning banner appears only when `docs_version` is different from `latest_docs_version`.

## Accessibility baseline

- Skip link to main content
- Semantic landmarks (`header`, `nav`, `main`, `article`, `aside`)
- `aria-current="page"` for active nav item
- Focus-visible styles and keyboard navigable controls

## Documentation governance

Editorial and structure standards are defined in [Writing guide](writing-guide.md).
