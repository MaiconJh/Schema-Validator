package com.maiconjh.schemacr.validation.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaType;
import com.maiconjh.schemacr.validation.ValidationError;

/**
 * Unit tests for ConstValidator.
 * 
 * <p>Tests the const JSON Schema keyword validation logic.</p>
 */
@DisplayName("ConstValidator Tests")
class ConstValidatorTest {

    private ConstValidator validator;
    private Schema schema;

    @BeforeEach
    void setUp() {
        // No setup needed - validator is created per test
    }

    // ========== POSITIVE TESTS (Valid inputs) ==========

    @Test
    @DisplayName("shouldPass_whenValueMatchesConst")
    void shouldPass_whenValueMatchesConst() {
        // Arrange
        // Schema: const: "active"
        // Valid: value equals exactly "active"
        Object constValue = "active";
        
        schema = Schema.builder("status", SchemaType.STRING)
                .constValue(constValue)
                .build();
        
        validator = new ConstValidator(constValue);
        
        // Value matches const exactly
        Object data = "active";

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/status", "status");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when value matches const");
    }

    @Test
    @DisplayName("shouldPass_whenConstIsStringAndValueMatches")
    void shouldPass_whenConstIsStringAndValueMatches() {
        // Arrange
        // Schema: const: "hello"
        // Valid: string value matches
        Object constValue = "hello";
        
        schema = Schema.builder("greeting", SchemaType.STRING)
                .constValue(constValue)
                .build();
        
        validator = new ConstValidator(constValue);
        
        // String value matches
        Object data = "hello";

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/greeting", "greeting");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when const is string and value matches");
    }

    @Test
    @DisplayName("shouldPass_whenConstIsNumberAndValueMatches")
    void shouldPass_whenConstIsNumberAndValueMatches() {
        // Arrange
        // Schema: const: 42
        // Valid: numeric value matches
        Object constValue = 42;
        
        schema = Schema.builder("answer", SchemaType.NUMBER)
                .constValue(constValue)
                .build();
        
        validator = new ConstValidator(constValue);
        
        // Numeric value matches
        Object data = 42;

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/answer", "answer");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when const is number and value matches");
    }

    // ========== NEGATIVE TESTS (Invalid inputs) ==========

    @Test
    @DisplayName("shouldFail_whenValueDoesNotMatchConst")
    void shouldFail_whenValueDoesNotMatchConst() {
        // Arrange
        // Schema: const: "active"
        // Invalid: value is different
        Object constValue = "active";
        
        schema = Schema.builder("status", SchemaType.STRING)
                .constValue(constValue)
                .build();
        
        validator = new ConstValidator(constValue);
        
        // Value does not match const
        Object data = "inactive";

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/status", "status");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors when value does not match const");
        assertEquals(1, errors.size(), "Expected exactly one error");
        
        ValidationError error = errors.get(0);
        assertEquals("const", error.getExpectedType(), "Expected keyword to be 'const'");
    }

    @Test
    @DisplayName("shouldFail_whenConstIsIntegerButValueIsString")
    void shouldFail_whenConstIsIntegerButValueIsString() {
        // Arrange
        // Schema: const: 42 (integer)
        // Invalid: value is string "42"
        Object constValue = 42;
        
        schema = Schema.builder("answer", SchemaType.NUMBER)
                .constValue(constValue)
                .build();
        
        validator = new ConstValidator(constValue);
        
        // String value instead of integer - different type
        Object data = "42";

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/answer", "answer");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors when const is integer but value is string");
        assertEquals(1, errors.size(), "Expected exactly one error");
        
        ValidationError error = errors.get(0);
        assertEquals("const", error.getExpectedType(), "Expected keyword to be 'const'");
    }

    // ========== EDGE CASES (Boundary conditions) ==========

    @Test
    @DisplayName("shouldPass_whenConstIsNullAndValueIsNull")
    void shouldPass_whenConstIsNullAndValueIsNull() {
        // Arrange
        // Schema: const: null
        // Valid: value is null
        Object constValue = null;
        
        schema = Schema.builder("nullableField", SchemaType.NULL)
                .constValue(constValue)
                .build();
        
        validator = new ConstValidator(constValue);
        
        // Null value matches null const
        Object data = null;

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/nullableField", "nullableField");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when const is null and value is null");
    }

    @Test
    @DisplayName("shouldPass_whenConstIsEmptyObjectAndValueMatches")
    void shouldPass_whenConstIsEmptyObjectAndValueMatches() {
        // Arrange
        // Schema: const: {}
        // Valid: empty object matches
        Object constValue = Collections.emptyMap();
        
        schema = Schema.builder("config", SchemaType.OBJECT)
                .constValue(constValue)
                .build();
        
        validator = new ConstValidator(constValue);
        
        // Empty object matches const
        Map<String, Object> data = new HashMap<>();

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/config", "config");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when const is empty object and value matches");
    }

    @Test
    @DisplayName("shouldPass_whenConstIsEmptyArrayAndValueMatches")
    void shouldPass_whenConstIsEmptyArrayAndValueMatches() {
        // Arrange
        // Schema: const: []
        // Valid: empty array matches
        Object constValue = Collections.emptyList();
        
        schema = Schema.builder("items", SchemaType.ARRAY)
                .constValue(constValue)
                .build();
        
        validator = new ConstValidator(constValue);
        
        // Empty array matches const
        List<Object> data = Collections.emptyList();

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when const is empty array and value matches");
    }

    @Test
    @DisplayName("shouldPass_whenConstIsBooleanTrueAndValueMatches")
    void shouldPass_whenConstIsBooleanTrueAndValueMatches() {
        // Arrange
        // Schema: const: true
        // Valid: boolean true matches
        Object constValue = true;
        
        schema = Schema.builder("enabled", SchemaType.BOOLEAN)
                .constValue(constValue)
                .build();
        
        validator = new ConstValidator(constValue);
        
        // Boolean true matches const
        Object data = true;

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/enabled", "enabled");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when const is boolean true and value matches");
    }

    @Test
    @DisplayName("shouldFail_whenInputIsNotArrayOrObject")
    void shouldFail_whenInputIsNotArrayOrObject() {
        // Arrange
        // Schema: const: {"key": "value"} (object)
        // Invalid: input is not array or object - but const comparison still applies
        Map<String, Object> constMap = new HashMap<>();
        constMap.put("key", "value");
        Object constValue = constMap;
        
        schema = Schema.builder("data", SchemaType.OBJECT)
                .constValue(constValue)
                .build();
        
        validator = new ConstValidator(constValue);
        
        // Non-object input - should fail const comparison
        Object data = "not an object";

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors when input is not array or object");
        assertEquals(1, errors.size(), "Expected exactly one error");
        
        ValidationError error = errors.get(0);
        assertEquals("const", error.getExpectedType(), "Expected keyword to be 'const'");
    }
}
