package com.maiconjh.schemacr.validation.array;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaType;
import com.maiconjh.schemacr.validation.ValidationError;

/**
 * Unit tests for PrefixItemsValidator.
 * 
 * <p>Tests the prefixItems JSON Schema keyword validation logic.</p>
 */
@DisplayName("PrefixItemsValidator Tests")
class PrefixItemsValidatorTest {

    private PrefixItemsValidator validator;
    private Schema schema;

    @BeforeEach
    void setUp() {
        // No setup needed - validator is created per test
    }

    // ========== POSITIVE TESTS (Valid inputs) ==========

    @Test
    @DisplayName("shouldPass_whenArrayMatchesAllPrefixItemSchemas")
    void shouldPass_whenArrayMatchesAllPrefixItemSchemas() {
        // Arrange
        List<Schema> prefixSchemas = Arrays.asList(
                Schema.builder("firstItem", SchemaType.STRING).build(),
                Schema.builder("secondItem", SchemaType.NUMBER).build(),
                Schema.builder("thirdItem", SchemaType.BOOLEAN).build()
        );
        
        schema = Schema.builder("testArray", SchemaType.ARRAY)
                .prefixItems(prefixSchemas)
                .build();
        validator = new PrefixItemsValidator(prefixSchemas);
        
        List<Object> data = Arrays.asList("hello", 42, true);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for array matching all prefixItem schemas");
    }

    @Test
    @DisplayName("shouldPass_whenArrayHasFewerItemsThanPrefixItems")
    void shouldPass_whenArrayHasFewerItemsThanPrefixItems() {
        // Arrange
        List<Schema> prefixSchemas = Arrays.asList(
                Schema.builder("firstItem", SchemaType.STRING).build(),
                Schema.builder("secondItem", SchemaType.NUMBER).build(),
                Schema.builder("thirdItem", SchemaType.BOOLEAN).build()
        );
        
        schema = Schema.builder("testArray", SchemaType.ARRAY)
                .prefixItems(prefixSchemas)
                .build();
        validator = new PrefixItemsValidator(prefixSchemas);
        
        // Array has only 2 items, but prefixItems has 3 schemas
        // Only the 2 existing items should be validated
        List<Object> data = Arrays.asList("hello", 42);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for array with fewer items than prefixItems");
    }

    @Test
    @DisplayName("shouldPass_whenPrefixItemsSchemaAllowsExtra")
    void shouldPass_whenPrefixItemsSchemaAllowsExtra() {
        // Arrange
        List<Schema> prefixSchemas = Arrays.asList(
                Schema.builder("firstItem", SchemaType.STRING).build()
        );
        
        // additionalItems is not set (null), which allows extra items
        schema = Schema.builder("testArray", SchemaType.ARRAY)
                .prefixItems(prefixSchemas)
                .build();
        validator = new PrefixItemsValidator(prefixSchemas);
        
        // Array has more items than prefixItems schemas - extra items are allowed
        List<Object> data = Arrays.asList("hello", 42, true, "extra");

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when prefixItems allows extra items");
    }

    // ========== NEGATIVE TESTS (Invalid inputs) ==========

    @Test
    @DisplayName("shouldFail_whenArrayItemFailsPrefixSchema")
    void shouldFail_whenArrayItemFailsPrefixSchema() {
        // Arrange
        List<Schema> prefixSchemas = Arrays.asList(
                Schema.builder("firstItem", SchemaType.STRING).build(),
                Schema.builder("secondItem", SchemaType.NUMBER).build()
        );
        
        schema = Schema.builder("testArray", SchemaType.ARRAY)
                .prefixItems(prefixSchemas)
                .build();
        validator = new PrefixItemsValidator(prefixSchemas);
        
        // Second item is a string, but schema expects a number
        List<Object> data = Arrays.asList("hello", "world");

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors when array item fails prefix schema");
        assertEquals(1, errors.size(), "Expected exactly one error");
        
        ValidationError error = errors.get(0);
        assertTrue(error.getNodePath().contains("[1]"), "Expected error path to contain position [1]");
    }

