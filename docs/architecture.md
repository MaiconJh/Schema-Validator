# Architecture

Technical documentation of the Schema Validator validation system.

---

## Overview

Schema Validator is a JSON/YAML validation system for Minecraft plugins with Skript integration. This document explains the internal architecture and how data flows through the validation pipeline.

---

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                        VALIDATION FLOW                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────────────┐    ┌──────────────┐    ┌───────────────────┐   │
│  │ JSON/YAML    │───▶│ FileSchema   │───▶│ Schema Registry   │   │
│  │ File         │    │ Loader       │    │ (Cache)           │   │
│  └──────────────┘    └──────────────┘    └───────────────────┘   │
│                                                      │              │
│                                                      ▼              │
│  ┌──────────────┐    ┌──────────────┐    ┌───────────────────┐   │
│  │ Validation   │◀───│ Validator    │◀───│ Schema            │   │
│  │ Result       │    │ Dispatcher    │    │ (Parsed Model)    │   │
│  └──────────────┘    └──────────────┘    └───────────────────┘   │
│         │                                                          │
│         │              ┌──────────────────────┐                   │
│         └─────────────▶│ ValidationError      │                   │
│                        │ (Error Details)       │                   │
│                        └──────────────────────┘                   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Core Components

### 1. Schema Model

The Schema class represents a parsed JSON Schema:

```java
public class Schema {
    private final String name;
    private final SchemaType type;
    private final Map<String, Schema> properties;
    private final Schema itemSchema;
    private final List<String> requiredFields;
    // ... validation constraints
    
    // Composition (NEW)
    private final List<Schema> allOf;
    private final List<Schema> anyOf;
}
```

### 2. SchemaType Enum

Defines all supported data types:

```java
public enum SchemaType {
    OBJECT,    // Maps/dictionaries
    ARRAY,     // Lists
    STRING,   // Text
    NUMBER,   // Decimal numbers only (3.14, -1.5)
    INTEGER,   // Whole numbers only (42, -10)
    BOOLEAN,   // true/false
    NULL,      // null value
    ANY        // Accepts any type
}
```

### 3. Validator Interface

All validators implement this contract:

```java
public interface Validator {
    List<ValidationError> validate(Object data, Schema schema, String path, String parentKey);
}
```

Returns an empty list if validation passes, or a list of errors if validation fails.

---

## Validation Pipeline

### Step 1: Schema Loading

```
JSON/YAML File
      │
      ▼
FileSchemaLoader.parseSchema()
      │
      ├── Parses type keyword
      ├── Parses properties
      ├── Parses constraints (min, max, pattern, etc.)
      ├── Parses allOf/anyOf composition (NEW)
      └── Returns Schema object
```

### Step 2: Schema Registration

```
Schema object
      │
      ▼
SchemaRegistry.registerSchema()
      │
      ├── Stores in memory cache
      ├── Adds timestamp for cache expiry
      └── Returns when needed for validation
```

### Step 3: Validation Dispatch

```
Data + Schema
      │
      ▼
ValidatorDispatcher.forSchema()
      │
      ├── Checks schema.getType()
      ├── OBJECT  → ObjectValidator
      ├── ARRAY   → ArrayValidator
      └── OTHER   → PrimitiveValidator
```

### Step 4: Type-Specific Validation

#### ObjectValidator validates objects:
```
1. Check if data is Map
2. Handle allOf composition (validate against ALL schemas)
3. Handle anyOf composition (validate against AT LEAST ONE schema)
4. Validate required fields
5. Validate properties
6. Validate patternProperties
7. Validate additionalProperties
```

#### ArrayValidator validates arrays:
```
1. Check if data is List
2. Validate each element against items schema
```

#### PrimitiveValidator validates primitives:
```
1. Check type match (STRING, NUMBER, INTEGER, BOOLEAN, NULL)
2. Validate enum constraints
3. Validate numeric constraints (min/max)
4. Validate string constraints (minLength, maxLength, pattern)
```

### Step 5: Error Collection

```
Validation Errors
      │
      ▼
ValidationResult.from(errors)
      │
      ├── Creates immutable result
      ├── Sets success = errors.isEmpty()
      └── Returns ValidationResult
```

---

## Validation Flow Diagram

