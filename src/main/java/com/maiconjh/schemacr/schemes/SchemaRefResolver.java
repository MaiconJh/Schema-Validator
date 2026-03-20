package com.maiconjh.schemacr.schemes;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Advanced resolver for $ref references between schemas.
 * 
 * <p>Supports multiple reference formats:
 * <ul>
 *     <li>Local references: #/definitions/SchemaName</li>
 *     <li>Relative paths: schemas/address.schema.json#/definitions/Address</li>
 *     <li>Registry references: schemaName (resolved from SchemaRegistry)</li>
 *     <li>URL references: https://example.com/schemas/player.json</li>
 * </ul>
 * 
 * <p>Features:
 * <ul>
 *     <li>Circular reference detection</li>
 *     <li>External schema caching</li>
 *     <li>Lazy loading of external schemas</li>
 *     <li>Automatic format detection</li>
 * </ul>
 */
public class SchemaRefResolver {

    // Pattern for relative paths: path/to/file.json#json/pointer
    private static final Pattern RELATIVE_PATH_PATTERN = Pattern.compile(
        "^([\\w/\\\\.-]+)?(#/.*)?$"
    );

    // Pattern for URL references
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^(https?://.*)$"
    );

    private final SchemaRegistry registry;
    private final Logger logger;
    private final Map<String, Schema> resolvedCache = new HashMap<>();
    private final Map<String, Schema> externalSchemaCache = new ConcurrentHashMap<>();
    private final Set<String> resolving = ConcurrentHashMap.newKeySet();
    private final Path schemaBaseDirectory;
    private final HttpClient httpClient;
    private final boolean cacheExternalSchemas;
    private final long cacheExpiryMs;

    /**
     * Creates a SchemaRefResolver with default settings.
     * 
     * @param registry the schema registry
     * @param logger logger for reporting
     */
    public SchemaRefResolver(SchemaRegistry registry, Logger logger) {
        this(registry, logger, null, true, 5 * 60 * 1000); // 5 min default
    }

    /**
     * Creates a SchemaRefResolver with custom configuration.
     * 
     * @param registry the schema registry
     * @param logger logger for reporting
     * @param schemaBaseDirectory base directory for relative paths
     * @param cacheExternalSchemas whether to cache external schemas
     * @param cacheExpiryMs cache expiry time in milliseconds
     */
    public SchemaRefResolver(SchemaRegistry registry, Logger logger, 
                             Path schemaBaseDirectory, boolean cacheExternalSchemas,
                             long cacheExpiryMs) {
        this.registry = registry;
        this.logger = logger;
        this.schemaBaseDirectory = schemaBaseDirectory;
        this.cacheExternalSchemas = cacheExternalSchemas;
        this.cacheExpiryMs = cacheExpiryMs;
        
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    }

    /**
     * Resolves a $ref reference to a Schema.
     * 
     * @param ref the reference string
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

        // Check if already being resolved (circular reference)
        if (resolving.contains(cacheKey)) {
            logger.warning("Circular reference detected: " + ref + " in " + currentSchemaName);
            return null;
        }

        Schema resolved = null;
        
        try {
            // Mark as being resolved
            resolving.add(cacheKey);

            // Detect and handle different reference formats
            if (ref.startsWith("#/")) {
                // Local reference: #/definitions/Player
                resolved = resolveLocalRef(ref, currentSchemaName);
            } else if (ref.contains("#")) {
                // External reference with JSON pointer: schemas/player.json#/definitions/Player
                resolved = resolveExternalRefWithPointer(ref, currentSchemaName);
            } else if (URL_PATTERN.matcher(ref).matches()) {
                // URL reference: https://example.com/schema.json
                resolved = resolveUrlRef(ref, null);
            } else {
                // Registry reference: SchemaName
                resolved = registry.getSchema(ref).orElse(null);
            }
        } finally {
            resolving.remove(cacheKey);
        }

        // Cache the result
        resolvedCache.put(cacheKey, resolved);
        
        if (resolved == null) {
            logger.warning("Failed to resolve $ref: " + ref + " in schema " + currentSchemaName);
        }
        
        return resolved;
    }

    /**
     * Resolves an external reference with JSON pointer.
     * Format: path/to/schema.json#json/pointer
     */
    private Schema resolveExternalRefWithPointer(String ref, String currentSchemaName) {
        String[] parts = ref.split("#", 2);
        String externalPath = parts[0];
        String jsonPointer = parts.length > 1 ? parts[1] : null;

        // Try relative path first
        Schema externalSchema = loadExternalSchema(externalPath, currentSchemaName);
        
        if (externalSchema != null && jsonPointer != null) {
            // Resolve the JSON pointer within the external schema
            return resolveJsonPointer(externalSchema, jsonPointer);
        }
        
        return externalSchema;
    }

