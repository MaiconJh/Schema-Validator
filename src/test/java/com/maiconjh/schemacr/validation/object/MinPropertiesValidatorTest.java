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
 * Unit tests for MinPropertiesValidator.
 * 
 * <p>Tests the minProperties JSON Schema keyword validation logic according to
 * JSON Schema specification. Validates that an object contains a minimum number
 * of properties.</p>
 */
@DisplayName("MinPropertiesValidator Tests")
class MinPropertiesValidatorTest {

    private ObjectValidator validator;

    @BeforeEach
    void setUp() {
        // Use ObjectValidator which properly checks if minProperties is defined in schema
        validator = new ObjectValidator();
    }

    // ========== POSITIVE TESTS (Objects with >= minProperties should pass) ==========

    @Nested
    @DisplayName("Positive Tests - Objects with sufficient properties")
    class PositiveTests {

        @Test
        @DisplayName("shouldPass_minProperties_whenObjectHasExactlyMinProperties")
        void shouldPass_minProperties_whenObjectHasExactlyMinProperties() {
            // Arrange - minProperties is 2, object has exactly 2 properties
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .minProperties(2)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");
            data.put("age", 30);

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when object has exactly minProperties");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_minProperties_whenObjectHasMoreThanMinProperties")
        void shouldPass_minProperties_whenObjectHasMoreThanMinProperties() {
            // Arrange - minProperties is 2, object has 3 properties
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .minProperties(2)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");
            data.put("age", 30);
            data.put("city", "NYC");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when object has more than minProperties");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_minProperties_whenObjectHasManyProperties")
        void shouldPass_minProperties_whenObjectHasManyProperties() {
            // Arrange - minProperties is 2, object has 5 properties
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .minProperties(2)
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
            assertTrue(errors.isEmpty(), "Expected no errors when object has many properties");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_minProperties_withZeroMinProperties")
        void shouldPass_minProperties_withZeroMinProperties() {
            // Arrange - minProperties is 0
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .minProperties(0)
                    .build();

            Map<String, Object> data = new HashMap<>();
            // Empty object should pass when minProperties is 0

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when minProperties is 0");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_minProperties_withOneMinProperties")
        void shouldPass_minProperties_withOneMinProperties() {
            // Arrange - minProperties is 1
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .minProperties(1)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when object has exactly 1 property and minProperties is 1");
            assertEquals(0, errors.size(), "Error list should be empty");
        }
    }

    // ========== NEGATIVE TESTS (Objects with < minProperties should fail) ==========

    @Nested
    @DisplayName("Negative Tests - Objects with insufficient properties")
    class NegativeTests {

        @Test
        @DisplayName("shouldFail_minProperties_whenObjectHasOneProperty")
        void shouldFail_minProperties_whenObjectHasOneProperty() {
            // Arrange - minProperties is 2, object has only 1 property
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .minProperties(2)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when object has fewer than minProperties");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check error details
            ValidationError error = errors.get(0);
            assertEquals("minProperties", error.getExpectedType(), "Expected 'minProperties' keyword in error");
            assertEquals("1", error.getActualType(), "Expected actual property count as actual type");
            assertTrue(error.getMessage().contains("at least 2"), 
                    "Error message should mention minimum required properties");
        }

        @Test
        @DisplayName("shouldFail_minProperties_whenObjectIsEmpty")
        void shouldFail_minProperties_whenObjectIsEmpty() {
            // Arrange - minProperties is 2, object is empty
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .minProperties(2)
                    .build();

            Map<String, Object> data = new HashMap<>();

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when object is empty");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check error details
            ValidationError error = errors.get(0);
            assertEquals("minProperties", error.getExpectedType(), "Expected 'minProperties' keyword in error");
            assertEquals("0", error.getActualType(), "Expected 0 as actual type for empty object");
            assertTrue(error.getMessage().contains("at least 2"), 
                    "Error message should mention minimum required properties");
        }

