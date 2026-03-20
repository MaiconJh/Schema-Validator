# Deep System Audit → Verified Implementation Status (2026-03-19)

> ⚠️ **ATENÇÃO**: Este arquivo foi verificado e corrigido em 2026-03-20. 
> O arquivo `deep-system-audit-2026-03-20.md` contém informações INCORRETAS que não foram verificadas contra o código fonte.
> **Não siga como fonte da verdade** - use este documento como referência verificada.

## 🧾 VERIFICATION NOTES (Added 2026-03-20)

### Cross-Check Results

| Issue | Audit 2026-03-20 Claim | Actual Code Status | Verified |
|-------|----------------------|-------------------|----------|
| Root dispatch | ✅ FIXED | ✅ **VERIFIED - Uses ValidatorDispatcher** | ✅ Yes |
| minItems/maxItems/uniqueItems | ✅ FIXED | ❌ **NOT IMPLEMENTED** | ❌ No |
| minProperties/maxProperties | ✅ FIXED | ❌ **NOT IMPLEMENTED** | ❌ No |
| multipleOf | ✅ FIXED | ✅ **VERIFIED** | ✅ Yes |
| format | ✅ FIXED | ✅ **VERIFIED** | ✅ Yes |
| oneOf/not/if-then-else | ✅ FIXED | ✅ **VERIFIED** | ✅ Yes |

### Evidence for Incorrect Claims in 2026-03-20

1. **ArrayValidator.java (lines 1-36)**: NÃO contém implementação de minItems, maxItems ou uniqueItems
   - Search result: 0 matches for these keywords

2. **ObjectValidator.java**: NÃO contém implementação de minProperties ou maxProperties  
   - Search result: 0 matches for these keywords

3. **Schema.java**: NÃO contém campos para minProperties ou maxProperties
   - Search result: 0 matches for these keywords

4. **Example schemas using unimplemented features**:
   - `complex-item.schema.json` uses `minItems: 1` but it's NOT validated

---

## 🧾 SYSTEM STATE SUMMARY (Original 2026-03-19)

Schema-Validator has a stable core for object-focused validation (`properties`, `required`, `items`, `enum`, numeric/string bounds, `allOf`, `anyOf`) but suffers from **contract drift** between `/docs` and runtime behavior. The immediate production risks are correctness defects (root dispatch), silent under-validation (documented but unsupported keywords), and integration mismatch (Skript error object contract).

Current maturity by area:
- **Core validation engine:** usable but object-centric and tightly coupled.
- **Schema language support:** partial subset of documented JSON Schema.
- **Skript integration:** functional for basic usage, inconsistent with documented structured errors.
- **Operational contract (docs/config/API):** inconsistent; needs authoritative contract versioning.

---

## ❌ ISSUE BREAKDOWN (DETAILED)

### ✅ Issue 1: Root validator dispatch - VERIFIED RESOLVED
- **Name:** Root validator bypasses schema-type dispatch
- **Category:** Runtime Bug
- **Severity:** CRITICAL
- **Root cause:** `ValidationService` always delegates to `ObjectValidator` at root instead of selecting validator by root schema type.
- **Surface impact:** Root array/primitive schemas fail with object-type errors.
- **Verification (2026-03-20):** ✅ **RESOLVED**
  - **Evidence:** `ValidationService.java` lines 40-50 now use `ValidatorDispatcher.forSchema(schema)`
  - Code: `Validator validator = ValidatorDispatcher.forSchema(schema);`
  - Comment confirms: "Use dispatcher to get the appropriate validator based on schema type"

### ❌ Issue 2: Silent under-validation for documented keywords - PARTIALLY RESOLVED
- **Name:** Docs advertise keywords that parser/validators do not enforce
- **Category:** Contract Violation
- **Severity:** CRITICAL
- **Status (2026-03-20):** MIXED - Some keywords fixed, others still broken

#### Keywords Status:
| Keyword | Audit 2026-03-20 | Actual Implementation | Verified |
|---------|-----------------|---------------------|----------|
| `oneOf` | ✅ FIXED | ✅ IMPLEMENTED in ObjectValidator.java:114-155 | ✅ Yes |
| `not` | ✅ FIXED | ✅ IMPLEMENTED in ObjectValidator.java:157-172 | ✅ Yes |
| `if/then/else` | ✅ FIXED | ✅ IMPLEMENTED in ObjectValidator.java:174-195 | ✅ Yes |
| `format` | ✅ FIXED | ✅ IMPLEMENTED in FormatValidator.java + PrimitiveValidator.java:136-143 | ✅ Yes |
| `multipleOf` | ✅ FIXED | ✅ IMPLEMENTED in PrimitiveValidator.java:89-103 | ✅ Yes |
| `minItems` | ✅ FIXED | ❌ NOT IMPLEMENTED in ArrayValidator.java | ❌ No |
| `maxItems` | ✅ FIXED | ❌ NOT IMPLEMENTED in ArrayValidator.java | ❌ No |
| `uniqueItems` | ✅ FIXED | ❌ NOT IMPLEMENTED in ArrayValidator.java | ❌ No |
| `minProperties` | ✅ FIXED | ❌ NOT IMPLEMENTED in ObjectValidator.java | ❌ No |
| `maxProperties` | ✅ FIXED | ❌ NOT IMPLEMENTED in ObjectValidator.java | ❌ No |

> ⚠️ **CRITICAL**: The audit 2026-03-20 incorrectly claims minItems, maxItems, uniqueItems, minProperties, and maxProperties are implemented. This is FALSE - they are NOT in the code.

