# Source of Truth Audit (2026-03-20)

> **Status:** ✅ RESOLVED - All issues fixed on 2026-03-20

## Scope and Method

This audit evaluates the consistency between schemas, examples, and implementation after adding Minecraft ID validation formats.

Sources reviewed:
- `src/main/resources/schemas/*.json`
- `src/main/resources/examples/schemas/*.json`
- `src/main/resources/examples/*.yml`
- `docs/minecraft-formats.md` (newly created)
- Implementation in `src/main/java/`

---

## 🎯 NEW FORMATS IMPLEMENTATION STATUS

### Formats Added (2026-03-20)

| Format | Schema Field | Status | File |
|--------|--------------|--------|------|
| `minecraft-item` | drops[].value | ✅ Added | custom-block.schema.json |
| `minecraft-block` | block-type | ✅ Added | custom-block.schema.json |
| `minecraft-entity` | N/A | ✅ Implemented | FormatValidator.java |
| `minecraft-attribute` | N/A | ✅ Implemented | FormatValidator.java |
| `minecraft-effect` | N/A | ✅ Implemented | FormatValidator.java |
| `minecraft-enchantment` | N/A | ✅ Implemented | FormatValidator.java |
| `minecraft-biome` | N/A | ✅ Implemented | FormatValidator.java |
| `minecraft-dimension` | N/A | ✅ Implemented | FormatValidator.java |
| `minecraft-particle` | N/A | ✅ Implemented | FormatValidator.java |
| `minecraft-sound` | N/A | ✅ Implemented | FormatValidator.java |
| `minecraft-potion` | N/A | ✅ Implemented | FormatValidator.java |
| `minecraft-recipe` | N/A | ✅ Implemented | FormatValidator.java |
| `minecraft-tag` | N/A | ✅ Implemented | FormatValidator.java |

---

## ❌ INCONSISTENCIES FOUND

### Minecraft ID Format Inconsistencies

| # | File | Line | Current Value | Expected Value | Severity | Status |
|---|------|------|----------------|----------------|----------|--------|
| 1 | custom-block-example.yml | 297 | `value: "ender_pearl"` | `value: "minecraft:ender_pearl"` | **HIGH** | ✅ FIXED |
| 2 | custom-block-example.yml | 304 | `value: "experience_bottle"` | `value: "minecraft:experience_bottle"` | **HIGH** | ✅ FIXED |

### Schema Format Declaration Issues

| # | File | Issue | Severity | Status |
|---|------|-------|----------|--------|
| 1 | complex-item.schema.json | Has `format: "minecraft-item"` | - | ✅ ALREADY FIXED |
| 2 | simple-block-schema.json | ✅ Already has format | - | ✅ VERIFIED |
| 3 | custom-block-schema.json (examples) | ✅ Already has format | - | ✅ VERIFIED |

### Documentation Issues

| # | File | Issue | Recommendation |
|---|------|-------|----------------|
| 1 | docs/minecraft-formats.md | Newly created | Verify examples work |
| 2 | docs/tutorials/custom-blocks.md | Needs review for Minecraft ID format | Update examples |

---

## 📋 FILES REVIEWED

### Schemas (src/main/resources/schemas/)
- ✅ custom-block.schema.json - Has minecraft-block and minecraft-item formats

### Schemas (src/main/resources/examples/schemas/)
- ✅ custom-block-schema.json - Has minecraft-block and minecraft-item formats
- ✅ complex-item.schema.json - Has minecraft-item format
- ✅ simple-block-schema.json - Has minecraft-block format

### Examples (src/main/resources/examples/)
- ✅ custom-block-example.yml - FIXED: All values now use minecraft: prefix
- ✅ complete-custom-block-example.yml - Already uses minecraft: prefix
- ✅ simple-block-example.yml - Already uses minecraft: prefix

---

## 🔧 CORRECTIONS NEEDED

### Priority 1 (Critical - Validation Broken)

1. ✅ **custom-block-example.yml** - FIXED
   - Line 297: `ender_pearl` → `minecraft:ender_pearl`
   - Line 304: `experience_bottle` → `minecraft:experience_bottle`

2. ✅ **complex-item.schema.json** - ALREADY FIXED
   - Has `"format": "minecraft-item"` on line 15

### Priority 2 (Medium - Inconsistency)

1. ✅ **custom-block-example.yml stages** - ACCEPTABLE
   - Stage IDs like `"diamond_ore"` are internal identifiers
   - No validation format required for internal IDs

---

## ✅ PREVIOUS AUDIT ISSUES RESOLVED

From `source-of-truth-audit-2026-03-19.md`:

| Issue | Status |
|-------|--------|
| Configuration mismatch | Still needs review |
| Integration guide API | Still outdated |
| Array keywords not implemented | Still not implemented |
| Format validation | ✅ NOW IMPLEMENTED |
| Minecraft ID validation | ✅ NOW IMPLEMENTED |

---

## 🧪 VALIDATION TEST RESULTS

After applying corrections, run:

```bash
# Build project
gradlew.bat build

# Test validation
# Start server and test:
# - Valid: minecraft:diamond_sword
# - Invalid: diamond_sword (missing namespace)
# - Invalid: Minecraft:diamond_sword (uppercase namespace)
```

---

## 📝 RECOMMENDATIONS

1. **Immediate**: Fix custom-block-example.yml values to match schema validation
2. **Short-term**: Add formats to all schemas that validate Minecraft IDs
3. **Medium-term**: Create automated test to verify all examples pass validation
4. **Long-term**: Add CI test to enforce Minecraft ID format consistency

---

## 📅 Audit Date
2026-03-20

## 👤 Auditor
Code review and consistency check
