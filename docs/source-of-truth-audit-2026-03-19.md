# Source of Truth Documentation Audit (2026-03-19)

## Scope and method

This audit evaluates whether project documentation behaves as an authoritative contract for implementation reality.

Sources reviewed:
- Repository docs (`/docs/**`)
- Top-level `README.md`
- `RELEASE_NOTES.md`
- Public GitHub Wiki (`/wiki`), page inventory and `README` page (checked on 2026-03-19 UTC)
- Runtime implementation in `src/main/java/**` for behavior verification

---

## 🧾 DOCUMENT ARCHITECTURE MAP

### Documentation surfaces

1. **Top-level product docs**
   - `README.md`
   - `RELEASE_NOTES.md`

2. **Developer docs site (`/docs`)**
   - Index and navigation: `docs/README.md`
   - Core references: `docs/api-reference.md`, `docs/architecture.md`, `docs/reference/*`
   - User flows: `docs/quickstart.md`, `docs/installation.md`, `docs/configuration.md`, `docs/faq.md`
   - Tutorials: `docs/tutorials/*`
   - Guides: `docs/guides/integration.md`
   - Additional strategic/legacy docs: `docs/Responsibilities.md`, `docs/practical-documentation-audit.md`, `docs/deep-system-audit-2026-03-19.md`

3. **GitHub Wiki**
   - Present; currently appears to have a single public page (`README`) and no multi-page taxonomy.

### Overlap map

- **README vs docs/README vs Wiki README**: all describe setup, syntax, and schema model.
- **API surface duplicated**: `docs/api-reference.md`, `docs/README.md` API section, `docs/guides/integration.md` code snippets.
- **Behavior reference duplicated**: `docs/reference/json-schema.md`, `docs/reference/data-types.md`, `docs/construction.md`.
- **Skript usage duplicated**: top-level `README.md`, `docs/quickstart.md`, `docs/reference/skript-syntax.md`, Wiki README.

### Structural findings

- There is **no enforced canonical layer**. Navigation in `docs/README.md` does not designate which pages are normative vs tutorial.
- Legacy/conceptual docs (e.g., `docs/Responsibilities.md`) remain co-located with runtime docs and appear equally authoritative.
- Wiki duplicates onboarding and syntax but uses different conventions/paths, creating a competing knowledge channel.

---

## ❌ INCONSISTENCIES FOUND

| # | Location A | Location B | Conflict description | Severity |
|---|---|---|---|---|
| 1 | `docs/guides/integration.md` uses `new FileSchemaLoader()` + `loadSchema(...)`/`loadSchemasFromDirectory(...)` | Actual class API requires `new FileSchemaLoader(Logger)` and method `load(Path,String)` | Documented Java integration code does not compile against implementation API. | **CRITICAL** |
| 2 | `docs/configuration.md` defines nested `settings.cache-expiry`, `schemas-folder`, `examples-folder` | Runtime config keys are flat: `schema-directory`, `auto-load`, `cache-enabled`, `validation-on-load` | Configuration contract is incompatible; following docs produces ineffective or ignored settings. | **CRITICAL** |
| 3 | `docs/guides/integration.md` references Skript expression `last schema validation result` | Registered syntax exposes only `last schema validation errors` | Docs describe non-existent expression/boolean result, causing integration dead ends. | **CRITICAL** |
| 4 | `docs/Responsibilities.md` lists expression `last validation errors` | Registered expression is `last schema validation errors` | Naming mismatch across docs for same feature. | **MEDIUM** |
| 5 | Wiki README uses plugin folder `plugins/SchemaValidator/...` and schema usage `using schema "myfile"` | Repo docs/code use `Schema-Validator` folder conventions and pass schema file path in effect examples | Wiki onboarding path + schema naming model diverges from repository docs and typical runtime usage. | **MEDIUM** |
| 6 | `docs/reference/json-schema.md` states format errors are warnings / informational | Validator returns hard validation errors for failed format checks | Contract mismatch for failure semantics. | **CRITICAL** |
| 7 | `docs/construction.md` and `docs/reference/json-schema.md` document `minItems`/`maxItems`/`uniqueItems` | `ArrayValidator` only validates item type and ignores these keywords | Documented constraints are not implemented; false safety for array contracts. | **CRITICAL** |
| 8 | `README.md` claims `$ref` support as feature | Skript runtime path constructs `ValidationService()` without resolver and therefore does not resolve `$ref` during normal validate effect | Feature claim overstates practical behavior in primary user flow. | **CRITICAL** |
| 9 | Multiple docs describe full type coverage including primitives at root | `ValidationService` default root validator is `ObjectValidator`, so non-object root schema validation fails in default path | Architectural behavior diverges from broad docs promise. | **CRITICAL** |
|10| `docs/reference/data-types.md` says Number is "with or without decimals" then says Number excludes integers | Same page internally contradicts number semantics | Internal inconsistency in type contract. | **LOW** |

---

## 📜 CONTRACT VALIDITY TABLE

