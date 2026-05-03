## 1.5.0 - 2026-05-02
- Async validation with configurable pool and queue.
- New command: /sv validate-async <arquivo> <schema> [--verbose].

## ⚠️ Important Notice

This addon was created with **Artificial Intelligence assistance**. Use with caution and report any bugs found.


## 🎮 Compatibility

| Software | Version |
|----------|---------|
| Minecraft | 1.21+ |
| Java | 21 |
| Skript | Optional, 2.14+ |
| Server | Paper/Spigot 1.21+ |

## 📋 Dependencies

- [Skript 2.14+](https://github.com/SkriptLang/Skript) (optional, only for Skript syntax)
- Paper 1.21+ ou Spigot 1.21+

---

## What's New in v1.4.0 - Schema Metadata and Advanced Admin Commands

### Major Features Added

#### 1. Runtime Schema Metadata
- Added source tracking for registered schemas
- Added source path and registration timestamp tracking for file-backed schemas
- Added selective reload support for one schema at a time when a file-backed source exists

#### 2. Advanced Administrative Commands
- Added `/sv export <schemaName> [json|yaml]`
- Added `/sv stats`
- Added `/sv reload <schemaName>`
- Expanded `list` and `info` output with registration metadata

#### 3. Validation Metrics
- Added tracked validation counters for API, command, and Skript flows
- Added average validation timing to the command stats output
- Kept self-validation and unrelated direct service usage outside the built-in counters

---

## What's New in v1.3.0 - Administrative Commands

### Major Features Added

#### 1. Administrative Command Interface
- Added `/schemavalidator` with aliases `/sv` and `/schema`
- Added `help`, `list`, `info`, `validate-file`, and `reload --all`
- Added permission nodes for read-only, reload, and full administrative access

#### 2. Operational Validation and Reload
- Added file-based validation directly from commands for JSON and YAML payloads
- Added command-driven schema directory reload without requiring a full server restart
- Reload now reapplies config-backed runtime settings before scanning the directory

#### 3. Documentation Expansion
- Added a dedicated `docs/pages/commands.md` reference page
- Updated installation, configuration, overview, and architecture docs to reflect the new administrative surface
- Updated deprecated pointer docs to documentation version `1.3.0`

---

## What's New in v1.2.0 - Optional Skript Runtime

### Major Features Added

#### 1. Optional Skript Integration at Runtime
- Removed the hard runtime requirement on Skript from `plugin.yml`
- Schema-Validator now starts normally even when Skript is not installed
- Skript syntax registration now runs only when Skript is present and enabled

#### 2. Documentation Expansion
- Added a dedicated `docs/pages/java-api.md` reference page
- Updated architecture, installation, overview, and getting-started docs for the hybrid Java/Bukkit plus Skript model
- Updated legacy pointer docs to the current documentation version

#### 3. Integration Model Cleanup
- Updated plugin metadata to describe the project as a Paper plugin with optional Skript integration
- Kept existing Skript behavior intact when Skript is installed

---

## What's New in v1.1.0 - Public Java/Bukkit API

### Major Features Added

#### 1. Public Java/Bukkit Integration API
- Added `SchemaValidatorAPI` for safe access from other Bukkit plugins
- Added stable public views for validation results and validation errors
- Added facade methods for single validation, batch validation, schema presence checks, schema listing, and file-based schema registration

#### 2. Plugin Service Exposure
- Exposed `ValidationService` from the main plugin to support programmatic validation without changing the existing Skript flow
- Kept current Skript behavior intact while adding the new integration layer

#### 3. Documentation Updates
- Added Java/Bukkit API usage guidance to the main README
- Documented the current runtime limitation that Skript is still required because the plugin keeps `depend: [Skript]`

---

## ✨ What's New in v1.0.5 - Complete Test Suite & Full Coverage

### 🎯 Major Features Added

#### 1. Advanced JSON Schema Validation Keywords
- Implementation of remaining P1/P2 keywords across parser and validators
- Fix for keyword detection scope and array items validation
- Implementation of P0 draft-2020-12 gaps: propertyNames, contains, and registry sync
- Hardening of unevaluated and dynamicRef semantics with follow-up plan
- Advance Phase H2 with dynamicRef and unevaluated regressions
- Complete next hardening stage for dynamic scope and content vocab
- Start next hardening stage for unevaluated applicator coverage

#### 2. Enhanced Documentation and User Experience
- Added star rating system with rate limiting (1-5 stars)
- Improved documentation with UUID validation utility
- Updated format-reference.md with corrected tables and examples
- Enhanced getting-started.html and installation.html documentation
- Added comprehensive examples for all JSON Schema features
- Updated documentation to reflect implemented JSON Schema features
- Removed GitHub discussions link from help-support
- Updated help-support page with repository links

#### 3. Cloudflare Workers Integration
- Complete configuration of Cloudflare Workers with KV
- Migrate rating storage from GitHub API to Cloudflare KV
- Allow dots in page parameter validation for .html support
- Improve privacy by removing userAgent/referrer storage and add data retention
- Add User-Agent header for GitHub API
- Add debug logs to worker
- Add feedback system with Cloudflare Workers integration

#### 4. Bug Fixes and Stability Improvements
- Fix complex schema to resolve validation issues
- Corrected FormatValidator regex patterns for RFC compliance
- Fix: Updated worker's URL of feedback for production
- Various documentation fixes and typo corrections
- Reset feedbacks.json periodically for clean state

---

### 🎯 Major Features Added

#### 1. Complete Unit Test Suite

Comprehensive test coverage for all JSON Schema validators:

- **373 unit tests** across **23 test classes**
- **100% pass rate** on all validators
- Test classes for:
  - Array validators: MinItems, MaxItems, UniqueItems, PrefixItems, AdditionalItems
  - Object validators: MinProperties, MaxProperties, DependentRequired, DependentSchemas
  - Conditional validators: ConditionalValidator, OneOfValidator, NotValidator
  - Format validators: 21 formats (12 standard + 9 Minecraft-specific)
  - Primitive validators: Type, Enum, Numeric, String constraints
  - Misc validators: Const, ReadOnly, WriteOnly
  - Schema system: Schema, FileSchemaLoader, SchemaRefResolver

#### 2. Documentation Updates

- Test execution & evolution guide with detailed instructions
- Test coverage report with comprehensive metrics
- Updated architecture documentation with test coverage status
- Updated schema-keywords documentation with verification status
- Corrected code-audit-2026-03.md to reflect actual implementation

#### 3. Bug Fixes

- MinPropertiesValidator now handles null data correctly
- MaxPropertiesValidator now handles null data correctly
- ObjectValidator now handles non-Map data gracefully

---

## ✨ What's New in v0.5.0 - Semantic Minecraft Validation

### 🎯 Major Features Added

#### 1. Semantic Validation for Minecraft Formats

Full semantic validation using real Bukkit registries when the server is running:

- **Entity validation** - Validates against `EntityType` registry
- **Biome validation** - Validates against `Biome` registry
- **Enchantment validation** - Validates against `Enchantment` registry
- **Particle validation** - Validates against `Particle` registry
- **Sound validation** - Validates against `Sound` registry
- **Effect validation** - Validates against `PotionEffectType` registry
- **Potion validation** - Validates against `PotionType` registry
- **Dimension validation** - Validates against hardcoded list (overworld, nether, the_end)
- **Recipe validation** - Validates against `Bukkit.getRecipe()`
- **Material validation** - Validates items/blocks using `Material.getMaterial()`

#### 2. Schema Metadata Support

Added support for JSON Schema metadata keywords:

- **`$schema`** - Schema dialect (e.g., "https://json-schema.org/draft/2020-12/schema")
- **`$id`** - Schema identifier
- **`title`** - Schema title
- **`description`** - Schema description
- **`type` as array** - Union types support (e.g., ["string", "null"])

---

## ✨ What's New in v0.4.0 - Full Schema Compliance

### 🎯 Major Features Added

#### 1. $ref Resolution with Full JSON Pointer Support

Complete JSON Pointer reference resolution with support for:

- **Full JSON Pointer navigation** — Navigate by keyword/object/list
- **definitions/$defs support** — Schema definitions with proper indexing
- **~0/~1 escaping** — Escape sequences for `~` and `/` characters
- **Indices support** — Reference items in allOf/anyOf/oneOf arrays
- **$id-based indexing** — External reference resolution

```json
{
  "$defs": {
    "Address": {
      "type": "object",
      "properties": {
        "street": {"type": "string"},
        "city": {"type": "string"}
      }
    }
  },
  "properties": {
    "billing_address": {"$ref": "#/$defs/Address"},
    "shipping_address": {"$ref": "#/$defs/Address"}
  }
}
```

#### 2. Array Constraints

| Feature | Description |
|---------|-------------|
| **minItems** | Minimum array length validation |
| **maxItems** | Maximum array length validation |
| **uniqueItems** | Uniqueness constraint for array elements |
| **prefixItems** | Tuple validation (2019-09/2020-12) |
| **items** | Schema validation for array elements |

#### 3. Object Constraints

| Feature | Description |
|---------|-------------|
| **minProperties** | Minimum property count validation |
| **maxProperties** | Maximum property count validation |
| **dependencies** | Property and schema dependency modes |
| **dependentRequired** | Required properties when dependency present (2019-09+) |
| **dependentSchemas** | Schema constraints when dependency present (2019-09+) |
| **additionalProperties** | Now supports both boolean and schema forms |

#### 4. Exclusive Minimum/Maximum

- **Modern numeric form** — Per 2019-09/2020-12 specification
- **Legacy boolean compatibility** — Maintained for Draft-04/06/07

```json
{
  "type": "number",
  "exclusiveMinimum": 0,
  "exclusiveMaximum": 100
}
```

#### 5. Metadata Modeling

| Keyword | Description |
|---------|-------------|
| **$schema** | Schema dialect identification |
| **$id** | Base URI for reference resolution |
| **title** | Schema title |
| **description** | Schema description |

#### 6. Modern Type Array Support

Runtime dispatch by actual data type with support for nullable types:

```json
{
  "type": ["string", "null"],
  "description": "A string that can also be null"
}
```

#### 7. 2019-09/2020-12 Keywords

| Keyword | Draft | Description |
|---------|-------|-------------|
| **$defs** | 2019-09 | Schema definitions |
| **prefixItems** | 2019-09 | Tuple validation |
| **dependentRequired** | 2019-09 | Conditional required properties |
| **dependentSchemas** | 2019-09 | Conditional schema constraints |

---

## 🐛 Bug Fixes

| Fix | Description |
|------|-------------|
| ✅ **Documentation Updated** | All documentation now reflects implemented features |
| ✅ **Schema Keywords** | Fixed status of all previously marked as "not enforced" |
| ✅ **Limitations Audit** | Updated to show completed implementations |

---

## 📦 Installation

1. Download the latest release from the **Assets** section below
2. Place the `.jar` file into your server's `/plugins` folder
3. Ensure **Skript** is already installed on your server
4. Restart the server

The plugin will create a `Schema-Validator/` folder in your plugins directory with:
- `schemas/` — Place your schema files here
- `config.yml` — Plugin settings

---

## 🧪 Basic Usage (Skript)

### Validate a YAML file

```skript
validate yaml "playerdata/myplayer.yml" using schema "schemas/player-profile.schema.json"
```

### Validate a JSON file

```skript
validate json "config/settings.json" using schema "schemas/settings.schema.json"
```

### Check for validation errors

```skript
set {_errors::*} to last schema validation errors

if size of {_errors::*} is 0:
    broadcast "✓ Validation passed!"
else:
    broadcast "✗ Validation failed:"
    loop {_errors::*}:
        broadcast "- %loop-value%"
```

---

## 🐞 Bug Reports & Contributions

Found a bug or have a feature request? Please open an issue on the GitHub repository:

🔗 **GitHub Issues:** https://github.com/MaiconJh/Schema-Validator/issues

---

## 📄 License

This project is licensed under the **MIT License**.

---

*Thank you for trying Schema-Validator!*
