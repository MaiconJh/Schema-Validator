# Reference: Examples and Schema Construction

This document provides a complete reference for building validation schemas using the Schema-Validator library. All content is based on the existing source code in `src/main/java/` and demonstrates the actual library implementation.

---

## 1. Schema Model

The Schema-Validator library uses the [`Schema`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/java/com/maiconjh/schemacr/schemes/Schema.java) class to represent schema nodes. This class contains the following main fields:

### 1.1 Structure and Type

| Field | Description |
|-------|-------------|
| `type` | Data type (object, array, string, number, integer, boolean, null, any) |
| `properties` | Property map for objects |
| `patternProperties` | Properties with regex patterns |
| `itemSchema` | Schema for array items |
| `requiredFields` | List of required fields |
| `additionalProperties` | Allows undeclared properties |

### 1.2 Primitive Constraints

| Field | Description |
|-------|-------------|
| `minimum`, `maximum` | Numeric limits |
| `exclusiveMinimum`, `exclusiveMaximum` | Exclusive limits |
| `minLength`, `maxLength` | String length |
| `pattern` | Regex for validation |
| `format` | Format (email, uri, date-time, etc.) |
| `multipleOf` | Valid multiple for numbers |
| `enumValues` | Allowed values |

### 1.3 Composition and Conditionals

| Field | Description |
|-------|-------------|
| `allOf` | Must validate against all schemas |
| `anyOf` | Must validate against at least one |
| `oneOf` | Must validate against exactly one |
| `notSchema` | Must not validate against this schema |
| `ifSchema`, `thenSchema`, `elseSchema` | Conditional validation |

### 1.4 References and Metadata

| Field | Description |
|-------|-------------|
| `ref` | `$ref` reference for external schemas |
| `version` | Schema version |
| `compatibility` | Compatibility (e.g., "1.21", "1.20") |

---

## 2. Supported Schema Types

The library supports the following types defined in the [`SchemaType`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/java/com/maiconjh/schemacr/schemes/SchemaType.java) enum:

- `OBJECT` - Object/Map
- `ARRAY` - Array/List
- `STRING` - String/Text
- `NUMBER` - Number (integer or decimal)
- `INTEGER` - Integer
- `BOOLEAN` - Boolean (true/false)
- `NULL` - Null
- `ANY` - Any type (always passes)

---

## 3. Validation Flow

### 3.1 Validator Dispatch

The [`ValidatorDispatcher`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/java/com/maiconjh/schemacr/validation/ValidatorDispatcher.java) selects the correct validator based on schema type:

- `OBJECT` → [`ObjectValidator`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/java/com/maiconjh/schemacr/validation/ObjectValidator.java)
- `ARRAY` → [`ArrayValidator`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/java/com/maiconjh/schemacr/validation/ArrayValidator.java)
- Other types → [`PrimitiveValidator`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/java/com/maiconjh/schemacr/validation/PrimitiveValidator.java)

### 3.2 Object Validation Order

1. Resolve `$ref` (only if `SchemaRefResolver` is present)
2. Verify value is a `Map`
3. Apply `allOf`, `anyOf`, `oneOf`, `not`, `if/then/else`
4. Check required fields
5. Validate declared properties that are present
6. For unknown keys, try `patternProperties`; if no match and `additionalProperties: false`, fail

### 3.3 Primitive Validation

