package com.maiconjh.schemacr.schemes;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Schema model used by validators.
 *
 * <p>Extends the basic model with constraints like required fields, 
 * min/max values, patterns, enums, format, multipleOf, etc.</p>
 */
public class Schema {

    private final String name;
    private final SchemaType type;
    private final Map<String, Schema> properties;
    private final Map<String, Schema> patternProperties;
    private final Schema itemSchema;
    private final List<String> requiredFields;
    private final Object additionalProperties; // Can be Boolean or Schema
    private final Number minimum;
    private final Number maximum;
    private final boolean exclusiveMinimum;
    private final boolean exclusiveMaximum;
    private final Integer minLength;
    private final Integer maxLength;
    private final String pattern;
    private final String format;
    private final Number multipleOf;
    private final List<Object> enumValues;
    private final String schemaDialect;
    private final String id;
    private final String title;
    private final String description;
    private final List<String> typeList;
    private final String ref;
    private final String version;
    private final String compatibility;
    private final List<Schema> allOf;
    private final List<Schema> anyOf;
    private final List<Schema> oneOf;
    private final Schema notSchema;
    private final Schema ifSchema;
    private final Schema thenSchema;
    private final Schema elseSchema;

    // Array constraints
    private final Integer minItems;
    private final Integer maxItems;
    private final Boolean uniqueItems;
    private final List<Schema> prefixItems;
    private final Schema additionalItemsSchema;

    // Object constraints
    private final Integer minProperties;
    private final Integer maxProperties;
    private final Map<String, List<String>> dependentRequired;
    private final Map<String, Schema> dependentSchemas;

    // Const and metadata keywords
    private final Object constValue;
    private final Boolean readOnly;
    private final Boolean writeOnly;

    // Construtor principal (único)
    public Schema(String name, SchemaType type, Map<String, Schema> properties, 
                  Map<String, Schema> patternProperties, Schema itemSchema, List<String> requiredFields, 
                  Object additionalProperties, Number minimum, Number maximum, 
                  boolean exclusiveMinimum, boolean exclusiveMaximum,
                  Integer minLength, Integer maxLength, String pattern, String format, Number multipleOf,
                  List<Object> enumValues, String schemaDialect, String id, String title, String description,
                  List<String> typeList, String ref, String version, String compatibility,
                  List<Schema> allOf, List<Schema> anyOf, List<Schema> oneOf,
                  Schema notSchema, Schema ifSchema, Schema thenSchema, Schema elseSchema,
                  Integer minItems, Integer maxItems, Boolean uniqueItems, List<Schema> prefixItems, Schema additionalItemsSchema,
                  Integer minProperties, Integer maxProperties, Map<String, List<String>> dependentRequired, Map<String, Schema> dependentSchemas,
                  Object constValue, Boolean readOnly, Boolean writeOnly) {
        this.name = name;
        this.type = type;
        this.properties = properties == null ? Collections.emptyMap() : Collections.unmodifiableMap(properties);
        this.patternProperties = patternProperties == null ? Collections.emptyMap() : Collections.unmodifiableMap(patternProperties);
        this.itemSchema = itemSchema;
        this.requiredFields = requiredFields == null ? Collections.emptyList() : Collections.unmodifiableList(requiredFields);
        this.additionalProperties = additionalProperties != null ? additionalProperties : true;
        this.minimum = minimum;
        this.maximum = maximum;
        this.exclusiveMinimum = exclusiveMinimum;
        this.exclusiveMaximum = exclusiveMaximum;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.pattern = pattern;
        this.format = format;
        this.multipleOf = multipleOf;
        this.enumValues = enumValues == null ? Collections.emptyList() : Collections.unmodifiableList(enumValues);
        this.schemaDialect = schemaDialect;
        this.id = id;
        this.title = title;
        this.description = description;
        this.typeList = typeList;
        this.ref = ref;
        this.version = version;
        this.compatibility = compatibility;
        this.allOf = allOf == null ? Collections.emptyList() : Collections.unmodifiableList(allOf);
        this.anyOf = anyOf == null ? Collections.emptyList() : Collections.unmodifiableList(anyOf);
        this.oneOf = oneOf == null ? Collections.emptyList() : Collections.unmodifiableList(oneOf);
        this.notSchema = notSchema;
        this.ifSchema = ifSchema;
        this.thenSchema = thenSchema;
        this.elseSchema = elseSchema;

        // Array constraints
        this.minItems = minItems;
        this.maxItems = maxItems;
        this.uniqueItems = uniqueItems;
        this.prefixItems = prefixItems == null ? Collections.emptyList() : Collections.unmodifiableList(prefixItems);
        this.additionalItemsSchema = additionalItemsSchema;

        // Object constraints
        this.minProperties = minProperties;
        this.maxProperties = maxProperties;
        this.dependentRequired = dependentRequired == null ? Collections.emptyMap() : Collections.unmodifiableMap(dependentRequired);
        this.dependentSchemas = dependentSchemas == null ? Collections.emptyMap() : Collections.unmodifiableMap(dependentSchemas);

        // Const and metadata
        this.constValue = constValue;
        this.readOnly = readOnly;
        this.writeOnly = writeOnly;
    }

