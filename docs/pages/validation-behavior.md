---
title: Validation behavior
description: Reference for dispatch order, rule evaluation, and error aggregation.
doc_type: reference
order: 2
sequence: 11
permalink: /validation-behavior.html
---

## Dispatch model

`ValidationService.validate(data, schema)` chooses validator by `SchemaType` using `ValidatorDispatcher`:

- `OBJECT` -> `ObjectValidator`
- `ARRAY` -> `ArrayValidator`
- other types -> `PrimitiveValidator`

## Object validation order

`ObjectValidator` evaluates in this order:

1. Resolve `$ref` when schema is reference and resolver is wired.
2. Ensure data is `Map<?, ?>`.
3. Apply composition and conditionals (`allOf`, `anyOf`, `oneOf`, `not`, `if/then/else`).
4. Validate required fields.
5. Validate declared `properties` that exist in input.
6. Validate unknown keys with `patternProperties`.
7. If still unmatched, enforce `additionalProperties`.

## Primitive validation details

### Type checks

- `number` accepts any Java `Number`.
- `integer` accepts integral numbers (`Integer`, `Long`, etc., or decimal with zero fraction).
- `null` requires `null` value.
- `any` always passes.

### Rule order

1. Type check first.
2. `enum` check next.
3. If `enum` exists and fails, remaining primitive constraints are not evaluated.
4. Numeric/string/format constraints run after successful type and enum checks.

## Array validation details

- Value must be `List<?>`.
- `items` schema is applied recursively to each element.
- If `items` is missing, per-item checks are skipped.
- `minItems`, `maxItems`, `uniqueItems` are fully enforced.
- `prefixItems` supports tuple validation (2019-09/2020-12).

## Error model

- Validators return `List<ValidationError>`.
- `ValidationResult.from(errors)` marks success if list is empty.
- Each `ValidationError` includes:
  - `nodePath`
  - `expectedType`
  - `actualType`
  - `description`

Compact format returned to Skript expression:

```text
[nodePath] expectedType: actualType - description
```

## Known runtime constraints

- Data loader in Skript integration parses root as `Map<String, Object>`.

## Related pages

- Keyword support scope: [Schema keywords](schema-keywords.html)
- Syntax contract and path behavior: [Skript API](skript-api.html)
- Practical recipes: [Examples](examples.html)
