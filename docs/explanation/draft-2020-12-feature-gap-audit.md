# Schema-Validator v1.0.0 — JSON Schema Draft 2020-12 Feature Gap Audit

## Scope

This audit compares the current implementation against Draft 2020-12 vocabularies:

- Core
- Applicator
- Validation
- Unevaluated
- Meta-data
- Format (annotation)
- Content

Reference keyword lists were taken from the official Draft 2020-12 vocabulary meta-schemas.

Implementation execution companion:
- `docs/explanation/draft-2020-12-implementation-plan-prompt-and-guidance.md`

---

## Executive summary

- **Total Draft 2020-12 vocabulary keywords audited:** 57
- **Fully implemented:** 31
- **Partially implemented:** 4
- **Declared in registry but effectively not implemented:** 5
- **Missing from registry and implementation:** 17

### Highest-impact gaps

1. `propertyNames`
2. `contains`, `minContains`, `maxContains`
3. `unevaluatedProperties`, `unevaluatedItems`
4. `$dynamicRef` / dynamic scope support
5. Registry/implementation drift (`prefixItems`, `dependentRequired`, `dependentSchemas`, `$comment`)

---

## Detailed keyword matrix

Legend:

- **Registry:** present in `SupportedKeywordsRegistry`
- **Implementation:**
  - ✅ Implemented
  - ⚠️ Partial
  - ❌ Missing

| Vocabulary | Keyword | Registry | Implementation | Notes |
|---|---|---:|---:|---|
| Core | `$schema` | Yes | ✅ | Parsed and stored as schema dialect metadata. |
| Core | `$id` | Yes | ✅ | Parsed and stored. |
| Core | `$ref` | Yes | ✅ | Parsed and resolved in validators. |
| Core | `$defs` | No | ⚠️ | Supported in `load(...)`, but not in `parseSchema(...)`; no registry entry. |
| Core | `$anchor` | No | ❌ | Not parsed/resolved. |
| Core | `$dynamicRef` | No | ❌ | Not parsed/resolved. |
| Core | `$dynamicAnchor` | Yes | ❌ | Declared only; no dynamic resolution behavior. |
| Core | `$vocabulary` | Yes | ❌ | Declared only; no behavior. |
| Core | `$comment` | No (`comment` exists) | ❌ | Wrong keyword in registry (`comment` instead of `$comment`). |
| Applicator | `allOf` | Yes | ✅ | Implemented in object validator. |
| Applicator | `anyOf` | Yes | ✅ | Implemented in object validator. |
| Applicator | `oneOf` | Yes | ✅ | Implemented in object validator. |
| Applicator | `not` | Yes | ✅ | Implemented in object validator. |
| Applicator | `if` | Yes | ✅ | Implemented in object validator. |
| Applicator | `then` | Yes | ✅ | Implemented in object validator. |
| Applicator | `else` | Yes | ✅ | Implemented in object validator. |
| Applicator | `properties` | Yes | ✅ | Implemented in object validator. |
| Applicator | `patternProperties` | Yes | ✅ | Implemented in object validator. |
| Applicator | `additionalProperties` | Yes | ✅ | Supports boolean and schema form. |
| Applicator | `propertyNames` | No | ❌ | Not parsed and not validated. |
| Applicator | `dependentSchemas` | No | ✅ | Implemented but missing from registry (false unsupported warnings). |
| Applicator | `prefixItems` | No | ✅ | Implemented but missing from registry (false unsupported warnings). |
| Applicator | `items` | Yes | ⚠️ | Works for single schema; limited interplay with `prefixItems`/tail semantics. |
| Applicator | `contains` | No | ❌ | Not implemented. |
| Validation | `type` | Yes | ✅ | Supports single and list type declarations. |
| Validation | `enum` | Yes | ✅ | Implemented. |
| Validation | `const` | Yes | ✅ | Implemented via dedicated validator. |
| Validation | `multipleOf` | Yes | ✅ | Implemented. |
| Validation | `maximum` | Yes | ✅ | Implemented. |
| Validation | `exclusiveMaximum` | Yes | ✅ | Implemented (boolean mode). |
| Validation | `minimum` | Yes | ✅ | Implemented. |
| Validation | `exclusiveMinimum` | Yes | ✅ | Implemented (boolean mode). |
| Validation | `maxLength` | Yes | ✅ | Implemented. |
| Validation | `minLength` | Yes | ✅ | Implemented. |
| Validation | `pattern` | Yes | ✅ | Implemented. |
| Validation | `maxItems` | Yes | ✅ | Implemented. |
| Validation | `minItems` | Yes | ✅ | Implemented. |
| Validation | `uniqueItems` | Yes | ✅ | Implemented. |
| Validation | `maxContains` | No | ❌ | Not implemented. |
| Validation | `minContains` | No | ❌ | Not implemented. |
| Validation | `maxProperties` | Yes | ✅ | Implemented. |
| Validation | `minProperties` | Yes | ✅ | Implemented. |
| Validation | `required` | Yes | ✅ | Implemented. |
| Validation | `dependentRequired` | No | ✅ | Implemented but missing from registry (false unsupported warnings). |
| Unevaluated | `unevaluatedProperties` | No | ❌ | Not implemented. |
| Unevaluated | `unevaluatedItems` | No | ❌ | Not implemented. |
| Meta-data | `title` | Yes | ✅ | Parsed/stored metadata. |
| Meta-data | `description` | Yes | ✅ | Parsed/stored metadata. |
| Meta-data | `default` | Yes | ❌ | Declared, but not parsed into model. |
| Meta-data | `deprecated` | Yes | ❌ | Declared, but not parsed/used. |
| Meta-data | `readOnly` | Yes | ✅ | Implemented validator behavior. |
| Meta-data | `writeOnly` | Yes | ✅ | Implemented validator behavior. |
| Meta-data | `examples` | Yes | ❌ | Declared, but not parsed/used. |
| Format | `format` | Yes | ✅ | Implemented including custom Minecraft-focused formats. |
| Content | `contentEncoding` | No | ❌ | Not implemented. |
| Content | `contentMediaType` | No | ❌ | Not implemented. |
| Content | `contentSchema` | No | ❌ | Not implemented. |

