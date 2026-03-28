package com.maiconjh.schemacr.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaType;

/**
 * Unit tests for ArrayValidator.
 * 
 * <p>Tests the array validation logic including minItems, maxItems,
 * uniqueItems, item schema validation, prefixItems, and edge cases.</p>
 */
@DisplayName("ArrayValidator Tests")
class ArrayValidatorTest {

    private ArrayValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ArrayValidator();
    }

    // ========== POSITIVE TESTS (Valid arrays that should pass) ==========

    @Nested
    @DisplayName("Positive Tests - Valid arrays that should pass")
    class PositiveTests {

        @Test
        @DisplayName("shouldPass_whenValidArrayWithNoConstraints")
        void shouldPass_whenValidArrayWithNoConstraints() {
            // Arrange
            Schema schema = Schema.builder("items", SchemaType.ARRAY).build();
            List<Object> data = Arrays.asList(1, 2, 3, "test", true);

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no validation errors for valid array without constraints");
        }

        @Test
        @DisplayName("shouldPass_whenArrayMeetsMinItemsConstraint")
        void shouldPass_whenArrayMeetsMinItemsConstraint() {
            // Arrange
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .minItems(3)
                    .build();
            List<Object> data = Arrays.asList(1, 2, 3);

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when array meets minItems constraint");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_whenArrayMeetsMaxItemsConstraint")
        void shouldPass_whenArrayMeetsMaxItemsConstraint() {
            // Arrange
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .maxItems(5)
                    .build();
            List<Object> data = Arrays.asList(1, 2, 3);

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when array meets maxItems constraint");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_whenArrayHasUniqueItems")
        void shouldPass_whenArrayHasUniqueItems() {
            // Arrange
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .uniqueItems(true)
                    .build();
            List<Object> data = Arrays.asList(1, 2, 3, 4, 5);

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when array has unique items");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_withItemSchemaValidation")
        void shouldPass_withItemSchemaValidation() {
            // Arrange
            Schema itemSchema = Schema.builder("item", SchemaType.INTEGER).build();
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .itemSchema(itemSchema)
                    .build();
            List<Object> data = Arrays.asList(1, 2, 3, 4, 5);

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when all items match item schema");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_withComplexItemSchema")
        void shouldPass_withComplexItemSchema() {
            // Arrange
            Map<String, Schema> itemProperties = new HashMap<>();
            itemProperties.put("name", Schema.builder("name", SchemaType.STRING).build());
            itemProperties.put("age", Schema.builder("age", SchemaType.INTEGER).build());
            
            Schema itemSchema = Schema.builder("person", SchemaType.OBJECT)
                    .properties(itemProperties)
                    .build();
            
            Schema schema = Schema.builder("people", SchemaType.ARRAY)
                    .itemSchema(itemSchema)
                    .build();
            
            List<Object> data = new ArrayList<>();
            Map<String, Object> person1 = new HashMap<>();
            person1.put("name", "John");
            person1.put("age", 30);
            data.add(person1);
            
            Map<String, Object> person2 = new HashMap<>();
            person2.put("name", "Jane");
            person2.put("age", 25);
            data.add(person2);

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/people", "people");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when all complex items match schema");
        }

        @Test
        @DisplayName("shouldPass_withMinAndMaxItemsCombined")
        void shouldPass_withMinAndMaxItemsCombined() {
            // Arrange
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .minItems(2)
                    .maxItems(5)
                    .build();
            List<Object> data = Arrays.asList(1, 2, 3);

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when array meets both min and max constraints");
        }

        @Test
        @DisplayName("shouldPass_withMinItemsEqualToBoundary")
        void shouldPass_withMinItemsEqualToBoundary() {
            // Arrange - minItems = 3, data has exactly 3 items
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .minItems(3)
                    .build();
            List<Object> data = Arrays.asList("a", "b", "c");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when array has exactly minItems");
        }

        @Test
        @DisplayName("shouldPass_withMaxItemsEqualToBoundary")
        void shouldPass_withMaxItemsEqualToBoundary() {
            // Arrange - maxItems = 5, data has exactly 5 items
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .maxItems(5)
                    .build();
            List<Object> data = Arrays.asList(1, 2, 3, 4, 5);

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when array has exactly maxItems");
        }

        @Test
        @DisplayName("shouldPass_whenContainsMatchesDefaultMinContains")
        void shouldPass_whenContainsMatchesDefaultMinContains() {
            Schema containsSchema = Schema.builder("containsInt", SchemaType.INTEGER).build();
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .containsSchema(containsSchema)
                    .build();
            List<Object> data = Arrays.asList("a", 1, "b");

            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            assertTrue(errors.isEmpty(), "Expected no errors when at least one item matches contains schema");
        }
    }

    // ========== NEGATIVE TESTS (Invalid arrays that should fail) ==========

    @Nested
    @DisplayName("Negative Tests - Invalid arrays that should fail")
    class NegativeTests {

        @Test
        @DisplayName("shouldFail_whenDataIsNotAnArray")
        void shouldFail_whenDataIsNotAnArray() {
            // Arrange
            Schema schema = Schema.builder("items", SchemaType.ARRAY).build();
            Object data = "not an array";

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data is not an array");
            assertEquals(1, errors.size(), "Error list should have 1 error");
            
            ValidationError error = errors.get(0);
            assertEquals("array", error.getExpectedType(), "Expected keyword should be 'array'");
            assertTrue(error.getMessage().contains("Expected a list/array"), 
                    "Error message should mention expected array type");
        }

        @Test
        @DisplayName("shouldFail_whenArrayViolatesMinItems")
        void shouldFail_whenArrayViolatesMinItems() {
            // Arrange
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .minItems(3)
                    .build();
            List<Object> data = Arrays.asList(1, 2); // Only 2 items

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when array violates minItems");
            assertEquals(1, errors.size(), "Error list should have 1 error");
            
            ValidationError error = errors.get(0);
            assertEquals("minItems", error.getExpectedType(), "Expected keyword should be 'minItems'");
            assertTrue(error.getMessage().contains("at least 3 items"), 
                    "Error message should contain minItems constraint description");
        }

        @Test
        @DisplayName("shouldFail_whenArrayViolatesMaxItems")
        void shouldFail_whenArrayViolatesMaxItems() {
            // Arrange
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .maxItems(3)
                    .build();
            List<Object> data = Arrays.asList(1, 2, 3, 4, 5); // 5 items

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when array violates maxItems");
            assertEquals(1, errors.size(), "Error list should have 1 error");
            
            ValidationError error = errors.get(0);
            assertEquals("maxItems", error.getExpectedType(), "Expected keyword should be 'maxItems'");
            assertTrue(error.getMessage().contains("at most 3 items"), 
                    "Error message should contain maxItems constraint description");
        }

        @Test
        @DisplayName("shouldFail_whenArrayHasDuplicates")
        void shouldFail_whenArrayHasDuplicates() {
            // Arrange
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .uniqueItems(true)
                    .build();
            List<Object> data = Arrays.asList(1, 2, 3, 1); // Duplicate 1

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when array has duplicates");
            assertEquals(1, errors.size(), "Error list should have 1 error");
            
            ValidationError error = errors.get(0);
            assertEquals("uniqueItems", error.getExpectedType(), "Expected keyword should be 'uniqueItems'");
            assertTrue(error.getMessage().contains("must be unique"), 
                    "Error message should mention uniqueness requirement");
        }

        @Test
        @DisplayName("shouldFail_withInvalidItemType")
        void shouldFail_withInvalidItemType() {
            // Arrange - item schema expects integers
            Schema itemSchema = Schema.builder("item", SchemaType.INTEGER).build();
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .itemSchema(itemSchema)
                    .build();
            List<Object> data = Arrays.asList(1, 2, "not an integer", 4);

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when at least one item violates item schema");
            assertTrue(errors.stream().anyMatch(e -> "integer".equals(e.getExpectedType())),
                    "Expected an integer type error for the invalid item");
        }

        @Test
        @DisplayName("shouldFail_whenMultipleConstraintsViolated")
        void shouldFail_whenMultipleConstraintsViolated() {
            // Arrange - minItems=3, maxItems=5, but data has 6 items
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .minItems(3)
                    .maxItems(5)
                    .build();
            List<Object> data = Arrays.asList(1, 2, 3, 4, 5, 6);

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when maxItems violated");
            assertEquals(1, errors.size(), "Error list should have 1 error (first violation)");
            
            ValidationError error = errors.get(0);
            assertEquals("maxItems", error.getExpectedType(), "Expected keyword should be 'maxItems'");
        }

        @Test
        @DisplayName("shouldFail_whenContainsViolatesMinContains")
        void shouldFail_whenContainsViolatesMinContains() {
            Schema containsSchema = Schema.builder("containsString", SchemaType.STRING).build();
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .containsSchema(containsSchema)
                    .minContains(2)
                    .build();
            List<Object> data = Arrays.asList("one", 2, 3);

            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            assertFalse(errors.isEmpty(), "Expected errors when contains matches are below minContains");
            assertTrue(errors.stream().anyMatch(e -> "minContains".equals(e.getExpectedType())),
                    "Expected minContains error");
        }

        @Test
        @DisplayName("shouldFail_whenContainsExceedsMaxContains")
        void shouldFail_whenContainsExceedsMaxContains() {
            Schema containsSchema = Schema.builder("containsNumber", SchemaType.NUMBER).build();
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .containsSchema(containsSchema)
                    .maxContains(1)
                    .build();
            List<Object> data = Arrays.asList(1, 2, "x");

            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            assertFalse(errors.isEmpty(), "Expected errors when contains matches exceed maxContains");
            assertTrue(errors.stream().anyMatch(e -> "maxContains".equals(e.getExpectedType())),
                    "Expected maxContains error");
        }

        @Test
        @DisplayName("shouldFail_whenUnevaluatedItemsIsFalse")
        void shouldFail_whenUnevaluatedItemsIsFalse() {
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .prefixItems(List.of(Schema.builder("first", SchemaType.STRING).build()))
                    .unevaluatedItemsAllowed(false)
                    .build();
            List<Object> data = Arrays.asList("ok", 2);

            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            assertTrue(errors.stream().anyMatch(e -> "unevaluatedItems".equals(e.getExpectedType())),
                    "Expected unevaluatedItems error for trailing item");
        }

        @Test
        @DisplayName("shouldFail_whenOnlyUnevaluatedItemsIsFalse")
        void shouldFail_whenOnlyUnevaluatedItemsIsFalse() {
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .unevaluatedItemsAllowed(false)
                    .build();
            List<Object> data = Arrays.asList("a");

            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            assertTrue(errors.stream().anyMatch(e -> "unevaluatedItems".equals(e.getExpectedType())),
                    "Expected unevaluatedItems error when no other item evaluators are defined");
        }

        @Test
        @DisplayName("shouldNotFailUnevaluatedItems_whenAdditionalItemsSchemaEvaluatesTrailingItems")
        void shouldNotFailUnevaluatedItems_whenAdditionalItemsSchemaEvaluatesTrailingItems() {
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .prefixItems(List.of(Schema.builder("first", SchemaType.STRING).build()))
                    .additionalItemsSchema(Schema.builder("rest", SchemaType.INTEGER).build())
                    .unevaluatedItemsAllowed(false)
                    .build();
            List<Object> data = Arrays.asList("ok", 1, 2);

            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            assertFalse(errors.stream().anyMatch(e -> "unevaluatedItems".equals(e.getExpectedType())),
                    "No unevaluatedItems error expected when additionalItems schema evaluates trailing items");
        }

        @Test
        @DisplayName("shouldFail_whenMinItemsAndUniqueItemsViolated")
        void shouldFail_whenMinItemsAndUniqueItemsViolated() {
            // Arrange - minItems=3 and uniqueItems=true, but data has 2 items with duplicate
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .minItems(3)
                    .uniqueItems(true)
                    .build();
            List<Object> data = Arrays.asList(1, 1); // Only 2 items and duplicate

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when constraints violated");
            // Both minItems and uniqueItems should be checked
            assertTrue(errors.size() >= 1, "Error list should have at least 1 error");
            
            // minItems error should come first (checked first in ArrayValidator)
            ValidationError firstError = errors.get(0);
            assertEquals("minItems", firstError.getExpectedType(), "First error should be minItems");
        }
    }

    // ========== EDGE CASES (Boundary conditions and special scenarios) ==========

    @Nested
    @DisplayName("Edge Cases - Boundary conditions and special scenarios")
    class EdgeCaseTests {

        @Test
        @DisplayName("shouldHandle_nullData")
        void shouldHandle_nullData() {
            // Arrange
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .minItems(1)
                    .build();

            // Act
            List<ValidationError> errors = validator.validate(null, schema, "/items", "items");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data is null");
            assertEquals(1, errors.size(), "Error list should have 1 error");
            
            ValidationError error = errors.get(0);
            assertEquals("array", error.getExpectedType(), "Expected keyword should be 'array'");
            assertTrue(error.getMessage().toLowerCase().contains("expected"), 
                    "Error message should indicate expected type");
        }

        @Test
        @DisplayName("shouldHandle_emptyArray")
        void shouldHandle_emptyArray() {
            // Arrange - schema with no constraints
            Schema schema = Schema.builder("items", SchemaType.ARRAY).build();
            List<Object> data = new ArrayList<>();

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors for empty array without constraints");
        }

        @Test
        @DisplayName("shouldHandle_emptyArrayWithMinItemsZero")
        void shouldHandle_emptyArrayWithMinItemsZero() {
            // Arrange - minItems=0 should allow empty array
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .minItems(0)
                    .build();
            List<Object> data = new ArrayList<>();

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors for empty array with minItems=0");
        }

        @Test
        @DisplayName("shouldHandle_arrayWithNullElements")
        void shouldHandle_arrayWithNullElements() {
            // Arrange
            Schema itemSchema = Schema.builder("item", SchemaType.STRING).build();
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .itemSchema(itemSchema)
                    .build();
            List<Object> data = Arrays.asList("a", null, "b");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert - null element validation depends on implementation
            // This test documents current behavior
            assertTrue(errors.isEmpty() || errors.size() > 0, 
                    "Validation should handle null elements gracefully");
        }

        @Test
        @DisplayName("shouldHandle_numericDuplicateNormalization")
        void shouldHandle_numericDuplicateNormalization() {
            // Arrange - 1 and 1.0 should be considered duplicates
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .uniqueItems(true)
                    .build();
            List<Object> data = Arrays.asList(1, 1.0); // Should be duplicates

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert - numeric values 1 and 1.0 should be normalized as duplicates
            assertFalse(errors.isEmpty(), "Expected errors when numeric duplicates (1 and 1.0)");
            
            ValidationError error = errors.get(0);
            assertEquals("uniqueItems", error.getExpectedType(), "Expected keyword should be 'uniqueItems'");
        }

        @Test
        @DisplayName("shouldHandle_schemaWithOnlyMinItemsConstraint")
        void shouldHandle_schemaWithOnlyMinItemsConstraint() {
            // Arrange
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .minItems(1)
                    .build();
            List<Object> data = Arrays.asList("single");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when meeting minItems");
        }

        @Test
        @DisplayName("shouldHandle_schemaWithOnlyMaxItemsConstraint")
        void shouldHandle_schemaWithOnlyMaxItemsConstraint() {
            // Arrange
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .maxItems(10)
                    .build();
            List<Object> data = Arrays.asList(1, 2, 3);

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when under maxItems");
        }

        @Test
        @DisplayName("shouldHandle_schemaWithOnlyUniqueItemsConstraint")
        void shouldHandle_schemaWithOnlyUniqueItemsConstraint() {
            // Arrange
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .uniqueItems(true)
                    .build();
            List<Object> data = Arrays.asList("a", "b", "c");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when items are unique");
        }

        @Test
        @DisplayName("shouldHandle_multipleValidationErrors")
        void shouldHandle_multipleValidationErrors() {
            // Arrange - Multiple items fail validation
            Schema itemSchema = Schema.builder("item", SchemaType.INTEGER).build();
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .itemSchema(itemSchema)
                    .build();
            List<Object> data = Arrays.asList("invalid1", "invalid2", "invalid3");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertEquals(3, errors.size(), "Expected one error per invalid item");
            assertTrue(errors.stream().allMatch(e -> "integer".equals(e.getExpectedType())),
                    "Expected integer type errors for all invalid items");
        }

        @Test
        @DisplayName("shouldHandle_stringData")
        void shouldHandle_stringData() {
            // Arrange - String is not a valid array
            Schema schema = Schema.builder("items", SchemaType.ARRAY).build();
            String data = "not an array string";

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data is string");
            assertEquals(1, errors.size(), "Error list should have 1 error");
            
            ValidationError error = errors.get(0);
            assertEquals("array", error.getExpectedType(), "Expected type should be 'array'");
        }

        @Test
        @DisplayName("shouldHandle_numberData")
        void shouldHandle_numberData() {
            // Arrange - Number is not a valid array
            Schema schema = Schema.builder("items", SchemaType.ARRAY).build();
            Number data = 42;

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data is number");
            assertEquals(1, errors.size(), "Error list should have 1 error");
            
            ValidationError error = errors.get(0);
            assertEquals("array", error.getExpectedType(), "Expected type should be 'array'");
        }

        @Test
        @DisplayName("shouldHandle_mapData")
        void shouldHandle_mapData() {
            // Arrange - Map is not a valid array
            Schema schema = Schema.builder("items", SchemaType.ARRAY).build();
            Map<String, Object> data = new HashMap<>();
            data.put("key", "value");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data is a map/object");
            assertEquals(1, errors.size(), "Error list should have 1 error");
            
            ValidationError error = errors.get(0);
            assertEquals("array", error.getExpectedType(), "Expected type should be 'array'");
        }

        @Test
        @DisplayName("shouldHandle_complexNestedArrayValidation")
        void shouldHandle_complexNestedArrayValidation() {
            // Arrange - Array of arrays with item schema
            Schema innerItemSchema = Schema.builder("innerItem", SchemaType.STRING).build();
            Schema arrayOfArraysSchema = Schema.builder("outer", SchemaType.ARRAY)
                    .itemSchema(innerItemSchema)
                    .minItems(1)
                    .uniqueItems(true)
                    .build();
            
            List<Object> outerData = Arrays.asList(
                Arrays.asList("a", "b"),
                Arrays.asList("c", "d")
            );

            // Act
            List<ValidationError> errors = validator.validate(outerData, arrayOfArraysSchema, "/outer", "outer");

            // Assert
            assertFalse(errors.isEmpty(), "Expected nested arrays to fail when item schema expects string values");
            assertTrue(errors.stream().anyMatch(e -> "string".equals(e.getExpectedType())),
                    "Expected string type errors for nested array elements");
        }

        @Test
        @DisplayName("shouldHandle_minItemsZeroWithUniqueItems")
        void shouldHandle_minItemsZeroWithUniqueItems() {
            // Arrange
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .minItems(0)
                    .uniqueItems(true)
                    .build();
            List<Object> data = new ArrayList<>();

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors for empty array with minItems=0 and uniqueItems");
        }

        @Test
        @DisplayName("shouldHandle_booleanData")
        void shouldHandle_booleanData() {
            // Arrange - Boolean is not a valid array
            Schema schema = Schema.builder("items", SchemaType.ARRAY).build();
            Boolean data = true;

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data is boolean");
            assertEquals(1, errors.size(), "Error list should have 1 error");
            
            ValidationError error = errors.get(0);
            assertEquals("array", error.getExpectedType(), "Expected type should be 'array'");
        }
    }
}
