# Reference: Schema Keyword Support

This page distinguishes parsed keywords from enforced behavior.

## Enforced At Runtime

- Type checks: `type`
- Object structure: `properties`, `patternProperties`, `required`, `additionalProperties`
- Array structure: `items`
- String constraints: `minLength`, `maxLength`, `pattern`, `format`
- Numeric constraints: `minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`
- Enumerations: `enum`
- Composition: `allOf`, `anyOf`, `oneOf`, `not`
- Conditional: `if`, `then`, `else`

## Parsed But Not Enforced

- `minItems`, `maxItems`, `uniqueItems`, `dependencies`, `const`
- Metadata and compatibility fields such as `version`, `compatibility`
- Registry-listed metadata keywords (`title`, `description`, `examples`, etc.)

## `$ref` Support

- `$ref` is parsed into `Schema.ref`.
- Runtime resolution only happens if validator uses `SchemaRefResolver`.
- Startup self-check path (`validation-on-load`) wires resolver.
- Skript effect path currently uses `new ValidationService()` without resolver.

## Unsupported Keywords

- Non-listed keys trigger warning logs by default.
- With `strict-mode: true`, unsupported keywords throw `IllegalArgumentException` during load.

## Code Mapping

- Parser: `FileSchemaLoader.toSchema()`
- Keyword registry: `SupportedKeywordsRegistry`
- Unsupported detection: `FileSchemaLoader.detectUnsupportedKeywords()`
- Runtime validators: `ObjectValidator`, `ArrayValidator`, `PrimitiveValidator`

---
Last updated: 2026-03-22  
Documentation version: 0.3.5
