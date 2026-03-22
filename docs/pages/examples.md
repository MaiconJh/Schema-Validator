---
title: Examples
description: Practical schema and data examples for common validation workflows.
doc_type: tutorial
order: 4
sequence: 6
permalink: /examples.html
---

## Example 1: Closed object contract

Use this when payload shape is fixed and unknown keys should fail.

```json
{
  "type": "object",
  "required": ["id", "active"],
  "additionalProperties": false,
  "properties": {
    "id": {"type": "string"},
    "active": {"type": "boolean"}
  }
}
```

## Example 2: Dynamic map keys with regex

Use this for dictionary-like objects where key names must follow a pattern.

```json
{
  "type": "object",
  "patternProperties": {
    "^[a-z0-9_-]+$": {"type": "string"}
  },
  "additionalProperties": false
}
```

## Example 3: Conditional requirements (`if/then/else`)

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

## Example 4: Format + numeric constraints

```json
{
  "type": "object",
  "required": ["email", "coins"],
  "properties": {
    "email": {"type": "string", "format": "email"},
    "coins": {"type": "integer", "multipleOf": 10, "minimum": 0}
  }
}
```

## Example 5: Skript debug loop

```skript
validate yaml "plugins/Schema-Validator/examples/simple-block-example.yml" using schema "plugins/Schema-Validator/examples/schemas/simple-block-schema.json"
set {_errors::*} to last schema validation errors

if size of {_errors::*} is 0:
    broadcast "Validation passed"
else:
    loop {_errors::*}:
        broadcast "- %loop-value%"
```

## Failure-first debugging workflow

1. Start from a known valid data file.
2. Introduce one controlled invalid value.
3. Validate and capture `_errors::*`.
4. Fix exactly one error at a time.
5. Repeat until error list is empty.

## Repository examples worth reusing

- `src/main/resources/examples/schemas/simple-block-schema.json`
- `src/main/resources/examples/schemas/conditional-validation.schema.json`
- `src/main/resources/examples/schemas/data-types-formats.schema.json`

## Related pages

- Full keyword support: [Schema keywords](schema-keywords.html)
- Full runtime order: [Validation behavior](validation-behavior.html)
- All supported formats: [Format reference](format-reference.html)
