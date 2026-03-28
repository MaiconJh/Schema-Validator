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

> [!NOTE]
> For complete details on each keyword's behavior, refer to the [Schema keywords](schema-keywords.html) reference page.

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

**Use when**: You need to enforce a fixed object structure where only specific properties are allowed.

**Details**:
- `type: "object"` specifies that the data must be a JSON object
- `required` array lists properties that must be present in the object
- `additionalProperties: false` prevents any properties not explicitly defined in `properties`
- Each property in `properties` defines the schema for that specific property

**Best Practices**:
- Always pair `additionalProperties: false` with `required` when you want a strict contract
- Consider using descriptive titles and descriptions for better documentation
- For optional properties, omit them from the `required` array

**See Also**: [Object constraints](schema-keywords.html#object-constraints) for more property-related keywords

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

**Use when**: You need to accept objects with dynamic keys that follow a specific naming pattern, while rejecting unknown keys.

**Details**:
- `type: "object"` specifies that the data must be a JSON object
- `patternProperties` defines a regex pattern that keys must match to be valid
- Each matching key's value must conform to the specified schema (in this case, a string)
- `additionalProperties: false` prevents any keys that don't match the pattern from being accepted

**Best Practices**:
- Test your regex pattern thoroughly to ensure it matches all valid keys and rejects invalid ones
- Consider using more specific patterns when possible (e.g., `^[a-z][a-z0-9_]*$` for identifiers that must start with a letter)
- For cases where you want to allow additional properties, set `additionalProperties: true` or provide a schema for them

**See Also**: [Pattern Properties](schema-keywords.html#patternproperties) and [Additional Properties](schema-keywords.html#additionalproperties)

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

**Use when**: You need to validate arrays where all elements must conform to the same type and constraints.

**Details**:
- `type: "array"` specifies that the data must be a JSON array
- `items` defines the schema that all array elements must validate against
- In this example, all elements must be integers with a minimum value of 0

**Best Practices**:
- For complex element schemas, consider extracting them to definitions/$defs for reuse
- Remember that `items` applies to all elements when `prefixItems` is not defined
- When you need different validation for different positions, consider `prefixItems` (tuple validation) instead

**See Also**: [Array constraints](schema-keywords.html#array-constraints) for more array-related keywords

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

**Use when**: You need to validate arrays with specific size, uniqueness, and element constraints.

**Details**:
- `type: "array"` specifies that the data must be a JSON array
- `minItems: 1` requires at least one element in the array
- `maxItems: 10` limits the array to a maximum of 10 elements
- `uniqueItems: true` ensures all elements in the array are unique
- `items` defines the schema that all array elements must validate against
- In this example, each element must be an object with "id" and "name" string properties

**Best Practices**:
- Consider whether `uniqueItems` is necessary for your use case, as it can impact performance for large arrays
- When you need different validation for different array positions, consider using `prefixItems` instead of or in addition to `items`
- For arrays of objects, consider adding `required` fields to the item schema if certain properties are mandatory

**See Also**: 
- [Size constraints](schema-keywords.html#size-constraints) for `minItems` and `maxItems`
- [Uniqueness constraint](schema-keywords.html#uniqueitems) for `uniqueItems`
- [Items validation](schema-keywords.html#items) for the `items` keyword

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

**Use when**: You need to validate arrays where specific positions have specific types (tuple validation), commonly used for fixed-length arrays with position-dependent types.

**Details**:
- `type: "array"` specifies that the data must be a JSON array
- `prefixItems` defines schemas for the first N elements of the array (positions 0, 1, 2 in this example)
- `items: false` means no additional elements are allowed beyond the prefixItems
- Position 0 must be a string, position 1 must be an integer, position 2 must be a boolean

**How it works**:
- The array must have at least as many elements as there are items in the `prefixItems` array
- Each element at position N must validate against the schema at `prefixItems[N]`
- When `items` is set to `false`, no additional elements beyond the prefixItems are allowed
- When `items` is omitted or set to a schema, additional elements must validate against that schema

**Best Practices**:
- Use `prefixItems` for fixed-length arrays where position matters (like [name, age, isActive])
- When you need variable-length arrays with consistent element types, use `items` instead
- Consider using `additionalItems` (when combined with prefixItems) to define a schema for extra elements
- For complex tuple schemas, consider extracting them to definitions/$defs for reuse

**See Also**: 
- [Prefix Items](schema-keywords.html#prefixitems) for tuple validation details
- [Items](schema-keywords.html#items) for continued array validation
- [Additional Items](schema-keywords.html#additionalitems) for validating elements beyond prefixItems

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

**Use when**: You need to constrain the number of properties in an object while defining specific properties.

**Details**:
- `type: "object"` specifies that the data must be a JSON object
- `minProperties: 1` requires at least one property to be present
- `maxProperties: 10` limits the object to a maximum of 10 properties
- `properties` defines the schema for specific named properties
- In this example, the object must have between 1 and 10 properties, with "name" as a string and "email" as a string with email format

**Best Practices**:
- Consider whether both min and max constraints are necessary for your use case
- When you only need a minimum, omit `maxProperties`; when you only need a maximum, omit `minProperties`
- Remember that these constraints count all properties, including those not defined in `properties` (unless `additionalProperties: false` is set)
- For objects where you want to allow additional properties while constraining defined ones, omit `additionalProperties: false` or set it to `true`

**See Also**: 
- [Object constraints](schema-keywords.html#object-constraints) for more object-related keywords
- [Properties](schema-keywords.html#properties) for defining specific property schemas
- [Additional Properties](schema-keywords.html#additionalproperties) for controlling undeclared properties

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

**Use when**: You need to apply different validation rules based on the values of other properties in the same object.

**Details**:
- `type: "object"` specifies that the data must be a JSON object
- `properties` defines the schema for the object's properties
- `if` specifies a condition that, when met, triggers the `then` schema
- `then` defines the schema to validate against when the `if` condition is true
- `else` defines the schema to validate against when the `if` condition is false
- In this example:
  - If `mode` is "strict", then `limit` must be at least 10
  - If `mode` is not "strict" (i.e., "loose"), then `limit` must be at least 0
  - The `mode` property must be either "strict" or "loose" due to the enum constraint
  - The `limit` property must be an integer

**How it works**:
- The validator first evaluates the `if` schema against the data
- If the `if` validation succeeds (no errors), it validates against the `then` schema
- If the `if` validation fails, it validates against the `else` schema
- Only one of `then` or `else` is applied, not both

**Best Practices**:
- Keep conditional logic simple and easy to understand
- Consider using `dependentRequired` or `dependentSchemas` for simpler property-based conditions
- For complex conditions, consider breaking them into multiple steps or using custom logic outside of JSON Schema
- Test all branches of your conditional logic to ensure they work as expected

**See Also**: 
- [Conditional](schema-keywords.html#conditional) for more details on if/then/else
- [Enum](schema-keywords.html#enum) for restricting property values
- [Dependencies](schema-keywords.html#dependencies) for alternative ways to express property relationships

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

**Use when**: You need to make certain properties required based on the presence of other properties in the same object.

**Details**:
- `type: "object"` specifies that the data must be a JSON object
- `properties` defines the schema for the object's properties
- `dependencies` specifies that when certain properties are present, other properties become required
- In this example, if the `credit_card` property is present, then the `billing_address` property becomes required
- Note: The `dependencies` keyword here refers to the "dependencies" from older JSON Schema drafts (Draft 04/06/07), which is different from `dependentRequired` and `dependentSchemas`

**How it works**:
- When validating an object, if the left-side property (e.g., "credit_card") is present in the data
- Then all properties listed in the right-side array (e.g., ["billing_address"]) must also be present in the data
- If any required dependency is missing, validation fails

**Best Practices**:
- Consider using `dependentRequired` (JSON Schema Draft 2019-09+) for a more explicit and modern approach
- Use `dependencies` when you need compatibility with older JSON Schema implementations
- Clearly document which properties depend on which others for maintainability

**See Also**: 
- [Dependencies](schema-keywords.html#dependencies) for the traditional dependencies keyword
- [Dependent Required](schema-keywords.html#dependentrequired) for the modern alternative
- [Dependent Schemas](schema-keywords.html#dependentschemas) for schema-based dependencies

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

**Use when**: You need to apply complex schema constraints based on the presence of a property, combining both property requirements and value constraints.

**Details**:
- `type: "object"` specifies that the data must be a JSON object
- `properties` defines the schema for the object's properties
- `dependencies` specifies that when the left-side property is present, the right-side schema constraints apply
- In this example, if the `quantity` property is present:
  - The `quantity` property itself must have a minimum value of 1
  - The `product` property becomes required
- Note: This uses the older "dependencies" keyword syntax (Draft 04/06/07) which allows schema constraints

**How it works**:
- When validating an object, if the left-side property (e.g., "quantity") is present in the data
- Then the schema specified on the right-side is applied to the entire data object
- In this case, it adds both a constraint on "quantity" (minimum 1) and makes "product" required

**Best Practices**:
- Consider using `dependentSchemas` (JSON Schema Draft 2019-09+) for a more explicit and modern approach
- Use the traditional `dependencies` keyword when you need compatibility with older JSON Schema implementations
- Clearly document the implied constraints for maintainability

**See Also**: 
- [Dependencies](schema-keywords.html#dependencies) for the traditional dependencies keyword
- [Dependent Required](schema-keywords.html#dependentrequired) for property-based dependencies
- [Dependent Schemas](schema-keywords.html#dependentschemas) for the modern schema-based alternative

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

**Use when**: You need to make certain properties required based on the presence of other properties, using the modern JSON Schema approach.

**Details**:
- `type: "object"` specifies that the data must be a JSON object
- `properties` defines the schema for the object's properties
- `dependentRequired` specifies that when the left-side property is present, the right-side properties become required
- In this example:
  - If the `email` property is present, then the `name` property becomes required
  - If the `phone` property is present, then the `name` property becomes required
- Note: This is the modern replacement for the traditional `dependencies` keyword, introduced in JSON Schema Draft 2019-09

**How it works**:
- When validating an object, for each property listed in `dependentRequired`:
  - If that property is present in the data
  - Then all properties listed in its array value must also be present in the data
- Unlike the traditional `dependencies` keyword, `dependentRequired` only affects requiredness, not other schema constraints

**Best Practices**:
- Prefer `dependentRequired` over the traditional `dependencies` keyword for modern JSON Schema usage
- Use this when you only need to affect requiredness, not other schema aspects of dependent properties
- For cases where you need to apply schema constraints (not just requiredness), consider `dependentSchemas`

**See Also**: 
- [Dependent Required](schema-keywords.html#dependentrequired) for more details
- [Dependent Schemas](schema-keywords.html#dependentschemas) for schema-based dependencies
- [Dependencies](schema-keywords.html#dependencies) for the traditional alternative

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

**Use when**: You need to apply additional schema constraints to the entire object based on the presence of a specific property.

**Details**:
- `type: "object"` specifies that the data must be a JSON object
- `properties` defines the schema for the object's properties
- `dependentSchemas` specifies that when the left-side property is present, the right-side schema applies to the entire data object
- In this example, if the `billing` property is present:
  - The `billing` property itself must be an object (can be empty)
  - The `billing_address` property becomes required and must be a string
- Note: This is the modern replacement for schema-based dependencies in the traditional `dependencies` keyword, introduced in JSON Schema Draft 2019-09

**How it works**:
- When validating an object, for each property listed in `dependentSchemas`:
  - If that property is present in the data
  - Then the schema specified as its value is applied to the entire data object
- This allows you to conditionally apply complex validation rules based on the presence of certain properties

**Best Practices**:
- Prefer `dependentSchemas` over the traditional `dependencies` keyword for schema-based dependencies in modern JSON Schema
- Use this when you need to apply constraints to the whole object based on a property's presence
- For cases where you only need to affect requiredness (not other schema aspects), consider `dependentRequired`

**See Also**: 
- [Dependent Schemas](schema-keywords.html#dependentschemas) for more details
- [Dependent Required](schema-keywords.html#dependentrequired) for property-based requiredness
- [Dependencies](schema-keywords.html#dependencies) for the traditional alternative

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

**Use when**: You need to allow additional properties beyond those explicitly defined, but want to constrain their type or schema.

**Details**:
- `type: "object"` specifies that the data must be a JSON object
- `properties` defines the schema for specific named properties
- `additionalProperties` defines the schema that any undeclared properties must validate against
- In this example:
  - The "name" property is explicitly defined and must be a string
  - Any other properties (not named "name") must also be strings
  - Properties like "id", "title", "description" would be valid as long as they are strings

**How it works**:
- During validation, each property in the object is checked:
  - If the property name exists in `properties`, it's validated against that schema
  - If the property name does not exist in `properties`, it's validated against the `additionalProperties` schema
- This allows you to be specific about known properties while still allowing flexibility for additional ones

**Best Practices**:
- Consider whether you want to allow additional properties at all - sometimes `additionalProperties: false` is safer
- When allowing additional properties, think about what type or schema makes sense for them
- For complex additional property schemas, consider extracting them to definitions/$defs for reuse
- Remember that `additionalProperties` can be either a boolean or a schema:
  - `true`: allows any additional properties (no validation)
  - `false`: prohibits any additional properties
  - `{}`: allows any additional properties (empty object schema)
  - `{"type": "string"}`: allows additional properties only if they are strings

**See Also**: 
- [Additional Properties](schema-keywords.html#additionalproperties) for more details
- [Properties](schema-keywords.html#properties) for defining specific property schemas

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

**Use when**: You need to set strict bounds where the limit values themselves are not allowed.

**Details**:
- `type: "number"` specifies that the data must be a JSON number (integer or floating-point)
- `exclusiveMinimum: 0` means the value must be strictly greater than 0 (not equal to 0)
- `exclusiveMaximum: 100` means the value must be strictly less than 100 (not equal to 100)
- Together, they constrain the value to the range (0, 100)

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

**Use when**: You need to use the older boolean form of exclusive constraints for compatibility with legacy JSON Schema validators.

**Details**:
- `type: "number"` specifies that the data must be a JSON number
- `minimum: 0` and `maximum: 100` set the inclusive bounds [0, 100]
- `exclusiveMinimum: true` inverts the minimum to be exclusive (value > 0)
- `exclusiveMaximum: true` inverts the maximum to be exclusive (value < 100)
- Together, they constrain the value to the range (0, 100)

**Compatibility Note**:
Both forms are supported and produce identical validation results. The modern numeric form (Direct numbers) is preferred in newer JSON Schema drafts (2019-09+), while the legacy boolean form maintains compatibility with older implementations.

**Best Practices**:
- Use the modern numeric form for new schemas targeting JSON Schema 2019-09 or later
- Consider adding both forms if you need to support very old JSON Schema validators
- Remember that exclusive constraints cannot be used with `minimum`/`maximum` set to null in the legacy form
- For inclusive bounds, use `minimum` and `maximum` without the exclusive flags

**See Also**: 
- [Numeric constraints](schema-keywords.html#numeric-constraints) for more details on minimum/maximum
- [Exclusive minimum](schema-keywords.html#exclusiveminimum) and [Exclusive maximum](schema-keywords.html#exclusivemaximum) reference pages

---

## Construction Pattern: Type Arrays (Nullable Types)

```json
{
  "type": ["string", "null"],
  "description": "A string that can also be null"
}
```

**Use when**: You need to allow a value to be one of multiple types, commonly used for nullable fields or fields that can accept different but related types.

**Details**:
- `type: ["string", "null"]` specifies that the data must be either a string or a null value
- JSON Schema allows arrays of types to create unions
- The order of types in the array does not matter
- At least one type in the array must match for validation to pass
- Null is a distinct type in JSON Schema, separate from "string", "number", etc.

**How it works**:
- During validation, the validator checks if the data matches ANY of the types in the array
- If at least one type matches, validation passes
- If none of the types match, validation fails

**Best Practices**:
- Use type arrays for nullable fields (combining a type with "null")
- Consider whether you really need multiple types - sometimes a more specific type is better
- Be aware that `null` is a distinct type - it's not the same as an empty string or zero
- For complex union types, consider using `oneOf` or `anyOf` instead for more flexibility
- Document why multiple types are needed for maintainability

**See Also**: 
- [Type](schema-keywords.html#type) for more details on the type keyword
- [Null type](schema-keywords.html#null-type) for information about null validation
- [OneOf](schema-keywords.html#oneof) and [AnyOf](schema-keywords.html#anyof) for alternative ways to express type unions

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
