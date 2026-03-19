# Implementation Task System — Schema-Validator

## Executive Summary

### Project Overview

This document establishes the complete task system for executing the reordering, reconstruction, and evolution plan of the Schema-Validator system, derived from the Deep System Audit dated 2026-03-19.

### Scope Metrics

| Metric | Value |
|--------|-------|
| **Total Issues Identified** | 9 |
| **Total Derived Tasks** | 28 |
| **Implementation Units** | 8 |
| **Execution Phases** | 5 |
| **Critical Tasks (P1)** | 12 |
| **High Priority Tasks (P2)** | 11 |
| **Medium Priority Tasks (P3)** | 5 |
| **Total Effort Estimate** | 120-160 hours |

### Severity and Impact Taxonomy

| Severity | Definition | Business Impact |
|----------|------------|-----------------|
| **CRITICAL** | Defect causing data loss, incorrect validation, or contract breaches with the user | High — Affects system reliability and user trust |
| **MEDIUM** | Inconsistency between documentation and runtime, or architecture that limits future functionality | Medium — Affects developer experience and maintainability |
| **LOW** | Technical code issue with no immediate observable behavior impact | Low — Improves internal technical quality |

### Mapped Risks

| Risk ID | Description | Probability | Impact | Mitigation |
|---------|-------------|-------------|--------|------------|
| R-01 | Silent under-validation continues after Phase A | High | Critical | Implement alerting system and regression tests |
| R-02 | Root dispatch bug not detected in production | Medium | Critical | Complete test coverage for root types |
| R-03 | Contract breakage in existing Skript scripts | Medium | Critical | Additive strategy, never removal |
| R-04 | Infinite recursion in $ref | Low | Critical | Recursion guard in validation context |
| R-05 | Delay in architectural dependencies | Medium | High | Plan intermediate milestones |
| R-06 | Docs/runtime misalignment re-introduced | High | High | CI gate for supported matrix validation |

### Execution Recommendations

1. **Start with Phase A** — Contract stabilization is a prerequisite for all other phases and must be completed before any runtime implementation
2. **Controlled parallelization** — After Phase A, config/API/docs and low-risk keyword implementations can run in parallel
3. **Mandatory feature flags** — Every new functionality must be delivered with feature toggle for quick rollback
4. **Contract tests as gate** — Each release must pass documented contract tests
5. **Clear ownership** — Each task must have a single clearly designated owner

---

## Priority Legend

| Priority | Criterion | Suggested SLA |
|----------|-----------|---------------|
| **P1 (Critical)** | CRITICAL issues from audit, blocking dependencies | 1-2 weeks |
| **P2 (High)** | MEDIUM issues, supporting functionality | 2-4 weeks |
| **P3 (Medium)** | LOW issues, technical improvements | 4-8 weeks |

---

## Phases and Delivery Milestones

### Phase A — Contract Stabilization

**Objective:** Align documentation with current behavior; add alerting system for unsupported keywords

**Estimated Duration:** 2-3 weeks

**Delivery Milestone A1:** Published supported features matrix and CI tests

**Tasks:**

| ID | Audit Ref. | Task | Priority | Owner | Acceptance Criteria | Effort | Dependencies | Status |
|----|------------|------|----------|-------|---------------------|--------|--------------|--------|
| A-001 | Issue #2 | Create supported keywords registry in the system | P1 | Tech Lead | Registry available in code and documented | 4h | — | **Done** |
| A-002 | Issue #2 | Implement unsupported keyword detector in SchemaLoader | P1 | Backend Dev | Logger emits warning for each unsupported keyword | 8h | A-001 | **Done** |
| A-003 | Issue #2 | Add optional fail-fast mode via config | P1 | Backend Dev | Config toggle enables exception on unsupported keyword | 4h | A-002 | **Done** |
| A-004 | Issue #5 | Review and correct configuration docs (settings.* vs root keys) | P2 | Tech Writer | Docs reflect actual config.yml keys | 6h | — | **Done** |
| A-005 | Issue #6 | Review and correct API reference (methods/overloads) | P2 | Tech Writer | API reference lists methods existing in code | 6h | — | **Done** |
| A-006 | Issue #2 | Publish supported features matrix in /docs | P1 | Tech Writer | Matrix visible in main documentation | 4h | A-001 | **Done** |
| A-007 | Issue #2 | Add CI gate that validates matrix vs implementation | P1 | DevOps | Build fails if matrix diverges from registry | 8h | A-001, A-006 | Pending |

