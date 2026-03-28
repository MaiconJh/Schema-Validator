# Research: JSON Schema Validation Libraries

This document presents a comprehensive research on JSON Schema validation libraries available in different programming languages. The goal is to provide detailed technical information to help select appropriate tools for data validation projects.

## Summary

1. [Java Libraries](#java-libraries)
2. [JavaScript/TypeScript Libraries](#javascripttypescript-libraries)
3. [Python Libraries](#python-libraries)
4. [Go Libraries](#go-libraries)
5. [C#/.NET Libraries](#cnet-libraries)
6. [Technical Comparison](#technical-comparison)
7. [Common Use Cases](#common-use-cases)

---

## Java Libraries

### 1. everit-org/json-schema (org.everit.json.schema)

**Description:** Mature and complete library for JSON Schema validation in Java, used in various enterprise projects.

**Repository:** https://github.com/everit-org/json-schema

**Supported Specifications:**
- Draft-07 ✓
- 2019-09 ✓
- 2020-12 ✓

**Supported Format Validators:**
- date-time, date, time, email, hostname, ipv4, ipv6, uri, uri-reference
- regex, uuid, duration, json-pointer, relative-json-pointer
- Custom formats through `FormatValidator`

**License:** Apache License 2.0

**Performance Characteristics:**
- Robust validation with detailed error messages
- Support for `$ref` (external references)
- Complex schema handling with lazy validation
- Optional BOM (Byte Order Mark) for UTF-8 input

### 2. networknt/json-schema-validator

**Description:** Lightweight and high-performance library, part of the Light-4j ecosystem, focused on speed and low memory consumption.

**Repository:** https://github.com/networknt/json-schema-validator

**Supported Specifications:**
- Draft-07 ✓
- 2019-09 ✓
- 2020-12 ✓

**Supported Format Validators:**
- date-time, date, time, email, hostname, ipv4, ipv6, uri
- uuid, regex, duration
- Custom formats via `FormatFinder`

**License:** Apache License 2.0

**Performance Characteristics:**
- Designed for high performance
- Support for compiled schema caching
- Incremental validation
- Low memory overhead

---

## JavaScript/TypeScript Libraries

### 1. AJV (Another JSON Schema Validator)

**Description:** The most popular JSON Schema validator in the JavaScript/TypeScript ecosystem. Uses schema compilation for extremely fast validation.

**Repository:** https://github.com/ajv-validator/ajv

**Supported Specifications:**
- Draft-07 ✓
- 2019-09 ✓
- 2020-12 ✓
- Draft-04, 06 (via options)

**Supported Format Validators:**
- date-time, date, time, email, hostname, ipv4, ipv6, uri, uri-reference
- uuid, regex, duration, json-pointer, relative-json-pointer

**License:** MIT License

**Performance Characteristics:**
- Extremely fast validation (ahead-of-time compilation)
- Custom plugin support
- Async validation for I/O operations
- Circular refs support (with configuration)

### 2. jsonschema (JavaScript)

**Description:** Pure JavaScript implementation of JSON Schema validator, without external dependencies.

**Repository:** https://github.com/tdegrunt/jsonschema

**Supported Specifications:**
- Draft-04 ✓
- Draft-06 ✓
- Draft-07 ✓

---

## Python Libraries

### 1. jsonschema (Python)

**Description:** Reference implementation of JSON Schema validator in Python, maintained by the JSON Schema community.

**Repository:** https://github.com/python-jsonschema/jsonschema

**Supported Specifications:**
- Draft-07 ✓
- 2019-09 ✓
- 2020-12 ✓
- Draft-04, 06 ✓

**License:** MIT License

### 2. Pydantic

**Description:** Data validation library that uses Python type annotations. Compatible with JSON Schema but focused on native Python.

**Repository:** https://github.com/pydantic/pydantic

**Supported Specifications:**
- Generates JSON Schema
- Supports Python type-based validation

**License:** MIT License

---

## Technical Comparison

| Feature | Schema-Validator | everit-org | networknt | AJV |
|---------|-----------------|------------|-----------|-----|
| Drafts supported | Partial | All | All | All |
| minItems/maxItems | ✅ | ✅ | ✅ | ✅ |
| uniqueItems | ✅ | ✅ | ✅ | ✅ |
| $ref external | Limited | ✅ | ✅ | ✅ |
| Custom formats | ✅ | ✅ | ✅ | ✅ |
| Performance | Good | Good | High | Very High |

---

*Document preserved for reference purposes - Schema-Validator v1.0.5 implements all major JSON Schema features*
*Last updated: 2026-03-28*