    // ========== Getters (todos os campos) ==========
    public String getName() { return name; }
    public SchemaType getType() { return type; }
    public Map<String, Schema> getProperties() { return properties; }
    public Map<String, Schema> getPatternProperties() { return patternProperties; }
    public Schema getItemSchema() { return itemSchema; }
    public List<String> getRequiredFields() { return requiredFields; }
    public boolean isAdditionalPropertiesAllowed() { 
        if (additionalProperties == null) return true;
        if (additionalProperties instanceof Boolean) return (Boolean) additionalProperties;
        return additionalProperties instanceof Schema; // If it's a Schema, additional properties are allowed
    }
    public Schema getAdditionalPropertiesSchema() {
        if (additionalProperties instanceof Schema) {
            return (Schema) additionalProperties;
        }
        return null;
    }
    public boolean hasAdditionalPropertiesSchema() {
        return additionalProperties instanceof Schema;
    }
    public Number getMinimum() { return minimum; }
    public Number getMaximum() { return maximum; }
    public boolean isExclusiveMinimum() { return exclusiveMinimum; }
    public boolean isExclusiveMaximum() { return exclusiveMaximum; }
    public Integer getMinLength() { return minLength; }
    public Integer getMaxLength() { return maxLength; }
    public String getPattern() { return pattern; }
    public String getFormat() { return format; }
    public boolean hasFormat() { return format != null && !format.isEmpty(); }
    public Number getMultipleOf() { return multipleOf; }
    public List<Object> getEnumValues() { return enumValues; }
    public boolean isValidEnum(Object value) { return enumValues.isEmpty() || enumValues.contains(value); }
    public String getRef() { return ref; }
    public boolean isRef() { return ref != null && !ref.isEmpty(); }
    public String getVersion() { return version; }
    public String getCompatibility() { return compatibility; }
    public List<Schema> getAllOf() { return allOf; }
    public List<Schema> getAnyOf() { return anyOf; }
    public List<Schema> getOneOf() { return oneOf; }
    public boolean hasAllOf() { return !allOf.isEmpty(); }
    public boolean hasAnyOf() { return !anyOf.isEmpty(); }
    public boolean hasOneOf() { return !oneOf.isEmpty(); }
    public Schema getNot() { return notSchema; }
    public boolean hasNot() { return notSchema != null; }
    public Schema getIfSchema() { return ifSchema; }
    public Schema getThenSchema() { return thenSchema; }
    public Schema getElseSchema() { return elseSchema; }
    public boolean hasConditional() { return ifSchema != null; }
    public String getSchemaDialect() { return schemaDialect; }
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean hasTypeUnion() { return typeList != null && !typeList.isEmpty(); }
    public List<String> getAllowedTypes() { return typeList != null ? typeList : Collections.emptyList(); }

