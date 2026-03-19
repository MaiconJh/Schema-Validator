# Supported JSON Schema Keywords

> **Last Updated:** 2026-03-19  
> **Version:** 1.0  
> **Source:** [`SupportedKeywordsRegistry.java`](src/main/java/com/maiconjh/schemacr/schemes/SupportedKeywordsRegistry.java)

This document provides the authoritative list of all JSON Schema keywords currently supported by Schema-Validator. All implementation work should align with this matrix.

---

## Overview

Schema-Validator supports a comprehensive subset of JSON Schema Draft 2020-12. The following tables document all supported keywords organized by category.

**Total Supported Keywords:** 39

---

## Type Keywords

| Keyword | Description | Status |
|---------|-------------|--------|
| `type` | Specifies the type of data (object, array, string, number, integer, boolean, null) | ✅ Supported |

---

## Object Keywords

| Keyword | Description | Status |
|---------|-------------|--------|
| `properties` | Defines properties of an object | ✅ Supported |
| `patternProperties` | Defines properties matching a regex pattern | ✅ Supported |
| `additionalProperties` | Controls additional properties | ✅ Supported |
| `required` | Array of required property names | ✅ Supported |
| `minProperties` | Minimum number of properties | ✅ Supported |
| `maxProperties` | Maximum number of properties | ✅ Supported |
| `dependencies` | Property dependencies (schema dependencies only) | ⚠️ Partial |

---

## Array Keywords

| Keyword | Description | Status |
|---------|-------------|--------|
| `items` | Schema for array elements | ✅ Supported |
| `minItems` | Minimum number of items | ✅ Supported |
| `maxItems` | Maximum number of items | ✅ Supported |
| `uniqueItems` | All items must be unique | ✅ Supported |
| `additionalItems` | Additional items constraint | ⚠️ Limited |

---

## String Keywords

| Keyword | Description | Status |
|---------|-------------|--------|
| `minLength` | Minimum string length | ✅ Supported |
| `maxLength` | Maximum string length | ✅ Supported |
| `pattern` | Regular expression pattern | ✅ Supported |
| `format` | String format (email, uri, date-time, etc.) | ✅ Supported |

### Supported Formats

| Format | Description |
|--------|-------------|
| `date-time` | ISO 8601 date-time |
| `date` | ISO 8601 date |
| `time` | ISO 8601 time |
| `email` | Email address |
| `idn-email` | Internationalized email |
| `hostname` | Domain hostname |
| `idn-hostname` | Internationalized hostname |
| `ipv4` | IPv4 address |
| `ipv6` | IPv6 address |
| `uri` | URI reference |
| `uri-reference` | URI reference |
| `uri-template` | URI template |
| `json-pointer` | JSON Pointer |
| `relative-json-pointer` | Relative JSON Pointer |
| `regex` | Regular expression |

---

## Number Keywords

| Keyword | Description | Status |
|---------|-------------|--------|
| `minimum` | Minimum value (inclusive) | ✅ Supported |
| `maximum` | Maximum value (inclusive) | ✅ Supported |
| `exclusiveMinimum` | Minimum value (exclusive) | ✅ Supported |
| `exclusiveMaximum` | Maximum value (exclusive) | ✅ Supported |
| `multipleOf` | Value must be a multiple of | ✅ Supported |

---

## Composition Keywords

| Keyword | Description | Status |
|---------|-------------|--------|
| `allOf` | Must validate against all schemas | ✅ Supported |
| `anyOf` | Must validate against at least one schema | ✅ Supported |
| `oneOf` | Must validate against exactly one schema | ✅ Supported |
| `not` | Must not validate against this schema | ✅ Supported |

---

## Conditional Keywords

| Keyword | Description | Status |
|---------|-------------|--------|
| `if` | Conditional schema | ✅ Supported |
| `then` | Schema to apply if `if` validates | ✅ Supported |
| `else` | Schema to apply if `if` does not validate | ✅ Supported |

---

## Reference Keywords

| Keyword | Description | Status |
|---------|-------------|--------|
| `$ref` | JSON Pointer reference | ✅ Supported |
| `definitions` | Schema definitions | ✅ Supported |
| `$schema` | Schema URI | ⚠️ Limited |
| `$id` | Schema ID | ⚠️ Limited |

---

## Constraint Keywords

| Keyword | Description | Status |
|---------|-------------|--------|
| `enum` | Enumeration of allowed values | ✅ Supported |
| `const` | Constant value constraint | ✅ Supported |

---

## Metadata Keywords

| Keyword | Description | Status |
|---------|-------------|--------|
| `title` | Schema title | ✅ Supported |
| `description` | Schema description | ✅ Supported |
| `default` | Default value | ✅ Supported |
| `examples` | Example values | ✅ Supported |
| `readOnly` | Read-only flag | ✅ Supported |
| `writeOnly` | Write-only flag | ✅ Supported |
| `deprecated` | Deprecation flag | ✅ Supported |
| `comment` | Comment string | ✅ Supported |

---

## Vocabulary Keywords

| Keyword | Description | Status |
|---------|-------------|--------|
| `$vocabulary` | Vocabulary declaration | ⚠️ Limited |
| `$dynamicAnchor` | Dynamic anchor | ⚠️ Limited |

---

## Status Legend

| Symbol | Meaning |
|--------|---------|
| ✅ Supported | Fully implemented and tested |
| ⚠️ Partial | Partially implemented or limited support |

---

## Unsupported Keywords

The following commonly requested keywords are **NOT YET SUPPORTED**:

- `propertyNames` - Validate property names
- `contains` - Array must contain specific item
- `const` - (if not using enum)
- `$defs` - Alternative to definitions
- `$recursiveRef` - Recursive references
- `unevaluatedProperties` - Unevaluated properties validation
- `unevaluatedItems` - Unevaluated items validation
- `dependentRequired` - Required dependencies
- `dependentSchemas` - Schema dependencies

---

## Version Compatibility

This matrix reflects support for **JSON Schema Draft 2020-12** with backward compatibility for Draft-07 and Draft-06.

---

## Contributing

To add support for a new keyword:

1. Add the keyword to [`SupportedKeywordsRegistry.java`](src/main/java/com/maiconjh/schemacr/schemes/SupportedKeywordsRegistry.java)
2. Update the parser in [`FileSchemaLoader.java`](src/main/java/com/maiconjh/schemacr/schemes/FileSchemaLoader.java)
3. Implement validation in the appropriate validator class
4. Add tests in `src/test/`
5. Update this documentation matrix

---

*Generated from SupportedKeywordsRegistry - Total: 39 keywords*
*Last Updated: 2026-03-19*