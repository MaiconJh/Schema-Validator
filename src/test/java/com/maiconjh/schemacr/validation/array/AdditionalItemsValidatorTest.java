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
 * Unit tests for AdditionalItemsValidator.
 * 
 * <p>Tests the additionalItems JSON Schema keyword validation logic.</p>
 */
@DisplayName("AdditionalItemsValidator Tests")
class AdditionalItemsValidatorTest {

    private AdditionalItemsValidator validator;
    private Schema schema;

    @BeforeEach
    void setUp() {
        // No setup needed - validator is created per test
    }

    // ========== POSITIVE TESTS (Valid inputs) ==========

    @Test
    @DisplayName("shouldPass_whenAdditionalItemsAllowsExtraItems")
    void shouldPass_whenAdditionalItemsAllowsExtraItems() {
        // Arrange
        // Schema: prefixItems: [{"type": "string"}], additionalItems: {"type": "number"}
        // Valid: extra items are numbers
        List<Schema> prefixSchemas = Collections.singletonList(
                Schema.builder("firstItem", SchemaType.STRING).build()
        );
        
        Schema additionalItemsSchema = Schema.builder("additionalItems", SchemaType.NUMBER).build();
        
        schema = Schema.builder("testArray", SchemaType.ARRAY)
                .prefixItems(prefixSchemas)
                .additionalItemsSchema(additionalItemsSchema)
                .build();
        
        validator = new AdditionalItemsValidator(additionalItemsSchema, prefixSchemas.size());
        
        // Extra items (42, 100) are numbers - should pass
        List<Object> data = Arrays.asList("hello", 42, 100);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when extra items match additionalItems schema");
    }

    @Test
    @DisplayName("shouldPass_whenArrayLengthEqualsPrefixItems")
    void shouldPass_whenArrayLengthEqualsPrefixItems() {
        // Arrange
        // Schema: prefixItems: [{"type": "string"}, {"type": "number"}], additionalItems: {"type": "boolean"}
        // Array has exactly 2 items (no extra items)
        List<Schema> prefixSchemas = Arrays.asList(
                Schema.builder("firstItem", SchemaType.STRING).build(),
                Schema.builder("secondItem", SchemaType.NUMBER).build()
        );
        
        Schema additionalItemsSchema = Schema.builder("additionalItems", SchemaType.BOOLEAN).build();
        
        schema = Schema.builder("testArray", SchemaType.ARRAY)
                .prefixItems(prefixSchemas)
                .additionalItemsSchema(additionalItemsSchema)
                .build();
        
        validator = new AdditionalItemsValidator(additionalItemsSchema, prefixSchemas.size());
        
        // No extra items - array length equals prefixItems length
        List<Object> data = Arrays.asList("test", 100);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when array has no extra items");
    }

    @Test
    @DisplayName("shouldPass_whenAdditionalItemsIsTrue")
    void shouldPass_whenAdditionalItemsIsTrue() {
        // Arrange
        // In the current implementation, additionalItems=true is simulated by using
        // SchemaType.ANY which accepts any type
        List<Schema> prefixSchemas = Collections.singletonList(
                Schema.builder("firstItem", SchemaType.STRING).build()
        );
        
        // Using SchemaType.ANY to simulate additionalItems:true (allows any extra items)
        Schema additionalItemsSchema = Schema.builder("additionalItems", SchemaType.ANY).build();
        
        schema = Schema.builder("testArray", SchemaType.ARRAY)
                .prefixItems(prefixSchemas)
                .additionalItemsSchema(additionalItemsSchema)
                .build();
        
        validator = new AdditionalItemsValidator(additionalItemsSchema, prefixSchemas.size());
        
        // Any extra items allowed with additionalItems=true
        List<Object> data = Arrays.asList("hello", 42, true, "world", 3.14);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for additionalItems=true scenario");
    }

    // ========== NEGATIVE TESTS (Invalid inputs) ==========

    @Test
    @DisplayName("shouldFail_whenAdditionalItemsSchemaRejectsExtraItem")
    void shouldFail_whenAdditionalItemsSchemaRejectsExtraItem() {
        // Arrange
        // Schema: prefixItems: [{"type": "string"}], additionalItems: {"type": "number"}
        // Invalid: extra items include string (not matching schema)
        List<Schema> prefixSchemas = Collections.singletonList(
                Schema.builder("firstItem", SchemaType.STRING).build()
        );
        
        Schema additionalItemsSchema = Schema.builder("additionalItems", SchemaType.NUMBER).build();
        
        schema = Schema.builder("testArray", SchemaType.ARRAY)
                .prefixItems(prefixSchemas)
                .additionalItemsSchema(additionalItemsSchema)
                .build();
        
        validator = new AdditionalItemsValidator(additionalItemsSchema, prefixSchemas.size());
        
        // Extra item "world" is a string, not a number - should fail
        List<Object> data = Arrays.asList("hello", "world");

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors when extra item doesn't match additionalItems schema");
        assertEquals(1, errors.size(), "Expected exactly one error for rejected extra item");
        
        ValidationError error = errors.get(0);
        // The keyword returned is from PrimitiveValidator (the type that failed validation)
        assertEquals("number", error.getExpectedType(), "Expected keyword to be 'number' (type mismatch)");
    }

