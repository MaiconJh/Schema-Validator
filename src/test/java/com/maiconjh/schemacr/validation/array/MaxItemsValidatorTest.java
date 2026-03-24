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
 * Unit tests for MaxItemsValidator.
 * 
 * <p>Tests the maxItems JSON Schema keyword validation logic.</p>
 */
@DisplayName("MaxItemsValidator Tests")
class MaxItemsValidatorTest {

    private MaxItemsValidator validator;

    @BeforeEach
    void setUp() {
        // No setup needed - validator is created per test
    }

    // ========== POSITIVE TESTS (Valid inputs) ==========

    @Test
    @DisplayName("shouldPass_whenArrayHasExactMaxItems")
    void shouldPass_whenArrayHasExactMaxItems() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .maxItems(3)
                .build();
        validator = new MaxItemsValidator(3);
        List<Object> data = Arrays.asList(1, 2, 3);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for array with exact maxItems");
    }

    @Test
    @DisplayName("shouldPass_whenArrayHasFewerItemsThanMax")
    void shouldPass_whenArrayHasFewerItemsThanMax() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .maxItems(3)
                .build();
        validator = new MaxItemsValidator(3);
        List<Object> data = Arrays.asList(1, 2);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for array with fewer items than maxItems");
    }

    // ========== NEGATIVE TESTS (Invalid inputs) ==========

    @Test
    @DisplayName("shouldFail_whenArrayExceedsMaxItems")
    void shouldFail_whenArrayExceedsMaxItems() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .maxItems(3)
                .build();
        validator = new MaxItemsValidator(3);
        List<Object> data = Arrays.asList(1, 2, 3, 4);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors for array exceeding maxItems");
        assertEquals(1, errors.size(), "Expected exactly one error");
        
        ValidationError error = errors.get(0);
        assertEquals("maxItems", error.getExpectedType(), "Expected keyword to be 'maxItems'");
        assertTrue(error.getDescription().contains("at most"), "Expected message to contain 'at most'");
    }

    @Test
    @DisplayName("shouldFail_whenMaxItemsIsZero")
    void shouldFail_whenMaxItemsIsZero() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .maxItems(0)
                .build();
        validator = new MaxItemsValidator(0);
        List<Object> data = Arrays.asList(1);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors for non-empty array when maxItems = 0");
        assertEquals(1, errors.size(), "Expected exactly one error");
        
        ValidationError error = errors.get(0);
        assertEquals("maxItems", error.getExpectedType(), "Expected keyword to be 'maxItems'");
        assertTrue(error.getDescription().contains("at most"), "Expected message to contain 'at most'");
    }

    // ========== EDGE CASES ==========

    @Test
    @DisplayName("shouldPass_whenArrayIsEmpty")
    void shouldPass_whenArrayIsEmpty() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .maxItems(3)
                .build();
        validator = new MaxItemsValidator(3);
        List<Object> data = Arrays.asList();

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for empty array regardless of maxItems");
    }

    @Test
    @DisplayName("shouldPass_whenMaxItemsIsLarge")
    void shouldPass_whenMaxItemsIsLarge() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .maxItems(1000000)
                .build();
        validator = new MaxItemsValidator(1000000);
        List<Object> data = Arrays.asList(1, 2, 3, 4, 5);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for array well below large maxItems");
    }

    @Test
    @DisplayName("shouldFail_whenInputIsNotArray")
    void shouldFail_whenInputIsNotArray() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .maxItems(3)
                .build();
        validator = new MaxItemsValidator(3);
        Object data = "not an array";

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for non-array input (type mismatch handled elsewhere)");
    }
}