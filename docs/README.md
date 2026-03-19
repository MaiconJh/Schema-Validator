# Documentation

Schema Validator plugin documentation for Minecraft with YAML/JSON validation and Skript integration.

---

## Table of Contents

### Getting Started

- [Installation](installation.md) - How to install the plugin
- [Quick Start](quickstart.md) - 5-minute tutorial

### Configuration

- [Configuration](configuration.md) - config.yml options
- [FAQ](faq.md) - Frequently asked questions

### Core Documentation

- [Architecture](architecture.md) - Internal validation flow
- [API Reference](api-reference.md) - Complete API documentation
- [Schema Construction](construction.md) - How to build valid schemas

### Guides

- [Integration Guide](guides/integration.md) - Integrate into your projects

### Reference

- [Skript Syntax](reference/skript-syntax.md) - Commands and expressions
- [JSON Schema](reference/json-schema.md) - Supported keywords
- [Data Types](reference/data-types.md) - Primitive and compound types
- [Schema Composition](reference/schema-composition.md) - allOf and anyOf

---

## Quick Links

| Resource | Description |
|----------|-------------|
| [Quick Start](quickstart.md) | Learn in 5 minutes |
| [Skript Syntax](reference/skript-syntax.md) | Complete reference |
| [JSON Schema](reference/json-schema.md) | All keywords |
| [Tutorials](tutorials/README.md) | Practical examples |

---

## File Structure

```
docs/
├── README.md              # This file
├── installation.md        # Installation guide
├── quickstart.md         # Quick tutorial
├── configuration.md      # Configuration options
├── faq.md                # Frequently asked questions
├── tutorials/
│   ├── README.md
│   ├── player-data-validation.md
│   ├── custom-blocks.md
│   └── inventory-validation.md
└── reference/
    ├── skript-syntax.md
    ├── json-schema.md
    └── data-types.md
```

---

## Examples

### Validate YAML file

```skript
validate yaml "myfile" using schema "schema.json"
set {_errors::*} to last schema validation errors

if size of {_errors::*} is 0:
    broadcast "Valid!"
else:
    loop {_errors::*}:
        broadcast "Error: %loop-value%"
```

### Simple Schema

```json
{
  "type": "object",
  "properties": {
    "name": { "type": "string" },
    "level": { "type": "integer", "minimum": 1 }
  },
  "required": ["name", "level"]
}
```

---

## Next Steps

1. Install the plugin
2. Do the [Quick Start](quickstart.md)
3. Read the [Skript Reference](reference/skript-syntax.md)

---

## Support

- Questions? See the [FAQ](faq.md)
- Found a bug? Open an issue
- Have a suggestion? Contribute!

---

[← Back to Home](../README.md)
