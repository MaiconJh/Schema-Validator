package com.yourname.schemacr.schemes;

import java.io.IOException;
import java.nio.file.Path;

/**
 * High-level API to register and retrieve schemas.
 */
public class SchemaRegistrationService {

    private final SchemaRegistry registry;
    private final FileSchemaLoader loader;

    public SchemaRegistrationService(SchemaRegistry registry, FileSchemaLoader loader) {
        this.registry = registry;
        this.loader = loader;
    }

    public Schema registerFromFile(String schemaName, Path schemaFile) throws IOException {
        Schema schema = loader.load(schemaFile, schemaName);
        registry.registerSchema(schemaName, schema);
        return schema;
    }

    public Schema getRequired(String schemaName) {
        return registry.getSchema(schemaName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown schema: " + schemaName));
    }
}
