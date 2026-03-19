# Deep System Audit & Safe Implementation Plan (2026-03-19)

## 🧾 SYSTEM OVERVIEW

The project already has a solid baseline for validating object-centered JSON/YAML structures with Skript integration: core schema parsing, object/array/primitive validators, and composition support for `allOf`/`anyOf` are present. However, the documentation advertises a broader JSON Schema contract than the implementation enforces. The largest risk is **silent under-validation**: schema authors can use documented keywords that are parsed or documented but not actually validated at runtime.

The current runtime path is:
1. `FileSchemaLoader` parses schema files into the `Schema` model.
2. `ValidationService` runs validation using `ObjectValidator` as root validator.
3. Skript effect `validate yaml/json ...` loads data + schema and stores the result in a static bridge.
4. Skript expression returns stringified errors only.

---

## 🔍 FEATURE GAP TABLE

| Feature | Status | Severity | Notes |
|--------|--------|--------|------|
| `type` (object/array/string/integer/number/boolean/null/any) | ⚠️ Partially implemented | 🔴 CRITICAL | Works when root schema is object; `ValidationService` always starts at `ObjectValidator`, so non-object root schemas fail before dispatch. |
| `properties` | ✅ Fully implemented | 🟠 MEDIUM | Implemented for object schemas via recursive property validation. |
| `required` | ✅ Fully implemented | 🟠 MEDIUM | Required keys are checked against map presence. |
| `additionalProperties` | ✅ Fully implemented | 🟠 MEDIUM | Enforced for keys not in `properties` and not matching `patternProperties`. |
| `patternProperties` | ✅ Fully implemented | 🟠 MEDIUM | Regex-based matching and child validation is present. |
| `items` | ✅ Fully implemented | 🟠 MEDIUM | Array item schema is recursively validated. |
| `minimum` / `maximum` | ✅ Fully implemented | 🟠 MEDIUM | Enforced in primitive numeric validation. |
| `exclusiveMinimum` / `exclusiveMaximum` | ✅ Fully implemented | 🟠 MEDIUM | Boolean-style exclusivity is implemented. |
| `minLength` / `maxLength` | ✅ Fully implemented | 🟠 MEDIUM | Enforced for strings. |
| `pattern` | ✅ Fully implemented | 🟠 MEDIUM | Regex compiled and enforced for strings. |
| `enum` | ✅ Fully implemented | 🟠 MEDIUM | Exact-value membership check exists. |
| `$ref` | ⚠️ Partially implemented | 🔴 CRITICAL | Resolver exists, but validation root path and local pointer navigation are incomplete for `definitions`. |
| `definitions` | ⚠️ Partially implemented | 🟠 MEDIUM | Loader parses definitions map, but `Schema` model does not expose it for local `#/definitions/...` navigation. |
| `allOf` | ✅ Fully implemented | 🟠 MEDIUM | Implemented in object validator with prefixed errors. |
| `anyOf` | ✅ Fully implemented | 🟠 MEDIUM | Implemented in object validator with summary error. |
| `oneOf` | ❌ Not implemented | 🔴 CRITICAL | Documented in JSON reference, but neither parser nor validator has support. |
| `not` | ❌ Not implemented | 🟠 MEDIUM | Documented but not parsed/validated. |
| `format` | ❌ Not implemented | 🟠 MEDIUM | Documented supported formats are not validated. |
| `multipleOf` | ❌ Not implemented | 🟠 MEDIUM | Documented but not validated. |
| `minItems` / `maxItems` | ❌ Not implemented | 🟠 MEDIUM | Documented in references and API page, absent in array validator/schema model. |
| `uniqueItems` | ❌ Not implemented | 🟠 MEDIUM | Documented in references and API page, absent in array validator/schema model. |
| `minProperties` / `maxProperties` | ❌ Not implemented | 🟠 MEDIUM | Documented object keywords not implemented. |
| `dependencies` | ❌ Not implemented | 🟠 MEDIUM | Documented object keyword not implemented. |
| Multiple types (`"type": [ ... ]`) | ❌ Not implemented | 🟠 MEDIUM | Docs claim support; parser only accepts string type token. |
| Skript error objects (`ValidationError`) | ❌ Not implemented as documented | 🔴 CRITICAL | Expression returns `String[]`, not typed error objects/properties. |
| Config structure `settings.*` | ❌ Not implemented as documented | 🟠 MEDIUM | Runtime expects top-level keys (`schema-directory`, `auto-load`, etc.). |
| Schema/data file resolution via plugin folders | ⚠️ Partially implemented | 🟠 MEDIUM | Auto-load uses configured directory, but Skript validate effect uses raw `Path.of(...)` arguments. |

---

## ❌ CONTRACT VIOLATIONS

