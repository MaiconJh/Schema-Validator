# Schema Construction Guide

> Non-normative guide. Canonical behavior: [`CONTRACT.md`](CONTRACT.md).

Use this guide to build schemas that match the current implemented subset.

## Minimal valid object schema

```json
{
  "type": "object",
  "properties": {
    "name": { "type": "string" }
  },
  "required": ["name"],
  "additionalProperties": false
}
```

## Supported construction patterns

### Object fields

- `properties`
- `required`
- `additionalProperties`
- `patternProperties`

### Array fields

- `items` only

### Primitive constraints

- numbers: `minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`
- strings: `minLength`, `maxLength`, `pattern`, `format`
- values: `enum`

### Composition

- `allOf`
- `anyOf`

## Unsupported keywords (ignored)

- `minItems`
- `maxItems`
- `uniqueItems`
- `minProperties`
- `maxProperties`
- `dependencies`

## `$ref` note

`$ref` requires resolver-enabled validation service (`ValidationService(refResolver)`). Default Skript path does not wire resolver.
