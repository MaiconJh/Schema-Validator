# Source of Truth Audit (2026-03-20)

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

| # | File | Line | Current Value | Expected Value | Severity |
|---|------|------|----------------|----------------|----------|
| 1 | custom-block-example.yml | 89 | `value: "diamond"` | `value: "minecraft:diamond"` | **HIGH** |
| 2 | custom-block-example.yml | 109 | `value: "diamond_block"` | `value: "minecraft:diamond_block"` | **HIGH** |
| 3 | custom-block-example.yml | 199 | `block: "cobblestone"` | `block: "minecraft:cobblestone"` | **HIGH** |
| 4 | custom-block-example.yml | 255 | `value: "iron_ingot"` | `value: "minecraft:iron_ingot"` | **HIGH** |
| 5 | custom-block-example.yml | 186-194 | `id: "diamond_ore"` (stages) | Consider namespace | **MEDIUM** |

### Schema Format Declaration Issues

| # | File | Issue | Severity |
|---|------|-------|----------|
| 1 | complex-item.schema.json | Missing `format: "minecraft-item"` for item IDs | **HIGH** |
| 2 | simple-block-schema.json | ✅ Already has format (fixed) | - |
| 3 | custom-block-schema.json (examples) | ✅ Already has format (fixed) | - |

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
- ⚠️ custom-block-schema.json - Needs format additions
- ⚠️ complex-item.schema.json - Missing minecraft-item format
- ✅ simple-block-schema.json - Already updated

### Examples (src/main/resources/examples/)
- ⚠️ custom-block-example.yml - Multiple values need minecraft: prefix
- ✅ complete-custom-block-example.yml - Already uses minecraft: prefix
- ✅ simple-block-example.yml - Already uses minecraft: prefix

---

## 🔧 CORRECTIONS NEEDED

### Priority 1 (Critical - Validation Broken)

1. **custom-block-example.yml** - Fix all item/block values to use `minecraft:` prefix
   - Line 89: `diamond` → `minecraft:diamond`
   - Line 109: `diamond_block` → `minecraft:diamond_block`
   - Line 199: `cobblestone` → `minecraft:cobblestone`
   - Line 255: `iron_ingot` → `minecraft:iron_ingot`

2. **complex-item.schema.json** - Add format validation
   - Add `"format": "minecraft-item"` to item ID fields

### Priority 2 (Medium - Inconsistency)

1. **custom-block-example.yml stages** - Consider adding namespace to stage IDs
   - Current: `"diamond_ore"`
   - Recommended: `"minecraft:diamond_ore"` or keep as internal ID

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