1. **Documented JSON Schema coverage exceeds runtime enforcement**
   - **Expected (docs):** keywords like `oneOf`, `not`, `format`, `multipleOf`, `minItems`, `uniqueItems`, `dependencies` are available.
   - **Actual (code):** parser/validators enforce only a subset (type/properties/required/additionalProperties/patternProperties/items/min-max/minLength-maxLength/pattern/enum/allOf/anyOf).
   - **Impact:** users write schemas believing constraints are active, causing false-positive validation success and data integrity risk.

2. **Skript error contract mismatch**
   - **Expected (docs):** `last schema validation errors` returns objects with `nodePath`, `expectedType`, etc.
   - **Actual (code):** expression returns stringified errors (`String[]`).
   - **Impact:** documented Skript snippets break; users cannot introspect structured error fields.

3. **Configuration shape mismatch**
   - **Expected (docs):** nested `settings.cache-enabled`, `settings.cache-expiry`, `settings.schemas-folder`.
   - **Actual (code/resources):** top-level keys `schema-directory`, `auto-load`, `cache-enabled`, `validation-on-load`; `cache-expiry` is not read from config for registry creation.
   - **Impact:** admins may configure documented keys that have no effect.

4. **API reference signatures mismatch**
   - **Expected (docs):** methods like `loadSchema(...)`, directory bulk loaders, and constructor examples not matching current signatures.
   - **Actual (code):** `FileSchemaLoader.load(Path, String)` / `parseSchema(...)`; no documented overload set as written.
   - **Impact:** Java integrators get compile-time failures following docs.

5. **Root validation dispatch mismatch**
   - **Expected (docs):** dispatcher chooses validator by schema type.
   - **Actual (code):** `ValidationService` always invokes `ObjectValidator` as root, bypassing root-type dispatch.
   - **Impact:** primitive/array root schemas fail incorrectly (`Expected an object/map node`).

6. **`$ref`/`definitions` behavior inconsistency**
   - **Expected (docs and comments):** local and external `$ref` support including definitions pointers.
   - **Actual (code):** definitions are extracted in loader but not navigable via schema tree for local pointers; resolver navigation handles `properties`/`items` only.
   - **Impact:** documented reference patterns can resolve unpredictably or fail.

---

## 🚨 CRITICAL ISSUES

1. **Silent under-validation from unsupported-but-documented keywords** (`oneOf`, `format`, `multipleOf`, etc.).
2. **Root-type validation bug in `ValidationService`** prevents correct validation for non-object roots.
3. **Skript error type mismatch** breaks error-handling examples and automation.
4. **`$ref` expectations exceed effective local-pointer implementation**, especially with `definitions`.

---

## ⚠️ MEDIUM ISSUES

1. Config docs do not match runtime keys/defaults.
2. API reference has outdated signatures and capabilities.
3. Skript file path resolution behavior is under-documented and inconsistent with plugin folder examples.
4. Cache expiry is documented but not wired from config into registry constructor.
5. Parser/validator architecture is object-centric and makes expansion costly without intermediate abstractions.

---

## 🟢 LOW ISSUES

1. Minor wording inconsistencies around NUMBER semantics in docs can confuse users.
2. Some docs mix conceptual future features with current behavior without status labels.
3. Existing practical audit is useful but not integrated as an authoritative compatibility matrix.

---

## 🧠 ARCHITECTURE ANALYSIS

### Stress points and fragility

1. **Validator entrypoint is tightly coupled to object validation**
   - `ValidationService` chooses `ObjectValidator` directly, not dispatcher-by-root type.
   - This violates open/closed expectations and creates correctness bugs for non-object roots.

2. **Composition and reference logic embedded in `ObjectValidator`**
   - `allOf`, `anyOf`, and `$ref` handling are object-validator concerns today.
   - This blocks composition/ref usage for non-object schemas and reduces reuse.

3. **Schema model lacks explicit keyword modules**
   - Every new keyword requires touching `Schema`, loader, and one or more validators.
   - No pluggable keyword-rule registry exists, so growth increases coupling.

4. **Skript bridge exposes global mutable last result**
   - Static shared `lastResult` is simple but not contextualized by player/event/script.
   - Concurrent validations can overwrite each other in multiplayer/script-heavy environments.

5. **Path resolution split-brain**
   - Plugin has configured schema directory for auto-load, but effect uses direct OS paths.
   - Operational behavior depends on runtime working directory assumptions.

6. **Reference resolver over-promises relative to schema graph model**
   - Advanced resolver features exist, but traversal model (`properties`/`items`) is too narrow for full JSON Pointer semantics.

---

## 🛠️ SAFE IMPLEMENTATION PLAN

### Phase 1 — Stabilization (No breaking changes)

1. **Publish a compatibility truth table** in docs:
   - Mark every keyword as `Implemented / Parsed-only / Unsupported`.
   - Clearly mark `oneOf`, `format`, `multipleOf`, etc. as unsupported.
2. **Fix docs/runtime mismatches immediately**:
   - Skript errors documented as string list (until typed contract exists).
   - Config page updated to top-level keys and actual defaults.
   - API reference updated to current method signatures.
