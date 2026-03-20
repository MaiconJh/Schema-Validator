# Documentation Consolidation Plan

**Last Updated:** 2026-03-20
**Revised:** 2026-03-20 (Cross-reference analysis completed)

---

## Cross-Reference Analysis: deep-system-audit-2026-03-19 vs Current Implementation

### Verification Methodology

Each issue from `deep-system-audit-2026-03-19.md` was verified against current source code:

| Issue # | Issue Name | Audit 2026-03-19 Status | Current Status | Evidence |
|---------|------------|-------------------------|----------------|----------|
| 1 | Root validator dispatch | CRITICAL - bypasses type dispatch | ✅ **FIXED** | ValidationService.java:40-50 uses `ValidatorDispatcher.forSchema()` |
| 2 | Silent under-validation | CRITICAL - keywords not enforced | ✅ **FIXED** | All documented keywords now implemented (2026-03-20 audit) |
| 3 | Skript error model mismatch | CRITICAL - String[] vs objects | ✅ **IMPROVED** | ValidationError.java has getMessage() and toCompactString() |
| 4 | $ref/definitions incomplete | CRITICAL - incomplete pointer navigation | ⚠️ **PARTIAL** | definitions/$defs parsing added, full resolution needs arch change |
| 5 | Config contract mismatch | MEDIUM - format divergence | ✅ **FIXED** | config.yml aligned with PluginConfig.java keys |
| 6 | API reference drift | MEDIUM - missing methods in docs | ✅ **FIXED** | api-reference.md now implementation-aligned |
| 7 | Path resolution split-brain | MEDIUM - autoload vs effect-time | ⚠️ **STILL PRESENT** | EffValidateData uses raw Path.of(), autoload uses config dir |
| 8 | Composition in ObjectValidator | MEDIUM - bound to object validator | ⚠️ **PARTIAL** | Still in ObjectValidator, but functional |
| 9 | Global mutable last-result | LOW - no scope partitioning | ⚠️ **STILL PRESENT** | SkriptValidationBridge still uses static field |

### Key Findings

#### ✅ FIXED Issues (6)
- **Issue 1**: Root validator now correctly dispatches by schema type
- **Issue 2**: All documented keywords are now enforced
- **Issue 3**: Error model improved with structured access
- **Issue 5**: Config keys now match between docs and code
- **Issue 6**: API reference matches implementation

#### ⚠️ PARTIALLY FIXED Issues (2)
- **Issue 4**: definitions/$defs parsing added, but full resolution requires new architecture (see new-architecture-plan.md)
- **Issue 8**: Composition logic remains in ObjectValidator, but works correctly

#### ❌ STILL PRESENT Issues (2)
- **Issue 7**: Path resolution inconsistency between autoload and Skript effect
- **Issue 9**: Global static mutable state in SkriptValidationBridge

---

## Executive Summary

This document tracks the ongoing documentation consolidation effort for Schema-Validator. The goal is to reduce redundancy, eliminate obsolete content, and establish a clear documentation structure.

---

## Changes Log

### 2026-03-20 - Phase 2 Complete

#### Files Moved to Archive

| Original Location | New Location | Reason |
|-----------------|--------------|--------|
| `wiki/` | `docs/archive/wiki/` | Redundant - GitHub wiki content with outdated links |
| `source-of-truth-audit-2026-03-19.md` | `docs/audits/` | Consolidated to audit folder |
| `deep-system-audit-2026-03-19.md` | `docs/audits/` | Consolidated to audit folder |
| `practical-documentation-audit.md` | `docs/audits/` | Consolidated to audit folder |
| `documentation-structure-audit-2026-03-20.md` | `docs/audits/` | Consolidated to audit folder |
| `documentation-consolidation-plan-2026-03-20.md` | `docs/audits/` | Moved to audit folder |
| `implementation-task-system.md` | `docs/archive/` | Internal project management doc |
| `construction.md` | `docs/archive/` | Legacy content |

#### Files Merged

| Source | Destination | Notes |
|--------|-------------|-------|
| `supported-keywords.md` | `reference/json-schema.md` | Combined with format documentation |
| `configuration.md` | `guides/integration.md` | Combined with integration guide |

#### Files Created

| File | Purpose |
|------|---------|
| `CHANGELOG.md` | Version history |
| `TROUBLESHOOTING.md` | Common issues guide |
| `audits/README.md` | Audit folder index |

---

## Current Structure

