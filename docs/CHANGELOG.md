# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [0.3.1-SNAPSHOT] - Unreleased

### ⚠️ WARNING: This is a pre-release version. Not recommended for production use.

### Added

#### Validation Features
- **Conditional Validation (oneOf, not, if/then/else)**
  - `oneOf` - Data must match exactly ONE of the defined schemas
  - `not` - Data must NOT match the specified schema
  - `if/then/else` - Conditional validation: if "if" condition is met, validate against "then"; otherwise validate against "else"

- **Format Validation**
  - `email` - Email addresses
  - `uri` - Full URIs
  - `uri-reference` - URI references (relative or absolute)
  - `date-time` - ISO 8601 datetime
  - `date` - ISO 8601 date (YYYY-MM-DD)
  - `time` - ISO 8601 time (HH:MM:SS)
  - `ipv4` - IPv4 addresses
  - `ipv6` - IPv6 addresses
  - `hostname` - Hostnames
  - `unix-time` - Unix timestamp (seconds since epoch)
  - `json-pointer` - JSON Pointer
  - `relative-json-pointer` - Relative JSON Pointer
  - **Minecraft Formats** (new in this session):
    - `minecraft-item` - Minecraft item IDs (namespace:name)
    - `minecraft-block` - Minecraft block IDs
    - `minecraft-entity` - Minecraft entity IDs
    - `minecraft-attribute` - Minecraft attribute IDs
    - `minecraft-effect` - Minecraft effect IDs
    - `minecraft-enchantment` - Minecraft enchantment IDs
    - `minecraft-biome` - Minecraft biome IDs
    - `minecraft-dimension` - Minecraft dimension IDs
    - `minecraft-particle` - Minecraft particle IDs
    - `minecraft-sound` - Minecraft sound IDs
    - `minecraft-potion` - Minecraft potion IDs
    - `minecraft-recipe` - Minecraft recipe IDs
    - `minecraft-tag` - Minecraft tag IDs

- **multipleOf Validation**
  - Support for validating multiples in numeric types

#### New Example Files
- `schemas/conditional-validation.schema.json` - Demonstrates oneOf, not, if/then/else
- `schemas/data-types-formats.schema.json` - Demonstrates formats and multipleOf
- `schemas/complex-item.schema.json` - Complex schema combining multiple features

### Fixed
- **FileSchemaLoader.java** - Fixed detection of unsupported keywords to properly recognize custom properties defined in `properties` and `patternProperties`
- **PrimitiveValidator.java** - Fixed validation of `type: number` to accept Integer values (was rejecting integers)
- Schema examples updated to use proper Minecraft namespaced IDs (e.g., `minecraft:diamond` instead of `diamond`)

### Changed
- Updated `custom-block.schema.json` to use Minecraft format validators
- Improved error messages for format validation failures
- Enhanced documentation with new Minecraft formats guide

### Technical Changes

#### New Classes
- `OneOfValidator.java` - Validator for oneOf
- `NotValidator.java` - Validator for not
- `ConditionalValidator.java` - Validator for if/then/else
- `FormatValidator.java` - Format validator

#### Modified Files
- `Schema.java` - Added fields and methods for oneOf, not, if/then/else
- `FileSchemaLoader.java` - Added parsing for new keywords
- `ObjectValidator.java` - Added conditional validation logic
- `PrimitiveValidator.java` - Added format validation

---

## [0.3.0] - 2026-03-XX

### Added
- Initial release features
- Basic JSON Schema validation support
- YAML support
- Skript integration
- Schema auto-loading system

---

## Older Versions

For older versions, please refer to the commit history.

---

## Upgrading

### Upgrading from 0.3.0 to 0.3.1

1. Update the plugin JAR file
2. Review your schemas for any deprecated features
3. Test thoroughly in development environment before production deployment

---

## Known Issues

- Pre-release version may contain bugs or incomplete features
- Some advanced JSON Schema features not yet implemented
- Documentation still being expanded

---

## Migration Guides

See [MIGRATION.md](MIGRATION.md) for detailed migration instructions between versions.

---

*This changelog was created as part of documentation consolidation on 2026-03-20*
