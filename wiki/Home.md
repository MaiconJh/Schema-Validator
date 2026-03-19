# Schema Validator Wiki

> Canonical source of truth: [`docs/CONTRACT.md`](../docs/CONTRACT.md).

Use this wiki as an operational map. Normative behavior lives in `/docs`, especially the contract and reference pages.

## Overview

Schema Validator is a Paper/Spigot plugin with Skript integration for validating YAML/JSON files against schema files.

## Concepts

- **Validation effect**: runs schema validation from Skript with explicit file paths.
- **Validation errors**: exposed through `last schema validation errors`.
- **Schema keywords**: implemented subset documented in the JSON schema reference.
- **Contract-first docs**: `docs/CONTRACT.md` overrides all other pages.

## Start here

1. [Quickstart and Setup](Quickstart-and-Setup)
2. [Schema and Validator Reference](Schema-and-Validator-Reference)
3. [Skript Integration](Skript-Integration)
4. [Troubleshooting and FAQ](Troubleshooting-and-FAQ)

## Authoritative references in `/docs`

- Contract: [`docs/CONTRACT.md`](../docs/CONTRACT.md)
- Docs index: [`docs/README.md`](../docs/README.md)
- Architecture: [`docs/architecture.md`](../docs/architecture.md)
- Configuration: [`docs/configuration.md`](../docs/configuration.md)
- JSON schema keywords: [`docs/reference/json-schema.md`](../docs/reference/json-schema.md)
- Skript syntax: [`docs/reference/skript-syntax.md`](../docs/reference/skript-syntax.md)
- Source-of-truth audit: [`docs/source-of-truth-audit-2026-03-19.md`](../docs/source-of-truth-audit-2026-03-19.md)

## Planned / experimental signals

The following are documented as **not implemented in current runtime** (planned/experimental only):

- `minItems`, `maxItems`, `uniqueItems`
- Boolean expression for last validation result
- Alternate expression name `last validation errors`

See the explicit non-supported list in [`docs/CONTRACT.md`](../docs/CONTRACT.md).
