# Documentation Index

> **Version:** 0.3.1-SNAPSHOT  
> **Last Updated:** 2026-03-20

This documentation is intentionally split into **normative** and **non-normative** layers.

---

## Canonical source of truth

- **[`CONTRACT.md`](CONTRACT.md)** ← authoritative behavior contract.

If any other page conflicts with `CONTRACT.md`, treat that page as outdated and follow `CONTRACT.md`.

---

## Version Compatibility

| Component | Version |
|-----------|---------|
| **Plugin** | 0.3.5 |
| **Minecraft** | 1.19+ (1.20.x recommended) |
| **Skript** | 2.8+ |
| **Server** | Paper 1.19+ / Spigot 1.19+ |
| **Java** | 17+ |

---

## Reference (normative, implementation-aligned)

- [`reference/json-schema.md`](reference/json-schema.md) — implemented schema keywords + status (includes supported-keywords.md).
- [`reference/skript-syntax.md`](reference/skript-syntax.md) — implemented Skript effects/expressions.
- [`minecraft-formats.md`](minecraft-formats.md) — custom Minecraft ID validation formats.
- [`api-reference.md`](api-reference.md) — implementation-facing Java API notes.
- [`guides/integration.md`](guides/integration.md) — Configuration and integration guide (includes configuration.md).

---

## Guides and tutorials (non-normative)

- [`quickstart.md`](quickstart.md)
- [`installation.md`](installation.md)
- [`guides/integration.md`](guides/integration.md)
- [`tutorials/README.md`](tutorials/README.md)

These pages provide workflows and examples. They must not redefine behavior already defined in `CONTRACT.md`.

---

## Troubleshooting

- [`TROUBLESHOOTING.md`](TROUBLESHOOTING.md) — Common issues and solutions.
- [`faq.md`](faq.md) — Frequently asked questions.

---

## Architecture and audits

- [`architecture.md`](architecture.md)
- [`audits/source-of-truth-audit-2026-03-20.md`](audits/source-of-truth-audit-2026-03-20.md)
- [`audits/deep-system-audit-2026-03-19.md`](audits/deep-system-audit-2026-03-19.md)
- [`audits/documentation-consolidation-plan-2026-03-20.md`](audits/documentation-consolidation-plan-2026-03-20.md)

---

## Archived / non-normative historical docs

- [`archive/Responsibilities.md`](archive/Responsibilities.md)
- [`archive/wiki/`](archive/wiki/) — Legacy wiki content (deprecated)

---

## Contributing

See [`CONTRIBUTING.md`](../CONTRIBUTING.md) for contribution guidelines.

---

## Changelog

See [`CHANGELOG.md`](CHANGELOG.md) for version history.
