# JSON Schema Reference

Complete guide to all JSON Schema Keywords supported by Schema Validator.

---

## Data Types (Type)

Defines the value type.

```json
{ "type": "string" }
{ "type": "integer" }
{ "type": "number" }
{ "type": "boolean" }
{ "type": "array" }
{ "type": "object" }
```

| Type | Description | Example |
|------|-------------|---------|
| `string` | Text | `"hello"` |
| `integer` | Integer number | `42` |
| `number` | Decimal number | `3.14` |
| `boolean` | True/False | `true` / `false` |
| `array` | List | `[1, 2, 3]` |
| `object` | Object | `{"key": "value"}` |

---

## String

### minLength / maxLength

```json
{
  "type": "string",
  "minLength": 3,
  "maxLength": 16
}
```

### pattern (Regex)

```json
{
  "type": "string",
  "pattern": "^[a-zA-Z][a-zA-Z0-9_]*$"
}
```

### format

```json
{
  "type": "string",
  "format": "date-time"
}
```

Supported formats:
- `date-time` - ISO 8601 date and time
- `date` - ISO 8601 date
- `time` - ISO 8601 time
- `email` - Email
- `uri` - URI
- `hostname` - Hostname
- `ipv4` - IPv4 address
- `ipv6` - IPv6 address

### enum

```json
{
  "type": "string",
  "enum": ["warrior", "mage", "archer"]
}
```

---

## Numbers (Number/Integer)

### minimum / maximum

```json
{
  "type": "integer",
  "minimum": 1,
  "maximum": 100
}
```

### exclusiveMinimum / exclusiveMaximum

```json
{
  "type": "number",
  "minimum": 0,
  "exclusiveMaximum": 100
}
```

### multipleOf

```json
{
  "type": "integer",
  "multipleOf": 5
}
```

---

## Arrays

### items

```json
{
  "type": "array",
  "items": {
    "type": "string"
  }
}
```

### minItems / maxItems

```json
{
  "type": "array",
  "items": { "type": "string" },
  "minItems": 1,
  "maxItems": 10
}
```

### uniqueItems

```json
{
  "type": "array",
  "items": { "type": "integer" },
  "uniqueItems": true
}
```

### Array of Objects

```json
{
  "type": "array",
  "items": {
    "type": "object",
    "properties": {
      "name": { "type": "string" },
      "amount": { "type": "integer" }
    }
  }
}
```

---

## Objects

### properties

```json
{
  "type": "object",
  "properties": {
    "name": { "type": "string" },
    "age": { "type": "integer" }
  }
}
```

### required

```json
{
  "type": "object",
  "properties": {
    "name": { "type": "string" },
    "email": { "type": "string" }
  },
  "required": ["name"]
}
```

### minProperties / maxProperties

```json
{
  "type": "object",
  "minProperties": 1,
  "maxProperties": 10
}
```

### additionalProperties

```json
{
  "type": "object",
  "additionalProperties": false
}
```

### patternProperties

```json
{
  "type": "object",
  "patternProperties": {
    "^stat_\\w+$": { "type": "integer" },
    "^bonus_\\w+$": { "type": "number" }
  }
}
```

### dependencies

```json
{
  "type": "object",
  "dependencies": {
    "credit_card": ["billing_address"]
  }
}
```

---

## Combining Schemas

### allOf

The data must validate against ALL schemas (AND logic).

```json
{
  "allOf": [
    { "type": "string" },
    { "minLength": 3 }
  ]
}
```

**Key points:**
- All schemas must pass validation
- Use when you need multiple constraints on the same data
- Errors include `allOf[index]` prefix for identification

### oneOf

```json
{
  "oneOf": [
    { "type": "integer", "minimum": 0 },
    { "type": "string", "enum": ["unknown"] }
  ]
}
```

### anyOf

The data must validate against AT LEAST ONE schema (OR logic).

```json
{
  "anyOf": [
    { "type": "string" },
    { "type": "integer" }
  ]
}
```

**Key points:**
- At least one schema must pass validation
- Use when you accept multiple valid formats
- If no schema matches, collects errors from all schemas
- Includes summary error with count of matched schemas

### not

```json
{
  "not": { "type": "null" }
}
```

> **Note:** For detailed composition examples, see [Schema Composition](schema-composition.md)

---

## Examples

### Complete Example: Player

```json
{
  "type": "object",
  "properties": {
    "username": {
      "type": "string",
      "minLength": 3,
      "maxLength": 16,
      "pattern": "^[a-zA-Z][a-zA-Z0-9_]*$"
    },
    "level": {
      "type": "integer",
      "minimum": 1,
      "maximum": 100
    },
    "class": {
      "type": "string",
      "enum": ["warrior", "mage", "archer", "rogue"]
    },
    "stats": {
      "type": "object",
      "properties": {
        "strength": { "type": "integer", "minimum": 0, "maximum": 100 },
        "agility": { "type": "integer", "minimum": 0, "maximum": 100 },
        "intelligence": { "type": "integer", "minimum": 0, "maximum": 100 }
      },
      "required": ["strength", "agility", "intelligence"]
    },
    "inventory": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "slot": { "type": "integer", "minimum": 0, "maximum": 35 },
          "item": { "type": "string" },
          "amount": { "type": "integer", "minimum": 1, "maximum": 64 }
        },
        "required": ["slot", "item", "amount"]
      }
    }
  },
  "required": ["username", "level", "class"]
}
```

---

## Unsupported Keywords

The following keywords are not supported in this version:
- `$ref` (external references)
- `$id` (definitions)
- `definitions` (internal definitions)
- `const` (constant value)

---

## Useful Links

- [JSON Schema Official](https://json-schema.org/)
- [Regex Tester](https://regexr.com/)

---

[← Back](../README.md) | [Previous: Skript Syntax](skript-syntax.md) | [Next: Data Types →](data-types.md)
