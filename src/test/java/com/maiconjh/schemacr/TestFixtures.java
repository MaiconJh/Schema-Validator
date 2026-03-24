package com.maiconjh.schemacr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaType;
import com.maiconjh.schemacr.validation.ValidationError;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Test fixtures utility class for creating test objects.
 * 
 * <p>Provides helper methods for:</p>
 * <ul>
 *   <li>Creating Schema objects using Schema.builder()</li>
 *   <li>Creating JsonNode objects using ObjectMapper</li>
 *   <li>Defining test resource paths</li>
 *   <li>Creating ValidationError instances</li>
 * </ul>
 * 
 * <p>Note: This class does NOT include specific test data - only utilities.</p>
 */
public class TestFixtures {

    // ========== ObjectMapper ==========
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // ========== Test Resource Path Constants ==========
    
    /** Base path for test resources */
    public static final String TEST_RESOURCES_PATH = "src/test/resources";
    
    /** Base path for example schemas */
    public static final String EXAMPLES_SCHEMAS_PATH = "src/main/resources/examples/schemas";
    
    /** Base path for main schemas */
    public static final String SCHEMAS_PATH = "src/main/resources/schemas";
    
    /** Path to complex example schema */
    public static final String COMPLEX_EXAMPLE_SCHEMA_PATH = EXAMPLES_SCHEMAS_PATH + "/complex-example.schema.json";
    
    /** Path to complex item schema */
    public static final String COMPLEX_ITEM_SCHEMA_PATH = EXAMPLES_SCHEMAS_PATH + "/complex-item.schema.json";
    
    /** Path to player profile schema */
    public static final String PLAYER_PROFILE_SCHEMA_PATH = EXAMPLES_SCHEMAS_PATH + "/player-profile.schema.json";
    
    /** Path to custom block schema */
    public static final String CUSTOM_BLOCK_SCHEMA_PATH = SCHEMAS_PATH + "/custom-block.schema.json";
    
    /** Path to item schema */
    public static final String ITEM_SCHEMA_PATH = SCHEMAS_PATH + "/item.schema.json";
    
    /** Path to player schema */
    public static final String PLAYER_SCHEMA_PATH = SCHEMAS_PATH + "/player.schema.json";
    
    /** Path to user profile schema */
    public static final String USER_PROFILE_SCHEMA_PATH = SCHEMAS_PATH + "/user-profile.schema.json";

    // ========== Constructor ==========
    
    /**
     * Default constructor - no initialization required.
     */
    public TestFixtures() {
        // No initialization required
    }

    // ========== Schema Builder Utilities ==========

    /**
     * Creates a basic string schema.
     * 
     * @param name the schema name
     * @return a new Schema instance
     */
    public static Schema createStringSchema(String name) {
        return Schema.builder(name, SchemaType.STRING).build();
    }

    /**
     * Creates a basic integer schema.
     * 
     * @param name the schema name
     * @return a new Schema instance
     */
    public static Schema createIntegerSchema(String name) {
        return Schema.builder(name, SchemaType.INTEGER).build();
    }

    /**
     * Creates a basic number schema.
     * 
     * @param name the schema name
     * @return a new Schema instance
     */
    public static Schema createNumberSchema(String name) {
        return Schema.builder(name, SchemaType.NUMBER).build();
    }

    /**
     * Creates a basic boolean schema.
     * 
     * @param name the schema name
     * @return a new Schema instance
     */
    public static Schema createBooleanSchema(String name) {
        return Schema.builder(name, SchemaType.BOOLEAN).build();
    }

    /**
     * Creates a basic object schema.
     * 
     * @param name the schema name
     * @return a new Schema instance
     */
    public static Schema createObjectSchema(String name) {
        return Schema.builder(name, SchemaType.OBJECT).build();
    }

    /**
     * Creates a basic array schema.
     * 
     * @param name the schema name
     * @return a new Schema instance
     */
    public static Schema createArraySchema(String name) {
        return Schema.builder(name, SchemaType.ARRAY).build();
    }

    /**
     * Creates a basic null schema.
     * 
     * @param name the schema name
     * @return a new Schema instance
     */
    public static Schema createNullSchema(String name) {
        return Schema.builder(name, SchemaType.NULL).build();
    }

    /**
     * Creates a schema with minimum items constraint.
     * 
     * @param name the schema name
     * @param minItems the minimum number of items
     * @return a new Schema instance
     */
    public static Schema createMinItemsSchema(String name, int minItems) {
        return Schema.builder(name, SchemaType.ARRAY)
                .minItems(minItems)
                .build();
    }

