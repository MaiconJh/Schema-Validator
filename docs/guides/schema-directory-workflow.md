# Guide: Work with auto-loaded schema directory

Use this when you want schemas loaded at plugin startup.

## Steps

1. Set `schema-directory` in `config.yml`.
2. Set `auto-load: true`.
3. Put `.json`, `.yml`, or `.yaml` files in that directory.
4. Restart/reload plugin.

## What happens on enable

- Plugin creates schema directory if missing.
- It loads matching schema files and registers each under filename without extension.
- If `validation-on-load` is enabled, plugin validates synthetic minimal data against each loaded schema and logs per-schema failures.

## Caveat

`failedCount` in auto-load summary log is currently never incremented; failed loads are logged individually as warnings.

## Source mapping

1. Startup auto-load control: `SchemaValidatorPlugin.onEnable()` + `PluginConfig.isAutoLoad()`.  
2. Directory creation and extension filtering: `SchemaValidatorPlugin.autoLoadSchemas()` + `loadSchemasFromDirectory()`.  
3. Registration naming strategy: `schemaName = fileName.substring(0, lastIndexOf('.'))`.  
4. Validation-on-load routine and minimal test data: `validateLoadedSchemas()`, `createMinimalTestData()`.

[← Previous](validate-json-file.md) | [Next →](../tutorials/README.md) | [Home](../../README.md)
