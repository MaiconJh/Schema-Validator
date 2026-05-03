## [1.5.0] - 2026-05-02
- Added async validation support, /sv validate-async, AsyncSchemaValidatorAPI, AsyncValidationCompleteEvent and async-validation config.

# Changelog - Schema-Validator

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.4.0] - 2026-04-08 - Schema Metadata, Export, and Stats

### Added

#### Runtime schema metadata
- Added per-schema registration metadata for source, source path, and registration timestamp
- Added selective file-backed schema reload support
- Added file-backed schema export command support

#### Metrics and commands
- Added runtime validation metrics for API, command, and Skript paths
- Added `/sv stats`
- Added `/sv export <schemaName> [json|yaml]`
- Added `/sv reload <schemaName>`

### Changed

- `list` and `info` now surface registration source metadata
- Updated command reference and architecture documentation for metrics and export behavior
- Updated project and documentation version to 1.4.0

---

## [1.3.0] - 2026-04-08 - Administrative Commands

### Added

#### Administrative command interface
- Added `/schemavalidator` with aliases `/sv` and `/schema`
- Added `help`, `list`, `info`, `validate-file`, and `reload --all` subcommands
- Added Bukkit permission nodes for read-only, reload, and admin access

#### Documentation
- Added a dedicated commands reference page in `docs/pages`
- Updated installation, configuration, architecture, and overview docs to include the command interface
- Updated legacy pointer documentation to version 1.3.0

### Changed

- Command-based reload now reapplies config-backed runtime settings before scanning the configured schema directory
- Schema directory load summaries now count failed files correctly
- Updated project and documentation version to 1.3.0

---

## [1.2.0] - 2026-04-08 - Optional Skript Runtime and Java API Documentation

### Added

#### Optional Skript Runtime
- Schema-Validator can now start without Skript installed
- Added guarded startup registration so Skript syntax is registered only when Skript is present

#### Documentation
- Added a dedicated Java API reference page in `docs/pages`
- Updated architecture, installation, getting-started, and overview pages to reflect the hybrid integration model
- Updated documentation pointers to the current documentation version

### Changed

- Changed `plugin.yml` so Skript is now an optional soft dependency instead of a required dependency
- Updated plugin description to reflect optional Skript integration and public Java/Bukkit usage
- Updated project and documentation version to 1.2.0

---

## [1.1.0] - 2026-04-08 - Public Java/Bukkit Integration API

### Added

#### Public Java/Bukkit API
- Added `SchemaValidatorAPI` as a stable static facade for other Bukkit plugins
- Added public validation result and validation error view interfaces
- Added safe schema lookup, batch validation, and schema registration helpers

### Changed

- Exposed `ValidationService` from the main plugin for programmatic consumers
- Updated README with Java/Bukkit integration guidance and usage examples
- Updated project and documentation version to 1.1.0

### Notes

- Programmatic access still depends on the `Schema-Validator` plugin being loaded
- Because this release still declares `depend: [Skript]`, Skript remains required at runtime

---

## [1.0.0] - 2026-03-24 - Complete Test Suite & Full Coverage

### Added

#### Unit Test Suite
- 373 unit tests across 23 validator test classes
- Test coverage for all JSON Schema validators:
  - Array validators: MinItems, MaxItems, UniqueItems, PrefixItems, AdditionalItems
  - Object validators: MinProperties, MaxProperties, DependentRequired, DependentSchemas
  - Conditional validators: ConditionalValidator, OneOfValidator, NotValidator
  - Format validators: FormatValidator with 21 formats (12 standard + 9 Minecraft)
  - Primitive validators: Type, Enum, Numeric, String constraints
  - Misc validators: Const, ReadOnly, WriteOnly
- Schema system tests: Schema, FileSchemaLoader, SchemaRefResolver
- Test fixtures for common validation scenarios

#### Documentation Updates
- Test execution & evolution guide
- Test coverage report with detailed metrics
- Updated code-audit-2026-03.md to reflect actual implementation
- Updated architecture.md with test coverage metrics
- Updated schema-keywords.md with test verification status

### Changed

- Updated plugin version to 1.0.0
- Documentation now reflects 100% test coverage for all validators
- Code audit document corrected to show implemented vs unimplemented features

### Fixed

- MinPropertiesValidator now handles null data correctly
- MaxPropertiesValidator now handles null data correctly
- ObjectValidator now handles non-Map data gracefully

---

## [1.0.5] - 2026-03-28 - Advanced JSON Schema Validation & Documentation

### Added

#### Advanced JSON Schema Validation Keywords
- Implementation of remaining P1/P2 keywords across parser and validators
- Fix for keyword detection scope and array items validation
- Implementation of P0 draft-2020-12 gaps: propertyNames, contains, and registry sync
- Hardening of unevaluated and dynamicRef semantics with follow-up plan
- Advance Phase H2 with dynamicRef and unevaluated regressions
- Complete next hardening stage for dynamic scope and content vocab
- Start next hardening stage for unevaluated applicator coverage

#### Enhanced Documentation and User Experience
- Added star rating system with rate limiting (1-5 stars)
- Improved documentation with UUID validation utility
- Updated format-reference.md with corrected tables and examples
- Enhanced getting-started.html and installation.html documentation
- Added comprehensive examples for all JSON Schema features
- Updated documentation to reflect implemented JSON Schema features
- Removed GitHub discussions link from help-support
- Updated help-support page with repository links

#### Cloudflare Workers Integration
- Complete configuration of Cloudflare Workers with KV
- Migrate rating storage from GitHub API to Cloudflare KV
- Allow dots in page parameter validation for .html support
- Improve privacy by removing userAgent/referrer storage and add data retention
- Add User-Agent header for GitHub API
- Add debug logs to worker
- Add feedback system with Cloudflare Workers integration

### Changed

- Updated plugin version to 1.0.5
- Updated documentation version to 1.0.5
- Updated plugin version range to 1.0.5

### Fixed

- Fix complex schema to resolve validation issues
- Corrected FormatValidator regex patterns for RFC compliance
- Fix: Atualiza URL do worker de feedback para produção
- Various documentation fixes and typo corrections
- Reset feedbacks.json periodically for clean state

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
