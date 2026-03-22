# Tutorial: First Validation Workflow

Goal: run one end-to-end validation and inspect failure output.

## Prerequisites

- Plugin + Skript installed.
- One schema file and one data file.

## 1. Create a schema

```json
{
  "type": "object",
  "required": ["id", "level"],
  "properties": {
    "id": { "type": "string" },
    "level": { "type": "integer", "minimum": 1 }
  },
  "additionalProperties": false
}
```

## 2. Create intentionally invalid data

```yaml
id: "player-01"
level: 0
extra: true
```

## 3. Run the Skript effect

```skript
validate yaml "plugins/Schema-Validator/data/player.yml" using schema "plugins/Schema-Validator/schemas/player.schema.json"
set {_errors::*} to last schema validation errors
loop {_errors::*}:
    broadcast "%loop-value%"
```

## 4. Interpret output

You should see failures for:

- `minimum` on `level`
- `additionalProperties` for `extra`

## Code Mapping

- Object rules: `ObjectValidator.validate()`
- Primitive numeric rules: `PrimitiveValidator.validate()`
- Compact message format: `ValidationError.toCompactString()`

---
Last updated: 2026-03-22  
Documentation version: 0.3.5