---

### Phase B — Runtime Corrections

**Objective:** Fix correctness defects (root dispatch, path semantics)

**Estimated Duration:** 2-3 weeks

**Delivery Milestone B1:** Root primitive/array/object schemas pass in tests; Path resolution functional

**Tasks:**

| ID | Audit Ref. | Task | Priority | Owner | Acceptance Criteria | Effort | Dependencies | Status |
|----|------------|------|----------|-------|---------------------|--------|--------------|--------|
| B-001 | Issue #1 | Fix ValidationService to use ValidatorDispatcher at entrypoint | P1 | Backend Dev | Root array/primitive schemas use correct validator | 6h | A-001 | Pending |
| B-002 | Issue #1 | Add tests for root schemas of each type (object, array, primitive) | P1 | QA Engineer | 3+ tests cover each root schema type | 8h | B-001 | Pending |
| B-003 | Issue #7 | Standardize path resolution (autoload vs effect-time) | P2 | Backend Dev | Path resolution document created and implemented | 10h | A-004 | Pending |
| B-004 | Issue #7 | Implement centralized path resolver utility | P2 | Backend Dev | EffValidateData uses centralized resolver | 6h | B-003 | Pending |
| B-005 | Issue #7 | Add integration tests for both path flows | P2 | QA Engineer | Tests cover autoload and validate effect | 6h | B-004 | Pending |
| B-006 | Issue #1 | Run regression tests for object-schema flows | P1 | QA Engineer | 0 regressions in existing functionality | 4h | B-001 | Pending |

---

### Phase C — Feature Expansion (Low Risk)

**Objective:** Implement low-complexity constraints

**Estimated Duration:** 3-4 weeks

**Delivery Milestone C1:** Keywords minItems, maxItems, uniqueItems, minProperties, maxProperties, multipleOf implemented

**Tasks:**

| ID | Audit Ref. | Task | Priority | Owner | Acceptance Criteria | Effort | Dependencies | Status |
|----|------------|------|----------|-------|---------------------|--------|--------------|--------|
| C-001 | Issue #2 | Implement minItems and maxItems | P2 | Backend Dev | Validators process constraint and generate specific errors | 8h | B-001 | Pending |
| C-002 | Issue #2 | Implement uniqueItems | P2 | Backend Dev | Validator detects duplicates in arrays | 6h | C-001 | Pending |
| C-003 | Issue #2 | Implement minProperties and maxProperties | P2 | Backend Dev | Validators process object cardinality constraint | 6h | B-001 | Pending |
| C-004 | Issue #2 | Implement multipleOf with safe numeric precision | P2 | Backend Dev | Validator performs division without floating point errors | 8h | B-001 | Pending |
| C-005 | Issue #2 | Add test fixtures (valid/invalid) for each keyword | P2 | QA Engineer | 5+ cases per keyword (valid and invalid) | 10h | C-001, C-002, C-003, C-004 | Pending |
| C-006 | Issue #2 | Implement feature flags per keyword family | P2 | Backend Dev | Toggle disables specific keywords via config | 4h | C-001 | Pending |
| C-007 | Issue #2 | Add contract tests per keyword | P2 | QA Engineer | Tests fail if behavior diverges from specification | 8h | C-005 | Pending |

---

### Phase D — High Complexity Features

**Objective:** Implement oneOf, not, and $ref improvements

**Estimated Duration:** 4-6 weeks

**Delivery Milestone D1:** Deterministic composition, cycle-safe references, correctly aggregated errors

**Tasks:**