        @Test
        @DisplayName("shouldFail_minProperties_whenMinPropertiesIsThree")
        void shouldFail_minProperties_whenMinPropertiesIsThree() {
            // Arrange - minProperties is 3, object has 2 properties
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .minProperties(3)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");
            data.put("age", 30);

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when object has fewer than minProperties");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check error details
            ValidationError error = errors.get(0);
            assertEquals("minProperties", error.getExpectedType(), "Expected 'minProperties' keyword in error");
            assertEquals("2", error.getActualType(), "Expected 2 as actual type");
            assertTrue(error.getMessage().contains("at least 3"), 
                    "Error message should mention minimum of 3 properties");
        }

        @Test
        @DisplayName("shouldFail_minProperties_errorMessageContainsActualCount")
        void shouldFail_minProperties_errorMessageContainsActualCount() {
            // Arrange - minProperties is 5, object has 2 properties
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .minProperties(5)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");
            data.put("age", 30);

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when object has fewer than minProperties");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check error contains actual count in message
            ValidationError error = errors.get(0);
            assertTrue(error.getMessage().contains("2"), 
                    "Error message should contain actual property count");
            assertTrue(error.getMessage().contains("5"), 
                    "Error message should contain minimum property count");
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
                    .minProperties(2)
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
                    .minProperties(2)
                    .build();

            String data = "not an object";

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - non-map data should be handled gracefully
            assertTrue(errors.isEmpty(), "Expected no errors for non-map data");
            assertEquals(0, errors.size(), "Error list should be empty for non-map");
        }

        @Test
        @DisplayName("shouldHandle_emptyObjectWithZeroMinProperties")
        void shouldHandle_emptyObjectWithZeroMinProperties() {
            // Arrange - minProperties is 0, empty object should pass
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .minProperties(0)
                    .build();

            Map<String, Object> data = new HashMap<>();

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors for empty object with minProperties 0");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldHandle_largeMinPropertiesValue")
        void shouldHandle_largeMinPropertiesValue() {
            // Arrange - minProperties is 100, object has only 1 property
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .minProperties(100)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when property count is far below minProperties");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check error details
            ValidationError error = errors.get(0);
            assertEquals("minProperties", error.getExpectedType(), "Expected 'minProperties' keyword in error");
            assertTrue(error.getMessage().contains("100"), 
                    "Error message should contain minimum of 100");
        }

        @Test
        @DisplayName("shouldHandle_schemaWithoutMinProperties")
        void shouldHandle_schemaWithoutMinProperties() {
            // Arrange - schema without minProperties constraint
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .build();

            Map<String, Object> data = new HashMap<>();
            // Empty object should pass when no minProperties is specified

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - no constraint means no validation failure
            assertTrue(errors.isEmpty(), "Expected no errors when schema has no minProperties");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldHandle_listDataInsteadOfMap")
        void shouldHandle_listDataInsteadOfMap() {
            // Arrange - data is a list, not a map
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .minProperties(2)
                    .build();

            List<Object> data = new ArrayList<>();
            data.add("item1");
            data.add("item2");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - list data should be handled gracefully
            assertTrue(errors.isEmpty(), "Expected no errors for list data");
            assertEquals(0, errors.size(), "Error list should be empty for list");
        }

        @Test
        @DisplayName("shouldHandle_minPropertiesOne_withEmptyObject")
        void shouldHandle_minPropertiesOne_withEmptyObject() {
            // Arrange - minProperties is 1, empty object should fail
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .minProperties(1)
                    .build();

            Map<String, Object> data = new HashMap<>();

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when minProperties is 1 but object is empty");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check error details
            ValidationError error = errors.get(0);
            assertEquals("minProperties", error.getExpectedType(), "Expected 'minProperties' keyword in error");
            assertEquals("0", error.getActualType(), "Expected 0 as actual type");
        }
    }
}
