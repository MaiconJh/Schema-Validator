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
2. Resolve `$dynamicRef` if present and no static reference.
3. Ensure data is `Map<?, ?>` (null values are skipped).
4. **Composition and conditionals** (order is as follows):
   - `allOf`: all subschemas must pass.
   - `anyOf`: at least one subschema must pass.
   - `oneOf`: exactly one subschema must pass.
   - `not`: instance must NOT match the subschema.
   - `if`/`then`/`else`: branch according to the `if` condition.
5. Validate required fields.
6. Validate declared `properties` that exist in input.
7. Validate unknown keys with `patternProperties` (regex matches).
8. If still unmatched, enforce `additionalProperties` (boolean or schema).
9. Finally, enforce `unevaluatedProperties` for any property not covered by steps 6–8.

## Array validation order

`ArrayValidator` evaluates in this order:

1. Ensure data is `List<?>`.
2. Apply `minItems` and `maxItems` constraints.
3. Apply `uniqueItems` constraint.
4. Validate `prefixItems` schemas for the first N items (tuple validation).
5. Apply `additionalItems` schema to items beyond `prefixItems` (if defined).
6. Validate `contains` with `minContains`/`maxContains`.
7. Validate items using `items` schema (applies to all items if `prefixItems` not defined, or to items beyond `prefixItems` if present).
8. Enforce `unevaluatedItems` for items not covered by `prefixItems`, `items`, `contains`.

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
