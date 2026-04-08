---
title: Examples and schema construction
description: Complete reference patterns for constructing schemas with all supported JSON Schema features.
doc_type: reference
order: 8
sequence: 17
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

## Construction Pattern: multipleOf

```json
{
  "type": "number",
  "multipleOf": 0.5
}
```

**Use when**: You need to constrain a numeric value to be a multiple of a specific number, commonly used for currency, measurements, or step-based values.

**Details**:
- `type: "number"` specifies that the data must be a JSON number
- `multipleOf: 0.5` means the value must be divisible by 0.5 (e.g., 0, 0.5, 1.0, 1.5, 2.0, etc.)
- Works with both integers and floating-point numbers
- The divisor must be a positive number

**How it works**:
- During validation, the validator checks if `value % multipleOf === 0`
- For floating-point numbers, this uses precise floating-point arithmetic
- If the remainder is zero, validation passes

**Best Practices**:
- Use `multipleOf` for values that must follow a specific step pattern
- For currency, consider using `multipleOf: 0.01` for cent-based precision
- For measurements, use appropriate multiples (e.g., `multipleOf: 0.5` for half-unit increments)
- Be aware of floating-point precision issues with very small or very large multiples

**See Also**: 
- [Numeric constraints](schema-keywords.html#numeric-constraints) for more details
- [Multiple of](schema-keywords.html#multipleof) reference page

---

## Construction Pattern: String Length Constraints

```json
{
  "type": "string",
  "minLength": 3,
  "maxLength": 50
}
```

**Use when**: You need to constrain the length of a string value, commonly used for usernames, passwords, or text fields.

**Details**:
- `type: "string"` specifies that the data must be a JSON string
- `minLength: 3` requires the string to have at least 3 characters
- `maxLength: 50` limits the string to a maximum of 50 characters
- Length is measured in Unicode code points (characters)

**How it works**:
- During validation, the validator checks the string's length property
- Empty strings have length 0
- Each character counts as 1, regardless of being multi-byte

**Best Practices**:
- Always set reasonable minimum and maximum lengths for string fields
- Consider user experience when setting minimum lengths (e.g., usernames should be at least 3 characters)
- Set maximum lengths to prevent excessively long inputs
- For passwords, consider both minimum length and complexity requirements

**See Also**: 
- [String constraints](schema-keywords.html#string-constraints) for more details
- [Min length](schema-keywords.html#minlength) and [Max length](schema-keywords.html#maxlength) reference pages

---

## Construction Pattern: String Pattern (Regex)

```json
{
  "type": "string",
  "pattern": "^[A-Z][a-z]+$"
}
```

**Use when**: You need to validate that a string matches a specific regular expression pattern, commonly used for identifiers, codes, or formatted text.

**Details**:
- `type: "string"` specifies that the data must be a JSON string
- `pattern: "^[A-Z][a-z]+$"` defines a regex pattern that the string must match
- In this example, the string must start with an uppercase letter followed by one or more lowercase letters
- The pattern uses JavaScript regex syntax

**How it works**:
- During validation, the validator tests the string against the regex pattern
- The pattern must match the entire string (implicitly anchored)
- If the pattern matches, validation passes

**Best Practices**:
- Test your regex patterns thoroughly to ensure they match valid inputs and reject invalid ones
- Use anchors (`^` and `---
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

) to match the entire string, not just a substring
- Consider using character classes (`[a-z]`, `[0-9]`) for common patterns
- For complex patterns, consider breaking them into multiple steps or using custom validation
- Document your regex patterns for maintainability

**See Also**: 
- [String constraints](schema-keywords.html#string-constraints) for more details
- [Pattern](schema-keywords.html#pattern) reference page

---

## Construction Pattern: Format Validation

```json
{
  "type": "string",
  "format": "email"
}
```

**Use when**: You need to validate that a string conforms to a specific format, such as email, date, URI, etc.

**Details**:
- `type: "string"` specifies that the data must be a JSON string
- `format: "email"` validates that the string is a valid email address
- JSON Schema defines many built-in formats (email, date, date-time, uri, uuid, etc.)
- Format validation is optional in JSON Schema but commonly used

**Common Formats**:
- `email`: Valid email address
- `date`: Date in YYYY-MM-DD format
- `date-time`: Date and time in ISO 8601 format
- `uri`: Valid URI/URL
- `uuid`: Valid UUID (e.g., 550e8400-e29b-41d4-a716-446655440000)
- `ipv4`: Valid IPv4 address
- `ipv6`: Valid IPv6 address

**Best Practices**:
- Use format validation for common data types instead of writing custom regex patterns
- Be aware that format validation may not be supported by all validators
- Consider using `pattern` as a fallback if format validation is not available
- Document which formats you're using for maintainability

**See Also**: 
- [Format](schema-keywords.html#format) for more details and supported formats
- [String constraints](schema-keywords.html#string-constraints) for other string validation options

---

## Construction Pattern: Enum (Enumeration)

```json
{
  "type": "string",
  "enum": ["active", "inactive", "pending"]
}
```

**Use when**: You need to restrict a value to a specific set of allowed values, commonly used for status fields, categories, or options.

**Details**:
- `type: "string"` specifies that the data must be a JSON string
- `enum: ["active", "inactive", "pending"]` defines the only allowed values
- The value must exactly match one of the enum values
- Enum values can be of any type (strings, numbers, objects, arrays, etc.)

**How it works**:
- During validation, the validator checks if the value is present in the enum array
- Comparison is strict (type and value must match)
- If the value is found in the enum array, validation passes

**Best Practices**:
- Use enum for values that have a fixed, known set of options
- Keep enum values simple and easy to understand
- Consider using `const` instead of `enum` when there's only one allowed value
- Document the meaning of each enum value for maintainability
- For large sets of values, consider using `pattern` or custom validation instead

**See Also**: 
- [Enum](schema-keywords.html#enum) for more details
- [Const](schema-keywords.html#const) for single-value constraints

---

## Construction Pattern: Const (Constant Value)

```json
{
  "type": "string",
  "const": "fixed-value"
}
```

**Use when**: You need to enforce that a value must be exactly a specific constant value, commonly used for version fields, types, or fixed identifiers.

**Details**:
- `type: "string"` specifies that the data must be a JSON string
- `const: "fixed-value"` defines the only allowed value
- The value must exactly match the const value
- Const can be of any type (string, number, object, array, etc.)

**How it works**:
- During validation, the validator checks if the value is strictly equal to the const value
- Comparison is strict (type and value must match)
- If the value matches exactly, validation passes

**Best Practices**:
- Use `const` when there's only one allowed value
- Use `enum` when there are multiple allowed values
- Consider using `const` for version fields, type discriminators, or fixed identifiers
- Document why the value is constant for maintainability

**See Also**: 
- [Const](schema-keywords.html#const) for more details
- [Enum](schema-keywords.html#enum) for multiple-value constraints

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

## Construction Pattern: allOf (Composition)

```json
{
  "type": "object",
  "allOf": [
    {
      "properties": {
        "name": {"type": "string"}
      },
      "required": ["name"]
    },
    {
      "properties": {
        "age": {"type": "integer", "minimum": 0}
      },
      "required": ["age"]
    }
  ]
}
```

**Use when**: You need to combine multiple schemas and validate that the data satisfies ALL of them, commonly used for composing complex schemas from simpler ones.

**Details**:
- `type: "object"` specifies that the data must be a JSON object
- `allOf` is an array of schemas that must all validate successfully
- In this example, the object must have both "name" (string) and "age" (non-negative integer) properties
- Each schema in the array is applied independently

**How it works**:
- During validation, the validator checks the data against each schema in the `allOf` array
- All schemas must validate successfully for the overall validation to pass
- If any schema fails, the entire validation fails
- Errors from all failing schemas are collected

**Best Practices**:
- Use `allOf` to compose complex schemas from reusable components
- Keep each schema in the array focused on a single concern
- Consider extracting common schemas to `definitions`/`$defs` for reuse
- Be aware that `allOf` can impact performance with many schemas
- For simple cases, consider merging schemas manually instead of using `allOf`

**See Also**: 
- [AllOf](schema-keywords.html#allof) for more details
- [AnyOf](schema-keywords.html#anyof) and [OneOf](schema-keywords.html#oneof) for alternative composition
- [Definitions](schema-keywords.html#definitions) for reusable schema components

---

## Construction Pattern: anyOf (Composition)

```json
{
  "type": "object",
  "anyOf": [
    {
      "properties": {
        "email": {"type": "string", "format": "email"}
      },
      "required": ["email"]
    },
    {
      "properties": {
        "phone": {"type": "string", "pattern": "^\\+?[0-9]{10,15}$"}
      },
      "required": ["phone"]
    }
  ]
}
```

**Use when**: You need to validate that the data satisfies AT LEAST ONE of multiple schemas, commonly used for alternative structures or optional combinations.

**Details**:
- `type: "object"` specifies that the data must be a JSON object
- `anyOf` is an array of schemas where at least one must validate successfully
- In this example, the object must have either an "email" property (valid email format) OR a "phone" property (valid phone pattern)
- The object can have both properties, but at least one is required

**How it works**:
- During validation, the validator checks the data against each schema in the `anyOf` array
- At least one schema must validate successfully for the overall validation to pass
- If all schemas fail, the entire validation fails
- The validator typically stops at the first successful schema

**Best Practices**:
- Use `anyOf` for alternative structures or optional combinations
- Keep each schema in the array focused on a single alternative
- Consider using `oneOf` instead if exactly one schema must match
- Be aware that `anyOf` can impact performance with many schemas
- Document the alternatives clearly for maintainability

**See Also**: 
- [AnyOf](schema-keywords.html#anyof) for more details
- [OneOf](schema-keywords.html#oneof) for exclusive alternatives
- [AllOf](schema-keywords.html#allof) for combining all schemas

---

## Construction Pattern: oneOf (Composition)

```json
{
  "type": "object",
  "oneOf": [
    {
      "properties": {
        "type": {"const": "user"},
        "username": {"type": "string"}
      },
      "required": ["type", "username"]
    },
    {
      "properties": {
        "type": {"const": "admin"},
        "permissions": {"type": "array", "items": {"type": "string"}}
      },
      "required": ["type", "permissions"]
    }
  ]
}
```

**Use when**: You need to validate that the data satisfies EXACTLY ONE of multiple schemas, commonly used for discriminated unions or mutually exclusive structures.

**Details**:
- `type: "object"` specifies that the data must be a JSON object
- `oneOf` is an array of schemas where exactly one must validate successfully
- In this example, the object must be either a "user" (with username) OR an "admin" (with permissions), but not both
- The "type" property acts as a discriminator to determine which schema applies

**How it works**:
- During validation, the validator checks the data against each schema in the `oneOf` array
- Exactly one schema must validate successfully for the overall validation to pass
- If zero or more than one schema validates, the entire validation fails
- This ensures mutual exclusivity among the alternatives

**Best Practices**:
- Use `oneOf` for discriminated unions or mutually exclusive structures
- Include a discriminator property (like "type") to clearly identify which schema applies
- Keep each schema in the array focused on a single alternative
- Be aware that `oneOf` can impact performance with many schemas
- Consider using `anyOf` if multiple schemas can match simultaneously

**See Also**: 
- [OneOf](schema-keywords.html#oneof) for more details
- [AnyOf](schema-keywords.html#anyof) for non-exclusive alternatives
- [AllOf](schema-keywords.html#allof) for combining all schemas

---

## Construction Pattern: not (Composition)

```json
{
  "type": "object",
  "not": {
    "properties": {
      "status": {"enum": ["deleted", "banned"]}
    },
    "required": ["status"]
  }
}
```

**Use when**: You need to validate that the data does NOT match a specific schema, commonly used for exclusions or negative constraints.

**Details**:
- `type: "object"` specifies that the data must be a JSON object
- `not` is a schema that the data must NOT validate against
- In this example, the object must NOT have a "status" property with value "deleted" or "banned"
- The object can have any other status value or no status at all

**How it works**:
- During validation, the validator checks the data against the `not` schema
- If the data validates successfully against the `not` schema, the overall validation fails
- If the data fails to validate against the `not` schema, the overall validation passes
- This is a logical negation operation

**Best Practices**:
- Use `not` for exclusions or negative constraints
- Keep the `not` schema simple and focused on what should be excluded
- Consider using `enum` or `pattern` for simpler exclusions
- Be aware that `not` can be confusing for complex schemas
- Document what is being excluded for maintainability

**See Also**: 
- [Not](schema-keywords.html#not) for more details
- [AllOf](schema-keywords.html#allof), [AnyOf](schema-keywords.html#anyof), and [OneOf](schema-keywords.html#oneof) for positive composition

---

## Construction Pattern: additionalItems with prefixItems

```json
{
  "type": "array",
  "prefixItems": [
    {"type": "string"},
    {"type": "integer"}
  ],
  "additionalItems": {"type": "boolean"}
}
```

**Use when**: You need to validate arrays where the first N elements have specific types (tuple validation) and remaining elements must conform to a specific schema.

**Details**:
- `type: "array"` specifies that the data must be a JSON array
- `prefixItems` defines schemas for the first 2 elements (positions 0 and 1)
- `additionalItems` defines the schema for any elements beyond the prefixItems
- In this example:
  - Position 0 must be a string
  - Position 1 must be an integer
  - Any additional elements (positions 2+) must be booleans

**How it works**:
- During validation, the validator checks each element:
  - If the position is within `prefixItems`, it validates against that schema
  - If the position is beyond `prefixItems`, it validates against the `additionalItems` schema
- If `additionalItems` is `false`, no additional elements are allowed
- If `additionalItems` is `true` or omitted, any additional elements are allowed

**Best Practices**:
- Use `additionalItems` when you need to validate elements beyond the tuple
- Set `additionalItems: false` to enforce a fixed-length tuple
- Consider using `items` instead for homogeneous arrays
- Document the expected array structure for maintainability

**See Also**: 
- [Additional Items](schema-keywords.html#additionalitems) for more details
- [Prefix Items](schema-keywords.html#prefixitems) for tuple validation
- [Items](schema-keywords.html#items) for homogeneous array validation

---

## Related Pages

- Keyword matrix: [Schema keywords](schema-keywords.html)
- Runtime rule order: [Validation behavior](validation-behavior.html)
- Practical examples: [Examples](examples.html)