| ID | Audit Ref. | Task | Priority | Owner | Acceptance Criteria | Effort | Dependencies | Status |
|----|------------|------|----------|-------|---------------------|--------|--------------|--------|
| D-001 | Issue #8 | Extract composition layer (CompositeValidator) | P1 | Backend Dev | allOf/anyOf work for all schema types | 12h | B-001 | Pending |
| D-002 | Issue #8 | Implement oneOf with deterministic error aggregation | P1 | Backend Dev | Exactly one schema must validate; specific errors if multiple/none | 10h | D-001 | Pending |
| D-003 | Issue #8 | Implement not with negation validation | P1 | Backend Dev | not validates that schema should not match | 8h | D-001 | Pending |
| D-004 | Issue #4 | Implement explicit $ref/pointer scope | P1 | Backend Dev | $ref scope document defined and implemented | 8h | D-001 | Pending |
| D-005 | Issue #4 | Improve resolver against rich schema node model | P1 | Backend Dev | Resolver navigates pointers correctly | 10h | D-004 | Pending |
| D-006 | Issue #4 | Add recursion guard in validation context | P1 | Backend Dev | Cycles are detected and exception thrown | 6h | D-005 | Pending |
| D-007 | Issue #4 | Implement cycle, deep-pointer, and mixed reference tests | P1 | QA Engineer | Test suite passes for all scenarios | 10h | D-005 | Pending |
| D-008 | Issue #3 | Define canonical ValidationError DTO contract | P1 | Tech Lead | DTO defined with structured fields | 4h | — | Pending |
| D-009 | Issue #3 | Implement DTO adapter layer for Skript | P1 | Backend Dev | Adapter converts internal errors to Skript format | 8h | D-008 | Pending |
| D-010 | Issue #3 | Add new structured expression/accessors in Skript | P1 | Backend Dev | New syntax available for structured error access | 6h | D-009 | Pending |

---

### Phase E — Architectural Refactor

**Objective:** Modular engine, pluggable keyword handlers, isolated validation context

**Estimated Duration:** 4-8 weeks

**Delivery Milestone E1:** New keyword can be added without touching core dispatcher logic

**Tasks:**

| ID | Audit Ref. | Task | Priority | Owner | Acceptance Criteria | Effort | Dependencies | Status |
|----|------------|------|----------|-------|---------------------|--------|--------------|--------|
| E-001 | — | Modularize validation engine (separate packages) | P2 | Architect | Package structure redefines responsibility | 16h | D-001 | Pending |
| E-002 | — | Implement plugin keyword handlers | P2 | Backend Dev | Handlers can be registered without modifying dispatcher | 12h | E-001 | Pending |
| E-003 | — | Isolate ValidationContext as abstraction | P2 | Backend Dev | Context manages path, state, error aggregation | 10h | E-001 | Pending |
| E-004 | Issue #9 | Remove global mutable last-result bridge | P3 | Backend Dev | Result passed via context, not shared | 6h | E-003 | Pending |
| E-005 | — | Implement context isolation tests | P2 | QA Engineer | Context is thread-safe and isolated per execution | 8h | E-003 | Pending |
| E-006 | — | Add observability (metrics, structured logs) | P3 | DevOps | Dashboards show validation, error, warning counters | 10h | E-001 | Pending |
| E-007 | — | Create legacy compatibility tests | P2 | QA Engineer | Legacy behavior preserved in all scenarios | 8h | E-002 | Pending |
| E-008 | — | Document modularized architecture | P3 | Tech Writer | Docs reflect new architecture | 6h | E-001 | Pending |

---

## Task Dependencies Matrix

```
A-001 ──┬──► A-002 ──► A-003 ──► A-007
        │
        └──► A-006 ──► A-007

A-004 ──► B-003 ──► B-004 ──► B-005
A-005 ──►

B-001 ──► B-002 ──► B-006
        │
        ├──► C-001 ──► C-002
        │          ├──► C-005
        │          └──► C-006
        │
        ├──► C-003 ──► C-005
        │
        └──► C-004 ──► C-005

D-001 ──► D-002 ──►
        │
        ├──► D-003 ──►
        │
        ├──► D-004 ──► D-005 ──► D-006 ──► D-007
        │
        └──► D-008 ──► D-009 ──► D-010

E-001 ──► E-002 ──► E-007
        │
        ├──► E-003 ──► E-004 ──►
        │          └──► E-005
        │
        └──► E-006 ──► E-008
```

