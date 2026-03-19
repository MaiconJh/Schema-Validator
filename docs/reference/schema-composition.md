# Schema Composition

> Canonical behavior contract: [../CONTRACT.md](../CONTRACT.md).

Guide to JSON Schema composition keywords: allOf and anyOf.

---

## Overview

Schema composition allows you to combine multiple schemas to create more complex validation rules. This is useful when you need to validate against multiple constraints or when you want to accept multiple valid formats.

---

## allOf

The `allOf` keyword requires data to validate against **ALL** schemas in the list. It's like an AND operator.

### Syntax

```json
{
  "allOf": [
    { /* schema 1 */ },
    { /* schema 2 */ },
    { /* ... */ }
  ]
}
```

### When to Use

- When you need to apply multiple constraints to the same data
- When combining base requirements with additional validation
- For schema inheritance patterns
- When separate concerns need to be validated together

### Example: Multiple Constraints

```json
{
  "allOf": [
    { "type": "string" },
    { "minLength": 3 },
    { "maxLength": 20 },
    { "pattern": "^[a-z]+$" }
  ]
}
```

This schema requires:
1. The value must be a string
2. Minimum 3 characters
3. Maximum 20 characters
4. Only lowercase letters

**Valid:** `"hello"`, `"world"`, `"abc123"`

**Invalid:** `"ab"` (too short), `"HELLO"` (uppercase), `123` (not a string)

### Example: Object with Base and Extended Properties

```json
{
  "type": "object",
  "allOf": [
    {
      "properties": {
        "id": { "type": "integer" },
        "name": { "type": "string" }
      },
      "required": ["id", "name"]
    },
    {
      "properties": {
        "email": { "type": "string" },
        "age": { "type": "integer", "minimum": 18 }
      }
    }
  ]
}
```

### Example: Combining Types with allOf

```json
{
  "allOf": [
    { "type": "object" },
    {
      "properties": {
        "status": { "type": "string", "enum": ["active", "inactive"] }
      }
    }
  ]
}
```

---

## anyOf

The `anyOf` keyword requires data to validate against **AT LEAST ONE** schema in the list. It's like an OR operator.

### Syntax

```json
{
  "anyOf": [
    { /* schema 1 */ },
    { /* schema 2 */ },
    { /* ... */ }
  ]
}
```

### When to Use

- When a field can have multiple valid formats
- For polymorphic data structures
- When you want to accept either type A or type B
- For optional but mutually exclusive fields

### Example: Multiple Types

```json
{
  "anyOf": [
    { "type": "string" },
    { "type": "integer" }
  ]
}
```

This accepts either a string OR an integer.

**Valid:** `"hello"`, `42`

**Invalid:** `3.14`, `true`, `null`

### Example: Multiple Object Formats

```json
{
  "anyOf": [
    {
      "type": "object",
      "properties": {
        "type": { "type": "string", "enum": ["user"] },
        "username": { "type": "string" }
      },
      "required": ["type", "username"]
    },
    {
      "type": "object",
      "properties": {
        "type": { "type": "string", "enum": ["admin"] },
        "adminLevel": { "type": "integer", "minimum": 1 }
      },
      "required": ["type", "adminLevel"]
    }
  ]
}
```

Valid for either:
```json
{ "type": "user", "username": "player123" }
```
or
```json
{ "type": "admin", "adminLevel": 5 }
```

### Example: Numeric Ranges

```json
{
  "anyOf": [
    { "type": "integer", "minimum": 0, "maximum": 100 },
    { "type": "integer", "minimum": 1000, "maximum": 9999 }
  ]
}
```

Accepts either 0-100 OR 1000-9999.

**Valid:** `50`, `5000`

**Invalid:** `500`, `50000`

---

## allOf vs anyOf Comparison

| Keyword | Logic | All Schemas Must Match | At Least One Must Match |
|---------|-------|----------------------|------------------------|
| `allOf` | AND | ✅ Yes | ❌ No |
| `anyOf` | OR | ❌ No | ✅ Yes |

### Decision Guide

Use **`allOf`** when:
- You need to apply multiple constraints to the same data
- All conditions must be satisfied simultaneously
- You're building a schema from reusable components

Use **`anyOf`** when:
- You accept multiple valid formats
- The data structure can vary
- You're creating optional or alternative schemas

---

## Advanced: Nested Composition

You can nest composition keywords for complex validation:

```json
{
  "allOf": [
    { "type": "object" },
    {
      "anyOf": [
        { "properties": { "role": { "enum": ["admin"] } } },
        { "properties": { "role": { "enum": ["user"] } } }
      ]
    }
  ]
}
```

---

## Error Messages

When validation fails, the error messages include the composition context:

### allOf Errors

If any schema in `allOf` fails, you'll see errors prefixed with `allOf[index]`:

```
allOf[0]: Expected type: string
allOf[1]: Value must be >= 3
```

### anyOf Errors

If no schema matches, you'll see:

```
anyOf[0].age: Value must be >= 18
anyOf[1].email: Primitive value does not match schema type.
Data must match at least one schema in anyOf (matched 0 of 2)
```

---

## Best Practices

1. **Keep schemas focused**: Each schema in a composition should validate one concern
2. **Use descriptive names**: When loading schemas, use meaningful names for debugging
3. **Consider performance**: Deeply nested compositions can slow down validation
4. **Document your schemas**: Explain why you're using allOf or anyOf

---

## Common Use Cases

### Use Case 1: API Request Validation

```json
{
  "type": "object",
  "allOf": [
    { "$ref": "#/definitions/baseRequest" },
    {
      "properties": {
        "action": { "type": "string" }
      },
      "required": ["action"]
    }
  ]
}
```

### Use Case 2: Flexible Data Formats

```json
{
  "anyOf": [
    { "$ref": "#/definitions/userV1" },
    { "$ref": "#/definitions/userV2" }
  ]
}
```

### Use Case 3: Conditional Validation

```json
{
  "type": "object",
  "allOf": [
    { "$ref": "#/definitions/baseUser" },
    {
      "anyOf": [
        {
          "properties": {
            "subscription": { "enum": ["premium"] }
          },
          "required": ["subscription"]
        },
        {
          "properties": {
            "subscription": { "enum": ["free"] }
          }
        }
      ]
    }
  ]
}
```

---

## Next Steps

- Learn about [Data Types](data-types.md)
- See [JSON Schema Reference](json-schema.md)
- Explore [Architecture](architecture.md)

---

[← Back](../README.md) | [Previous: JSON Schema](json-schema.md) | [Next: Data Types](data-types.md)
