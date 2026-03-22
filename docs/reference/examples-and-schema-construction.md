# Reference: Examples And Schema Construction

Examples here reflect currently implemented behavior.

## Minimal Object Contract

```json
{
  "type": "object",
  "required": ["id"],
  "properties": {
    "id": { "type": "string", "minLength": 3 }
  },
  "additionalProperties": false
}
```

## Array With Item Validation

```json
{
  "type": "array",
  "items": { "type": "integer", "minimum": 0 }
}
```

## Composition Example

```json
{
  "type": "object",
  "allOf": [
    { "properties": { "name": { "type": "string" } } },
    { "required": ["name"] }
  ]
}
```

## Conditional Example

```json
{
  "type": "object",
  "properties": {
    "mode": { "type": "string", "enum": ["strict", "loose"] },
    "limit": { "type": "integer" }
  },
  "if": { "properties": { "mode": { "enum": ["strict"] } } },
  "then": { "properties": { "limit": { "minimum": 10 } } },
  "else": { "properties": { "limit": { "minimum": 0 } } }
}
```

## Format Example

```json
{
  "type": "object",
  "properties": {
    "email": { "type": "string", "format": "email" },
    "block": { "type": "string", "format": "minecraft-block" }
  }
}
```

## Current Limits To Keep In Mind

- `minItems`, `maxItems`, and `uniqueItems` are not enforced yet.
- Skript data loading expects object root (`Map<String, Object>`).
- `$ref` is not resolved in current Skript effect validation path.

## Code Mapping

- Schema model: `Schema`, `SchemaType`
- Schema parser: `FileSchemaLoader`
- Runtime validation: `ValidatorDispatcher` + validators
- Format checks: `FormatValidator`

---
Last updated: 2026-03-22  
Documentation version: 0.3.5
