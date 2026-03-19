# Deep System Audit → Executable Implementation Strategy (2026-03-19)

## 🧾 SYSTEM STATE SUMMARY

Schema-Validator has a stable core for object-focused validation (`properties`, `required`, `items`, `enum`, numeric/string bounds, `allOf`, `anyOf`) but suffers from **contract drift** between `/docs` and runtime behavior. The immediate production risks are correctness defects (root dispatch), silent under-validation (documented but unsupported keywords), and integration mismatch (Skript error object contract).

Current maturity by area:
- **Core validation engine:** usable but object-centric and tightly coupled.
- **Schema language support:** partial subset of documented JSON Schema.
- **Skript integration:** functional for basic usage, inconsistent with documented structured errors.
- **Operational contract (docs/config/API):** inconsistent; needs authoritative contract versioning.

---

## ❌ ISSUE BREAKDOWN (DETAILED)

Issue:
- **Name:** Root validator bypasses schema-type dispatch
- **Category:** Runtime Bug
- **Severity:** CRITICAL
- **Root cause:** `ValidationService` always delegates to `ObjectValidator` at root instead of selecting validator by root schema type.
- **Surface impact:** Root array/primitive schemas fail with object-type errors.
- **Hidden risk:** Future feature work appears broken/non-deterministic because validation entrypoint is semantically wrong.

Issue:
- **Name:** Silent under-validation for documented keywords
- **Category:** Contract Violation
- **Severity:** CRITICAL
- **Root cause:** Docs advertise keywords (`oneOf`, `not`, `format`, `multipleOf`, `minItems`, `uniqueItems`, etc.) that parser/validators do not enforce.
- **Surface impact:** Invalid data can pass validation while users believe constraints are active.
- **Hidden risk:** Data integrity defects and loss of trust in validation guarantees.

Issue:
- **Name:** Skript error model mismatch
- **Category:** Contract Violation
- **Severity:** CRITICAL
- **Root cause:** Expression returns `String[]` while docs promise structured `ValidationError` objects.
- **Surface impact:** Documented scripts using error property access fail.
- **Hidden risk:** Ecosystem scripts encode unstable assumptions; migration becomes costly later.

Issue:
- **Name:** `$ref` and `definitions` support is incomplete relative to claims
- **Category:** Feature Gap
- **Severity:** CRITICAL
- **Root cause:** Resolver and loader partially support references, but pointer navigation model is incomplete for full definitions/pointer behavior.
- **Surface impact:** Referenced schemas may resolve unpredictably, especially local pointers.
- **Hidden risk:** Recursive/composed schemas become brittle as schema library grows.

Issue:
- **Name:** Config contract mismatch (`settings.*` vs root keys)
- **Category:** Contract Violation
- **Severity:** MEDIUM
- **Root cause:** Documentation format diverges from actual `config.yml` and code lookup keys.
- **Surface impact:** Operator config changes are ignored or misapplied.
- **Hidden risk:** Production environments become hard to support due to inconsistent setup states.

Issue:
- **Name:** API reference signature drift
- **Category:** Contract Violation
- **Severity:** MEDIUM
- **Root cause:** Docs list methods/overloads not present in implementation.
- **Surface impact:** Integration attempts fail at compile time.
- **Hidden risk:** Maintainers lose confidence in docs as source of truth.

Issue:
- **Name:** Path resolution split-brain (autoload vs effect-time validation)
- **Category:** Architecture
- **Severity:** MEDIUM
- **Root cause:** Auto-load uses configured schema directory while Skript effect uses raw `Path.of` inputs.
- **Surface impact:** Same schema path works in one flow and fails in another.
- **Hidden risk:** Environment-specific failures and difficult reproduction.

