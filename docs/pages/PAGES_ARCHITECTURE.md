# Documentation UX Architecture (`docs/pages/`)

## Scope and constraints

This architecture is intentionally isolated to `docs/pages/` and does not modify project-level docs outside this tree.

## Design references

The current visual system follows modern product-docs patterns inspired by Stripe, Vercel, Next.js, React, and Tailwind-style layouts. The core implementation is the `modern-docs.css` design system and `modern-docs.js` interaction layer.

## Source of truth in this folder

Primary assets:
- `docs/pages/assets/css/modern-docs.css`
- `docs/pages/assets/js/modern-docs.js`

Optional Jekyll compatibility (not required for the static HTML build):
- `docs/pages/_config.yml`
- `docs/pages/_layouts/default.html`
- `docs/pages/_includes/*`

## Information architecture

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

## Navigation system

Persistent elements:
- Sidebar navigation is always present on desktop and collapsible on mobile.
- Breadcrumbs provide local context within each page.
- Navigation arrows at the end of articles provide linear progression.

Active state:
- `modern-docs.js` sets `.active` and `aria-current="page"` on the current page link.
- Active styling uses a left inset highlight and elevated contrast for visibility.

Icons:
- Sidebar icons are injected by `modern-docs.js` and inherit link color on hover and active.

## Sidebar visual system

Hierarchy and spacing:
- Uppercase section label with increased letter-spacing.
- Links are flex-aligned with icon and text spacing (`gap`) and padded hit targets.

Interaction styling:
- No default underline. Underline appears only on hover and focus.
- Hover uses a subtle background and text color shift.
- Active uses a stronger background and a left accent bar.

Theming:
- Sidebar colors are driven by `--sidebar-*` tokens.
- Background uses a subtle vertical gradient in both light and dark modes.

## Table of contents (TOC)

The TOC is generated client-side by scanning `h2` and `h3` headings inside `#article-body`. Active section tracking is handled via `IntersectionObserver`.

## Search behavior

The header search input performs client-side filtering:
- Filters sidebar links by text match.
- Marks the article with a no-results state if the term is not present.

## Layout and responsiveness

Desktop layout:
- `.shell` grid: sidebar + main content.
- `.content-layout` grid: article + TOC.

Responsive behavior:
- At `max-width: 1200px`, TOC is hidden.
- At `max-width: 1024px`, sidebar becomes an off-canvas panel.
- At `max-width: 768px`, paddings and typography scale down.

## Accessibility decisions

Key decisions aligned with WCAG 2.1:
- Skip link for keyboard users.
- Semantic landmarks (`header`, `nav`, `main`, `article`, `aside`).
- ARIA labels for sidebar, breadcrumbs, search, and TOC.
- Visible focus styles and `aria-current="page"` for active nav items.

## Information flow rationale

The sequence prioritizes user success:
- Concept framing first.
- Immediate setup and quick win second.
- Operational and reference depth third.
- Architectural understanding last.
