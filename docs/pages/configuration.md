---
title: Configuration
description: Configure schema loading, cache behavior, and strictness safely.
doc_type: how-to
order: 2
sequence: 5
permalink: /configuration.html
---

## Config file location

Schema Validator reads:

- `plugins/Schema-Validator/config.yml`

## Current keys and behavior

| Key | Type | Bundled value | Runtime effect |
|---|---|---:|---|
| `schema-directory` | string | `schemas` | Folder scanned by startup autoload |
| `auto-load` | boolean | `true` | Loads `.json`, `.yml`, `.yaml` schemas on enable |
| `cache-enabled` | boolean | `true` | Enables cache expiration in `SchemaRegistry#getSchema` |
| `validation-on-load` | boolean | `false` | Runs schema self-check pass after autoload |
| `strict-mode` | boolean | `false` | Turns unsupported keyword warnings into load exceptions |

> [!NOTE]
> `PluginConfig` uses fallback defaults if a key is missing. For `validation-on-load`, bundled file is `false`, but code fallback is `true` if the key is removed.

## Safe tuning strategy

### 1. Start with low-risk defaults

```yaml
schema-directory: "schemas"
auto-load: true
cache-enabled: true
validation-on-load: false
strict-mode: false
```

### 2. Enable strict-mode in controlled environments first

Use `strict-mode: true` in CI/staging to catch unsupported keywords before production deploy.

### 3. Enable validation-on-load only after schema set stabilizes

`validation-on-load` validates each loaded schema using generated minimal test data. This can increase startup time and may flag complex schemas early.

### 4. Restart and verify logs

After config changes:

1. restart server
2. confirm config summary in logs
3. confirm schema load count
4. run one known validation command

## Operational checklists

### Production baseline

- `auto-load: true`
- `cache-enabled: true`
- `strict-mode: true` (recommended once schema set is stable)
- `validation-on-load: false` (unless startup checks are required)

### Debug session baseline

- `strict-mode: false`
- `validation-on-load: true`

## Continue

- Compare keyword support details in [Schema keywords](schema-keywords.html)
- Review runtime order in [Validation behavior](validation-behavior.html)