Issue:
- **Name:** Composition logic is object-validator bound
- **Category:** Architecture
- **Severity:** MEDIUM
- **Root cause:** `allOf`/`anyOf` execution lives in `ObjectValidator` instead of shared composition layer.
- **Surface impact:** Non-object composition support is constrained.
- **Hidden risk:** `oneOf`/`not` implementation complexity and duplicated logic.

Issue:
- **Name:** Global mutable last-result bridge in Skript integration
- **Category:** Architecture
- **Severity:** LOW
- **Root cause:** static shared `lastResult` has no scope partitioning.
- **Surface impact:** Concurrent scripts can overwrite each other’s results.
- **Hidden risk:** Race-condition-like observability bugs under load.

---

## 🔗 DEPENDENCY GRAPH

### Core dependency chain (must be sequential)
1. **Contract baseline** (supported keyword matrix, docs corrections)
2. **Runtime correctness fix** (root dispatch)
3. **Shared validation context abstraction** (path/state/error aggregation)
4. **Low-risk keyword additions** (`minItems`, `maxItems`, `uniqueItems`, `minProperties`, `maxProperties`, `multipleOf`)
5. **Composition engine extraction** (type-agnostic `allOf`/`anyOf` infrastructure)
6. **High-complexity features** (`oneOf`, `not`, robust `$ref`/pointer semantics)

### Feature dependency map
- **`oneOf` depends on:**
  - root dispatch correctness
  - composition engine (type-agnostic)
  - deterministic error aggregation model
- **`not` depends on:**
  - composition engine
  - unified validation context
- **`multipleOf` depends on:**
  - numeric utility abstraction (precision-safe arithmetic)
- **`minItems/maxItems/uniqueItems` depend on:**
  - schema model extension
  - array validator extension
- **`minProperties/maxProperties` depend on:**
  - schema model extension
  - object validator extension
- **`$ref` hardening depends on:**
  - explicit reference contract
  - schema graph/pointer navigation improvements
  - recursion/cycle guard in context
- **Structured Skript errors depend on:**
  - stable canonical error DTO contract
  - adapter layer for backward compatibility

### Parallelization opportunities
Can run in parallel after Phase A contract stabilization:
- Config/API/docs fixes
- Low-risk keyword implementation streams (array cardinality vs object cardinality)
- Contract/regression test scaffolding

Blocked by architecture:
- `oneOf`, `not`, deep `$ref` behaviors should wait for shared composition + context layers.

---

## 🛠️ IMPLEMENTATION UNITS

### Unit 1
- **Feature/Issue:** Root validator dispatch bug
- **Strategy:** Use `ValidatorDispatcher.forSchema(schema)` at validation entrypoint.
- **Files/components affected:** `ValidationService`, validator tests.
- **Required refactor:** Minimal (entrypoint selection only).
- **Backward compatibility plan:** Preserve error model and existing API signatures.
- **Risk level:** MEDIUM.
- **Rollback strategy:** Feature toggle (`validation.root-dispatch-v2`) or quick revert commit.

### Unit 2
- **Feature/Issue:** Unsupported documented keywords causing silent under-validation
- **Strategy:** Add unsupported-keyword detection in schema loader + warnings + optional fail-fast mode.
- **Files/components affected:** `FileSchemaLoader`, config docs/runtime flags, logging.
- **Required refactor:** Add keyword scan utility.
- **Backward compatibility plan:** Default behavior remains permissive; warnings only unless fail-fast enabled.
- **Risk level:** LOW-MEDIUM.
- **Rollback strategy:** Disable warnings/fail-fast via config flag.

### Unit 3
- **Feature/Issue:** Config and API contract drift
- **Strategy:** Align docs to runtime now; optionally introduce alias reader for legacy doc keys.
- **Files/components affected:** `/docs/configuration.md`, `/docs/api-reference.md`, `PluginConfig` (if aliasing added).
- **Required refactor:** None or very small key alias handling.
- **Backward compatibility plan:** Support both old/new keys for one deprecation cycle if aliases added.
- **Risk level:** LOW.
- **Rollback strategy:** Remove aliases; retain current keys only.

