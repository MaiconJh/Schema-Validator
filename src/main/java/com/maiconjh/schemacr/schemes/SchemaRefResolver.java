package com.maiconjh.schemacr.schemes;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Resolves $ref references between schemas.
 * 
 * <p>Supports:
 * <ul>
 *     <li>Local references: #/definitions/schemaName</li>
 *     <li>External references: schemaName (resolved from registry)</li>
 * </ul>
 */
public class SchemaRefResolver {

    private final SchemaRegistry registry;
    private final Logger logger;
    private final Map<String, Schema> resolvedCache = new HashMap<>();

    public SchemaRefResolver(SchemaRegistry registry, Logger logger) {
        this.registry = registry;
        this.logger = logger;
    }

    /**
     * Resolves a $ref reference to a Schema.
     * 
     * @param ref the reference string (e.g., "#/definitions/Player", "Player")
     * @param currentSchemaName the name of the schema containing the reference
     * @return the resolved Schema, or null if not found
     */
    public Schema resolveRef(String ref, String currentSchemaName) {
        if (ref == null || ref.isEmpty()) {
            return null;
        }

        // Check cache first
        String cacheKey = currentSchemaName + "->" + ref;
        if (resolvedCache.containsKey(cacheKey)) {
            return resolvedCache.get(cacheKey);
        }

        Schema resolved = null;
        
        // Handle local references (JSON Pointer style)
        if (ref.startsWith("#/")) {
            resolved = resolveLocalRef(ref, currentSchemaName);
        } 
        // Handle external/registry references
        else {
            resolved = registry.getSchema(ref).orElse(null);
        }

        // Cache the result
        resolvedCache.put(cacheKey, resolved);
        
        if (resolved == null) {
            logger.warning("Failed to resolve $ref: " + ref + " in schema " + currentSchemaName);
        }
        
        return resolved;
    }

    /**
     * Resolves a local JSON Pointer reference.
     * 
     * @param ref the reference (e.g., "#/definitions/Player")
     * @param currentSchemaName the current schema name
     * @return the resolved Schema
     */
    private Schema resolveLocalRef(String ref, String currentSchemaName) {
        // Parse JSON Pointer: #/definitions/player -> ["definitions", "player"]
        String path = ref.substring(2); // Remove "#/"
        String[] parts = path.split("/");
        
        // Get the current schema as starting point
        Schema current = registry.getSchema(currentSchemaName).orElse(null);
        if (current == null) {
            return null;
        }

        // Navigate through the path
        Schema resolved = current;
        for (String part : parts) {
            if (resolved == null) {
                return null;
            }
            
            // Unescape JSON Pointer escape sequences
            part = unescapeJsonPointer(part);
            
            resolved = navigateTo(part, resolved);
        }
        
        return resolved;
    }

    /**
     * Navigates to a specific part of a schema.
     */
    private Schema navigateTo(String part, Schema current) {
        // Try properties
        if (current.getProperties() != null && current.getProperties().containsKey(part)) {
            return current.getProperties().get(part);
        }
        
        // Try item schema (for arrays)
        if ("items".equals(part) && current.getItemSchema() != null) {
            return current.getItemSchema();
        }
        
        return null;
    }

    /**
     * Unescapes JSON Pointer escape sequences.
     * (~0 -> ~, ~1 -> /)
     */
    private String unescapeJsonPointer(String value) {
        return value.replace("~1", "/")
                   .replace("~0", "~");
    }

    /**
     * Clears the resolution cache.
     */
    public void clearCache() {
        resolvedCache.clear();
    }

    /**
     * Checks if a reference can be resolved.
     */
    public boolean canResolve(String ref, String currentSchemaName) {
        return resolveRef(ref, currentSchemaName) != null;
    }
}
