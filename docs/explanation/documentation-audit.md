# Documentation audit and reconstruction report

This report documents Phase 2 (destructive audit) and Phase 3 (reconstruction) of the documentation rebuild.

## Phase 2 classification of previous documentation set

Legend:
- ✅ VALID
- ⚠️ PARTIALLY VALID
- ❌ INVALID
- 🗑 OBSOLETE

### Root docs files (before reconstruction)

| File | Classification | Notes |
|---|---|---|
| `docs/README.md` | ⚠️ | Useful index intent, but linked many stale/duplicative pages and mixed normative layers not enforced in code. |
| `docs/quickstart.md` | ⚠️ | Conceptually aligned but examples and flow mixed with stale references. |
| `docs/installation.md` | ⚠️ | Core install idea valid; version details and linked paths inconsistent with current project files. |
| `docs/CHANGELOG.md` | ✅ | Historical changelog content; non-behavioral. |
| `docs/CONTRACT.md` | ❌ | Included claims beyond currently implemented enforcement in validators. |
| `docs/TROUBLESHOOTING.md` | ⚠️ | Some operational tips valid, but error semantics not always aligned with current classes. |
| `docs/api-reference.md` | ❌ | API descriptions did not consistently match constructor/use-site behavior. |
| `docs/architecture.md` | ⚠️ | High-level structure roughly valid, but startup/validation details drifted. |
| `docs/faq.md` | ⚠️ | Mixed accurate and speculative behavior statements. |
| `docs/minecraft-formats.md` | ⚠️ | Listed formats mostly real; lacked direct tie to active validator logic and fallback behavior. |
| `docs/new-architecture-plan.md` | 🗑 | Planning artifact, not representation of implemented behavior. |

### Guides/tutorials/reference pages (before reconstruction)

| File | Classification | Notes |
|---|---|---|
| `docs/guides/integration.md` | ❌ | Blended desired and implemented behavior; overstated runtime capabilities in places. |
| `docs/tutorials/README.md` | ⚠️ | Navigation intent useful, links/content drifted. |
| `docs/tutorials/custom-blocks.md` | ⚠️ | Example-oriented but not consistently validated against current execution path. |
| `docs/tutorials/inventory-validation.md` | ❌ | Included unsupported assumptions for current validator behavior. |
| `docs/tutorials/player-data-validation.md` | ⚠️ | Partly usable examples but with outdated assertions. |
| `docs/reference/skript-syntax.md` | ⚠️ | Core syntax mostly accurate, but some behavior notes were stale. |
| `docs/reference/json-schema.md` | ❌ | Claimed support/enforcement beyond actual validator implementation. |
| `docs/reference/data-types.md` | ⚠️ | Type concepts mostly valid, but integer/number and format details incomplete. |
| `docs/reference/schema-composition.md` | ⚠️ | Core idea valid, edge behavior and error model not fully accurate. |

### Archived/audit/wiki pages (before reconstruction)

All files under previous `docs/archive/**` and `docs/audits/**` were classified as **🗑 OBSOLETE for user-facing documentation** (historical/planning/audit snapshots, not stable external system representation).

## Phase 3 reconstruction actions

### New structure

```text
docs/
  README.md
  quickstart.md
  installation.md
  configuration.md
  guides/
    README.md
    validate-json-file.md
    schema-directory-workflow.md
  tutorials/
    README.md
    first-validation.md
  reference/
    README.md
    skript-syntax.md
    schema-keywords.md
    validation-behavior.md
    config-reference.md
  explanation/
    README.md
    architecture.md
    design-constraints.md
    documentation-audit.md
```

### File operation report

#### Merged/replaced by reconstructed pages

- `docs/reference/json-schema.md`, `docs/reference/data-types.md`, `docs/reference/schema-composition.md` → merged into `docs/reference/schema-keywords.md` and `docs/reference/validation-behavior.md` to remove duplication and align enforcement scope.
- `docs/guides/integration.md` + parts of old install/config pages → split into `docs/configuration.md` + focused guides.
- Tutorial set consolidated into one verified onboarding tutorial: `docs/tutorials/first-validation.md`.

#### Deleted as obsolete or non-source-of-truth artifacts

- `docs/CONTRACT.md`, `docs/api-reference.md`, `docs/new-architecture-plan.md`.
- `docs/TROUBLESHOOTING.md`, `docs/faq.md`, `docs/minecraft-formats.md`.
- Removed previous `docs/archive/**` and `docs/audits/**` trees (historical process docs, not behavior docs).

### Verified examples included in reconstructed docs

1. Skript effect + expression pattern usage (from `validate-simple-example.sk`).
2. Example schema snippet (from `player-profile.schema.json`).
3. YAML validation workflow using `simple-block-example.yml` + `simple-block-schema.json`.

## Structural explanation

- **Diátaxis applied:**
  - Tutorials: learning flow only.
  - Guides: single task procedures.
  - Reference: exact behavior tables/order.
  - Explanation: architecture and constraints.
- **Navigation model:** each page has previous/next/home links to prevent dead ends and enable linear or selective reading.
- **Traceability model:** each page has a “Source mapping” section that maps claims to classes/methods without cluttering main prose.

[← Previous](design-constraints.md) | [Next →](../README.md) | [Home](../../README.md)
