# API Reference (Implementation-Aligned)

> Normative behavior is in [`CONTRACT.md`](CONTRACT.md).

## FileSchemaLoader

```java
public FileSchemaLoader(java.util.logging.Logger logger)
public Schema load(java.nio.file.Path path, String schemaName) throws IOException
public Schema parseSchema(String schemaName, Map<String, Object> raw)
public Schema getDefinition(String name)
```

## SchemaRegistrationService

```java
public SchemaRegistrationService(SchemaRegistry registry, FileSchemaLoader loader)
public Schema registerFromFile(String schemaName, Path schemaFile) throws IOException
public Schema getRequired(String schemaName)
```

## ValidationService

```java
public ValidationService()
public ValidationService(SchemaRefResolver refResolver)
public ValidationResult validate(Object data, Schema schema)
public List<ValidationResult> validateBatch(List<Object> dataList, Schema schema)
public boolean validateAll(List<Object> dataList, Schema schema)
public int getFailedCount(List<Object> dataList, Schema schema)
```

### Resolver behavior

- `ValidationService()` does not enable `$ref` resolution.
- `ValidationService(refResolver)` enables `$ref` resolution through `ObjectValidator`.

## Skript registration

Registered syntax:

```text
validate yaml %string% using schema %string%
validate json %string% using schema %string%
last schema validation errors
```

## Notes

- Historical method names like `loadSchema(...)` and `loadSchemasFromDirectory(...)` are not part of current `FileSchemaLoader` API.