    @Test
    @DisplayName("shouldFail_whenAdditionalItemsIsFalseAndHasExtraItems")
    void shouldFail_whenAdditionalItemsIsFalseAndHasExtraItems() {
        // Arrange
        // Simulating additionalItems:false by using a schema that will always fail
        // In JSON Schema, additionalItems:false means no extra items allowed
        // We use NULL type which only accepts null value - any non-null extra item will fail
        List<Schema> prefixSchemas = Collections.singletonList(
                Schema.builder("firstItem", SchemaType.STRING).build()
        );
        
        // Using NULL type schema - only accepts null, any other value fails
        // This simulates additionalItems:false (rejects all extra items)
        Schema rejectingSchema = Schema.builder("rejectAll", SchemaType.NULL).build();
        
        schema = Schema.builder("testArray", SchemaType.ARRAY)
                .prefixItems(prefixSchemas)
                .additionalItemsSchema(rejectingSchema)
                .build();
        
        validator = new AdditionalItemsValidator(rejectingSchema, prefixSchemas.size());
        
        // Extra item present - should fail because additionalItems:false
        List<Object> data = Arrays.asList("hello", "extra");

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors when additionalItems is false and has extra items");
        assertEquals(1, errors.size(), "Expected exactly one error for extra item with additionalItems:false");
        
        ValidationError error = errors.get(0);
        // The keyword returned is from PrimitiveValidator (the type that failed validation)
        assertEquals("null", error.getExpectedType(), "Expected keyword to be 'null' (type mismatch)");
    }

    // ========== EDGE CASES (Boundary conditions) ==========

    @Test
    @DisplayName("shouldPass_whenNoPrefixItemsAndAdditionalItemsNotSet")
    void shouldPass_whenNoPrefixItemsAndAdditionalItemsNotSet() {
        // Arrange
        // Schema without prefixItems and without additionalItems - allows any array
        schema = Schema.builder("testArray", SchemaType.ARRAY)
                .build();
        
        // When prefixItems is null, validator is not created in ArrayValidator
        // But we can still test directly with empty prefixSchemas
        // Using ANY type to allow any items
        Schema additionalItemsSchema = Schema.builder("additionalItems", SchemaType.ANY).build();
        validator = new AdditionalItemsValidator(additionalItemsSchema, 0);
        
        // Any array should be allowed when no prefixItems constraint
        List<Object> data = Arrays.asList(1, 2, 3, "string", true, null);

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when no prefixItems and additionalItems not enforced");
    }

    @Test
    @DisplayName("shouldPass_whenInputIsNotArray")
    void shouldPass_whenInputIsNotArray() {
        // Arrange
        List<Schema> prefixSchemas = Collections.singletonList(
                Schema.builder("firstItem", SchemaType.STRING).build()
        );
        
        Schema additionalItemsSchema = Schema.builder("additionalItems", SchemaType.NUMBER).build();
        
        schema = Schema.builder("testArray", SchemaType.ARRAY)
                .prefixItems(prefixSchemas)
                .additionalItemsSchema(additionalItemsSchema)
                .build();
        
        validator = new AdditionalItemsValidator(additionalItemsSchema, prefixSchemas.size());
        
        // Non-array input - type mismatch handled elsewhere
        Object data = "not an array";

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for non-array input (type mismatch handled elsewhere)");
    }

    @Test
    @DisplayName("shouldPass_whenAdditionalItemsWithEmptyPrefixItems")
    void shouldPass_whenAdditionalItemsWithEmptyPrefixItems() {
        // Arrange
        // Schema: prefixItems: [], additionalItems: {"type": "string"}
        // Empty prefixItems means all items are "additional"
        List<Schema> prefixSchemas = Collections.emptyList();
        
        Schema additionalItemsSchema = Schema.builder("additionalItems", SchemaType.STRING).build();
        
        schema = Schema.builder("testArray", SchemaType.ARRAY)
                .prefixItems(prefixSchemas)
                .additionalItemsSchema(additionalItemsSchema)
                .build();
        
        validator = new AdditionalItemsValidator(additionalItemsSchema, prefixSchemas.size());
        
        // All items are "additional" and should match the additionalItems schema
        List<Object> data = Arrays.asList("hello", "world", "test");

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when all items match additionalItems schema");
    }

    @Test
    @DisplayName("shouldValidateAllExtraItems_whenMultipleExtraItems")
    void shouldValidateAllExtraItems_whenMultipleExtraItems() {
        // Arrange
        // Tests that all extra items are validated, not just the first one
        List<Schema> prefixSchemas = Collections.singletonList(
                Schema.builder("firstItem", SchemaType.STRING).build()
        );
        
        // Extra items must be numbers
        Schema additionalItemsSchema = Schema.builder("additionalItems", SchemaType.NUMBER).build();
        
        schema = Schema.builder("testArray", SchemaType.ARRAY)
                .prefixItems(prefixSchemas)
                .additionalItemsSchema(additionalItemsSchema)
                .build();
        
        validator = new AdditionalItemsValidator(additionalItemsSchema, prefixSchemas.size());
        
        // Multiple extra items: one valid (42), one invalid (true), one invalid ("test")
        List<Object> data = Arrays.asList("hello", 42, true, "test");

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors for invalid extra items");
        // Should have 2 errors: one for true at [2], one for "test" at [3]
        assertEquals(2, errors.size(), "Expected two errors for two invalid extra items");
    }
}