---

## Audit → Tasks Traceability

| Audit Issue | Severity | Derived Tasks |
|-------------|----------|---------------|
| Issue #1: Root validator bypasses schema-type dispatch | CRITICAL | B-001, B-002, B-006 |
| Issue #2: Silent under-validation for documented keywords | CRITICAL | A-001, A-002, A-003, A-006, A-007, C-001, C-002, C-003, C-004, C-005, C-006, C-007 |
| Issue #3: Skript error model mismatch | CRITICAL | D-008, D-009, D-010 |
| Issue #4: $ref and definitions support incomplete | CRITICAL | D-004, D-005, D-006, D-007 |
| Issue #5: Config contract mismatch | MEDIUM | A-004 |
| Issue #6: API reference signature drift | MEDIUM | A-005 |
| Issue #7: Path resolution split-brain | MEDIUM | B-003, B-004, B-005 |
| Issue #8: Composition logic object-validator bound | MEDIUM | D-001, D-002, D-003 |
| Issue #9: Global mutable last-result bridge | LOW | E-004 |

---

## Tracking Structure

| Column | Description |
|--------|-------------|
| **ID** | Unique task identifier in format [PHASE]-[SEQ] |
| **Audit Ref.** | Original issue from audit document |
| **Task** | Concise description of the activity |
| **Priority** | P1 (Critical), P2 (High), P3 (Medium) |
| **Owner** | Person responsible for execution |
| **Acceptance Criteria** | Conditions for task acceptance |
| **Effort** | Estimated hours |
| **Dependencies** | Predecessor task IDs |
| **Status** | Pending, In Progress, Blocked, Done |

---

## Ready and Done Definitions

### Ready for Execution
- Dependent tasks completed
- Acceptance criteria understood by owner
- Required resources available

### Done
- Code implemented and reviewed
- Unit and integration tests passing
- Acceptance criteria verified
- Documentation updated where applicable
- PR/Merge request approved

---

## Continuous Risk Controls

| Checkpoint | Frequency | Responsible | Gate |
|------------|-----------|-------------|------|
| Contract Compliance Check | Per release | QA + Tech Lead | CI fails if matrix != registry |
| Regression Suite | Per PR | CI/CD | 100% pass required |
| Performance Baseline | Weekly | DevOps | <10% degradation allowed |
| Security Scan | Per release | DevOps | 0 vulnerabilities HIGH |

---

## Current Project Status

### Project Phase: PRE-PHASE A — Contract Stabilization Not Started

**Last Updated:** 2026-03-19

### Current State Analysis

Based on source code analysis conducted on 2026-03-19, the Schema-Validator system is currently in a **pre-implementation state** relative to the audit recommendations. The following critical findings have been identified:

| Issue ID | Issue Description | Audit Severity | Code Status | Impact on Implementation |
|----------|-------------------|----------------|-------------|-------------------------|
| Issue #1 | Root validator bypasses schema-type dispatch | CRITICAL | **NOT FIXED** - ValidatorDispatcher exists but not used by ValidationService | Task B-001 remains blocked by Phase A completion |
| Issue #2 | Silent under-validation for documented keywords | CRITICAL | **In Progress** - SupportedKeywordsRegistry + detector implemented | Tasks A-001, A-002, A-003, A-006 done; A-007 pending |
| Issue #3 | Skript error model mismatch | CRITICAL | **PARTIAL** - ValidationError DTO exists but not exposed to Skript | Task D-008/D-009 ready to proceed |
| Issue #4 | $ref and definitions support incomplete | CRITICAL | **PARTIAL** - $ref parsing exists in FileSchemaLoader | Tasks D-004→D-007 pending |
| Issue #5 | Config contract mismatch | MEDIUM | **NOT VERIFIED** - Needs documentation comparison | Task A-004 pending |
| Issue #6 | API reference signature drift | MEDIUM | **NOT VERIFIED** - Needs documentation comparison | Task A-005 pending |
| Issue #7 | Path resolution split-brain | MEDIUM | **NOT FIXED** - EffValidateData uses raw Path.of() | Tasks B-003→B-005 pending |
| Issue #8 | Composition logic object-validator bound | MEDIUM | **NOT EXTRACTED** - Composition in ObjectValidator | Tasks D-001→D-003 pending |
| Issue #9 | Global mutable last-result bridge | LOW | **NOT FIXED** - Static lastResult in SkriptValidationBridge | Task E-004 pending |

