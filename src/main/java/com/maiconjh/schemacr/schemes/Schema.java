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
    private final boolean additionalProperties;
    private final Number minimum;
    private final Number maximum;
    private final boolean exclusiveMinimum;
    private final boolean exclusiveMaximum;
    private final Integer minLength;
    private final Integer maxLength;
    private final String pattern;
    private final String format; // JSON Schema format (date, email, uri, etc.)
    private final Number multipleOf; // MultipleOf constraint for numbers
    private final List<Object> enumValues;
    private final String schemaDialect; // $schema keyword
    private final String id; // $id keyword
    private final String title; // title keyword
    private final String description; // description keyword
    private final List<String> typeList; // type as list (e.g., ["string", "null"])
    private final String ref; // $ref for schema references
    private final String version; // Schema version for compatibility
    private final String compatibility; // Compatibility flag (e.g., "1.21", "1.20")
    private final List<Schema> allOf; // allOf composition
    private final List<Schema> anyOf; // anyOf composition
    private final List<Schema> oneOf; // oneOf composition
    private final Schema notSchema; // not schema
    private final Schema ifSchema; // if schema (conditional)
    private final Schema thenSchema; // then schema (conditional)
    private final Schema elseSchema; // else schema (conditional)

    // Main constructor with all parameters
    public Schema(String name, SchemaType type, Map<String, Schema> properties, 
                  Map<String, Schema> patternProperties, Schema itemSchema, List<String> requiredFields, 
                  boolean additionalProperties, Number minimum, Number maximum, 
                  boolean exclusiveMinimum, boolean exclusiveMaximum,
                  Integer minLength, Integer maxLength, String pattern, String format, Number multipleOf,
                  List<Object> enumValues, String schemaDialect, String id, String title, String description,
                  List<String> typeList, String ref, String version, String compatibility,
                  List<Schema> allOf, List<Schema> anyOf, List<Schema> oneOf,
                  Schema notSchema, Schema ifSchema, Schema thenSchema, Schema elseSchema) {
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
    }

    // Simplified constructor: name, type, properties, itemSchema
    public Schema(String name, SchemaType type, Map<String, Schema> properties, Schema itemSchema) {
        this(name, type, properties, null, itemSchema, null, true,
             null, null, false, false,
             null, null, null, null, null,
             null, null, null, null, null,
             null, null, null, null,
             null, null, null, null, null, null, null);
    }

    // Constructor with oneOf composition
    public Schema(String name, SchemaType type, List<Schema> oneOf) {
        this(name, type, null, null, null, null, true,
             null, null, false, false,
             null, null, null, null, null,
             null, null, null, null, null,
             null, null, null, null,
             null, null, oneOf, null, null, null, null);
    }

    // Constructor: name, type, properties, itemSchema, requiredFields
    public Schema(String name, SchemaType type, Map<String, Schema> properties, 
                  Schema itemSchema, List<String> requiredFields) {
        this(name, type, properties, null, itemSchema, requiredFields, true,
             null, null, false, false,
             null, null, null, null, null,
             null, null, null, null, null,
             null, null, null, null,
             null, null, null, null, null, null, null);
    }

    // Constructor: name, type, properties, itemSchema, requiredFields, additionalProperties
    public Schema(String name, SchemaType type, Map<String, Schema> properties, 
                  Schema itemSchema, List<String> requiredFields, boolean additionalProperties) {
        this(name, type, properties, null, itemSchema, requiredFields, additionalProperties,
             null, null, false, false,
             null, null, null, null, null,
             null, null, null, null, null,
             null, null, null, null,
             null, null, null, null, null, null, null);
    }

    // Constructor with numeric/string constraints
    public Schema(String name, SchemaType type, Map<String, Schema> properties, 
                  Schema itemSchema, List<String> requiredFields, boolean additionalProperties,
                  Number minimum, Number maximum, boolean exclusiveMinimum, boolean exclusiveMaximum,
                  Integer minLength, Integer maxLength, String pattern, List<Object> enumValues) {
        this(name, type, properties, null, itemSchema, requiredFields, additionalProperties,
             minimum, maximum, exclusiveMinimum, exclusiveMaximum,
             minLength, maxLength, pattern, null, null, enumValues,
             null, null, null, null, null,
             null, null, null, null,
             null, null, null, null, null, null);
    }

    // Constructor with patternProperties
    public Schema(String name, SchemaType type, Map<String, Schema> properties, 
                  Map<String, Schema> patternProperties, Schema itemSchema, List<String> requiredFields) {
        this(name, type, properties, patternProperties, itemSchema, requiredFields, true,
             null, null, false, false,
             null, null, null, null, null,
             null, null, null, null, null,
             null, null, null, null,
             null, null, null, null, null, null, null);
    }

    // Constructor with patternProperties and additionalProperties
    public Schema(String name, SchemaType type, Map<String, Schema> properties, 
                  Map<String, Schema> patternProperties, Schema itemSchema, List<String> requiredFields, 
                  boolean additionalProperties) {
        this(name, type, properties, patternProperties, itemSchema, requiredFields, additionalProperties,
             null, null, false, false,
             null, null, null, null, null,
             null, null, null, null, null,
             null, null, null, null,
             null, null, null, null, null, null, null);
    }

    // Constructor with numeric/string constraints and $ref
    public Schema(String name, SchemaType type, Map<String, Schema> properties, 
                  Map<String, Schema> patternProperties, Schema itemSchema, List<String> requiredFields, 
                  boolean additionalProperties, Number minimum, Number maximum, 
                  boolean exclusiveMinimum, boolean exclusiveMaximum,
                  Integer minLength, Integer maxLength, String pattern, List<Object> enumValues, String ref) {
        this(name, type, properties, patternProperties, itemSchema, requiredFields, additionalProperties,
             minimum, maximum, exclusiveMinimum, exclusiveMaximum,
             minLength, maxLength, pattern, null, null, enumValues,
             null, null, null, null, null,
             ref, null, null, null,
             null, null, null, null, null, null);
    }

    // Constructor with format, multipleOf, version, compatibility, allOf, anyOf
    public Schema(String name, SchemaType type, Map<String, Schema> properties, 
                  Map<String, Schema> patternProperties, Schema itemSchema, List<String> requiredFields, 
                  boolean additionalProperties, Number minimum, Number maximum, 
                  boolean exclusiveMinimum, boolean exclusiveMaximum,
                  Integer minLength, Integer maxLength, String pattern, String format, Number multipleOf,
                  List<Object> enumValues, String ref, String version, String compatibility, List<Schema> allOf, List<Schema> anyOf) {
        this(name, type, properties, patternProperties, itemSchema, requiredFields, additionalProperties,
             minimum, maximum, exclusiveMinimum, exclusiveMaximum,
             minLength, maxLength, pattern, format, multipleOf, enumValues,
             null, null, null, null, null,
             ref, version, compatibility,
             allOf, anyOf, null, null, null, null, null);
    }

    // Getters (unchanged)
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

    /**
     * Returns the format constraint (e.g., "email", "uri", "date-time").
     * @return the format string or null
     */
    public String getFormat() {
        return format;
    }

    /**
     * Returns the multipleOf constraint for numeric types.
     * @return the multipleOf value or null
     */
    public Number getMultipleOf() {
        return multipleOf;
    }

    /**
     * Checks if this schema has a format constraint.
     * @return true if format is defined
     */
    public boolean hasFormat() {
        return format != null && !format.isEmpty();
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

    /**
     * Returns the schema version.
     * @return the version string or null
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the compatibility flag (e.g., "1.21", "1.20").
     * @return the compatibility string or null
     */
    public String getCompatibility() {
        return compatibility;
    }

    /**
     * Returns the allOf schemas for composition.
     * @return list of allOf schemas, or empty list if not defined
     */
    public List<Schema> getAllOf() {
        return allOf;
    }

    /**
     * Returns the anyOf schemas for composition.
     * @return list of anyOf schemas, or empty list if not defined
     */
    public List<Schema> getAnyOf() {
        return anyOf;
    }

    /**
     * Returns the oneOf schemas for composition.
     * @return list of oneOf schemas, or empty list if not defined
     */
    public List<Schema> getOneOf() {
        return oneOf;
    }

    /**
     * Checks if this schema has allOf composition.
     * @return true if allOf is defined and not empty
     */
    public boolean hasAllOf() {
        return !allOf.isEmpty();
    }

    /**
     * Checks if this schema has anyOf composition.
     * @return true if anyOf is defined and not empty
     */
    public boolean hasAnyOf() {
        return !anyOf.isEmpty();
    }

    /**
     * Checks if this schema has oneOf composition.
     * @return true if oneOf is defined and not empty
     */
    public boolean hasOneOf() {
        return !oneOf.isEmpty();
    }

    /**
     * Returns the not schema.
     * @return the not schema or null
     */
    public Schema getNot() {
        return notSchema;
    }

    /**
     * Checks if this schema has a not clause.
     * @return true if not schema is defined
     */
    public boolean hasNot() {
        return notSchema != null;
    }

    /**
     * Returns the if schema for conditional validation.
     * @return the if schema or null
     */
    public Schema getIfSchema() {
        return ifSchema;
    }

    /**
     * Returns the then schema for conditional validation.
     * @return the then schema or null
     */
    public Schema getThenSchema() {
        return thenSchema;
    }

    /**
     * Returns the else schema for conditional validation.
     * @return the else schema or null
     */
    public Schema getElseSchema() {
        return elseSchema;
    }

    /**
     * Checks if this schema has conditional validation (if/then/else).
     * @return true if if schema is defined
     */
    public boolean hasConditional() {
        return ifSchema != null;
    }

    /**
     * Returns the schema dialect (e.g., "https://json-schema.org/draft/2020-12/schema").
     * @return the schema dialect or null
     */
    public String getSchemaDialect() {
        return schemaDialect;
    }

    /**
     * Returns the schema ID (e.g., "https://example.com/schemas/user.json").
     * @return the schema ID or null
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the schema title.
     * @return the title or null
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the schema description.
     * @return the description or null
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if this schema has a type union (array of types).
     * @return true if type is an array
     */
    public boolean hasTypeUnion() {
        return typeList != null && !typeList.isEmpty();
    }

    /**
     * Returns the allowed types for this schema.
     * @return list of types or empty list
     */
    public List<String> getAllowedTypes() {
        return typeList != null ? typeList : Collections.emptyList();
    }
}