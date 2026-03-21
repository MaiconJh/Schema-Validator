# Reference

This section provides comprehensive technical documentation for the Schema-Validator library, including API interfaces, syntax references, and behavioral specifications.

## Overview

The Reference section is organized into a learning path that takes you from basic usage to advanced features:

```
Skript Syntax → Schema Keywords → Validation Behavior → Examples → Configuration
```

### Quick Navigation

| Document | Description | Start Here? |
|----------|-------------|--------------|
| [Skript syntax](skript-syntax.md) | Syntax reference for Skript integration | ✓ |
| [Schema keywords](schema-keywords.md) | Supported JSON Schema keywords | |
| [Validation behavior](validation-behavior.md) | How validation works internally | |
| [Examples and schema construction](examples-and-schema-construction.md) | Practical examples & code samples | ✓ |
| [Config reference](config-reference.md) | Configuration options | |

## Getting Started

If you're new to Schema-Validator, start with these documents:

1. **[Examples and schema construction](examples-and-schema-construction.md)** - Learn how to build validation schemas with practical examples
2. **[Skript syntax](skript-syntax.md)** - Understand how to use validation in your Skript scripts

## Understanding the Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Schema-Validator Flow                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐    ┌───────────┐ │
│  │   Skript    │───▶│ File/JSON    │───▶│   Schema    │───▶│ Validation│ │
│  │   Script    │    │   Loader     │    │   Parser    │    │  Engine   │ │
│  └──────────────┘    └──────────────┘    └──────────────┘    └───────────┘ │
│         │                                                           │        │
│         │              ┌──────────────────────────────────────────┘        │
│         │              │                                                      │
│         ▼              ▼                                                      │
│  ┌──────────────┐    ┌──────────────┐                                         │
│  │   Error      │◀───│  Validation  │                                         │
│  │   Report     │    │   Result     │                                         │
│  └──────────────┘    └──────────────┘                                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Key Concepts

### Schema Keywords
JSON Schema keywords define *what* validation rules to apply. See [Schema keywords](schema-keywords.md) for the complete list of supported keywords.

### Validation Behavior
Understanding how validation works helps you debug issues and optimize schemas. See [Validation behavior](validation-behavior.md) for details on the validation process.

### Configuration
The plugin can be configured to match your server's needs. See [Config reference](config-reference.md) for all available options.

## Document Index

### 1. Skript Syntax
**File:** `skript-syntax.md`

- Registered effect patterns (`validate yaml/json ... using schema ...`)
- Expression patterns (`last schema validation errors`)
- Runtime behavior and data flow

### 2. Schema Keywords
**File:** `schema-keywords.md`

- Keywords enforced at validation time
- Keywords parsed but not enforced
- Unsupported keyword handling
- `$ref` support

### 3. Validation Behavior
**File:** `validation-behavior.md`

- Validator dispatch model
- Object, array, and primitive validation order
- Result and error model
- Source code mapping

### 4. Examples and Schema Construction
**File:** `examples-and-schema-construction.md`

- Schema model and types
- Supported formats (including Minecraft-specific)
- 13+ practical examples
- Java API and Skript usage

### 5. Configuration Reference
**File:** `config-reference.md`

- All configuration options
- Default values and effects

## Related Sections

- **[Tutorials](../tutorials/README.md)** - Step-by-step guides for common use cases
- **[Guides](../guides/README.md)** - In-depth articles on specific topics
- **[Explanation](../explanation/README.md)** - Architectural and design documentation

## Contributing to This Documentation

If you find issues or want to improve these documents:

1. Edit the `.md` files directly in `docs/reference/`
2. Follow the existing structure and formatting
3. Include source code references when relevant

---

[← Previous](../tutorials/first-validation.md) | [Next →](skript-syntax.md) | [Home](../../README.md)