### Unit 4
- **Feature/Issue:** Skript structured error mismatch
- **Strategy:** Keep current string expression; add new structured expression/effect accessors.
- **Files/components affected:** Skript integration classes + docs.
- **Required refactor:** Adapter DTO layer from internal `ValidationError`.
- **Backward compatibility plan:** Existing scripts continue using string list unchanged.
- **Risk level:** MEDIUM.
- **Rollback strategy:** Keep legacy path; disable new syntax registration.

### Unit 5
- **Feature/Issue:** Low-risk keyword expansion (array/object cardinality + multipleOf)
- **Strategy:** Extend `Schema` model and parser; implement validators with deterministic errors.
- **Files/components affected:** `Schema`, `FileSchemaLoader`, `ArrayValidator`, `ObjectValidator`, `PrimitiveValidator`, tests.
- **Required refactor:** Constraint fields + parser/validator wiring.
- **Backward compatibility plan:** New constraints are opt-in via schema keyword usage.
- **Risk level:** MEDIUM.
- **Rollback strategy:** Guard by per-keyword feature flags; disable on regressions.

### Unit 6
- **Feature/Issue:** Composition extraction for `oneOf`/`not`
- **Strategy:** Build `CompositeValidator` layer reusable across all schema types.
- **Files/components affected:** validator package, dispatcher, schema composition parsing, tests.
- **Required refactor:** Move composition logic out of `ObjectValidator`.
- **Backward compatibility plan:** Keep existing `allOf`/`anyOf` behavior parity via conformance tests.
- **Risk level:** HIGH.
- **Rollback strategy:** Keep legacy composition path behind fallback toggle.

### Unit 7
- **Feature/Issue:** `$ref`/pointer robustness
- **Strategy:** Define explicit pointer support scope; implement resolver against richer schema node model.
- **Files/components affected:** `SchemaRefResolver`, schema model/parser, registry interactions, tests.
- **Required refactor:** Potential schema AST/pointer navigation utilities.
- **Backward compatibility plan:** Keep current simple ref behavior as fallback mode.
- **Risk level:** HIGH.
- **Rollback strategy:** fallback to current resolver path by config toggle.

### Unit 8
- **Feature/Issue:** Path resolution consistency
- **Strategy:** Standardize resolution rules for validate effect (absolute, plugin-relative, configured schema dir).
- **Files/components affected:** `EffValidateData`, docs, integration tests.
- **Required refactor:** centralized path resolver utility.
- **Backward compatibility plan:** Continue accepting raw paths; add deterministic resolution order.
- **Risk level:** MEDIUM.
- **Rollback strategy:** revert to raw path mode via config toggle.

---

## 🧱 EXECUTION PHASES

### Phase A — Contract Stabilization
- **Scope:** Align docs with current behavior; add unsupported-keyword warnings; freeze misleading examples.
- **Entry conditions:** Audit accepted; docs owners and maintainers aligned.
- **Exit validation criteria:**
  - Published supported-feature matrix.
  - Config/API/Skript docs match runtime.
  - Unsupported-keyword warnings observable in logs.
- **Risk level:** LOW.

### Phase B — Runtime Corrections
- **Scope:** Fix correctness bugs (root dispatch, path semantics baseline).
- **Entry conditions:** Phase A complete; regression harness for current behavior exists.
- **Exit validation criteria:**
  - Root primitive/array schema tests pass.
  - No regressions in object-schema flows.
  - Path resolution behavior documented and tested.
- **Risk level:** MEDIUM.

### Phase C — Feature Expansion (Low Risk First)
- **Scope:** Implement low-complexity constraints (`minItems`, `maxItems`, `uniqueItems`, `minProperties`, `maxProperties`, `multipleOf`).
- **Entry conditions:** Phase B stable across CI.
- **Exit validation criteria:**
  - Contract tests (valid/invalid fixtures) per new keyword.
  - Feature flags available for each new keyword family.
