# Deep System Audit — Verified Implementation State (revalidated on 2026-03-21)

> **Goal of this revision:** fully revalidate the 2026-03-19 report against the current codebase, removing assumptions and correcting documentation drift.
>
> **Truth criterion used:** only observable behavior in `src/main/java` and active contracts in `src/main/resources`.

---

## 1) Cross-check methodology

### 1.1 Executed process
1. Full reading of the previous document (`deep-system-audit-2026-03-19.md`).
2. Extraction of each claim, hypothesis, section, and conclusion.
3. Direct mapping of every item to concrete code evidence (class, method, execution flow).
4. Status classification:
   - **Valid**: implemented and consistent with current execution.
   - **Partial**: implementation exists but is incomplete or constrained.
   - **Invalid**: contradicts current implementation.
   - **Not verifiable**: could not be confirmed due to missing executable/contractual artifacts.

### 1.2 Traceable sources used
- `src/main/java/com/maiconjh/schemacr/core/ValidationService.java`
- `src/main/java/com/maiconjh/schemacr/validation/{ObjectValidator,ArrayValidator,PrimitiveValidator,FormatValidator,ValidatorDispatcher,ValidationError}.java`
- `src/main/java/com/maiconjh/schemacr/schemes/{Schema,FileSchemaLoader,SchemaRefResolver,SupportedKeywordsRegistry}.java`
- `src/main/java/com/maiconjh/schemacr/integration/{EffValidateData,ExprLastValidationErrors,SkriptValidationBridge,DataFileLoader}.java`
- `src/main/java/com/maiconjh/schemacr/config/PluginConfig.java`
- `src/main/resources/config.yml`
- `docs/CONTRACT.md` and `docs/api-reference.md` (used only to detect drift, not as primary behavior sources).

---

## 2) Verification matrix — claims from the previous document

## 2.1 Root validator dispatch

**Previous claim:** critical root dispatch bug was resolved.

**Status:** ✅ **Valid**.

**Evidence:** `ValidationService.validate(...)` uses `ValidatorDispatcher.forSchema(schema)` before validation (`ValidationService.java`, `validate`, lines 40–50), so root type is no longer hard-bound to `ObjectValidator`.

**Current real state:** root schemas of type `array` and primitives are dispatched to the appropriate validator.

**Technical change note**
- **What changed in the document:** removed active-risk narrative for root dispatch and reclassified it as corrected and live.
- **Why this was required:** the previous version still had legacy wording that could conflict with the current execution path.
- **Correction evidence:** explicit call to `ValidatorDispatcher.forSchema(schema)` in the main validation flow.
- **Real state:** dynamic root dispatch is implemented.

## 2.2 `minItems`, `maxItems`, `uniqueItems`

**Previous claim:** not implemented (with mention of incorrect fixes in another audit).

**Status:** ✅ **Valid (still not implemented)**.

**Evidence:** `ArrayValidator.validate(...)` only checks list type and validates `items` per element; there is no cardinality/uniqueness enforcement.

**Current real state:** schemas containing those keywords may parse, but constraints are not enforced at runtime.

**Technical change note**
- **What changed in the document:** kept the non-implementation conclusion and added practical impact (silent under-validation).
- **Why this was required:** to avoid false confidence about JSON Schema coverage.
- **Correction evidence:** missing rules in `ArrayValidator`.
- **Real state:** only `items` is effectively validated for arrays.

## 2.3 `minProperties`, `maxProperties`

**Previous claim:** not implemented.

**Status:** ✅ **Valid (still not implemented)**.

**Evidence:** `ObjectValidator` enforces `required`, `properties`, `patternProperties`, `additionalProperties`, composition (`allOf/anyOf/oneOf/not/if-then-else`) and `$ref`, but no minimum/maximum property count checks exist.

**Current real state:** object property cardinality is not enforced.

**Technical change note**
- **What changed in the document:** clarified the distinction between “object structural validation” and “property count limits”.
- **Why this was required:** to remove ambiguity between `required` support and count-limit support.
- **Correction evidence:** no `minProperties/maxProperties` logic in object validation.
- **Real state:** property count limits are still absent.

## 2.4 `multipleOf`

**Previous claim:** implemented.

**Status:** ✅ **Valid**.

**Evidence:** `PrimitiveValidator` performs division by the divisor and validates integral result (`multipleOf`) for `number`/`integer`.

**Current real state:** `multipleOf` is active for numeric types.

## 2.5 `format`

**Previous claim:** implemented.

**Status:** ✅ **Valid**.

**Evidence:** `PrimitiveValidator` calls `FormatValidator.isValid(...)` when `schema.hasFormat()`; failures become `ValidationError`.

