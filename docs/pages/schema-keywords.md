---
title: Schema keywords
description: Reference of parsed and enforced keyword behavior in the current implementation.
doc_type: reference
order: 1
sequence: 10
permalink: /schema-keywords.html
---

## How to read this page

Schema handling has two phases:

1. **Parse phase** (`FileSchemaLoader`): keywords are recognized and mapped to `Schema` fields.
2. **Validation phase** (`ObjectValidator`, `ArrayValidator`, `PrimitiveValidator`): only some parsed fields are enforced.

A keyword can be accepted at parse time but still not enforced at runtime.

## Enforced keywords (runtime)

### Type and structure

- `type` (including type arrays like `["string", "null"]`)
- `properties`
- `patternProperties`
- `required`
- `additionalProperties` (boolean and schema forms)
- `propertyNames`
- `unevaluatedProperties`
- `items`
- `prefixItems` (2019-09/2020-12 tuple validation)
- `contains`, `minContains`, `maxContains`
- `unevaluatedItems`

### Primitive constraints

- `enum`
- `const` (including `const: null`)
- `minimum`, `maximum`
- `exclusiveMinimum`, `exclusiveMaximum` (numeric form per 2019-09/2020-12 + boolean legacy)
- `multipleOf`
- `minLength`, `maxLength`
- `pattern`
- `format`
- `contentEncoding`, `contentMediaType`, `contentSchema`

### Composition and conditional

- `allOf`
- `anyOf`
- `oneOf`
- `not`
- `if`, `then`, `else`

## Fully Implemented Keywords

All keywords below are now fully enforced at runtime and verified by 373 unit tests across 23 validator classes.

### Array Keywords
- `minItems` — Minimum array length
- `maxItems` — Maximum array length
- `uniqueItems` — Uniqueness constraint
- `prefixItems` — Tuple validation (2019-09+)
- `items` — Schema for array elements
- `contains` / `minContains` / `maxContains` — Match-count constraints
- `unevaluatedItems` — Post-evaluation array-item constraints
- `additionalItems` — Limited support (applies only when `prefixItems` is defined; standard `items` keyword behavior is fully supported)

### Object Keywords
- `minProperties` — Minimum property count
- `maxProperties` — Maximum property count
- `dependencies` — Property and schema dependencies
- `dependentRequired` — Required properties based on presence (2019-09+)
- `dependentSchemas` — Schema constraints based on presence (2019-09+)
- `propertyNames` — Property-name schema validation
- `unevaluatedProperties` — Post-evaluation object-property constraints

### Reference Keywords
- `$ref` — JSON Pointer reference resolution
- `$dynamicRef` / `$dynamicAnchor` — Dynamic reference and anchor support
- `$defs` / `definitions` — Schema definitions (`definitions` as legacy alias; `$defs` is the Draft 2020-12+ preferred form)
- `$id` — Base URI for reference resolution
- `$schema` — Schema dialect identification

### Metadata Keywords
- `title` — Schema title
- `description` — Schema description
- `default` — Default value
- `examples` — Example values
- `readOnly` / `writeOnly` — Property constraints
- `deprecated` — Deprecation status
- `contentEncoding` / `contentMediaType` / `contentSchema` — Content vocabulary constraints
- `$comment` / `comment` — Annotations (`comment` as legacy alias)

## Numeric boundary semantics (Draft 2020-12)

- `exclusiveMinimum` and `exclusiveMaximum` numeric values are treated as strict bounds.
- Legacy boolean exclusives are still accepted for compatibility with older schemas.
- When numeric `exclusiveMinimum` / `exclusiveMaximum` is present, it takes precedence over legacy boolean behavior tied to `minimum` / `maximum`.

## `const` semantics

- `const` is treated as present even when the value is `null` (`const: null`), matching JSON Schema semantics.

## Unsupported keyword handling

Unknown keywords are handled by `FileSchemaLoader.detectUnsupportedKeywords()`:

- `strict-mode: false` -> warning log, schema still loads
- `strict-mode: true` -> throws exception and aborts loading

## Reference behavior (`$ref`)

- Full JSON Pointer resolution with navigation by:
  - Keywords (`properties`, `items`, `additionalProperties`)
  - Object keys (`properties/name`)
  - Array indices (`prefixItems/0`, `allOf/1`)
- Support for `definitions` and `$defs` sections
- Escaping support for `~0` (represents `~`) and `~1` (represents `/`)
- `$id`-based indexing for external reference resolution

---

## Keyword Examples

### Type and structure

#### type

```json
{
  "type": "string"
}
```

Validates that the value is a string.

#### properties

```json
{
  "type": "object",
  "properties": {
    "name": {"type": "string"},
    "age": {"type": "integer"}
  }
}
```

Defines schemas for specific object properties.

#### patternProperties

```json
{
  "type": "object",
  "patternProperties": {
    "^S_": {"type": "string"},
    "^I_": {"type": "integer"}
  }
}
```

Defines schemas for properties matching regex patterns.

#### required

```json
{
  "type": "object",
  "properties": {
    "name": {"type": "string"},
    "email": {"type": "string"}
  },
  "required": ["name", "email"]
}
```

Specifies which properties must be present.

#### additionalProperties

```json
{
  "type": "object",
  "properties": {
    "name": {"type": "string"}
  },
  "additionalProperties": false
}
```

Controls whether additional properties are allowed.

#### propertyNames

```json
{
  "type": "object",
  "propertyNames": {
    "pattern": "^[A-Z][a-z]+$"
  }
}
```

Validates property names against a schema.

#### items

```json
{
  "type": "array",
  "items": {
    "type": "integer",
    "minimum": 0
  }
}
```

