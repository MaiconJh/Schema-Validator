# Documentation Pagination and UX Architecture (`docs/pages/`)

## Scope and constraints

This architecture is intentionally isolated to `docs/pages/` and does not modify project-level docs outside this tree.

## Research basis (GitHub Docs + GitHub Pages)

The implementation aligns with patterns from:

1. GitHub Docs information architecture using a persistent left sidebar, breadcrumb trail, and “In this article” table of contents.
2. GitHub Docs style guidance that emphasizes consistency, scannability, and predictable article structure.
3. GitHub Pages + Jekyll conventions for `_config.yml`, `_layouts`, and `_includes`-driven templates.
4. GitHub/Primer design foundations for color tokens and light/dark mode behavior.

Reference links used during design:

- https://docs.github.com/en/contributing/style-guide-and-content-model/style-guide
- https://docs.github.com/en/pages/setting-up-a-github-pages-site-with-jekyll
- https://docs.github.com/en/pages/configuring-a-publishing-source-for-your-github-pages-site
- https://primer.style/product/getting-started/foundations/color/

## Pagination architecture

### Hierarchy

The page order reflects onboarding-to-advanced progression:

1. `index.html` (Overview)
2. `getting-started.html`
3. `installation.html`
4. `quickstart.html`
5. `configuration.html`
6. `schema-keywords.html`
7. `validation-behavior.html`
8. `examples.html`
9. `architecture.html`

### Navigation and permanence

- Sidebar links provide persistent global navigation.
- Breadcrumbs provide local context and path awareness.
- Permanent heading anchors (`¶`) are generated for deep-linking.

## Jekyll compatibility model

Implemented Jekyll files in this directory:

- `_config.yml`
- `_layouts/default.html`
- `_includes/*`

Pages use front matter to map page metadata (`title`, `description`, `section`) into shared templates.

## Table of contents model

A client-side script scans `h2` and `h3` headings, builds a TOC, and marks active section links via `IntersectionObserver`.

## Search approach

The header search field performs lightweight client-side filtering:

- Filters sidebar page links by text match.
- Highlights whether the current article contains the query term.

## Visual style system

The CSS reproduces a GitHub Docs-like aesthetic with:

- Primer-aligned neutral, border, and accent palette.
- Sticky top header and sticky sidebar.
- Code block/card borders and subtle surfaces.
- Automatic and user-toggleable theme mode.

## Responsive behavior

- Desktop: sidebar + content + TOC columns.
- Tablet: TOC moves below main content.
- Mobile: collapsible sidebar menu with explicit toggle button.

## Accessibility decisions (WCAG 2.1 focused)

- Skip link for keyboard users.
- Semantic landmarks (`header`, `nav`, `main`, `article`, `aside`).
- ARIA labels for sidebar, breadcrumbs, search, and TOC.
- Keyboard-focusable controls and visible focus behavior.
- Contrast-aware color tokens in light/dark themes.

## Information flow rationale

The sequence prioritizes user success:

- Concept framing first.
- Immediate setup and quick win second.
- Operational and reference depth third.
- Architectural understanding last.

This reduces cognitive load while preserving discoverability for advanced users.