**Current real state:** format validation is hard-fail (validation error, not warning).

## 2.6 `oneOf`, `not`, `if/then/else`

**Previous claim:** implemented.

**Status:** ✅ **Valid**.

**Evidence:** dedicated blocks in `ObjectValidator.validate(...)` for `oneOf`, `not`, and conditional `if/then/else`.

**Current real state:** conditional composition and logical exclusion work in object validation.

## 2.7 Skript error model

**Previous claim:** mismatch existed and was “improved”.

**Status:** ✅ **Partially valid**.

**Evidence:**
- `ValidationError` includes `getMessage()` and `toCompactString()`.
- `ExprLastValidationErrors` returns `String[]` using `toCompactString()`.

**Current real state:** Skript integration is still string-based, not structured object output in Skript expressions.

**Technical change note**
- **What changed in the document:** clarified that serialization improved, but return-type contract did not change.
- **Why this was required:** to avoid treating this as fully solved while the core limitation remains.
- **Correction evidence:** `ExprLastValidationErrors extends SimpleExpression<String>`.
- **Real state:** compact string output, no rich object exposure in Skript.

## 2.8 `$ref`, `definitions`, `$defs`

**Previous claim:** partial support.

**Status:** ✅ **Valid (with important caveats)**.

**Evidence:**
- `FileSchemaLoader` extracts `definitions` and `$defs` into an internal map.
- `Schema` does not store a `definitions/$defs` tree.
- `SchemaRefResolver.navigateTo(...)` only navigates `properties` and `items`; it does not navigate `definitions/$defs`.

**Current real state:** definition-block parsing exists, but JSON Pointer resolution for `#/definitions/...`/`#/$defs/...` is not complete end-to-end.

## 2.9 “Issue 5: Config contract mismatch” (left unverified in the old text)

**Status:** ❌ **Não segue como fonte da verdade** (*Not to be followed as source of truth*).

**Critical analysis and evidence:**
- The old document left this as “Needs verification”, without validated conclusion.
- Current code confirms objective contract drift:
  - `config.yml` includes `strict-mode` and sets `validation-on-load: false` by default.
  - `PluginConfig` reads `strict-mode` and uses internal default `validation-on-load = true` (when key is missing).
  - `docs/CONTRACT.md` lists `validation-on-load: true` and omits `strict-mode` from config contract.

**Current real state:** there is a mismatch between contractual docs and shipped configuration behavior.

## 2.10 “Issue 6: API reference signature drift”

**Status:** ⚠️ **Partial / requires time-scope context**.

**Evidence:** `docs/api-reference.md` is mostly aligned with current signatures in `FileSchemaLoader`, `SchemaRegistrationService`, and `ValidationService`.

**Current real state:** the historical drift claim does not fully hold in the current code state; API reference is significantly aligned.

**Technical change note**
- **What changed in the document:** downgraded from generalized active issue to historical observation requiring periodic review.
- **Why this was required:** previous conclusion was broad and no longer reflected actual public methods.
- **Correction evidence:** direct comparison between `docs/api-reference.md` and current source signatures.
- **Real state:** current API reference is close to implementation.

## 2.11 “Issue 7: Path resolution split-brain”

**Status:** ✅ **Valid**.

**Evidence:**
- `SchemaValidatorPlugin.autoLoadSchemas()` uses configured schema directory (`PluginConfig#getSchemaDirectory()`).
- `EffValidateData` validates using direct `Path.of(schemaFile)` and `Path.of(dataFile)` from Skript effect inputs.

**Current real state:** two path-resolution modes coexist (config-driven auto-load vs explicit runtime paths in effect execution).

## 2.12 “Issue 8: Composition logic object-validator bound”

**Status:** ✅ **Valid**.

**Evidence:** `allOf`, `anyOf`, `oneOf`, `not`, `if/then/else` blocks are implemented inside `ObjectValidator`, not in a shared cross-type composition layer.

**Current real state:** composition remains coupled to object-validator flow, even though nested schemas are type-dispatched.

## 2.13 “Issue 9: Global mutable last-result bridge”

**Status:** ✅ **Valid**.

**Evidence:** `SkriptValidationBridge` stores a single global `private static volatile ValidationResult lastResult`.

**Current real state:** there is no scope partition by player/event/context; the last result is global.

## 2.14 Supported keyword registry

**Status:** ❌ **Não segue como fonte da verdade** (*Not to be followed as source of truth*) when interpreted as full enforcement support.

**Critical analysis and evidence:**
- `SupportedKeywordsRegistry` marks `minItems`, `maxItems`, `uniqueItems`, `minProperties`, `maxProperties`, `dependencies` as supported.
- Validators (`ArrayValidator`/`ObjectValidator`) do not enforce those rules.

