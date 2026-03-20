# JSON Schema Reference

> **Canonical contract:** [`../CONTRACT.md`](../CONTRACT.md)

This document provides the authoritative reference for all JSON Schema keywords supported by Schema-Validator.

---

## Overview

Schema-Validator implements a **subset** of JSON Schema Draft 2020-12 with backward compatibility for Draft-07 and Draft-06. This reference documents all implemented keywords, their status, and usage examples.

**Total Supported Keywords:** 39+

---

## Keyword Status Matrix

| Keyword | Status | Validator | Notes |
|---------|--------|-----------|-------|
| `type` | ✅ Implemented | `ValidatorDispatcher` | Core type validation |
| `properties` | ✅ Implemented | `ObjectValidator` | Object property definitions |
| `patternProperties` | ✅ Implemented | `ObjectValidator` | Regex-based properties |
| `additionalProperties` | ✅ Implemented | `ObjectValidator` | Control extra properties |
| `required` | ✅ Implemented | `ObjectValidator` | Required field array |
| `minProperties` | ✅ Implemented | `ObjectValidator` | Min properties count |
| `maxProperties` | ✅ Implemented | `ObjectValidator` | Max properties count |
| `dependencies` | ⚠️ Partial | - | Schema dependencies only |
| `items` | ✅ Implemented | `ArrayValidator` | Array item schema |
| `minItems` | ✅ Implemented | `ArrayValidator` | Min array length |
| `maxItems` | ✅ Implemented | `ArrayValidator` | Max array length |
| `uniqueItems` | ✅ Implemented | `ArrayValidator` | Require unique items |
| `additionalItems` | ⚠️ Limited | - | Limited support |
| `minLength` | ✅ Implemented | `PrimitiveValidator` | Min string length |
| `maxLength` | ✅ Implemented | `PrimitiveValidator` | Max string length |
| `pattern` | ✅ Implemented | `PrimitiveValidator` | Regex pattern |
| `format` | ✅ Implemented | `FormatValidator` | Format validation |
| `minimum` | ✅ Implemented | `PrimitiveValidator` | Min numeric value |
| `maximum` | ✅ Implemented | `PrimitiveValidator` | Max numeric value |
| `exclusiveMinimum` | ✅ Implemented | `PrimitiveValidator` | Exclusive min |
| `exclusiveMaximum` | ✅ Implemented | `PrimitiveValidator` | Exclusive max |
| `multipleOf` | ✅ Implemented | `PrimitiveValidator` | Numeric multiple |
| `enum` | ✅ Implemented | `PrimitiveValidator` | Enumeration |
| `const` | ✅ Implemented | `PrimitiveValidator` | Constant value |
| `allOf` | ✅ Implemented | `ObjectValidator` | Validate all schemas |
| `anyOf` | ✅ Implemented | `ObjectValidator` | Validate any schema |
| `oneOf` | ✅ Implemented | `OneOfValidator` | Exactly one schema |
| `not` | ✅ Implemented | `NotValidator` | Must NOT match |
| `if` | ✅ Implemented | `ConditionalValidator` | Conditional schema |
| `then` | ✅ Implemented | `ConditionalValidator` | If branch |
| `else` | ✅ Implemented | `ConditionalValidator` | Else branch |
| `$ref` | ⚠️ Partial | `SchemaRefResolver` | JSON Pointer ref |
| `definitions` | ✅ Implemented | - | Schema definitions |
| `title` | ✅ Implemented | - | Metadata only |
| `description` | ✅ Implemented | - | Metadata only |
| `default` | ✅ Implemented | - | Metadata only |
| `examples` | ✅ Implemented | - | Metadata only |

---

## Supported Formats

### Standard Formats

| Format | Description | Example |
|--------|-------------|---------|
| `date-time` | ISO 8601 datetime | `2024-01-15T10:30:00Z` |
| `date` | ISO 8601 date | `2024-01-15` |
| `time` | ISO 8601 time | `10:30:00` |
| `email` | Email address | `user@example.com` |
| `idn-email` | Internationalized email | `用户@example.com` |
| `hostname` | Domain hostname | `example.com` |
| `idn-hostname` | Internationalized hostname | `exemple.com` |
| `ipv4` | IPv4 address | `192.168.1.1` |
| `ipv6` | IPv6 address | `2001:0db8::1` |
| `uri` | Full URI | `https://example.com/path` |
| `uri-reference` | URI reference | `/path` or `relative` |
| `uri-template` | URI template | `/users/{id}` |
| `json-pointer` | JSON Pointer | `/path/to/field` |
| `relative-json-pointer` | Relative JSON Pointer | `0/field` |
| `regex` | Regular expression | `^[a-z]+$` |
| `unix-time` | Unix timestamp | `1705312200` |

