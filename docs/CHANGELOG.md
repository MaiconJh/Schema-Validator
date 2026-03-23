# Changelog - Schema-Validator

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [0.5.0] - 2026-03-23 - Semantic Minecraft Validation

### Added

#### Semantic Validation for Minecraft Formats
- Entity validation against `EntityType` registry
- Biome validation against `Biome` registry
- Enchantment validation against `Enchantment` registry
- Particle validation against `Particle` registry
- Sound validation against `Sound` registry
- Effect validation against `PotionEffectType` registry
- Potion validation against `PotionType` registry
- Dimension validation against hardcoded list
- Recipe validation against `Bukkit.getRecipe()`
- Material validation using `Material.getMaterial()`
- Automatic cache initialization in plugin's `onEnable()`
- Fallback to pattern validation when server is offline
- Custom namespaces (non-minecraft) always allowed

#### Schema Metadata Support
- `$schema` - Schema dialect parsing
- `$id` - Schema identifier parsing
- `title` - Schema title parsing
- `description` - Schema description parsing
- `type` as array - Union types support

---

## [0.4.0] - 2026-03-22 - Full Schema Compliance

### Added

#### $ref Resolution with Full JSON Pointer Support
- Full JSON Pointer navigation by keyword/object/list
- Support for `definitions` and `$defs` with proper indexing
- Escaping support for `~0` (represents `~`) and `~1` (represents `/`)
- Indices support for `allOf`/`anyOf`/`oneOf` arrays
- `$id`-based indexing for external reference resolution

#### Array Constraints
- `minItems` — Minimum array length validation
- `maxItems` — Maximum array length validation  
- `uniqueItems` — Uniqueness constraint for array elements
- `prefixItems` — Tuple validation (2019-09/2020-12)
- `items` — Schema validation for array elements

#### Object Constraints
- `minProperties` — Minimum property count validation
- `maxProperties` — Maximum property count validation
- `dependencies` — Property and schema dependency modes
- `dependentRequired` — Required properties when dependency is present (2019-09+)
- `dependentSchemas` — Schema constraints when dependency is present (2019-09+)
- `additionalProperties` — Now supports both boolean and schema forms

#### Numeric Constraints
- `exclusiveMinimum`/`exclusiveMaximum` — Modern numeric form (2019-09/2020-12)
- Legacy boolean compatibility maintained for Draft-04/06/07

#### Metadata Support
- `$schema` — Schema dialect identification
- `$id` — Base URI and identification for reference resolution
- `title` — Schema title
- `description` — Schema description

#### Type System Enhancements
- Modern type array support (e.g., `["string", "null"]`)
- Runtime dispatch by actual data type
- Support for 2019-09/2020-12 keywords: `$defs`, `prefixItems`, `dependentRequired`, `dependentSchemas`

### Changed

- Updated `README.md` to reflect all implemented features
- Updated `schema-keywords.md` documentation with accurate status
- Updated `limitations-audit-195410.md` to reflect completed implementations

### Fixed

- Documentation now correctly reflects that all major JSON Schema features are implemented

---

## [0.3.5] - 2026-03-22

### Added

#### Conditional Validation
- **oneOf** — Data must match exactly ONE of the defined schemas
- **not** — Data must NOT match the specified schema
- **if/then/else** — Conditional validation based on schema matching

#### Format Validation
- 12 standard formats: email, uri, uri-reference, date-time, date, time, ipv4, ipv6, hostname, unix-time, json-pointer, relative-json-pointer
- 13 Minecraft-specific formats: minecraft-item, minecraft-block, minecraft-entity, minecraft-attribute, minecraft-effect, minecraft-enchantment, minecraft-biome, minecraft-dimension, minecraft-particle, minecraft-sound, minecraft-potion, minecraft-recipe, minecraft-tag

#### MultipleOf Validation
- Support for numeric divisibility constraints

#### Array & Object Constraints
- **minItems/maxItems/uniqueItems** — Array cardinality validation
- **minProperties/maxProperties** — Object property count validation

#### System Features
- **Supported Keywords Registry** — 39 keywords officially supported
- **Unsupported Keyword Detection** — Automatic warnings for invalid keywords
- **Fail-Fast Mode** — Optional strict validation via config

### Fixed

- Fixed detection of unsupported keywords in custom properties (FileSchemaLoader.java)
- Fixed validation of `type: number` to accept Integer values (PrimitiveValidator.java)
- Updated schema examples to use proper Minecraft namespaced IDs

---

## [0.3.0] - Previous Version

### Added

- Initial release with basic JSON Schema validation support
- YAML and JSON file validation
- Skript integration with `validate yaml/json` effects
- Basic type validation (string, number, integer, boolean, array, object, null)
- Basic property validation (properties, required, patternProperties)
- String constraints (minLength, maxLength, pattern, format)
- Numeric constraints (minimum, maximum, multipleOf)
- Composition keywords (allOf, anyOf)
- Conditional keywords (if, then, else)

---

## Upgrade Notes

### From 0.3.x to 0.4.0

Version 0.4.0 adds significant new functionality. If you're upgrading:

1. **$ref references** — You can now use full JSON Pointer references including `#/definitions/...` and `#/$defs/...`
2. **Array constraints** — `minItems`, `maxItems`, and `uniqueItems` are now enforced
3. **Object constraints** — `minProperties`, `maxProperties`, `dependencies`, `dependentRequired`, and `dependentSchemas` are now enforced
4. **Type arrays** — You can now use `type: ["string", "null"]` syntax for nullable types
5. **exclusiveMinimum/exclusiveMaximum** — Now supports both numeric (2019-09/2020-12) and boolean (legacy) formats

---

## Vocabulary Keywords (2019-09/2020-12)

The following keywords from newer JSON Schema drafts are now supported:

| Keyword | Draft | Description |
|---------|-------|-------------|
| `$defs` | 2019-09 | Schema definitions |
| `prefixItems` | 2019-09 | Tuple validation |
| `dependentRequired` | 2019-09 | Conditional required properties |
| `dependentSchemas` | 2019-09 | Conditional schema constraints |
| `$id` | 2019-09 | URI identification |
| `$schema` | 2019-09/2020-12 | Schema dialect |
| `$vocabulary` | 2019-09 | Vocabulary declaration |
| `$dynamicAnchor` | 2020-12 | Dynamic anchors |

---

*This changelog was generated as part of the documentation update on 2026-03-22.*
