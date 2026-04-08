---
title: Pages architecture
description: Structural and implementation model for the docs/pages Jekyll site.
nav_exclude: true
permalink: /pages-architecture.html
---

# Documentation UX Architecture (`docs/pages/`)

## Scope

This architecture defines how the published documentation site is authored and built from `docs/pages/`.

## Source of truth

Published documentation content is authored in Markdown under `docs/pages/*.md`.

Shared rendering and behavior:

- `docs/pages/_layouts/default.html`
- `docs/pages/_includes/*`
- `docs/pages/assets/css/modern-docs.css`
- `docs/pages/assets/js/modern-docs.js`

Legacy markdown files under `docs/**` outside `docs/pages/**` are compatibility pointers only and are not the published source.

## Information architecture (Diataxis)

Navigation is grouped by `doc_type` front matter:

- `tutorial`
- `how-to`
- `reference`
- `explanation`

Each navigable page also defines `order`, `sequence`, and `permalink`.

## Page metadata contract

Required keys for navigable pages:

- `title`
- `description`
- `doc_type`
- `order`
- `sequence`
- `permalink`

Optional keys:

- `toc` (default `true`)
- `nav_exclude` (default `false`)
- `search_exclude` (default `false`)

## Layout model

`_layouts/default.html` renders:

1. Sticky header (search + theme + mobile menu)
2. Left sidebar grouped by Diataxis
3. Main article container
4. Right TOC generated from `h2` and `h3`

## Client behavior model

`modern-docs.js` provides:

- Theme toggle
- Mobile sidebar open/close
- Active sidebar nav state
- TOC generation and active heading tracking
- Full-text search over `search.json` (title + description + content)
- Code block copy buttons and language labels
- Markdown callout rendering (`[!NOTE]`, `[!TIP]`, etc.)

## URL stability

Use permanent `.html` routes for published pages, including:

- `/index.html`
- `/getting-started.html`
- `/installation.html`
- `/quickstart.html`
- `/first-validation.html`
- `/examples.html`
- `/validate-json-file.html`
- `/schema-directory-workflow.html`
- `/configuration.html`
- `/schema-keywords.html`
- `/validation-behavior.html`
- `/skript-api.html`
- `/java-api.html`
- `/commands.html`
- `/format-reference.html`
- `/config-reference.html`
- `/examples-and-schema-construction.html`
- `/architecture.html`
- `/design-constraints.html`

## Documentation governance

- Published docs source: `docs/pages/**`
- Internal process docs can remain outside `docs/pages/**`
- Any code behavior change must update affected page(s) in the same PR
- Keep compatibility pointers in `docs/**` when old links need continuity

Editorial standards: [Writing guide](writing-guide.html).
