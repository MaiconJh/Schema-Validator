---
title: Config reference
description: Canonical table of configuration keys, defaults, and runtime effects.
doc_type: reference
order: 7
sequence: 16
permalink: /config-reference.html
---

## Configuration keys

| Key | Type | Bundled default (`config.yml`) | Fallback in code (`PluginConfig`) | Runtime effect |
|---|---|---|---|---|
| `schema-directory` | string | `schemas` | `schemas` | Directory used by startup autoload |
| `auto-load` | boolean | `true` | `true` | Enables startup directory scan and registration |
| `cache-enabled` | boolean | `true` | `true` | Enables cache timestamp expiration in `SchemaRegistry` |
| `validation-on-load` | boolean | `false` | `true` | Runs startup schema self-check pass |
| `strict-mode` | boolean | `false` | `false` | Throws on unsupported keywords during schema parse |

## Implementation notes

- `SchemaRegistry` cache expiry is currently configured to `5 minutes` in plugin startup wiring.
- Expired entries are removed lazily on `getSchema()` access.
- Strict mode is applied via `FileSchemaLoader.setFailFastMode(...)`.

## Source mapping

- Config file: `src/main/resources/config.yml`
- Parsing and getters: `PluginConfig`
- Startup wiring: `SchemaValidatorPlugin.onEnable()`
- Cache behavior: `SchemaRegistry.getSchema()`
