# Schema Validator - Technical Documentation

**Developer Documentation for JSON/YAML Schema Validation System**

---

## Overview

Schema Validator is a powerful data validation system for Minecraft servers with Skript integration. This documentation covers technical details for developers who need to extend, integrate, or contribute to the project.

---

## Architecture

### Core Components

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
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Class Diagram

| Class | Responsibility |
|-------|----------------|
| `Schema` | Represents a parsed JSON Schema with all constraints |
| `SchemaType` | Enum defining supported data types |
| `Validator` | Base interface for all validators |
| `ObjectValidator` | Validates object/map structures |
| `ArrayValidator` | Validates array/list structures |
| `PrimitiveValidator` | Validates primitive values |
| `SchemaRegistry` | In-memory schema cache |
| `FileSchemaLoader` | Parses JSON/YAML schema files |
| `ValidationError` | Error details with path and description |
| `ValidationResult` | Wrapper for validation results |

---

## API Reference

### Schema

```java
// Core constructors
public Schema(String name, SchemaType type, Map<String, Schema> properties, Schema itemSchema)
public Schema(String name, SchemaType type, Map<String, Schema> properties, Schema itemSchema, List<String> requiredFields)

// Full constructor with all options
public Schema(String name, SchemaType type, Map<String, Schema> properties, 
    Map<String, Schema> patternProperties, Schema itemSchema, List<String> requiredFields, 
    boolean additionalProperties, Number minimum, Number maximum, 
    boolean exclusiveMinimum, boolean exclusiveMaximum, Integer minLength, 
    Integer maxLength, String pattern, List<Object> enumValues, String ref, 
    String version, String compatibility, List<Schema> allOf, List<Schema> anyOf)

// Key methods
public SchemaType getType()
public Map<String, Schema> getProperties()
public Schema getItemSchema()
public List<String> getRequiredFields()
public boolean hasAllOf()
public boolean hasAnyOf()
public List<Schema> getAllOf()
public List<Schema> getAnyOf()
```

### SchemaType Enum

```java
public enum SchemaType {
    OBJECT,    // Maps/dictionaries
    ARRAY,     // Lists
    STRING,    // Text
    NUMBER,    // Decimal numbers only (3.14)
    INTEGER,   // Whole numbers only (42)
    BOOLEAN,   // true/false
    NULL,      // null value
    ANY        // Accepts any type
}
```

### Validator Interface

```java
public interface Validator {
    List<ValidationError> validate(Object data, Schema schema, String path, String parentKey);
}
```

### ValidationError

```java
public class ValidationError {
    public ValidationError(String nodePath, String expectedType, String actualType, String description)
    public String getNodePath()    // Path: "$.user.name"
    public String getExpectedType() // Expected: "integer"
    public String getActualType()   // Received: "String"
    public String getDescription()  // Human-readable message
}
```

### SchemaRegistry

```java
public class SchemaRegistry {
    public SchemaRegistry()  // Default: cache enabled, 5 min expiry
    public SchemaRegistry(boolean cacheEnabled, long cacheExpiryMs)
    
    public void registerSchema(String name, Schema schema)
    public Optional<Schema> getSchema(String name)
    public void clearCache()
    public void setCacheEnabled(boolean enabled)
}
```

### FileSchemaLoader

```java
public class FileSchemaLoader {
    public Schema loadSchema(Path path) throws IOException
    public Schema loadSchema(Path path, String baseUri) throws IOException
    public List<Schema> loadSchemasFromDirectory(Path directory, String extension)
    public Schema toSchema(String name, Map<String, Object> raw)
}
```

---

## Data Types

### INTEGER vs NUMBER

This implementation follows the JSON Schema specification:

| Type | JSON Value | Accepts | Use Case |
|------|-------------|---------|----------|
| `integer` | `"integer"` | 42, -10, 0 | Counts, IDs |
| `number` | `"number"` | 3.14, -1.5 | Measurements |

**Important**: `number` does NOT accept integers. Use `integer` for whole numbers.

```json
// For whole numbers only
{ "type": "integer", "minimum": 1 }

// For decimals only  
{ "type": "number", "minimum": 0.0 }
```

---

## Schema Composition

### allOf

Data must validate against ALL schemas:

```json
{
  "allOf": [
    { "type": "string" },
    { "minLength": 3 }
  ]
}
```

### anyOf

Data must validate against AT LEAST ONE schema:

```json
{
  "anyOf": [
    { "type": "string" },
    { "type": "integer" }
  ]
}
```

---

## Integration Examples

### Maven

```xml
<dependency>
    <groupId>com.maiconjh</groupId>
    <artifactId>schema-validator</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Java Integration

```java
// Load and validate
FileSchemaLoader loader = new FileSchemaLoader();
Schema schema = loader.loadSchema(Path.of("schema.json"));

Validator validator = ValidatorDispatcher.forSchema(schema);
ValidationResult result = ValidationResult.from(
    validator.validate(data, schema, "$", null)
);

if (result.isSuccess()) {
    // Handle success
}
```

### Custom Validator

```java
public class CustomValidator implements Validator {
    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();
        
        // Custom validation logic
        
        return errors;
    }
}
```

---

## Validation Flow

1. **Load Schema**: Parse JSON/YAML to Schema object
2. **Register**: Store in SchemaRegistry (cached)
3. **Dispatch**: ValidatorDispatcher selects validator by type
4. **Validate**: Type-specific validator checks constraints
5. **Return**: ValidationResult with errors (if any)

---

## Contributing

### Adding New Validators

1. Create class implementing `Validator`
2. Add case to `ValidatorDispatcher.forSchema()`
3. Add type to `SchemaType` enum if needed

### Adding New Schema Keywords

1. Add field to `Schema` class
2. Update `FileSchemaLoader.toSchema()` to parse
3. Update relevant validator to use constraint

---

## File Structure

```
docs/
├── README.md              # This file
├── architecture.md        # System architecture
├── api-reference.md       # Complete API docs
├── construction.md        # Schema building guide
├── configuration.md       # Plugin configuration
├── installation.md        # Installation guide
├── quickstart.md         # Quick tutorial
├── faq.md               # FAQ
├── guides/
│   └── integration.md    # Integration guide
├── reference/
│   ├── data-types.md           # Data types
│   ├── json-schema.md          # JSON Schema keywords
│   ├── schema-composition.md   # allOf/anyOf
│   └── skript-syntax.md        # Skript commands
└── tutorials/
    ├── player-data-validation.md
    ├── custom-blocks.md
    └── inventory-validation.md
```

---

## Quick Links

| Resource | Description |
|----------|-------------|
| [Architecture](architecture.md) | Internal system design |
| [API Reference](api-reference.md) | Complete API documentation |
| [Construction Guide](construction.md) | Building valid schemas |
| [Integration Guide](guides/integration.md) | Using in Java projects |
| [Schema Composition](reference/schema-composition.md) | allOf and anyOf |
| [Data Types](reference/data-types.md) | Type system |

---

## Support

- **Issues**: [GitHub Issues](https://github.com/MaiconJh/Schema-Validator/issues)
- **Wiki**: [User Guide](https://github.com/MaiconJh/Schema-Validator/wiki)

---

*Last updated: 2026-03-19*
