package com.maiconjh.schemacr.validation.object;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaType;
import com.maiconjh.schemacr.validation.ObjectValidator;
import com.maiconjh.schemacr.validation.ValidationError;

/**
 * Unit tests for MaxPropertiesValidator.
 * 
 * <p>Tests the maxProperties JSON Schema keyword validation logic according to
 * JSON Schema specification. Validates that an object contains at most a
 * specified number of properties.</p>
 */
@DisplayName("MaxPropertiesValidator Tests")
class MaxPropertiesValidatorTest {

    private ObjectValidator validator;

    @BeforeEach
    void setUp() {
        // Use ObjectValidator which properly checks if maxProperties is defined in schema
        validator = new ObjectValidator();
    }

    // ========== POSITIVE TESTS (Objects with <= maxProperties should pass) ==========

    @Nested
    @DisplayName("Positive Tests - Objects with at most maxProperties")
    class PositiveTests {

        @Test
        @DisplayName("shouldPass_maxProperties_whenObjectHasExactlyMaxProperties")
        void shouldPass_maxProperties_whenObjectHasExactlyMaxProperties() {
            // Arrange - maxProperties is 2, object has exactly 2 properties
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .maxProperties(2)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");
            data.put("age", 30);

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when object has exactly maxProperties");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_maxProperties_whenObjectHasLessThanMaxProperties")
        void shouldPass_maxProperties_whenObjectHasLessThanMaxProperties() {
            // Arrange - maxProperties is 2, object has 1 property
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .maxProperties(2)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when object has less than maxProperties");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_maxProperties_whenObjectIsEmpty")
        void shouldPass_maxProperties_whenObjectIsEmpty() {
            // Arrange - maxProperties is 2, object is empty
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .maxProperties(2)
                    .build();

            Map<String, Object> data = new HashMap<>();

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when object is empty");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_maxProperties_withZeroMaxProperties")
        void shouldPass_maxProperties_withZeroMaxProperties() {
            // Arrange - maxProperties is 0
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .maxProperties(0)
                    .build();

            Map<String, Object> data = new HashMap<>();
            // Empty object should pass when maxProperties is 0

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when maxProperties is 0");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_maxProperties_withOneMaxProperties")
        void shouldPass_maxProperties_withOneMaxProperties() {
            // Arrange - maxProperties is 1
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .maxProperties(1)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when object has exactly 1 property and maxProperties is 1");
            assertEquals(0, errors.size(), "Error list should be empty");
        }
    }

    // ========== NEGATIVE TESTS (Objects with > maxProperties should fail) ==========

    @Nested
    @DisplayName("Negative Tests - Objects with more than maxProperties")
    class NegativeTests {

        @Test
        @DisplayName("shouldFail_maxProperties_whenObjectHasOneMoreThanMaxProperties")
        void shouldFail_maxProperties_whenObjectHasOneMoreThanMaxProperties() {
            // Arrange - maxProperties is 2, object has 3 properties
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .maxProperties(2)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");
            data.put("age", 30);
            data.put("city", "NYC");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when object has more than maxProperties");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check error details
            ValidationError error = errors.get(0);
            assertEquals("maxProperties", error.getExpectedType(), "Expected 'maxProperties' keyword in error");
            assertEquals("3", error.getActualType(), "Expected actual property count as actual type");
            assertTrue(error.getMessage().contains("at most 2"), 
                    "Error message should mention maximum allowed properties");
        }

        @Test
        @DisplayName("shouldFail_maxProperties_whenObjectHasTwoMoreThanMaxProperties")
        void shouldFail_maxProperties_whenObjectHasTwoMoreThanMaxProperties() {
            // Arrange - maxProperties is 2, object has 4 properties
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .maxProperties(2)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");
            data.put("age", 30);
            data.put("city", "NYC");
            data.put("country", "USA");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when object has more than maxProperties");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check error details
            ValidationError error = errors.get(0);
            assertEquals("maxProperties", error.getExpectedType(), "Expected 'maxProperties' keyword in error");
            assertEquals("4", error.getActualType(), "Expected 4 as actual type");
            assertTrue(error.getMessage().contains("at most 2"), 
                    "Error message should mention maximum of 2 properties");
        }

        @Test
        @DisplayName("shouldFail_maxProperties_whenMaxPropertiesIsOne")
        void shouldFail_maxProperties_whenMaxPropertiesIsOne() {
            // Arrange - maxProperties is 1, object has 2 properties
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .maxProperties(1)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");
            data.put("age", 30);

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when object has more than maxProperties");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check error details
            ValidationError error = errors.get(0);
            assertEquals("maxProperties", error.getExpectedType(), "Expected 'maxProperties' keyword in error");
            assertEquals("2", error.getActualType(), "Expected 2 as actual type");
            assertTrue(error.getMessage().contains("at most 1"), 
                    "Error message should mention maximum of 1 property");
        }

