# Reference: Config

| Key | Type | Default | Effect |
|---|---|---:|---|
| `schema-directory` | string | `schemas` | Directory scanned for schema autoload on startup |
| `auto-load` | boolean | `true` | Enables startup schema scan and registration |
| `cache-enabled` | boolean | `true` | Enables expiry-based schema retrieval in registry |
| `validation-on-load` | boolean | `false` (bundled file) | Runs self-check validation on loaded schemas |
| `strict-mode` | boolean | `false` | Turns unsupported keyword warnings into parsing exceptions |

## Source mapping

1. Config defaults in resource file: `src/main/resources/config.yml`.  
2. Load/access methods: `PluginConfig.load()` and getters.  
3. Runtime usage: `SchemaValidatorPlugin.onEnable()`, `SchemaRegistry`, `FileSchemaLoader`.

[← Previous](examples-and-schema-construction.md) | [Next →](../explanation/README.md) | [Home](../../README.md)
