# Documentation Consolidation Plan

**Last Updated:** 2026-03-20

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

## Pending Items

| Item | Priority | Status |
|------|----------|--------|
| architecture.md split (>40KB) | Low | Deferred - well structured |

---

*This document is maintained as part of the consolidation effort.*
