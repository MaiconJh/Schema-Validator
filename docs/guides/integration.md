# Integration Guide

> This page is non-normative. Canonical behavior is in [`../CONTRACT.md`](../CONTRACT.md).

## Java integration (current API)

### Load and register a schema from file

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

### Validate data (default path)

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

### Validate data with `$ref` resolution enabled

```java
import com.maiconjh.schemacr.schemes.SchemaRefResolver;

SchemaRefResolver resolver = new SchemaRefResolver(registry, Logger.getLogger("SchemaValidator"));
ValidationService serviceWithRefs = new ValidationService(resolver);
ValidationResult result = serviceWithRefs.validate(data, schema);
```

## Skript integration

Implemented syntax:

```skript
validate yaml "path/to/data.yml" using schema "path/to/schema.json"
validate json "path/to/data.json" using schema "path/to/schema.json"
set {_errors::*} to last schema validation errors
```

Not implemented:

- `last schema validation result`
- `last validation errors`

## Practical note about root type

`ValidationService()` uses `ObjectValidator` at root.

If root data is not object-like, validation fails in the default path. See [`../CONTRACT.md`](../CONTRACT.md) for details.
