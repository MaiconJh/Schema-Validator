# Integration Guide

Guide to integrating Schema Validator into your projects.

---

## Maven Integration

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.maiconjh</groupId>
    <artifactId>schema-validator</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## Gradle Integration

Add to your `build.gradle`:

```groovy
dependencies {
    implementation 'com.maiconjh:schema-validator:1.0.0'
}
```

---

## Basic Integration

### Step 1: Create SchemaRegistry

```java
import com.maiconjh.schemacr.schemes.*;
import com.maiconjh.schemacr.validation.*;

SchemaRegistry registry = new SchemaRegistry();
```

### Step 2: Load Schemas

```java
import java.nio.file.Path;

FileSchemaLoader loader = new FileSchemaLoader();

// Load single schema
Schema playerSchema = loader.loadSchema(Path.of("schemas/player.json"));
registry.registerSchema("player", playerSchema);

// Load multiple schemas
List<Schema> schemas = loader.loadSchemasFromDirectory(
    Path.of("schemas"), 
    ".json"
);
schemas.forEach(s -> registry.registerSchema(s.getName(), s));
```

### Step 3: Validate Data

```java
// Get schema from registry
Schema schema = registry.getSchema("player")
    .orElseThrow(() -> new RuntimeException("Schema not found"));

// Create validator
Validator validator = ValidatorDispatcher.forSchema(schema);

// Validate
Object data = /* your data */;
ValidationResult result = ValidationResult.from(
    validator.validate(data, schema, "$", null)
);

if (result.isSuccess()) {
    System.out.println("Validation passed!");
} else {
    result.getErrors().forEach(error -> 
        System.out.println(error.getNodePath() + ": " + error.getDescription())
    );
}
```

---

## Skript Integration

The plugin provides Skript integration for Minecraft servers.

### Commands

#### Validate YAML

```skript
validate yaml "players/%player%" using schema "player"
set {_result} to last schema validation result
```

#### Validate JSON

```skript
validate json "data.json" using schema "inventory"
set {_result} to last schema validation result
```

### Expressions

#### Last Validation Result

```skript
set {_errors::*} to last schema validation errors
set {_success} to last schema validation result is successful
```

#### Get Specific Error

```skript
loop last schema validation errors:
    broadcast "%loop-index%: %loop-value%"
```

---

## Programmatic Validation

### Creating Schemas Programmatically

```java
// Simple object schema
Schema playerSchema = new Schema(
    "player",
    SchemaType.OBJECT,
    Map.of(
        "name", new Schema("name", SchemaType.STRING, null, null),
        "level", new Schema("level", SchemaType.INTEGER, null, null),
        "health", new Schema("health", SchemaType.NUMBER, null, null)
    ),
    null,
    List.of("name", "level")
);

// With constraints
Schema constrainedSchema = new Schema(
    "item",
    SchemaType.OBJECT,
    Map.of(
        "id", new Schema("id", SchemaType.STRING, null, null),
        "quantity", new Schema("quantity", SchemaType.INTEGER, null, null, 
            List.of("quantity"), false, null, null, false, false, 
            1, 64, null, null, null, null, null, null)
    ),
    null,
    List.of("id", "quantity")
);
```

### Schema with Composition

```java
// allOf - combining constraints
List<Schema> allOfSchemas = List.of(
    new Schema("stringType", SchemaType.STRING, null, null),
    new Schema("minLen3", SchemaType.STRING, null, null, null, false, 
        null, null, false, false, 3, null, null, null, null, null, null, null)
);

Schema combinedSchema = new Schema(
    "username",
    SchemaType.OBJECT,
    Map.of("username", new Schema("username", SchemaType.STRING, null, null)),
    null,
    List.of("username"),
    false, null, null, false, false, null, null, null, null,
    null, null, null, null, allOfSchemas, null
);

// anyOf - alternative schemas
List<Schema> anyOfSchemas = List.of(
    new Schema("v1", SchemaType.STRING, null, null),
    new Schema("v2", SchemaType.INTEGER, null, null)
);

Schema flexibleSchema = new Schema(
    "value",
    SchemaType.OBJECT,
    Map.of("data", new Schema("data", SchemaType.ANY, null, null)),
    null,
    null, false, null, null, false, false, null, null, null, null,
    null, null, null, null, null, anyOfSchemas
);
```

---

## Custom Validators

### Creating a Custom Validator

```java
public class CustomValidator implements Validator {
    
    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();
        
        // Your custom validation logic
        
        return errors;
    }
}

// Register with dispatcher
// Note: Currently validators are dispatched by type in ValidatorDispatcher
```

---

## Cache Management

```java
// Configure cache
SchemaRegistry registry = new SchemaRegistry(true, 5 * 60 * 1000); // 5 minutes

// Disable cache (for development)
registry.setCacheEnabled(false);

// Clear cache manually
registry.clearCache();

// Check cache status
System.out.println("Cached schemas: " + registry.getSchemaCount());
```

---

## Error Handling Best Practices

### Always Check for Null

```java
Optional<Schema> schemaOpt = registry.getSchema("player");
if (schemaOpt.isEmpty()) {
    // Handle missing schema
    return;
}
Schema schema = schemaOpt.get();
```

### Handle File Loading Errors

```java
try {
    Schema schema = loader.loadSchema(Path.of("schema.json"));
    registry.registerSchema("player", schema);
} catch (IOException e) {
    // Log error
    logger.error("Failed to load schema: " + e.getMessage());
    // Decide: skip or fail
}
```

### Collect All Errors

```java
// Don't return on first error - collect all
List<ValidationError> allErrors = new ArrayList<>();

// Validation continues through all fields
for (Map.Entry<String, Object> entry : data.entrySet()) {
    Schema propSchema = schema.getProperties().get(entry.getKey());
    if (propSchema != null) {
        List<ValidationError> errors = validator.validate(
            entry.getValue(), 
            propSchema, 
            path + "." + entry.getKey(), 
            entry.getKey()
        );
        allErrors.addAll(errors);
    }
}
```

---

## Performance Tips

1. **Reuse Validators**: Create validators once, reuse for multiple validations
2. **Cache Schemas**: Use SchemaRegistry for automatic caching
3. **Validate Early**: Validate at input time, not at use time
4. **Batch Validation**: Group related validations

---

## Testing Integration

```java
@Test
void testPlayerValidation() {
    // Setup
    SchemaRegistry registry = new SchemaRegistry();
    FileSchemaLoader loader = new FileSchemaLoader();
    
    // Load schema
    Schema schema = loader.loadSchema(Path.of("test-schema.json"));
    registry.registerSchema("player", schema);
    
    // Test valid data
    Map<String, Object> validData = Map.of(
        "name", "Player1",
        "level", 10,
        "health", 100.0
    );
    
    Validator validator = ValidatorDispatcher.forSchema(schema);
    ValidationResult result = ValidationResult.from(
        validator.validate(validData, schema, "$", null)
    );
    
    assertTrue(result.isSuccess());
    
    // Test invalid data
    Map<String, Object> invalidData = Map.of(
        "name", "P",  // too short
        "level", -5    // negative
    );
    
    result = ValidationResult.from(
        validator.validate(invalidData, schema, "$", null)
    );
    
    assertFalse(result.isSuccess());
    assertEquals(2, result.getErrors().size());
}
```

---

## Next Steps

- [Architecture](architecture.md)
- [API Reference](api-reference.md)
- [Schema Construction](construction.md)

---

[← Back to Documentation](docs/README.md)