        @Test
        @DisplayName("shouldFail_maxProperties_errorMessageContainsActualCount")
        void shouldFail_maxProperties_errorMessageContainsActualCount() {
            // Arrange - maxProperties is 3, object has 5 properties
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .maxProperties(3)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");
            data.put("age", 30);
            data.put("city", "NYC");
            data.put("country", "USA");
            data.put("active", true);

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when object has more than maxProperties");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check error contains actual count in message
            ValidationError error = errors.get(0);
            assertTrue(error.getMessage().contains("5"), 
                    "Error message should contain actual property count");
            assertTrue(error.getMessage().contains("3"), 
                    "Error message should contain maximum property count");
        }

        @Test
        @DisplayName("shouldFail_maxProperties_whenObjectHasManyProperties")
        void shouldFail_maxProperties_whenObjectHasManyProperties() {
            // Arrange - maxProperties is 2, object has 10 properties
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .maxProperties(2)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");
            data.put("age", 30);
            data.put("city", "NYC");
            data.put("country", "USA");
            data.put("active", true);
            data.put("email", "john@example.com");
            data.put("phone", "1234567890");
            data.put("address", "123 Main St");
            data.put("zipcode", "12345");
            data.put("state", "NY");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when object has many more properties than maxProperties");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check error details
            ValidationError error = errors.get(0);
            assertEquals("maxProperties", error.getExpectedType(), "Expected 'maxProperties' keyword in error");
            assertEquals("10", error.getActualType(), "Expected 10 as actual type");
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
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .maxProperties(2)
                    .build();

            // Act
            List<ValidationError> errors = validator.validate(null, schema, "/data", "data");

            // Assert - null should be handled gracefully (no error for non-map)
            assertTrue(errors.isEmpty(), "Expected no errors for null data (not an object)");
            assertEquals(0, errors.size(), "Error list should be empty for null");
        }

        @Test
        @DisplayName("shouldHandle_nonMapData")
        void shouldHandle_nonMapData() {
            // Arrange - data is a string, not an object
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .maxProperties(2)
                    .build();

            String data = "not an object";

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - non-map data should be handled gracefully
            assertTrue(errors.isEmpty(), "Expected no errors for non-map data");
            assertEquals(0, errors.size(), "Error list should be empty for non-map");
        }

        @Test
        @DisplayName("shouldHandle_emptyObjectWithZeroMaxProperties")
        void shouldHandle_emptyObjectWithZeroMaxProperties() {
            // Arrange - maxProperties is 0, empty object should pass
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .maxProperties(0)
                    .build();

            Map<String, Object> data = new HashMap<>();

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors for empty object with maxProperties 0");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldHandle_zeroMaxProperties_withNonEmptyObject")
        void shouldHandle_zeroMaxProperties_withNonEmptyObject() {
            // Arrange - maxProperties is 0, object has 1 property
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .maxProperties(0)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when maxProperties is 0 but object has properties");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check error details
            ValidationError error = errors.get(0);
            assertEquals("maxProperties", error.getExpectedType(), "Expected 'maxProperties' keyword in error");
            assertEquals("1", error.getActualType(), "Expected 1 as actual type");
        }

        @Test
        @DisplayName("shouldHandle_largeMaxPropertiesValue")
        void shouldHandle_largeMaxPropertiesValue() {
            // Arrange - maxProperties is 100, object has only 10 properties
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .maxProperties(100)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");
            data.put("age", 30);
            data.put("city", "NYC");
            data.put("country", "USA");
            data.put("active", true);
            data.put("email", "john@example.com");
            data.put("phone", "1234567890");
            data.put("address", "123 Main St");
            data.put("zipcode", "12345");
            data.put("state", "NY");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - property count is far below maxProperties
            assertTrue(errors.isEmpty(), "Expected no errors when property count is far below maxProperties");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldHandle_schemaWithoutMaxProperties")
        void shouldHandle_schemaWithoutMaxProperties() {
            // Arrange - schema without maxProperties constraint
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");
            data.put("age", 30);
            data.put("city", "NYC");
            // Object with many properties should pass when no maxProperties is specified

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - no constraint means no validation failure
            assertTrue(errors.isEmpty(), "Expected no errors when schema has no maxProperties");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldHandle_listDataInsteadOfMap")
        void shouldHandle_listDataInsteadOfMap() {
            // Arrange - data is a list, not a map
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .maxProperties(2)
                    .build();

            List<Object> data = new ArrayList<>();
            data.add("item1");
            data.add("item2");
            data.add("item3");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - list data should be handled gracefully
            assertTrue(errors.isEmpty(), "Expected no errors for list data");
            assertEquals(0, errors.size(), "Error list should be empty for list");
        }

        @Test
        @DisplayName("shouldHandle_maxPropertiesOne_withEmptyObject")
        void shouldHandle_maxPropertiesOne_withEmptyObject() {
            // Arrange - maxProperties is 1, empty object should pass
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .maxProperties(1)
                    .build();

            Map<String, Object> data = new HashMap<>();

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when maxProperties is 1 but object is empty");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldHandle_integerData")
        void shouldHandle_integerData() {
            // Arrange - data is an integer, not an object
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .maxProperties(2)
                    .build();

            Integer data = 42;

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - integer data should be handled gracefully
            assertTrue(errors.isEmpty(), "Expected no errors for integer data");
            assertEquals(0, errors.size(), "Error list should be empty for integer");
        }
    }
}
