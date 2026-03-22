---
title: Schema keywords
description: Reference of parsed and enforced keyword behavior in the current implementation.
doc_type: reference
order: 1
permalink: /schema-keywords.html
---

## Enforced during validation

- `type`
- `properties`, `patternProperties`, `required`, `additionalProperties`
- `items`
- `enum`
- `minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`
- `minLength`, `maxLength`, `pattern`, `format`
- `allOf`, `anyOf`, `oneOf`, `not`
- `if`, `then`, `else`

## Parsed but not enforced

- `version`, `compatibility`
- `definitions`, `$defs`
- `minItems`, `maxItems`, `uniqueItems`
- `const`, `dependencies`, `minProperties`, `maxProperties`

## Unsupported keyword handling

- Default mode: unknown keywords are logged as warnings.
- Strict mode: unknown keywords throw parsing errors.

## Reference resolution

- `$ref` is parsed into schema metadata.
- `$ref` is only resolved when validation path is created with `SchemaRefResolver`.

## Source mapping

- Parsing: `FileSchemaLoader.toSchema()`
- Unsupported keyword detection: `FileSchemaLoader.detectUnsupportedKeywords()` and `SupportedKeywordsRegistry`
- Enforcement: `ObjectValidator`, `ArrayValidator`, `PrimitiveValidator`, `FormatValidator`

## Related pages

- Runtime execution model: [Validation behavior](validation-behavior.html)
- Component-level architecture: [Architecture](architecture.html)