    // Array getters
    public Integer getMinItems() { return minItems; }
    public Integer getMaxItems() { return maxItems; }
    public Boolean isUniqueItems() { return uniqueItems; }
    public List<Schema> getPrefixItems() { return prefixItems; }
    public Schema getAdditionalItemsSchema() { return additionalItemsSchema; }
    public boolean hasArrayConstraints() { return minItems != null || maxItems != null || uniqueItems != null; }

    // Object getters
    public Integer getMinProperties() { return minProperties; }
    public Integer getMaxProperties() { return maxProperties; }
    public Map<String, List<String>> getDependentRequired() { return dependentRequired; }
    public Map<String, Schema> getDependentSchemas() { return dependentSchemas; }
    public boolean hasObjectConstraints() { return minProperties != null || maxProperties != null || !dependentRequired.isEmpty() || !dependentSchemas.isEmpty(); }

    // Const & metadata getters
    public Object getConstValue() { return constValue; }
    public boolean hasConst() { return constValue != null; }
    public Boolean isReadOnly() { return readOnly; }
    public Boolean isWriteOnly() { return writeOnly; }
    public boolean hasReadWriteOnly() { return readOnly != null || writeOnly != null; }

    // ========== Builder ==========
    public static Builder builder(String name, SchemaType type) {
        return new Builder(name, type);
    }

    public static class Builder {
        private final String name;
        private final SchemaType type;
        private Map<String, Schema> properties = Collections.emptyMap();
        private Map<String, Schema> patternProperties = Collections.emptyMap();
        private Schema itemSchema;
        private List<String> requiredFields = Collections.emptyList();
        private Object additionalProperties = true; // Can be Boolean or Schema
        private Number minimum;
        private Number maximum;
        private boolean exclusiveMinimum = false;
        private boolean exclusiveMaximum = false;
        private Integer minLength;
        private Integer maxLength;
        private String pattern;
        private String format;
        private Number multipleOf;
        private List<Object> enumValues = Collections.emptyList();
        private String schemaDialect;
        private String id;
        private String title;
        private String description;
        private List<String> typeList = Collections.emptyList();
        private String ref;
        private String version;
        private String compatibility;
        private List<Schema> allOf = Collections.emptyList();
        private List<Schema> anyOf = Collections.emptyList();
        private List<Schema> oneOf = Collections.emptyList();
        private Schema notSchema;
        private Schema ifSchema;
        private Schema thenSchema;
        private Schema elseSchema;
        // Array
        private Integer minItems;
        private Integer maxItems;
        private Boolean uniqueItems;
        private List<Schema> prefixItems = Collections.emptyList();
        private Schema additionalItemsSchema;
        // Object
        private Integer minProperties;
        private Integer maxProperties;
        private Map<String, List<String>> dependentRequired = Collections.emptyMap();
        private Map<String, Schema> dependentSchemas = Collections.emptyMap();
        // Const & metadata
        private Object constValue;
        private Boolean readOnly;
        private Boolean writeOnly;

        private Builder(String name, SchemaType type) {
            this.name = name;
            this.type = type;
        }

