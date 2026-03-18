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

---

### Integer

Whole numbers (no decimals).

```json
{ "type": "integer" }
```

**Valid values:** `42`, `-10`, `0`

**Invalid values:** `3.14`, `"42"`, `true`

---

### Number

Whole or decimal numbers.

```json
{ "type": "number" }
```

**Valid values:** `42`, `3.14`, `-10.5`

**Invalid values:** `"42"`, `true`

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

---

### Object

Object with key-value pairs.

```json
{ "type": "object" }
```

**Valid values:** `{"name": "John", "age": 30}`, `{}`

**Invalid values:** `[1, 2]`, `42`

---

## Multiple Types

You can specify multiple types using an array:

```json
{ "type": ["string", "integer"] }
```

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

---

## Next Steps

- Go back to [JSON Schema Reference](json-schema.md)
- Explore the [Tutorials](../tutorials/README.md)
- See [Skript Syntax](skript-syntax.md)

---

[← Back](../README.md) | [Previous: JSON Schema](json-schema.md)
