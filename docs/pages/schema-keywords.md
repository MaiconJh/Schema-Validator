---
title: Schema keywords
description: Reference of parsed and enforced keyword behavior in the current implementation.
doc_type: reference
order: 1
sequence: 10
permalink: /schema-keywords.html
---

## How to read this page

Schema handling has two phases:

1. **Parse phase** (`FileSchemaLoader`): keywords are recognized and mapped to `Schema` fields.
2. **Validation phase** (`ObjectValidator`, `ArrayValidator`, `PrimitiveValidator`): only some parsed fields are enforced.

A keyword can be accepted at parse time but still not enforced at runtime.

## Enforced keywords (runtime)

### Type and structure

- `type` (including type arrays like `["string", "null"]`)
- `properties`
- `patternProperties`
- `required`
- `additionalProperties` (boolean and schema forms)
- `items`
- `prefixItems` (2019-09/2020-12 tuple validation)

### Primitive constraints

- `enum`
- `const`
- `minimum`, `maximum`
- `exclusiveMinimum`, `exclusiveMaximum` (numeric form per 2019-09/2020-12 + boolean legacy)
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

## Fully Implemented Keywords

All keywords below are now fully enforced at runtime:

### Array Keywords
- `minItems` — Minimum array length
- `maxItems` — Maximum array length
- `uniqueItems` — Uniqueness constraint
- `prefixItems` — Tuple validation (2019-09+)
- `items` — Schema for array elements
- `additionalItems` — Limited support

### Object Keywords
- `minProperties` — Minimum property count
- `maxProperties` — Maximum property count
- `dependencies` — Property and schema dependencies
- `dependentRequired` — Required properties based on presence (2019-09+)
- `dependentSchemas` — Schema constraints based on presence (2019-09+)

### Reference Keywords
- `$ref` — JSON Pointer reference resolution
- `definitions` / `$defs` — Schema definitions
- `$id` — Base URI for reference resolution
- `$schema` — Schema dialect identification

### Metadata Keywords
- `title` — Schema title
- `description` — Schema description
- `default` — Default value
- `examples` — Example values
- `readOnly` / `writeOnly` — Property constraints
- `deprecated` — Deprecation status
- `comment` — Annotations

## Unsupported keyword handling

Unknown non-`$` keywords are handled by `FileSchemaLoader.detectUnsupportedKeywords()`:

- `strict-mode: false` -> warning log, schema still loads
- `strict-mode: true` -> throws exception and aborts loading

Keys starting with `$` are skipped by unsupported detection logic.

## Reference behavior (`$ref`)

- Full JSON Pointer resolution with navigation by:
  - Keywords (`properties`, `items`, `additionalProperties`)
  - Object keys (`properties/name`)
  - Array indices (`prefixItems/0`, `allOf/1`)
- Support for `definitions` and `$defs` sections
- Escaping support for `~0` (represents `~`) and `~1` (represents `/`)
- `$id`-based indexing for external reference resolution

## Source mapping

- Parse and unsupported detection: `FileSchemaLoader.java`
- Supported keyword registry: `SupportedKeywordsRegistry.java`
- Runtime enforcement: `ObjectValidator.java`, `ArrayValidator.java`, `PrimitiveValidator.java`, `FormatValidator.java`

## Related pages

- Runtime execution order: [Validation behavior](validation-behavior.html)
- Skript-facing constraints: [Skript API](skript-api.html)
- Supported format catalog: [Format reference](format-reference.html)
