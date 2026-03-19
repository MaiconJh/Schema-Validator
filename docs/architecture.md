# Schema-Validator Technical Architecture

> **Version:** 2.0  
> **Last Updated:** 2026-03-19  
> **Status:** Technical Documentation

---

## Table of Contents

1. [Architecture Overview](#1-architecture-overview)
2. [Component Architecture](#2-component-architecture)
3. [Module Documentation](#3-module-documentation)
4. [Validation Flow](#4-validation-flow)
5. [Integration Guide](#5-integration-guide)
6. [Module Dependencies](#6-module-dependencies)
7. [Change History](#7-change-history)

---

## 1. Architecture Overview

### 1.1 System Context

Schema-Validator is a JSON/YAML validation system designed for Minecraft plugins with Skript integration. It provides runtime data validation against predefined schemas with support for complex validation rules including composition, conditionals, and references.

### 1.2 Component Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           SCHEMA-VALIDATOR ARCHITECTURE                         │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                  │
│  ┌──────────────────┐    ┌───────────────────┐    ┌────────────────────────┐  │
│  │   JSON/YAML      │    │   PluginConfig    │    │   SupportedKeywords   │  │
│  │   Schema Files   │    │   (Configuration) │    │   Registry            │  │
│  └────────┬─────────┘    └─────────┬─────────┘    └──────────┬───────────┘  │
│           │                         │                       │                │
│           │                         │                       │                │
│           ▼                         ▼                       ▼                │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │                         FILE SCHEMA LOADER                               │   │
│  │  ┌──────────────────────────────────────────────────────────────────┐   │   │
│  │  │  • Parses JSON/YAML files                                         │   │   │
│  │  │  • Validates against SupportedKeywordsRegistry                   │   │   │
│  │  │  • Detects unsupported keywords (fail-fast mode)                │   │   │
│  │  │  • Extracts definitions, $ref resolution                         │   │   │
│  │  └──────────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                    │                                            │
│                                    ▼                                            │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │                         SCHEMA REGISTRY                                  │   │
│  │  ┌──────────────────────────────────────────────────────────────────┐   │   │
│  │  │  • In-memory cache                                                │   │   │
│  │  │  • Time-based expiry                                             │   │   │
│  │  │  • Schema lookup by name                                          │   │   │
│  │  └──────────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                    │                                            │
│                                    ▼                                            │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │                      VALIDATION SERVICE                                  │   │
│  │                                                                           │   │
│  │   ┌────────────────┐    ┌─────────────────┐    ┌────────────────────┐    │   │
│  │   │  Schema       │───▶│ Validator       │───▶│ ValidationError   │    │   │
│  │   │  (Model)     │    │ Dispatcher      │    │ Collector         │    │   │
│  │   └────────────────┘    └─────────────────┘    └────────────────────┘    │   │
│  │                                    │                                     │   │
│  │           ┌────────────────────────┼────────────────────────┐            │   │
│  │           │                        │                        │            │   │
│  │           ▼                        ▼                        ▼            │   │
│  │   ┌──────────────┐      ┌──────────────┐      ┌──────────────────┐  │   │
│  │   │ ObjectValid.  │      │ ArrayValidator│      │ PrimitiveValidator│  │   │
│  │   └──────────────┘      └──────────────┘      └──────────────────┘  │   │
│  │                                                                           │   │
│  │   ┌──────────────┐      ┌──────────────┐      ┌──────────────────┐  │   │
│  │   │ OneOfValid.   │      │ NotValidator │      │ ConditionalValid. │  │   │
│  │   └──────────────┘      └──────────────┘      └──────────────────┘  │   │
│  │                                                                           │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                    │                                            │
│                                    ▼                                            │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │                      VALIDATION RESULT                                   │   │
│  │  • Success/Failure status                                                │   │
│  │  • List of ValidationError objects                                       │   │
│  │  • Skript bridge for error access                                        │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                                                                  │
└──────────────────────────────────────────────────────────────────────────────────┘

External Dependencies:
┌──────────────────┐    ┌───────────────────┐    ┌────────────────────────┐
│   Bukkit/Paper   │    │   Skript Lang    │    │   Jackson YAML/JSON   │
│   Minecraft API  │    │   Integration    │    │   Parser Libraries    │
└──────────────────┘    └───────────────────┘    └────────────────────────┘
```

---

## 2. Component Architecture

### 2.1 Module Overview

| Module | Package | Responsibility |
|--------|---------|----------------|
| **SupportedKeywordsRegistry** | `com.maiconjh.schemacr.schemes` | Central registry of supported JSON Schema keywords |
| **FileSchemaLoader** | `com.maiconjh.schemacr.schemes` | Parses JSON/YAML schema files |
| **Schema** | `com.maiconjh.schemacr.schemes` | Immutable schema data model |
| **SchemaRegistry** | `com.maiconjh.schemacr.schemes` | In-memory schema cache |
| **SchemaRefResolver** | `com.maiconjh.schemacr.schemes` | Resolves $ref references |
| **ValidationService** | `com.maiconjh.schemacr.core` | Main validation facade |
| **ValidatorDispatcher** | `com.maiconjh.schemacr.validation` | Routes to type-specific validators |
| **ObjectValidator** | `com.maiconjh.schemacr.validation` | Validates object structures |
| **ArrayValidator** | `com.maiconjh.schemacr.validation` | Validates array structures |
| **PrimitiveValidator** | `com.maiconjh.schemacr.validation` | Validates primitive types |
| **ValidationError** | `com.maiconjh.schemacr.validation` | Error detail model |
| **PluginConfig** | `com.maiconjh.schemacr.config` | Configuration management |

---

## 3. Module Documentation

### 3.1 SupportedKeywordsRegistry

**Package:** `com.maiconjh.schemacr.schemes`

**Purpose:** Central registry containing all supported JSON Schema keywords. Provides keyword validation, category organization, and export capabilities.

**Responsibilities:**
- Maintain list of 39 supported JSON Schema keywords
- Categorize keywords by type (Object, Array, String, Number, etc.)
- Validate if a keyword is supported
- Export to JSON/Markdown for documentation

**Public Interface:**

```java
public class SupportedKeywordsRegistry {
    
    // Categories for keyword organization
    public enum KeywordCategory {
        TYPE_KEYWORDS,
        OBJECT_KEYWORDS,
        ARRAY_KEYWORDS,
        STRING_KEYWORDS,
        NUMBER_KEYWORDS,
        COMPOSITION_KEYWORDS,
        CONDITIONAL_KEYWORDS,
        REFERENCE_KEYWORDS,
        FORMAT_KEYWORDS,
        CONSTRAINT_KEYWORDS
    }
    
    // Constructors
    public SupportedKeywordsRegistry()
    public SupportedKeywordsRegistry(Logger logger)
    
    // Core Methods
    public boolean isKeywordSupported(String keyword)
    public Set<String> getAllSupportedKeywords()
    public int getSupportedKeywordCount()
    public boolean checkAndLogUnsupported(String keyword)
    public Set<String> findUnsupportedKeywords(Iterable<String> keywords)
    
    // Category Methods
    public Set<String> getKeywordsByCategory(KeywordCategory category)
    
    // Export Methods
    public String toMarkdown()
    public String toJson()
}
```

**Supported Keywords:**

| Category | Keywords |
|----------|----------|
| Type | `type` |
| Object | `properties`, `patternProperties`, `additionalProperties`, `required`, `minProperties`, `maxProperties`, `dependencies` |
| Array | `items`, `minItems`, `maxItems`, `uniqueItems`, `additionalItems` |
| String | `minLength`, `maxLength`, `pattern`, `format` |
| Number | `minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf` |
| Composition | `allOf`, `anyOf`, `oneOf`, `not` |
| Conditional | `if`, `then`, `else` |
| Reference | `$ref`, `definitions`, `$schema`, `$id` |
| Constraint | `enum`, `const` |

**Usage Example:**

```java
// Create registry
SupportedKeywordsRegistry registry = new SupportedKeywordsRegistry();

// Check if keyword is supported
if (registry.isKeywordSupported("type")) {
    System.out.println("Type keyword is supported!");
}

// Get all supported keywords
Set<String> allKeywords = registry.getAllSupportedKeywords();
System.out.println("Total keywords: " + allKeywords.size());

// Find unsupported keywords in a list
Set<String> unknown = registry.findUnsupportedKeywords(
    List.of("type", "propertyNames", "contains")
);
System.out.println("Unsupported: " + unknown);
```

---

### 3.2 FileSchemaLoader

**Package:** `com.maiconjh.schemacr.schemes`

**Purpose:** Parses JSON and YAML schema files into Schema objects. Integrates with SupportedKeywordsRegistry for keyword validation.

**Responsibilities:**
- Parse JSON/YAML files
- Create Schema objects from parsed data
- Detect unsupported keywords (with fail-fast mode)
- Extract schema definitions
- Support nested schema parsing

**Public Interface:**

```java
public class FileSchemaLoader {
    
    // Constructors
    public FileSchemaLoader(Logger logger)
    public FileSchemaLoader(Logger logger, SupportedKeywordsRegistry keywordsRegistry)
    
    // Loading Methods
    public Schema load(Path path, String schemaName) throws IOException
    public Schema parseSchema(String schemaName, Map<String, Object> raw)
    
    // Fail-Fast Configuration
    public void setFailFastMode(boolean failFastMode)
    public boolean isFailFastMode()
    
    // Registry Access
    public SupportedKeywordsRegistry getKeywordsRegistry()
    
    // Definition Access
    public Schema getDefinition(String name)
}
```

**Exceptions Thrown:**

| Exception | Condition |
|-----------|-----------|
| `IOException` | File not found or read error |
| `IllegalArgumentException` | Unsupported file extension or unsupported keyword (fail-fast mode) |

**Usage Example:**

```java
// Create loader with custom logger
Logger logger = Logger.getLogger("SchemaLoader");
FileSchemaLoader loader = new FileSchemaLoader(logger);

// Enable fail-fast mode (throws on unsupported keywords)
loader.setFailFastMode(true);

// Load schema from file
Path schemaPath = Path.of("schemas/player.schema.json");
Schema schema = loader.load(schemaPath, "player");

// Or parse from raw map (e.g., from API)
Map<String, Object> rawSchema = Map.of(
    "type", "object",
    "properties", Map.of(
        "name", Map.of("type", "string"),
        "age", Map.of("type", "integer", "minimum", 0)
    )
);
Schema schema = loader.parseSchema("inline", rawSchema);
```

---

### 3.3 Schema

**Package:** `com.maiconjh.schemacr.schemes`

**Purpose:** Immutable data model representing a parsed JSON Schema.

**Public Interface:**

```java
public class Schema {
    
    // Constructors (multiple overloads)
    public Schema(String name, SchemaType type, Map<String, Schema> properties, Schema itemSchema)
    public Schema(String name, SchemaType type, List<Schema> oneOf)
    // ... more constructors
    
    // Type Access
    public String getName()
    public SchemaType getType()
    
    // Object Properties
    public Map<String, Schema> getProperties()
    public Map<String, Schema> getPatternProperties()
    public Schema getItemSchema()
    public List<String> getRequiredFields()
    public boolean isAdditionalPropertiesAllowed()
    public Integer getMinProperties()
    public Integer getMaxProperties()
    
    // Array Constraints
    public Integer getMinItems()
    public Integer getMaxItems()
    public boolean isUniqueItems()
    
    // String Constraints
    public Integer getMinLength()
    public Integer getMaxLength()
    public String getPattern()
    public String getFormat()
    public boolean hasFormat()
    
    // Numeric Constraints
    public Number getMinimum()
    public Number getMaximum()
    public boolean isExclusiveMinimum()
    public boolean isExclusiveMaximum()
    public Number getMultipleOf()
    
    // Enum & Constraints
    public List<Object> getEnumValues()
    public boolean isValidEnum(Object value)
    
    // References
    public String getRef()
    public boolean isRef()
    
    // Composition
    public List<Schema> getAllOf()
    public List<Schema> getAnyOf()
    public List<Schema> getOneOf()
    public boolean hasAllOf()
    public boolean hasAnyOf()
    public boolean hasOneOf()
    
    // Negation
    public Schema getNot()
    public boolean hasNot()
    
    // Conditional
    public Schema getIfSchema()
    public Schema getThenSchema()
    public Schema getElseSchema()
    public boolean hasConditional()
    
    // Metadata
    public String getVersion()
    public String getCompatibility()
}
```

---

### 3.4 ValidationService

**Package:** `com.maiconjh.schemacr.core`

**Purpose:** Main facade for validation operations.

**Public Interface:**

```java
public class ValidationService {
    
    // Constructors
    public ValidationService()
    public ValidationService(SchemaRefResolver refResolver)
    
    // Validation Methods
    public ValidationResult validate(Object data, Schema schema)
    public List<ValidationResult> validateBatch(List<Object> dataList, Schema schema)
    public boolean validateAll(List<Object> dataList, Schema schema)
    public int getFailedCount(List<Object> dataList, Schema schema)
}
```

**Usage Example:**

```java
// Create validation service
ValidationService service = new ValidationService();

// Validate single object
Object data = Map.of(
    "name", "Player1",
    "age", 25
);
ValidationResult result = service.validate(data, schema);

if (result.isSuccess()) {
    System.out.println("Validation passed!");
} else {
    for (ValidationError error : result.getErrors()) {
        System.out.println(error.getNodePath() + ": " + error.getDescription());
    }
}
```

---

### 3.5 ValidatorDispatcher

**Package:** `com.maiconjh.schemacr.validation`

**Purpose:** Routes validation to the appropriate validator based on schema type.

**Public Interface:**

```java
public final class ValidatorDispatcher {
    
    public static Validator forSchema(Schema schema)
}
```

**Type Routing:**

| SchemaType | Validator |
|------------|-----------|
| OBJECT | ObjectValidator |
| ARRAY | ArrayValidator |
| other | PrimitiveValidator |

---

### 3.6 PluginConfig

**Package:** `com.maiconjh.schemacr.config`

**Purpose:** Manages plugin configuration from config.yml.

**Public Interface:**

```java
public class PluginConfig {
    
    public PluginConfig(SchemaValidatorPlugin plugin)
    
    // Configuration Accessors
    public Path getSchemaDirectory()
    public boolean isAutoLoad()
    public boolean isCacheEnabled()
    public boolean isValidateOnLoad()
    public boolean isStrictMode()
    
    // Management
    public void reload()
    public String getSchemaDirectoryName()
}
```

**Configuration Keys:**

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `schema-directory` | String | "schemas" | Directory for schema files |
| `auto-load` | Boolean | true | Auto-load schemas on startup |
| `cache-enabled` | Boolean | true | Enable schema caching |
| `validation-on-load` | Boolean | false | Validate schemas on load |
| `strict-mode` | Boolean | false | Fail on unsupported keywords |

---

## 4. Validation Flow

### 4.1 Complete Validation Pipeline

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           VALIDATION PIPELINE                                     │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                  │
│  STEP 1: SCHEMA LOADING                                                         │
│  ═══════════════════════                                                         │
│                                                                                  │
│  ┌──────────────┐      ┌─────────────────────┐      ┌───────────────────────┐   │
│  │ JSON/YAML   │      │   FileSchemaLoader  │      │ SupportedKeywords    │   │
│  │ File        │ ───▶ │   .load()           │ ───▶ │   Registry           │   │
│  └──────────────┘      └──────────┬──────────┘      │   .isKeywordSupported│   │
│                                   │                  └───────────┬───────────┘   │
│                                   │                              │               │
│                                   ▼                              │               │
│                          ┌─────────────────────┐                   │               │
│                          │   Unsupported      │ ◀────── NO ───────┘               │
│                          │   Keyword?          │                   │               │
│                          └──────────┬──────────┘                   │               │
│                                     │ YES                         │               │
│                          ┌──────────┴──────────┐                   │               │
│                          │                      │                   │               │
│                   ┌──────▼──────┐      ┌───────▼───────┐          │               │
│                   │ Log Warning  │      │ Throw Exception│          │               │
│                   │ (default)    │      │ (strict mode) │          │               │
│                   └─────────────┘      └───────────────┘          │               │
│                                   │                              │               │
│                                   ▼                              │               │
│                          ┌─────────────────────┐                   │               │
│                          │   Schema Object     │ ◀────────────────┘               │
│                          │   (Immutable)       │                                   │
│                          └─────────────────────┘                                   │
│                                                                                  │
│  STEP 2: SCHEMA REGISTRATION                                                    │
│  ═════════════════════════════                                                   │
│                                                                                  │
│  ┌─────────────────────┐      ┌─────────────────────┐                           │
│  │   Schema Object     │      │   SchemaRegistry   │                           │
│  └──────────┬──────────┘      │   .registerSchema() │                           │
│             │                 └──────────┬──────────┘                           │
│             │                            │                                       │
│             ▼                            ▼                                       │
│  ┌─────────────────────────────────────────────────────┐                        │
│  │              In-Memory Cache                         │                        │
│  │  • Schema name → Schema mapping                      │                        │
│  │  • Timestamp for expiry tracking                     │                        │
│  └─────────────────────────────────────────────────────┘                        │
│                                                                                  │
│  STEP 3: VALIDATION DISPATCH                                                    │
│  ═══════════════════════                                                         │
│                                                                                  │
│  ┌─────────────────────┐      ┌─────────────────────┐                           │
│  │   ValidationService │      │  ValidatorDispatcher│                           │
│  │   .validate()      │ ───▶ │  .forSchema()      │                           │
│  └──────────┬──────────┘      └──────────┬──────────┘                           │
│             │                            │                                       │
│             │                  ┌────────┴────────┐                             │
│             │                  │                 │                             │
│             │           ┌──────▼──────┐    ┌─────▼──────┐                     │
│             │           │ SchemaType   │    │            │                     │
│             │           │ = OBJECT?    │    │            │                     │
│             │           └──────┬───────┘    │            │                     │
│             │           YES/──┘ │ NO        │            │                     │
│             │                 ┌──▼───────────┐│            │                     │
│             │                 │              ││            │                     │
│             │                 ▼              ▼▼           │                     │
│             │          ┌──────────┐  ┌────────────┐    │                     │
│             │          │  Object  │  │  Array    │    │                     │
│             │          │ Validator │  │ Validator │    │                     │
│             │          └────┬─────┘  └─────┬──────┘    │                     │
│             │               └──────┬────────┘            │                     │
│             │                      │                     │                     │
│             │               ┌──────▼──────┐             │                     │
│             │               │ Primitive   │             │                     │
│             │               │ Validator   │             │                     │
│             │               └──────┬──────┘             │                     │
│             │                      │                     │                     │
│             ▼                      ▼                     ▼                     │
│  STEP 4: ERROR COLLECTION                                                     │
│  ═════════════════════════                                                     │
│                                                                                  │
│  ┌─────────────────────────────────────────────────────┐                         │
│  │              List<ValidationError>                   │                         │
│  │                                                       │                         │
│  │  ┌───────────────────────────────────────────────┐  │                         │
│  │  │ ValidationError                               │  │                         │
│  │  │  • nodePath: "$.user.age"                   │  │                         │
│  │  │  • expectedType: "integer"                  │  │                         │
│  │  │  • actualType: "String"                     │  │                         │
│  │  │  • description: "Expected integer, got..."  │  │                         │
│  │  └───────────────────────────────────────────────┘  │                         │
│  └─────────────────────────────────────────────────────┘                         │
│                                    │                                            │
│                                    ▼                                            │
│  STEP 5: RESULT CREATION                                                     │
│  ═══════════════════                                                           │
│                                                                                  │
│  ┌─────────────────────────────────────────────────────┐                         │
│  │              ValidationResult                         │                         │
│  │  • isSuccess() = errors.isEmpty()                   │                         │
│  │  • getErrors() = List<ValidationError>              │                         │
│  └─────────────────────────────────────────────────────┘                         │
│                                                                                  │
└──────────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 Step-by-Step Detail

#### Step 1: Schema Loading

1. **File Reading**: FileSchemaLoader reads JSON/YAML file from disk
2. **Keyword Detection**: SupportedKeywordsRegistry checks each keyword
3. **Fail-Fast Check**: If strict-mode enabled, throws on unsupported keyword
4. **Parsing**: toSchema() method parses raw Map into Schema object
5. **Definition Extraction**: definitions section is extracted and stored

#### Step 2: Schema Registration

1. **Cache Storage**: Schema is stored in SchemaRegistry memory cache
2. **Timestamp**: Current time is stored for cache expiry
3. **Name Mapping**: Schema name is indexed for lookup

#### Step 3: Validation Dispatch

1. **Type Detection**: ValidatorDispatcher checks schema.getType()
2. **Route Selection**: Appropriate validator is selected
3. **Validation Execution**: Validator processes data against schema

#### Step 4: Error Collection

1. **Error Creation**: Each validation failure creates ValidationError
2. **Path Tracking**: Error path is tracked (e.g., "$.user.age")
3. **Error Aggregation**: All errors collected into List

#### Step 5: Result Creation

1. **Success Check**: result.isSuccess() = errors.isEmpty()
2. **Result Return**: ValidationResult object returned to caller

---

## 5. Integration Guide

### 5.1 Adding to Your Project

**Gradle Configuration:**

```groovy
dependencies {
    implementation 'com.maiconjh:Schema-Validator:0.3.1'
}
```

**Maven Configuration:**

```xml
<dependency>
    <groupId>com.maiconjh</groupId>
    <artifactId>Schema-Validator</artifactId>
    <version>0.3.1</version>
</dependency>
```

### 5.2 Basic Usage

```java
import com.maiconjh.schemacr.schemes.*;
import com.maiconjh.schemacr.core.ValidationService;
import com.maiconjh.schemacr.validation.ValidationResult;

// 1. Create schema loader
FileSchemaLoader loader = new FileSchemaLoader(logger);

// 2. Load schema
Schema schema = loader.load(Path.of("schemas/player.json"), "player");

// 3. Create validation service
ValidationService service = new ValidationService();

// 4. Prepare data to validate
Map<String, Object> playerData = Map.of(
    "name", "Hero123",
    "level", 50,
    "class", "warrior"
);

// 5. Validate
ValidationResult result = service.validate(playerData, schema);

// 6. Check result
if (result.isSuccess()) {
    System.out.println("Player data is valid!");
} else {
    result.getErrors().forEach(error -> 
        System.out.println(error.getNodePath() + ": " + error.getDescription())
    );
}
```

### 5.3 Using Strict Mode

```java
// Enable strict mode for production
loader.setFailFastMode(true);

try {
    Schema schema = loader.load(Path.of("schemas/player.json"), "player");
} catch (IllegalArgumentException e) {
    System.err.println("Schema contains unsupported keywords: " + e.getMessage());
}
```

### 5.4 Configuration File (config.yml)

```yaml
# Schema-Validator Configuration
schema-directory: "schemas"
auto-load: true
cache-enabled: true
validation-on-load: false
strict-mode: false  # Set to true in production
```

---

## 6. Module Dependencies

### 6.1 Dependency Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              MODULE DEPENDENCIES                                  │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                  │
│                                                                                  │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │                      CONFIGURATION LAYER                                   │   │
│  │  ┌─────────────────────┐                                                │   │
│  │  │    PluginConfig     │                                                │   │
│  │  └──────────┬──────────┘                                                │   │
│  │             │                                                             │   │
│  └─────────────┼─────────────────────────────────────────────────────────────┘   │
│                │                                                               │
│                ▼                                                               │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │                        CORE LAYER                                        │   │
│  │                                                                           │   │
│  │  ┌─────────────────────┐    ┌─────────────────────┐                       │   │
│  │  │ ValidationService   │    │ PluginContext       │                       │   │
│  │  └──────────┬──────────┘    └──────────┬──────────┘                       │   │
│  │             │                          │                                  │   │
│  │  ┌──────────┴──────────┐    ┌──────────┴──────────┐                       │   │
│  │  │                     │    │                     │                       │   │
│  │  ▼                     ▼    ▼                     ▼                       │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌───────────┐  │   │
│  │  │  Validator   │  │   Schema     │  │    Schema    │  │  Schema   │  │   │
│  │  │  Dispatcher  │◀─│   Registry    │  │   RefResolver│  │   Type    │  │   │
│  │  └──────┬───────┘  └───────────────┘  └──────────────┘  └───────────┘  │   │
│  │         │                                                               │   │
│  │  ┌──────┴──────────────────────────────────────────────────────────┐     │   │
│  │  │                                                                 │     │   │
│  │  ▼         VALIDATORS                                              │     │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐  │     │   │
│  │  │  Object      │  │    Array      │  │      Primitive          │  │     │   │
│  │  │  Validator   │  │   Validator  │  │      Validator          │  │     │   │
│  │  └──────────────┘  └──────────────┘  └──────────────────────────┘  │     │   │
│  │                                                                   │     │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐  │     │   │
│  │  │    OneOf     │  │      Not      │  │     Conditional         │  │     │   │
│  │  │  Validator   │  │   Validator  │  │     Validator          │  │     │   │
│  │  └──────────────┘  └──────────────┘  └──────────────────────────┘  │     │   │
│  │                                                                   │     │   │
│  │  ┌──────────────────────────────────────────────────────────┐     │     │   │
│  │  │                    FormatValidator                      │     │     │   │
│  │  └──────────────────────────────────────────────────────────┘     │     │   │
│  │                                                                   │     │   │
│  └───────────────────────────────────────────────────────────────────┘     │   │
│                                                                             │   │
│  ┌───────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │                        SCHEMA LAYER                                     │   │
│  │                                                                           │   │
│  │  ┌─────────────────────┐    ┌─────────────────────────────────────┐    │   │
│  │  │    Supported        │◀───│      FileSchemaLoader               │    │   │
│  │  │    Keywords         │    │                                     │    │   │
│  │  │    Registry        │    └──────────────────┬────────────────────┘    │   │
│  │  └─────────────────────┘                     │                         │   │
│  │                                              ▼                          │   │
│  │  ┌──────────────────────────────────────────────────────────────┐      │   │
│  │  │                      Schema                                  │      │   │
│  │  │  (Immutable data model containing all parsed constraints)   │      │   │
│  │  └──────────────────────────────────────────────────────────────┘      │   │
│  │                                                                           │   │
│  └───────────────────────────────────────────────────────────────────────────┘   │
│                                                                                  │
└──────────────────────────────────────────────────────────────────────────────────┘
```

### 6.2 SupportedKeywordsRegistry Integration

**How it Works:**

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│              SupportedKeywordsRegistry Integration Flow                           │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                  │
│  FileSchemaLoader                                                              │
│       │                                                                       │
│       ├── Constructor: Takes SupportedKeywordsRegistry as dependency            │
│       │                                                                       │
│       ▼                                                                       │
│  ┌─────────────────────────────────────────────────────┐                       │
│  │ detectUnsupportedKeywords(Map<String, Object> raw)  │                       │
│  │                                                     │                       │
│  │  1. Iterate through all keys in raw schema          │                       │
│  │  2. Skip keys starting with "$" (internal)         │                       │
│  │  3. Call registry.isKeywordSupported(key)          │                       │
│  │  4. If not supported:                              │                       │
│  │     • Fail-fast mode: Throw IllegalArgumentException│                      │
│  │     • Normal mode: Log warning                    │                       │
│  │  5. Recursively check nested schemas               │                       │
│  └─────────────────────────────────────────────────────┘                       │
│                                                                                  │
│  Benefits:                                                                     │
│  • Testability: Registry can be mocked/substituted                             │
│  • Extensibility: Easy to add new keywords                                      │
│  • Maintainability: Single source of truth for keywords                        │
│  • Observability: Metrics can be collected per keyword                          │
│                                                                                  │
└──────────────────────────────────────────────────────────────────────────────────┘
```

### 6.3 Extending with Custom Keywords

To add support for a new keyword:

1. **Add to SupportedKeywordsRegistry:**

```java
// In SupportedKeywordsRegistry.initializeSupportedKeywords()
keywords.add("propertyNames");  // New keyword
```

2. **Add to Schema model:**

```java
// In Schema class
private final Map<String, Schema> propertyNamesSchemas;
```

3. **Add parser in FileSchemaLoader:**

```java
// In toSchema() method
if (raw.containsKey("propertyNames") && raw.get("propertyNames") instanceof Map<?, ?> pnMap) {
    // Parse property name schemas
}
```

4. **Add validator in ObjectValidator:**

```java
// Validate property names against schema
```

---

## 7. Change History

### 7.1 Recent Changes (2026-03-19)

| Change | Description | Files Modified |
|--------|-------------|----------------|
| **SupportedKeywordsRegistry Addition** | Replaced static keyword checking with centralized registry | `SupportedKeywordsRegistry.java` (new) |
| **Fail-Fast Mode** | Added strict mode support for unsupported keywords | `FileSchemaLoader.java`, `PluginConfig.java`, `SchemaValidatorPlugin.java` |
| **Configuration Update** | Added `strict-mode` configuration option | `config.yml`, `PluginConfig.java` |
| **Error Message Improvement** | Fixed error message for strict mode | `FileSchemaLoader.java` |

### 7.2 Benefits of SupportedKeywordsRegistry

| Aspect | Before | After |
|--------|--------|-------|
| **Testability** | Hard-coded static lists | Injectable registry for mocking |
| **Maintainability** | Scattered keyword checks | Single source of truth |
| **Extensibility** | Modify multiple files | Add to registry only |
| **Observability** | No metrics | Per-keyword metrics possible |
| **Configuration** | No runtime control | Strict mode toggle |

### 7.3 Migration Notes

**Old Code:**
```java
// Direct keyword checking (deprecated)
if (schema.containsKey("propertyNames")) {
    // handle
}
```

**New Code:**
```java
// Registry-based checking (recommended)
if (registry.isKeywordSupported("propertyNames")) {
    // handle
}
```

---

## Appendix A: Exception Reference

| Exception | Throw Location | Cause |
|-----------|---------------|-------|
| `IOException` | FileSchemaLoader.load() | File not found, permission denied |
| `IllegalArgumentException` | FileSchemaLoader.load() | Unsupported extension or keyword (strict mode) |
| `IllegalStateException` | PluginContext.getPlugin() | Plugin not initialized |

---

## Appendix B: Test Coverage

Test files are located in `src/test/java/com/maiconjh/schemacr/schemes/`:

- `SupportedKeywordsRegistryTest.java` - 24 tests
- `FileSchemaLoaderTest.java` - 10 tests

Run tests with:
```bash
./gradlew test
```

---

*Document Version: 2.0*  
*Last Updated: 2026-03-19*  
*Maintained by: Schema-Validator Team*