3. **Add warning logs for ignored keywords** in schema loader:
   - If schema contains known unsupported documented keys, log explicit warnings.
4. **Add one verified end-to-end example** matching real runtime behavior.

**Risk:** LOW. Purely additive/documentation + non-breaking warnings.

### Phase 2 — Contract Alignment

1. **Define canonical contract document (`CONTRACT.md`)** covering:
   - Supported schema keywords and precise semantics.
   - Validation error model (core API vs Skript API).
   - File resolution rules (auto-load vs validate effect paths).
   - `$ref` support scope and explicit non-goals.
2. **Decide authority direction per area**:
   - For mature behavior (current implemented subset), docs must follow code.
   - For strategic features (`oneOf`, typed Skript errors), code should evolve to docs via feature flags.
3. **Introduce semantic versioning for contract changes**:
   - e.g., Contract v1.0 (current subset), v1.1 (safe additions), v2.0 (if any breaking changes).

**Risk:** LOW-MEDIUM. Governance/documentation work; no runtime break.

### Phase 3 — Feature Implementation (Incremental)

1. **Fix root validator dispatch (highest priority)**
   - Strategy: `ValidationService.validate` should call `ValidatorDispatcher.forSchema(schema)` at root.
   - Dependency: none.
   - Risk: MEDIUM (may expose previously hidden schema issues).

2. **Implement array/object cardinality keywords** (`minItems/maxItems/uniqueItems`, `minProperties/maxProperties`)
   - Strategy: extend `Schema` + loader parse + validators.
   - Dependency: root dispatch fix + regression tests.
   - Risk: MEDIUM.

3. **Implement numeric `multipleOf`**
   - Strategy: decimal-safe modulo check (BigDecimal preferred).
   - Dependency: numeric utility abstraction.
   - Risk: MEDIUM.

4. **Implement `oneOf` and `not` composition**
   - Strategy: create shared `CompositeConstraintEvaluator` used independent of schema type.
   - Dependency: decouple composition from object validator.
   - Risk: HIGH (error semantics, performance, recursion).

5. **Implement `format` as opt-in validators**
   - Strategy: pluggable `FormatValidatorRegistry` (`email`, `date-time`, etc.).
   - Dependency: keyword-rule modularization.
   - Risk: MEDIUM-HIGH (spec nuance).

6. **Complete `$ref`/`definitions` support scope**
   - Strategy: preserve parsed raw schema tree or richer pointer-addressable model; align resolver to JSON Pointer rules.
   - Dependency: schema parser/model refactor.
   - Risk: HIGH.

7. **Skript structured error API (non-breaking)**
   - Strategy: keep existing string expression, add new expression returning structured wrapper/accessors.
   - Dependency: Skript integration design review.
   - Risk: MEDIUM.

### Phase 4 — Architecture Refactor

1. **Modular validator engine**
   - Introduce keyword-rule pipeline (`Rule` interface + registry per schema type/global).
2. **Separate parsing from semantic compilation**
   - Parse raw schema AST first, compile to runtime schema model with capability checks.
3. **Isolate composition/reference into independent layers**
   - Composition should work for any schema type.
4. **Create Skript adapter boundary**
   - `ValidationFacade` produces canonical result DTO; Skript layer translates output types.
5. **Introduce validation context object**
   - Path stack, recursion guard, reference resolution state, and diagnostics in one context.

**Risk:** MEDIUM-HIGH. Must be staged behind compatibility tests and feature toggles.

### Phase 5 — Validation & Safety

1. **Regression suite by keyword**
   - Matrix: supported keyword × data type × pass/fail fixtures.
2. **Contract tests: docs examples must execute**
   - Every docs sample gets machine-validated expected result.
3. **Feature flags for new validators**
   - e.g., `validation.experimental.oneOf=true` before default-on.
4. **Backward compatibility strategy**
   - Keep legacy Skript string errors until next major version.
5. **Performance + recursion safety tests**
   - Deep nesting, circular refs, many-anyOf branches.
6. **Operational safety checks**
   - Deterministic logs for unsupported keywords and ref resolution failures.

---

## 🧱 PRIORITY ROADMAP

1. **Stabilize contract communication now** (docs truth table, config/API/Skript corrections, unsupported-keyword warnings).
2. **Repair correctness-critical runtime behavior** (root dispatch, ref scope clarification, path semantics).
3. **Add low-risk keywords first** (min/max items/properties, uniqueItems, multipleOf).
4. **Implement high-complexity composition/ref features incrementally** (`oneOf`, `not`, robust JSON Pointer).
5. **Refactor architecture once behavior is protected by tests and flags.**

---

## 🧠 FINAL VERDICT

- **Is the system scalable?** Yes, but only after contract hardening and validator modularization.
- **Can it safely evolve?** Yes, if changes follow staged rollout with compatibility matrix, feature flags, and regression tests.
- **Biggest technical risk:** **Contract drift causing silent under-validation**—users trusting documented constraints that are not enforced.
