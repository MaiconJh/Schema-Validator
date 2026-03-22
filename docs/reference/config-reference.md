# Reference: Config Keys

Schema-Validator reads these keys from `config.yml`.

| Key | Type | Bundled default | Fallback in code | Runtime effect |
| --- | --- | --- | --- | --- |
| `schema-directory` | string | `schemas` | `schemas` | Directory scanned on startup auto-load |
| `auto-load` | boolean | `true` | `true` | Enables startup schema loading |
| `cache-enabled` | boolean | `true` | `true` | Enables time-based cache eviction in registry |
| `validation-on-load` | boolean | `false` | `true` | Enables startup schema self-check |
| `strict-mode` | boolean | `false` | `false` | Fail-fast on unsupported keywords during parse |

## Additional Notes

- Registry cache expiry is hardcoded to 5 minutes in `SchemaValidatorPlugin` constructor wiring.
- Expired entries are removed on read in `SchemaRegistry.getSchema()`.

## Code Mapping

- Config file: `src/main/resources/config.yml`
- Parsing: `PluginConfig.load()`
- Startup wiring: `SchemaValidatorPlugin.onEnable()`
- Cache behavior: `SchemaRegistry`

---
Last updated: 2026-03-22  
Documentation version: 0.3.5
