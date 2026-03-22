---
title: Schema keywords
description: Reference of parsed and enforced keyword behavior in the current implementation.
doc_type: reference
order: 1
sequence: 6
permalink: /schema-keywords.html
---

## How to read this page

Schema handling has two phases:

1. **Parse phase** (`FileSchemaLoader`): keywords are recognized and mapped to `Schema` fields.
2. **Validation phase** (`ObjectValidator`, `ArrayValidator`, `PrimitiveValidator`): only some parsed fields are enforced.

A keyword can be accepted at parse time but still not enforced at runtime.

## Enforced keywords (runtime)

### Type and structure

- `type`
- `properties`
- `patternProperties`
- `required`
- `additionalProperties`
- `items`

### Primitive constraints

- `enum`
- `minimum`, `maximum`
- `exclusiveMinimum`, `exclusiveMaximum`
- `multipleOf`
- `minLength`, `maxLength`
- `pattern`
- `format`

### Composition and conditional

- `allOf`
- `anyOf`
- `oneOf`
- `not`
- `if`, `then`, `else`

## Parsed but not enforced

These are accepted in schema loading and may be retained in `Schema`, but current validators do not enforce them.

- `version`, `compatibility`
- `definitions`, `$defs`
- `const`
- `minItems`, `maxItems`, `uniqueItems`
- `minProperties`, `maxProperties`, `dependencies`

## Unsupported keyword handling

Unknown non-`$` keywords are handled by `FileSchemaLoader.detectUnsupportedKeywords()`:

- `strict-mode: false` -> warning log, schema still loads
- `strict-mode: true` -> throws exception and aborts loading

Keys starting with `$` are skipped by unsupported detection logic.

## Reference behavior (`$ref`)

- `$ref` is parsed into schema metadata.
- Runtime resolution requires `SchemaRefResolver` wired into validation flow.
- The default Skript effect path currently uses `new ValidationService()` without resolver.

## Source mapping

- Parse and unsupported detection: `FileSchemaLoader.java`
- Supported keyword registry: `SupportedKeywordsRegistry.java`
- Runtime enforcement: `ObjectValidator.java`, `ArrayValidator.java`, `PrimitiveValidator.java`, `FormatValidator.java`

## Related pages

- Runtime execution order: [Validation behavior](validation-behavior.html)
- Skript-facing constraints: [Skript API](skript-api.html)
- Supported format catalog: [Format reference](format-reference.html)
