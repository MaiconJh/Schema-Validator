# JSON Schema Support Audit — Schema-Validator

**Audit date:** 2026-03-22 (UTC)  
**Last updated:** 2026-03-22 (implementations completed)  
**Project audited:** `Schema-Validator`  
**Specification comparison base:** Official JSON Schema at https://json-schema.org/ (mainly Draft-07, 2019-09 and 2020-12 for the requested items).

## Methodology

This audit was done by direct source code inspection of the plugin (parser, model, and validators), checking:

- which keywords are only recognized in parsing;
- which keywords are really **enforced** at runtime;
- which behaviors diverge from the official specification.

## Status: Implementations Completed

All functionalities listed below have been implemented:

- ✅ `$ref` resolution with full JSON Pointer support
- ✅ Array constraints (`minItems`, `maxItems`, `uniqueItems`, `prefixItems`, `items`)
- ✅ Object constraints (`minProperties`, `maxProperties`, `dependencies`, `dependentRequired`, `dependentSchemas`, `additionalProperties` as schema)
- ✅ Support for `exclusiveMinimum`/`exclusiveMaximum` in modern numeric form
- ✅ Metadata modeling (`$schema`, `$id`, `title`, `description`)
- ✅ Type array support with runtime dispatch
- ✅ 2019-09/2020-12 keyword support (`$defs`, `prefixItems`, `dependentRequired`, `dependentSchemas`)

---

## Detailed Matrix by Requirement

## 1) Supported schema types (`string`, `number`, `integer`, `boolean`, `array`, `object`, `null`)

**Status:** ✅ **Complete** (for type validation)

### Evidence
- Parsing of `type` explicitly covers: `object`, `array`, `string`, `integer`, `number`, `boolean`, `null` (and fallback to `any`).
- The dispatcher routes `object` to `ObjectValidator`, `array` to `ArrayValidator`, and other types to `PrimitiveValidator`.
- `PrimitiveValidator` implements concrete checks for `STRING`, `NUMBER`, `INTEGER`, `BOOLEAN`, `NULL`.

### Notes
- **`type` as array of types** is now supported with runtime dispatch by current data type (e.g.: `"type": ["string", "null"]`)

---

## 2) `format` validators (`date`, `time`, `email`, `uri`, `hostname`, `ipv4`, `ipv6`, `uuid`, etc.)

**Status:** ⚠️ **Partial**

### Effectively implemented formats
The `FormatValidator` handles the following standard formats:
- `date-time`, `date`, `time`, `duration`
- `email`, `idn-email`
- `hostname`, `idn-hostname`
- `ipv4`, `ipv6`
- `uri`, `uri-reference`, `uri-template`
- `json-pointer`, `relative-json-pointer`
- `uuid`, `regex`

There are also custom Minecraft formats (`minecraft-item`, `minecraft-block`, etc.).

### Relevant limitations
- For **unknown** format, validation returns success (`default -> true`), so there is no error for unsupported `format`.
- Regex implementations may diverge from RFC/ECMA corner cases expected by the specification.
- `idn-email` and `idn-hostname` reuse the same regex as `email`/`hostname` (without dedicated internationalized handling).

---

## 3) Definitions and references (`definitions`, `$defs`, `$ref` with `#/definitions/...` and `#/$defs/...`)

**Status:** ✅ **Complete**

### Implemented
- Loader extracts `definitions` and `$defs` in a first pass.
- Parser captures `$ref` in the schema.
- `SchemaRefResolver` for local, external file/URL references and with pointer.
- Complete JSON Pointer navigation (`navigateTo`) by:
  - Keywords (`properties`, `items`, `additionalProperties`)
  - Object keys (`properties/name`)
  - Array indices (`prefixItems/0`, `allOf/1`)
- Support for `definitions` and `$defs` with proper resolution
- Support for escaping `~0` (represents `~`) and `~1` (represents `/`)
- `$id`-based indexing for external reference resolution

---

## 4) Composition operators (`allOf`, `anyOf`, `oneOf`, `not`)

**Status:** ✅ **Complete**

### Evidence
- Explicit parsing of `allOf`, `anyOf`, `oneOf`, `not`.
- `ObjectValidator` applies:
  - `allOf`: requires all valid;
  - `anyOf`: requires at least one valid;
  - `oneOf`: requires exactly one valid;
  - `not`: requires internal schema to **not** validate.

### Notes
- There are specific classes (`OneOfValidator`, `NotValidator`), but the main path already covers these operators in `ObjectValidator`.

---

## 5) String validators (`pattern`, `minLength`, `maxLength`, `format`)

**Status:** ✅ **Complete**

### Evidence
- Parsing: `minLength`, `maxLength`, `pattern`, `format`.
- Enforcement in `PrimitiveValidator`:
  - minimum/maximum length;
  - `pattern` with compiled regex;
  - `format` via `FormatValidator`.

### Limitations
- `pattern` uses `matcher.matches()` (total match); depending on user interpretation, this may be surprising (many expect partial search).
- Regex compilation failure at load time generates warning and ignores invalid `pattern`.

---

## 6) Numeric validators (`minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`)

**Status:** ✅ **Complete**

