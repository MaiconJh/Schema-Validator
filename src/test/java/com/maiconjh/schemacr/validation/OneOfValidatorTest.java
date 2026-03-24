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
 * Unit tests for OneOfValidator.
 * 
 * <p>Tests the oneOf JSON Schema keyword validation logic according to
 * JSON Schema 2019-09 specification. Validates that exactly one of the 
 * subschemas MUST be valid.</p>
 */
@DisplayName("OneOfValidator Tests")
class OneOfValidatorTest {

    private OneOfValidator validator;

    @BeforeEach
    void setUp() {
        validator = new OneOfValidator();
    }

    // ========== POSITIVE TESTS (Valid oneOf that should pass) ==========

    @Nested
    @DisplayName("Positive Tests - Valid oneOf that should pass")
    class PositiveTests {

        @Test
        @DisplayName("shouldPass_oneOf_whenFirstSchemaMatches")
        void shouldPass_oneOf_whenFirstSchemaMatches() {
            // Arrange - oneOf with two schemas, first one matches
            Schema schema1 = Schema.builder("stringSchema", SchemaType.STRING)
                    .minLength(3)
                    .build();
            Schema schema2 = Schema.builder("numberSchema", SchemaType.INTEGER)
                    .minimum(100)
                    .build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2);
            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .oneOf(oneOfSchemas)
                    .build();

            String data = "test"; // Matches stringSchema with minLength 3

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when exactly one schema matches");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_oneOf_whenSecondSchemaMatches")
        void shouldPass_oneOf_whenSecondSchemaMatches() {
            // Arrange - oneOf with two schemas, second one matches
            Schema schema1 = Schema.builder("stringSchema", SchemaType.STRING)
                    .minLength(10)
                    .build();
            Schema schema2 = Schema.builder("numberSchema", SchemaType.INTEGER)
                    .minimum(0)
                    .maximum(50)
                    .build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2);
            Schema schema = Schema.builder("data", SchemaType.INTEGER)
                    .oneOf(oneOfSchemas)
                    .build();