```
Input Data
    │
    ▼
┌─────────────────────────┐
│ Is there a $ref?       │───YES──▶ Resolve reference
└───────────┬─────────────┘
            │ NO
            ▼
┌─────────────────────────┐
│ Get Validator by Type  │
└───────────┬─────────────┘
            │
            ▼
    ┌───────────────┐
    │ Type: OBJECT? │
    └───────┬───────┘
       YES/│\NO
          / │ \
         /  │  \
        ▼   ▼   ▼
   ┌─────┐ ┌─────┐ ┌──────────┐
   │Obj  │ │Arr  │ │Primitive │
   │Valid│ │Valid│ │Validator │
   └──┬──┘ └──┬──┘ └────┬─────┘
      │      │         │
      ▼      ▼         ▼
   ┌────────────────────────┐
   │ Collect Errors         │
   └───────────┬────────────┘
               │
               ▼
        ValidationResult
               │
               ▼
        Success/Failure
```

---

## Class Dependencies

```
                    ┌─────────────────┐
                    │    Schema       │
                    └────────┬────────┘
                             │ contains
              ┌──────────────┼──────────────┐
              │              │              │
              ▼              ▼              ▼
      ┌───────────────┐ ┌──────────┐ ┌───────────┐
      │ SchemaType    │ │  Schema  │ │  Schema   │
      │ (enum)        │ │ Registry │ │RefResolver│
      └───────────────┘ └──────────┘ └───────────┘
                             │              │
                             └───────┬──────┘
                                     │
                                     ▼
                         ┌─────────────────────┐
                         │ ValidatorDispatcher │
                         └──────────┬──────────┘
                                    │
           ┌────────────────────────┼────────────────────────┐
           │                        │                        │
           ▼                        ▼                        ▼
   ┌───────────────┐       ┌───────────────┐       ┌───────────────┐
   │ ObjectValid. │       │ ArrayValidator │       │PrimitiveValid.│
   └───────────────┘       └───────────────┘       └───────────────┘
           │                        │                        │
           └────────────────────────┴────────────────────────┘
                                    │
                                    ▼
                          ┌─────────────────┐
                          │ValidationError  │
                          └─────────────────┘
                                    │
                                    ▼
                          ┌─────────────────┐
                          │ValidationResult │
                          └─────────────────┘
```

---

## Key Design Decisions

### 1. Separate INTEGER from NUMBER

Following JSON Schema specification, INTEGER and NUMBER are distinct types:
- `integer`: Whole numbers only (42, -10, 0)
- `number`: Decimal numbers only (3.14, -1.5)

This provides type safety and clearer schema intent.

### 2. Composition Before Property Validation

The validation order in ObjectValidator is:
1. $ref resolution
2. Type check
3. allOf/anyOf composition
4. Required fields
5. Properties
6. Pattern properties
7. Additional properties

This ensures composition is validated even if the data is not an object, but errors are more meaningful.

### 3. Immutable Schema Objects

Schema objects are immutable once created, making them thread-safe for concurrent validation.

### 4. Cache with Expiry

SchemaRegistry uses time-based cache expiry to handle schema updates without restart.

---

## Error Handling

### ValidationError Structure

```java
public class ValidationError {
    private final String nodePath;      // e.g., "$.user.name"
    private final String expectedType;   // e.g., "string", "allOf[0].minLength"
    private final String actualType;      // e.g., "Integer", "null"
    private final String description;    // Human-readable message
}
```

### Error Path Format

| Location | Path Example |
|----------|--------------|
| Root | `$` |
| Property | `$.name` |
| Nested | `$.user.profile.age` |
| Array | `$.items[0]` |
| allOf error | `allOf[0].name` |
| anyOf error | `anyOf[1].age` |

---

## Performance Considerations

1. **Schema Caching**: Schemas are cached to avoid re-parsing
2. **Early Returns**: Validation stops on type mismatch
3. **Immutable Objects**: Thread-safe for concurrent validation
4. **Single Pass**: Validation is done in a single pass through the data

---

## Extension Points

### Adding New Validators

1. Create a class implementing `Validator`
2. Add new case to `ValidatorDispatcher.forSchema()`
3. Add new type to `SchemaType` enum (if needed)

### Adding New Schema Keywords

1. Add field to `Schema` class
2. Update `FileSchemaLoader.toSchema()` to parse the keyword
3. Update relevant validator to use the new constraint

---

## Next Steps

- [API Reference](api-reference.md)
- [Schema Construction Guide](construction.md)
- [Integration Guide](guides/integration.md)

---

[← Back to Documentation](docs/README.md)
