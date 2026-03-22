---
title: First validation workflow
description: Execute one failing validation intentionally and interpret every returned error line.
doc_type: tutorial
order: 3
sequence: 5
permalink: /first-validation.html
---

## Goal

Run one end-to-end validation, force a failure, and read the error list correctly.

## Prerequisites

- Plugin installed and enabled.
- Skript installed.
- Access to `plugins/Schema-Validator/`.

## Step 1: Create a schema

```json
{
  "type": "object",
  "required": ["id", "level"],
  "additionalProperties": false,
  "properties": {
    "id": {"type": "string"},
    "level": {"type": "integer", "minimum": 1}
  }
}
```

Save as `plugins/Schema-Validator/schemas/player.schema.json`.

## Step 2: Create invalid data

```yaml
id: "player-01"
level: 0
extra: true
```

Save as `plugins/Schema-Validator/data/player.yml`.

## Step 3: Run the Skript validation command

```skript
validate yaml "plugins/Schema-Validator/data/player.yml" using schema "plugins/Schema-Validator/schemas/player.schema.json"
set {_errors::*} to last schema validation errors
loop {_errors::*}:
    broadcast "%loop-value%"
```

## Step 4: Read the failure output

Expected failures in this scenario:

- `minimum` failure for `level`.
- `additionalProperties` failure for `extra`.

If `_errors::*` is empty, your data unexpectedly passed and the test payload is not failing the current schema.

## Next

- Runtime order details: [Validation behavior](validation-behavior.html)
- Output semantics: [Skript API](skript-api.html)