    /**
     * Creates a schema with maximum items constraint.
     * 
     * @param name the schema name
     * @param maxItems the maximum number of items
     * @return a new Schema instance
     */
    public static Schema createMaxItemsSchema(String name, int maxItems) {
        return Schema.builder(name, SchemaType.ARRAY)
                .maxItems(maxItems)
                .build();
    }

    /**
     * Creates a schema with unique items constraint.
     * 
     * @param name the schema name
     * @param uniqueItems whether items must be unique
     * @return a new Schema instance
     */
    public static Schema createUniqueItemsSchema(String name, boolean uniqueItems) {
        return Schema.builder(name, SchemaType.ARRAY)
                .uniqueItems(uniqueItems)
                .build();
    }

    /**
     * Creates a schema with minLength constraint.
     * 
     * @param name the schema name
     * @param minLength the minimum string length
     * @return a new Schema instance
     */
    public static Schema createMinLengthSchema(String name, int minLength) {
        return Schema.builder(name, SchemaType.STRING)
                .minLength(minLength)
                .build();
    }

    /**
     * Creates a schema with maxLength constraint.
     * 
     * @param name the schema name
     * @param maxLength the maximum string length
     * @return a new Schema instance
     */
    public static Schema createMaxLengthSchema(String name, int maxLength) {
        return Schema.builder(name, SchemaType.STRING)
                .maxLength(maxLength)
                .build();
    }

    /**
     * Creates a schema with pattern constraint.
     * 
     * @param name the schema name
     * @param pattern the regex pattern
     * @return a new Schema instance
     */
    public static Schema createPatternSchema(String name, String pattern) {
        return Schema.builder(name, SchemaType.STRING)
                .pattern(pattern)
                .build();
    }

    /**
     * Creates a schema with format constraint.
     * 
     * @param name the schema name
     * @param format the format string (e.g., "email", "uri")
     * @return a new Schema instance
     */
    public static Schema createFormatSchema(String name, String format) {
        return Schema.builder(name, SchemaType.STRING)
                .format(format)
                .build();
    }

    /**
     * Creates a schema with minimum value constraint.
     * 
     * @param name the schema name
     * @param minimum the minimum value
     * @return a new Schema instance
     */
    public static Schema createMinimumSchema(String name, Number minimum) {
        return Schema.builder(name, SchemaType.NUMBER)
                .minimum(minimum)
                .build();
    }

    /**
     * Creates a schema with maximum value constraint.
     * 
     * @param name the schema name
     * @param maximum the maximum value
     * @return a new Schema instance
     */
    public static Schema createMaximumSchema(String name, Number maximum) {
        return Schema.builder(name, SchemaType.NUMBER)
                .maximum(maximum)
                .build();
    }

    /**
     * Creates a schema with enum constraint.
     * 
     * @param name the schema name
     * @param enumValues the allowed values
     * @return a new Schema instance
     */
    public static Schema createEnumSchema(String name, List<Object> enumValues) {
        return Schema.builder(name, SchemaType.STRING)
                .enumValues(enumValues)
                .build();
    }

    /**
     * Creates a schema with const value constraint.
     * 
     * @param name the schema name
     * @param constValue the constant value
     * @return a new Schema instance
     */
    public static Schema createConstSchema(String name, Object constValue) {
        return Schema.builder(name, SchemaType.STRING)
                .constValue(constValue)
                .build();
    }

    /**
     * Creates a schema with readOnly constraint.
     * 
     * @param name the schema name
     * @param readOnly whether the field is read-only
     * @return a new Schema instance
     */
    public static Schema createReadOnlySchema(String name, boolean readOnly) {
        return Schema.builder(name, SchemaType.STRING)
                .readOnly(readOnly)
                .build();
    }

    /**
     * Creates a schema with writeOnly constraint.
     * 
     * @param name the schema name
     * @param writeOnly whether the field is write-only
     * @return a new Schema instance
     */
    public static Schema createWriteOnlySchema(String name, boolean writeOnly) {
        return Schema.builder(name, SchemaType.STRING)
                .writeOnly(writeOnly)
                .build();
    }

    /**
     * Creates a schema with minProperties constraint.
     * 
     * @param name the schema name
     * @param minProperties the minimum number of properties
     * @return a new Schema instance
     */
    public static Schema createMinPropertiesSchema(String name, int minProperties) {
        return Schema.builder(name, SchemaType.OBJECT)
                .minProperties(minProperties)
                .build();
    }

