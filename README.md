# Schema-Validator

Schema-Validator is a Paper/Skript add-on that validates JSON or YAML data files against a schema file from Skript code.

This documentation was reconstructed from source code in this repository (not from prior docs). If behavior in docs and code diverge, code is authoritative.

## Start here

- [Quickstart](docs/quickstart.md)
- [Installation](docs/installation.md)
- [Configuration](docs/configuration.md)
- [Guides](docs/guides/README.md)
- [Reference](docs/reference/README.md)
- [Explanation](docs/explanation/README.md)

## What the plugin actually exposes

- Skript effect: `validate yaml %string% using schema %string%`
- Skript effect: `validate json %string% using schema %string%`
- Skript expression: `last schema validation errors`
- Automatic schema loading from a configured folder during plugin startup
- Validation engine for object, array, and primitive schema types with composition/conditional keywords and selected constraints

## Runtime constraints you should know

- The Skript validation effect loads data files as `Map<String, Object>` roots; top-level arrays/scalars are not supported in that path.
- `$ref` resolution is implemented but the Skript effect path uses `new ValidationService()` (without resolver wiring), so `$ref` is not resolved there.
- Array size/uniqueness keywords (`minItems`, `maxItems`, `uniqueItems`) are recognized as supported keywords metadata, but are not enforced by `ArrayValidator`.

## Source mapping

1. Plugin bootstrap, auto-load, config wiring: `SchemaValidatorPlugin.onEnable()`, `autoLoadSchemas()`.  
2. Skript syntax registration: `SkriptSyntaxRegistration.register()`.  
3. Skript effect + expression behavior: `EffValidateData`, `ExprLastValidationErrors`, `SkriptValidationBridge`.  
4. Data loading root type: `DataFileLoader.load()` uses `TypeReference<Map<String, Object>>`.  
5. `$ref` mechanism and resolver-aware path: `ObjectValidator.validate()` + `ValidationService(SchemaRefResolver)` constructor.  
6. Array enforcement scope: `ArrayValidator.validate()`.

[← Previous](README.md) | [Next →](docs/README.md) | [Home](README.md)
