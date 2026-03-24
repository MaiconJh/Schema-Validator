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
 * Unit tests for UniqueItemsValidator.
 * 
 * <p>Tests the uniqueItems JSON Schema keyword validation logic.</p>
 */
@DisplayName("UniqueItemsValidator Tests")
class UniqueItemsValidatorTest {

    private UniqueItemsValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UniqueItemsValidator();
    }

    // ========== POSITIVE TESTS (Valid inputs) ==========

    @Test
    @DisplayName("shouldPass_whenArrayHasUniqueItems")
    void shouldPass_whenArrayHasUniqueItems() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .uniqueItems(true)
                .build();
        List<Object> data = Arrays.asList(1, 2, 3, 4, 5);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for array with unique items");
    }

    @Test
    @DisplayName("shouldPass_whenArrayHasOneItem")
    void shouldPass_whenArrayHasOneItem() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .uniqueItems(true)
                .build();
        List<Object> data = Arrays.asList(1);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for array with single item");
    }

    // ========== NEGATIVE TESTS (Invalid inputs) ==========

    @Test
    @DisplayName("shouldFail_whenArrayHasDuplicateIntegers")
    void shouldFail_whenArrayHasDuplicateIntegers() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .uniqueItems(true)
                .build();
        List<Object> data = Arrays.asList(1, 2, 3, 2);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors for array with duplicate integers");
        assertEquals(1, errors.size(), "Expected exactly one error");
        
        ValidationError error = errors.get(0);
        assertEquals("uniqueItems", error.getExpectedType(), "Expected keyword to be 'uniqueItems'");
        assertTrue(error.getDescription().contains("duplicate"), "Expected message to contain 'duplicate'");
    }

    @Test
    @DisplayName("shouldFail_whenArrayHasDuplicateStrings")
    void shouldFail_whenArrayHasDuplicateStrings() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .uniqueItems(true)
                .build();
        List<Object> data = Arrays.asList("a", "b", "a");

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors for array with duplicate strings");
        assertEquals(1, errors.size(), "Expected exactly one error");
        
        ValidationError error = errors.get(0);
        assertEquals("uniqueItems", error.getExpectedType(), "Expected keyword to be 'uniqueItems'");
        assertTrue(error.getDescription().contains("duplicate"), "Expected message to contain 'duplicate'");
    }

    // ========== EDGE CASES ==========

    @Test
    @DisplayName("shouldPass_whenArrayIsEmpty")
    void shouldPass_whenArrayIsEmpty() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .uniqueItems(true)
                .build();
        List<Object> data = Arrays.asList();

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for empty array (no duplicates)");
    }

    @Test
    @DisplayName("shouldFail_whenArrayHasNumericAndStringDuplicates")
    void shouldFail_whenArrayHasNumericAndStringDuplicates() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .uniqueItems(true)
                .build();
        // In Java, 1 == 1.0 is true (numeric equality)
        List<Object> data = Arrays.asList(1, 1.0);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors for array with numeric duplicates (1 == 1.0)");
        assertEquals(1, errors.size(), "Expected exactly one error");
        
        ValidationError error = errors.get(0);
        assertEquals("uniqueItems", error.getExpectedType(), "Expected keyword to be 'uniqueItems'");
        assertTrue(error.getDescription().contains("duplicate"), "Expected message to contain 'duplicate'");
    }

    @Test
    @DisplayName("shouldPass_whenUniqueItemsIsFalse")
    void shouldPass_whenUniqueItemsIsFalse() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .uniqueItems(false)
                .build();
        // Even with duplicates, uniqueItems=false should not validate
        List<Object> data = Arrays.asList(1, 1, 1);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when uniqueItems is false");
    }

    @Test
    @DisplayName("shouldPass_whenInputIsNotArray")
    void shouldPass_whenInputIsNotArray() {
        // Arrange
        Schema schema = Schema.builder("testArray", SchemaType.ARRAY)
                .uniqueItems(true)
                .build();
        Object data = "not an array";

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for non-array input (type mismatch handled elsewhere)");
    }
}
