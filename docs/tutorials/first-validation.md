# Tutorial: First validation workflow

Goal: run one end-to-end schema validation and inspect the produced errors.

## Prerequisites

- Plugin and Skript installed
- One schema file and one data file

## Step 1 — Use a schema with object properties

Use `player-profile.schema.json` example structure:

```json
{
  "type": "object",
  "properties": {
    "id": {"type": "string"},
    "level": {"type": "number"},
    "active": {"type": "boolean"}
  }
}
```

## Step 2 — Run validation effect in Skript

```skript
validate yaml "examples/simple-block-example.yml" using schema "examples/schemas/simple-block-schema.json"
set {_errors::*} to last schema validation errors
```

## Step 3 — Interpret output

- If `{_errors::*}` is empty, validation succeeded.
- If not empty, each value is a compact message with path, expected facet, actual value/type, and detail.

## Step 4 — Trigger a failure deliberately

Set an invalid value in your data file (e.g., wrong type for a property), rerun, and inspect the error list.

## Source mapping

1. Schema example source: `src/main/resources/examples/schemas/player-profile.schema.json`.  
2. Skript usage example source: `src/main/resources/examples/validate-simple-example.sk`.  
3. Error formatting: `ValidationError.toCompactString()`, `ExprLastValidationErrors.get()`.

[← Previous](README.md) | [Next →](../reference/README.md) | [Home](../../README.md)
