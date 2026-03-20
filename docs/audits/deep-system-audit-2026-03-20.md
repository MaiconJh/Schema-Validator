# Deep System Audit - Updated (2026-03-20)

## 🧾 SYSTEM STATE SUMMARY

Schema-Validator has significantly evolved since the last audit. The system now supports:
- Full conditional validation (`oneOf`, `not`, `if/then/else`)
- Comprehensive format validation (12 standard + 13 Minecraft-specific formats)
- Array cardinality constraints (`minItems`, `maxItems`, `uniqueItems`)
- Object cardinality constraints (`minProperties`, `maxProperties`)
- Numeric multiple validation (`multipleOf`)
- 39+ documented keywords with registry

**Current maturity by area:**
- **Core validation engine:** ✅ Stable - supports all major JSON Schema features
- **Schema language support:** ✅ Complete subset of JSON Schema Draft 2020-12
- **Skript integration:** ⚠️ Basic - works but could improve error handling
- **Documentation:** ✅ Much improved - now aligned with implementation

---

## ✅ ISSUES RESOLVED (vs 2026-03-19)

| Issue | Status | Evidence |
|-------|--------|-----------|
| Silent under-validation for `oneOf`, `not`, `if/then/else` | ✅ FIXED | Implemented in ObjectValidator.java lines 114-199 |
| Silent under-validation for `minItems`, `maxItems`, `uniqueItems` | ✅ FIXED | Implemented in ArrayValidator.java |
| Silent under-validation for `minProperties`, `maxProperties` | ✅ FIXED | Implemented in ObjectValidator.java |
| Silent under-validation for `multipleOf` | ✅ FIXED | Implemented in PrimitiveValidator.java |
| Silent under-validation for `format` | ✅ FIXED | FormatValidator.java with 25 total formats |
| Keyword registry | ✅ FIXED | SupportedKeywordsRegistry.java with 39+ keywords |

---

## ⚠️ ISSUES STILL PENDING

### Issue 1: Root Validator Dispatch (CRITICAL - FIXED ✅)
- **Name:** Root validator bypasses schema-type dispatch
- **Category:** Runtime Bug
- **Severity:** CRITICAL
- **Root cause:** `ValidationService.validate()` always used `ObjectValidator` instead of dispatching via `ValidatorDispatcher.forSchema()`
- **Surface impact:** Root array/primitive schemas may fail with object-type errors
- **Evidence (BEFORE):** `ValidationService.java` line 24: `this.rootValidator = new ObjectValidator();`
- **Recommended fix:** Use `ValidatorDispatcher.forSchema(schema)` at validation entrypoint
- **Implementation:** ✅ FIXED in `src/main/java/com/maiconjh/schemacr/core/ValidationService.java:41-50`
  - Modified `validate()` method to use `ValidatorDispatcher.forSchema(schema)`
  - Added refResolver support for ObjectValidator instances
  - Changes include new import for ValidatorDispatcher

### Issue 2: Skript Error Model Mismatch (LOW)
- **Name:** Expression returns String[] while docs describe structured objects
- **Category:** Contract Violation  
- **Severity:** LOW
- **Root cause:** `ExprLastValidationErrors` returns String[] (line 42)
- **Surface impact:** Users expect ValidationError objects but get strings

### Issue 3: $ref and definitions Support (PARTIAL)
- **Name:** Reference support is partial relative to JSON Schema spec
- **Category:** Feature Gap
- **Severity:** MEDIUM
- **Root cause:** Resolver supports basic JSON Pointer but not full recursive references
- **Recommended:** Document current scope limitations clearly

### Issue 4: Config Contract Mismatch (RESOLVED)
- **Status:** ✅ Previously documented, current config appears aligned
- **Recommendation:** Verify `config.yml` matches documentation

---

## 📊 CURRENT FEATURE MATRIX

### Supported Keywords (39+)

| Category | Keywords | Status |
|---------|----------|--------|
| **Type** | `type` | ✅ Full |
| **Object** | `properties`, `patternProperties`, `additionalProperties`, `required`, `minProperties`, `maxProperties`, `dependencies` | ✅ Full |
| **Array** | `items`, `minItems`, `maxItems`, `uniqueItems`, `additionalItems` | ✅ Full |
| **String** | `minLength`, `maxLength`, `pattern`, `format` | ✅ Full |
| **Number** | `minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf` | ✅ Full |
| **Composition** | `allOf`, `anyOf`, `oneOf`, `not` | ✅ Full |
| **Conditional** | `if`, `then`, `else` | ✅ Full |
| **Reference** | `$ref`, `definitions`, `$schema`, `$id` | ⚠️ Partial |
| **Constraint** | `enum`, `const` | ✅ Full |
| **Metadata** | `title`, `description`, `default`, `examples`, `readOnly`, `writeOnly`, `deprecated`, `comment` | ✅ Full |

### Supported Formats (25 total)

#### Standard Formats (12)
| Format | Example |
|--------|---------|
| `date-time` | `2024-01-15T10:30:00Z` |
| `date` | `2024-01-15` |
| `time` | `10:30:00` |
| `email` | `user@example.com` |
| `hostname` | `example.com` |
| `ipv4` | `192.168.1.1` |
| `ipv6` | `2001:0db8::1` |
| `uri` | `https://example.com/path` |
| `uri-reference` | `/path` or `relative` |
| `uri-template` | `/users/{id}` |
| `json-pointer` | `/path/to/field` |
| `relative-json-pointer` | `0/field` |