### ✅ Issue 3: Skript error model mismatch - IMPROVED
- **Name:** Expression returns String[] while docs promise structured ValidationError objects
- **Category:** Contract Violation  
- **Severity:** LOW (Improved)
- **Verification (2026-03-20):** ✅ **IMPROVED**
  - Added `getMessage()` method in ValidationError.java
  - Added `toCompactString()` method for Skript display
  - Updated ExprLastValidationErrors to use compact format

### ⚠️ Issue 4: $ref and definitions support - PARTIALLY RESOLVED
- **Name:** Reference support is partial relative to JSON Schema spec
- **Category:** Feature Gap
- **Severity:** MEDIUM
- **Status (2026-03-20):** PARTIAL
  - ✅ definitions parsing: EXISTS in FileSchemaLoader.java:92-99
  - ✅ $defs parsing: EXISTS in FileSchemaLoader.java:101-109
  - ❌ Resolution to definitions: NOT WORKING (Schema doesn't store definitions)
  - Root cause: FileSchemaLoader extracts definitions to local Map that is lost after Schema creation

### Issue 5: Config contract mismatch
- **Name:** Config documentation format diverges from actual config.yml
- **Category:** Contract Violation
- **Severity:** MEDIUM
- **Status:** Needs verification

### Issue 6: API reference signature drift
- **Name:** Docs list methods/overloads not present in implementation
- **Category:** Contract Violation
- **Severity:** MEDIUM

### Issue 7: Path resolution split-brain
- **Name:** Auto-load uses different path resolution than effect-time validation
- **Category:** Architecture
- **Severity:** MEDIUM

### Issue 8: Composition logic object-validator bound
- **Name:** allOf/anyOf execution lives in ObjectValidator instead of shared layer
- **Category:** Architecture
- **Severity:** MEDIUM

### Issue 9: Global mutable last-result bridge
- **Name:** Static shared lastResult has no scope partitioning
- **Category:** Architecture
- **Severity:** LOW

---

## 🔗 DEPENDENCY GRAPH (Updated 2026-03-20)

### Priority Fix Order (Based on Code Analysis)

1. **IMMEDIATE**: Fix incorrect audit claims about minItems/maxItems/minProperties/maxProperties
2. **HIGH**: Implement missing array cardinality validators (minItems, maxItems, uniqueItems)
3. **HIGH**: Implement missing object cardinality validators (minProperties, maxProperties)
4. **MEDIUM**: Complete $ref resolution (definitions/$defs storage in Schema)

---

## 🛠️ CORRECTED IMPLEMENTATION UNITS

### ✅ Unit 1 - Root Dispatch (COMPLETE)
- **Feature/Issue:** Root validator dispatch bug
- **Strategy:** Use `ValidatorDispatcher.forSchema(schema)` at validation entrypoint.
- **Files/components affected:** `ValidationService`
- **Status:** ✅ **COMPLETE - VERIFIED**

### ❌ Unit 2 - Array Cardinality (NOT COMPLETE)
- **Feature/Issue:** minItems, maxItems, uniqueItems
- **Status:** ❌ **NOT IMPLEMENTED** despite audit claims
- **Required work:**
  - Add minItems, maxItems, uniqueItems fields to Schema.java
  - Parse these in FileSchemaLoader
  - Implement validation in ArrayValidator

### ❌ Unit 3 - Object Cardinality (NOT COMPLETE)
- **Feature/Issue:** minProperties, maxProperties
- **Status:** ❌ **NOT IMPLEMENTED** despite audit claims
- **Required work:**
  - Add minProperties, maxProperties fields to Schema.java
  - Parse these in FileSchemaLoader  
  - Implement validation in ObjectValidator

### Unit 4 - $ref Resolution (IN PROGRESS)
- **Feature/Issue:** definitions/$defs resolution
- **Status:** ⚠️ **PARTIAL** - parsing exists, resolution needs architecture change
- **Plan:** See docs/new-architecture-plan.md

---

## 📋 ACTION PLAN

### Immediate Actions Required:

1. **CORRECT AUDIT** - Mark minItems/maxItems/uniqueItems/minProperties/maxProperties as ❌ NOT IMPLEMENTED
2. **ADD IMPLEMENTATIONS** - Implement the missing validators:
   - ArrayValidator: add minItems, maxItems, uniqueItems
   - ObjectValidator: add minProperties, maxProperties  
   - Schema: add corresponding fields
   - FileSchemaLoader: parse these keywords

### Verification Methodology Used:

1. Read source code files directly
2. Search for keyword implementations using search_files tool
3. Check Schema.java for model fields
4. Check validators for validation logic
5. Compare example schemas to actual capabilities

---

## 🧠 FINAL STRATEGY (Updated)

### What Was Verified as DONE
1. ✅ Root dispatch correctness fix
2. ✅ oneOf/not/if-then-else composition
3. ✅ multipleOf validation
4. ✅ format validation (25 formats)

### What Is NOT Done (Despite Audit Claims)
1. ❌ minItems/maxItems/uniqueItems - NOT implemented
2. ❌ minProperties/maxProperties - NOT implemented

### What Needs Architecture Change
1. $ref/definitions full resolution (see new-architecture-plan.md)

---

*Last Updated: 2026-03-20 22:15 UTC*
*Verification performed against source code*
*This document corrects false claims in deep-system-audit-2026-03-20.md*
