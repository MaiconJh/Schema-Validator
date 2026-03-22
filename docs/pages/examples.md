---
title: Examples
description: Practical schema and data examples for common validation workflows.
doc_type: tutorial
order: 3
permalink: /examples.html
---

## Example 1: Basic object contract

```json
{
  "type": "object",
  "required": ["id", "active"],
  "properties": {
    "id": {"type": "string"},
    "active": {"type": "boolean"}
  },
  "additionalProperties": false
}
```

Use this when your payload has a fixed shape and no dynamic keys.

## Example 2: Pattern-based map keys

```json
{
  "type": "object",
  "patternProperties": {
    "^[a-z0-9_-]+$": {"type": "string"}
  },
  "additionalProperties": false
}
```

Use this for dictionary-like data with controlled key syntax.

## Example 3: Conditional rules

```json
{
  "type": "object",
  "required": ["playerType"],
  "properties": {
    "playerType": {"type": "string", "enum": ["warrior", "merchant"]},
    "level": {"type": "integer"}
  },
  "if": {
    "properties": {"playerType": {"enum": ["warrior"]}}
  },
  "then": {
    "required": ["level"],
    "properties": {"level": {"minimum": 1}}
  }
}
```

Use this when field requirements depend on another field value.

## Debug loop for failed validations

1. Validate with a known failing payload.
2. Inspect `last schema validation errors` output.
3. Fix one error at a time.
4. Re-run validation until errors are empty.

## Related pages

- Full keyword reference: [Schema keywords](schema-keywords.html)
- Runtime behavior details: [Validation behavior](validation-behavior.html)
