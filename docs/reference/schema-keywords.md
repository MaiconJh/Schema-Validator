# Reference: Schema keyword support

This table reflects implemented parsing + enforcement behavior.

## Enforced at validation time

- `type`
- `properties`
- `patternProperties`
- `required`
- `additionalProperties`
- `items`
- `enum`
- `minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`
- `minLength`, `maxLength`, `pattern`, `format`
- `allOf`, `anyOf`, `oneOf`, `not`
- `if`, `then`, `else`

## Parsed but not enforced (current implementation)

- `version`, `compatibility` (stored only)
- `definitions`, `$defs` (parsed for loader definitions map)
- `minItems`, `maxItems`, `uniqueItems`, `dependencies`, `const`, many metadata keywords listed in `SupportedKeywordsRegistry`

## Unsupported-keyword handling

- Unknown keywords are logged as warnings by default.
- In strict mode (`failFastMode=true`), unknown keywords throw `IllegalArgumentException` during schema parsing.

## `$ref`

- Parsed into `Schema.ref`.
- Resolved only if validator path has a `SchemaRefResolver` wired into `ObjectValidator`.

## Source mapping

1. Parsing: `FileSchemaLoader.toSchema()`.  
2. Unsupported-keyword detection: `FileSchemaLoader.detectUnsupportedKeywords()`, `SupportedKeywordsRegistry`.  
3. Enforcement: `ObjectValidator`, `ArrayValidator`, `PrimitiveValidator`, `FormatValidator`.  
4. `$ref` support path: `ObjectValidator.validate()`, `ValidationService(SchemaRefResolver)`.

[← Previous](skript-syntax.md) | [Next →](validation-behavior.md) | [Home](../../README.md)
