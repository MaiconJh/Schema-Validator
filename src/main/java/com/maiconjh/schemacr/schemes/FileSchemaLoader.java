package com.maiconjh.schemacr.schemes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.logging.Logger;

/**
 * Loads schema definitions from JSON or YAML files.
 *
 * <p>Current parser supports a starter subset:</p>
 * <ul>
 *     <li>type: object/array/string/number/boolean/null/any</li>
 *     <li>properties for objects</li>
 *     <li>items for arrays</li>
 * </ul>
 * <p>Add new fields here to expand the schema language.</p>
 *
 * <p>Unsupported keyword detection is performed using {@link SupportedKeywordsRegistry}.</p>
 */
public class FileSchemaLoader {

    private final Logger logger;
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final Map<String, Schema> definitions = new HashMap<>();
    private final SupportedKeywordsRegistry keywordsRegistry;
    private boolean failFastMode = false;

    public FileSchemaLoader(Logger logger) {
        this(logger, new SupportedKeywordsRegistry(logger));
    }

    /**
     * Creates a FileSchemaLoader with a custom keywords registry.
     * @param logger the logger to use
     * @param keywordsRegistry the supported keywords registry
     */
    public FileSchemaLoader(Logger logger, SupportedKeywordsRegistry keywordsRegistry) {
        this.logger = logger;
        this.keywordsRegistry = keywordsRegistry;
    }

    /**
     * Enables or disables fail-fast mode.
     * When enabled, unsupported keywords will throw exceptions instead of warnings.
     * @param failFastMode true to enable fail-fast mode
     */
    public void setFailFastMode(boolean failFastMode) {
        this.failFastMode = failFastMode;
    }

    /**
     * Checks if fail-fast mode is enabled.
     * @return true if fail-fast mode is enabled
     */
    public boolean isFailFastMode() {
        return failFastMode;
    }

    public Schema load(Path path, String schemaName) throws IOException {
        String lower = path.getFileName().toString().toLowerCase();
        ObjectMapper mapper;
        if (lower.endsWith(".json")) {
            mapper = jsonMapper;
        } else if (lower.endsWith(".yml") || lower.endsWith(".yaml")) {
            mapper = yamlMapper;
        } else {
            throw new IllegalArgumentException("Unsupported schema extension: " + path);
        }

        if (!Files.exists(path)) {
            throw new IOException("Schema file does not exist: " + path);
        }

        Map<String, Object> raw = mapper.readValue(path.toFile(), Map.class);
        
        // Detect unsupported keywords
        detectUnsupportedKeywords(raw, schemaName);
        
        // First pass: extract definitions
        definitions.clear();
        if (raw.containsKey("definitions") && raw.get("definitions") instanceof Map<?, ?> defs) {
            for (Map.Entry<?, ?> entry : defs.entrySet()) {
                if (entry.getValue() instanceof Map<?, ?> defMap) {
                    String defName = String.valueOf(entry.getKey());
                    definitions.put(defName, toSchema(defName, castMap(defMap)));
                }
            }
        }
        
        Schema schema = toSchema(schemaName, raw);
        logger.info("Loaded schema '" + schemaName + "' from " + path);
        return schema;
    }

    /**
     * Parses a raw JSON/YAML map into a Schema.
     * Can be used for external schema loading.
     * 
     * @param schemaName name for the schema
     * @param raw raw JSON/YAML map
     * @return parsed Schema
     */
    public Schema parseSchema(String schemaName, Map<String, Object> raw) {
        // Detect unsupported keywords
        detectUnsupportedKeywords(raw, schemaName);
        
        // First pass: extract definitions
        definitions.clear();
        if (raw.containsKey("definitions") && raw.get("definitions") instanceof Map<?, ?> defs) {
            for (Map.Entry<?, ?> entry : defs.entrySet()) {
                if (entry.getValue() instanceof Map<?, ?> defMap) {
                    String defName = String.valueOf(entry.getKey());
                    definitions.put(defName, toSchema(defName, castMap(defMap)));
                }
            }
        }
        return toSchema(schemaName, raw);
    }

