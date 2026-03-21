# Reference: Examples and schema construction (code-verified)

This page is a full rewrite of the examples section based on:

- Source code in `src/main/java/**` (parser + validators + Skript bridge).
- Example assets in `src/main/resources/examples/**`.

It is organized in the requested phased workflow and only documents behavior that is implemented.

## Phase 1 — Schema behavior analysis map

## 1) Schema node model

All schema nodes are parsed into the `Schema` model, with these fields:

- Type and structure: `type`, `properties`, `patternProperties`, `items`, `required`, `additionalProperties`.
- Primitive constraints: `minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `minLength`, `maxLength`, `pattern`, `format`, `multipleOf`, `enum`.
- Composition/conditional: `allOf`, `anyOf`, `oneOf`, `not`, `if`, `then`, `else`.
- Metadata/storage-only fields: `version`, `compatibility`, `$ref`.

Source: `Schema` class fields/getters and constructor defaults (empty maps/lists when null).【F:src/main/java/com/maiconjh/schemacr/schemes/Schema.java†L15-L157】

## 2) Supported schema `type` values

The parser maps `type` to:

- `object`, `array`, `string`, `number`, `integer`, `boolean`, `null`, `any`

Source: `SchemaType` enum and loader type parsing path.【F:src/main/java/com/maiconjh/schemacr/schemes/SchemaType.java†L6-L15】【F:src/main/java/com/maiconjh/schemacr/schemes/FileSchemaLoader.java†L142-L318】

## 3) Parse defaults and optional behavior

Defaults when keyword is omitted:

- `type`: defaults to `any`.
- `additionalProperties`: defaults to `true`.
- `required`: defaults to empty list.
- `properties` / `patternProperties`: default to empty map.
- `enum`: default empty list (no enum restriction).

Regex patterns in schema are compiled during parsing; invalid regex is logged and ignored for that constraint.

Sources: loader parsing + `Schema` constructor null normalization.【F:src/main/java/com/maiconjh/schemacr/schemes/FileSchemaLoader.java†L143-L214】【F:src/main/java/com/maiconjh/schemacr/schemes/Schema.java†L121-L157】

## 4) Validation flow map

Runtime validator dispatch:

- `OBJECT` → `ObjectValidator`
- `ARRAY` → `ArrayValidator`
- all other types → `PrimitiveValidator`

Object validation order:

1. Resolve `$ref` **only if** resolver is wired.
2. Ensure runtime value is `Map`.
3. Apply `allOf`, `anyOf`, `oneOf`, `not`, `if/then/else`.
4. Check `required`.
5. Validate declared `properties` that are present.
6. For unknown keys, try `patternProperties`; if no match and `additionalProperties: false`, fail.

Sources: `ValidationService`, `ValidatorDispatcher`, `ObjectValidator`.【F:src/main/java/com/maiconjh/schemacr/core/ValidationService.java†L22-L48】【F:src/main/java/com/maiconjh/schemacr/validation/ValidatorDispatcher.java†L12-L27】【F:src/main/java/com/maiconjh/schemacr/validation/ObjectValidator.java†L30-L254】

Primitive validation behavior:

- `any` always passes.
- `integer` accepts integral values (including numeric types with no fractional part).
- `enum` short-circuits: when `enum` exists, other primitive constraints are not evaluated after enum check.
- Unknown `format` values pass (no failure).

Sources: `PrimitiveValidator`, `FormatValidator`.【F:src/main/java/com/maiconjh/schemacr/validation/PrimitiveValidator.java†L18-L145】【F:src/main/java/com/maiconjh/schemacr/validation/FormatValidator.java†L218-L259】

Array validation behavior:

- Value must be a `List`.
- `items` is applied recursively per element.
- `minItems`, `maxItems`, `uniqueItems` are not enforced by `ArrayValidator`.

Sources: `ArrayValidator` and keyword registry mismatch (recognized vs enforced).【F:src/main/java/com/maiconjh/schemacr/validation/ArrayValidator.java†L13-L36】【F:src/main/java/com/maiconjh/schemacr/schemes/SupportedKeywordsRegistry.java†L75-L94】

## 5) Interdependencies that affect examples

- `$ref` only works in resolver-aware paths. The Skript effect creates `new ValidationService()` without resolver, so `$ref` inside the `validate yaml/json ...` effect is not resolved there.
- Data loaded by the Skript effect is hard-typed to `Map<String,Object>`, so top-level array/scalar data files are not supported in that path.

Sources: `EffValidateData` and `DataFileLoader`.【F:src/main/java/com/maiconjh/schemacr/integration/EffValidateData.java†L57-L61】【F:src/main/java/com/maiconjh/schemacr/integration/DataFileLoader.java†L19-L26】

---

## Phase 2 — Example identification and audit

## Primary example corpus audited

- Schemas: `src/main/resources/examples/schemas/*.json|*.yml`
- Data files: `src/main/resources/examples/*.yml`
- Skript examples: `src/main/resources/examples/*.sk`

Representative assets used below:

- `simple-block-schema.json` + `simple-block-example.yml`
- `data-types-formats.schema.json` + `formats-valid-examples.yml` / `formats-invalid-examples.yml`
- `conditional-validation.schema.json` + valid/invalid conditional YAML samples
- `player-profile.schema.json` and `.yml`
- `player-with-address.schema.json` (`$ref`/definitions case)

Sources: files under `src/main/resources/examples`.【F:src/main/resources/examples/schemas/simple-block-schema.json†L1-L59】【F:src/main/resources/examples/simple-block-example.yml†L1-L21】【F:src/main/resources/examples/schemas/data-types-formats.schema.json†L1-L104】【F:src/main/resources/examples/formats-valid-examples.yml†L1-L57】【F:src/main/resources/examples/formats-invalid-examples.yml†L1-L162】【F:src/main/resources/examples/schemas/conditional-validation.schema.json†L1-L152】【F:src/main/resources/examples/schemas/player-with-address.schema.json†L1-L74】

## Corrections made from audit

1. Some `.sk` examples use syntax not registered by this plugin (for example `validate {_player} against {_schema}`), while implemented syntax is file-path based (`validate yaml/json %string% using schema %string%`).
2. `$ref` examples in schema files are valid as schema assets, but are not reproducible through the current Skript effect path due to resolver wiring limitation.
3. Keywords like `const` may appear in examples (`conditional-validation.schema.json`, `complex-item.schema.json`) but are not enforced by current validators.

Sources: syntax registration, effect implementation, keyword/enforcement behavior, example schemas containing `const` and `$ref`.【F:src/main/java/com/maiconjh/schemacr/integration/SkriptSyntaxRegistration.java†L14-L22】【F:src/main/java/com/maiconjh/schemacr/integration/EffValidateData.java†L57-L63】【F:src/main/java/com/maiconjh/schemacr/schemes/SupportedKeywordsRegistry.java†L115-L116】【F:src/main/resources/examples/schemas/conditional-validation.schema.json†L35-L84】【F:src/main/resources/examples/schemas/player-with-address.schema.json†L24-L36】

---

## Phase 3 — Reconstructed examples (step-by-step)

All steps below use currently implemented Skript syntax and executable file paths.

## Example A: Basic object + array validation (JSON schema and YAML data)

### Schema (`player-profile.schema.json`)

```json
{
  "type": "object",
  "properties": {
    "id": {"type": "string"},
    "level": {"type": "number"},
    "active": {"type": "boolean"},
    "tags": {"type": "array", "items": {"type": "string"}}
  }
}
```

Source file exists as-is.【F:src/main/resources/examples/schemas/player-profile.schema.json†L1-L11】

### Skript command

```skript
command /validateprofile:
    trigger:
        validate yaml "plugins/Schema-Validator/examples/profile.yml" using schema "plugins/Schema-Validator/examples/schemas/player-profile.schema.json"
        set {_errors::*} to last schema validation errors
        if size of {_errors::*} is 0:
            send "Profile is valid" to player
        else:
            loop {_errors::*}:
                send "- %loop-value%" to player
```

### Expected behavior

- Passes when YAML root is an object and field types match.
- Fails with type errors when mismatched (for example `level: "ten"`).
- Missing fields do **not** fail here because schema has no `required` list.

Behavior source: object/property/type rules and no required enforcement without `required`.【F:src/main/java/com/maiconjh/schemacr/validation/ObjectValidator.java†L188-L216】【F:src/main/java/com/maiconjh/schemacr/validation/PrimitiveValidator.java†L22-L43】

## Example B: Required + enum + format + numeric constraints

Use `simple-block-schema.json` and `simple-block-example.yml`.

### Why this is useful

This pair demonstrates these enforced rules together:

- `required`
- nested `properties`
- `enum`
- `format: minecraft-block`
- numeric `minimum`

Sources: schema and format validator entries.【F:src/main/resources/examples/schemas/simple-block-schema.json†L1-L59】【F:src/main/java/com/maiconjh/schemacr/validation/FormatValidator.java†L148-L157】

### Skript command

```skript
validate yaml "plugins/Schema-Validator/examples/simple-block-example.yml" using schema "plugins/Schema-Validator/examples/schemas/simple-block-schema.json"
set {_errors::*} to last schema validation errors
```

### Expected output profile

- Empty error list for the provided sample.
- If `verification.block-type` is malformed (for example `diamond_ore` without namespace), expect a format error.
- If `info.category` is outside enum, expect enum error.

Enforcement source: primitive enum/format and required traversal logic.【F:src/main/java/com/maiconjh/schemacr/validation/PrimitiveValidator.java†L46-L138】【F:src/main/java/com/maiconjh/schemacr/validation/ObjectValidator.java†L188-L204】

## Example C: Advanced string formats and `multipleOf`

Use `data-types-formats.schema.json` with `formats-valid-examples.yml` and `formats-invalid-examples.yml`.

### Command

```skript
validate yaml "plugins/Schema-Validator/examples/formats-valid-examples.yml" using schema "plugins/Schema-Validator/examples/schemas/data-types-formats.schema.json"
set {_errors::*} to last schema validation errors
```

### What to observe

- Required keys: `playerId`, `email`, `website`, `registeredAt`, `ipAddress`.
- `multipleOf` is enforced for decimal and integer values.
- Unknown format names are ignored (for example `unix-time` does not fail format by itself in current implementation).

Sources: example schema includes `unix-time`; validator default for unknown format is pass.【F:src/main/resources/examples/schemas/data-types-formats.schema.json†L58-L66】【F:src/main/java/com/maiconjh/schemacr/validation/FormatValidator.java†L254-L259】

## Example D: Conditional composition (`oneOf`, `not`, `if/then/else`) and caveats

Use `conditional-validation.schema.json`.

### Command

```skript
validate yaml "plugins/Schema-Validator/examples/conditional-valid-examples.yml" using schema "plugins/Schema-Validator/examples/schemas/conditional-validation.schema.json"
set {_errors::*} to last schema validation errors
```

### Important caveat (must know)

This schema uses `const` inside `oneOf` branches, but `const` is not enforced by current validators. So branches that rely on `const` as discriminator may behave differently than standard JSON Schema expectations.

Sources: schema includes `const`; `const` only appears in keyword registry (no validator enforcement).【F:src/main/resources/examples/schemas/conditional-validation.schema.json†L35-L84】【F:src/main/java/com/maiconjh/schemacr/schemes/SupportedKeywordsRegistry.java†L115-L116】【F:src/main/java/com/maiconjh/schemacr/validation/ObjectValidator.java†L52-L186】

## Example E: `patternProperties` architecture for dynamic keys

Use `custom-block-schema.json` and `complete-custom-block-example.yml`.

### Why this architecture matters

It validates objects where top-level keys are dynamic (like block IDs) by regex instead of fixed property names.

### Command

```skript
validate yaml "plugins/Schema-Validator/examples/complete-custom-block-example.yml" using schema "plugins/Schema-Validator/examples/schemas/custom-block-schema.json"
set {_errors::*} to last schema validation errors
```

### Expected behavior

- Top-level key names are checked against regex patterns.
- Matching keys are validated against nested object schema.
- Non-matching unknown keys fail when `additionalProperties: false`.

Source: `patternProperties` matching and fallback to `additionalProperties` in `ObjectValidator`.【F:src/main/java/com/maiconjh/schemacr/validation/ObjectValidator.java†L218-L251】【F:src/main/resources/examples/schemas/custom-block-schema.json†L1-L8】

## Example F: `$ref` schema composition (Java service path only)

Use `player-with-address.schema.json`.

### What works

- The loader parses `definitions` and `$ref`.
- Resolution works when validation uses `ValidationService(SchemaRefResolver)`.

### What does not work through Skript effect

- The effect uses `new ValidationService()` (without resolver), so `$ref` remains unresolved there.

Sources: resolver-aware constructor vs effect constructor call.【F:src/main/java/com/maiconjh/schemacr/core/ValidationService.java†L22-L36】【F:src/main/java/com/maiconjh/schemacr/integration/EffValidateData.java†L60-L63】【F:src/main/resources/examples/schemas/player-with-address.schema.json†L24-L36】

---

## Schema construction cookbook (by architecture)

## 1) Strict object contract

Use when you want fully known keys.

```json
{
  "type": "object",
  "required": ["id"],
  "additionalProperties": false,
  "properties": {
    "id": {"type": "string"}
  }
}
```

Effect: unknown keys fail immediately at object level.【F:src/main/java/com/maiconjh/schemacr/validation/ObjectValidator.java†L245-L251】

## 2) Dynamic-key map contract

Use `patternProperties` + `additionalProperties: false` for map-like structures keyed by naming convention.

```json
{
  "type": "object",
  "patternProperties": {
    "^[a-z0-9_-]+$": {"type": "string"}
  },
  "additionalProperties": false
}
```

Mechanics are implemented in unknown-field pass of `ObjectValidator`.【F:src/main/java/com/maiconjh/schemacr/validation/ObjectValidator.java†L218-L251】

## 3) Homogeneous list contract

```json
{
  "type": "array",
  "items": {"type": "integer", "minimum": 0}
}
```

Each element is validated recursively through dispatcher.【F:src/main/java/com/maiconjh/schemacr/validation/ArrayValidator.java†L30-L33】

## 4) Nullable/any payload contract

```json
{"type": "any"}
```

`any` always succeeds in primitive validator.【F:src/main/java/com/maiconjh/schemacr/validation/PrimitiveValidator.java†L21-L24】

## 5) Boolean / null / integer specifics

- `{"type":"boolean"}` requires a Java boolean value.
- `{"type":"null"}` requires actual null.
- `{"type":"integer"}` allows `3` and `3.0`, but not `3.2`.

Source: primitive type switch + integer helper logic.【F:src/main/java/com/maiconjh/schemacr/validation/PrimitiveValidator.java†L26-L43】【F:src/main/java/com/maiconjh/schemacr/validation/PrimitiveValidator.java†L147-L170】

---

## Reproducibility and execution checklist

To reproduce examples reliably:

1. Ensure schema/data files are object-root documents for Skript effect path.
2. Use exact registered syntax:
   - `validate yaml %string% using schema %string%`
   - `validate json %string% using schema %string%`
3. Always inspect `last schema validation errors` right after each validation call.

Sources: syntax registration, data loader root type, expression registration.【F:src/main/java/com/maiconjh/schemacr/integration/SkriptSyntaxRegistration.java†L14-L22】【F:src/main/java/com/maiconjh/schemacr/integration/DataFileLoader.java†L24-L26】

## Source mapping index (quick links)

- Parsing and defaults: `FileSchemaLoader`, `Schema`.
- Runtime validation: `ValidationService`, `ValidatorDispatcher`, `ObjectValidator`, `ArrayValidator`, `PrimitiveValidator`, `FormatValidator`.
- Skript integration: `SkriptSyntaxRegistration`, `EffValidateData`, `ExprLastValidationErrors`, `DataFileLoader`.
- Example assets: `src/main/resources/examples/**`.

[← Previous](validation-behavior.md) | [Next →](config-reference.md) | [Home](../../README.md)
