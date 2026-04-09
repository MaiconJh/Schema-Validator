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
| `data-directory` | string | `data` | `data` | Directory for data files to auto-validate |
| `auto-load` | boolean | `true` | `true` | Enables startup directory scan and registration |
| `cache-enabled` | boolean | `true` | `true` | Enables cache timestamp expiration in `SchemaRegistry` |
| `validation-on-load` | boolean | `false` | `true` | Runs startup schema self-check pass |
| `auto-validate-data-files` | boolean | `true` | `true` | Auto-validates data files with `schema-validation-path` key |
| `strict-mode` | boolean | `false` | `false` | Throws on unsupported keywords during schema parse |

## Data file validation

When `auto-validate-data-files` is enabled, the plugin will scan the `data-directory` for files containing a `schema-validation-path` key. Example:

```yaml
# data/my-block.yml
schema-validation-path: "custom-block.schema.json"
name: "Diamond Block"
id: "diamond_block"
rarity: "epic"
type: "block"
```

The `schema-validation-path` value should be a path relative to the `schema-directory`.

## Implementation notes

- `SchemaRegistry` cache expiry is currently configured to `5 minutes` in plugin startup wiring.
- Expired entries are removed lazily on `getSchema()` access.
- Strict mode is applied via `FileSchemaLoader.setFailFastMode(...)`.

## Source mapping

- Config file: `src/main/resources/config.yml`
- Parsing and getters: `PluginConfig`
- Startup wiring: `SchemaValidatorPlugin.onEnable()`
- Cache behavior: `SchemaRegistry.getSchema()`
