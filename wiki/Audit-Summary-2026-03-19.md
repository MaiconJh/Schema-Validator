# Wiki Audit Summary (2026-03-19)

This audit reviews the current GitHub Wiki surface and aligns it to the repository's canonical documentation model.

## Scope and inventory

### Existing wiki pages detected

1. `README` (single public page)

### Existing sections detected on `README`

- Project overview/introduction
- Setup and folder layout guidance
- Skript usage snippet(s)
- Schema usage explanation

## Consistency and alignment findings

| Severity | Problem | Evidence | Impact |
|---|---|---|---|
| CRITICAL | Wiki behaved as an alternate source of truth rather than a portal to `docs/CONTRACT.md`. | Prior state duplicated setup + syntax as standalone guidance. | Users can follow stale instructions and diverge from actual runtime behavior. |
| MEDIUM | Folder-path conventions differed from current repo docs (`SchemaValidator` vs `Schema-Validator` naming). | Existing audits recorded path mismatch in wiki onboarding. | Operators may place files in wrong locations and fail validation at runtime. |
| MEDIUM | Schema usage wording implied schema name abstractions rather than actual path-based effect usage. | Runtime effect reads explicit file-system path strings. | Confusion when scripts cannot resolve schema names. |
| LOW | No structured topology (overview, quickstart, reference, integration, troubleshooting). | Only one page, no taxonomy. | Hard to find information and maintain consistency over time. |

## Refactor decisions

| Existing page | Action | Reason |
|---|---|---|
| `README` | **Merge/replace into structured pages** | Convert single-page wiki into a role-based information architecture with canonical links to `/docs`. |

## New wiki structure

1. `README` (Overview / Concepts)
2. `Quickstart-and-Setup`
3. `Schema-and-Validator-Reference`
4. `Skript-Integration`
5. `Troubleshooting-and-FAQ`
6. `_Sidebar` (navigation)

## Governance rules applied

- Canonical behavior source: `docs/CONTRACT.md`
- Reference details are linked to authoritative `/docs` pages to prevent drift.
- Any planned or unsupported feature is explicitly labeled **Experimental/Planned**.
- Examples use currently implemented syntax:
  - `validate yaml %string% using schema %string%`
  - `validate json %string% using schema %string%`
  - `last schema validation errors`

## Follow-up recommendation

Mirror this `wiki/` directory into the GitHub Wiki repository on release, or replace remote wiki content with links to these pages to preserve one-way authority from `/docs`. See `wiki/PUBLISHING.md` for the operational publishing sequence.