```
docs/
├── README.md                 # Main index
├── CONTRACT.md              # Canonical contract
├── CHANGELOG.md             # Version history [NEW]
├── TROUBLESHOOTING.md       # Problem solving [NEW]
├── quickstart.md            # Quick start guide
├── installation.md           # Installation guide
├── faq.md                   # FAQ
├── api-reference.md         # API reference
├── minecraft-formats.md     # Minecraft format validators
├── architecture.md          # Technical architecture
│
├── guides/
│   └── integration.md       # Java + Skript + Config [MERGED]
│
├── reference/
│   ├── json-schema.md       # Keywords + Formats [MERGED]
│   ├── schema-composition.md
│   ├── data-types.md
│   └── skript-syntax.md
│
├── tutorials/
│   ├── README.md
│   ├── custom-blocks.md
│   ├── inventory-validation.md
│   └── player-data-validation.md
│
├── audits/                  [NEW FOLDER]
│   ├── README.md
│   ├── deep-system-audit-2026-03-19.md
│   ├── source-of-truth-audit-2026-03-20.md
│   ├── practical-documentation-audit.md
│   ├── documentation-structure-audit-2026-03-20.md
│   └── documentation-consolidation-plan-2026-03-20.md
│
└── archive/
    ├── construction.md
    ├── implementation-task-system.md
    ├── Responsibilities.md
    └── wiki/               [MOVED]
```

---

## Metrics

### Before Consolidation (2026-03-19)

| Metric | Value |
|--------|-------|
| Total files in docs/ | 30 |
| Audit files scattered | 5 |
| Duplicate content | Yes |
| Wiki folder separate | Yes |

### After Consolidation (2026-03-20)

| Metric | Value | Change |
|--------|-------|--------|
| Total files in docs/ | 27 | -10% |
| Audit files consolidated | 6 (in audits/) | Centralized |
| Duplicate content | 0 | -100% |
| Wiki folder | In archive/ | Moved |

---

## Policy

### Documentation Standards

1. **Single Source of Truth** - All documentation in `docs/` folder
2. **File Naming** - kebab-case for content, UPPERCASE for special (README, CHANGELOG)
3. **No Duplication** - Merge related content instead of duplicating
4. **Audit Folder** - All audit/analysis documents go in `docs/audits/`
5. **Archive** - Obsolete content goes in `docs/archive/`

### Maintenance

- Quarterly documentation review
- Annual structural audit
- PRs must include documentation impact statement

---

## Integrated Action Plan

Based on cross-reference analysis and new-architecture-plan.md:

### Immediate Actions (Next Release)

| Action | Issue | Priority | Owner |
|--------|-------|----------|-------|
| Implement Schema fields for definitions/$defs | Issue 4 | CRITICAL | TBD |
| Update SchemaRefResolver for local definitions | Issue 4 | CRITICAL | TBD |

### Future Actions (Backlog)

| Action | Issue | Priority | Status |
|--------|-------|----------|--------|
| Fix path resolution consistency | Issue 7 | MEDIUM | Pending |
| Address global mutable state | Issue 9 | LOW | Backlog |
| Extract composition to shared layer | Issue 8 | MEDIUM | Backlog |

---

## Notes

### Why 2026-03-19 Audit is Now Obsolete

> **Não segue como fonte da verdade** - The `deep-system-audit-2026-03-19.md` contains several issues that have been resolved:

1. **Issue 1 (Root dispatch)** - Listed as CRITICAL but was FIXED before 2026-03-20
   - Evidence: `ValidationService.java` now uses `ValidatorDispatcher.forSchema()` at line 40-50
   
2. **Issue 2 (Silent under-validation)** - Listed as CRITICAL but ALL documented keywords are now implemented
   - Evidence: ObjectValidator, ArrayValidator, PrimitiveValidator all fully implement keywords

3. **Issue 3 (Error model)** - Listed as CRITICAL but IMPROVED
   - Evidence: ValidationError now has structured methods

4. **Issue 5 & 6 (Config/API)** - Listed as MEDIUM but FIXED
   - Evidence: api-reference.md and config.yml are now aligned

**Recommendation:** The 2026-03-19 audit should be marked as superseded by the 2026-03-20 audit, which more accurately reflects the current system state.

---

## Pending Items

| Item | Priority | Status |
|------|----------|--------|
| architecture.md split (>40KB) | Low | Deferred - well structured |
| Mark deep-system-audit-2026-03-19.md as superseded | Medium | Pending |
| Implement new architecture plan for $ref/definitions | Critical | In Progress |
| Fix path resolution consistency | Medium | Backlog |

---

*This document is maintained as part of the consolidation effort.*