    /**
     * Gets a definition by name.
     * @param name the definition name
     * @return the Schema definition or null
     */
    public Schema getDefinition(String name) {
        return definitions.get(name);
    }

    private Schema toSchema(String name, Map<String, Object> raw) {
        SchemaType type = parseType(String.valueOf(raw.getOrDefault("type", "any")));

        Map<String, Schema> properties = new HashMap<>();
        Map<String, Schema> patternProperties = new HashMap<>();
        Schema itemSchema = null;
        List<String> requiredFields = new ArrayList<>();

        // Parse required fields
        if (raw.containsKey("required") && raw.get("required") instanceof List<?> requiredRaw) {
            for (Object item : requiredRaw) {
                requiredFields.add(String.valueOf(item));
            }
        }

        // Parse additionalProperties (default true)
        boolean additionalProps = true;
        if (raw.containsKey("additionalProperties")) {
            Object value = raw.get("additionalProperties");
            if (value instanceof Boolean) {
                additionalProps = (Boolean) value;
            }
        }

        // Parse numeric constraints
        Number minimum = null;
        Number maximum = null;
        Number multipleOf = null;
        boolean exclusiveMinimum = false;
        boolean exclusiveMaximum = false;
        
        if (raw.containsKey("minimum") && raw.get("minimum") instanceof Number min) {
            minimum = min;
        }
        if (raw.containsKey("maximum") && raw.get("maximum") instanceof Number max) {
            maximum = max;
        }
        if (raw.containsKey("multipleOf") && raw.get("multipleOf") instanceof Number mult) {
            multipleOf = mult;
        }
        if (raw.containsKey("exclusiveMinimum") && raw.get("exclusiveMinimum") instanceof Boolean exclMin) {
            exclusiveMinimum = exclMin;
        }
        if (raw.containsKey("exclusiveMaximum") && raw.get("exclusiveMaximum") instanceof Boolean exclMax) {
            exclusiveMaximum = exclMax;
        }

        // Parse string constraints
        Integer minLength = null;
        Integer maxLength = null;
        String pattern = null;
        String format = null;
        
        if (raw.containsKey("minLength") && raw.get("minLength") instanceof Number minLen) {
            minLength = minLen.intValue();
        }
        if (raw.containsKey("maxLength") && raw.get("maxLength") instanceof Number maxLen) {
            maxLength = maxLen.intValue();
        }
        if (raw.containsKey("pattern") && raw.get("pattern") instanceof String pat) {
            // Validate regex pattern
            try {
                Pattern.compile(pat);
                pattern = pat;
            } catch (PatternSyntaxException e) {
                logger.warning("Invalid regex pattern in schema '" + name + "': " + pat);
            }
        }
        if (raw.containsKey("format") && raw.get("format") instanceof String fmt) {
            format = fmt;
        }

        // Parse enum values
        List<Object> enumValues = new ArrayList<>();
        if (raw.containsKey("enum") && raw.get("enum") instanceof List<?> enumRaw) {
            enumValues.addAll(enumRaw);
        }

        // Parse $ref (schema reference)
        String ref = null;
        if (raw.containsKey("$ref") && raw.get("$ref") instanceof String refValue) {
            ref = refValue;
        }

        // Parse version and compatibility
        String version = null;
        String compatibility = null;
        if (raw.containsKey("version") && raw.get("version") instanceof String v) {
            version = v;
        }
        if (raw.containsKey("compatibility") && raw.get("compatibility") instanceof String c) {
            compatibility = c;
        }

        // Parse allOf composition
        List<Schema> allOfSchemas = new ArrayList<>();
        if (raw.containsKey("allOf") && raw.get("allOf") instanceof List<?> allOfRaw) {
            for (Object item : allOfRaw) {
                if (item instanceof Map<?, ?> allOfMap) {
                    allOfSchemas.add(toSchema(name + "_allOf_" + allOfSchemas.size(), castMap(allOfMap)));
                }
            }
        }

        // Parse anyOf composition
        List<Schema> anyOfSchemas = new ArrayList<>();
        if (raw.containsKey("anyOf") && raw.get("anyOf") instanceof List<?> anyOfRaw) {
            for (Object item : anyOfRaw) {
                if (item instanceof Map<?, ?> anyOfMap) {
                    anyOfSchemas.add(toSchema(name + "_anyOf_" + anyOfSchemas.size(), castMap(anyOfMap)));
                }
            }
        }

        // Parse oneOf composition
        List<Schema> oneOfSchemas = new ArrayList<>();
        if (raw.containsKey("oneOf") && raw.get("oneOf") instanceof List<?> oneOfRaw) {
            for (Object item : oneOfRaw) {
                if (item instanceof Map<?, ?> oneOfMap) {
                    oneOfSchemas.add(toSchema(name + "_oneOf_" + oneOfSchemas.size(), castMap(oneOfMap)));
                }
            }
        }

        // Parse not schema
        Schema notSchema = null;
        if (raw.containsKey("not") && raw.get("not") instanceof Map<?, ?> notMap) {
            notSchema = toSchema(name + "_not", castMap(notMap));
        }

        // Parse if/then/else schemas
        Schema ifSchema = null;
        Schema thenSchema = null;
        Schema elseSchema = null;
        if (raw.containsKey("if") && raw.get("if") instanceof Map<?, ?> ifMap) {
            ifSchema = toSchema(name + "_if", castMap(ifMap));
        }
        if (raw.containsKey("then") && raw.get("then") instanceof Map<?, ?> thenMap) {
            thenSchema = toSchema(name + "_then", castMap(thenMap));
        }
        if (raw.containsKey("else") && raw.get("else") instanceof Map<?, ?> elseMap) {
            elseSchema = toSchema(name + "_else", castMap(elseMap));
        }

        // Parse patternProperties
        if (type == SchemaType.OBJECT && raw.containsKey("patternProperties")) {
            Object pp = raw.get("patternProperties");
            if (pp instanceof Map<?, ?> ppMap) {
                for (Map.Entry<?, ?> entry : ppMap.entrySet()) {
                    if (entry.getValue() instanceof Map<?, ?> childMap) {
                        patternProperties.put(String.valueOf(entry.getKey()),
                                toSchema("pattern_" + String.valueOf(entry.getKey()), castMap(childMap)));
                    }
                }
            }
        }

        if (type == SchemaType.OBJECT && raw.containsKey("properties")) {
            Object props = raw.get("properties");
            if (props instanceof Map<?, ?> propsMap) {
                for (Map.Entry<?, ?> entry : propsMap.entrySet()) {
                    if (entry.getValue() instanceof Map<?, ?> childMap) {
                        properties.put(String.valueOf(entry.getKey()),
                                toSchema(String.valueOf(entry.getKey()), castMap(childMap)));
                    }
                }
            }
        }

        if (type == SchemaType.ARRAY && raw.get("items") instanceof Map<?, ?> itemsMap) {
            itemSchema = toSchema(name + "[]", castMap(itemsMap));
        }

        return new Schema(name, type, properties, patternProperties, itemSchema, requiredFields, additionalProps,
                         minimum, maximum, exclusiveMinimum, exclusiveMaximum,
                         minLength, maxLength, pattern, format, multipleOf, enumValues, ref, version, compatibility,
                         allOfSchemas, anyOfSchemas, oneOfSchemas, notSchema, ifSchema, thenSchema, elseSchema);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Map<?, ?> source) {
        return (Map<String, Object>) source;
    }

