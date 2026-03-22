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