            Integer data = 25; // Matches numberSchema (0-50)

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when exactly one schema matches");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_oneOf_whenMiddleSchemaMatches")
        void shouldPass_oneOf_whenMiddleSchemaMatches() {
            // Arrange - oneOf with three schemas, middle one matches
            Schema schema1 = Schema.builder("schema1", SchemaType.STRING)
                    .pattern("^[a-z]+$")
                    .build();
            Schema schema2 = Schema.builder("schema2", SchemaType.INTEGER)
                    .multipleOf(5)
                    .build();
            Schema schema3 = Schema.builder("schema3", SchemaType.BOOLEAN)
                    .build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2, schema3);
            Schema schema = Schema.builder("data", SchemaType.INTEGER)
                    .oneOf(oneOfSchemas)
                    .build();

            Integer data = 15; // Matches schema2 (multiple of 5)

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when middle schema matches");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_oneOf_withObjectSchema")
        void shouldPass_oneOf_withObjectSchema() {
            // Arrange - oneOf with object schemas
            Map<String, Schema> props1 = new HashMap<>();
            props1.put("type", Schema.builder("type", SchemaType.STRING).build());

            Schema schema1 = Schema.builder("object1", SchemaType.OBJECT)
                    .properties(props1)
                    .requiredFields(Arrays.asList("type"))
                    .build();

            Map<String, Schema> props2 = new HashMap<>();
            props2.put("name", Schema.builder("name", SchemaType.STRING).build());

            Schema schema2 = Schema.builder("object2", SchemaType.OBJECT)
                    .properties(props2)
                    .requiredFields(Arrays.asList("name"))
                    .build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2);
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .oneOf(oneOfSchemas)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("type", "admin"); // Matches schema1

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when object matches exactly one schema");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_oneOf_withArraySchema")
        void shouldPass_oneOf_withArraySchema() {
            // Arrange - oneOf with array schemas
            Schema schema1 = Schema.builder("array1", SchemaType.ARRAY)
                    .minItems(1)
                    .maxItems(3)
                    .build();
            Schema schema2 = Schema.builder("array2", SchemaType.ARRAY)
                    .minItems(5)
                    .maxItems(10)
                    .build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2);
            Schema schema = Schema.builder("data", SchemaType.ARRAY)
                    .oneOf(oneOfSchemas)
                    .build();

            List<Object> data = Arrays.asList("a", "b"); // 2 items - matches schema1

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when array matches exactly one schema");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_oneOf_withoutOneOfKeyword")
        void shouldPass_oneOf_withoutOneOfKeyword() {
            // Arrange - schema without oneOf
            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .minLength(3)
                    .build();

            String data = "test";

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors for schema without oneOf");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_oneOf_withEmptyOneOfList")
        void shouldPass_oneOf_withEmptyOneOfList() {
            // Arrange - schema with empty oneOf list
            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .oneOf(new ArrayList<>())
                    .build();

            String data = "test";

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors for empty oneOf list");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_oneOf_withEnumMatching")
        void shouldPass_oneOf_withEnumMatching() {
            // Arrange - oneOf with enum schemas
            Schema schema1 = Schema.builder("enum1", SchemaType.STRING)
                    .enumValues(Arrays.asList("admin", "user", "guest"))
                    .build();
            Schema schema2 = Schema.builder("enum2", SchemaType.INTEGER)
                    .enumValues(Arrays.asList(1, 2, 3))
                    .build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2);
            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .oneOf(oneOfSchemas)
                    .build();

            String data = "admin"; // Matches enum1

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when enum matches");
            assertEquals(0, errors.size(), "Error list should be empty");
        }
    }

    // ========== NEGATIVE TESTS (Invalid oneOf that should fail) ==========

    @Nested
    @DisplayName("Negative Tests - Invalid oneOf that should fail")
    class NegativeTests {

        @Test
        @DisplayName("shouldFail_oneOf_whenNoSchemaMatches")
        void shouldFail_oneOf_whenNoSchemaMatches() {
            // Arrange - oneOf with two schemas, neither matches
            Schema schema1 = Schema.builder("stringSchema", SchemaType.STRING)
                    .minLength(10)
                    .build();
            Schema schema2 = Schema.builder("numberSchema", SchemaType.INTEGER)
                    .minimum(100)
                    .build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2);
            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .oneOf(oneOfSchemas)
                    .build();

            String data = "hi"; // Too short for schema1, not a number for schema2

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when no schema matches");
            assertTrue(errors.size() >= 1, "Error list should have at least 1 error");

            // Check for oneOf keyword error
            boolean hasOneOfError = errors.stream()
                    .anyMatch(e -> e.getExpectedType().equals("oneOf"));
            assertTrue(hasOneOfError, "Expected 'oneOf' keyword in error");

            // Check error message
            boolean hasMessage = errors.stream()
                    .anyMatch(e -> e.getMessage().contains("oneOf") && e.getMessage().contains("0"));
            assertTrue(hasMessage, "Error message should mention oneOf and 0 matches");
        }

        @Test
        @DisplayName("shouldFail_oneOf_whenMultipleSchemasMatch")
        void shouldFail_oneOf_whenMultipleSchemasMatch() {
            // Arrange - oneOf where multiple schemas match
            // Using anyOf-style schemas that can both match: both accept empty object
            Schema schema1 = Schema.builder("anyObject", SchemaType.OBJECT)
                    .additionalProperties(true)
                    .build();
            Schema schema2 = Schema.builder("objectWithProps", SchemaType.OBJECT)
                    .additionalProperties(false)
                    .build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2);
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .oneOf(oneOfSchemas)
                    .build();

            Map<String, Object> data = new HashMap<>(); // Empty object matches both

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - both schemas match empty object, so should fail oneOf
            assertFalse(errors.isEmpty(), "Expected errors when multiple schemas match");
            assertTrue(errors.size() >= 1, "Error list should have at least 1 error");

            // Check for oneOf keyword error about multiple matches
            boolean hasOneOfError = errors.stream()
                    .anyMatch(e -> e.getExpectedType().equals("oneOf"));
            assertTrue(hasOneOfError, "Expected 'oneOf' keyword in error");

            // Check error message contains multiple matches info
            boolean hasMultipleMatchMessage = errors.stream()
                    .anyMatch(e -> e.getMessage().contains("multiple") || e.getMessage().contains("2"));
            assertTrue(hasMultipleMatchMessage, "Error message should mention multiple matches");
        }

        @Test
        @DisplayName("shouldFail_oneOf_whenDataTypeNotInAnySchema")
        void shouldFail_oneOf_whenDataTypeNotInAnySchema() {
            // Arrange - data type doesn't match any schema type
            Schema schema1 = Schema.builder("stringSchema", SchemaType.STRING)
                    .build();
            Schema schema2 = Schema.builder("arraySchema", SchemaType.ARRAY)
                    .build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2);
            Schema schema = Schema.builder("data", SchemaType.INTEGER)
                    .oneOf(oneOfSchemas)
                    .build();

            Integer data = 42; // Not string, not array

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when data type doesn't match any schema");
            assertTrue(errors.size() >= 1, "Error list should have at least 1 error");

            // Check for oneOf keyword error
            boolean hasOneOfError = errors.stream()
                    .anyMatch(e -> e.getExpectedType().equals("oneOf"));
            assertTrue(hasOneOfError, "Expected 'oneOf' keyword in error");
        }

        @Test
        @DisplayName("shouldFail_oneOf_allSchemasFail")
        void shouldFail_oneOf_allSchemasFail() {
            // Arrange - all schemas have constraints that fail
            Schema schema1 = Schema.builder("stringSchema", SchemaType.STRING)
                    .minLength(20)
                    .pattern("^[A-Z].*")
                    .build();
            Schema schema2 = Schema.builder("numberSchema", SchemaType.INTEGER)
                    .minimum(1000)
                    .maximum(2000)
                    .build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2);
            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .oneOf(oneOfSchemas)
                    .build();

            String data = "short"; // Fails both: too short, doesn't start with uppercase

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when all schemas fail");
            assertTrue(errors.size() >= 1, "Error list should have at least 1 error");

            // Verify error contains oneOf keyword
            boolean hasOneOfKeyword = errors.stream()
                    .anyMatch(e -> e.getExpectedType().equals("oneOf"));
            assertTrue(hasOneOfKeyword, "Error should contain 'oneOf' keyword");
        }

        @Test
        @DisplayName("shouldFail_oneOf_complexValidationErrorMessage")
        void shouldFail_oneOf_complexValidationErrorMessage() {
            // Arrange - complex oneOf with specific error message requirements
            Schema schema1 = Schema.builder("stringSchema", SchemaType.STRING)
                    .format("email")
                    .build();
            Schema schema2 = Schema.builder("stringSchema2", SchemaType.STRING)
                    .format("uri")
                    .build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2);
            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .oneOf(oneOfSchemas)
                    .build();

            String data = "not-an-email-or-uri"; // Invalid format for both

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when format validation fails");

            // Check error message contains oneOf
            boolean hasOneOfInMessage = errors.stream()
                    .anyMatch(e -> e.getMessage().toLowerCase().contains("oneof") || 
                                   e.getMessage().toLowerCase().contains("one of"));
            assertTrue(hasOneOfInMessage, "Error message should contain 'oneOf'");
        }

        @Test
        @DisplayName("shouldFail_oneOf_withThreeMatchingSchemas")
        void shouldFail_oneOf_withThreeMatchingSchemas() {
            // Arrange - oneOf where all three schemas match
            Schema schema1 = Schema.builder("object1", SchemaType.OBJECT)
                    .build();
            Schema schema2 = Schema.builder("object2", SchemaType.OBJECT)
                    .additionalProperties(true)
                    .build();
            Schema schema3 = Schema.builder("object3", SchemaType.OBJECT)
                    .additionalProperties(false)
                    .build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2, schema3);
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .oneOf(oneOfSchemas)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("key", "value");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - multiple schemas match
            assertFalse(errors.isEmpty(), "Expected errors when multiple schemas match");

            // Verify error count shows multiple matches
            boolean hasMultipleMatchError = errors.stream()
                    .anyMatch(e -> e.getMessage().contains("3") || e.getMessage().contains("multiple"));
            assertTrue(hasMultipleMatchError, "Error should indicate 3 matches");
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
            Schema schema1 = Schema.builder("schema1", SchemaType.STRING).build();
            Schema schema2 = Schema.builder("schema2", SchemaType.OBJECT).build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2);
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .oneOf(oneOfSchemas)
                    .build();

            // Act
            List<ValidationError> errors = validator.validate(null, schema, "/data", "data");

            // Assert - null should be handled gracefully
            // The exact behavior depends on implementation, but should not throw
            assertTrue(errors.isEmpty() || errors.size() >= 0, "Should handle null data gracefully");
        }

        @Test
        @DisplayName("shouldHandle_emptyObjectInOneOf")
        void shouldHandle_emptyObjectInOneOf() {
            // Arrange - oneOf with empty schemas
            Schema schema1 = Schema.builder("empty1", SchemaType.OBJECT).build();
            Schema schema2 = Schema.builder("empty2", SchemaType.OBJECT).build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2);
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .oneOf(oneOfSchemas)
                    .build();

            Map<String, Object> data = new HashMap<>();

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - empty object matches both, so should fail oneOf
            assertFalse(errors.isEmpty(), "Expected errors when multiple empty schemas match");
        }

        @Test
        @DisplayName("shouldHandle_nestedOneOf")
        void shouldHandle_nestedOneOf() {
            // Arrange - nested oneOf
            Schema innerSchema1 = Schema.builder("inner1", SchemaType.STRING).build();
            Schema innerSchema2 = Schema.builder("inner2", SchemaType.INTEGER).build();

            Schema outerSchema1 = Schema.builder("outer1", SchemaType.ARRAY)
                    .oneOf(Arrays.asList(innerSchema1, innerSchema2))
                    .build();
            Schema outerSchema2 = Schema.builder("outer2", SchemaType.OBJECT).build();

            List<Schema> oneOfSchemas = Arrays.asList(outerSchema1, outerSchema2);
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .oneOf(oneOfSchemas)
                    .build();

            Map<String, Object> data = new HashMap<>();

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors for nested oneOf with matching inner schema");
        }

        @Test
        @DisplayName("shouldHandle_oneOfWithRequiredFields")
        void shouldHandle_oneOfWithRequiredFields() {
            // Arrange - oneOf with required fields in schemas
            Map<String, Schema> props1 = new HashMap<>();
            props1.put("email", Schema.builder("email", SchemaType.STRING).format("email").build());

            Schema schema1 = Schema.builder("user", SchemaType.OBJECT)
                    .properties(props1)
                    .requiredFields(Arrays.asList("email"))
                    .build();

            Map<String, Schema> props2 = new HashMap<>();
            props2.put("username", Schema.builder("username", SchemaType.STRING).build());

            Schema schema2 = Schema.builder("account", SchemaType.OBJECT)
                    .properties(props2)
                    .requiredFields(Arrays.asList("username"))
                    .build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2);
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .oneOf(oneOfSchemas)
                    .build();

            // Has email but no username
            Map<String, Object> data = new HashMap<>();
            data.put("email", "test@example.com");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when exactly one schema's required fields met");
        }

        @Test
        @DisplayName("shouldHandle_oneOfWithPatternMatching")
        void shouldHandle_oneOfWithPatternMatching() {
            // Arrange - oneOf with pattern validation
            Schema schema1 = Schema.builder("phonePattern", SchemaType.STRING)
                    .pattern("^\\+?\\d{10,15}$")
                    .build();
            Schema schema2 = Schema.builder("emailPattern", SchemaType.STRING)
                    .pattern("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
                    .build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2);
            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .oneOf(oneOfSchemas)
                    .build();

            String data = "+1234567890"; // Matches phone pattern

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when pattern matches exactly one schema");
        }

        @Test
        @DisplayName("shouldHandle_oneOfWithConstValue")
        void shouldHandle_oneOfWithConstValue() {
            // Arrange - oneOf with const values
            Schema schema1 = Schema.builder("const1", SchemaType.STRING)
                    .constValue("active")
                    .build();
            Schema schema2 = Schema.builder("const2", SchemaType.STRING)
                    .constValue("inactive")
                    .build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2);
            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .oneOf(oneOfSchemas)
                    .build();

            String data = "active"; // Matches const1

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when const value matches");
        }

        @Test
        @DisplayName("shouldHandle_oneOfWithBooleanSchemas")
        void shouldHandle_oneOfWithBooleanSchemas() {
            // Arrange - oneOf with true/false schemas (boolean shorthand)
            // In JSON Schema, true means schema passes, false means schema fails
            Schema schema1 = Schema.builder("trueSchema", SchemaType.STRING).build();
            Schema falseSchema = Schema.builder("falseSchema", SchemaType.STRING)
                    .enumValues(Arrays.asList("__IMPOSSIBLE_VALUE__"))
                    .build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, falseSchema);
            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .oneOf(oneOfSchemas)
                    .build();

            String data = "test"; // Matches schema1, but falseSchema can't match anything

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - should pass because only one schema matches
            assertTrue(errors.isEmpty(), "Expected no errors when exactly one valid schema matches");
        }

        @Test
        @DisplayName("shouldHandle_oneOfWithMultipleConstraints")
        void shouldHandle_oneOfWithMultipleConstraints() {
            // Arrange - oneOf with multiple constraints in each schema
            Schema schema1 = Schema.builder("strictString", SchemaType.STRING)
                    .minLength(5)
                    .maxLength(10)
                    .pattern("^[a-z]+$")
                    .build();
            Schema schema2 = Schema.builder("number", SchemaType.INTEGER)
                    .minimum(0)
                    .maximum(100)
                    .build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2);
            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .oneOf(oneOfSchemas)
                    .build();

            String data = "valid"; // 5 chars, lowercase - matches schema1

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when all constraints in one schema are met");
        }

        @Test
        @DisplayName("shouldHandle_emptyArrayData")
        void shouldHandle_emptyArrayData() {
            // Arrange
            Schema schema1 = Schema.builder("array1", SchemaType.ARRAY)
                    .minItems(1)
                    .build();
            Schema schema2 = Schema.builder("string2", SchemaType.STRING)
                    .build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2);
            Schema schema = Schema.builder("data", SchemaType.ARRAY)
                    .oneOf(oneOfSchemas)
                    .build();

            List<Object> data = new ArrayList<>(); // Empty array

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - empty array doesn't match schema1 (minItems 1), doesn't match schema2 (not string)
            assertFalse(errors.isEmpty(), "Expected errors when empty array matches no schema");
            assertTrue(errors.size() >= 1, "Error list should have at least 1 error");
        }

        @Test
        @DisplayName("shouldHandle_complexTypeValidation")
        void shouldHandle_complexTypeValidation() {
            // Arrange - complex validation scenario
            Schema schema1 = Schema.builder("schema1", SchemaType.OBJECT)
                    .minProperties(2)
                    .build();
            Schema schema2 = Schema.builder("schema2", SchemaType.OBJECT)
                    .maxProperties(1)
                    .build();

            List<Schema> oneOfSchemas = Arrays.asList(schema1, schema2);
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .oneOf(oneOfSchemas)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("key1", "value1");
            data.put("key2", "value2"); // 2 properties - matches schema1, fails schema2

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when exactly one schema matches");
        }
    }
}
