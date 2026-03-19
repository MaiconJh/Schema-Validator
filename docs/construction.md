# Schema Construction Guide

Complete guide to building valid schemas for Schema Validator.

---

## Introduction

This guide explains how to construct JSON schemas that are compatible with Schema Validator. Each section covers a specific aspect of schema building with practical examples.

---

## Basic Structure

Every schema is a JSON object. At minimum, you need to specify the `type`:

```json
{ "type": "string" }
```

A complete schema can include:

```json
{
    "type": "object",
    "properties": { ... },
    "required": [ ... ],
    "additionalProperties": false
}
```

---

## Type System

### Primitive Types

| Type | JSON Value | Example |
|------|------------|---------|
| `string` | Text | `"hello"` |
| `integer` | Whole number | `42` |
| `number` | Decimal number | `3.14` |
| `boolean` | True/False | `true` |
| `null` | Null | `null` |
| `any` | Any type | anything |

### Compound Types

| Type | JSON Value | Example |
|------|------------|---------|
| `array` | List | `[1, 2, 3]` |
| `object` | Map | `{"key": "value"}` |

---

## Building Objects

### Simple Object

```json
{
    "type": "object",
    "properties": {
        "name": { "type": "string" },
        "age": { "type": "integer" }
    }
}
```

### Required Fields

```json
{
    "type": "object",
    "properties": {
        "username": { "type": "string" },
        "email": { "type": "string" },
        "age": { "type": "integer" }
    },
    "required": ["username", "email"]
}
```

### No Extra Properties

```json
{
    "type": "object",
    "properties": {
        "name": { "type": "string" }
    },
    "additionalProperties": false
}
```

### Pattern Properties

For dynamic property names:

```json
{
    "type": "object",
    "patternProperties": {
        "^stat_\\w+$": { "type": "integer" },
        "^bonus_\\w+$": { "type": "number" }
    }
}
```

---

## Building Arrays

### Simple Array

```json
{
    "type": "array",
    "items": { "type": "string" }
}
```

### Array of Objects

```json
{
    "type": "array",
    "items": {
        "type": "object",
        "properties": {
            "id": { "type": "integer" },
            "name": { "type": "string" }
        }
    }
}
```

### Constrained Array

```json
{
    "type": "array",
    "items": { "type": "string" },
    "minItems": 1,
    "maxItems": 10,
    "uniqueItems": true
}
```

---

## String Constraints

### Length Constraints

```json
{
    "type": "string",
    "minLength": 3,
    "maxLength": 50
}
```

### Pattern (Regex)

```json
{
    "type": "string",
    "pattern": "^[a-z][a-z0-9_]*$"
}
```

### Enum Values

```json
{
    "type": "string",
    "enum": ["warrior", "mage", "archer"]
}
```

---

## Number Constraints

### IMPORTANT: INTEGER vs NUMBER

**Use `integer` for whole numbers:**

```json
{
    "type": "integer",
    "minimum": 1,
    "maximum": 100
}
```

**Use `number` for decimals only:**

```json
{
    "type": "number",
    "minimum": 0.0,
    "maximum": 100.0
}
```

> Note: In Schema Validator, `number` does NOT accept integers. If you need both, use `number`.

### Range Constraints

```json
{
    "type": "integer",
    "minimum": 0,
    "maximum": 100,
    "exclusiveMaximum": true
}
```

---

## Composition: allOf

Use `allOf` when data must meet ALL conditions:

```json
{
    "allOf": [
        { "type": "string" },
        { "minLength": 3 },
        { "maxLength": 20 }
    ]
}
```

### Combining Object Constraints

```json
{
    "type": "object",
    "allOf": [
        {
            "properties": {
                "name": { "type": "string" }
            },
            "required": ["name"]
        },
        {
            "properties": {
                "level": { "type": "integer", "minimum": 1 }
            }
        }
    ]
}
```

---

## Composition: anyOf

Use `anyOf` when data can match ANY of the conditions:

```json
{
    "anyOf": [
        { "type": "string" },
        { "type": "integer" }
    ]
}
```

### Multiple Object Formats

```json
{
    "anyOf": [
        {
            "type": "object",
            "properties": {
                "type": { "type": "string", "enum": ["user"] }
            },
            "required": ["type"]
        },
        {
            "type": "object",
            "properties": {
                "type": { "type": "string", "enum": ["admin"] }
            },
            "required": ["type"]
        }
    ]
}
```

