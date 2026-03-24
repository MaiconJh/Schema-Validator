package com.maiconjh.schemacr.validation.array;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaType;
import com.maiconjh.schemacr.validation.ValidationError;

/**
 * Unit tests for MinItemsValidator.
 * 
 * <p>Tests the minItems JSON Schema keyword validation logic.</p>
 */
@DisplayName("MinItemsValidator Tests")
class MinItemsValidatorTest {

    private MinItemsValidator validator;

    @BeforeEach
    void setUp() {
        // No setup needed - validator is created per test
    }

    // ========== POSITIVE TESTS (Valid inputs) ==========

    @Test
    @DisplayName("shouldPass_whenArrayHasExactMinItems")
    void shouldPass_whenArrayHasExactMinItems() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .minItems(3)
                .build();
        validator = new MinItemsValidator(3);
        List<Object> data = Arrays.asList(1, 2, 3);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for array with exact minItems");
    }

    @Test
    @DisplayName("shouldPass_whenArrayExceedsMinItems")
    void shouldPass_whenArrayExceedsMinItems() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .minItems(3)
                .build();
        validator = new MinItemsValidator(3);
        List<Object> data = Arrays.asList(1, 2, 3, 4, 5);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for array exceeding minItems");
    }

    // ========== NEGATIVE TESTS (Invalid inputs) ==========

    @Test
    @DisplayName("shouldFail_whenArrayHasFewerItemsThanMin")
    void shouldFail_whenArrayHasFewerItemsThanMin() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .minItems(3)
                .build();
        validator = new MinItemsValidator(3);
        List<Object> data = Arrays.asList(1, 2);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors for array with fewer items than minItems");
        assertEquals(1, errors.size(), "Expected exactly one error");
        
        ValidationError error = errors.get(0);
        assertEquals("minItems", error.getExpectedType(), "Expected keyword to be 'minItems'");
        assertTrue(error.getDescription().contains("at least"), "Expected message to contain 'at least'");
    }

    @Test
    @DisplayName("shouldFail_whenArrayIsEmpty")
    void shouldFail_whenArrayIsEmpty() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .minItems(3)
                .build();
        validator = new MinItemsValidator(3);
        List<Object> data = Arrays.asList();

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors for empty array when minItems > 0");
        assertEquals(1, errors.size(), "Expected exactly one error");
        
        ValidationError error = errors.get(0);
        assertEquals("minItems", error.getExpectedType(), "Expected keyword to be 'minItems'");
        assertTrue(error.getDescription().contains("at least"), "Expected message to contain 'at least'");
    }

    // ========== EDGE CASES ==========

    @Test
    @DisplayName("shouldPass_whenMinItemsIsZero")
    void shouldPass_whenMinItemsIsZero() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .minItems(0)
                .build();
        validator = new MinItemsValidator(0);
        List<Object> data = Arrays.asList();

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for empty array when minItems = 0");
    }

    @Test
    @DisplayName("shouldPass_whenMinItemsIsOne")
    void shouldPass_whenMinItemsIsOne() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .minItems(1)
                .build();
        validator = new MinItemsValidator(1);
        List<Object> data = Arrays.asList(1);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for array with exactly one item when minItems = 1");
    }

    @Test
    @DisplayName("shouldFail_whenInputIsNotArray")
    void shouldFail_whenInputIsNotArray() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .minItems(3)
                .build();
        validator = new MinItemsValidator(3);
        Object data = "not an array";

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for non-array input (type mismatch handled elsewhere)");
    }
}