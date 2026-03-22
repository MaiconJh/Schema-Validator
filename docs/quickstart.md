# Quickstart

Use this path to run one validation in a few minutes.

## Prerequisites

- Java 21 runtime on the server.
- Paper server with Skript installed.
- `Schema-Validator` jar in `plugins/`.

## 1. Place a schema and a data file

Example files already bundled in this repository:

- Schema: `src/main/resources/examples/schemas/simple-block-schema.json`
- Data: `src/main/resources/examples/simple-block-example.yml`

## 2. Run validation from Skript

```skript
validate yaml "examples/simple-block-example.yml" using schema "examples/schemas/simple-block-schema.json"
set {_errors::*} to last schema validation errors
if size of {_errors::*} is 0:
    broadcast "Validation passed"
else:
    loop {_errors::*}:
        broadcast "%loop-value%"
```

## 3. Read the result

- Empty `{_errors::*}` means success.
- Non-empty list means failure; each item uses compact format from `ValidationError.toCompactString()`.

## Code Mapping

- Syntax registration: `SkriptSyntaxRegistration.register()`
- Effect execution: `EffValidateData.execute()`
- Last-result bridge: `SkriptValidationBridge`
- Error list expression: `ExprLastValidationErrors.get()`

## Next

- [Installation](installation.md)
- [Configuration](configuration.md)

---
Last updated: 2026-03-22  
Documentation version: 0.3.5