**Current real state:** the registry behaves more like parsing/documentation whitelist than enforcement truth.

## 2.15 Root constraint in canonical docs

**Status:** ❌ **Não segue como fonte da verdade** (*Not to be followed as source of truth*).

**Critical analysis and evidence:**
- `docs/CONTRACT.md` states `ValidationService()` effectively enforces object root by fixed `ObjectValidator` behavior.
- `ValidationService.validate(...)` dispatches by root schema type.

**Current real state:** that root-object restriction no longer reflects current runtime behavior.

---

## 3) Consolidated current state (validated facts only)

### 3.1 Features confirmed as implemented
- Root-node type dispatch (`object`, `array`, primitives).
- Object validation: `properties`, `required`, `patternProperties`, `additionalProperties`, `allOf`, `anyOf`, `oneOf`, `not`, `if/then/else`.
- Array validation: `items`.
- Primitive validation: `minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`, `minLength`, `maxLength`, `pattern`, `format`, `enum`.
- Skript error expression with text output (`String[]`) and compact formatting.

### 3.2 Missing or partial features
- Not implemented: `minItems`, `maxItems`, `uniqueItems`.
- Not implemented: `minProperties`, `maxProperties`.
- Partial: `$ref` with `definitions/$defs` (extraction exists; local pointer resolution does not traverse those nodes in current model).
- Architectural limitation: globally shared validation result in Skript bridge.

### 3.3 Critical documentation divergences
- `docs/CONTRACT.md` contains at least two high-impact divergences:
  1. claims an object-root restriction that no longer matches runtime behavior;
  2. describes Skript error output contract as `toString()` while implementation uses `toCompactString()`.
- Documented configuration contract does not fully match `config.yml`/`PluginConfig` (example: `strict-mode`).

---

## 4) Detailed technical action plan

## 4.1 Identified inconsistencies and impact
1. **Declared keyword support vs real enforcement (array/object cardinality).**
   - **Impact:** false confidence in validation; invalid data can be silently accepted.
2. **Partial `$ref` for `definitions/$defs`.**
   - **Impact:** schemas using standard internal references can fail or validate incompletely.
3. **Drift in canonical documentation (`CONTRACT.md`).**
   - **Impact:** integrators make wrong assumptions about runtime capabilities/constraints.
4. **Global `lastResult` in Skript bridge.**
   - **Impact:** possible context overlap in concurrent/event-driven scenarios.
5. **Path-resolution semantic split (auto-load vs effect path).**
   - **Impact:** inconsistent behavior between automatic loading and ad hoc validation.

## 4.2 Verification methodologies used
- Static inspection of core runtime flow (entry point → parser → dispatcher → validators).
- Semantic validation by reading methods that enforce constraints.
- Contract-vs-implementation comparison for public API and docs.
- Architectural coupling review (global state, reference resolution, integration boundaries).

## 4.3 Criteria used to validate truth
- **Executable code takes precedence** over textual documentation.
- **Implemented** means parser + model + runtime enforcement (not just keyword recognition).
- **Partial** when part of the pipeline exists but functional closure is missing.
- **Not verifiable** when there is no concrete executable evidence.

## 4.4 Concrete next steps (recommended order)
1. Update `docs/CONTRACT.md` to reflect real root dispatch behavior, Skript output type, and current config keys.
2. Implement `minItems/maxItems/uniqueItems` (`Schema` model, `FileSchemaLoader` parse, `ArrayValidator` enforcement).
3. Implement `minProperties/maxProperties` (model + parse + `ObjectValidator` enforcement).
4. Refactor `$ref` architecture to support `definitions/$defs` traversal in in-memory model.
5. Update `SupportedKeywordsRegistry` to clearly separate:
   - **recognized** keywords;
   - **enforced** keywords.
6. Evaluate contextual scoping for `SkriptValidationBridge` (per event/player/topic) to remove global shared state.
7. Unify path resolution strategy between auto-load and Skript effect validation (or document explicit dual behavior).

---

## 5) Restructuring record for this document

### 5.1 What was restructured
- Reorganized from mixed narrative to auditable sequence: **methodology → verification matrix → consolidated state → action plan**.
- Added explicit status and concrete code evidence to each section.
- Marked incorrect/obsolete/non-conclusive sections with the required formal marker.

### 5.2 Why restructuring was necessary
- Previous version mixed historical state, partial conclusions, and “to verify” placeholders, reducing reliability as an operational source.
- Hierarchical progression for technical decision-making was missing.

### 5.3 Result
- This file now represents only validated information against the current system state.
- Items without strong validation are explicitly treated as non-authoritative.