    /**
     * Detects and logs unsupported keywords in the raw schema map.
     * This method scans all keys in the schema and warns about any that are not supported.
     * In fail-fast mode, throws an exception for unsupported keywords.
     * 
     * @param raw the raw schema map
     * @param path path context for logging
     * @throws IllegalArgumentException if fail-fast mode is enabled and unsupported keyword is found
     */
    private void detectUnsupportedKeywords(Map<String, Object> raw, String path) {
        detectUnsupportedKeywords(raw, path, null);
    }
    
    /**
     * Internal method with set of valid custom property names from parent scope.
     * @param raw raw schema map
     * @param path path context for logging
     * @param validCustomProperties set of valid custom property names from parent properties/patternProperties
     */
    private void detectUnsupportedKeywords(Map<String, Object> raw, String path, java.util.Set<String> validCustomProperties) {
        if (raw == null || keywordsRegistry == null) {
            return;
        }
        
        // Collect valid custom properties from properties/patternProperties at current level
        java.util.Set<String> currentValidProperties = new java.util.HashSet<>();
        
        // Add valid properties from "properties" keyword
        Object propertiesObj = raw.get("properties");
        if (propertiesObj instanceof Map<?, ?> propertiesMap) {
            for (Object key : propertiesMap.keySet()) {
                if (key instanceof String keyStr) {
                    currentValidProperties.add(keyStr);
                }
            }
        }
        
        // Add valid properties from "patternProperties" keyword
        Object patternPropertiesObj = raw.get("patternProperties");
        if (patternPropertiesObj instanceof Map<?, ?> patternPropertiesMap) {
            for (Object key : patternPropertiesMap.keySet()) {
                if (key instanceof String keyStr) {
                    currentValidProperties.add(keyStr);
                }
            }
        }
        
        // Merge with parent valid properties
        java.util.Set<String> allValidProperties = new java.util.HashSet<>();
        if (validCustomProperties != null) {
            allValidProperties.addAll(validCustomProperties);
        }
        allValidProperties.addAll(currentValidProperties);
        
        for (String key : raw.keySet()) {
            // Skip internal/structural keys
            if (key.startsWith("$")) {
                continue;
            }
            
            // Skip if this is a valid custom property defined in properties/patternProperties
            // or if it's a standard JSON Schema keyword
            if (allValidProperties.contains(key) || keywordsRegistry.isKeywordSupported(key)) {
                continue;
            }
            
            String message = "[" + path + "] Unsupported keyword detected: '" + key + 
                        "'. This keyword will be ignored during validation.";
            
            if (failFastMode) {
                throw new IllegalArgumentException("FAIL-FAST: " + message + " Set strict-mode: false in config to allow.");
            } else {
                logger.warning(message);
            }
        }
        
        // Recursively check nested schemas, passing current valid properties
        for (Object value : raw.values()) {
            if (value instanceof Map<?, ?> nestedMap) {
                detectUnsupportedKeywords((Map<String, Object>) nestedMap, path + ".nested", allValidProperties);
            } else if (value instanceof List<?> list) {
                for (Object item : list) {
                    if (item instanceof Map<?, ?> itemMap) {
                        detectUnsupportedKeywords((Map<String, Object>) itemMap, path + ".item", allValidProperties);
                    }
                }
            }
        }
    }

    /**
     * Gets the supported keywords registry.
     * @return the keywords registry
     */
    public SupportedKeywordsRegistry getKeywordsRegistry() {
        return keywordsRegistry;
    }

    private SchemaType parseType(String input) {
        return switch (input.toLowerCase()) {
            case "object" -> SchemaType.OBJECT;
            case "array" -> SchemaType.ARRAY;
            case "string" -> SchemaType.STRING;
            case "integer" -> SchemaType.INTEGER;
            case "number" -> SchemaType.NUMBER;
            case "boolean" -> SchemaType.BOOLEAN;
            case "null" -> SchemaType.NULL;
            default -> SchemaType.ANY;
        };
    }
}
