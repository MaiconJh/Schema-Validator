---
title: Validate JSON file
description: Validate JSON payloads through the optional Skript integration with reliable path and output handling.
doc_type: how-to
order: 3
sequence: 7
permalink: /validate-json-file.html
---

## When to use this

Use this path when your payload file is JSON (not YAML).

## Steps

1. Place the JSON payload on disk.
2. Place the schema file on disk (`.json`, `.yml`, or `.yaml`).
3. Execute the effect:

```skript
validate json "plugins/Schema-Validator/data/player.json" using schema "plugins/Schema-Validator/schemas/player.schema.json"
set {_errors::*} to last schema validation errors
```

4. Branch by result size:

```skript
if size of {_errors::*} is 0:
    broadcast "Validation passed"
else:
    loop {_errors::*}:
        broadcast "- %loop-value%"
```

## Operational notes

- Pattern index in `EffValidateData.init()` controls YAML vs JSON mode.
- Data loading is delegated to `DataFileLoader.load(path, yamlMode)`.
- Current loader deserializes JSON root as `Map<String, Object>` in this Skript path.

## Related pages

- Syntax and runtime semantics: [Skript API](skript-api.html)
- Rule processing details: [Validation behavior](validation-behavior.html)
