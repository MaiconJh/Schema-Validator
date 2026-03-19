package com.maiconjh.schemacr.schemes;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Schema model used by validators.
 *
 * <p>Extends the basic model with constraints like required fields, 
 * min/max values, patterns, enums, etc.</p>
 */
public class Schema {

    private final String name;
    private final SchemaType type;
    private final Map<String, Schema> properties;
    private final Map<String, Schema> patternProperties;
    private final Schema itemSchema;
    private final List<String> requiredFields;
    private final boolean additionalProperties;
    private final Number minimum;
    private final Number maximum;
    private final boolean exclusiveMinimum;
    private final boolean exclusiveMaximum;
    private final Integer minLength;
    private final Integer maxLength;
    private final String pattern;
    private final List<Object> enumValues;
    private final String ref; // $ref for schema references

    public Schema(String name, SchemaType type, Map<String, Schema> properties, Schema itemSchema) {
        this(name, type, properties, null, itemSchema, null);
    }

    public Schema(String name, SchemaType type, Map<String, Schema> properties, 
                  Schema itemSchema, List<String> requiredFields) {
        this(name, type, properties, null, itemSchema, requiredFields, true);
    }

    public Schema(String name, SchemaType type, Map<String, Schema> properties, 
                  Schema itemSchema, List<String> requiredFields, boolean additionalProperties) {
        this(name, type, properties, null, itemSchema, requiredFields, additionalProperties, 
             null, null, false, false, null, null, null, null, null);
    }

    public Schema(String name, SchemaType type, Map<String, Schema> properties, 
                  Schema itemSchema, List<String> requiredFields, boolean additionalProperties,
                  Number minimum, Number maximum, boolean exclusiveMinimum, boolean exclusiveMaximum,
                  Integer minLength, Integer maxLength, String pattern, List<Object> enumValues) {
        this(name, type, properties, null, itemSchema, requiredFields, additionalProperties, 
             minimum, maximum, exclusiveMinimum, exclusiveMaximum, minLength, maxLength, pattern, enumValues, null);
    }

    public Schema(String name, SchemaType type, Map<String, Schema> properties, 
                  Map<String, Schema> patternProperties, Schema itemSchema, List<String> requiredFields) {
        this(name, type, properties, patternProperties, itemSchema, requiredFields, true);
    }

    public Schema(String name, SchemaType type, Map<String, Schema> properties, 
                  Map<String, Schema> patternProperties, Schema itemSchema, List<String> requiredFields, 
                  boolean additionalProperties) {
        this(name, type, properties, patternProperties, itemSchema, requiredFields, additionalProperties, 
             null, null, false, false, null, null, null, null, null);
    }

    public Schema(String name, SchemaType type, Map<String, Schema> properties, 
                  Map<String, Schema> patternProperties, Schema itemSchema, List<String> requiredFields, 
                  boolean additionalProperties, Number minimum, Number maximum, 
                  boolean exclusiveMinimum, boolean exclusiveMaximum,
                  Integer minLength, Integer maxLength, String pattern, List<Object> enumValues, String ref) {
        this.name = name;
        this.type = type;
        this.properties = properties == null ? Collections.emptyMap() : Collections.unmodifiableMap(properties);
        this.patternProperties = patternProperties == null ? Collections.emptyMap() : Collections.unmodifiableMap(patternProperties);
        this.itemSchema = itemSchema;
        this.requiredFields = requiredFields == null ? Collections.emptyList() : Collections.unmodifiableList(requiredFields);
        this.additionalProperties = additionalProperties;
        this.minimum = minimum;
        this.maximum = maximum;
        this.exclusiveMinimum = exclusiveMinimum;
        this.exclusiveMaximum = exclusiveMaximum;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.pattern = pattern;
        this.enumValues = enumValues == null ? Collections.emptyList() : Collections.unmodifiableList(enumValues);
        this.ref = ref;
    }

    public String getName() {
        return name;
    }

    public SchemaType getType() {
        return type;
    }

    public Map<String, Schema> getProperties() {
        return properties;
    }

    public Map<String, Schema> getPatternProperties() {
        return patternProperties;
    }

    public Schema getItemSchema() {
        return itemSchema;
    }

    public List<String> getRequiredFields() {
        return requiredFields;
    }

    public boolean isAdditionalPropertiesAllowed() {
        return additionalProperties;
    }

    public Number getMinimum() {
        return minimum;
    }

    public Number getMaximum() {
        return maximum;
    }

    public boolean isExclusiveMinimum() {
        return exclusiveMinimum;
    }

    public boolean isExclusiveMaximum() {
        return exclusiveMaximum;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public String getPattern() {
        return pattern;
    }

    public List<Object> getEnumValues() {
        return enumValues;
    }

    public boolean isValidEnum(Object value) {
        return enumValues.isEmpty() || enumValues.contains(value);
    }

    /**
     * Returns the $ref value for schema references.
     * @return the reference path or null
     */
    public String getRef() {
        return ref;
    }

    /**
     * Checks if this schema is a reference ($ref).
     * @return true if this schema has a reference
     */
    public boolean isRef() {
        return ref != null && !ref.isEmpty();
    }
}
