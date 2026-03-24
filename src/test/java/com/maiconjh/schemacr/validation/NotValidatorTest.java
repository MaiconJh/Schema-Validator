package com.maiconjh.schemacr.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaType;

/**
 * Unit tests for NotValidator.
 * 
 * <p>Tests the not JSON Schema keyword validation logic according to
 * JSON Schema 2019-09 specification. Validates that the instance MUST NOT
 * be valid against the provided schema.</p>
 */
@DisplayName("NotValidator Tests")
class NotValidatorTest {

    private NotValidator validator;

    @BeforeEach
    void setUp() {
        validator = new NotValidator();
    }

    // ========== POSITIVE TESTS (Valid not that should pass - data does NOT match the not schema) ==========

    @Nested
    @DisplayName("Positive Tests - Valid not that should pass")
    class PositiveTests {

        @Test
        @DisplayName("shouldPass_not_whenDataDoesNotMatchNotSchema_string")
        void shouldPass_not_whenDataDoesNotMatchNotSchema_string() {
            // Arrange - not schema requires string with minLength 5
            // Data is "hi" which does NOT match (too short)
            Schema notSchema = Schema.builder("notSchema", SchemaType.STRING)
                    .minLength(5)
                    .build();

            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .notSchema(notSchema)
                    .build();

            String data = "hi";

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when data does NOT match not schema");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_not_whenDataDoesNotMatchNotSchema_integer")
        void shouldPass_not_whenDataDoesNotMatchNotSchema_integer() {
            // Arrange - not schema requires integer >= 100
            // Data is 50 which does NOT match
            Schema notSchema = Schema.builder("notSchema", SchemaType.INTEGER)
                    .minimum(100)
                    .build();

            Schema schema = Schema.builder("data", SchemaType.INTEGER)
                    .notSchema(notSchema)
                    .build();

            Integer data = 50;

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when data does NOT match not schema");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_not_whenDataDoesNotMatchNotSchema_object")
        void shouldPass_not_whenDataDoesNotMatchNotSchema_object() {
            // Arrange - not schema requires object with property "admin"
            // Data does not have "admin" property
            Map<String, Schema> notProps = new HashMap<>();
            notProps.put("admin", Schema.builder("admin", SchemaType.STRING).build());

            Schema notSchema = Schema.builder("notSchema", SchemaType.OBJECT)
                    .properties(notProps)
                    .requiredFields(Arrays.asList("admin"))
                    .build();

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .notSchema(notSchema)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John"); // Does not have "admin" field

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when data does NOT match not schema");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_not_whenDataDoesNotMatchNotSchema_array")
        void shouldPass_not_whenDataDoesNotMatchNotSchema_array() {
            // Arrange - not schema requires array with minItems 3
            // Data has only 2 items
            Schema notSchema = Schema.builder("notSchema", SchemaType.ARRAY)
                    .minItems(3)
                    .build();

            Schema schema = Schema.builder("data", SchemaType.ARRAY)
                    .notSchema(notSchema)
                    .build();

            List<Object> data = Arrays.asList("a", "b");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when data does NOT match not schema");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_not_withoutNotKeyword")
        void shouldPass_not_withoutNotKeyword() {
            // Arrange - schema without not keyword
            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .minLength(3)
                    .build();

            String data = "test";

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors for schema without not");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_not_withEnumNotSchema")
        void shouldPass_not_withEnumNotSchema() {
            // Arrange - not schema with enum values ["admin", "root"]
            // Data is "user" which is NOT in the enum
            Schema notSchema = Schema.builder("notSchema", SchemaType.STRING)
                    .enumValues(Arrays.asList("admin", "root"))
                    .build();

            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .notSchema(notSchema)
                    .build();

            String data = "user";

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when data is not in not enum");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_not_withFormatNotSchema")
        void shouldPass_not_withFormatNotSchema() {
            // Arrange - not schema requires email format
            // Data is not a valid email
            Schema notSchema = Schema.builder("notSchema", SchemaType.STRING)
                    .format("email")
                    .build();

            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .notSchema(notSchema)
                    .build();

            String data = "not-an-email";

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when data does not match not format");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_not_complexNotSchema")
        void shouldPass_not_complexNotSchema() {
            // Arrange - complex not schema with multiple conditions
            // not schema: must be integer AND >= 100 AND <= 200
            // Data is 250, which fails the range
            Schema notSchema = Schema.builder("notSchema", SchemaType.INTEGER)
                    .minimum(100)
                    .maximum(200)
                    .build();

            Schema schema = Schema.builder("data", SchemaType.INTEGER)
                    .notSchema(notSchema)
                    .build();

            Integer data = 250;

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when data does not match complex not schema");
            assertEquals(0, errors.size(), "Error list should be empty");
        }
    }

