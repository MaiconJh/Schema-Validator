# Schema-Validator v1.0.5

Schema-Validator is a Paper/Skript plugin that validates JSON or YAML data files against JSON Schema files.

> **Note:** If documentation and code diverge, the code is authoritative.

## 🎯 Version 1.0.5 Highlights

- **373 unit tests** passing across **23 test classes**
- **100% coverage** on all implemented validators
- Complete coverage for: array, object, string, number, logical, format and conditional validators

---

## 📚 Documentation

### Online Documentation

- **Full documentation:** [Schema Validator Docs](https://maiconjh.github.io/Schema-Validator/)
- **Quick start:** [docs/pages/index.md](docs/pages/index.md)

[![SkriptHubViewTheDocs](http://skripthub.net/static/addon/ViewTheDocsButton.png)](http://skripthub.net/docs/?addon=Schema-Validator)

### Reference Documentation

- [Schema keywords](docs/pages/schema-keywords.md)
- [Validation behavior](docs/pages/validation-behavior.md)
- [Architecture](docs/pages/architecture.md)
- [Developer guide](docs/pages/dev-guide.md)

---

## 🧪 Unit Test Suite

Version 1.0.5 introduces a complete test suite with **373 unit tests** organized across **23 test classes**, ensuring **100% coverage** on all implemented validators.

### Test Classes

| Category | Test Classes |
|----------|---------------|
| **Array Validators** | MinItemsValidatorTest, MaxItemsValidatorTest, UniqueItemsValidatorTest, PrefixItemsValidatorTest, AdditionalItemsValidatorTest |
| **Object Validators** | MinPropertiesValidatorTest, MaxPropertiesValidatorTest, DependentRequiredValidatorTest, DependentSchemasValidatorTest |
| **Conditional Validators** | ConditionalValidatorTest, OneOfValidatorTest, NotValidatorTest |
| **Format Validators** | FormatValidatorTest (21 formats) |
| **Primitive Validators** | PrimitiveValidatorTest |
| **Schema System** | SchemaTest, FileSchemaLoaderTest, SchemaRefResolverTest |

### Coverage by Validator

- **Array validators:** minItems, maxItems, uniqueItems, prefixItems, items, additionalItems
- **Object validators:** minProperties, maxProperties, dependencies, dependentRequired, dependentSchemas
- **String validators:** minLength, maxLength, pattern
- **Number validators:** minimum, maximum, exclusiveMinimum, exclusiveMaximum, multipleOf
- **Logical validators:** allOf, anyOf, oneOf, not
- **Format validators:** 21 formats (12 standard + 9 Minecraft-specific)
- **Conditional validators:** if/then/else
- **Misc validators:** const, enum, readOnly, writeOnly

---

## ✅ Supported JSON Schema Validators

### Array Keywords
- `minItems` — Minimum array length validation
- `maxItems` — Maximum array length validation
- `uniqueItems` — Uniqueness constraint for array elements
- `prefixItems` — Tuple validation (2019-09/2020-12)
- `items` — Schema validation for array elements
- `additionalItems` — Limited support

### Object Keywords
- `minProperties` — Minimum property count
- `maxProperties` — Maximum property count
- `dependencies` — Property and schema dependency modes
- `dependentRequired` — Required properties when dependency is present (2019-09+)
- `dependentSchemas` — Schema constraints when dependency is present (2019-09+)
- `properties` — Property definitions
- `patternProperties` — Properties with pattern matching
- `additionalProperties` — Supports boolean and schema forms
- `required` — Required property list

### String Keywords
- `minLength` — Minimum string length
- `maxLength` — Maximum string length
- `pattern` — Regex pattern
- `format` — Predefined format

### Number Keywords
- `minimum` / `maximum` — Numeric bounds
- `exclusiveMinimum` / `exclusiveMaximum` — Modern form (2019-09/2020-12) + legacy compatibility
- `multipleOf` — Multiple of value

### Logical Keywords
- `allOf` — All conditions must be valid
- `anyOf` — At least one condition must be valid
- `oneOf` — Exactly one condition must be valid
- `not` — Negation

### Conditional Keywords
- `if` — Primary condition
- `then` — Schema if condition is true
- `else` — Schema if condition is false

### Format Keywords
21 formats supported (12 standard + 9 Minecraft-specific):

**Standard Formats:**
- `date-time`, `date`, `time`, `duration`, `email`, `uri`, `uri-reference`, `uuid`, `hostname`, `ipv4`, `ipv6`, `json-pointer`

**Minecraft Formats:**
- `minecraft:entity` — Validation against EntityType registry
- `minecraft:biome` — Validation against Biome registry
- `minecraft:enchantment` — Validation against Enchantment registry
- `minecraft:particle` — Validation against Particle registry
- `minecraft:sound` — Validation against Sound registry
- `minecraft:effect` — Validation against PotionEffectType registry
- `minecraft:potion` — Validation against PotionType registry
- `minecraft:dimension` — Validation against list (overworld, nether, the_end)
- `minecraft:recipe` — Validation against Bukkit.getRecipe()

### Reference Keywords
- `$ref` — JSON Pointer reference resolution
- `definitions` / `$defs` — Schema definitions
- `$id` — Base URI for reference resolution
- `$schema` — Schema dialect identification

### Metadata Keywords
- `title` — Schema title
- `description` — Schema description
- `default` — Default value
- `examples` — Example values
- `readOnly` / `writeOnly` — Property constraints
- `deprecated` — Deprecation status

---

## 🔧 Installation and Configuration

### Installation

1. Download the latest release from the **Assets** section
2. Place the `.jar` file into your server's `/plugins` folder
3. Ensure **Skript** is already installed on your server
4. Restart the server

### File Structure

The plugin will create a `Schema-Validator/` folder in your plugins directory with:
- `schemas/` — Place your schema files here
- `config.yml` — Plugin settings

### Configuration

Edit `plugins/Schema-Validator/config.yml`:

```yaml
# Strict mode - rejects schemas with unsupported keywords
strict-mode: false

# Directory containing schema files
schema-directory: "plugins/Schema-Validator/schemas"

# Auto-load schemas on startup
auto-load: true

# Validate schemas on load
validation-on-load: true
```

---

## 🎮 Exposed Skript API

### Effects

```skript
# Validate a YAML file
validate yaml "playerdata/myplayer.yml" using schema "schemas/player-profile.schema.json"

# Validate a JSON file
validate json "config/settings.json" using schema "schemas/settings.schema.json"
```

### Expression

```skript
# Get errors from last validation
set {_errors::*} to last schema validation errors

if size of {_errors::*} is 0:
    broadcast "✓ Validation passed!"
else:
    broadcast "✗ Validation failed:"
    loop {_errors::*}:
        broadcast "- %loop-value%"
```

---

## 🏗 Project Structure

```
Schema-Validator/
├── src/main/java/com/maiconjh/schemacr/
│   ├── core/                    # Plugin lifecycle
│   │   ├── SchemaValidatorPlugin.java
│   │   ├── PluginContext.java
│   │   └── ValidationService.java
│   ├── config/                  # Configuration
│   │   └── PluginConfig.java
│   ├── schemes/                 # Schema system
│   │   ├── Schema.java
│   │   ├── SchemaRefResolver.java
│   │   ├── FileSchemaLoader.java
│   │   ├── SchemaRegistry.java
│   │   └── SupportedKeywordsRegistry.java
│   ├── validation/              # Validators
│   │   ├── ArrayValidator.java
│   │   ├── ObjectValidator.java
│   │   ├── PrimitiveValidator.java
│   │   ├── FormatValidator.java
│   │   ├── ConditionalValidator.java
│   │   ├── OneOfValidator.java
│   │   ├── NotValidator.java
│   │   ├── array/               # Array validators
│   │   ├── object/              # Object validators
│   │   └── misc/                # Miscellaneous validators
│   └── integration/             # Skript integration
│       ├── SkriptSyntaxRegistration.java
│       ├── EffValidateData.java
│       └── ExprLastValidationErrors.java
├── src/main/resources/
│   ├── schemas/                 # Built-in schemas
│   ├── examples/                # Usage examples
│   ├── config.yml
│   └── plugin.yml
├── src/test/java/               # Unit tests (373 tests)
├── docs/                        # Documentation
│   ├── pages/                   # Site documentation
│   └── explanation/             # Internal documentation
└── build/libs/                  # Compiled artifacts
```

### Main Components

| Component | Description |
|-----------|-------------|
| `SchemaValidatorPlugin` | Lifecycle (onEnable/onDisable) and startup orchestration |
| `PluginConfig` | Reads config keys and resolves schema directory path |
| `SchemaRegistry` | Case-insensitive schema storage with cache |
| `FileSchemaLoader` | JSON/YAML parsing, unsupported keyword detection |
| `ValidationService` | Facade over validator dispatch and result assembly |
| `Validators` | ObjectValidator, ArrayValidator, PrimitiveValidator, FormatValidator |
| `Skript bridge` | SkriptSyntaxRegistration, EffValidateData, ExprLastValidationErrors |

---

## 📋 Release Notes

### v1.0.5 - Complete Test Suite & Full Coverage

#### Major Features Added

1. **Advanced JSON Schema Validation Keywords**
   - Implementation of remaining P1/P2 keywords across parser and validators
   - Fix for keyword detection scope and array items validation
   - Implementation of P0 draft-2020-12 gaps: propertyNames, contains, and registry sync
   - Hardening of unevaluated and dynamicRef semantics with follow-up plan
   - Advance Phase H2 with dynamicRef and unevaluated regressions
   - Complete next hardening stage for dynamic scope and content vocab
   - Start next hardening stage for unevaluated applicator coverage

2. **Enhanced Documentation and User Experience**
   - Added star rating system with rate limiting (1-5 stars)
   - Improved documentation with UUID validation utility
   - Updated format-reference.md with corrected tables and examples
   - Enhanced getting-started.html and installation.html documentation
   - Added comprehensive examples for all JSON Schema features
   - Updated documentation to reflect implemented JSON Schema features
   - Removed GitHub discussions link from help-support
   - Updated help-support page with repository links

3. **Cloudflare Workers Integration**
   - Complete configuration of Cloudflare Workers with KV
   - Migrate rating storage from GitHub API to Cloudflare KV
   - Allow dots in page parameter validation for .html support
   - Improve privacy by removing userAgent/referrer storage and add data retention
   - Add User-Agent header for GitHub API
   - Add debug logs to worker
   - Add feedback system with Cloudflare Workers integration

4. **Bug Fixes and Stability Improvements**
   - Fix complex schema to resolve validation issues
   - Corrected FormatValidator regex patterns for RFC compliance
   - Fix: Updated worker's URL of feedback for production
   - Various documentation fixes and typo corrections
   - Reset feedbacks.json periodically for clean state

5. **Complete Unit Test Suite**
   - 373 unit tests across 23 test classes
   - 100% pass rate on all validators
   - Test classes for:
     - Array validators: MinItems, MaxItems, UniqueItems, PrefixItems, AdditionalItems
     - Object validators: MinProperties, MaxProperties, DependentRequired, DependentSchemas
     - Conditional validators: ConditionalValidator, OneOfValidator, NotValidator
     - Format validators: 21 formats (12 standard + 9 Minecraft-specific)
     - Primitive validators: Type, Enum, Numeric, String constraints
     - Misc validators: Const, ReadOnly, WriteOnly
     - Schema system: Schema, FileSchemaLoader, SchemaRefResolver

6. **Documentation Updates**
   - Test execution & evolution guide with detailed instructions
   - Test coverage report with comprehensive metrics
   - Updated architecture documentation with test coverage status
   - Updated schema-keywords documentation with verification status
   - Corrected code-audit-2026-03.md to reflect actual implementation

7. **Bug Fixes**
   - MinPropertiesValidator now handles null data correctly
   - MaxPropertiesValidator now handles null data correctly
   - ObjectValidator now handles non-Map data gracefully

---

## 🐞 Bug Reports & Contributions

Found a bug or have a feature request? Please open an issue on the GitHub repository:

🔗 **GitHub Issues:** https://github.com/MaiconJh/Schema-Validator/issues

---

## 📄 License

This project is licensed under the **MIT License**.

---

*Thank you for using Schema-Validator!*