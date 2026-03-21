# Configuration

Schema-Validator reads `plugins/Schema-Validator/config.yml`.

## Keys

- `schema-directory` (default: `schemas`)
- `auto-load` (default: `true`)
- `cache-enabled` (default: `true`)
- `validation-on-load` (default: `false` in bundled config)
- `strict-mode` (default: `false`)

## Behavior notes

- `schema-directory` is resolved relative to plugin data folder via `Paths.get(pluginDataFolder, schema-directory)`.
- `strict-mode` toggles FileSchemaLoader fail-fast handling for unsupported keywords.
- `cache-enabled` controls expiration behavior in `SchemaRegistry` lookups.

## Source mapping

1. Default values and comments: `src/main/resources/config.yml`.  
2. Parsing and getters: `PluginConfig`.  
3. Loader strict wiring: `SchemaValidatorPlugin.onEnable()`, `FileSchemaLoader.setFailFastMode()`.  
4. Registry cache behavior: `SchemaRegistry.getSchema()`.

[← Previous](installation.md) | [Next →](guides/README.md) | [Home](../README.md)
