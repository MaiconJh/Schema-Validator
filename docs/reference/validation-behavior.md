# Reference: Validation Behavior

## Dispatch Model

`ValidatorDispatcher.forSchema(schema)` routes by schema type:

- `OBJECT` -> `ObjectValidator`
- `ARRAY` -> `ArrayValidator`
- all others -> `PrimitiveValidator`

## Object Validation Order

1. Resolve `$ref` if resolver exists.
2. Require input data to be `Map<?, ?>`.
3. Evaluate `allOf`, `anyOf`, `oneOf`, `not`, `if/then/else`.
4. Enforce `required` keys.
5. Validate declared `properties` that are present.
6. Evaluate unknown keys with `patternProperties` and `additionalProperties`.

## Array Validation

- Data must be `List<?>`.
- If `items` exists, each element is validated recursively.
- If `items` is absent, array passes without per-item checks.

## Primitive Validation

- `any` always passes.
- `integer` accepts integral numeric values (including `3.0`).
- `enum` short-circuits remaining primitive constraints.
- Unknown `format` values pass (`default -> true` in `FormatValidator`).

## Result Model

- `ValidationResult` wraps immutable error list.
- `ValidationError` has path, expected, actual, and description.
- Compact output for scripts uses `ValidationError.toCompactString()`.

## Code Mapping

- Dispatcher: `ValidatorDispatcher`
- Object: `ObjectValidator`
- Array: `ArrayValidator`
- Primitive: `PrimitiveValidator`
- Result types: `ValidationResult`, `ValidationError`

---
Last updated: 2026-03-22  
Documentation version: 0.3.5
