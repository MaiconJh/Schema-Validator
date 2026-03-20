# Schema Validator

Schema Validator is a Minecraft (Paper/Spigot) plugin with Skript syntax for validating YAML/JSON data against schema files.

## Documentation policy

- Canonical behavior contract: [`docs/CONTRACT.md`](docs/CONTRACT.md)
- Docs index and structure: [`docs/README.md`](docs/README.md)

If this README conflicts with the contract, `docs/CONTRACT.md` is authoritative.

## Implemented core capabilities

- Validate YAML/JSON files from Skript using:
  - `validate yaml %string% using schema %string%`
  - `validate json %string% using schema %string%`
- Retrieve latest validation errors using:
  - `last schema validation errors`
- Schema keyword subset including:
  - object keywords (`properties`, `required`, `additionalProperties`, `patternProperties`)
  - array `items`
  - string/number constraints (`minLength`, `maxLength`, `pattern`, `format`, `minimum`, `maximum`, `multipleOf`, etc.)
  - `enum`, `allOf`, `anyOf`

## Important limitations

- Default validation entrypoint expects object-like root data.
- `$ref` exists but is only active when validation uses resolver wiring (`ValidationService(refResolver)`).
- `minItems`, `maxItems`, and `uniqueItems` are not currently enforced.

## Installation

1. Build with Gradle:

```bash
./gradlew build
```

2. Copy generated JAR to your server `plugins/` directory.
3. Ensure Skript is installed.
4. Start/restart the server.

## Custom Minecraft Formats

The plugin supports custom validation formats for Minecraft IDs using the `format` keyword in JSON schemas:

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
| `minecraft-tag` | `#minecraft:pickaxes`, `#minecraft:logs` | Tags (with #) |

### Usage Example

```json
{
  "type": "object",
  "properties": {
    "item_id": { "type": "string", "format": "minecraft-item" },
    "block_id": { "type": "string", "format": "minecraft-block" },
    "entity_id": { "type": "string", "format": "minecraft-entity" },
    "effect_id": { "type": "string", "format": "minecraft-effect" },
    "tag_id": { "type": "string", "format": "minecraft-tag" }
  }
}
```

## Runtime config

`plugins/Schema-Validator/config.yml` supports:

```yaml
schema-directory: "schemas"
auto-load: true
cache-enabled: true
validation-on-load: true
```

See [`docs/configuration.md`](docs/configuration.md) for details.