        public Builder properties(Map<String, Schema> properties) { this.properties = properties; return this; }
        public Builder patternProperties(Map<String, Schema> patternProperties) { this.patternProperties = patternProperties; return this; }
        public Builder itemSchema(Schema itemSchema) { this.itemSchema = itemSchema; return this; }
        public Builder requiredFields(List<String> requiredFields) { this.requiredFields = requiredFields; return this; }
        public Builder additionalProperties(boolean additionalProperties) { this.additionalProperties = additionalProperties; return this; }
        public Builder additionalProperties(Schema additionalPropertiesSchema) { this.additionalProperties = additionalPropertiesSchema; return this; }
        public Builder minimum(Number minimum) { this.minimum = minimum; return this; }
        public Builder maximum(Number maximum) { this.maximum = maximum; return this; }
        public Builder exclusiveMinimum(boolean exclusiveMinimum) { this.exclusiveMinimum = exclusiveMinimum; return this; }
        public Builder exclusiveMaximum(boolean exclusiveMaximum) { this.exclusiveMaximum = exclusiveMaximum; return this; }
        public Builder minLength(Integer minLength) { this.minLength = minLength; return this; }
        public Builder maxLength(Integer maxLength) { this.maxLength = maxLength; return this; }
        public Builder pattern(String pattern) { this.pattern = pattern; return this; }
        public Builder format(String format) { this.format = format; return this; }
        public Builder multipleOf(Number multipleOf) { this.multipleOf = multipleOf; return this; }
        public Builder enumValues(List<Object> enumValues) { this.enumValues = enumValues; return this; }
        public Builder schemaDialect(String schemaDialect) { this.schemaDialect = schemaDialect; return this; }
        public Builder id(String id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder typeList(List<String> typeList) { this.typeList = typeList; return this; }
        public Builder ref(String ref) { this.ref = ref; return this; }
        public Builder version(String version) { this.version = version; return this; }
        public Builder compatibility(String compatibility) { this.compatibility = compatibility; return this; }
        public Builder allOf(List<Schema> allOf) { this.allOf = allOf; return this; }
        public Builder anyOf(List<Schema> anyOf) { this.anyOf = anyOf; return this; }
        public Builder oneOf(List<Schema> oneOf) { this.oneOf = oneOf; return this; }
        public Builder notSchema(Schema notSchema) { this.notSchema = notSchema; return this; }
        public Builder ifSchema(Schema ifSchema) { this.ifSchema = ifSchema; return this; }
        public Builder thenSchema(Schema thenSchema) { this.thenSchema = thenSchema; return this; }
        public Builder elseSchema(Schema elseSchema) { this.elseSchema = elseSchema; return this; }
        // Array
        public Builder minItems(Integer minItems) { this.minItems = minItems; return this; }
        public Builder maxItems(Integer maxItems) { this.maxItems = maxItems; return this; }
        public Builder uniqueItems(Boolean uniqueItems) { this.uniqueItems = uniqueItems; return this; }
        public Builder prefixItems(List<Schema> prefixItems) { this.prefixItems = prefixItems; return this; }
        public Builder additionalItemsSchema(Schema additionalItemsSchema) { this.additionalItemsSchema = additionalItemsSchema; return this; }
        // Object
        public Builder minProperties(Integer minProperties) { this.minProperties = minProperties; return this; }
        public Builder maxProperties(Integer maxProperties) { this.maxProperties = maxProperties; return this; }
        public Builder dependentRequired(Map<String, List<String>> dependentRequired) { this.dependentRequired = dependentRequired; return this; }
        public Builder dependentSchemas(Map<String, Schema> dependentSchemas) { this.dependentSchemas = dependentSchemas; return this; }
        // Const & metadata
        public Builder constValue(Object constValue) { this.constValue = constValue; return this; }
        public Builder readOnly(Boolean readOnly) { this.readOnly = readOnly; return this; }
        public Builder writeOnly(Boolean writeOnly) { this.writeOnly = writeOnly; return this; }

        public Schema build() {
            return new Schema(name, type, properties, patternProperties, itemSchema, requiredFields,
                    additionalProperties, minimum, maximum, exclusiveMinimum, exclusiveMaximum,
                    minLength, maxLength, pattern, format, multipleOf, enumValues,
                    schemaDialect, id, title, description, typeList, ref, version, compatibility,
                    allOf, anyOf, oneOf, notSchema, ifSchema, thenSchema, elseSchema,
                    minItems, maxItems, uniqueItems, prefixItems, additionalItemsSchema,
                    minProperties, maxProperties, dependentRequired, dependentSchemas,
                    constValue, readOnly, writeOnly);
        }
    }
}