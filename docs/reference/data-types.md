# Data Types

Guide to data types supported by Schema Validator.

---

## Primitive Types

### String

Simple text in quotes.

```json
{ "type": "string" }
```

**Valid values:** `"hello"`, `"player123"`, `""`

**Invalid values:** `123`, `true`

**Constraints:**
- `minLength` - Minimum character count
- `maxLength` - Maximum character count
- `pattern` - Regular expression pattern
- `enum` - Allowed values list

---

### Integer

Whole numbers (no decimals). This type was separated from NUMBER to provide stricter validation.

```json
{ "type": "integer" }
```

**Valid values:** `42`, `-10`, `0`

**Invalid values:** `3.14`, `"42"`, `true`

**Constraints:**
- `minimum` - Minimum value (inclusive)
- `maximum` - Maximum value (inclusive)
- `exclusiveMinimum` - Minimum value (exclusive)
- `exclusiveMaximum` - Maximum value (exclusive)
- `enum` - Allowed values list

**Internal validation:** Uses `isValidInteger()` method that accepts:
- `Integer`, `Long`, `Short`, `Byte` types directly
- Other number types (Double, Float, BigDecimal) if they have no fractional part

---

### Number

Decimal numbers (with or without decimals). Per JSON Schema specification, this type excludes integers.

```json
{ "type": "number" }
```

**Valid values:** `3.14`, `-10.5`, `0.5`

**Invalid values:** `42`, `"42"`, `true`

**Important:** In this implementation, `number` accepts ONLY decimal numbers. If you need to accept both integers and decimals, use `number`. If you need ONLY whole numbers, use `integer`.

**Constraints:**
- `minimum` - Minimum value (inclusive)
- `maximum` - Maximum value (inclusive)
- `exclusiveMinimum` - Minimum value (exclusive)
- `exclusiveMaximum` - Maximum value (exclusive)
- `enum` - Allowed values list

---

## Technical Difference: INTEGER vs NUMBER

This implementation follows the JSON Schema specification where INTEGER and NUMBER are distinct types:

| Aspect | `integer` | `number` |
|--------|-----------|----------|
| JSON type value | `"integer"` | `"number"` |
| Accepts Integer | ✅ Yes | ❌ No |
| Accepts Long | ✅ Yes | �️ No |
| Accepts Double/Float | ✅ If no decimal part | ✅ Yes |
| Accepts BigDecimal | ✅ If no decimal part | ✅ Yes |
| Use case | Counts, IDs, indices | Measurements, coordinates |

### Why Separate These Types?

1. **Type Safety**: Prevents accidental decimal values where whole numbers are expected
2. **JSON Schema Compliance**: Follows the official JSON Schema specification
3. **Clear Intent**: Schema authors can explicitly declare their numeric requirements

### Examples

```json
// Count of items (must be whole number)
{ "type": "integer", "minimum": 0 }

// Price (must have decimal)
{ "type": "number", "minimum": 0.01 }

// Either works - accepts any number
{ "type": "number" }
```

---

### Boolean

True or false values.

```json
{ "type": "boolean" }
```

**Valid values:** `true`, `false`

**Invalid values:** `"true"`, `1`, `0`

---

### Null

Null value.

```json
{ "type": "null" }
```

**Valid values:** `null`

---

## Compound Types

### Array

Ordered list of values.

```json
{ "type": "array" }
```

**Valid values:** `[1, 2, 3]`, `["a", "b"]`, `[]`

**Invalid values:** `{"a": 1}`, `42`

**Constraints:**
- `items` - Schema for array elements
- `minItems` - Minimum element count
- `maxItems` - Maximum element count
- `uniqueItems` - All elements must be unique

---

### Object

Object with key-value pairs.

```json
{ "type": "object" }
```

**Valid values:** `{"name": "John", "age": 30}`, `{}`

**Invalid values:** `[1, 2]`, `42`

**Constraints:**
- `properties` - Define allowed properties
- `required` - List of required property names
- `additionalProperties` - Allow/disallow extra properties
- `patternProperties` - Validate properties by regex pattern
- `minProperties` - Minimum property count
- `maxProperties` - Maximum property count

---

## Any Type

Accepts any value.

```json
{ "type": "any" }
```

This is useful when a field can accept any type of value.

---

## Multiple Types

You can specify multiple types using an array:

```json
{ "type": ["string", "integer"] }
```

**Valid values:** `"hello"`, `42`

**Invalid values:** `3.14`, `true`

---

## Minecraft-Specific Types

### Materials

Minecraft material names.

```json
{
  "type": "string",
  "pattern": "^[A-Z][A-Z_]*$"
}
```

**Examples:** `"DIAMOND_SWORD"`, `"APPLE"`, `"GRASS_BLOCK"`

---

### Item Slots

Inventory slots (0-53).

```json
{
  "type": "integer",
  "minimum": 0,
  "maximum": 53
}
```

| Range | Location |
|-------|----------|
| 0-8 | Hotbar |
| 9-35 | Inventory |
| 36-39 | Armor (feet, legs, chest, head) |
| 40-44 | Armor offhand |
| 45 | Armor secondary |

---

### Enchantments

Enchantment names.

```json
{
  "type": "string",
  "enum": [
    "sharpness",
    "protection",
    "efficiency",
    "unbreaking",
    "fortune",
    "looting",
    "fire_aspect",
    "knockback",
    "power",
    "punch"
  ]
}
```

---

### Attributes

Item attributes.

```json
{
  "type": "string",
  "enum": [
    "generic.attack_damage",
    "generic.attack_speed",
    "generic.max_health",
    "generic.movement_speed",
    "generic.armor",
    "generic.knockback_resistance"
  ]
}
```

---

## YAML-Specific Type

### Item List

```yaml
items:
  - slot: 0
    material: "DIAMOND_SWORD"
    amount: 1
```

Equivalent JSON:
```json
{
  "type": "object",
  "properties": {
    "items": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "slot": { "type": "integer" },
          "material": { "type": "string" },
          "amount": { "type": "integer" }
        }
      }
    }
  }
}
```

---

## YAML → JSON Conversion

| YAML | JSON |
|------|------|
| `true` / `false` | `true` / `false` |
| `null` | `null` |
| `1.0` | `1` (integer) |
| `1.5` | `1.5` (number) |
| `~` | `null` |
| `>-|` (block) | string |
| `|` (block) | string with newlines |

---

## Reference Table

| JSON Type | Skript Type | Description |
|-----------|-------------|-------------|
| `string` | text | Text |
| `integer` | integer | Whole number |
| `number` | number | Decimal number |
| `boolean` | boolean | True/False |
| `array` | list | List |
| `object` | object | Object/Map |
| `null` | null | Null value |
| `any` | any | Any value |

---

## Next Steps

- Go back to [JSON Schema Reference](json-schema.md)
- Explore the [Schema Composition](schema-composition.md) guide
- Explore the [Tutorials](../tutorials/README.md)
- See [Skript Syntax](skript-syntax.md)

---

[← Back](../README.md) | [Previous: JSON Schema](json-schema.md)
