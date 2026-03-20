# Practical Documentation Audit (`/docs`)

## 🧾 PRACTICAL SUMMARY

**Can this documentation be used in real life?**

**Score: 4/10**

The docs give a strong conceptual picture, but they are not consistently executable as a real onboarding path. A new developer can understand *intent* quickly, but will hit blocking gaps during setup and first validation attempt because key operational details conflict with the codebase and repository state.

Top blockers:

1. Installation instructions assume Unix `./gradlew`, but repository only includes `gradlew.bat` and no Gradle build files.
2. Syntax and API sections describe capabilities that are either partially implemented or not implemented (`oneOf`, `format`, `minItems`, etc.).
3. Config docs describe a `settings:` section that does not match the actual config shape used by code.
4. Skript error object model is documented as typed objects, but implementation returns plain strings.

---

## 🧪 SIMULATION RESULT

### 1) Trying to understand the system from docs only

What works:
- It is clear this is a Minecraft plugin for JSON/YAML validation with Skript integration.
- The docs provide architecture diagrams and schema concepts.

Where it breaks:
- The docs switch between conceptual Java API docs and plugin/Skript usage without clearly separating “library API” from “plugin runtime API.”
- There is no single reliable “golden path” verified against current implementation.

### 2) Trying to create a schema

I can create a basic object schema with `type`, `properties`, `required`, `minimum`, `maximum`, `enum`, `patternProperties`, and `additionalProperties`.

Breakpoints:
- Docs advertise many JSON Schema keywords (`format`, `multipleOf`, `minItems`, `maxItems`, `uniqueItems`, `minProperties`, `dependencies`, `oneOf`) but validators do not actually enforce most of them.
- A developer may trust the reference and ship schemas with unsupported constraints, resulting in silent under-validation.

### 3) Trying to validate a YAML/JSON file

Expected from docs:
- `validate yaml "file" using schema "schema"` + read typed validation errors.

Actual practical issue:
- Path expectations are unclear. Effect code reads direct `Path.of(dataFile)` / `Path.of(schemaFile)` rather than resolving against plugin schema/data directories.
- Error expression returns strings, not `ValidationError` objects as docs imply.

### 4) Interpreting failures

What works:
- `ValidationError#toString()` messages are available in Skript.

Where it breaks:
- Docs say you can access fields like `loop-value.nodePath`; implementation gives `String[]`, so property access examples are misleading.

---

## ❌ REAL-WORLD FAILURES

1. **At installation step, user cannot run documented build command** if on Linux/macOS (`./gradlew build`) because wrapper/build files are missing.
2. **At config setup, user cannot map doc keys to runtime keys** (`settings.cache-enabled` vs root keys like `cache-enabled`, `auto-load`).
3. **At schema authoring, user trusts unsupported keywords** and thinks constraints are enforced when they are ignored.
4. **At Skript error handling step, user tries object properties on errors** and fails because errors are strings.
5. **At file location step, docs imply plugin folder conventions** but validation effect currently depends on directly passed filesystem paths.
6. **Tutorial examples include invalid/ambiguous keys** (`req biome`, `req permission`) that conflict with shown regex patterns and likely YAML key intentions.
7. **CLI command `/pl SchemaValidator` may not match plugin identity expectations** (`Schema-Validator` naming used elsewhere), increasing confusion when verifying installation.

---

## ⚠️ HIDDEN ASSUMPTIONS

- Assumes developers know exact runtime working directory for relative paths passed to Skript effect.
- Assumes developers know which JSON Schema subset is truly enforced vs merely documented.
- Assumes users can infer that `last schema validation errors` are strings despite object-style examples.
- Assumes build tooling exists in repository despite absent Gradle project files.
- Assumes users understand that schema auto-load and effect-time schema loading are distinct flows.

---

## 🧠 MISSING LINKS

1. **No authoritative feature matrix** mapping “documented keyword” → “implemented validator support.”
2. **No end-to-end path resolution model** for data file and schema file lookup.
3. **No clear separation of docs audiences** (plugin users with Skript vs Java integrators).
4. **No troubleshooting path for malformed assumptions** (unsupported keyword, wrong path root, string errors vs object errors).
5. **No conformance examples** showing success/failure for each supported keyword.

---

## 💡 WHAT IS NEEDED TO MAKE IT USABLE

1. Add a **single verified quickstart** tested against current build/repo state.
2. Add a **supported-keywords table** with explicit statuses:
   - ✅ Implemented
   - ⚠️ Parsed but not validated
   - ❌ Not supported
3. Fix **configuration docs** to match real `config.yml` keys and defaults.
4. Correct **Skript error type docs** (string list) or change implementation to expose typed error object.
5. Document **file path resolution rules** with absolute and relative examples.
6. Add **failure examples** for each common error category (schema not found, file not found, type mismatch, enum mismatch).
7. Remove/repair tutorial snippets with invalid keys or inconsistent schema names/extensions.
8. Add **“Docs vs Runtime contract” test checklist** before release.

---

## 🧱 MINIMUM VIABLE DOCUMENTATION

To be usable in practice, docs must include these mandatory sections:

1. **Installation that actually runs** in this repository (OS-specific commands).
2. **Runtime file layout and path resolution** (where schemas/data are read from, and how Skript arguments resolve).
3. **Validated JSON Schema subset** with examples of pass/fail behavior.
4. **Exact Skript syntax and return types** with one full command example.
5. **Error interpretation guide** showing real outputs and remediation steps.
6. **One complete end-to-end tutorial** (schema + data + script + expected success/failure output).
7. **Version compatibility table** (Minecraft, Skript, Java, plugin version).

---

## 🧠 FINAL VERDICT

- **Usable in practice?** Partially, only for experienced developers willing to inspect source code and resolve inconsistencies manually.
- **Only conceptual?** Largely conceptual today; execution details are unreliable in critical places.
- **Production-ready docs?** No. The docs need a contract-level pass to align with implementation before they can be considered production-ready.

In short: strong educational intent, but insufficient operational reliability for first-time real-world adoption.