    /**
     * Creates a schema with maxProperties constraint.
     * 
     * @param name the schema name
     * @param maxProperties the maximum number of properties
     * @return a new Schema instance
     */
    public static Schema createMaxPropertiesSchema(String name, int maxProperties) {
        return Schema.builder(name, SchemaType.OBJECT)
                .maxProperties(maxProperties)
                .build();
    }

    /**
     * Creates a schema with required fields.
     * 
     * @param name the schema name
     * @param requiredFields list of required field names
     * @return a new Schema instance
     */
    public static Schema createRequiredFieldsSchema(String name, List<String> requiredFields) {
        return Schema.builder(name, SchemaType.OBJECT)
                .requiredFields(requiredFields)
                .build();
    }

    /**
     * Creates a schema with properties.
     * 
     * @param name the schema name
     * @param properties map of property names to schemas
     * @return a new Schema instance
     */
    public static Schema createPropertiesSchema(String name, Map<String, Schema> properties) {
        return Schema.builder(name, SchemaType.OBJECT)
                .properties(properties)
                .build();
    }

    /**
     * Creates a schema with item schema for array validation.
     * 
     * @param name the schema name
     * @param itemSchema the schema for array items
     * @return a new Schema instance
     */
    public static Schema createItemSchema(String name, Schema itemSchema) {
        return Schema.builder(name, SchemaType.ARRAY)
                .itemSchema(itemSchema)
                .build();
    }

    /**
     * Creates a schema with prefix items for tuple validation.
     * 
     * @param name the schema name
     * @param prefixItems list of schemas for prefix items
     * @return a new Schema instance
     */
    public static Schema createPrefixItemsSchema(String name, List<Schema> prefixItems) {
        return Schema.builder(name, SchemaType.ARRAY)
                .prefixItems(prefixItems)
                .build();
    }

    /**
     * Creates a schema with additional items schema.
     * 
     * @param name the schema name
     * @param additionalItemsSchema schema for additional items
     * @return a new Schema instance
     */
    public static Schema createAdditionalItemsSchema(String name, Schema additionalItemsSchema) {
        return Schema.builder(name, SchemaType.ARRAY)
                .additionalItemsSchema(additionalItemsSchema)
                .build();
    }

    /**
     * Creates a schema with additional properties allowed.
     * 
     * @param name the schema name
     * @param allowAdditionalProperties whether to allow additional properties
     * @return a new Schema instance
     */
    public static Schema createAdditionalPropertiesSchema(String name, boolean allowAdditionalProperties) {
        return Schema.builder(name, SchemaType.OBJECT)
                .additionalProperties(allowAdditionalProperties)
                .build();
    }

    /**
     * Creates a schema with additional properties schema.
     * 
     * @param name the schema name
     * @param additionalPropertiesSchema schema for additional properties
     * @return a new Schema instance
     */
    public static Schema createAdditionalPropertiesSchema(String name, Schema additionalPropertiesSchema) {
        return Schema.builder(name, SchemaType.OBJECT)
                .additionalProperties(additionalPropertiesSchema)
                .build();
    }

    // ========== JsonNode Helper Methods ==========

    /**
     * Gets the ObjectMapper instance.
     * 
     * @return the shared ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    /**
     * Parses a JSON string into a JsonNode.
     * 
     * @param json the JSON string to parse
     * @return the parsed JsonNode
     * @throws JsonProcessingException if parsing fails
     */
    public static JsonNode parseJson(String json) throws JsonProcessingException {
        return OBJECT_MAPPER.readTree(json);
    }

