package com.maiconjh.schemacr.schemes;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory schema registry with caching support.
 *
 * <p>Use this as the central lookup point for all validation operations.
 * Supports caching with configurable expiry.</p>
 */
public class SchemaRegistry {

    private final Map<String, Schema> schemasByName = new ConcurrentHashMap<>();
    private final Map<String, Long> cacheTimestamps = new ConcurrentHashMap<>();
    private final Map<String, RegisteredSchemaMetadata> metadataByName = new ConcurrentHashMap<>();
    private boolean cacheEnabled = true;
    private long cacheExpiryMs = 5 * 60 * 1000; // 5 minutes default

    public SchemaRegistry() {
    }

    public SchemaRegistry(boolean cacheEnabled, long cacheExpiryMs) {
        this.cacheEnabled = cacheEnabled;
        this.cacheExpiryMs = cacheExpiryMs;
    }

    public void registerSchema(String name, Schema schema) {
        registerSchema(name, schema, SchemaRegistrationSource.UNKNOWN, null);
    }

    public void registerSchema(String name,
                               Schema schema,
                               SchemaRegistrationSource source,
                               java.nio.file.Path sourcePath) {
        schemasByName.put(name.toLowerCase(), schema);
        cacheTimestamps.put(name.toLowerCase(), System.currentTimeMillis());
        metadataByName.put(name.toLowerCase(), new RegisteredSchemaMetadata(
                name,
                source,
                sourcePath == null ? null : sourcePath.toAbsolutePath().normalize(),
                System.currentTimeMillis()
        ));
    }

    public Optional<Schema> getSchema(String name) {
        String key = name.toLowerCase();
        
        if (!cacheEnabled) {
            return Optional.ofNullable(schemasByName.get(key));
        }
        
        Long timestamp = cacheTimestamps.get(key);
        if (timestamp != null && System.currentTimeMillis() - timestamp > cacheExpiryMs) {
            // Cache expired, remove entry
            schemasByName.remove(key);
            cacheTimestamps.remove(key);
            metadataByName.remove(key);
            return Optional.empty();
        }
        
        return Optional.ofNullable(schemasByName.get(key));
    }

    public boolean contains(String name) {
        return schemasByName.containsKey(name.toLowerCase());
    }

    public Optional<RegisteredSchemaMetadata> getSchemaMetadata(String name) {
        return Optional.ofNullable(metadataByName.get(name.toLowerCase()));
    }

    /**
     * Clears all cached schemas.
     */
    public void clearCache() {
        schemasByName.clear();
        cacheTimestamps.clear();
        metadataByName.clear();
    }

    /**
     * Returns the number of registered schemas.
     */
    public int getSchemaCount() {
        return schemasByName.size();
    }

    /**
     * Enables or disables caching.
     */
    public void setCacheEnabled(boolean enabled) {
        this.cacheEnabled = enabled;
    }

    /**
     * Returns whether caching is enabled.
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    /**
     * Returns all registered schema names.
     * @return set of schema names
     */
    public Set<String> getAllSchemaNames() {
        return schemasByName.keySet();
    }

    public java.util.Collection<RegisteredSchemaMetadata> getAllSchemaMetadata() {
        return java.util.List.copyOf(metadataByName.values());
    }
}
