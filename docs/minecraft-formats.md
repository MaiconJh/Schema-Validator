# Custom Minecraft Formats

Schema Validator supports custom validation formats for Minecraft IDs using the `format` keyword in JSON schemas. These formats follow the Minecraft namespaced ID pattern (`namespace:name`).

## Supported Formats

| Format | Example Valid Values | Description |
|--------|---------------------|-------------|
| `minecraft-item` | `minecraft:diamond_sword`, `myplugin:custom_sword` | Minecraft items |
| `minecraft-block` | `minecraft:gold_ore`, `minecraft:stone` | Minecraft blocks |
| `minecraft-entity` | `minecraft:zombie`, `minecraft:player` | Minecraft entities |
| `minecraft-attribute` | `minecraft:generic.max_health`, `minecraft:generic.attack_damage` | Entity attributes |
| `minecraft-effect` | `minecraft:speed`, `minecraft:regeneration` | Status effects |
| `minecraft-enchantment` | `minecraft:efficiency`, `minecraft:sharpness` | Enchantments |
| `minecraft-biome` | `minecraft:plains`, `minecraft:deep_dark` | Biomes |
| `minecraft-dimension` | `minecraft:overworld`, `minecraft:the_nether` | Dimensions |
| `minecraft-particle` | `minecraft:blockcrack_15232`, `minecraft:happy_villager` | Particles |
| `minecraft-sound` | `minecraft:block.gold_ore.break`, `minecraft:entity.player.levelup` | Sound events |
| `minecraft-potion` | `minecraft:strength`, `minecraft:healing` | Potion types |
| `minecraft-recipe` | `minecraft:diamond_sword`, `myplugin:custom_craft` | Recipes |
| `minecraft-tag` | `#minecraft:pickaxes`, `#minecraft:logs` | Tags (with # prefix) |

## ID Pattern Specification

All formats follow the Minecraft namespaced ID pattern:

```
namespace:name
```

- **Namespace**: Must start with a lowercase letter
- **Name**: Must start with a lowercase letter
- **Allowed characters**: Lowercase letters (a-z), numbers (0-9), underscores (_), hyphens (-)
- **Additional characters for some types**: Dots (.) for attributes/sounds, slashes (/) for recipes
- **Tags**: Must be prefixed with `#` (e.g., `#minecraft:pickaxes`)

## Usage Examples

### Basic Schema Example

```json
{
  "type": "object",
  "properties": {
    "item_id": {
      "type": "string",
      "format": "minecraft-item"
    },
    "block_id": {
      "type": "string",
      "format": "minecraft-block"
    },
    "entity_id": {
      "type": "string",
      "format": "minecraft-entity"
    },
    "effect_id": {
      "type": "string",
      "format": "minecraft-effect"
    }
  }
}
```

### Custom Block Schema Example

This example shows a complete custom block definition schema:

```json
{
  "type": "object",
  "patternProperties": {
    "^[a-zA-Z0-9_-]+$": {
      "type": "object",
      "properties": {
        "block-type": {
          "type": "string",
          "format": "minecraft-block",
          "description": "The Minecraft block type"
        },
        "drops": {
          "type": "object",
          "patternProperties": {
            "^[a-zA-Z0-9_-]+$": {
              "type": "object",
              "properties": {
                "type": {
                  "type": "string",
                  "enum": ["item", "variable", "command"]
                },
                "value": {
                  "type": "string",
                  "format": "minecraft-item",
                  "description": "The item to drop"
                }
              },
              "required": ["type", "value"]
            }
          }
        }
      },
      "required": ["block-type"]
    }
  }
}
```

### Valid Data Example

```yaml
my_custom_block:
  block-type: "minecraft:gold_ore"
  drops:
    primary:
      type: "item"
      value: "minecraft:diamond"
    rare:
      type: "item"
      value: "minecraft:emerald"
```

### Invalid Data Example

The following would fail validation because the item ID format is incorrect:

```yaml
my_custom_block:
  block-type: "gold_ore"  # Invalid - missing namespace
  drops:
    primary:
      type: "item"
      value: "Diamond"  # Invalid - uppercase letters
```

## Integration with ValidationService

When using the `ValidationService` directly in Java code, the formats are automatically validated:

```java
ValidationService service = new ValidationService();
ValidationResult result = service.validate(data, schema);

if (!result.isValid()) {
    for (ValidationError error : result.getErrors()) {
        System.out.println(error.getPath() + ": " + error.getMessage());
    }
}
```

## Implementation Details

The format validation is implemented in [`FormatValidator.java`](../src/main/java/com/maiconjh/schemacr/validation/FormatValidator.java). Each format uses a specific regular expression pattern to validate the ID structure.

### Code Reference

```java
// Example: Minecraft item pattern
private static final Pattern MINECRAFT_ITEM = Pattern.compile(
    "^[a-z][a-z0-9_*-]*:[a-z][a-z0-9_*-]+$"
);

// Usage in validation
case "minecraft-item" -> MINECRAFT_ITEM.matcher(value).matches();
```

## Error Messages

When validation fails, the error message will indicate which format was expected:

```
Validation failed at: $.my_block.block-type
Expected format: minecraft-block
Actual value: gold_orew
Details: Value does not match format 'minecraft-block'
```

## Extending Formats

To add new Minecraft-related formats, update the `FormatValidator.java` file:

1. Add a new `Pattern` constant for the format
2. Add a new case in the `isValid()` switch statement
3. Update this documentation

## See Also

- [JSON Schema Reference](reference/json-schema.md)
- [Custom Blocks Tutorial](../tutorials/custom-blocks.md)
- [Format Validator Source Code](../src/main/java/com/maiconjh/schemacr/validation/FormatValidator.java)
