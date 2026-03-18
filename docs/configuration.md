# Configuration

Learn how to configure Schema Validator for your needs.

## config.yml

The configuration file is automatically generated at `plugins/Schema-Validator/config.yml`:

```yaml
# Schema Validator Settings
settings:
  # Enable cache for loaded schemas
  cache-enabled: true
  
  # Cache expiry time in milliseconds (default: 1 hour)
  cache-expiry: 3600000
  
  # Default folder for schemas
  schemas-folder: "schemas"
  
  # Default folder for examples
  examples-folder: "examples"
```

## Detailed Settings

### cache-enabled

| Value | Behavior |
|-------|----------|
| `true` (default) | Schemas are cached for better performance |
| `false` | Schemas are reloaded on every validation |

### cache-expiry

Time a schema stays in cache before being invalidated.

| Value | Time |
|-------|------|
| `3600000` | 1 hour |
| `1800000` | 30 minutes |
| `60000` | 1 minute |

## Folder Structure

```
plugins/
└── Schema-Validator/
    ├── config.yml           # Plugin configuration
    ├── schemas/             # Your schemas
    │   ├── player.schema.json
    │   └── custom-block.schema.json
    └── examples/            # Examples
        ├── player-example.yml
        └── custom-block-example.yml
```

## Creating Schemas

### Location

Place your schemas in `plugins/Schema-Validator/schemas/`.

### Supported Extensions

- `.json` - JSON Schema
- `.yml` / `.yaml` - YAML Schema

### Example: Player Schema

```json
{
  "type": "object",
  "properties": {
    "name": { "type": "string" },
    "level": { "type": "integer", "minimum": 1 },
    "class": { "type": "string", "enum": ["warrior", "mage"] }
  },
  "required": ["name", "level"],
  "additionalProperties": false
}
```

---

[← Back](README.md) | [Previous: Quick Start](quickstart.md) | [Next: FAQ →](faq.md)
