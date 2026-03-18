# Frequently Asked Questions (FAQ)

Find answers to common questions about Schema Validator.

---

## General

### What is Schema Validator?

Schema Validator is a Minecraft plugin that validates YAML and JSON files using schemas (structure definitions). It's integrated with Skript to allow automatic validation in your scripts.

### What are the requirements?

- **PaperMC** or **Spigot** 1.21+
- **Skript** 2.9+
- Java 17 or higher

---

## Installation

### Where do I download the plugin?

Build the project using Gradle or get the JAR at `build/libs/`.

### How do I install it?

1. Download the `.jar` file
2. Place it in `plugins/`
3. Restart the server
4. Configure `config.yml`

---

## Usage

### How do I validate a YAML file?

```skript
validate yaml "myfile.yml" using schema "myschema.json"
```

### How do I validate a JSON file?

```skript
validate json "myfile.json" using schema "myschema.json"
```

### How do I get the errors?

```skript
set {_errors::*} to last schema validation errors
loop {_errors::*}:
    broadcast "%loop-value%"
```

---

## Schemas

### What data types are supported?

| Type | Description |
|------|-------------|
| `string` | Text |
| `integer` | Integer number |
| `number` | Decimal number |
| `boolean` | True/False |
| `array` | List of values |
| `object` | Object with properties |

### How to use enum?

```json
{
  "type": "string",
  "enum": ["warrior", "mage", "archer"]
}
```

### How to use minimum and maximum?

```json
{
  "type": "integer",
  "minimum": 1,
  "maximum": 100
}
```

### What is `required`?

Defines which fields are mandatory:

```json
{
  "type": "object",
  "required": ["name", "level"]
}
```

### What is `additionalProperties`?

Controls whether extra fields are allowed:

```json
{
  "type": "object",
  "additionalProperties": false
}
```

---

## Troubleshooting

### "Schema not found"

The schema file was not found. Check:
- The schema is in `plugins/Schema-Validator/schemas/`
- The name is correct (with extension)

### "File not found"

The data file was not found. Check:
- The file is in `plugins/Schema-Validator/`
- The path is correct

### "Validation failed"

The data doesn't match the schema. Use `last schema validation errors` to see the errors.

---

## Contact

Need more help? Get in touch!

---

[← Back](README.md) | [Previous: Configuration](configuration.md)
