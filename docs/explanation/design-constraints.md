# Explanation: Known Design Constraints

## 1. Global Last Result

`SkriptValidationBridge` keeps only one static `lastResult`. Parallel validations can overwrite each other.

## 2. Root Data Shape In Skript Path

`DataFileLoader.load(...)` deserializes to `Map<String, Object>`. Root arrays/primitives are not supported in this path.

## 3. `$ref` Is Path-Dependent

`SchemaRefResolver` exists, but `EffValidateData` uses `new ValidationService()` without resolver wiring.

## 4. Registry Cache Is Time-Based Eviction On Read

`SchemaRegistry.getSchema()` removes entries when expired. No background refresh or warming exists.

## 5. Keyword Registry Is Broader Than Enforcement

`SupportedKeywordsRegistry` lists many recognized keys, but runtime enforcement is limited to implemented validators.

## 6. Auto-Load Failure Summary Is Incomplete

`autoLoadSchemas()` logs `failedCount` but does not increment it.

## Practical Impact

When documenting or designing schemas, treat reference pages as the contract and treat unimplemented keywords as non-enforced until validators are extended.

---
Last updated: 2026-03-22  
Documentation version: 0.3.5
