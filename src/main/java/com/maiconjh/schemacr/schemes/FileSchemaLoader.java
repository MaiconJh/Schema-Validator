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
 */
public class FileSchemaLoader {

    private final Logger logger;
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public FileSchemaLoader(Logger logger) {
        this.logger = logger;
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
        Schema schema = toSchema(schemaName, raw);
        logger.info("Loaded schema '" + schemaName + "' from " + path);
        return schema;
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
        boolean exclusiveMinimum = false;
        boolean exclusiveMaximum = false;
        
        if (raw.containsKey("minimum") && raw.get("minimum") instanceof Number min) {
            minimum = min;
        }
        if (raw.containsKey("maximum") && raw.get("maximum") instanceof Number max) {
            maximum = max;
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
                         minLength, maxLength, pattern, enumValues, ref, version, compatibility);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Map<?, ?> source) {
        return (Map<String, Object>) source;
    }

    private SchemaType parseType(String input) {
        return switch (input.toLowerCase()) {
            case "object" -> SchemaType.OBJECT;
            case "array" -> SchemaType.ARRAY;
            case "string" -> SchemaType.STRING;
            case "number", "integer" -> SchemaType.NUMBER;
            case "boolean" -> SchemaType.BOOLEAN;
            case "null" -> SchemaType.NULL;
            default -> SchemaType.ANY;
        };
    }
}
