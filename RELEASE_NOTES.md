## ⚠️ Important Notice

This addon was created with **Artificial Intelligence assistance**. Use with caution and report any bugs found.


## 🎮 Compatibility

| Software | Version |
|----------|---------|
| Minecraft | 1.21+ |
| Java | 21 |
| Skript | 2.14+ |
| Server | Paper/Spigot 1.21+ |

## 📋 Dependencies

- [Skript 2.14+](https://github.com/SkriptLang/Skript)
- Paper 1.21+ ou Spigot 1.21+

## ✨ What's New in v0.3.5 - Minecraft Formats Update

### 🎯 Major Features Added

#### 1. Conditional Validation (oneOf, not, if/then/else)

Full JSON Schema conditional validation support:

- **oneOf** — Data must match exactly ONE of the defined schemas
- **not** — Data must NOT match the specified schema
- **if/then/else** — Conditional validation: if "if" condition is met, validate against "then"; otherwise validate against "else"

#### 2. Format Validation

Comprehensive format validators including 12 standard formats and 13 Minecraft-specific formats:

| Standard Formats | Minecraft Formats |
|-----------------|-------------------|
| email | minecraft-item |
| uri | minecraft-block |
| uri-reference | minecraft-entity |
| date-time | minecraft-attribute |
| date | minecraft-effect |
| time | minecraft-enchantment |
| ipv4 | minecraft-biome |
| ipv6 | minecraft-dimension |
| hostname | minecraft-particle |
| unix-time | minecraft-sound |
| json-pointer | minecraft-potion |
| relative-json-pointer | minecraft-recipe |
| | minecraft-tag |

#### 3. MultipleOf Validation

Support for numeric divisibility constraints:

```json
{
  "type": "number",
  "multipleOf": 0.5
}
```

#### 4. Array & Object Validation

- **minItems/maxItems/uniqueItems** — Array cardinality validation
- **minProperties/maxProperties** — Object property count validation

#### 5. System Features

- **Supported Keywords Registry** — 39 keywords officially supported
- **Unsupported Keyword Detection** — Automatic warnings for invalid keywords
- **Fail-Fast Mode** — Optional strict validation via config

---

## 🐛 Bug Fixes

| Fix | Description |
|------|-------------|
| **FileSchemaLoader.java** | Fixed detection of unsupported keywords in custom properties |
| **PrimitiveValidator.java** | Fixed validation of `type: number` to accept Integer values |
| **Schema examples** | Updated to use proper Minecraft namespaced IDs (e.g., `minecraft:diamond` instead of `diamond`) |

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
