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
    private final Schema containsSchema;
    private final Integer minContains;
    private final Integer maxContains;

    // Object constraints
    private final Integer minProperties;
    private final Integer maxProperties;
    private final Map<String, List<String>> dependentRequired;
    private final Map<String, Schema> dependentSchemas;
    private final Schema propertyNamesSchema;
    private final Boolean unevaluatedPropertiesAllowed;
    private final Schema unevaluatedPropertiesSchema;

    // Const and metadata keywords
    private final Object constValue;
    private final Boolean readOnly;
    private final Boolean writeOnly;
    private final Object defaultValue;
    private final List<Object> examples;
    private final Boolean deprecated;
    private final String contentEncoding;
    private final String contentMediaType;
    private final Schema contentSchema;
    private final Boolean unevaluatedItemsAllowed;
    private final Schema unevaluatedItemsSchema;
    private final String dynamicRef;
    private final String dynamicAnchor;

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
                  Schema containsSchema, Integer minContains, Integer maxContains,
                  Integer minProperties, Integer maxProperties, Map<String, List<String>> dependentRequired, Map<String, Schema> dependentSchemas,
                  Schema propertyNamesSchema, Boolean unevaluatedPropertiesAllowed, Schema unevaluatedPropertiesSchema,
                  Object constValue, Boolean readOnly, Boolean writeOnly,
                  Object defaultValue, List<Object> examples, Boolean deprecated,
                  String contentEncoding, String contentMediaType, Schema contentSchema,
                  Boolean unevaluatedItemsAllowed, Schema unevaluatedItemsSchema,
                  String dynamicRef, String dynamicAnchor) {
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
        this.containsSchema = containsSchema;
        this.minContains = minContains;
        this.maxContains = maxContains;

        // Object constraints
        this.minProperties = minProperties;
        this.maxProperties = maxProperties;
        this.dependentRequired = dependentRequired == null ? Collections.emptyMap() : Collections.unmodifiableMap(dependentRequired);
        this.dependentSchemas = dependentSchemas == null ? Collections.emptyMap() : Collections.unmodifiableMap(dependentSchemas);
        this.propertyNamesSchema = propertyNamesSchema;
        this.unevaluatedPropertiesAllowed = unevaluatedPropertiesAllowed;
        this.unevaluatedPropertiesSchema = unevaluatedPropertiesSchema;

        // Const and metadata
        this.constValue = constValue;
        this.readOnly = readOnly;
        this.writeOnly = writeOnly;
        this.defaultValue = defaultValue;
        this.examples = examples == null ? Collections.emptyList() : Collections.unmodifiableList(examples);
        this.deprecated = deprecated;
        this.contentEncoding = contentEncoding;
        this.contentMediaType = contentMediaType;
        this.contentSchema = contentSchema;
        this.unevaluatedItemsAllowed = unevaluatedItemsAllowed;
        this.unevaluatedItemsSchema = unevaluatedItemsSchema;
        this.dynamicRef = dynamicRef;
        this.dynamicAnchor = dynamicAnchor;
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
    public Schema getContainsSchema() { return containsSchema; }
    public Integer getMinContains() { return minContains; }
    public Integer getMaxContains() { return maxContains; }
    public boolean hasArrayConstraints() {
        return minItems != null || maxItems != null || uniqueItems != null || containsSchema != null
                || minContains != null || maxContains != null
                || unevaluatedItemsAllowed != null || unevaluatedItemsSchema != null;
    }

    // Object getters
    public Integer getMinProperties() { return minProperties; }
    public Integer getMaxProperties() { return maxProperties; }
    public Map<String, List<String>> getDependentRequired() { return dependentRequired; }
    public Map<String, Schema> getDependentSchemas() { return dependentSchemas; }
    public Schema getPropertyNamesSchema() { return propertyNamesSchema; }
    public Boolean isUnevaluatedPropertiesAllowed() { return unevaluatedPropertiesAllowed; }
    public Schema getUnevaluatedPropertiesSchema() { return unevaluatedPropertiesSchema; }
    public boolean hasObjectConstraints() {
        return minProperties != null || maxProperties != null || !dependentRequired.isEmpty()
                || !dependentSchemas.isEmpty() || propertyNamesSchema != null
                || unevaluatedPropertiesAllowed != null || unevaluatedPropertiesSchema != null;
    }

    // Const & metadata getters
    public Object getConstValue() { return constValue; }
    public boolean hasConst() { return constValue != null; }
    public Boolean isReadOnly() { return readOnly; }
    public Boolean isWriteOnly() { return writeOnly; }
    public boolean hasReadWriteOnly() { return readOnly != null || writeOnly != null; }
    public Object getDefaultValue() { return defaultValue; }
    public List<Object> getExamples() { return examples; }
    public Boolean isDeprecated() { return deprecated; }
    public String getContentEncoding() { return contentEncoding; }
    public String getContentMediaType() { return contentMediaType; }
    public Schema getContentSchema() { return contentSchema; }
    public Boolean isUnevaluatedItemsAllowed() { return unevaluatedItemsAllowed; }
    public Schema getUnevaluatedItemsSchema() { return unevaluatedItemsSchema; }
    public String getDynamicRef() { return dynamicRef; }
    public String getDynamicAnchor() { return dynamicAnchor; }
    public boolean hasContentVocabulary() {
        return contentEncoding != null || contentMediaType != null || contentSchema != null;
    }

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
        private Schema containsSchema;
        private Integer minContains;
        private Integer maxContains;
        // Object
        private Integer minProperties;
        private Integer maxProperties;
        private Map<String, List<String>> dependentRequired = Collections.emptyMap();
        private Map<String, Schema> dependentSchemas = Collections.emptyMap();
        private Schema propertyNamesSchema;
        private Boolean unevaluatedPropertiesAllowed;
        private Schema unevaluatedPropertiesSchema;
        // Const & metadata
        private Object constValue;
        private Boolean readOnly;
        private Boolean writeOnly;
        private Object defaultValue;
        private List<Object> examples = Collections.emptyList();
        private Boolean deprecated;
        private String contentEncoding;
        private String contentMediaType;
        private Schema contentSchema;
        private Boolean unevaluatedItemsAllowed;
        private Schema unevaluatedItemsSchema;
        private String dynamicRef;
        private String dynamicAnchor;

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
        public Builder containsSchema(Schema containsSchema) { this.containsSchema = containsSchema; return this; }
        public Builder minContains(Integer minContains) { this.minContains = minContains; return this; }
        public Builder maxContains(Integer maxContains) { this.maxContains = maxContains; return this; }
        // Object
        public Builder minProperties(Integer minProperties) { this.minProperties = minProperties; return this; }
        public Builder maxProperties(Integer maxProperties) { this.maxProperties = maxProperties; return this; }
        public Builder dependentRequired(Map<String, List<String>> dependentRequired) { this.dependentRequired = dependentRequired; return this; }
        public Builder dependentSchemas(Map<String, Schema> dependentSchemas) { this.dependentSchemas = dependentSchemas; return this; }
        public Builder propertyNamesSchema(Schema propertyNamesSchema) { this.propertyNamesSchema = propertyNamesSchema; return this; }
        public Builder unevaluatedPropertiesAllowed(Boolean unevaluatedPropertiesAllowed) { this.unevaluatedPropertiesAllowed = unevaluatedPropertiesAllowed; return this; }
        public Builder unevaluatedPropertiesSchema(Schema unevaluatedPropertiesSchema) { this.unevaluatedPropertiesSchema = unevaluatedPropertiesSchema; return this; }
        // Const & metadata
        public Builder constValue(Object constValue) { this.constValue = constValue; return this; }
        public Builder readOnly(Boolean readOnly) { this.readOnly = readOnly; return this; }
        public Builder writeOnly(Boolean writeOnly) { this.writeOnly = writeOnly; return this; }
        public Builder defaultValue(Object defaultValue) { this.defaultValue = defaultValue; return this; }
        public Builder examples(List<Object> examples) { this.examples = examples; return this; }
        public Builder deprecated(Boolean deprecated) { this.deprecated = deprecated; return this; }
        public Builder contentEncoding(String contentEncoding) { this.contentEncoding = contentEncoding; return this; }
        public Builder contentMediaType(String contentMediaType) { this.contentMediaType = contentMediaType; return this; }
        public Builder contentSchema(Schema contentSchema) { this.contentSchema = contentSchema; return this; }
        public Builder unevaluatedItemsAllowed(Boolean unevaluatedItemsAllowed) { this.unevaluatedItemsAllowed = unevaluatedItemsAllowed; return this; }
        public Builder unevaluatedItemsSchema(Schema unevaluatedItemsSchema) { this.unevaluatedItemsSchema = unevaluatedItemsSchema; return this; }
        public Builder dynamicRef(String dynamicRef) { this.dynamicRef = dynamicRef; return this; }
        public Builder dynamicAnchor(String dynamicAnchor) { this.dynamicAnchor = dynamicAnchor; return this; }

        public Schema build() {
            return new Schema(name, type, properties, patternProperties, itemSchema, requiredFields,
                    additionalProperties, minimum, maximum, exclusiveMinimum, exclusiveMaximum,
                    minLength, maxLength, pattern, format, multipleOf, enumValues,
                    schemaDialect, id, title, description, typeList, ref, version, compatibility,
                    allOf, anyOf, oneOf, notSchema, ifSchema, thenSchema, elseSchema,
                    minItems, maxItems, uniqueItems, prefixItems, additionalItemsSchema,
                    containsSchema, minContains, maxContains,
                    minProperties, maxProperties, dependentRequired, dependentSchemas,
                    propertyNamesSchema, unevaluatedPropertiesAllowed, unevaluatedPropertiesSchema,
                    constValue, readOnly, writeOnly,
                    defaultValue, examples, deprecated,
                    contentEncoding, contentMediaType, contentSchema,
                    unevaluatedItemsAllowed, unevaluatedItemsSchema,
                    dynamicRef, dynamicAnchor);
        }
    }
}