- **Risk level:** MEDIUM.

### Phase D — High Complexity Features
- **Scope:** `oneOf`, `not`, and `$ref` scope improvements.
- **Entry conditions:** Shared composition/context infrastructure available.
- **Exit validation criteria:**
  - Deterministic error aggregation for composed schemas.
  - Cycle-safe reference tests pass.
  - Performance baseline unchanged within agreed threshold.
- **Risk level:** HIGH.

### Phase E — Architecture Refactor
- **Scope:** Modular validator engine, pluggable keyword handlers, validation context, adapter isolation.
- **Entry conditions:** Phase D feature behavior stabilized and covered by contract/regression suites.
- **Exit validation criteria:**
  - New keyword can be added without touching core dispatcher logic.
  - Legacy behavior preserved under compatibility tests.
  - Operational observability dashboards/log counters in place.
- **Risk level:** HIGH.

---

## 🧪 VALIDATION STRATEGY

### 1) Contract Tests (per documented feature)
For each supported keyword:
- `valid/<keyword>/*.json|yml` fixtures that must pass.
- `invalid/<keyword>/*.json|yml` fixtures that must fail with expected error code/path.
- Golden assertions for error shape and message invariants.

### 2) Regression Tests
- Preserve legacy behavior for existing schemas and Skript flows.
- Snapshot tests for error-path formatting (`$`, `$.a.b`, `$.arr[0]`).
- Compatibility tests for prior examples before/after each phase.

### 3) Docs Execution Tests
- Parse runnable snippets from docs and execute in test harness.
- Docs CI gate fails if examples reference unsupported keywords without disclaimer.
- Ensure quickstart path and config examples pass as-is.

### 4) Safety Checks
- Runtime detection for ignored/unknown keywords.
- Fail-fast optional mode for strict environments.
- CI check that documented “supported” matrix equals implemented keyword registry.

---

## 🚨 RISK CONTROL PLAN

### Risk: Silent under-validation (CRITICAL)
- **Detection method:** loader logs + test that unsupported keyword usage emits warning/error.
- **Prevention strategy:** supported-keyword registry, strict mode, docs matrix CI gate.
- **Monitoring/logging:** warning counter metric (`unsupported_keyword_count`) and per-keyword frequency.

### Risk: Root-dispatch correctness failure (CRITICAL)
- **Detection method:** dedicated tests for root primitive/array/object schemas.
- **Prevention strategy:** centralize entry dispatch and enforce via architectural unit test.
- **Monitoring/logging:** startup self-check validates sample schemas for each root type.

### Risk: Skript error contract breakage (CRITICAL)
- **Detection method:** integration tests for both legacy string expression and new structured API.
- **Prevention strategy:** additive rollout; never repurpose existing expression semantics.
- **Monitoring/logging:** deprecation warnings when legacy path is used (after structured API matures).

### Risk: `$ref` recursion/pointer instability (CRITICAL)
- **Detection method:** cycle, deep-pointer, and mixed local/external reference test suites.
- **Prevention strategy:** validation context with recursion guard + pointer scope contract.
- **Monitoring/logging:** resolver diagnostics (cache hit/miss, unresolved refs, cycle detections).

---

## 🧠 FINAL STRATEGY

### What should be done FIRST
1. Stabilize contract truth: docs matrix + warning system for unsupported keywords.
2. Fix root dispatch correctness defect.
3. Establish contract/regression/docs execution tests as release gates.

### What must NEVER be rushed
- `oneOf`/`not` and deep `$ref` semantics. These require composition/context architecture first.
- Structured Skript error migration without additive compatibility path.

### Where the system is most fragile
- Contract drift between docs and runtime (creates user-facing false confidence).
- Object-centric validator coupling (slows safe feature growth).
- Reference/composition complexity without unified validation context.
