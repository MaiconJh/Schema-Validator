---
title: Quickstart
description: Run one successful and one failing validation end to end.
doc_type: tutorial
order: 2
sequence: 4
permalink: /quickstart.html
---

## Step 1: Place schema and data

Use repository examples:

- Schema: `src/main/resources/examples/schemas/simple-block-schema.json`
- Data: `src/main/resources/examples/simple-block-example.yml`

## Step 2: Run validation from Skript

```skript
validate yaml "examples/simple-block-example.yml" using schema "examples/schemas/simple-block-schema.json"
set {_errors::*} to last schema validation errors
if size of {_errors::*} is 0:
    broadcast "Validation passed"
else:
    loop {_errors::*}:
        broadcast "%loop-value%"
```

## Step 3: Read the result

- Empty `{_errors::*}` means validation passed.
- Non-empty list means validation failed with compact path and reason details.

## Step 4: Trigger an expected failure

Change one value in the data file to an invalid type and re-run the script. Confirm that the error points to the changed field.

## Minimal schema pattern

```json
{
  "type": "object",
  "required": ["id", "material"],
  "properties": {
    "id": {"type": "string"},
    "material": {"type": "string", "format": "minecraft-block"}
  },
  "additionalProperties": false
}
```

## Continue

- Tune runtime options in [Configuration](configuration.html)
- Review supported rules in [Schema keywords](schema-keywords.html)