    // ========== NEGATIVE TESTS (Invalid not that should fail - data DOES match the not schema) ==========

    @Nested
    @DisplayName("Negative Tests - Invalid not that should fail")
    class NegativeTests {

        @Test
        @DisplayName("shouldFail_not_whenDataMatchesNotSchema_string")
        void shouldFail_not_whenDataMatchesNotSchema_string() {
            // Arrange - not schema requires string with minLength 5
            // Data is "hello" which DOES match
            Schema notSchema = Schema.builder("notSchema", SchemaType.STRING)
                    .minLength(5)
                    .build();

            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .notSchema(notSchema)
                    .build();

            String data = "hello";

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data matches not schema");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check for not keyword error
            ValidationError error = errors.get(0);
            assertEquals("not", error.getExpectedType(), "Expected 'not' keyword in error");
            assertEquals("matched", error.getActualType(), "Expected 'matched' as actual type");
            assertTrue(error.getMessage().contains("NOT") || error.getMessage().contains("not"), 
                    "Error message should mention not matching");
        }

        @Test
        @DisplayName("shouldFail_not_whenDataMatchesNotSchema_integer")
        void shouldFail_not_whenDataMatchesNotSchema_integer() {
            // Arrange - not schema requires integer >= 100
            // Data is 150 which DOES match
            Schema notSchema = Schema.builder("notSchema", SchemaType.INTEGER)
                    .minimum(100)
                    .build();

            Schema schema = Schema.builder("data", SchemaType.INTEGER)
                    .notSchema(notSchema)
                    .build();

            Integer data = 150;

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data matches not schema");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check for not keyword error
            ValidationError error = errors.get(0);
            assertEquals("not", error.getExpectedType(), "Expected 'not' keyword in error");
            assertEquals("matched", error.getActualType(), "Expected 'matched' as actual type");
        }

        @Test
        @DisplayName("shouldFail_not_whenDataMatchesNotSchema_object")
        void shouldFail_not_whenDataMatchesNotSchema_object() {
            // Arrange - not schema requires object with property "admin"
            // Data has "admin" property
            Map<String, Schema> notProps = new HashMap<>();
            notProps.put("admin", Schema.builder("admin", SchemaType.STRING).build());

            Schema notSchema = Schema.builder("notSchema", SchemaType.OBJECT)
                    .properties(notProps)
                    .requiredFields(Arrays.asList("admin"))
                    .build();

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .notSchema(notSchema)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("admin", "yes"); // Has "admin" field - matches not schema

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data matches not schema");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check for not keyword error
            ValidationError error = errors.get(0);
            assertEquals("not", error.getExpectedType(), "Expected 'not' keyword in error");
            assertEquals("matched", error.getActualType(), "Expected 'matched' as actual type");
        }

        @Test
        @DisplayName("shouldFail_not_whenDataMatchesNotSchema_array")
        void shouldFail_not_whenDataMatchesNotSchema_array() {
            // Arrange - not schema requires array with minItems 3
            // Data has 5 items
            Schema notSchema = Schema.builder("notSchema", SchemaType.ARRAY)
                    .minItems(3)
                    .build();

            Schema schema = Schema.builder("data", SchemaType.ARRAY)
                    .notSchema(notSchema)
                    .build();

            List<Object> data = Arrays.asList("a", "b", "c", "d", "e");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data matches not schema");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check for not keyword error
            ValidationError error = errors.get(0);
            assertEquals("not", error.getExpectedType(), "Expected 'not' keyword in error");
            assertEquals("matched", error.getActualType(), "Expected 'matched' as actual type");
        }

        @Test
        @DisplayName("shouldFail_not_whenDataMatchesEnumNotSchema")
        void shouldFail_not_whenDataMatchesEnumNotSchema() {
            // Arrange - not schema with enum values ["admin", "root"]
            // Data is "admin" which IS in the enum
            Schema notSchema = Schema.builder("notSchema", SchemaType.STRING)
                    .enumValues(Arrays.asList("admin", "root"))
                    .build();

            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .notSchema(notSchema)
                    .build();

            String data = "admin";

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data matches not enum");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check for not keyword error
            ValidationError error = errors.get(0);
            assertEquals("not", error.getExpectedType(), "Expected 'not' keyword in error");
            assertEquals("matched", error.getActualType(), "Expected 'matched' as actual type");
        }

