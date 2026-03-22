---
title: Architecture
description: Understand runtime components, integration boundaries, and validation data flow.
doc_type: explanation
order: 2
sequence: 16
permalink: /architecture.html
---

## Main components

- `SchemaValidatorPlugin`: lifecycle (`onEnable`/`onDisable`) and startup orchestration.
- `PluginConfig`: reads config keys and resolves schema directory path.
- `SchemaRegistry`: case-insensitive schema storage and optional cache expiration.
- `FileSchemaLoader`: JSON/YAML parsing, unsupported keyword detection, schema object construction.
- `ValidationService`: facade over validator dispatch and result assembly.
- Validators: `ObjectValidator`, `ArrayValidator`, `PrimitiveValidator`, plus `FormatValidator` helper.
- Skript bridge: `SkriptSyntaxRegistration`, `EffValidateData`, `ExprLastValidationErrors`, `SkriptValidationBridge`.

## Runtime flow

1. Plugin starts and loads `config.yml`.
2. Registry and loader are initialized.
3. Loader fail-fast mode mirrors `strict-mode`.
4. If `auto-load` is true, schemas are scanned and registered.
5. Optional self-validation runs if `validation-on-load` is true.
6. Skript syntax is registered.
7. Runtime validation requests execute and latest result is stored in bridge state.

## Data and schema boundaries

### Schema loading boundary

- Accepts `.json`, `.yml`, `.yaml`.
- Unsupported non-`$` keywords are warned or rejected (strict mode).
- `definitions` and `$defs` are parsed during load.

### Validation boundary

- Validation result is immutable (`ValidationResult`, `ValidationError`).
- Errors include path and detailed reason.
- Dispatch uses `SchemaType` only.

### Skript integration boundary

- Effect accepts file paths as strings.
- Data loader parses to `Map<String, Object>` root.
- Expression reads only the latest bridge result (global mutable state).

## Important implementation constraints

- `$ref` resolution is available in code (`SchemaRefResolver`) but not wired in default Skript effect flow.
- Cache expiry in `SchemaRegistry` is fixed at construction (`5 minutes` in plugin startup).
- Some parser-accepted keywords are not yet enforced by validators.

## Extension points

- Wire `SchemaRefResolver` into Skript effect validation path.
- Add enforcement for currently parse-only keywords (`const`, item/property count constraints).
- Extend `ArrayValidator` with `minItems`, `maxItems`, `uniqueItems`.
- Replace global bridge with request-scoped/session-scoped result storage.

## Related pages

- Runtime rule order and errors: [Validation behavior](validation-behavior.html)
- Keyword support contract: [Schema keywords](schema-keywords.html)
- Skript API details: [Skript API](skript-api.html)