- `ANY` always passes
- `INTEGER` accepts integral values (Integer, Long, Short, Byte or numbers with no decimal part)
- `enum` takes precedence: when present, other primitive constraints are not evaluated
- Unknown formats pass (don't cause failure)

### 3.4 Array Validation

- Value must be a `List`
- `items` is applied recursively per element
- `minItems`, `maxItems`, `uniqueItems` are **not enforced** currently

---

## 4. Supported Formats

The library supports the following formats through [`FormatValidator`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/java/com/maiconjh/schemacr/validation/FormatValidator.java):

### Standard JSON Schema Formats

| Format | Description |
|--------|-------------|
| `date-time` | RFC 3339 date-time |
| `date` | RFC 3339 full-date (YYYY-MM-DD) |
| `time` | RFC 3339 full-time (HH:MM:SS) |
| `duration` | ISO 8601 duration |
| `email` | RFC 5322 email |
| `idn-email` | Internationalized email |
| `hostname` | RFC 1123 hostname |
| `idn-hostname` | Internationalized hostname |
| `ipv4` | IPv4 address |
| `ipv6` | IPv6 address |
| `uri` | Absolute URI |
| `uri-reference` | Absolute or relative URI |
| `uri-template` | RFC 6570 URI Template |
| `json-pointer` | RFC 6901 JSON Pointer |
| `relative-json-pointer` | Relative JSON Pointer |
| `uuid` | UUID (RFC 4122) |
| `regex` | ECMA 262 regular expression |

### Minecraft-Specific Formats

| Format | Description | Example |
|--------|-------------|---------|
| `minecraft-item` | Item ID (namespace:name) | `minecraft:diamond_sword` |
| `minecraft-block` | Block ID | `minecraft:gold_ore` |
| `minecraft-entity` | Entity ID | `minecraft:zombie` |
| `minecraft-attribute` | Attribute ID | `minecraft:generic.max_health` |
| `minecraft-effect` | Effect ID | `minecraft:speed` |
| `minecraft-enchantment` | Enchantment ID | `minecraft:efficiency` |
| `minecraft-biome` | Biome ID | `minecraft:plains` |
| `minecraft-dimension` | Dimension ID | `minecraft:overworld` |
| `minecraft-particle` | Particle ID | `minecraft:blockcrack_15232` |
| `minecraft-sound` | Sound ID | `minecraft:block.gold_ore.break` |
| `minecraft-potion` | Potion ID | `minecraft:strength` |
| `minecraft-recipe` | Recipe ID | `minecraft:diamond_sword` |
| `minecraft-tag` | Tag (#namespace:name) | `#minecraft:pickaxes` |

---

## 5. Practical Schema Building Examples

### 5.1 Simple Object with Properties

```json
{
  "type": "object",
  "properties": {
    "id": {"type": "string"},
    "level": {"type": "number"},
    "active": {"type": "boolean"},
    "tags": {"type": "array", "items": {"type": "string"}}
  }
}
```

Source: [`player-profile.schema.json`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/resources/examples/schemas/player-profile.schema.json)

### 5.2 Strict Object (Closed Contract)

Use when you want fully known keys:

```json
{
  "type": "object",
  "required": ["id", "name"],
  "additionalProperties": false,
  "properties": {
    "id": {"type": "string"},
    "name": {"type": "string", "minLength": 2, "maxLength": 32},
    "level": {"type": "integer", "minimum": 1, "maximum": 100}
  }
}
```

**Effect**: Unknown keys fail immediately at the object level.

### 5.3 Dynamic Key Map

Use `patternProperties` + `additionalProperties: false` for map-like structures:

```json
{
  "type": "object",
  "patternProperties": {
    "^[a-z0-9_-]+$": {"type": "string"}
  },
  "additionalProperties": false
}
```

### 5.4 Homogeneous List

```json
{
  "type": "array",
  "items": {"type": "integer", "minimum": 0}
}
```

Each element is validated recursively through the dispatcher.

### 5.5 Nullable/Any Payload

```json
{"type": "any"}
```

**Always passes** - useful for optional fields that accept any value.

### 5.6 Boolean, Null, and Integer Types

```json
{"type": "boolean"}   // Requires Java boolean value
{"type": null}       // Requires null value
{"type": "integer"}   // Accepts 3 and 3.0, but not 3.2
```

### 5.7 Enum and Format

```json
{
  "type": "object",
  "required": ["playerType"],
  "properties": {
    "playerType": {
      "type": "string",
      "enum": ["warrior", "mage", "rogue", "healer"]
    },
    "email": {
      "type": "string",
      "format": "email"
    },
    "blockId": {
      "type": "string",
      "format": "minecraft-block"
    }
  }
}
```

### 5.8 Numeric Constraints

```json
{
  "type": "object",
  "properties": {
    "level": {
      "type": "integer",
      "minimum": 1,
      "maximum": 100
    },
    "health": {
      "type": "number",
      "minimum": 0,
      "maximum": 1000
    },
    "coins": {
      "type": "integer",
      "multipleOf": 10
    },
    "discount": {
      "type": "number",
      "multipleOf": 0.05,
      "minimum": 0,
      "maximum": 1
    }
  }
}
```

### 5.9 Conditional Validation (if/then/else)

```json
{
  "type": "object",
  "required": ["playerType", "name", "stats"],
  "additionalProperties": false,
  "properties": {
    "playerType": {
      "type": "string",
      "enum": ["warrior", "mage", "rogue", "healer", "merchant", "quest-giver"]
    },
    "name": {"type": "string", "minLength": 2, "maxLength": 32},
    "level": {"type": "number", "minimum": 1, "maximum": 100},
    "stats": {"type": "object"}
  },
  "if": {
    "properties": {
      "playerType": {"enum": ["warrior", "mage", "rogue", "healer"]}
    },
    "required": ["level"]
  },
  "then": {
    "properties": {
      "level": {"type": "number", "minimum": 1, "maximum": 100}
    }
  },
  "else": {
    "properties": {
      "level": {"type": "number", "minimum": 0, "maximum": 10}
    }
  }
}
```

Source: [`conditional-validation.schema.json`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/resources/examples/schemas/conditional-validation.schema.json)

### 5.10 oneOf (Exactly One)

```json
{
  "type": "object",
  "required": ["itemType", "id", "name"],
  "additionalProperties": false,
  "properties": {
    "itemType": {
      "type": "string",
      "enum": ["weapon", "armor", "tool", "consumable"]
    },
    "id": {"type": "string"},
    "name": {"type": "string"}
  },
  "oneOf": [
    {
      "required": ["damage"],
      "properties": {
        "itemType": {"const": "weapon"},
        "damage": {"type": "number", "minimum": 1}
      }
    },
    {
      "required": ["defense"],
      "properties": {
        "itemType": {"const": "armor"},
        "defense": {"type": "number", "minimum": 1}
      }
    }
  ]
}
```

### 5.11 allOf (All Schemas)

```json
{
  "type": "object",
  "allOf": [
    {
      "properties": {
        "id": {"type": "string"}
      }
    },
    {
      "required": ["name"],
      "properties": {
        "name": {"type": "string", "minLength": 1}
      }
    }
  ]
}
```

### 5.12 not (Negation)

```json
{
  "type": "object",
  "not": {
    "properties": {
      "playerType": {"const": "banned"}
    }
  }
}
```

### 5.13 Complete Game Item Schema

This example demonstrates various combined features:

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Game Item Schema",
  "description": "Game item with conditional validation",
  "type": "object",
  "required": ["id", "type"],
  "additionalProperties": false,
  "properties": {
    "id": {
      "type": "string",
      "pattern": "^[a-z0-9_:]+$"
    },
    "type": {
      "type": "string",
      "enum": ["weapon", "armor", "tool", "consumable"]
    },
    "name": {
      "type": "string",
      "minLength": 1,
      "maxLength": 50
    },
    "damage": {"type": "number", "minimum": 1},
    "defense": {"type": "number", "minimum": 0},
    "durability": {"type": "number", "minimum": 1},
    "healing": {"type": "number", "minimum": 0}
  },
  "oneOf": [
    {
      "required": ["damage"],
      "properties": {"type": {"const": "weapon"}, "damage": {"type": "number", "minimum": 1}}
    },
    {
      "required": ["defense"],
      "properties": {"type": {"const": "armor"}, "defense": {"type": "number", "minimum": 1}}
    },
    {
      "required": ["durability"],
      "properties": {"type": {"const": "tool"}, "durability": {"type": "number", "minimum": 1}}
    },
    {
      "required": ["healing"],
      "properties": {"type": {"const": "consumable"}, "healing": {"type": "number", "minimum": 1}}
    }
  ],
  "not": {
    "properties": {"id": {"pattern": "^banned:.*"}}
  },
  "if": {
    "properties": {"type": {"const": "weapon"}}
  },
  "then": {
    "required": ["damage", "durability"],
    "properties": {
      "damage": {"type": "number", "minimum": 1},
      "durability": {"type": "number", "minimum": 10}
    }
  }
}
```

Source: [`item.schema.json`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/resources/schemas/item.schema.json)

---

## 6. Java API Usage

### 6.1 Basic Validation

```java
import com.maiconjh.schemacr.core.ValidationService;
import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.FileSchemaLoader;
import java.util.Map;

// Load schema
FileSchemaLoader loader = new FileSchemaLoader();
Schema schema = loader.load("schemas/player.schema.json");

// Data to validate
Map<String, Object> playerData = Map.of(
    "name", "PlayerOne",
    "level", 50,
    "class", "warrior"
);

// Validate
ValidationService service = new ValidationService();
var result = service.validate(playerData, schema);

if (result.isSuccess()) {
    System.out.println("Validation passed!");
} else {
    result.getErrors().forEach(e -> System.out.println(e));
}
```

### 6.2 Validation with $ref Support

```java
import com.maiconjh.schemacr.core.ValidationService;
import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaRefResolver;
import com.maiconjh.schemacr.schemes.FileSchemaLoader;

// Load schema with references
FileSchemaLoader loader = new FileSchemaLoader();
Schema schema = loader.load("schemas/player-with-address.schema.json");

// Create resolver
SchemaRefResolver resolver = new SchemaRefResolver(loader.getDefinitions());

// Validate with $ref support
ValidationService service = new ValidationService(resolver);
var result = service.validate(data, schema);
```

### 6.3 Batch Validation

```java
List<Map<String, Object>> players = // ... list of players
ValidationService service = new ValidationService();
List<ValidationResult> results = service.validateBatch(players, schema);
```

---

## 7. Skript Usage

### 7.1 Registered Syntax

The library provides the following Skript effects:

```
validate yaml <string> using schema <string>
validate json <string> using schema <string>
```

### 7.2 Complete Example

```skript
command /validateplayer:
    trigger:
        # Validate YAML file against schema
        validate yaml "plugins/Schema-Validator/examples/player.yml" using schema "plugins/Schema-Validator/examples/schemas/player.schema.json"
        
        # Get validation errors
        set {_errors::*} to last schema validation errors
        
        # Check result
        if size of {_errors::*} is 0:
            send "✓ Valid data!" to player
            send "Player validated successfully!" to player
        else:
            send "✗ Invalid data!" to player
            send "Errors found:" to player
            loop {_errors::*}:
                send "- %loop-value%" to player
```

Source: [`validate-simple-example.sk`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/resources/examples/validate-simple-example.sk)

---

## 8. Supported Keywords

### 8.1 Enforced at Validation Time

- `type`
- `properties`
- `patternProperties`
- `required`
- `additionalProperties`
- `items`
- `enum`
- `minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`
- `minLength`, `maxLength`, `pattern`, `format`
- `allOf`, `anyOf`, `oneOf`, `not`
- `if`, `then`, `else`

### 8.2 Parsed but Not Enforced

- `version`, `compatibility` (stored only)
- `definitions`, `$defs`
- `minItems`, `maxItems`, `uniqueItems` (recognized but not enforced)
- `const` (recognized but not enforced)
- `dependencies`, `minProperties`, `maxProperties`

---

## 9. Known Limitations

1. **$ref via Skript**: The Skript effect uses `new ValidationService()` without resolver, so `$ref` doesn't work in the effect path.
2. **Root types**: Data loaded by the Skript effect is typed as `Map<String,Object>`, so root-level arrays or scalars are not supported in that path.
3. **Not enforced keywords**: `const`, `minItems`, `maxItems`, `uniqueItems` are recognized but don't cause validation failures.
4. **Unknown format**: Unknown formats pass (don't cause failure) - see [`FormatValidator.java`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/java/com/maiconjh/schemacr/validation/FormatValidator.java)

---

## 10. Source File Reference

| Component | File |
|-----------|------|
| Schema Model | [`Schema.java`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/java/com/maiconjh/schemacr/schemes/Schema.java) |
| Schema Types | [`SchemaType.java`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/java/com/maiconjh/schemacr/schemes/SchemaType.java) |
| Object Validator | [`ObjectValidator.java`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/java/com/maiconjh/schemacr/validation/ObjectValidator.java) |
| Array Validator | [`ArrayValidator.java`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/java/com/maiconjh/schemacr/validation/ArrayValidator.java) |
| Primitive Validator | [`PrimitiveValidator.java`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/java/com/maiconjh/schemacr/validation/PrimitiveValidator.java) |
| Format Validator | [`FormatValidator.java`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/java/com/maiconjh/schemacr/validation/FormatValidator.java) |
| Conditional Validator | [`ConditionalValidator.java`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/java/com/maiconjh/schemacr/validation/ConditionalValidator.java) |
| Validation Service | [`ValidationService.java`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/java/com/maiconjh/schemacr/core/ValidationService.java) |
| Keywords Registry | [`SupportedKeywordsRegistry.java`](https://github.com/MaiconJh/Schema-Validator/blob/main/src/main/java/com/maiconjh/schemacr/schemes/SupportedKeywordsRegistry.java) |

---

[← Previous](validation-behavior.md) | [Next →](config-reference.md) | [Home](../../README.md)