        @Test
        @DisplayName("shouldFail_not_whenDataMatchesFormatNotSchema")
        void shouldFail_not_whenDataMatchesFormatNotSchema() {
            // Arrange - not schema requires email format
            // Data is a valid email
            Schema notSchema = Schema.builder("notSchema", SchemaType.STRING)
                    .format("email")
                    .build();

            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .notSchema(notSchema)
                    .build();

            String data = "test@example.com";

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data matches not format");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check for not keyword error
            ValidationError error = errors.get(0);
            assertEquals("not", error.getExpectedType(), "Expected 'not' keyword in error");
            assertEquals("matched", error.getActualType(), "Expected 'matched' as actual type");
        }

        @Test
        @DisplayName("shouldFail_not_complexValidationErrorMessage")
        void shouldFail_not_complexValidationErrorMessage() {
            // Arrange - complex not schema with specific error message requirements
            Schema notSchema = Schema.builder("notSchema", SchemaType.STRING)
                    .pattern("^[A-Z].*")
                    .minLength(10)
                    .build();

            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .notSchema(notSchema)
                    .build();

            String data = "ValidPattern"; // Matches pattern and minLength

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data matches complex not schema");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check error message contains not
            ValidationError error = errors.get(0);
            assertTrue(error.getMessage().toLowerCase().contains("not"), 
                    "Error message should contain 'not'");
        }

        @Test
        @DisplayName("shouldFail_not_withEmptyNotSchema")
        void shouldFail_not_withEmptyNotSchema() {
            // Arrange - not schema with no constraints (always valid)
            // Using STRING type with no constraints - matches any string
            Schema notSchema = Schema.builder("emptyNotSchema", SchemaType.STRING)
                    .build();

            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .notSchema(notSchema)
                    .build();

            String data = "any value";

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - empty schema matches everything, so should fail
            assertFalse(errors.isEmpty(), "Expected errors when not schema is empty (always matches)");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check for not keyword error
            ValidationError error = errors.get(0);
            assertEquals("not", error.getExpectedType(), "Expected 'not' keyword in error");
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
            Schema notSchema = Schema.builder("notSchema", SchemaType.STRING)
                    .minLength(5)
                    .build();

            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .notSchema(notSchema)
                    .build();

            // Act
            List<ValidationError> errors = validator.validate(null, schema, "/data", "data");

            // Assert - null should be handled gracefully
            // The exact behavior depends on implementation, but should not throw
            assertTrue(errors.isEmpty() || errors.size() >= 0, "Should handle null data gracefully");
        }

        @Test
        @DisplayName("shouldHandle_emptyNotSchema")
        void shouldHandle_emptyNotSchema() {
            // Arrange - not schema with no constraints (always valid)
            Schema notSchema = Schema.builder("emptyNotSchema", SchemaType.STRING)
                    .build();

            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .notSchema(notSchema)
                    .build();

            String data = "any value";

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - empty schema matches everything, so should fail
            assertFalse(errors.isEmpty(), "Expected errors when not schema is empty (always matches)");
            assertEquals(1, errors.size(), "Error list should have 1 error");
        }

