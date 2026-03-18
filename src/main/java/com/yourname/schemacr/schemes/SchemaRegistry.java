package com.yourname.schemacr.schemes;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory schema registry.
 *
 * <p>Use this as the central lookup point for all validation operations.</p>
 */
public class SchemaRegistry {

    private final Map<String, Schema> schemasByName = new ConcurrentHashMap<>();

    public void registerSchema(String name, Schema schema) {
        schemasByName.put(name.toLowerCase(), schema);
    }

    public Optional<Schema> getSchema(String name) {
        return Optional.ofNullable(schemasByName.get(name.toLowerCase()));
    }

    public boolean contains(String name) {
        return schemasByName.containsKey(name.toLowerCase());
    }
}
