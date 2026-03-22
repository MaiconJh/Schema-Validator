# Reference: Skript Syntax

## Registered Effect Patterns

1. `validate yaml %string% using schema %string%`
2. `validate json %string% using schema %string%`

## Registered Expression Pattern

- `last schema validation errors`

## Runtime Semantics

- `matchedPattern == 0` means YAML mode in `EffValidateData.init()`.
- Data and schema paths are converted with `Path.of(...)` and used as-is.
- Effect loads schema and registers it into registry under schema file name (including extension).
- Result is stored in static bridge (`SkriptValidationBridge`).

## Error Behavior

- On validation failure, the effect logs full `ValidationError.toString()` lines.
- On exception, the bridge receives one synthetic error at path `$`.
- Expression returns empty array when no result or when validation succeeded.

## Code Mapping

- Registration: `SkriptSyntaxRegistration.register()`
- Effect: `EffValidateData`
- Expression: `ExprLastValidationErrors`
- Bridge: `SkriptValidationBridge`

---
Last updated: 2026-03-22  
Documentation version: 0.3.5
