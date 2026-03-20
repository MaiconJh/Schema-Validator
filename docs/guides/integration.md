# Integration Guide

> This page is non-normative. Canonical behavior is in [`../CONTRACT.md`](../CONTRACT.md).

This guide covers all integration methods: Java API, Skript, and Configuration.

---

## Configuration

### Config File Location

```
plugins/Schema-Validator/config.yml
```

### Supported Keys

```yaml
schema-directory: "schemas"
auto-load: true
cache-enabled: true
validation-on-load: true
```

#### Key Details

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `schema-directory` | string | `"schemas"` | Directory for schema files (`.json`, `.yml`, `.yaml`) |
| `auto-load` | boolean | `true` | If enabled, plugin loads schemas from `schema-directory` at startup |
| `cache-enabled` | boolean | `true` | Enables schema cache in `SchemaRegistry` |
| `validation-on-load` | boolean | `true` | Validates loaded schemas with minimal generated sample data |

### Example Folder Layout

```text
plugins/
└── Schema-Validator/
    ├── config.yml
    └── schemas/
        ├── player-profile.schema.json
        └── custom-block-schema.json
```

### Not Supported Keys

These keys are not read by runtime code:

- `settings.cache-expiry`
- `settings.schemas-folder`
- `settings.examples-folder`
- any nested `settings.*` contract

---

## Java Integration

### Load and Register a Schema from File

```java
import com.maiconjh.schemacr.schemes.FileSchemaLoader;
import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaRegistry;

import java.nio.file.Path;
import java.util.logging.Logger;

SchemaRegistry registry = new SchemaRegistry();
FileSchemaLoader loader = new FileSchemaLoader(Logger.getLogger("SchemaValidator"));

Path schemaPath = Path.of("schemas/player-profile.schema.json");
Schema schema = loader.load(schemaPath, "player-profile");
registry.registerSchema("player-profile", schema);
```

### Validate Data (Default Path)

```java
import com.maiconjh.schemacr.core.ValidationService;
import com.maiconjh.schemacr.validation.ValidationResult;

Object data = /* parsed YAML/JSON object graph */;
ValidationService service = new ValidationService();
ValidationResult result = service.validate(data, schema);

if (result.isSuccess()) {
    System.out.println("Validation passed");
} else {
    result.getErrors().forEach(System.out::println);
}
```

### Validate Data with `$ref` Resolution Enabled

```java
import com.maiconjh.schemacr.schemes.SchemaRefResolver;

SchemaRefResolver resolver = new SchemaRefResolver(registry, Logger.getLogger("SchemaValidator"));
ValidationService serviceWithRefs = new ValidationService(resolver);
ValidationResult result = serviceWithRefs.validate(data, schema);
```

---

## Skript Integration

### Implemented Syntax

```skript
validate yaml "path/to/data.yml" using schema "path/to/schema.json"
validate json "path/to/data.json" using schema "path/to/schema.json"
set {_errors::*} to last schema validation errors
```

### Not Implemented

- `last schema validation result`
- `last validation errors`

---

## Practical Notes

### Root Type

`ValidationService()` uses `ObjectValidator` at root.

If root data is not object-like, validation fails in the default path. See [`../CONTRACT.md`](../CONTRACT.md) for details.

---

*Last Updated: 2026-03-20*
*This document consolidates content from configuration.md*
