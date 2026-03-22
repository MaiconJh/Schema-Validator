# Guide: Use Schema Auto-Load Directory

Use this when schemas should be loaded automatically on plugin startup.

## Steps

1. Set `schema-directory` in `config.yml`.
2. Set `auto-load: true`.
3. Add schema files with `.json`, `.yml`, or `.yaml` extension.
4. Restart server and check startup logs.

## What Happens On Startup

- Missing schema directory is created.
- Files are loaded from that directory (non-recursive scan).
- Registered schema name is filename without extension.
- If `validation-on-load` is enabled, minimal test data is validated against loaded schemas.

## Known Limitation

`failedCount` is initialized but never incremented in `autoLoadSchemas()`. Failed loads are still logged per file.

## Code Mapping

- Auto-load entrypoint: `SchemaValidatorPlugin.autoLoadSchemas()`
- Directory scan: `SchemaValidatorPlugin.loadSchemasFromDirectory()`
- Validation-on-load: `SchemaValidatorPlugin.validateLoadedSchemas()`

---
Last updated: 2026-03-22  
Documentation version: 0.3.5