### Implemented
- `minimum`, `maximum`, `multipleOf` are parsed and validated.
- `exclusiveMinimum`/`exclusiveMaximum` in **numeric** form (2019-09/2020-12)
- Compatibility with legacy **boolean** form (Draft-04/06/07)
- Support for both formats for compatibility with old and new schemas

---

## 7) Array validators (`minItems`, `maxItems`, `uniqueItems`, `items`, `prefixItems`)

**Status:** ✅ **Complete**

### Implemented
- `items` (single object) is parsed and applied to each array element.
- `minItems` — Array minimum length validation
- `maxItems` — Array maximum length validation
- `uniqueItems` — Element uniqueness check
- `prefixItems` — Tuple validation (2019-09/2020-12)
- `additionalItems` — Limited support

---

## 8) Object validators (`properties`, `required`, `minProperties`, `maxProperties`, `additionalProperties`, `patternProperties`, `dependencies`, `dependentRequired`, `dependentSchemas`)

**Status:** ✅ **Complete**

### Implemented
- `properties`: validates declared properties when present.
- `required`: requires mandatory fields.
- `additionalProperties`: supports boolean form (allow/block extras).
- `patternProperties`: applies schema by regex on key name.
- `minProperties` — Minimum property count validation
- `maxProperties` — Maximum property count validation
- `dependencies` — Support for property and schema modes
- `dependentRequired` — Required properties when dependent is present (2019-09+)
- `dependentSchemas` — Schema constraints when dependent is present (2019-09+)
- `additionalProperties` as **schema** (not just boolean)

---

## 9) Conditionals (`if`, `then`, `else`)

**Status:** ✅ **Complete**

### Evidence
- Parsing of `if`, `then`, `else`.
- `ObjectValidator` executes conditional logic:
  - if `if` passes, validates `then` (if exists);
  - if `if` fails, validates `else` (if exists).

### Notes
- There is also `ConditionalValidator`, but the main flow already performs conditional validation in the object validator.

---

## 10) Metadata (`$schema`, `$id`, `description`, `title`)

**Status:** ✅ **Supported**

### Implemented
- `$schema` — Schema dialect identification
- `$id` — Base URI for reference resolution
- `title` — Schema title
- `description` — Schema description
- `default` — Default value
- `examples` — Value examples
- `readOnly` / `writeOnly` — Property restrictions
- `deprecated` — Deprecation status
- `comment` — Annotations

### Usage in validation
- `$id` is used for indexing and external reference resolution
- `$schema` allows identifying the JSON Schema dialect in use

---

## Consolidated Comparison (status)

| Requirement | Status | Short Note |
|---|---|---|
| 1) Base types (`string`, `number`, `integer`, `boolean`, `array`, `object`, `null`) | ✅ Complete | Covered in parsing + type validation |
| 2) `format` (`date`, `time`, `email`, `uri`, `hostname`, `ipv4`, `ipv6`, `uuid`, etc.) | ⚠️ Partial | Wide catalog, but with simplifications and "unknown format = pass" |
| 3) `definitions`, `$defs`, `$ref` with `#/definitions` and `#/$defs` | ✅ Complete | Complete pointer resolution with $id support |
| 4) `allOf`, `anyOf`, `oneOf`, `not` | ✅ Complete | Implemented in object validator |
| 5) String (`pattern`, `minLength`, `maxLength`, `format`) | ✅ Complete | Implemented in `PrimitiveValidator` |
| 6) Numeric (`minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`) | ✅ Complete | Modern numeric form + legacy compatibility |
| 7) Array (`minItems`, `maxItems`, `uniqueItems`, `items`, `prefixItems`) | ✅ Complete | All constraints implemented |
| 8) Object (`properties`, `required`, `minProperties`, `maxProperties`, `additionalProperties`, `patternProperties`, `dependencies`, `dependentRequired`, `dependentSchemas`) | ✅ Complete | All constraints + additionalProperties as schema |
| 9) Conditionals (`if`, `then`, `else`) | ✅ Complete | Functional implementation in `ObjectValidator` |
| 10) Metadata (`$schema`, `$id`, `description`, `title`) | ✅ Complete | Complete modeling with usage in validation |

---

## Recommended Technical Backlog (priority)

All main functionalities listed below have been implemented:

1. ✅ **Complete JSON Pointer `$ref` resolution** (`definitions`, `$defs`, escaping and general keyword/object navigation)
2. ✅ **Array constraints** (`minItems`, `maxItems`, `uniqueItems`, `prefixItems`, `items`)
3. ✅ **Object constraints** (`minProperties`, `maxProperties`, `dependencies`, `dependentRequired`, `dependentSchemas`, `additionalProperties` as schema)
4. ✅ **`exclusiveMinimum`/`exclusiveMaximum` aligned to modern draft** (numeric value)
5. ✅ **Metadata modeling** (`$schema`, `$id`, `title`, `description`) for dialect/resolution/documentation
6. ✅ **`type` array support** and modern keywords (2019-09/2020-12)

---

## Final Note

The project now has complete compliance with main JSON Schema functionalities, including:

- Complete `$ref` resolution with JSON Pointer
- All array and object constraints implemented
- Metadata support for reference resolution
- Compatibility with modern drafts (2019-09/2020-12)

Areas for future improvement may include:

- More validation formats
- Improvements in regex validation
- Performance for very large schemas
