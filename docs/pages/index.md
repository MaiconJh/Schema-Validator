---
title: Overview
description: Learn the documentation model, navigation system, and recommended reading paths for Schema Validator.
doc_type: explanation
order: 1
sequence: 1
permalink: /index.html
---

## Documentation model

This site uses Diataxis so each page has one clear purpose:

- Tutorials: learn by doing.
- How-to guides: complete a specific operational task.
- Reference: consult exact behavior and interfaces.
- Explanation: understand architecture and design choices.

## Recommended reading paths

### First-time user

1. [Getting started](getting-started.html)
2. [Installation](installation.html)
3. [Quickstart](quickstart.html)
4. [First validation workflow](first-validation.html)
5. [Configuration](configuration.html)

### Production rollout

1. [Installation](installation.html)
2. [Schema directory workflow](schema-directory-workflow.html)
3. [Configuration](configuration.html)
4. [Commands](commands.html)
5. [Config reference](config-reference.html)
6. [Validation behavior](validation-behavior.html)

### Plugin integrator / Java API

1. [Installation](installation.html)
2. [Java API](java-api.html)
3. [Architecture](architecture.html)
4. [Validation behavior](validation-behavior.html)

### Server administrator / Commands

1. [Installation](installation.html)
2. [Commands](commands.html)
3. [Configuration](configuration.html)
4. [Schema directory workflow](schema-directory-workflow.html)
5. [Design constraints](design-constraints.html)

### Schema author / validator deep dive

1. [Schema keywords](schema-keywords.html)
2. [Format reference](format-reference.html)
3. [Examples and schema construction](examples-and-schema-construction.html)
4. [Examples](examples.html)

### Internal architecture review

1. [Architecture](architecture.html)
2. [Design constraints](design-constraints.html)
3. [Validation behavior](validation-behavior.html)

## Section map

### Tutorials

- [Getting started](getting-started.html)
- [Quickstart](quickstart.html)
- [First validation workflow](first-validation.html)
- [Examples](examples.html)

### How-to guides

- [Installation](installation.html)
- [Validate JSON file](validate-json-file.html)
- [Schema directory workflow](schema-directory-workflow.html)
- [Configuration](configuration.html)

### Reference

- [Schema keywords](schema-keywords.html)
- [Validation behavior](validation-behavior.html)
- [Skript API](skript-api.html)
- [Java API](java-api.html)
- [Commands](commands.html)
- [Format reference](format-reference.html)
- [Config reference](config-reference.html)
- [Examples and schema construction](examples-and-schema-construction.html)

### Explanation

- [Overview](index.html)
- [Architecture](architecture.html)
- [Design constraints](design-constraints.html)

## Documentation governance

- Structure and implementation details: [Pages architecture](pages-architecture.html)
- Writing standards: [Writing guide](writing-guide.html)
- Contributor workflow: [Developer guide](dev-guide.html)

---

## JSON Schema Versions and Drafts

Schema-Validator supports multiple JSON Schema drafts. Understanding the differences helps you write better schemas.

### Supported Drafts

| Draft | Status | Key Features |
|---|---|---|
| Draft-04 | Legacy | `exclusiveMinimum`/`exclusiveMaximum` as booleans, `dependencies` keyword |
| Draft-06 | Legacy | `const`, `contains`, `propertyNames` |
| Draft-07 | Legacy | `if`/`then`/`else`, `readOnly`/`writeOnly` |
| 2019-09 | Current | `dependentRequired`/`dependentSchemas`, `unevaluatedProperties`/`unevaluatedItems`, `$defs` |
| 2020-12 | Current | `prefixItems` (replaces `items` for tuples), `prefixItems` + `items` for tuple validation |

### Draft-Specific Examples

#### Draft-04 (Legacy)

```json
{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "type": "object",
  "properties": {
    "age": {
      "type": "integer",
      "minimum": 0,
      "maximum": 100,
      "exclusiveMinimum": true,
      "exclusiveMaximum": true
    }
  }
}
```

**Note**: In Draft-04, `exclusiveMinimum` and `exclusiveMaximum` are booleans that modify `minimum` and `maximum`.

#### Draft-06

```json
{
  "$schema": "http://json-schema.org/draft-06/schema#",
  "type": "object",
  "properties": {
    "status": {
      "const": "active"
    },
    "tags": {
      "type": "array",
      "contains": {"type": "string"}
    }
  }
}
```

**Note**: Draft-06 introduced `const` and `contains` keywords.

#### Draft-07

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "properties": {
    "type": {"enum": ["user", "admin"]},
    "permissions": {"type": "array"}
  },
  "if": {"properties": {"type": {"const": "admin"}}},
  "then": {"required": ["permissions"]},
  "else": {"properties": {"permissions": false}}
}
```

**Note**: Draft-07 introduced `if`/`then`/`else` for conditional validation.

#### 2019-09

```json
{
  "$schema": "https://json-schema.org/draft/2019-09/schema",
  "type": "object",
  "properties": {
    "name": {"type": "string"},
    "email": {"type": "string"}
  },
  "dependentRequired": {
    "email": ["name"]
  },
  "$defs": {
    "address": {
      "type": "object",
      "properties": {
        "street": {"type": "string"}
      }
    }
  }
}
```

**Note**: 2019-09 introduced `dependentRequired`, `dependentSchemas`, `unevaluatedProperties`, and `$defs`.

#### 2020-12 (Recommended)

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "type": "array",
  "prefixItems": [
    {"type": "string"},
    {"type": "integer"}
  ],
  "items": false
}
```

**Note**: 2020-12 uses `prefixItems` for tuple validation (replaces Draft-07's `items` array form).

### Migration Guides

- **Draft-04 to 2020-12**: Update `exclusiveMinimum`/`exclusiveMaximum` from boolean to numeric form
- **Draft-07 to 2020-12**: Replace `items` array with `prefixItems`
- **All drafts**: Use `$defs` instead of `definitions` for new schemas

### External Resources

- [JSON Schema Official Documentation](https://json-schema.org/)
- [JSON Schema Specification (2020-12)](https://json-schema.org/draft/2020-12/schema)
- [JSON Schema Specification (2019-09)](https://json-schema.org/draft/2019-09/schema)
- [Understanding JSON Schema](https://json-schema.org/understanding-json-schema/)
- [JSON Schema Examples](https://json-schema.org/learn/getting-started-step-by-step)

### Related Pages

- [Schema keywords](schema-keywords.html) - Complete keyword reference
- [Examples and schema construction](examples-and-schema-construction.html) - Practical schema patterns
- [Format reference](format-reference.html) - Supported string formats
- [Validation behavior](validation-behavior.html) - Runtime execution order