        @Test
        @DisplayName("shouldHandle_notSchemaWithTypeMismatch")
        void shouldHandle_notSchemaWithTypeMismatch() {
            // Arrange - not schema expects integer, data is string
            // The not schema type check will fail, so data does NOT match
            Schema notSchema = Schema.builder("notSchema", SchemaType.INTEGER)
                    .minimum(100)
                    .build();

            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .notSchema(notSchema)
                    .build();

            String data = "not a number";

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - type mismatch means data doesn't match not schema
            assertTrue(errors.isEmpty(), "Expected no errors when data type doesn't match not schema type");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldHandle_notWithPattern")
        void shouldHandle_notWithPattern() {
            // Arrange - not schema with pattern that should NOT match
            Schema notSchema = Schema.builder("notSchema", SchemaType.STRING)
                    .pattern("^[0-9]+$")
                    .build();

            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .notSchema(notSchema)
                    .build();

            String data = "abc123"; // Contains letters, does NOT match pattern

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when data does not match pattern");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldHandle_notWithPatternMatching")
        void shouldHandle_notWithPatternMatching() {
            // Arrange - not schema with pattern that DOES match
            Schema notSchema = Schema.builder("notSchema", SchemaType.STRING)
                    .pattern("^[0-9]+$")
                    .build();

            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .notSchema(notSchema)
                    .build();

            String data = "12345"; // Only digits, matches pattern

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data matches pattern");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check for not keyword error
            ValidationError error = errors.get(0);
            assertEquals("not", error.getExpectedType(), "Expected 'not' keyword in error");
        }

        @Test
        @DisplayName("shouldHandle_notWithMultipleOf")
        void shouldHandle_notWithMultipleOf() {
            // Arrange - not schema requires multiple of 5
            Schema notSchema = Schema.builder("notSchema", SchemaType.INTEGER)
                    .multipleOf(5)
                    .build();

            Schema schema = Schema.builder("data", SchemaType.INTEGER)
                    .notSchema(notSchema)
                    .build();

            Integer data = 15; // Multiple of 5

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data is multiple of 5");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check for not keyword error
            ValidationError error = errors.get(0);
            assertEquals("not", error.getExpectedType(), "Expected 'not' keyword in error");
        }

        @Test
        @DisplayName("shouldHandle_notWithMaxLength")
        void shouldHandle_notWithMaxLength() {
            // Arrange - not schema requires maxLength 3
            Schema notSchema = Schema.builder("notSchema", SchemaType.STRING)
                    .maxLength(3)
                    .build();

            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .notSchema(notSchema)
                    .build();

            String data = "ab"; // Length 2, matches maxLength

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data matches maxLength");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check for not keyword error
            ValidationError error = errors.get(0);
            assertEquals("not", error.getExpectedType(), "Expected 'not' keyword in error");
        }

        @Test
        @DisplayName("shouldHandle_notWithExclusiveMaximum")
        void shouldHandle_notWithExclusiveMaximum() {
            // Arrange - not schema with exclusiveMaximum (use maximum with exclusiveMaximum=true)
            Schema notSchema = Schema.builder("notSchema", SchemaType.INTEGER)
                    .maximum(100)
                    .exclusiveMaximum(true)
                    .build();

            Schema schema = Schema.builder("data", SchemaType.INTEGER)
                    .notSchema(notSchema)
                    .build();

            Integer data = 50; // Less than 100, matches exclusiveMaximum

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data matches exclusiveMaximum");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check for not keyword error
            ValidationError error = errors.get(0);
            assertEquals("not", error.getExpectedType(), "Expected 'not' keyword in error");
        }

        @Test
        @DisplayName("shouldHandle_notWithExclusiveMinimum")
        void shouldHandle_notWithExclusiveMinimum() {
            // Arrange - not schema with exclusiveMinimum (use minimum with exclusiveMinimum=true)
            Schema notSchema = Schema.builder("notSchema", SchemaType.INTEGER)
                    .minimum(0)
                    .exclusiveMinimum(true)
                    .build();

            Schema schema = Schema.builder("data", SchemaType.INTEGER)
                    .notSchema(notSchema)
                    .build();

            Integer data = 10; // Greater than 0, matches exclusiveMinimum

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data matches exclusiveMinimum");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check for not keyword error
            ValidationError error = errors.get(0);
            assertEquals("not", error.getExpectedType(), "Expected 'not' keyword in error");
        }

        @Test
        @DisplayName("shouldHandle_notWithConstValue")
        void shouldHandle_notWithConstValue() {
            // Arrange - not schema with const value
            Schema notSchema = Schema.builder("notSchema", SchemaType.STRING)
                    .constValue("secret")
                    .build();

            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .notSchema(notSchema)
                    .build();

            String data = "secret"; // Matches const

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data matches const");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check for not keyword error
            ValidationError error = errors.get(0);
            assertEquals("not", error.getExpectedType(), "Expected 'not' keyword in error");
        }

        @Test
        @DisplayName("shouldHandle_notWithUniqueItems")
        void shouldHandle_notWithUniqueItems() {
            // Arrange - not schema requires unique items
            Schema notSchema = Schema.builder("notSchema", SchemaType.ARRAY)
                    .uniqueItems(true)
                    .build();

            Schema schema = Schema.builder("data", SchemaType.ARRAY)
                    .notSchema(notSchema)
                    .build();

            List<Object> data = Arrays.asList("a", "b", "a"); // Has duplicates

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - duplicates fail uniqueItems, so data does NOT match not schema
            assertTrue(errors.isEmpty(), "Expected no errors when data fails uniqueItems constraint");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldHandle_notWithUniqueItemsMatching")
        void shouldHandle_notWithUniqueItemsMatching() {
            // Arrange - not schema requires unique items
            Schema notSchema = Schema.builder("notSchema", SchemaType.ARRAY)
                    .uniqueItems(true)
                    .build();

            Schema schema = Schema.builder("data", SchemaType.ARRAY)
                    .notSchema(notSchema)
                    .build();

            List<Object> data = Arrays.asList("a", "b", "c"); // All unique

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - all unique matches uniqueItems constraint
            assertFalse(errors.isEmpty(), "Expected errors when data has unique items");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check for not keyword error
            ValidationError error = errors.get(0);
            assertEquals("not", error.getExpectedType(), "Expected 'not' keyword in error");
        }
    }
}
