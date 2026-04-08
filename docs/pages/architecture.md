---
title: Architecture
description: Understand runtime components, integration boundaries, and validation data flow.
doc_type: explanation
order: 2
sequence: 18
permalink: /architecture.html
---

## Main components

- `SchemaValidatorPlugin`: lifecycle (`onEnable`/`onDisable`) and startup orchestration.
- `PluginConfig`: reads config keys and resolves schema directory path.
- `SchemaRegistry`: case-insensitive schema storage and optional cache expiration.
- `RegisteredSchemaMetadata`: registration source, path, and timestamp for runtime schemas.
- `FileSchemaLoader`: JSON/YAML parsing, unsupported keyword detection, schema object construction.
- `ValidationService`: facade over validator dispatch and result assembly.
- `ValidationMetrics`: tracks built-in runtime validation counts and average timing.
- `SchemaValidatorAPI`: public Java/Bukkit facade for other plugins.
- `SchemaValidatorCommand`: command executor and tab completer for administrative usage.
- Validators: `ObjectValidator`, `ArrayValidator`, `PrimitiveValidator`, plus `FormatValidator` helper.
- Optional Skript bridge: `SkriptSyntaxRegistration`, `EffValidateData`, `ExprLastValidationErrors`, `SkriptValidationBridge`.

## Runtime flow

1. Plugin starts and loads `config.yml`.
2. Registry and loader are initialized.
3. Loader fail-fast mode mirrors `strict-mode`.
4. If `auto-load` is true, schemas are scanned and registered.
5. Optional self-validation runs if `validation-on-load` is true.
6. `SchemaValidatorAPI` can serve programmatic requests as soon as the plugin is enabled.
7. Administrative commands are registered.
8. If Skript is available, Skript syntax is registered.
9. Skript validation requests also store the latest result in bridge state.

## Data and schema boundaries

### Schema loading boundary

- Accepts `.json`, `.yml`, `.yaml`.
- Unsupported non-`$` keywords are warned or rejected (strict mode).
- `definitions` and `$defs` are parsed during load.

### Validation boundary

- Validation result is immutable (`ValidationResult`, `ValidationError`).
- Errors include path and detailed reason.
- Dispatch uses `SchemaType` only.

### Java/Bukkit integration boundary

- `SchemaValidatorAPI` resolves the plugin, registry, and validation service.
- Programmatic callers receive results directly without using the Skript bridge.
- API-driven validations are tracked in runtime metrics.

### Command integration boundary

- Commands query the same `SchemaRegistry` used by startup, Skript, and the Java API.
- `validate-file` parses JSON/YAML files directly and validates through `ValidationService`.
- `reload <schemaName>` and `export` depend on file-backed registration metadata.
- `reload --all` updates schemas from the configured directory without wiping unrelated registered schemas.
- `stats` reflects only built-in tracked validation paths.

### Skript integration boundary

- Effect accepts file paths as strings.
- Data loader parses to `Map<String, Object>` root.
- Expression reads only the latest bridge result (global mutable state).

## Implementation notes

- Cache expiry in `SchemaRegistry` is fixed at construction (`5 minutes` in plugin startup).
- All major JSON Schema keywords are now supported and enforced. **Test coverage:** 373 unit tests passing across 23 validator test classes.

## Related pages

- Runtime rule order and errors: [Validation behavior](validation-behavior.html)
- Keyword support contract: [Schema keywords](schema-keywords.html)
- Java/Bukkit integration details: [Java API](java-api.html)
- Administrative commands: [Commands](commands.html)
- Skript API details: [Skript API](skript-api.html)
