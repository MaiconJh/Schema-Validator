# Guide: Validate a JSON file from Skript

Use this when your input file is JSON instead of YAML.

## Steps

1. Place JSON data file on disk.
2. Place schema file on disk (JSON or YAML schema extension is supported by loader).
3. Run effect:

```skript
validate json "plugins/Schema-Validator/data/player.json" using schema "plugins/Schema-Validator/schemas/player.schema.json"
set {_errors::*} to last schema validation errors
```

4. Branch on error list size.

## Operational details

- The first `%string%` path is loaded with JSON `ObjectMapper` in `DataFileLoader`.
- The schema path is always loaded by `FileSchemaLoader` using extension detection.
- Result is stored globally as “last result” in `SkriptValidationBridge`.

## Source mapping

1. Effect patterns: `SkriptSyntaxRegistration.register()`.  
2. JSON vs YAML mode switch: `EffValidateData.init()` (`matchedPattern == 0` for YAML; JSON is the other pattern).  
3. Data and schema loading: `DataFileLoader.load()`, `FileSchemaLoader.load()`.  
4. Last-result bridge: `SkriptValidationBridge`.

[← Previous](README.md) | [Next →](schema-directory-workflow.md) | [Home](../../README.md)