    /**
     * Parses a JSON string into a JsonNode (unchecked).
     * 
     * @param json the JSON string to parse
     * @return the parsed JsonNode
     */
    public static JsonNode parseJsonUnchecked(String json) {
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON: " + json, e);
        }
    }

    /**
     * Creates a JsonNode from an object using Jackson's ObjectMapper.
     * 
     * @param object the object to convert
     * @return the JsonNode representation
     */
    public static JsonNode toJsonNode(Object object) {
        return OBJECT_MAPPER.valueToTree(object);
    }

    /**
     * Serializes a JsonNode to a JSON string.
     * 
     * @param node the JsonNode to serialize
     * @return the JSON string
     * @throws JsonProcessingException if serialization fails
     */
    public static String toJsonString(JsonNode node) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(node);
    }

    /**
     * Serializes a JsonNode to a JSON string (unchecked).
     * 
     * @param node the JsonNode to serialize
     * @return the JSON string
     */
    public static String toJsonStringUnchecked(JsonNode node) {
        try {
            return OBJECT_MAPPER.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize JSON", e);
        }
    }

    /**
     * Creates an empty JsonNode object ({}).
     * 
     * @return an empty object JsonNode
     */
    public static JsonNode createEmptyObjectNode() {
        return OBJECT_MAPPER.createObjectNode();
    }

    /**
     * Creates an empty JsonNode array ([]).
     * 
     * @return an empty array JsonNode
     */
    public static JsonNode createEmptyArrayNode() {
        return OBJECT_MAPPER.createArrayNode();
    }

    /**
     * Reads a JSON file from the classpath.
     * 
     * @param path the classpath resource path
     * @return the JsonNode from the file
     * @throws IOException if reading fails
     */
    public static JsonNode readJsonFromClasspath(String path) throws IOException {
        return OBJECT_MAPPER.readTree(TestFixtures.class.getClassLoader().getResourceAsStream(path));
    }

    /**
     * Reads a JSON file from the classpath (unchecked).
     * 
     * @param path the classpath resource path
     * @return the JsonNode from the file
     */
    public static JsonNode readJsonFromClasspathUnchecked(String path) {
        try {
            return OBJECT_MAPPER.readTree(TestFixtures.class.getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON from classpath: " + path, e);
        }
    }

    // ========== ValidationError Factory Methods ==========

    /**
     * Creates a ValidationError with all parameters.
     * 
     * @param nodePath the JSON path where the error occurred
     * @param expectedType the expected type
     * @param actualType the actual type found
     * @param description the error description
     * @return a new ValidationError instance
     */
    public static ValidationError createValidationError(String nodePath, String expectedType, 
                                                          String actualType, String description) {
        return new ValidationError(nodePath, expectedType, actualType, description);
    }

    /**
     * Creates a ValidationError with node path and description.
     * 
     * @param nodePath the JSON path where the error occurred
     * @param description the error description
     * @return a new ValidationError instance
     */
    public static ValidationError createValidationError(String nodePath, String description) {
        return new ValidationError(nodePath, null, null, description);
    }

    /**
     * Creates a ValidationError for type mismatch.
     * 
     * @param nodePath the JSON path where the error occurred
     * @param expectedType the expected type
     * @param actualType the actual type found
     * @return a new ValidationError instance
     */
    public static ValidationError createTypeError(String nodePath, String expectedType, String actualType) {
        return new ValidationError(nodePath, expectedType, actualType, 
                "Expected " + expectedType + " but got " + actualType + " at " + nodePath);
    }

    /**
     * Creates a ValidationError for missing required field.
     * 
     * @param nodePath the JSON path where the error occurred
     * @param fieldName the missing field name
     * @return a new ValidationError instance
     */
    public static ValidationError createRequiredFieldError(String nodePath, String fieldName) {
        return new ValidationError(nodePath, "required", "missing", 
                "Required field '" + fieldName + "' is missing at " + nodePath);
    }

    /**
     * Creates a ValidationError for minimum items violation.
     * 
     * @param nodePath the JSON path where the error occurred
     * @param minItems the minimum required items
     * @param actualItems the actual number of items
     * @return a new ValidationError instance
     */
    public static ValidationError createMinItemsError(String nodePath, int minItems, int actualItems) {
        return new ValidationError(nodePath, "array", "array", 
                "Array must have at least " + minItems + " items, but has " + actualItems + " at " + nodePath);
    }

    /**
     * Creates a ValidationError for maximum items violation.
     * 
     * @param nodePath the JSON path where the error occurred
     * @param maxItems the maximum allowed items
     * @param actualItems the actual number of items
     * @return a new ValidationError instance
     */
    public static ValidationError createMaxItemsError(String nodePath, int maxItems, int actualItems) {
        return new ValidationError(nodePath, "array", "array", 
                "Array must have at most " + maxItems + " items, but has " + actualItems + " at " + nodePath);
    }

    /**
     * Creates a ValidationError for unique items violation.
     * 
     * @param nodePath the JSON path where the error occurred
     * @return a new ValidationError instance
     */
    public static ValidationError createUniqueItemsError(String nodePath) {
        return new ValidationError(nodePath, "array", "array", 
                "Array items must be unique at " + nodePath);
    }

    /**
     * Creates a ValidationError for minimum length violation.
     * 
     * @param nodePath the JSON path where the error occurred
     * @param minLength the minimum required length
     * @param actualLength the actual length
     * @return a new ValidationError instance
     */
    public static ValidationError createMinLengthError(String nodePath, int minLength, int actualLength) {
        return new ValidationError(nodePath, "string", "string", 
                "String must have at least " + minLength + " characters, but has " + actualLength + " at " + nodePath);
    }

    /**
     * Creates a ValidationError for maximum length violation.
     * 
     * @param nodePath the JSON path where the error occurred
     * @param maxLength the maximum allowed length
     * @param actualLength the actual length
     * @return a new ValidationError instance
     */
    public static ValidationError createMaxLengthError(String nodePath, int maxLength, int actualLength) {
        return new ValidationError(nodePath, "string", "string", 
                "String must have at most " + maxLength + " characters, but has " + actualLength + " at " + nodePath);
    }

    /**
     * Creates a ValidationError for pattern mismatch.
     * 
     * @param nodePath the JSON path where the error occurred
     * @param pattern the expected pattern
     * @return a new ValidationError instance
     */
    public static ValidationError createPatternError(String nodePath, String pattern) {
        return new ValidationError(nodePath, "string", "string", 
                "String must match pattern '" + pattern + "' at " + nodePath);
    }

    /**
     * Creates a ValidationError for format violation.
     * 
     * @param nodePath the JSON path where the error occurred
     * @param format the expected format
     * @return a new ValidationError instance
     */
    public static ValidationError createFormatError(String nodePath, String format) {
        return new ValidationError(nodePath, "string", "string", 
                "String must be a valid " + format + " format at " + nodePath);
    }

    /**
     * Creates a ValidationError for minimum value violation.
     * 
     * @param nodePath the JSON path where the error occurred
     * @param minimum the minimum required value
     * @param actual the actual value
     * @return a new ValidationError instance
     */
    public static ValidationError createMinimumError(String nodePath, Number minimum, Number actual) {
        return new ValidationError(nodePath, "number", "number", 
                "Value must be at least " + minimum + ", but is " + actual + " at " + nodePath);
    }

    /**
     * Creates a ValidationError for maximum value violation.
     * 
     * @param nodePath the JSON path where the error occurred
     * @param maximum the maximum allowed value
     * @param actual the actual value
     * @return a new ValidationError instance
     */
    public static ValidationError createMaximumError(String nodePath, Number maximum, Number actual) {
        return new ValidationError(nodePath, "number", "number", 
                "Value must be at most " + maximum + ", but is " + actual + " at " + nodePath);
    }

    /**
     * Creates a ValidationError for enum violation.
     * 
     * @param nodePath the JSON path where the error occurred
     * @param value the invalid value
     * @return a new ValidationError instance
     */
    public static ValidationError createEnumError(String nodePath, Object value) {
        return new ValidationError(nodePath, "enum", "string", 
                "Value '" + value + "' is not in the allowed enum values at " + nodePath);
    }

    /**
     * Creates a ValidationError for const violation.
     * 
     * @param nodePath the JSON path where the error occurred
     * @param constValue the expected constant value
     * @return a new ValidationError instance
     */
    public static ValidationError createConstError(String nodePath, Object constValue) {
        return new ValidationError(nodePath, "const", "any", 
                "Value must be " + constValue + " at " + nodePath);
    }

    /**
     * Creates a ValidationError for minProperties violation.
     * 
     * @param nodePath the JSON path where the error occurred
     * @param minProperties the minimum required properties
     * @param actualProperties the actual number of properties
     * @return a new ValidationError instance
     */
    public static ValidationError createMinPropertiesError(String nodePath, int minProperties, int actualProperties) {
        return new ValidationError(nodePath, "object", "object", 
                "Object must have at least " + minProperties + " properties, but has " + actualProperties + " at " + nodePath);
    }

    /**
     * Creates a ValidationError for maxProperties violation.
     * 
     * @param nodePath the JSON path where the error occurred
     * @param maxProperties the maximum allowed properties
     * @param actualProperties the actual number of properties
     * @return a new ValidationError instance
     */
    public static ValidationError createMaxPropertiesError(String nodePath, int maxProperties, int actualProperties) {
        return new ValidationError(nodePath, "object", "object", 
                "Object must have at most " + maxProperties + " properties, but has " + actualProperties + " at " + nodePath);
    }

    /**
     * Creates a ValidationError for additional properties violation.
     * 
     * @param nodePath the JSON path where the error occurred
     * @param propertyName the disallowed property name
     * @return a new ValidationError instance
     */
    public static ValidationError createAdditionalPropertiesError(String nodePath, String propertyName) {
        return new ValidationError(nodePath, "object", "object", 
                "Additional property '" + propertyName + "' is not allowed at " + nodePath);
    }
}