    /**
     * Loads an external schema from path or URL.
     */
    private Schema loadExternalSchema(String pathOrUrl, String currentSchemaName) {
        // Check cache
        if (externalSchemaCache.containsKey(pathOrUrl)) {
            return externalSchemaCache.get(pathOrUrl);
        }

        Schema schema = null;

        // Try as relative path
        if (schemaBaseDirectory != null) {
            Path fullPath = schemaBaseDirectory.resolve(pathOrUrl).normalize();
            if (Files.exists(fullPath)) {
                try {
                    FileSchemaLoader loader = new FileSchemaLoader(logger);
                    String schemaName = extractSchemaName(pathOrUrl);
                    schema = loader.load(fullPath, schemaName);
                    logger.info("Loaded external schema from relative path: " + pathOrUrl);
                } catch (IOException e) {
                    logger.warning("Failed to load schema from " + pathOrUrl + ": " + e.getMessage());
                }
            }
        }

        // Try as URL if not found
        if (schema == null && URL_PATTERN.matcher(pathOrUrl).matches()) {
            schema = resolveUrlRef(pathOrUrl, null);
        }

        // Cache if enabled
        if (schema != null && cacheExternalSchemas) {
            externalSchemaCache.put(pathOrUrl, schema);
        }

        return schema;
    }

    /**
     * Resolves a URL reference.
     */
    private Schema resolveUrlRef(String url, String jsonPointer) {
        String cacheKey = url + (jsonPointer != null ? "#" + jsonPointer : "");
        
        if (externalSchemaCache.containsKey(cacheKey)) {
            Schema cached = externalSchemaCache.get(cacheKey);
            if (cached != null) {
                return cached;
            }
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Parse the JSON response
                com.fasterxml.jackson.databind.ObjectMapper mapper = 
                    new com.fasterxml.jackson.databind.ObjectMapper();
                java.util.Map<String, Object> raw = mapper.readValue(
                    response.body(), java.util.Map.class);
                
                FileSchemaLoader loader = new FileSchemaLoader(logger);
                String schemaName = extractSchemaName(url);
                Schema schema = loader.parseSchema(schemaName, raw);
                
                if (jsonPointer != null) {
                    schema = resolveJsonPointer(schema, jsonPointer);
                }

                if (cacheExternalSchemas) {
                    externalSchemaCache.put(cacheKey, schema);
                }
                
                logger.info("Loaded schema from URL: " + url);
                return schema;
            } else {
                logger.warning("Failed to fetch schema from " + url + 
                    ": HTTP " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            logger.warning("Error fetching schema from " + url + ": " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        return null;
    }

    /**
     * Resolves a JSON pointer within a schema.
     */
    private Schema resolveJsonPointer(Schema root, String jsonPointer) {
        if (!jsonPointer.startsWith("#/")) {
            return null;
        }

        String path = jsonPointer.substring(2);
        String[] parts = path.split("/");
        
        Schema current = root;
        for (String part : parts) {
            if (current == null) return null;
            
            // Unescape JSON Pointer
            part = part.replace("~1", "/").replace("~0", "~");
            
            current = navigateTo(part, current);
        }
        
        return current;
    }

    /**
     * Resolves a local JSON Pointer reference.
     */
    private Schema resolveLocalRef(String ref, String currentSchemaName) {
        String path = ref.substring(2); // Remove "#/"
        String[] parts = path.split("/");
        
        Schema current = registry.getSchema(currentSchemaName).orElse(null);
        if (current == null) {
            return null;
        }

        Schema resolved = current;
        for (String part : parts) {
            if (resolved == null) {
                return null;
            }
            part = part.replace("~1", "/").replace("~0", "~");
            resolved = navigateTo(part, resolved);
        }
        
        return resolved;
    }

    /**
     * Navigates to a specific part of a schema.
     */
    private Schema navigateTo(String part, Schema current) {
        if (current.getProperties() != null && current.getProperties().containsKey(part)) {
            return current.getProperties().get(part);
        }
        
        if ("items".equals(part) && current.getItemSchema() != null) {
            return current.getItemSchema();
        }
        
        return null;
    }

    /**
     * Extracts schema name from path or URL.
     */
    private String extractSchemaName(String pathOrUrl) {
        String name = pathOrUrl;
        
        // Remove query params
        int queryIdx = name.indexOf('?');
        if (queryIdx > 0) {
            name = name.substring(0, queryIdx);
        }
        
        // Get filename
        int lastSlash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (lastSlash >= 0) {
            name = name.substring(lastSlash + 1);
        }
        
        // Remove extension
        int dotIdx = name.lastIndexOf('.');
        if (dotIdx > 0) {
            name = name.substring(0, dotIdx);
        }
        
        return name;
    }

    /**
     * Clears all caches.
     */
    public void clearCache() {
        resolvedCache.clear();
        externalSchemaCache.clear();
    }

    /**
     * Clears only external schema cache.
     */
    public void clearExternalCache() {
        externalSchemaCache.clear();
    }

    /**
     * Checks if a reference can be resolved.
     */
    public boolean canResolve(String ref, String currentSchemaName) {
        return resolveRef(ref, currentSchemaName) != null;
    }

    /**
     * Preloads an external schema.
     */
    public void preloadSchema(String pathOrUrl) {
        loadExternalSchema(pathOrUrl, null);
    }

    /**
     * Returns the number of cached external schemas.
     */
    public int getExternalCacheSize() {
        return externalSchemaCache.size();
    }
}