#### Minecraft Formats (13)
| Format | Example |
|--------|---------|
| `minecraft-item` | `minecraft:diamond_sword` |
| `minecraft-block` | `minecraft:stone` |
| `minecraft-entity` | `minecraft:zombie` |
| `minecraft-attribute` | `minecraft:generic.attack_damage` |
| `minecraft-effect` | `minecraft:speed` |
| `minecraft-enchantment` | `minecraft:efficiency` |
| `minecraft-biome` | `minecraft:plains` |
| `minecraft-dimension` | `minecraft:the_nether` |
| `minecraft-particle` | `minecraft:flame` |
| `minecraft-sound` | `minecraft:block.break` |
| `minecraft-potion` | `minecraft:strength` |
| `minecraft-recipe` | `minecraft:diamond_pickaxe` |
| `minecraft-tag` | `minecraft:logs` |

---

## 🏗️ ARCHITECTURE CURRENT STATE

### Validation Flow
```
EffValidateData (Skript effect)
    ↓
ValidationService.validate()
    ↓
ValidatorDispatcher.forSchema(schema) ✅ FIXED
    ↓
[ObjectValidator | ArrayValidator | PrimitiveValidator]
```

### Validator Classes
| Class | Responsibility |
|-------|---------------|
| `ValidationService` | Entry point, facade |
| `ValidatorDispatcher` | Type-based validator selection (NOW USED at entrypoint ✅) |
| `ObjectValidator` | Object validation, composition, conditional |
| `ArrayValidator` | Array validation, cardinality |
| `PrimitiveValidator` | String, number, boolean validation |
| `FormatValidator` | Format validation (standard + Minecraft) |

---

## 📁 SCHEMA FILES

### Production Schemas (src/main/resources/schemas/)
| Schema | Status |
|--------|--------|
| `custom-block.schema.json` | ✅ Active - uses patternProperties, minecraft-block/item formats |
| `item.schema.json` | ✅ Active |
| `player.schema.json` | ✅ Active |
| `user-profile.schema.json` | ✅ Active |

### Example Schemas (src/main/resources/examples/schemas/)
| Schema | Status |
|--------|--------|
| `conditional-validation.schema.json` | ✅ Tests oneOf, not, if/then/else |
| `complex-item.schema.json` | ✅ Tests minecraft-item format |
| `data-types-formats.schema.json` | ✅ Tests all format validators |
| `player-profile.schema.json` | ✅ Basic example |
| `player-with-address.schema.json` | ✅ Nested objects |
| `simple-block-schema.json` | ✅ Basic validation |

---

## 🔗 DEPENDENCY STATUS

### What Works Now (Can Be Released)
- ✅ All basic validation (type, required, properties)
- ✅ Conditional validation (oneOf, not, if/then/else)
- ✅ Format validation (standard + Minecraft)
- ✅ Array cardinality (minItems, maxItems, uniqueItems)
- ✅ Object cardinality (minProperties, maxProperties)
- ✅ Numeric multipleOf validation
- ✅ Keyword registry with warnings

### What Needs Architecture First
- ✅ Root validator dispatch fix (RESOLVED)
- ⚠️ Full $ref recursive support
- ⚠️ Structured Skript error objects

---

## 🧪 VALIDATION TESTS STATUS

### Test Coverage
| Area | Tests | Status |
|------|-------|--------|
| Type validation | Multiple | ✅ Covered |
| Object validation | Multiple | ✅ Covered |
| Array validation | Multiple | ✅ Covered |
| Conditional validation | Multiple | ✅ Covered |
| Format validation | Multiple | ✅ Covered |

---

## 🚨 RECOMMENDATIONS

### Priority 1: Fix Root Dispatch (RESOLVED ✅)
```java
// FIXED in ValidationService.validate() method:
Validator validator = ValidatorDispatcher.forSchema(schema);

// With refResolver support:
if (refResolver != null && validator instanceof ObjectValidator) {
    ((ObjectValidator) validator).setRefResolver(refResolver);
}
```

### Priority 2: Document $ref Limitations
- Current: Basic JSON Pointer support only
- Not supported: Recursive references, $dynamicAnchor
- Action: Add limitation warning to docs

### Priority 3: Improve Skript Error Model
- Option A: Add new structured expression (additive)
- Option B: Document current string[] behavior clearly

---

## 📋 UNSOLICITED ADVICE

### For v0.3.5 Release
1. ✅ Include root dispatch bug workaround (users should use object-root schemas)
2. ✅ Test all example schemas before release
3. ✅ Add fail-fast mode option in config

### For Future Versions
1. Consider adding schema versioning in metadata
2. Add more detailed error paths for debugging
3. Consider async validation for large datasets

---

## 🔗 USEFUL LINKS

- JSON Schema Spec: https://json-schema.org/
- Understanding JSON Schema: https://json-schema.org/understanding-json-schema/
- GitHub Issues: https://github.com/MaiconJH/Schema-Validator/issues

---

*Last Updated: 2026-03-20*
*Author: System Audit*
*This document supersedes deep-system-audit-2026-03-19.md*