Defines schema for all array elements.

#### prefixItems

```json
{
  "type": "array",
  "prefixItems": [
    {"type": "string"},
    {"type": "integer"}
  ],
  "items": false
}
```

Defines schemas for specific array positions (tuple validation).

#### contains / minContains / maxContains

```json
{
  "type": "array",
  "contains": {"type": "integer"},
  "minContains": 1,
  "maxContains": 3
}
```

Requires at least one element to match the contains schema.

### Primitive constraints

#### enum

```json
{
  "type": "string",
  "enum": ["active", "inactive", "pending"]
}
```

Restricts value to a specific set.

#### const

```json
{
  "type": "string",
  "const": "fixed-value"
}
```

Requires exact value match.

#### minimum / maximum

```json
{
  "type": "number",
  "minimum": 0,
  "maximum": 100
}
```

Sets inclusive numeric bounds.

#### exclusiveMinimum / exclusiveMaximum

```json
{
  "type": "number",
  "exclusiveMinimum": 0,
  "exclusiveMaximum": 100
}
```

Sets exclusive numeric bounds (value must be greater than 0 and less than 100).

#### multipleOf

```json
{
  "type": "number",
  "multipleOf": 0.5
}
```

Requires value to be a multiple of specified number.

#### minLength / maxLength

```json
{
  "type": "string",
  "minLength": 3,
  "maxLength": 50
}
```

Constrains string length.

#### pattern

```json
{
  "type": "string",
  "pattern": "^[A-Z][a-z]+$"
}
```

Validates string against regex pattern.

#### format

```json
{
  "type": "string",
  "format": "email"
}
```

Validates string format (email, date, uri, etc.).

### Composition and conditional

#### allOf

```json
{
  "allOf": [
    {"type": "object"},
    {"properties": {"name": {"type": "string"}}}
  ]
}
```

Value must validate against ALL schemas.

#### anyOf

```json
{
  "anyOf": [
    {"type": "string"},
    {"type": "integer"}
  ]
}
```

Value must validate against AT LEAST ONE schema.

#### oneOf

```json
{
  "oneOf": [
    {"type": "string"},
    {"type": "integer"}
  ]
}
```

Value must validate against EXACTLY ONE schema.

#### not

```json
{
  "not": {"type": "string"}
}
```

Value must NOT validate against the schema.

#### if / then / else

```json
{
  "type": "object",
  "properties": {
    "type": {"enum": ["user", "admin"]}
  },
  "if": {"properties": {"type": {"const": "admin"}}},
  "then": {"properties": {"permissions": {"type": "array"}}},
  "else": {"properties": {"permissions": {"type": "null"}}}
}
```

Applies conditional validation based on if/then/else logic.

### Object Keywords

#### minProperties / maxProperties

```json
{
  "type": "object",
  "minProperties": 1,
  "maxProperties": 10
}
```

Constrains number of object properties.

#### dependencies

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

Makes properties required based on presence of other properties.

#### dependentRequired

```json
{
  "type": "object",
  "properties": {
    "name": {"type": "string"},
    "credit_card": {"type": "string"}
  },
  "dependentRequired": {
    "credit_card": ["billing_address"]
  }
}
```

Modern replacement for dependencies (2019-09+).

#### dependentSchemas

```json
{
  "type": "object",
  "properties": {
    "name": {"type": "string"}
  },
  "dependentSchemas": {
    "name": {
      "properties": {
        "first": {"type": "string"},
        "last": {"type": "string"}
      },
      "required": ["first", "last"]
    }
  }
}
```

Applies schema constraints based on property presence (2019-09+).

### Reference Keywords

#### $ref

```json
{
  "$defs": {
    "address": {
      "type": "object",
      "properties": {
        "street": {"type": "string"}
      }
    }
  },
  "type": "object",
  "properties": {
    "billing": {"$ref": "#/$defs/address"}
  }
}
```

References another schema definition.

#### $defs / definitions

```json
{
  "$defs": {
    "person": {
      "type": "object",
      "properties": {
        "name": {"type": "string"}
      }
    }
  }
}
```

Defines reusable schema components.

### Metadata Keywords

#### title / description

```json
{
  "title": "User Profile",
  "description": "Schema for user profile validation",
  "type": "object"
}
```

Provides human-readable documentation.

#### default

```json
{
  "type": "object",
  "properties": {
    "status": {
      "type": "string",
      "default": "active"
    }
  }
}
```

Specifies default value if property is missing.

#### examples

```json
{
  "type": "object",
  "properties": {
    "name": {
      "type": "string",
      "examples": ["John", "Jane"]
    }
  }
}
```

Provides example values for documentation.

#### readOnly / writeOnly

```json
{
  "type": "object",
  "properties": {
    "id": {
      "type": "string",
      "readOnly": true
    },
    "password": {
      "type": "string",
      "writeOnly": true
    }
  }
}
```

Marks properties as read-only or write-only.

#### deprecated

```json
{
  "type": "object",
  "properties": {
    "oldField": {
      "type": "string",
      "deprecated": true
    }
  }
}
```

Marks property as deprecated.

## Source mapping

- Parse and unsupported detection: `FileSchemaLoader.java`
- Supported keyword registry: `SupportedKeywordsRegistry.java`
- Runtime enforcement: `ObjectValidator.java`, `ArrayValidator.java`, `PrimitiveValidator.java`, `FormatValidator.java`

## Related pages

- Runtime execution order: [Validation behavior](validation-behavior.html)
- Skript-facing constraints: [Skript API](skript-api.html)
- Supported format catalog: [Format reference](format-reference.html)
