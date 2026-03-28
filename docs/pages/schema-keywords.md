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
- `propertyNames`
- `items`
- `prefixItems` (2019-09/2020-12 tuple validation)
- `contains`, `minContains`, `maxContains`

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

All keywords below are now fully enforced at runtime and verified by 324 unit tests across 22 validator classes.

### Array Keywords
- `minItems` — Minimum array length
- `maxItems` — Maximum array length
- `uniqueItems` — Uniqueness constraint
- `prefixItems` — Tuple validation (2019-09+)
- `items` — Schema for array elements
- `contains` / `minContains` / `maxContains` — Match-count constraints
- `additionalItems` — Limited support

### Object Keywords
- `minProperties` — Minimum property count
- `maxProperties` — Maximum property count
- `dependencies` — Property and schema dependencies
- `dependentRequired` — Required properties based on presence (2019-09+)
- `dependentSchemas` — Schema constraints based on presence (2019-09+)
- `propertyNames` — Property-name schema validation

### Reference Keywords
- `$ref` — JSON Pointer reference resolution
- `$defs` / `definitions` — Schema definitions (`definitions` as legacy alias)
- `$id` — Base URI for reference resolution
- `$schema` — Schema dialect identification

### Metadata Keywords
- `title` — Schema title
- `description` — Schema description
- `default` — Default value
- `examples` — Example values
- `readOnly` / `writeOnly` — Property constraints
- `deprecated` — Deprecation status
- `$comment` / `comment` — Annotations (`comment` as legacy alias)

## Unsupported keyword handling

Unknown keywords are handled by `FileSchemaLoader.detectUnsupportedKeywords()`:

- `strict-mode: false` -> warning log, schema still loads
- `strict-mode: true` -> throws exception and aborts loading

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
