# Explanation: Known design constraints

## 1) Last validation result is global

`SkriptValidationBridge` stores one static volatile result. Concurrent validations overwrite prior results.

## 2) `$ref` support is path-dependent

Resolver infrastructure exists (`SchemaRefResolver`), but effect-based validation path currently constructs `ValidationService()` without resolver.

## 3) Registry cache expires by deletion

When cache is enabled and a schema is older than expiry, `getSchema()` removes it and returns empty.

## 4) Unsupported keyword semantics

Keyword support metadata in `SupportedKeywordsRegistry` is broader than current enforcement. Some listed keywords are only recognized for warning/fail-fast filtering.

## Source mapping

1. Global result bridge: `SkriptValidationBridge`.  
2. Effect path and service constructor usage: `EffValidateData.execute()`, `ValidationService`.  
3. Expiry behavior: `SchemaRegistry.getSchema()`.  
4. Keyword handling: `SupportedKeywordsRegistry`, `FileSchemaLoader.detectUnsupportedKeywords()`, validators.

[← Previous](architecture.md) | [Next →](documentation-audit.md) | [Home](../../README.md)
