# API Reference

Complete API documentation for Schema Validator.

---

## Core Classes

### Schema

Represents a parsed JSON Schema with all its constraints.

```java
public class Schema {
    // Constructors
    public Schema(String name, SchemaType type, Map<String, Schema> properties, Schema itemSchema)
    public Schema(String name, SchemaType type, Map<String, Schema> properties, Schema itemSchema, List<String> requiredFields)
    public Schema(String name, SchemaType type, Map<String, Schema> properties, Schema itemSchema, List<String> requiredFields, boolean additionalProperties)
    public Schema(String name, SchemaType type, Map<String, Schema> properties, Map<String, Schema> patternProperties, Schema itemSchema, List<String> requiredFields, boolean additionalProperties, Number minimum, Number maximum, boolean exclusiveMinimum, boolean exclusiveMaximum, Integer minLength, Integer maxLength, String pattern, List<Object> enumValues, String ref, String version, String compatibility, List<Schema> allOf, List<Schema> anyOf)
    
    // Getters
    public String getName()
    public SchemaType getType()
    public Map<String, Schema> getProperties()
    public Map<String, Schema> getPatternProperties()
    public Schema getItemSchema()
    public List<String> getRequiredFields()
    public boolean isAdditionalPropertiesAllowed()
    public Number getMinimum()
    public Number getMaximum()
    public boolean isExclusiveMinimum()
    public boolean isExclusiveMaximum()
    public Integer getMinLength()
    public Integer getMaxLength()
    public String getPattern()
    public List<Object> getEnumValues()
    public String getRef()
    public String getVersion()
    public String getCompatibility()
    
    // Composition (NEW)
    public List<Schema> getAllOf()
    public List<Schema> getAnyOf()
    public boolean hasAllOf()
    public boolean hasAnyOf()
    
    // Enum Check
    public boolean isValidEnum(Object value)
    public boolean isRef()
}
```

---

### SchemaType

Enum defining all supported data types.

```java
public enum SchemaType {
    OBJECT,    // Maps/dictionaries {"key": "value"}
    ARRAY,     // Lists [1, 2, 3]
    STRING,    // Text "hello"
    NUMBER,    // Decimal numbers only 3.14, -1.5
    INTEGER,   // Whole numbers only 42, -10
    BOOLEAN,   // true or false
    NULL,      // null
    ANY        // Accepts any type
}
```

---

### Validator

Base interface for all validators.

```java
public interface Validator {
    /**
     * Validate data against a schema.
     *
     * @param data      Runtime data node to validate
     * @param schema    Target schema to validate against
     * @param path      Logical node path (e.g., "$.user.name")
     * @param parentKey Optional parent key for context
     * @return List of validation errors; empty list means success
     */
    List<ValidationError> validate(Object data, Schema schema, String path, String parentKey);
}
```

---

### ValidationError

Represents a single validation error.

```java
public class ValidationError {
    // Constructor
    public ValidationError(String nodePath, String expectedType, String actualType, String description)
    
    // Getters
    public String getNodePath()      // Path to the error (e.g., "$.user.age")
    public String getExpectedType()   // Expected type/constraint (e.g., "integer", "minLength")
    public String getActualType()    // Actual type received (e.g., "String")
    public String getDescription()   // Human-readable error message
    
    // Inherited from Object
    public String toString()
}
```

---

### ValidationResult

Wrapper for validation results.

```java
public class ValidationResult {
    // Factory
    public static ValidationResult from(List<ValidationError> errors)
    
    // Getters
    public boolean isSuccess()                   // true if no errors
    public List<ValidationError> getErrors()     // List of errors (empty if success)
}
```

---

### SchemaRegistry

In-memory schema storage with caching.

```java
public class SchemaRegistry {
    // Constructors
    public SchemaRegistry()
    public SchemaRegistry(boolean cacheEnabled, long cacheExpiryMs)
    
    // Registration
    public void registerSchema(String name, Schema schema)
    
    // Retrieval
    public Optional<Schema> getSchema(String name)
    public boolean contains(String name)
    public Set<String> getAllSchemaNames()
    
    // Cache Management
    public void clearCache()
    public void setCacheEnabled(boolean enabled)
    public boolean isCacheEnabled()
    public int getSchemaCount()
}
```