### Minecraft Formats (Custom)

| Format | Description | Example |
|--------|-------------|---------|
| `minecraft-item` | Minecraft item ID | `minecraft:diamond` |
| `minecraft-block` | Minecraft block ID | `minecraft:stone` |
| `minecraft-entity` | Minecraft entity ID | `minecraft:zombie` |
| `minecraft-attribute` | Minecraft attribute | `minecraft:generic.attack_damage` |
| `minecraft-effect` | Minecraft effect | `minecraft:speed` |
| `minecraft-enchantment` | Minecraft enchantment | `minecraft:efficiency` |
| `minecraft-biome` | Minecraft biome | `minecraft:plains` |
| `minecraft-dimension` | Minecraft dimension | `minecraft:the_nether` |
| `minecraft-particle` | Minecraft particle | `minecraft:flame` |
| `minecraft-sound` | Minecraft sound | `minecraft:block.break` |
| `minecraft-potion` | Minecraft potion | `minecraft:strength` |
| `minecraft-recipe` | Minecraft recipe | `minecraft:diamond_pickaxe` |
| `minecraft-tag` | Minecraft tag | `minecraft:logs` |

> **Note:** Minecraft formats follow the `namespace:name` pattern. Using just `diamond` will fail validation; use `minecraft:diamond`.

---

## Unsupported Keywords

The following keywords are **NOT YET SUPPORTED**:

- `propertyNames` - Validate property names
- `contains` - Array must contain specific item
- `$defs` - Alternative to definitions
- `$recursiveRef` - Recursive references
- `unevaluatedProperties` - Unevaluated properties
- `unevaluatedItems` - Unevaluated items
- `dependentRequired` - Required dependencies
- `dependentSchemas` - Schema dependencies

---

## Usage Examples

### Object with Required Fields

```json
{
  "type": "object",
  "properties": {
    "name": { "type": "string", "minLength": 1 },
    "level": { "type": "integer", "minimum": 1 }
  },
  "required": ["name", "level"],
  "additionalProperties": false
}
```

### Conditional Validation (oneOf)

```json
{
  "oneOf": [
    { "properties": { "type": { "const": "weapon" } }, "required": ["damage"] },
    { "properties": { "type": { "const": "armor" } }, "required": ["defense"] }
  ]
}
```

### Conditional Validation (if/then/else)

```json
{
  "if": { "properties": { "rarity": { "const": "legendary" } } },
  "then": { "required": ["abilities"] },
  "else": { "required": ["description"] }
}
```

### Minecraft Item Validation

```json
{
  "type": "object",
  "properties": {
    "item": {
      "type": "string",
      "format": "minecraft-item"
    },
    "block": {
      "type": "string", 
      "format": "minecraft-block"
    }
  }
}
```

### Numeric Multiple Of

```json
{
  "type": "number",
  "multipleOf": 0.5
}
```

### String Pattern

```json
{
  "type": "string",
  "pattern": "^[a-zA-Z0-9_-]+$"
}
```

### Enum Constraint

```json
{
  "type": "string",
  "enum": ["common", "uncommon", "rare", "epic", "legendary"]
}
```

---

## Important Behavior Notes

1. **Format Failures:** Format validation errors are hard errors (not warnings)
2. **$ref Resolution:** Requires proper resolver wiring (`new ValidationService(refResolver)`)
3. **Type: Number:** Accepts both integers and floating-point numbers
4. **Custom Properties:** Custom keywords are ignored with a warning (not errors)

---

## Version Compatibility

This reference reflects support for **JSON Schema Draft 2020-12** with backward compatibility for Draft-07 and Draft-06.

---

## Contributing

To add support for a new keyword:

1. Add the keyword to [`SupportedKeywordsRegistry.java`](src/main/java/com/maiconjh/schemacr/schemes/SupportedKeywordsRegistry.java)
2. Update the parser in [`FileSchemaLoader.java`](src/main/java/com/maiconjh/schemacr/schemes/FileSchemaLoader.java)
3. Implement validation in the appropriate validator class
4. Add tests in `src/test/`
5. Update this documentation

---

*Last Updated: 2026-03-20*
*This document consolidates content from supported-keywords.md and reference/json-schema.md*
