# Documentation Audit Report (Outside `docs/pages`)

## Objective

Audit and rebuild repository docs (`docs/**`, excluding `docs/pages/**`) for focus, commitment, and structural quality using Write the Docs style principles.

## Applied Criteria

- Audience clarity: each page targets one reader intent.
- Information architecture: tutorials/guides/reference/explanation are separated by purpose.
- Skimmability: short sections, direct headings, low-noise prose.
- Verifiability: behavior statements map to source files.
- Maintainability: consistent footer metadata and predictable navigation.

## Reference Baseline

- Write the Docs guide index: <https://www.writethedocs.org/guide/>
- Documentation principles: <https://www.writethedocs.org/guide/writing/docs-principles.html>
- Docs as code workflow: <https://www.writethedocs.org/guide/docs-as-code.html>
- Beginner composition guidance: <https://www.writethedocs.org/guide/writing/beginners-guide-to-docs.html>

## Findings Before Rebuild

1. Mixed intents in single pages (tutorial + reference + architecture together).
2. Duplicate content across files with drift over time.
3. Stale links and mojibake/encoding artifacts.
4. Claims not aligned with current runtime behavior in some sections.
5. Missing ownership metadata for update/version tracking.

## Rebuild Actions Executed

1. Rewrote all markdown files under `docs/**` except `docs/pages/**`.
2. Standardized section purpose by Diataxis intent.
3. Corrected behavioral claims using current Java sources.
4. Simplified navigation and removed dead/ambiguous references.
5. Added mandatory footer to every file:
   - `Last updated: 2026-03-22`
   - `Documentation version: 0.3.5`

## Resulting Governance Rules

- Keep task pages procedural and short.
- Keep reference pages exact and implementation-bound.
- Keep explanation pages focused on tradeoffs and constraints.
- Any behavior change in code must include matching doc update in this tree.

## Verification Checklist

- [x] All files in scope include footer metadata.
- [x] Links between pages resolve relative to current tree.
- [x] Runtime claims checked against `src/main/java/com/maiconjh/schemacr/**`.
- [x] Initial audit scope excluded `docs/pages/**`; migration phase executed separately.

## Migration phase (2026-03-22)

A structural migration moved published content ownership to `docs/pages/**`.

- Product and user documentation is now authored in `docs/pages/*.md`.
- Legacy files under `docs/**` outside `docs/pages/**` were converted into compatibility pointers.
- Internal process documentation can remain outside `docs/pages/**` when it is not part of the public docs flow.

---
Last updated: 2026-03-22  
Documentation version: 0.3.5
