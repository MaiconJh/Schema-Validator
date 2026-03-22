---
title: Examples and schema construction
description: Complete reference patterns for constructing schemas with all supported JSON Schema features.
doc_type: reference
order: 6
sequence: 15
permalink: /examples-and-schema-construction.html
---

## Schema Model Summary

Core schema structure is represented by `Schema` and parsed by `FileSchemaLoader`.

### Supported Keyword Groups

- **Type and shape**: `type`, `properties`, `patternProperties`, `items`, `prefixItems`, `required`, `additionalProperties`
- **Primitive constraints**: `minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`, `minLength`, `maxLength`, `pattern`, `format`, `enum`, `const`
- **Array constraints**: `minItems`, `maxItems`, `uniqueItems`, `items`, `prefixItems`, `additionalItems`
- **Object constraints**: `minProperties`, `maxProperties`, `dependencies`, `dependentRequired`, `dependentSchemas`
- **Composition**: `allOf`, `anyOf`, `oneOf`, `not`
- **Conditional**: `if`, `then`, `else`
- **Reference**: `$ref`, `definitions`, `$defs`
- **Metadata**: `$schema`, `$id`, `title`, `description`

---

## Construction Pattern: Strict Object Contract

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

---

## Construction Pattern: Dictionary/Map Object

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

---

## Construction Pattern: Typed Homogeneous Array

```json
{
  "type": "array",
  "items": {
    "type": "integer",
    "minimum": 0
  }
}
```

---

## Construction Pattern: Array with Constraints

All array constraints are now fully enforced:

```json
{
  "type": "array",
  "minItems": 1,
  "maxItems": 10,
  "uniqueItems": true,
  "items": {
    "type": "object",
    "properties": {
      "id": {"type": "string"},
      "name": {"type": "string"}
    }
  }
}
```

### Tuple Validation with prefixItems (2019-09/2020-12)

```json
{
  "type": "array",
  "prefixItems": [
    {"type": "string"},
    {"type": "integer"},
    {"type": "boolean"}
  ],
  "items": false
}
```

This validates that:
- First element must be a string
- Second element must be an integer
- Third element must be a boolean
- No additional items allowed (due to `items: false`)

---

## Construction Pattern: Object with Property Count Constraints

```json
{
  "type": "object",
  "minProperties": 1,
  "maxProperties": 10,
  "properties": {
    "name": {"type": "string"},
    "email": {"type": "string", "format": "email"}
  }
}
```

---

## Construction Pattern: Conditional Rules

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

---

## Construction Pattern: Dependencies

### Property Dependencies

```json
{
  "type": "object",
  "properties": {
    "name": {"type": "string"},
    "credit_card": {"type": "string"}
  },
  "dependencies": {
    "credit_card": ["billing_address"]
  }
}
```

When `credit_card` is present, `billing_address` becomes required.

### Schema Dependencies

```json
{
  "type": "object",
  "properties": {
    "product": {"type": "string"},
    "quantity": {"type": "integer"}
  },
  "dependencies": {
    "quantity": {
      "properties": {
        "quantity": {"minimum": 1}
      },
      "required": ["product"]
    }
  }
}
```

---

## Construction Pattern: dependentRequired (2019-09+)

```json
{
  "type": "object",
  "properties": {
    "name": {"type": "string"},
    "email": {"type": "string"},
    "phone": {"type": "string"}
  },
  "dependentRequired": {
    "email": ["name"],
    "phone": ["name"]
  }
}
```

If `email` or `phone` is present, `name` becomes required.

---

## Construction Pattern: dependentSchemas (2019-09+)

```json
{
  "type": "object",
  "properties": {
    "shipping": {"type": "object"},
    "billing": {"type": "object"}
  },
  "dependentSchemas": {
    "billing": {
      "properties": {
        "billing": {},
        "billing_address": {"type": "string"}
      },
      "required": ["billing_address"]
    }
  }
}
```

If `billing` is present, additional schema constraints are applied.

---

## Construction Pattern: additionalProperties as Schema

```json
{
  "type": "object",
  "properties": {
    "name": {"type": "string"}
  },
  "additionalProperties": {"type": "string"}
}
```

This allows any additional properties as long as they are strings.

---

## Construction Pattern: Exclusive Minimum/Maximum

### Modern Numeric Form (2019-09/2020-12)

```json
{
  "type": "number",
  "exclusiveMinimum": 0,
  "exclusiveMaximum": 100
}
```

This means: `value > 0 AND value < 100`

### Legacy Boolean Form (Draft-04/06/07)

```json
{
  "type": "number",
  "minimum": 0,
  "maximum": 100,
  "exclusiveMinimum": true,
  "exclusiveMaximum": true
}
```

Both forms are supported for compatibility.

---

## Construction Pattern: Type Arrays (Nullable Types)

```json
{
  "type": ["string", "null"],
  "description": "A string that can also be null"
}
```

This accepts either a string or null value.

### Multiple Types

```json
{
  "type": ["string", "number", "integer"],
  "description": "Any numeric or string value"
}
```

---

## Construction Pattern: $ref with JSON Pointer

### Local Reference with definitions

```json
{
  "definitions": {
    "Address": {
      "type": "object",
      "properties": {
        "street": {"type": "string"},
        "city": {"type": "string"}
      }
    }
  },
  "type": "object",
  "properties": {
    "billing_address": {"$ref": "#/definitions/Address"},
    "shipping_address": {"$ref": "#/definitions/Address"}
  }
}
```

### Using $defs (2019-09/2020-12)

```json
{
  "$defs": {
    "Person": {
      "type": "object",
      "properties": {
        "name": {"type": "string"},
        "age": {"type": "integer", "minimum": 0}
      }
    }
  },
  "type": "object",
  "properties": {
    "author": {"$ref": "#/$defs/Person"},
    "reviewer": {"$ref": "#/$defs/Person"}
  }
}
```

### Escaping in JSON Pointer

```json
{
  "$defs": {
    "foo~bar": {
      "type": "object"
    },
    "baz/qux": {
      "type": "object"
    }
  },
  "properties": {
    "field1": {"$ref": "#/$defs/foo~0bar"},
    "field2": {"$ref": "#/$defs/baz~1qux"}
  }
}
```

- `~0` represents `~`
- `~1` represents `/`

### Reference to allOf/anyOf/oneOf Items

```json
{
  "type": "object",
  "allOf": [
    {"$ref": "#/definitions/BaseEntity"},
    {"properties": {
      "extra": {"type": "string"}
    }}
  ],
  "definitions": {
    "BaseEntity": {
      "type": "object",
      "properties": {
        "id": {"type": "string"}
      }
    }
  }
}
```

### External Reference with $id

```json
{
  "$id": "https://example.com/schemas/person.json",
  "type": "object",
  "properties": {
    "name": {"type": "string"}
  }
}
```

---

## Construction Pattern: Metadata

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "https://example.com/schemas/user.json",
  "title": "User Profile",
  "description": "Schema for user profile validation",
  "type": "object",
  "properties": {
    "username": {
      "type": "string",
      "title": "Username",
      "description": "The user's unique identifier"
    },
    "email": {
      "type": "string",
      "format": "email"
    }
  },
  "required": ["username", "email"]
}
```

---

## Related Pages

- Keyword matrix: [Schema keywords](schema-keywords.html)
- Runtime rule order: [Validation behavior](validation-behavior.html)
- Practical examples: [Examples](examples.html)
