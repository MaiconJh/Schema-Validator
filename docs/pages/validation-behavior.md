---
title: Validation behavior
description: Reference for dispatch order, rule evaluation, and error aggregation.
doc_type: reference
order: 2
sequence: 7
permalink: /validation-behavior.html
---

## Dispatch model

- `OBJECT` schemas -> `ObjectValidator`
- `ARRAY` schemas -> `ArrayValidator`
- Primitive schemas -> `PrimitiveValidator`

## Object validation order

1. Resolve `$ref` when resolver is available.
2. Confirm value type is `Map`.
3. Apply composition and conditionals (`allOf`, `anyOf`, `oneOf`, `not`, `if/then/else`).
4. Validate required fields.
5. Validate declared properties.
6. Evaluate unknown properties using `patternProperties` and `additionalProperties`.

## Primitive behavior

- `number` accepts any Java `Number`.
- `integer` accepts integral numeric values.
- `enum` is evaluated before other primitive constraints.
- Unknown formats currently pass.

## Array behavior

- Value must be `List<?>`.
- `items` validates each element recursively.
- If `items` is missing, per-item checks are skipped.

## Error model

- Validation returns `ValidationResult`.
- Errors are immutable `ValidationError` entries with path, expected rule, actual value/type, and detail.

## Related pages

- Keyword support details: [Schema keywords](schema-keywords.html)
- Practical usage examples: [Examples](examples.html)