    @Test
    @DisplayName("shouldFail_whenMultipleItemsFailPrefixSchemas")
    void shouldFail_whenMultipleItemsFailPrefixSchemas() {
        // Arrange
        List<Schema> prefixSchemas = Arrays.asList(
                Schema.builder("firstItem", SchemaType.STRING).build(),
                Schema.builder("secondItem", SchemaType.NUMBER).build(),
                Schema.builder("thirdItem", SchemaType.BOOLEAN).build()
        );
        
        schema = Schema.builder("testArray", SchemaType.ARRAY)
                .prefixItems(prefixSchemas)
                .build();
        validator = new PrefixItemsValidator(prefixSchemas);
        
        // Multiple items fail their schemas - all 3 items fail
        List<Object> data = Arrays.asList(123, "not a number", 999);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors when multiple items fail");
        assertEquals(3, errors.size(), "Expected three errors for failing items (one per position)");
    }

    // ========== EDGE CASES (Boundary conditions) ==========

    @Test
    @DisplayName("shouldPass_whenPrefixItemsIsEmpty")
    void shouldPass_whenPrefixItemsIsEmpty() {
        // Arrange
        List<Schema> prefixSchemas = Collections.emptyList();
        
        schema = Schema.builder("testArray", SchemaType.ARRAY)
                .prefixItems(prefixSchemas)
                .build();
        validator = new PrefixItemsValidator(prefixSchemas);
        
        // Empty prefixItems allows any array
        List<Object> data = Arrays.asList(1, 2, 3, "string", true);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for empty prefixItems");
    }

    @Test
    @DisplayName("shouldPass_whenInputIsNotArray")
    void shouldPass_whenInputIsNotArray() {
        // Arrange
        List<Schema> prefixSchemas = Arrays.asList(
                Schema.builder("firstItem", SchemaType.STRING).build()
        );
        
        schema = Schema.builder("testArray", SchemaType.ARRAY)
                .prefixItems(prefixSchemas)
                .build();
        validator = new PrefixItemsValidator(prefixSchemas);
        
        // Non-array input - type mismatch handled elsewhere
        Object data = "not an array";

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for non-array input");
    }

    @Test
    @DisplayName("shouldPass_whenArrayLengthEqualsPrefixItemsLength")
    void shouldPass_whenArrayLengthEqualsPrefixItemsLength() {
        // Arrange
        List<Schema> prefixSchemas = Arrays.asList(
                Schema.builder("firstItem", SchemaType.STRING).build(),
                Schema.builder("secondItem", SchemaType.NUMBER).build()
        );
        
        schema = Schema.builder("testArray", SchemaType.ARRAY)
                .prefixItems(prefixSchemas)
                .build();
        validator = new PrefixItemsValidator(prefixSchemas);
        
        // Array length equals prefixItems length
        List<Object> data = Arrays.asList("test", 100);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when array length equals prefixItems length");
    }

    @Test
    @DisplayName("shouldPass_whenEmptyArray")
    void shouldPass_whenEmptyArray() {
        // Arrange
        List<Schema> prefixSchemas = Arrays.asList(
                Schema.builder("firstItem", SchemaType.STRING).build()
        );
        
        schema = Schema.builder("testArray", SchemaType.ARRAY)
                .prefixItems(prefixSchemas)
                .build();
        validator = new PrefixItemsValidator(prefixSchemas);
        
        // Empty array - no items to validate
        List<Object> data = Collections.emptyList();

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for empty array");
    }

    @Test
    @DisplayName("shouldPass_whenNullData")
    void shouldPass_whenNullData() {
        // Arrange
        List<Schema> prefixSchemas = Arrays.asList(
                Schema.builder("firstItem", SchemaType.STRING).build()
        );
        
        schema = Schema.builder("testArray", SchemaType.ARRAY)
                .prefixItems(prefixSchemas)
                .build();
        validator = new PrefixItemsValidator(prefixSchemas);
        
        // Null data - no validation
        Object data = null;

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for null data");
    }
}