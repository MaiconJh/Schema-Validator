# Guide: Validate A JSON File From Skript

Use this when your input data is JSON.

## Steps

1. Put your JSON data file on disk.
2. Put your schema file (`.json`, `.yml`, or `.yaml`) on disk.
3. Run the effect:

```skript
validate json "plugins/Schema-Validator/data/player.json" using schema "plugins/Schema-Validator/schemas/player.schema.json"
set {_errors::*} to last schema validation errors
```

4. If `size of {_errors::*}` is `0`, validation passed.

## Behavior Details

- JSON/YAML mode is selected by the parsed effect pattern in `EffValidateData.init()`.
- Data is loaded by `DataFileLoader.load(path, yamlMode)`.
- The current loader deserializes to `Map<String, Object>` root, so root arrays/scalars are not supported in this effect path.

## Code Mapping

- Syntax patterns: `SkriptSyntaxRegistration.register()`
- Effect mode and execution: `EffValidateData.init()`, `EffValidateData.execute()`
- Data loader: `DataFileLoader.load()`
- Result expression: `ExprLastValidationErrors.get()`

---
Last updated: 2026-03-22  
Documentation version: 0.3.5