---

### FileSchemaLoader

Loads and parses JSON/YAML schema files.

```java
public class FileSchemaLoader {
    // Load from file
    public Schema loadSchema(Path path) throws IOException
    public Schema loadSchema(Path path, String baseUri) throws IOException
    
    // Load multiple
    public List<Schema> loadSchemasFromDirectory(Path directory, String extension)
    
    // Parse raw JSON/YAML
    public Schema toSchema(String name, Map<String, Object> raw)
}
```

---

### ValidatorDispatcher

Routes validation to the appropriate validator based on schema type.

```java
public final class ValidatorDispatcher {
    // Get validator for schema type
    public static Validator forSchema(Schema schema)
    
    // Returns:
    // - OBJECT → ObjectValidator
    // - ARRAY  → ArrayValidator
    // - Other  → PrimitiveValidator
}
```

---

## Validators

### ObjectValidator

Validates object/map structures.

```java
public class ObjectValidator implements Validator {
    // Constructors
    public ObjectValidator()
    public ObjectValidator(SchemaRefResolver refResolver)
    
    // Configuration
    public void setRefResolver(SchemaRefResolver refResolver)
    
    // Validation
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey)
}
```

**Validates:**
- Type is object
- allOf composition
- anyOf composition
- Required fields
- Properties against schema
- Pattern properties
- Additional properties

---

### ArrayValidator

Validates array/list structures.

```java
public class ArrayValidator implements Validator {
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey)
}
```

**Validates:**
- Type is array
- Items against item schema
- minItems / maxItems
- uniqueItems

---

### PrimitiveValidator

Validates primitive values (strings, numbers, booleans, null).

```java
public class PrimitiveValidator implements Validator {
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey)
}
```

**Validates:**
- Type match (STRING, NUMBER, INTEGER, BOOLEAN, NULL)
- Enum values
- Numeric constraints (minimum, maximum)
- String constraints (minLength, maxLength, pattern)

---

## Utility Classes

### ValidationUtils

```java
public final class ValidationUtils {
    // Get type name for error messages
    public static String typeName(Object data)
    // Returns: "String", "Integer", "ArrayList", "null", etc.
}
```

---

## Usage Examples

### Basic Validation

```java
// Create schema programmatically
Schema schema = new Schema(
    "player",
    SchemaType.OBJECT,
    Map.of(
        "name", new Schema("name", SchemaType.STRING, null, null),
        "age", new Schema("age", SchemaType.INTEGER, null, null)
    ),
    null,
    List.of("name")
);

// Validate data
Map<String, Object> playerData = Map.of(
    "name", "John",
    "age", 25
);

Validator validator = new ObjectValidator();
List<ValidationError> errors = validator.validate(playerData, schema, "$", null);

if (errors.isEmpty()) {
    System.out.println("Valid!");
} else {
    errors.forEach(e -> System.out.println(e.getDescription()));
}
```

### Using SchemaRegistry

```java
// Create registry
SchemaRegistry registry = new SchemaRegistry();

// Load and register schema
FileSchemaLoader loader = new FileSchemaLoader();
Schema schema = loader.loadSchema(Path.of("schemas/player.json"));
registry.registerSchema("player", schema);

// Later, retrieve and validate
Schema savedSchema = registry.getSchema("player").orElseThrow();
Validator validator = ValidatorDispatcher.forSchema(savedSchema);
ValidationResult result = ValidationResult.from(
    validator.validate(data, savedSchema, "$", null)
);

if (result.isSuccess()) {
    // Handle success
}
```

---

## Exception Handling

The validation API does not throw exceptions for validation failures. Instead, it returns a list of `ValidationError` objects. Exceptions may be thrown during schema loading:

```java
try {
    Schema schema = loader.loadSchema(Path.of("schema.json"));
} catch (IOException e) {
    // Handle file not found or parse errors
}
```

---

## Next Steps

- [Architecture](architecture.md)
- [Schema Construction](construction.md)
- [Integration Guide](guides/integration.md)

---

[← Back to Documentation](docs/README.md)
