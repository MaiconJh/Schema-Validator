package com.maiconjh.schemacr.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaType;

/**
 * Unit tests for ObjectValidator.
 * 
 * <p>Tests the object validation logic including required properties,
 * additional properties, nested objects, and property types.</p>
 */
@DisplayName("ObjectValidator Tests")
class ObjectValidatorTest {

    private ObjectValidator validator;
    private Schema schema;

    @BeforeEach
    void setUp() {
        validator = new ObjectValidator();
    }

    // ========== POSITIVE TESTS (Valid inputs) ==========

    @Test
    @DisplayName("shouldPass_whenAllRequiredPropertiesPresent")
    void shouldPass_whenAllRequiredPropertiesPresent() {
        // Arrange
        // Schema: type object with required properties "name" and "age"
        // Valid: all required properties are present
        
        Map<String, Schema> properties = new HashMap<>();
        properties.put("name", Schema.builder("name", SchemaType.STRING).build());
        properties.put("age", Schema.builder("age", SchemaType.INTEGER).build());
        
        schema = Schema.builder("user", SchemaType.OBJECT)
                .properties(properties)
                .requiredFields(List.of("name", "age"))
                .build();
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");
        data.put("age", 30);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/user", "user");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when all required properties are present");
    }

    @Test
    @DisplayName("shouldPass_whenOptionalPropertiesMissing")
    void shouldPass_whenOptionalPropertiesMissing() {
        // Arrange
        // Schema: type object with required "name" and optional "email"
        // Valid: optional property is missing
        
        Map<String, Schema> properties = new HashMap<>();
        properties.put("name", Schema.builder("name", SchemaType.STRING).build());
        properties.put("email", Schema.builder("email", SchemaType.STRING).build());
        
        schema = Schema.builder("user", SchemaType.OBJECT)
                .properties(properties)
                .requiredFields(List.of("name"))
                .build();
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/user", "user");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when optional properties are missing");
    }

    @Test
    @DisplayName("shouldPass_whenAdditionalPropertiesAllowed")
    void shouldPass_whenAdditionalPropertiesAllowed() {
        // Arrange
        // Schema: type object with additionalProperties allowed (default)
        // Valid: extra properties are allowed
        
        Map<String, Schema> properties = new HashMap<>();
        properties.put("name", Schema.builder("name", SchemaType.STRING).build());
        
        schema = Schema.builder("user", SchemaType.OBJECT)
                .properties(properties)
                .additionalProperties(true) // Default, but explicit
                .build();
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");
        data.put("extraField", "extra value");

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/user", "user");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when additional properties are allowed");
    }

    // ========== NEGATIVE TESTS (Invalid inputs) ==========

    @Test
    @DisplayName("shouldFail_whenRequiredPropertyMissing")
    void shouldFail_whenRequiredPropertyMissing() {
        // Arrange
        // Schema: type object with required "name" and "age"
        // Invalid: required property "age" is missing
        
        Map<String, Schema> properties = new HashMap<>();
        properties.put("name", Schema.builder("name", SchemaType.STRING).build());
        properties.put("age", Schema.builder("age", SchemaType.INTEGER).build());
        
        schema = Schema.builder("user", SchemaType.OBJECT)
                .properties(properties)
                .requiredFields(List.of("name", "age"))
                .build();
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/user", "user");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors when required property is missing");
        assertTrue(errors.stream().anyMatch(e -> e.getDescription().contains("Required field 'age' is missing")),
                "Expected error about missing required field 'age'");
    }

    @Test
    @DisplayName("shouldFail_whenAdditionalPropertiesNotAllowed")
    void shouldFail_whenAdditionalPropertiesNotAllowed() {
        // Arrange
        // Schema: type object with additionalProperties = false
        // Invalid: unknown property is present
        
        Map<String, Schema> properties = new HashMap<>();
        properties.put("name", Schema.builder("name", SchemaType.STRING).build());
        
        schema = Schema.builder("user", SchemaType.OBJECT)
                .properties(properties)
                .additionalProperties(false)
                .build();
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");
        data.put("unknownField", "extra value");

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/user", "user");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors when additional properties are not allowed");
        assertTrue(errors.stream().anyMatch(e -> e.getDescription().contains("is not allowed")),
                "Expected error about unknown field not being allowed");
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    @DisplayName("shouldPass_whenObjectIsEmptyAndNoRequired")
    void shouldPass_whenObjectIsEmptyAndNoRequired() {
        // Arrange
        // Schema: type object with no required fields
        // Valid: empty object is allowed
        
        schema = Schema.builder("user", SchemaType.OBJECT)
                .build();
        
        Map<String, Object> data = new HashMap<>();

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/user", "user");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when object is empty and no required fields");
    }

    @Test
    @DisplayName("shouldHandleNestedObjects")
    void shouldHandleNestedObjects() {
        // Arrange
        // Schema: type object with nested object property
        // Valid: nested object validates correctly
        
        Map<String, Schema> addressProperties = new HashMap<>();
        addressProperties.put("street", Schema.builder("street", SchemaType.STRING).build());
        addressProperties.put("city", Schema.builder("city", SchemaType.STRING).build());
        
        Schema addressSchema = Schema.builder("address", SchemaType.OBJECT)
                .properties(addressProperties)
                .requiredFields(List.of("city"))
                .build();
        
        Map<String, Schema> userProperties = new HashMap<>();
        userProperties.put("name", Schema.builder("name", SchemaType.STRING).build());
        userProperties.put("address", addressSchema);
        
        schema = Schema.builder("user", SchemaType.OBJECT)
                .properties(userProperties)
                .requiredFields(List.of("name", "address"))
                .build();
        
        Map<String, Object> addressData = new HashMap<>();
        addressData.put("street", "Main St");
        addressData.put("city", "New York");
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");
        data.put("address", addressData);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/user", "user");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for valid nested objects");
    }

    @Test
    @DisplayName("shouldValidatePropertyTypes")
    void shouldValidatePropertyTypes() {
        // Arrange
        // Schema: type object with typed properties
        // Valid: properties with correct types pass validation
        
        Map<String, Schema> properties = new HashMap<>();
        properties.put("name", Schema.builder("name", SchemaType.STRING).build());
        properties.put("age", Schema.builder("age", SchemaType.INTEGER).build());
        properties.put("active", Schema.builder("active", SchemaType.BOOLEAN).build());
        
        schema = Schema.builder("user", SchemaType.OBJECT)
                .properties(properties)
                .requiredFields(List.of("name", "age", "active"))
                .build();
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");
        data.put("age", 30);
        data.put("active", true);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/user", "user");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when property types match");
    }

    // ========== ADDITIONAL EDGE CASES ==========

    @Test
    @DisplayName("shouldFail_whenPropertyTypeMismatch")
    void shouldFail_whenPropertyTypeMismatch() {
        // Arrange
        // Schema: type object with typed property "age" as INTEGER
        // Invalid: "age" is a string instead of integer
        
        Map<String, Schema> properties = new HashMap<>();
        properties.put("name", Schema.builder("name", SchemaType.STRING).build());
        properties.put("age", Schema.builder("age", SchemaType.INTEGER).build());
        
        schema = Schema.builder("user", SchemaType.OBJECT)
                .properties(properties)
                .requiredFields(List.of("age"))
                .build();
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");
        data.put("age", "not an integer");

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/user", "user");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors when property type mismatch");
    }

    @Test
    @DisplayName("shouldPass_whenAdditionalPropertiesHasSchema")
    void shouldPass_whenAdditionalPropertiesHasSchema() {
        // Arrange
        // Schema: type object with additionalProperties as Schema (allows any string)
        // Valid: extra property that matches the additionalProperties schema
        
        Map<String, Schema> properties = new HashMap<>();
        properties.put("name", Schema.builder("name", SchemaType.STRING).build());
        
        Schema additionalPropsSchema = Schema.builder("additional", SchemaType.STRING).build();
        
        schema = Schema.builder("user", SchemaType.OBJECT)
                .properties(properties)
                .additionalProperties(additionalPropsSchema)
                .build();
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");
        data.put("extraField", "extra string value");

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/user", "user");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when additional property matches schema");
    }

    @Test
    @DisplayName("shouldFail_whenAdditionalPropertyFailsSchemaValidation")
    void shouldFail_whenAdditionalPropertyFailsSchemaValidation() {
        // Arrange
        // Schema: type object with additionalProperties as INTEGER schema
        // Invalid: extra property is a string, not integer
        
        Map<String, Schema> properties = new HashMap<>();
        properties.put("name", Schema.builder("name", SchemaType.STRING).build());
        
        Schema additionalPropsSchema = Schema.builder("additional", SchemaType.INTEGER).build();
        
        schema = Schema.builder("user", SchemaType.OBJECT)
                .properties(properties)
                .additionalProperties(additionalPropsSchema)
                .build();
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");
        data.put("extraField", "not an integer");

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/user", "user");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors when additional property fails schema validation");
    }

    @Test
    @DisplayName("shouldHandleNullData")
    void shouldHandleNullData() {
        // Arrange
        // Schema: type object
        // Current behavior: null is acceptable for optional fields
        
        schema = Schema.builder("user", SchemaType.OBJECT)
                .build();
        
        Object data = null;

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/user", "user");

        // Assert - Current behavior returns no errors for null (acceptable for optional fields)
        assertTrue(errors.isEmpty(), "Expected no validation error when data is null for object type (current behavior)");
    }

    @Test
    @DisplayName("shouldHandleListDataAsObject")
    void shouldHandleListDataAsObject() {
        // Arrange
        // Schema: type object
        // Current behavior: List data is handled gracefully (skipped)
        
        schema = Schema.builder("user", SchemaType.OBJECT)
                .build();
        
        Object data = List.of("item1", "item2");

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/user", "user");

        // Assert - Current behavior returns no errors for List (handled by ArrayValidator)
        assertTrue(errors.isEmpty(), "Expected no validation error when data is a list (current behavior)");
    }

    @Test
    @DisplayName("shouldPass_whenPropertyNamesMatchConstraint")
    void shouldPass_whenPropertyNamesMatchConstraint() {
        Schema propertyNamesSchema = Schema.builder("propertyNames", SchemaType.STRING)
                .pattern("^[a-z_]+$")
                .build();

        schema = Schema.builder("user", SchemaType.OBJECT)
                .propertyNamesSchema(propertyNamesSchema)
                .build();

        Map<String, Object> data = new HashMap<>();
        data.put("first_name", "John");
        data.put("last_name", "Doe");

        List<ValidationError> errors = validator.validate(data, schema, "/user", "user");

        assertTrue(errors.isEmpty(), "Expected no validation errors when all property names match pattern");
    }

    @Test
    @DisplayName("shouldFail_whenPropertyNameViolatesConstraint")
    void shouldFail_whenPropertyNameViolatesConstraint() {
        Schema propertyNamesSchema = Schema.builder("propertyNames", SchemaType.STRING)
                .pattern("^[a-z_]+$")
                .build();

        schema = Schema.builder("user", SchemaType.OBJECT)
                .propertyNamesSchema(propertyNamesSchema)
                .build();

        Map<String, Object> data = new HashMap<>();
        data.put("Invalid-Name", "John");

        List<ValidationError> errors = validator.validate(data, schema, "/user", "user");

        assertFalse(errors.isEmpty(), "Expected validation errors when a property name is invalid");
        assertTrue(errors.stream().anyMatch(e -> e.getNodePath().contains("Invalid-Name")),
                "Expected error path to include invalid property name");
    }
}
