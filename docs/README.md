# Documentation Index

This documentation is intentionally split into **normative** and **non-normative** layers.

## Canonical source of truth

- **[`CONTRACT.md`](CONTRACT.md)** ← authoritative behavior contract.

If any other page conflicts with `CONTRACT.md`, treat that page as outdated and follow `CONTRACT.md`.

## Reference (normative, implementation-aligned)

- [`reference/json-schema.md`](reference/json-schema.md) — implemented schema keywords + status.
- [`reference/skript-syntax.md`](reference/skript-syntax.md) — implemented Skript effects/expressions.
- [`api-reference.md`](api-reference.md) — implementation-facing Java API notes.
- [`configuration.md`](configuration.md) — exact runtime config keys.

## Guides and tutorials (non-normative)

- [`quickstart.md`](quickstart.md)
- [`installation.md`](installation.md)
- [`guides/integration.md`](guides/integration.md)
- [`tutorials/README.md`](tutorials/README.md)

These pages provide workflows and examples. They must not redefine behavior already defined in `CONTRACT.md`.

## Architecture and audits

- [`architecture.md`](architecture.md)
- [`source-of-truth-audit-2026-03-19.md`](source-of-truth-audit-2026-03-19.md)
- [`deep-system-audit-2026-03-19.md`](deep-system-audit-2026-03-19.md)
- [`practical-documentation-audit.md`](practical-documentation-audit.md)

## Archived / non-normative historical docs

- [`archive/Responsibilities.md`](archive/Responsibilities.md)

## Wiki mirror (operational index)

- [`../wiki/Home.md`](../wiki/Home.md)
- [`../wiki/Audit-Summary-2026-03-19.md`](../wiki/Audit-Summary-2026-03-19.md)

The wiki mirror is non-normative and should always defer to `CONTRACT.md`.
