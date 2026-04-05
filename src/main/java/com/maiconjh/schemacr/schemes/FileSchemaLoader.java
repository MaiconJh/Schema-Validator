package com.maiconjh.schemacr.schemes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

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

    public FileSchemaLoader(Logger logger, SupportedKeywordsRegistry keywordsRegistry) {
        this.logger = logger;
        this.keywordsRegistry = keywordsRegistry;
    }

    public void setFailFastMode(boolean failFastMode) { this.failFastMode = failFastMode; }
    public boolean isFailFastMode() { return failFastMode; }

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
        detectUnsupportedKeywords(raw, schemaName);

        definitions.clear();
        if (raw.containsKey("definitions") && raw.get("definitions") instanceof Map<?, ?> defs) {
            for (Map.Entry<?, ?> entry : defs.entrySet()) {
                if (entry.getValue() instanceof Map<?, ?> defMap) {
                    String defName = String.valueOf(entry.getKey());
                    definitions.put(defName, toSchema(defName, castMap(defMap)));
                }
            }
        }
        if (raw.containsKey("$defs") && raw.get("$defs") instanceof Map<?, ?> defs) {
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

    public Schema parseSchema(String schemaName, Map<String, Object> raw) {
        detectUnsupportedKeywords(raw, schemaName);
        definitions.clear();
        if (raw.containsKey("definitions") && raw.get("definitions") instanceof Map<?, ?> defs) {
            for (Map.Entry<?, ?> entry : defs.entrySet()) {
                if (entry.getValue() instanceof Map<?, ?> defMap) {
                    String defName = String.valueOf(entry.getKey());
                    definitions.put(defName, toSchema(defName, castMap(defMap)));
                }
            }
        }
        if (raw.containsKey("$defs") && raw.get("$defs") instanceof Map<?, ?> defs) {
            for (Map.Entry<?, ?> entry : defs.entrySet()) {
                if (entry.getValue() instanceof Map<?, ?> defMap) {
                    String defName = String.valueOf(entry.getKey());
                    definitions.put(defName, toSchema(defName, castMap(defMap)));
                }
            }
        }
        return toSchema(schemaName, raw);
    }

    public Schema getDefinition(String name) { return definitions.get(name); }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Map<?, ?> source) { return (Map<String, Object>) source; }

    private void detectUnsupportedKeywords(Map<String, Object> raw, String path) {
        if (raw == null || keywordsRegistry == null) return;

        for (Map.Entry<String, Object> entry : raw.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (!keywordsRegistry.isKeywordSupported(key)) {
                String message = "[" + path + "] Unsupported keyword detected: '" + key +
                        "'. This keyword will be ignored during validation.";
                if (failFastMode) {
                    throw new IllegalArgumentException("FAIL-FAST: " + message);
                } else {
                    logger.warning(message);
                }
            }

            if (isKeywordMapWithCustomKeys(key) && value instanceof Map<?, ?> customKeyMap) {
                for (Map.Entry<?, ?> customEntry : customKeyMap.entrySet()) {
                    if (customEntry.getValue() instanceof Map<?, ?> nestedSchemaMap) {
                        detectUnsupportedKeywords(castMap(nestedSchemaMap), path + "." + key + "." + customEntry.getKey());
                    }
                }
                continue;
            }

            if (value instanceof Map<?, ?> nestedMap) {
                detectUnsupportedKeywords(castMap(nestedMap), path + "." + key);
            } else if (value instanceof List<?> list) {
                for (int i = 0; i < list.size(); i++) {
                    Object item = list.get(i);
                    if (item instanceof Map<?, ?> itemMap) {
                        detectUnsupportedKeywords(castMap(itemMap), path + "." + key + "[" + i + "]");
                    }
                }
            }
        }
    }

    private boolean isKeywordMapWithCustomKeys(String keyword) {
        return "properties".equals(keyword)
                || "patternProperties".equals(keyword)
                || "definitions".equals(keyword)
                || "$defs".equals(keyword)
                || "dependentSchemas".equals(keyword);
    }

    private Schema toSchema(String name, Map<String, Object> raw) {
        Schema.Builder builder = Schema.builder(name, parseType(String.valueOf(raw.getOrDefault("type", "any"))));

        // required fields
        if (raw.containsKey("required") && raw.get("required") instanceof List<?> requiredRaw) {
            List<String> required = new ArrayList<>();
            for (Object item : requiredRaw) required.add(String.valueOf(item));
            builder.requiredFields(required);
        }

        // additionalProperties
        if (raw.containsKey("additionalProperties")) {
            Object value = raw.get("additionalProperties");
            if (value instanceof Boolean b) {
                builder.additionalProperties(b);
            } else if (value instanceof Map<?, ?> map) {
                builder.additionalProperties(toSchema(name + "_additionalProperties", castMap(map)));
            }
        }

        // numeric
        if (raw.containsKey("minimum") && raw.get("minimum") instanceof Number min) builder.minimum(min);
        if (raw.containsKey("maximum") && raw.get("maximum") instanceof Number max) builder.maximum(max);
        if (raw.containsKey("multipleOf") && raw.get("multipleOf") instanceof Number mult) builder.multipleOf(mult);
        if (raw.containsKey("exclusiveMinimum") && raw.get("exclusiveMinimum") instanceof Boolean exclMin) builder.exclusiveMinimum(exclMin);
        if (raw.containsKey("exclusiveMaximum") && raw.get("exclusiveMaximum") instanceof Boolean exclMax) builder.exclusiveMaximum(exclMax);

        // string
        if (raw.containsKey("minLength") && raw.get("minLength") instanceof Number minLen) builder.minLength(minLen.intValue());
        if (raw.containsKey("maxLength") && raw.get("maxLength") instanceof Number maxLen) builder.maxLength(maxLen.intValue());
        if (raw.containsKey("pattern") && raw.get("pattern") instanceof String pat) {
            try {
                Pattern.compile(pat);
                builder.pattern(pat);
            } catch (PatternSyntaxException e) {
                logger.warning("Invalid regex pattern in schema '" + name + "': " + pat);
            }
        }
        if (raw.containsKey("format") && raw.get("format") instanceof String fmt) builder.format(fmt);

        // enum
        if (raw.containsKey("enum") && raw.get("enum") instanceof List<?> enumRaw) {
            builder.enumValues(new ArrayList<>(enumRaw));
        }

        // metadata
        if (raw.containsKey("$schema") && raw.get("$schema") instanceof String schemaDialect) builder.schemaDialect(schemaDialect);
        if (raw.containsKey("$id") && raw.get("$id") instanceof String id) builder.id(id);
        if (raw.containsKey("title") && raw.get("title") instanceof String title) builder.title(title);
        if (raw.containsKey("description") && raw.get("description") instanceof String description) builder.description(description);
        if (raw.containsKey("version") && raw.get("version") instanceof String v) builder.version(v);
        if (raw.containsKey("compatibility") && raw.get("compatibility") instanceof String c) builder.compatibility(c);

        // type as array
        if (raw.containsKey("type") && raw.get("type") instanceof List<?> typeRaw) {
            List<String> typeList = new ArrayList<>();
            for (Object item : typeRaw) typeList.add(String.valueOf(item));
            builder.typeList(typeList);
        }

        // $ref
        if (raw.containsKey("$ref") && raw.get("$ref") instanceof String ref) builder.ref(ref);
        if (raw.containsKey("$dynamicRef") && raw.get("$dynamicRef") instanceof String dynamicRef) builder.dynamicRef(dynamicRef);
        if (raw.containsKey("$dynamicAnchor") && raw.get("$dynamicAnchor") instanceof String dynamicAnchor) builder.dynamicAnchor(dynamicAnchor);

        // composition
        if (raw.containsKey("allOf") && raw.get("allOf") instanceof List<?> allOfRaw) {
            List<Schema> allOfSchemas = new ArrayList<>();
            for (Object item : allOfRaw) {
                if (item instanceof Map<?, ?> map) allOfSchemas.add(toSchema(name + "_allOf_" + allOfSchemas.size(), castMap(map)));
            }
            builder.allOf(allOfSchemas);
        }
        if (raw.containsKey("anyOf") && raw.get("anyOf") instanceof List<?> anyOfRaw) {
            List<Schema> anyOfSchemas = new ArrayList<>();
            for (Object item : anyOfRaw) {
                if (item instanceof Map<?, ?> map) anyOfSchemas.add(toSchema(name + "_anyOf_" + anyOfSchemas.size(), castMap(map)));
            }
            builder.anyOf(anyOfSchemas);
        }
        if (raw.containsKey("oneOf") && raw.get("oneOf") instanceof List<?> oneOfRaw) {
            List<Schema> oneOfSchemas = new ArrayList<>();
            for (Object item : oneOfRaw) {
                if (item instanceof Map<?, ?> map) oneOfSchemas.add(toSchema(name + "_oneOf_" + oneOfSchemas.size(), castMap(map)));
            }
            builder.oneOf(oneOfSchemas);
        }
        if (raw.containsKey("not") && raw.get("not") instanceof Map<?, ?> notMap) {
            builder.notSchema(toSchema(name + "_not", castMap(notMap)));
        }
        if (raw.containsKey("if") && raw.get("if") instanceof Map<?, ?> ifMap) builder.ifSchema(toSchema(name + "_if", castMap(ifMap)));
        if (raw.containsKey("then") && raw.get("then") instanceof Map<?, ?> thenMap) builder.thenSchema(toSchema(name + "_then", castMap(thenMap)));
        if (raw.containsKey("else") && raw.get("else") instanceof Map<?, ?> elseMap) builder.elseSchema(toSchema(name + "_else", castMap(elseMap)));

        // patternProperties
        if (raw.containsKey("patternProperties")) {
            Object pp = raw.get("patternProperties");
            if (pp instanceof Map<?, ?> ppMap) {
                Map<String, Schema> patternPropertiesMap = new HashMap<>();
                for (Map.Entry<?, ?> entry : ppMap.entrySet()) {
                    if (entry.getValue() instanceof Map<?, ?> childMap) {
                        patternPropertiesMap.put(String.valueOf(entry.getKey()),
                                toSchema("pattern_" + String.valueOf(entry.getKey()), castMap(childMap)));
                    }
                }
                builder.patternProperties(patternPropertiesMap);
            }
        }

        // properties
        if (raw.containsKey("properties")) {
            Object props = raw.get("properties");
            if (props instanceof Map<?, ?> propsMap) {
                Map<String, Schema> propertiesMap = new HashMap<>();
                for (Map.Entry<?, ?> entry : propsMap.entrySet()) {
                    if (entry.getValue() instanceof Map<?, ?> childMap) {
                        propertiesMap.put(String.valueOf(entry.getKey()),
                                toSchema(String.valueOf(entry.getKey()), castMap(childMap)));
                    }
                }
                builder.properties(propertiesMap);
            }
        }

        // items
        if (raw.containsKey("items") && raw.get("items") instanceof Map<?, ?> itemsMap) {
            builder.itemSchema(toSchema(name + "[]", castMap(itemsMap)));
        }

        // array constraints
        if (raw.containsKey("minItems") && raw.get("minItems") instanceof Number n) builder.minItems(n.intValue());
        if (raw.containsKey("maxItems") && raw.get("maxItems") instanceof Number n) builder.maxItems(n.intValue());
        if (raw.containsKey("uniqueItems") && raw.get("uniqueItems") instanceof Boolean b) builder.uniqueItems(b);
        if (raw.containsKey("prefixItems") && raw.get("prefixItems") instanceof List<?> prefixRaw) {
            List<Schema> prefixSchemas = new ArrayList<>();
            for (Object o : prefixRaw) {
                if (o instanceof Map<?, ?> map) prefixSchemas.add(toSchema(name + "_prefixItem_" + prefixSchemas.size(), castMap(map)));
            }
            builder.prefixItems(prefixSchemas);
        }
        if (raw.containsKey("additionalItems")) {
            Object ai = raw.get("additionalItems");
            if (ai instanceof Boolean b) {
                // Cria schema especial usando builder
                Schema special = Schema.builder(name + "_additionalItems", b ? SchemaType.ANY : SchemaType.NULL)
                        .build();
                builder.additionalItemsSchema(special);
            } else if (ai instanceof Map<?, ?> map) {
                builder.additionalItemsSchema(toSchema(name + "_additionalItems", castMap(map)));
            }
        }
        if (raw.containsKey("contains")) {
            Object containsValue = raw.get("contains");
            if (containsValue instanceof Map<?, ?> map) {
                builder.containsSchema(toSchema(name + "_contains", castMap(map)));
            } else if (containsValue instanceof Boolean b) {
                builder.containsBoolean(b);
            }
        }
        if (raw.containsKey("minContains") && raw.get("minContains") instanceof Number n) {
            builder.minContains(n.intValue());
        } else if (raw.containsKey("contains")) {
            Object containsValue = raw.get("contains");
            if (containsValue instanceof Map<?, ?>) {
                builder.minContains(1);
            }
        }
        if (raw.containsKey("maxContains") && raw.get("maxContains") instanceof Number n) {
            builder.maxContains(n.intValue());
        }
        if (raw.containsKey("unevaluatedItems")) {
            Object unevaluatedItems = raw.get("unevaluatedItems");
            if (unevaluatedItems instanceof Boolean b) {
                builder.unevaluatedItemsAllowed(b);
            } else if (unevaluatedItems instanceof Map<?, ?> map) {
                builder.unevaluatedItemsSchema(toSchema(name + "_unevaluatedItems", castMap(map)));
            }
        }

        // object constraints
        if (raw.containsKey("minProperties") && raw.get("minProperties") instanceof Number n) builder.minProperties(n.intValue());
        if (raw.containsKey("maxProperties") && raw.get("maxProperties") instanceof Number n) builder.maxProperties(n.intValue());
        if (raw.containsKey("propertyNames") && raw.get("propertyNames") instanceof Map<?, ?> map) {
            builder.propertyNamesSchema(toSchema(name + "_propertyNames", castMap(map)));
        }
        if (raw.containsKey("dependentRequired") && raw.get("dependentRequired") instanceof Map<?, ?> map) {
            Map<String, List<String>> depReq = new HashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = String.valueOf(entry.getKey());
                if (entry.getValue() instanceof List<?> list) {
                    List<String> req = new ArrayList<>();
                    for (Object item : list) req.add(String.valueOf(item));
                    depReq.put(key, req);
                }
            }
            builder.dependentRequired(depReq);
        }
        if (raw.containsKey("dependentSchemas") && raw.get("dependentSchemas") instanceof Map<?, ?> map) {
            Map<String, Schema> depSchemas = new HashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = String.valueOf(entry.getKey());
                if (entry.getValue() instanceof Map<?, ?> subMap) {
                    depSchemas.put(key, toSchema(name + "_depSchema_" + key, castMap(subMap)));
                }
            }
            builder.dependentSchemas(depSchemas);
        }
        if (raw.containsKey("unevaluatedProperties")) {
            Object unevaluatedProperties = raw.get("unevaluatedProperties");
            if (unevaluatedProperties instanceof Boolean b) {
                builder.unevaluatedPropertiesAllowed(b);
            } else if (unevaluatedProperties instanceof Map<?, ?> map) {
                builder.unevaluatedPropertiesSchema(toSchema(name + "_unevaluatedProperties", castMap(map)));
            }
        }

        // const e metadados
        if (raw.containsKey("const")) builder.constValue(raw.get("const"));
        if (raw.containsKey("readOnly") && raw.get("readOnly") instanceof Boolean b) builder.readOnly(b);
        if (raw.containsKey("writeOnly") && raw.get("writeOnly") instanceof Boolean b) builder.writeOnly(b);
        if (raw.containsKey("default")) builder.defaultValue(raw.get("default"));
        if (raw.containsKey("examples") && raw.get("examples") instanceof List<?> examples) {
            builder.examples(new ArrayList<>(examples));
        }
        if (raw.containsKey("deprecated") && raw.get("deprecated") instanceof Boolean b) builder.deprecated(b);
        if (raw.containsKey("$comment") && raw.get("$comment") instanceof String comment) builder.comment(comment);
        if (raw.containsKey("contentEncoding") && raw.get("contentEncoding") instanceof String contentEncoding) builder.contentEncoding(contentEncoding);
        if (raw.containsKey("contentMediaType") && raw.get("contentMediaType") instanceof String contentMediaType) builder.contentMediaType(contentMediaType);
        if (raw.containsKey("contentSchema") && raw.get("contentSchema") instanceof Map<?, ?> map) {
            builder.contentSchema(toSchema(name + "_contentSchema", castMap(map)));
        }

        return builder.build();
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