---

## Functional behavior for unsupported keywords

Current loader behavior:

1. **Unknown non-`$` keyword + fail-fast disabled** → warning and continue.
2. **Unknown non-`$` keyword + fail-fast enabled** → throws `IllegalArgumentException`.
3. **Any `$...` keyword** → currently skipped in unsupported-keyword detection logic (no warning/fail-fast), even when not implemented.
4. **Implemented keywords missing in registry** (e.g., `prefixItems`) → false warnings despite working implementation.

---

## Priority roadmap

### P0 (High)

1. `propertyNames`
2. `contains`, `minContains`, `maxContains`
3. Registry synchronization:
   - add: `prefixItems`, `dependentRequired`, `dependentSchemas`, `$defs`, `$comment`
   - remove/deprecate: `dependencies`, `comment`, `definitions` (or map explicitly as legacy aliases)

### P1 (Medium)

1. `unevaluatedProperties`, `unevaluatedItems`
2. `$dynamicRef`, `$dynamicAnchor` dynamic scope behavior
3. Content vocabulary (`contentEncoding`, `contentMediaType`, `contentSchema`)

### P2 (Low)

1. Full annotation handling for `default`, `examples`, `deprecated`
2. Optional support for additional Draft 2020-12 annotation vocabularies and output formats

---

## Proposed repository issues/enhancements

1. **Add `propertyNames` validator and parser support**
   - Acceptance criteria: invalid key names fail validation with precise path.

2. **Implement `contains` + `minContains` + `maxContains` in array validator**
   - Acceptance criteria: arrays enforce match counts per spec.

3. **Fix SupportedKeywordsRegistry drift with real implementation**
   - Acceptance criteria: no false unsupported warnings for implemented keywords.

4. **Implement `unevaluatedProperties` and `unevaluatedItems` tracking**
   - Acceptance criteria: evaluated-property/item bookkeeping across applicators.

5. **Add `$dynamicRef` / `$dynamicAnchor` resolution strategy**
   - Acceptance criteria: dynamic scope chain respected in nested references.

6. **Implement content vocabulary (`contentEncoding`, `contentMediaType`, `contentSchema`)**
   - Acceptance criteria: optional annotation mode and assertive mode available.

7. **Metadata parity task (`default`, `examples`, `deprecated`)**
   - Acceptance criteria: model stores annotations and exposes them via APIs.

---

## Implementation hints (top gaps)

### 1) `propertyNames`

- **Model**: add `Schema propertyNamesSchema` to `Schema`.
- **Loader**: parse `propertyNames` object into `propertyNamesSchema`.
- **Validator**: in `ObjectValidator`, validate each key string against `propertyNamesSchema` using `PrimitiveValidator` with path like `$.<key>` and keyword `propertyNames`.

### 2) `contains` / `minContains` / `maxContains`

- **Model**: add `Schema containsSchema`, `Integer minContains`, `Integer maxContains`.
- **Loader**: parse these fields for arrays.
- **Validator**:
  - iterate array elements and count matches of `containsSchema`;
  - default `minContains = 1` when `contains` exists and `minContains` absent;
  - enforce `maxContains` if present.

### 3) Registry alignment

- Update `SupportedKeywordsRegistry.initializeSupportedKeywords()` to match Draft 2020-12 canonical names.
- Keep legacy aliases behind explicit compatibility flag (if needed), instead of mixing canonical + legacy in a single list.
