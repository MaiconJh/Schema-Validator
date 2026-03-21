# Explanation: Runtime architecture

## Main execution path

1. Plugin enables.
2. Config is loaded.
3. Registry + schema loader are created.
4. Optional startup schema autoload runs.
5. Skript syntaxes are registered.
6. Static `PluginContext` is initialized for integration classes.

## Validation subsystems

- `FileSchemaLoader`: parses JSON/YAML schema documents into `Schema` model.
- `SchemaRegistry`: stores schemas by lowercase name with optional expiry behavior.
- `ValidationService`: dispatches validation by schema type and returns `ValidationResult`.
- Validators:
  - `ObjectValidator`
  - `ArrayValidator`
  - `PrimitiveValidator`

## Integration boundary

Skript integration is intentionally thin: it loads files, runs validation, then exposes the latest result through one global bridge.

## Source mapping

1. Plugin lifecycle + wiring: `SchemaValidatorPlugin`.  
2. Context holder: `PluginContext`.  
3. Integration classes: `EffValidateData`, `ExprLastValidationErrors`, `SkriptValidationBridge`.  
4. Core schema/validation classes in `schemes/` and `validation/` packages.

[← Previous](README.md) | [Next →](design-constraints.md) | [Home](../../README.md)
