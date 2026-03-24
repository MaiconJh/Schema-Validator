package com.maiconjh.schemacr.validation.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaType;
import com.maiconjh.schemacr.validation.ValidationError;

/**
 * Unit tests for WriteOnlyValidator.
 * 
 * <p>Tests the writeOnly JSON Schema keyword validation logic.</p>
 */
@DisplayName("WriteOnlyValidator Tests")
class WriteOnlyValidatorTest {

    private WriteOnlyValidator validator;
    private Schema schema;

    @BeforeEach
    void setUp() {
        // No setup needed - validator is created per test
    }

    // ========== POSITIVE TESTS (Valid inputs) ==========

    @Test
    @DisplayName("shouldPass_whenWriteOnlyIsFalseAndValueIsPresent")
    void shouldPass_whenWriteOnlyIsFalseAndValueIsPresent() {
        // Arrange
        // Schema: writeOnly=false
        // Valid: value is present but writeOnly is false - should allow
        
        schema = Schema.builder("password", SchemaType.STRING)
                .writeOnly(false)
                .build();
        
        validator = new WriteOnlyValidator(false);
        
        // Value is present with writeOnly=false
        Object data = "someValue";

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/password", "password");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when writeOnly is false and value is present");
    }

    @Test
    @DisplayName("shouldPass_whenWriteOnlySchemaNotSet")
    void shouldPass_whenWriteOnlySchemaNotSet() {
        // Arrange
        // Schema: writeOnly not configured
        // Valid: no writeOnly constraint set - should allow any value
        
        schema = Schema.builder("name", SchemaType.STRING)
                .build();
        
        // When writeOnly is not set, the validator should be created with false
        // So we assume writeOnly=false when not set
        validator = new WriteOnlyValidator(false);
        
        // Any value should be allowed
        Object data = "anyValue";

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/name", "name");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when writeOnly is not set in schema");
    }

    // ========== NEGATIVE TESTS (Invalid inputs) ==========

    @Test
    @DisplayName("shouldFail_whenWriteOnlyIsTrueAndValueIsPresent")
    void shouldFail_whenWriteOnlyIsTrueAndValueIsPresent() {
        // Arrange
        // Schema: writeOnly=true
        // Invalid: value is present when writeOnly is true
        
        schema = Schema.builder("password", SchemaType.STRING)
                .writeOnly(true)
                .build();
        
        validator = new WriteOnlyValidator(true);
        
        // Value is present - violates writeOnly
        Object data = "secretPassword";

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/password", "password");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors when writeOnly is true and value is present");
        assertEquals(1, errors.size(), "Expected exactly one error");
        
        ValidationError error = errors.get(0);
        assertEquals("writeOnly", error.getExpectedType(), "Expected keyword to be 'writeOnly'");
        assertEquals("secretPassword", error.getActualType(), "Expected actual type to be the provided data");
    }

    // ========== EDGE CASES (Boundary conditions) ==========

    @Test
    @DisplayName("shouldPass_whenWriteOnlyIsTrueAndValueIsNull")
    void shouldPass_whenWriteOnlyIsTrueAndValueIsNull() {
        // Arrange
        // Schema: writeOnly=true
        // Edge: value is null (null is not "presence")
        
        schema = Schema.builder("secretToken", SchemaType.STRING)
                .writeOnly(true)
                .build();
        
        validator = new WriteOnlyValidator(true);
        
        // Null value - not considered "present"
        Object data = null;

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/secretToken", "secretToken");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when writeOnly is true and value is null");
    }

    @Test
    @DisplayName("shouldPass_whenWriteOnlyIsTrueAndValueIsMissing")
    void shouldPass_whenWriteOnlyIsTrueAndValueIsMissing() {
        // Arrange
        // Schema: writeOnly=true
        // Edge: value is missing/not sent
        
        schema = Schema.builder("internalCode", SchemaType.STRING)
                .writeOnly(true)
                .build();
        
        validator = new WriteOnlyValidator(true);
        
        // Missing value - simulated by null (property not sent)
        Object data = null;

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/internalCode", "internalCode");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when writeOnly is true and value is missing");
    }

    @Test
    @DisplayName("shouldPass_whenWriteOnlyWithEmptyValue")
    void shouldPass_whenWriteOnlyWithEmptyValue() {
        // Arrange
        // Schema: writeOnly=false
        // Edge: value is empty string
        
        schema = Schema.builder("description", SchemaType.STRING)
                .writeOnly(false)
                .build();
        
        validator = new WriteOnlyValidator(false);
        
        // Empty value with writeOnly=false
        Object data = "";

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/description", "description");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when writeOnly is false and value is empty");
    }
}