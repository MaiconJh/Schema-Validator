# Configuration

Schema-Validator reads `plugins/Schema-Validator/config.yml`.

## Configuration Workflow

1. Set `schema-directory` for startup auto-load.
2. Choose if `auto-load` should run on startup.
3. Keep `cache-enabled` on unless you need immediate hard refresh behavior.
4. Enable `validation-on-load` only when you want startup self-checking.
5. Enable `strict-mode` to fail fast on unsupported keywords.

## Important Runtime Notes

- `schema-directory` is resolved under plugin data folder by `PluginConfig.getSchemaDirectory()`.
- The bundled config sets `validation-on-load: false`, but `PluginConfig` fallback default is `true` if the key is missing.
- `strict-mode` controls `FileSchemaLoader.setFailFastMode(...)`.

## Full Key Table

See [Reference: Config](reference/config-reference.md).

## Code Mapping

- Config parsing: `PluginConfig.load()`
- Startup wiring: `SchemaValidatorPlugin.onEnable()`
- Default values file: `src/main/resources/config.yml`

---
Last updated: 2026-03-22  
Documentation version: 0.3.5