### Missing Keywords (Not Implemented in Code)

The following keywords are documented but NOT currently parsed/enforced in FileSchemaLoader:

| Keyword | Documented In | Parser Status | Validator Status |
|---------|--------------|---------------|------------------|
| minItems | docs/reference/json-schema.md, docs/construction.md | **NOT IMPLEMENTED** | Not applicable |
| maxItems | docs/reference/json-schema.md, docs/construction.md | **NOT IMPLEMENTED** | Not applicable |
| uniqueItems | docs/reference/json-schema.md, docs/construction.md | **NOT IMPLEMENTED** | Not applicable |
| minProperties | docs/reference/data-types.md | **NOT IMPLEMENTED** | Not applicable |
| maxProperties | docs/reference/data-types.md | **NOT IMPLEMENTED** | Not applicable |

### Implemented Keywords (Already Working)

| Keyword | Parser | Validator | Notes |
|---------|--------|-----------|-------|
| type (object/array/string/number/integer/boolean/null/any) | ✅ Implemented | ✅ Working | SchemaType enum complete |
| properties | ✅ Implemented | ✅ Working | |
| items | ✅ Implemented | ✅ Working | |
| required | ✅ Implemented | ✅ Working | |
| additionalProperties | ✅ Implemented | ✅ Working | |
| minimum/maximum | ✅ Implemented | ✅ Working | |
| exclusiveMinimum/exclusiveMaximum | ✅ Implemented | ✅ Working | |
| minLength/maxLength | ✅ Implemented | ✅ Working | |
| pattern | ✅ Implemented | ✅ Working | |
| format | ✅ Implemented | ✅ Working | FormatValidator class exists |
| multipleOf | ✅ Implemented | ✅ Working | Parsed but validator needs verification |
| enum | ✅ Implemented | ✅ Working | |
| $ref | ✅ Implemented | ⚠️ Partial | Basic support exists |
| allOf | ✅ Implemented | ✅ Working | |
| anyOf | ✅ Implemented | ✅ Working | |
| oneOf | ✅ Implemented | ✅ Working | OneOfValidator exists |
| not | ✅ Implemented | ✅ Working | NotValidator exists |
| if/then/else | ✅ Implemented | ✅ Working | ConditionalValidator exists |

### Progress Summary

| Phase | Status | Tasks Completed | Tasks Pending | Tasks Blocked |
|-------|--------|-----------------|----------------|---------------|
| Phase A — Contract Stabilization | **In Progress** | 5 | 2 | 0 |
| Phase B — Runtime Corrections | **Blocked** | 0 | 6 | 6 (waiting A-001) |
| Phase C — Feature Expansion | **Blocked** | 0 | 7 | 7 (waiting B-001) |
| Phase D — High Complexity | **Blocked** | 0 | 10 | 10 (waiting C completion) |
| Phase E — Architectural Refactor | **Blocked** | 0 | 8 | 8 (waiting D completion) |

### Next Steps

1. **Start Phase A - Contract Stabilization**
   - Task A-001: Create supported keywords registry
   - Task A-002: Implement unsupported keyword detector
   - Task A-003: Add fail-fast mode
   - Task A-004: Review/correct config docs
   - Task A-005: Review/correct API reference docs
   - Task A-006: Publish supported features matrix
   - Task A-007: Add CI gate

### Blocking Dependencies

- **Phase B** cannot start until **A-001** is complete
- **Phase C** cannot start until **B-001** is complete  
- **Phase D** cannot start until **Phase C** is complete
- **Phase E** cannot start until **Phase D** is complete

---

*Document automatically generated based on deep-system-audit-2026-03-19.md*
*Version: 1.0 | Date: 2026-03-19*