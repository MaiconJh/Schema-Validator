---
title: Examples and schema construction
description: Reference patterns for constructing schemas that align with current validator behavior.
doc_type: reference
order: 6
sequence: 15
permalink: /examples-and-schema-construction.html
---

## Schema model summary

Core schema structure is represented by `Schema` and parsed by `FileSchemaLoader`.

Important field groups:

- Type and shape: `type`, `properties`, `patternProperties`, `items`, `required`, `additionalProperties`
- Primitive constraints: `minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`, `minLength`, `maxLength`, `pattern`, `format`, `enum`
- Composition: `allOf`, `anyOf`, `oneOf`, `not`
- Conditional: `if`, `then`, `else`
- Reference: `$ref`

## Construction pattern: strict object contract

```json
{
  "type": "object",
  "required": ["id", "name"],
  "additionalProperties": false,
  "properties": {
    "id": {"type": "string"},
    "name": {"type": "string", "minLength": 2}
  }
}
```

Use when payload shape must be fixed.

## Construction pattern: dictionary/map object

```json
{
  "type": "object",
  "patternProperties": {
    "^[a-z0-9_-]+$": {"type": "string"}
  },
  "additionalProperties": false
}
```

Use when keys are dynamic but still constrained by regex.

## Construction pattern: typed homogeneous array

```json
{
  "type": "array",
  "items": {
    "type": "integer",
    "minimum": 0
  }
}
```

Current `ArrayValidator` enforces item schema only.

## Construction pattern: conditional rules

```json
{
  "type": "object",
  "properties": {
    "mode": {"type": "string", "enum": ["strict", "loose"]},
    "limit": {"type": "integer"}
  },
  "if": {"properties": {"mode": {"enum": ["strict"]}}},
  "then": {"properties": {"limit": {"minimum": 10}}},
  "else": {"properties": {"limit": {"minimum": 0}}}
}
```

## Current limitations to account for

- `minItems`, `maxItems`, and `uniqueItems` are parsed but not enforced by `ArrayValidator`.
- `$ref` is parsed but not resolver-wired in the default Skript effect path.
- Unknown `format` names currently pass validation.

## Related pages

- Keyword matrix: [Schema keywords](schema-keywords.html)
- Runtime rule order: [Validation behavior](validation-behavior.html)
- Practical examples: [Examples](examples.html)
