# CONTRACT.md — Schema-Validator Canonical Behavior Contract

> **Canonical scope:** This file is the single source of truth for implemented behavior in this repository. If any other documentation conflicts with this file, this file wins.

## 1) Supported Skript syntax (implemented)

### Effects

```skript
validate yaml %string% using schema %string%
validate json %string% using schema %string%
```

- First string: data file path.
- Second string: schema file path.
- Both are interpreted as file-system paths by the effect implementation.

### Expressions

```skript
last schema validation errors
```

- Returns a list of strings (`ValidationError#toString()`), or empty list if validation succeeded or if no validation has happened yet.

### NOT SUPPORTED (Skript)

- `last schema validation result`
- `last validation errors` (missing `schema`)
- Built-in boolean success expression syntax for the last result

## 2) Runtime configuration keys (implemented)

`plugins/Schema-Validator/config.yml` supports these top-level keys:

```yaml
schema-directory: "schemas"
auto-load: true
cache-enabled: true
validation-on-load: true
```

### Key semantics

- `schema-directory` (string): schema folder path (relative to plugin data folder or absolute).
- `auto-load` (boolean): auto-load `*.json`, `*.yml`, `*.yaml` on plugin enable.
- `cache-enabled` (boolean): enables schema registry cache.
- `validation-on-load` (boolean): validates loaded schemas using generated minimal test data.

### NOT SUPPORTED (configuration)

- Nested `settings.*` structure
- `cache-expiry`
- `schemas-folder`
- `examples-folder`

## 3) Validation behavior contract

## 3.1 Supported schema types

- `object`
- `array`
- `string`
- `number`
- `integer`
- `boolean`
- `null`
- `any`

## 3.2 Important root constraint

Default `ValidationService()` uses `ObjectValidator` as root validator.

Implication:
- In the default path (including Skript effect), root data must be object-like (`Map`), or validation fails with object-type errors.

## 3.3 Implemented keywords

### Object keywords (implemented)

- `properties`
- `required`
- `additionalProperties`
- `patternProperties`
- `allOf`
- `anyOf`

### Array keywords (implemented)

- `items`

### Primitive keywords (implemented)

- `minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`
- `minLength`, `maxLength`, `pattern`, `format`
- `enum`

### Reference keyword (partially implemented)

- `$ref`
  - Implemented when `ObjectValidator` has `SchemaRefResolver` set (e.g., `new ValidationService(refResolver)`).
  - **Not active in default Skript validate effect path**, which uses `new ValidationService()`.

## 3.4 Failure semantics

- Any failed implemented constraint returns a `ValidationError` and makes validation unsuccessful.
- `format` failures are hard validation errors (not warnings).
- Unknown/unsupported keywords are effectively ignored by parser/validators.

## 4) Explicit NOT SUPPORTED contract

The following are documented in some historical pages but are **not implemented**:

- `minItems`
- `maxItems`
- `uniqueItems`
- `minProperties`
- `maxProperties`
- `dependencies`
- Multi-type arrays in `type`, e.g. `"type": ["string", "integer"]`

## 5) Valid and invalid examples

### Valid example (implemented features only)

```json
{
  "type": "object",
  "properties": {
    "name": { "type": "string", "minLength": 3 },
    "level": { "type": "integer", "minimum": 1 },
    "email": { "type": "string", "format": "email" },
    "tags": {
      "type": "array",
      "items": { "type": "string" }
    }
  },
  "required": ["name", "level"],
  "additionalProperties": false
}
```

### Invalid contract example (keyword not implemented)

```json
{
  "type": "array",
  "items": { "type": "string" },
  "minItems": 1,
  "uniqueItems": true
}
```

- This schema parses, but `minItems` and `uniqueItems` are not enforced.

### Invalid runtime assumption example (`$ref` in default Skript path)

```json
{
  "$ref": "#/definitions/Player",
  "definitions": {
    "Player": {
      "type": "object",
      "properties": {
        "name": { "type": "string" }
      },
      "required": ["name"]
    }
  }
}
```

- Resolver wiring is required for `$ref` to work reliably.
- Default Skript validation path does not inject resolver.