| Feature / contract | Status | Why |
|---|---|---|
| Skript effect syntax (`validate yaml/json ... using schema ...`) | **VALID CONTRACT** | Syntax names align with actual registration and effect implementation. |
| Retrieval of last validation errors | **VALID CONTRACT** | Expression exists and is wired to bridge state. |
| Object property validation (`properties`, `required`, `additionalProperties`, `patternProperties`) | **VALID CONTRACT** | Implemented directly in `ObjectValidator`. |
| Numeric/string primitive constraints (`minimum`, `maximum`, exclusive bounds, `multipleOf`, `minLength`, `maxLength`, `pattern`, `format`) | **PARTIAL CONTRACT** | Implemented, but some docs misstate severity/semantics (e.g., format behavior). |
| Enum constraints | **VALID CONTRACT** | Implemented in `PrimitiveValidator`. |
| Composition (`allOf`, `anyOf`) | **PARTIAL CONTRACT** | Implemented in object validator path; behavior depends on validation entrypoint assumptions. |
| `$ref` support | **PARTIAL CONTRACT** | Resolver exists, but default Skript validation path does not inject resolver. |
| Array constraints (`minItems`, `maxItems`, `uniqueItems`) | **INVALID CONTRACT** | Documented in reference/construction docs; not implemented in validator or parser behavior for enforcement. |
| Java integration API snippets | **INVALID CONTRACT** | Signatures/method names in docs are stale and non-compilable. |
| Configuration schema | **INVALID CONTRACT** | Documented keys differ from runtime keys. |

---

## ⚠️ SOURCE OF TRUTH VERDICT

### Is there a canonical definition of behavior?

**No.** There are multiple competing “truth” sources:
- Top-level README
- `/docs` reference pages
- Wiki README
- Legacy conceptual docs (`Responsibilities`)

### Do sources compete?

**Yes.** Configuration, API signatures, and Skript capabilities differ between documents. Implementation behavior must be read from code to resolve conflicts.

### Outdated/future-presented-as-current documentation

- Integration guide documents an API surface that appears from an earlier design and is no longer current.
- Array keyword docs present standards-level support that runtime does not currently enforce.
- `$ref` feature is presented as broadly available without flow-level caveats.

**Conclusion:** Documentation corpus is **not authoritative** today.

---

## 🧱 ARCHITECTURAL MISALIGNMENTS

1. **Validation entrypoint model not reflected**
   - Docs imply schema-type-driven dispatch from root.
   - Runtime service defaults to object-only root validator, creating hidden constraint on root schema type.

2. **Reference resolution architecture under-documented for runtime path**
   - Resolver component exists and is described as a feature.
   - Main Skript effect path does not configure resolver, so docs omit critical operational dependency.

3. **Configuration architecture mismatch**
   - Runtime config supports auto-load / validation-on-load toggles, but user docs present a different, nested config model.

4. **No architecture-level “implemented vs planned” boundary**
   - Reference pages mix JSON Schema vocabulary with implemented subset, obscuring enforceable behavior.

---

## 🧪 REAL USAGE FAILURES

If a developer uses docs only:

1. **Java integrator failure at compile time**
   - Following `docs/guides/integration.md` yields missing constructors/methods.

2. **Server admin misconfiguration**
   - Editing `settings.cache-expiry` in `config.yml` has no effect because runtime reads different keys.

3. **False confidence in array safety**
   - Admin expects `minItems`/`uniqueItems` enforcement; invalid arrays may pass silently.

4. **Skript script logic dead end**
   - Script using `last schema validation result is successful` fails because expression is undocumented-but-missing runtime feature.

5. **Unexpected `$ref` failures in normal validation flow**
   - Feature advertised, but resolver not wired by default path can produce unresolved reference behavior.

---

## 🚨 SYSTEMIC RISKS

| Risk | Impact | Severity |
|---|---|---|
| Misconfigured production behavior due to wrong config contract | Operators believe controls exist while runtime ignores them | **CRITICAL** |
| Under-validation of arrays despite documented constraints | Invalid or unsafe data accepted silently | **CRITICAL** |
| API drift in docs breaks integrations | Integration teams lose trust and incur rework | **MEDIUM** |
| Competing docs (Wiki vs repo docs) fragment onboarding | Support burden and inconsistent user setups | **MEDIUM** |
| Naming inconsistencies in syntax docs | Low-level friction and script mistakes | **LOW** |

---

## 🛠️ RESTRUCTURING PLAN

### 1) Define single Source of Truth

Create a canonical directory with strict roles:
- `docs/reference/` = **normative, testable contract only**
- `docs/guides/` and `docs/tutorials/` = non-normative examples
- `README.md` = onboarding and links, minimal duplicated behavior text
- Wiki = either deprecated (link to repo docs only) or auto-mirrored from a generated subset

### 2) Introduce contract taxonomy in every reference page

Each keyword/feature block must include:
- **Status**: Implemented / Partial / Planned
- **Validator path**: class+method
- **Failure semantics**: error/warning/ignored
- **Minimal executable example**

### 3) Remove or quarantine stale pages

- Move `docs/Responsibilities.md` to `docs/archive/` (or delete) unless updated and marked non-normative.
- Replace stale integration snippets with compile-verified snippets.

### 4) Enforce docs against code in CI

- Add a docs-contract test suite that validates documented keywords against parser + validators.
- Add snippet compilation tests for Java API examples.
- Add lint rule for forbidden undocumented Skript syntax tokens.

### 5) Governance

- Add `docs/DECISIONS.md` with canonical-source policy.
- Require “doc impact” checklist in PR template.
- Stamp each reference page with “Last validated against commit <sha>”.

### 6) Immediate repairs (priority order)

1. Fix `docs/configuration.md` to exact runtime keys.
2. Fix `docs/guides/integration.md` to current API and supported expressions.
3. Mark unsupported JSON Schema keywords (`minItems`, `maxItems`, `uniqueItems`) as not implemented.
4. Clarify `$ref` runtime limitations in Skript path.
5. De-duplicate syntax docs and unify naming.

---

## 🧠 FINAL VERDICT

- **Is documentation trustworthy today?** **No (not as a source of truth).**
- **Biggest structural flaw:** No single canonical contract layer; reference, guide, wiki, and legacy pages all act as peers and conflict.
- **Fix first:** Align and lock the **configuration + API + validation-keyword contract pages** to implementation reality, then gate with CI contract tests.

