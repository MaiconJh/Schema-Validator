# Reference: Validation behavior

## Dispatch model

- `OBJECT` schemas use `ObjectValidator`.
- `ARRAY` schemas use `ArrayValidator`.
- Everything else uses `PrimitiveValidator`.

## Object validation order

1. `$ref` resolution (if resolver available)
2. Type check (`Map` required)
3. `allOf` / `anyOf` / `oneOf` / `not` / conditional (`if/then/else`)
4. Required fields
5. Declared property validation
6. Unknown-field handling through `patternProperties` and `additionalProperties`

## Primitive behavior

- `number` accepts any `Number`.
- `integer` accepts integral numeric values (including decimal numeric types without fractional part).
- `enum` short-circuits further primitive constraint checks when enum fails/succeeds.
- Unknown `format` values are treated as pass (no error).

## Array behavior

- Data must be `List<?>`.
- If schema has `items`, each element is validated recursively.
- If schema has no `items`, no per-item checks are performed.

## Result and error model

- Validation returns `ValidationResult` with immutable error list.
- `ValidationError` contains path, expected facet/type, actual value/type, and description.

## Source mapping

1. Dispatcher: `ValidatorDispatcher.forSchema()`.  
2. Object flow: `ObjectValidator.validate()`.  
3. Primitive flow: `PrimitiveValidator.validate()`.  
4. Array flow: `ArrayValidator.validate()`.  
5. Result model: `ValidationResult`, `ValidationError`.

[← Previous](schema-keywords.md) | [Next →](config-reference.md) | [Home](../../README.md)
