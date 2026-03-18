package com.yourname.schemacr.schemes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Loads schema definitions from JSON or YAML files.
 *
 * <p>Current parser supports a starter subset:
 * <ul>
 *     <li>type: object/array/string/number/boolean/null/any</li>
 *     <li>properties for objects</li>
 *     <li>items for arrays</li>
 * </ul>
 * Add new fields here to expand the schema language.</p>
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
        Schema itemSchema = null;

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

        return new Schema(name, type, properties, itemSchema);
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
