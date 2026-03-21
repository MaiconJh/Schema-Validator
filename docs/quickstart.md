# Quickstart

This quickstart validates one YAML file against one JSON schema from Skript.

## 1) Place plugin JAR + dependency

- Install Schema-Validator in `plugins/`
- Install Skript (required dependency)

## 2) Put schema and data files on disk

Use repository examples:

- Schema: `src/main/resources/examples/schemas/simple-block-schema.json`
- Data: `src/main/resources/examples/simple-block-example.yml`

## 3) Add Skript snippet

```skript
validate yaml "examples/simple-block-example.yml" using schema "examples/schemas/simple-block-schema.json"
set {_errors::*} to last schema validation errors
if size of {_errors::*} is 0:
    broadcast "Validation passed"
else:
    loop {_errors::*}:
        broadcast "%loop-value%"
```

## 4) Read result

- No list items in `{_errors::*}` means success.
- One or more list items means validation failed, with compact path/type/error text.

## Source mapping

1. Syntax strings and registration: `SkriptSyntaxRegistration.register()`.  
2. Validation execution and error storage: `EffValidateData.execute()`, `SkriptValidationBridge.setLastResult()`.  
3. Error expression output format: `ExprLastValidationErrors.get()`, `ValidationError.toCompactString()`.

[← Previous](README.md) | [Next →](installation.md) | [Home](../README.md)
