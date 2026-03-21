# Reference: Skript syntax

## Registered effect patterns

1. `validate yaml %string% using schema %string%`
2. `validate json %string% using schema %string%`

## Registered expression pattern

- `last schema validation errors`

## Runtime behavior

- Effect loads data path and schema path from the two string expressions.
- Effect stores `ValidationResult` into global bridge (`SkriptValidationBridge`).
- Expression returns `String[]`; empty array when no result or success.

## Source mapping

1. Registration: `SkriptSyntaxRegistration.register()`.  
2. Effect init/execute: `EffValidateData.init()`, `EffValidateData.execute()`.  
3. Expression retrieval: `ExprLastValidationErrors.get()`.

[← Previous](README.md) | [Next →](schema-keywords.md) | [Home](../../README.md)