---

## Nested Structures

### Player with Inventory

```json
{
    "type": "object",
    "properties": {
        "username": { "type": "string" },
        "level": { "type": "integer", "minimum": 1 },
        "inventory": {
            "type": "array",
            "items": {
                "type": "object",
                "properties": {
                    "slot": { "type": "integer", "minimum": 0, "maximum": 35 },
                    "item": { "type": "string" },
                    "amount": { "type": "integer", "minimum": 1, "maximum": 64 }
                },
                "required": ["slot", "item"]
            }
        }
    },
    "required": ["username", "level"]
}
```

### Complex Nested Example

```json
{
    "type": "object",
    "properties": {
        "player": {
            "type": "object",
            "properties": {
                "name": { "type": "string" },
                "stats": {
                    "type": "object",
                    "properties": {
                        "health": { "type": "number", "minimum": 0 },
                        "mana": { "type": "number", "minimum": 0 }
                    },
                    "required": ["health"]
                }
            },
            "required": ["name", "stats"]
        },
        "guild": {
            "type": "object",
            "properties": {
                "name": { "type": "string" },
                "rank": { "type": "string", "enum": ["owner", "admin", "member"] }
            }
        }
    },
    "required": ["player"]
}
```

---

## Minecraft-Specific Examples

### Material List

```json
{
    "type": "array",
    "items": {
        "type": "string",
        "pattern": "^[A-Z][A-Z_]+$"
    },
    "uniqueItems": true
}
```

Valid: `["DIAMOND_SWORD", "GOLDEN_APPLE", "IRON_BLOCK"]`

### Inventory Slot

```json
{
    "type": "object",
    "properties": {
        "slot": {
            "type": "integer",
            "minimum": 0,
            "maximum": 53
        },
        "material": {
            "type": "string",
            "pattern": "^[A-Z][A-Z_]+$"
        },
        "amount": {
            "type": "integer",
            "minimum": 1,
            "maximum": 64
        }
    },
    "required": ["slot", "material"]
}
```

### Player Stats

```json
{
    "type": "object",
    "properties": {
        "maxHealth": { "type": "number", "minimum": 1 },
        "movementSpeed": { "type": "number", "minimum": 0, "maximum": 1 },
        "attackDamage": { "type": "number", "minimum": 0 }
    }
}
```

---

## Common Patterns

### Optional Fields

Make a field optional by NOT including it in `required` and using `additionalProperties: true`:

```json
{
    "type": "object",
    "properties": {
        "required_field": { "type": "string" },
        "optional_field": { "type": "integer" }
    },
    "required": ["required_field"]
}
```

### Multiple Valid Formats (anyOf)

```json
{
    "type": "object",
    "properties": {
        "data": {
            "anyOf": [
                { "type": "string" },
                { "type": "integer" },
                { "type": "object" }
            ]
        }
    }
}
```

### Combining Multiple Constraints

```json
{
    "type": "string",
    "allOf": [
        { "minLength": 3 },
        { "maxLength": 20 },
        { "pattern": "^[a-z]+$" },
        { "enum": ["admin", "moderator", "user"] }
    ]
}
```

---

## Validation Keywords Reference

| Keyword | Type | Description |
|---------|------|-------------|
| `type` | string | Data type |
| `properties` | object | Field definitions |
| `required` | array | Required field names |
| `additionalProperties` | boolean | Allow extra fields |
| `patternProperties` | object | Regex field validation |
| `items` | object | Array element schema |
| `minItems` | integer | Minimum array size |
| `maxItems` | integer | Maximum array size |
| `uniqueItems` | boolean | All elements must be unique |
| `minimum` | number | Minimum value (inclusive) |
| `maximum` | number | Maximum value (inclusive) |
| `exclusiveMinimum` | boolean | Minimum is exclusive |
| `exclusiveMaximum` | boolean | Maximum is exclusive |
| `minLength` | integer | Minimum string length |
| `maxLength` | integer | Maximum string length |
| `pattern` | string | Regex pattern |
| `enum` | array | Allowed values |
| `allOf` | array | Must match all schemas |
| `anyOf` | array | Must match one schema |

---

## Next Steps

- [Data Types Reference](reference/data-types.md)
- [Schema Composition](reference/schema-composition.md)
- [JSON Schema Reference](reference/json-schema.md)

---

[← Back to Documentation](docs/README.md)
