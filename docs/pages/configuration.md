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

## Common keys

```yaml
schema-directory: "schemas"
auto-load: true
cache-enabled: true
validation-on-load: false
strict-mode: false
```

## How to tune safely

### Schema directory

Set `schema-directory` relative to the plugin data folder. Use one controlled directory per environment.

### Auto load

Enable `auto-load` when schemas are stable at startup. Disable it if deployment manages loading manually.

### Cache behavior

Enable `cache-enabled` for repeated schema lookups during normal operation.

### Strict mode

Enable `strict-mode` to fail fast on unsupported schema keywords while loading schemas.

## Validation on load

Use `validation-on-load` only when startup time overhead is acceptable and early failure detection is needed.

## Operational checklist

1. Apply config changes.
2. Restart server.
3. Confirm startup logs and schema load output.
4. Run a known validation script to verify expected behavior.

## Continue

- See exact keyword support in [Schema keywords](schema-keywords.html)
- Inspect runtime order in [Validation behavior](validation-behavior.html)
