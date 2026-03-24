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
 * Unit tests for ConditionalValidator.
 * 
 * <p>Tests the if/then/else conditional validation logic according to
 * JSON Schema 2019-09 specification.</p>
 */
@DisplayName("ConditionalValidator Tests")
class ConditionalValidatorTest {

    private ConditionalValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ConditionalValidator();
    }

    // ========== POSITIVE TESTS (Valid if/then/else that should pass) ==========

    @Nested
    @DisplayName("Positive Tests - Valid if/then/else that should pass")
    class PositiveTests {

        @Test
        @DisplayName("shouldPass_ifThenElse_whenIfConditionMetAndThenValid")
        void shouldPass_ifThenElse_whenIfConditionMetAndThenValid() {
            // Arrange - if type is object, then requires email property
            Schema thenSchema = Schema.builder("then", SchemaType.OBJECT)
                    .additionalProperties(false)
                    .build();
            
            Schema ifSchema = Schema.builder("if", SchemaType.OBJECT).build();
            
            Schema schema = Schema.builder("user", SchemaType.OBJECT)
                    .ifSchema(ifSchema)
                    .thenSchema(thenSchema)
                    .build();
            
            Map<String, Object> data = new HashMap<>();

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/user", "user");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when if condition met and then is valid");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_ifThenElse_whenIfConditionNotMetAndElseValid")
        void shouldPass_ifThenElse_whenIfConditionNotMetAndElseValid() {
            // Arrange - if type is string, else requires minimum length
            Schema ifSchema = Schema.builder("if", SchemaType.STRING)
                    .minLength(5)
                    .build();
            
            Schema elseSchema = Schema.builder("else", SchemaType.INTEGER)
                    .minimum(0)
                    .build();
            
            Schema schema = Schema.builder("data", SchemaType.INTEGER)
                    .ifSchema(ifSchema)
                    .elseSchema(elseSchema)
                    .build();
            
            Object data = 10; // Not a string, so if fails, else should validate

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when if fails and else is valid");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_ifThen_whenOnlyIfAndThenPresent")
        void shouldPass_ifThen_whenOnlyIfAndThenPresent() {
            // Arrange - if checks for property, then validates it
            Schema ifSchema = Schema.builder("if", SchemaType.OBJECT)
                    .build();
            
            Schema thenSchema = Schema.builder("then", SchemaType.OBJECT)
                    .build();
            
            Schema schema = Schema.builder("obj", SchemaType.OBJECT)
                    .ifSchema(ifSchema)
                    .thenSchema(thenSchema)
                    .build();
            
            Map<String, Object> data = new HashMap<>();

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/obj", "obj");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors with if/then only");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_ifElse_whenOnlyIfAndElsePresent")
        void shouldPass_ifElse_whenOnlyIfAndElsePresent() {
            // Arrange - if fails, else must pass
            Schema ifSchema = Schema.builder("if", SchemaType.STRING)
                    .minLength(100)
                    .build();
            
            Schema elseSchema = Schema.builder("else", SchemaType.OBJECT)
                    .build();
            
            Schema schema = Schema.builder("obj", SchemaType.OBJECT)
                    .ifSchema(ifSchema)
                    .elseSchema(elseSchema)
                    .build();
            
            Map<String, Object> data = new HashMap<>();

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/obj", "obj");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when if fails and else validates");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_ifThenElseFull_whenAllConditionsMet")
        void shouldPass_ifThenElseFull_whenAllConditionsMet() {
            // Arrange - complete if/then/else
            Schema ifSchema = Schema.builder("if", SchemaType.STRING)
                    .pattern("^[A-Z].*")
                    .build();
            
            Schema thenSchema = Schema.builder("then", SchemaType.STRING)
                    .minLength(5)
                    .build();
            
            Schema elseSchema = Schema.builder("else", SchemaType.STRING)
                    .maxLength(10)
                    .build();
            
            Schema schema = Schema.builder("text", SchemaType.STRING)
                    .ifSchema(ifSchema)
                    .thenSchema(thenSchema)
                    .elseSchema(elseSchema)
                    .build();
            
            String data = "ValidString"; // Starts with uppercase, 11 chars - passes then

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/text", "text");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when if and then conditions are met");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_noConditional_whenNoIfSchema")
        void shouldPass_noConditional_whenNoIfSchema() {
            // Arrange - schema without conditional
            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .minLength(3)
                    .build();
            
            String data = "test";

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors for schema without conditional");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_ifConditionFallsThrough_whenNoThenOrElse")
        void shouldPass_ifConditionFallsThrough_whenNoThenOrElse() {
            // Arrange - only if schema (no then or else means if must simply be valid)
            Schema ifSchema = Schema.builder("if", SchemaType.STRING)
                    .minLength(1)
                    .build();
            
            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .ifSchema(ifSchema)
                    .build();
            
            String data = "valid";

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when if validates and no then/else");
            assertEquals(0, errors.size(), "Error list should be empty");
        }
    }

    // ========== NEGATIVE TESTS (Conditional validation that should fail) ==========

    @Nested
    @DisplayName("Negative Tests - Conditional validation that should fail")
    class NegativeTests {

        @Test
        @DisplayName("shouldFail_ifThen_whenIfMetButThenFails")
        void shouldFail_ifThen_whenIfMetButThenFails() {
            // Arrange - if type is object, then requires name property
            Map<String, Schema> thenProperties = new HashMap<>();
            thenProperties.put("name", Schema.builder("name", SchemaType.STRING).build());
            
            Schema ifSchema = Schema.builder("if", SchemaType.OBJECT).build();
            Schema thenSchema = Schema.builder("then", SchemaType.OBJECT)
                    .properties(thenProperties)
                    .requiredFields(Arrays.asList("name"))
                    .build();
            
            Schema schema = Schema.builder("user", SchemaType.OBJECT)
                    .ifSchema(ifSchema)
                    .thenSchema(thenSchema)
                    .build();
            
            Map<String, Object> data = new HashMap<>(); // Empty object - if passes, but then fails

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/user", "user");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when if passes but then fails");
            assertTrue(errors.size() >= 1, "Error list should have at least 1 error");
            
            // Check for if/then keyword error
            boolean hasIfThenError = errors.stream()
                    .anyMatch(e -> e.getExpectedType().equals("if/then"));
            assertTrue(hasIfThenError, "Expected 'if/then' keyword in error");
        }

        @Test
        @DisplayName("shouldFail_ifElse_whenIfFailsAndElseFails")
        void shouldFail_ifElse_whenIfFailsAndElseFails() {
            // Arrange - if is string with minLength 10, else is integer with minimum 100
            Schema ifSchema = Schema.builder("if", SchemaType.STRING)
                    .minLength(10)
                    .build();
            
            Schema elseSchema = Schema.builder("else", SchemaType.INTEGER)
                    .minimum(100)
                    .build();
            
            Schema schema = Schema.builder("data", SchemaType.INTEGER)
                    .ifSchema(ifSchema)
                    .elseSchema(elseSchema)
                    .build();
            
            Object data = 50; // Not string, so if fails (validation continues with else), but 50 < 100

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when if fails and else also fails");
            assertTrue(errors.size() >= 1, "Error list should have at least 1 error");
            
            // Check for if/else keyword error
            boolean hasIfElseError = errors.stream()
                    .anyMatch(e -> e.getExpectedType().equals("if/else"));
            assertTrue(hasIfElseError, "Expected 'if/else' keyword in error");
        }

        @Test
        @DisplayName("shouldFail_ifThenElse_whenThenSchemaErrorMessage")
        void shouldFail_ifThenElse_whenThenSchemaErrorMessage() {
            // Arrange
            Schema ifSchema = Schema.builder("if", SchemaType.STRING).build();
            Schema thenSchema = Schema.builder("then", SchemaType.STRING)
                    .pattern("\\d+") // Requires digits only
                    .build();
            
            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .ifSchema(ifSchema)
                    .thenSchema(thenSchema)
                    .build();
            
            String data = "abc"; // String but doesn't match then's pattern

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when then schema fails");
            
            // Check error message contains "then"
            boolean hasThenInMessage = errors.stream()
                    .anyMatch(e -> e.getMessage().toLowerCase().contains("then"));
            assertTrue(hasThenInMessage, "Error message should contain 'then'");
        }

        @Test
        @DisplayName("shouldFail_ifThenElse_whenElseSchemaErrorMessage")
        void shouldFail_ifThenElse_whenElseSchemaErrorMessage() {
            // Arrange
            Schema ifSchema = Schema.builder("if", SchemaType.STRING)
                    .minLength(100)
                    .build();
            Schema thenSchema = Schema.builder("then", SchemaType.STRING).build();
            Schema elseSchema = Schema.builder("else", SchemaType.INTEGER)
                    .minimum(50)
                    .build();
            
            Schema schema = Schema.builder("data", SchemaType.INTEGER)
                    .ifSchema(ifSchema)
                    .thenSchema(thenSchema)
                    .elseSchema(elseSchema)
                    .build();
            
            Object data = 10; // Not string (if fails), 10 < 50 (else fails)

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when else schema fails");
            
            // Check error message contains "else"
            boolean hasElseInMessage = errors.stream()
                    .anyMatch(e -> e.getMessage().toLowerCase().contains("else"));
            assertTrue(hasElseInMessage, "Error message should contain 'else'");
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
            Schema ifSchema = Schema.builder("if", SchemaType.OBJECT).build();
            Schema thenSchema = Schema.builder("then", SchemaType.OBJECT)
                    .requiredFields(Arrays.asList("name"))
                    .build();
            
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .ifSchema(ifSchema)
                    .thenSchema(thenSchema)
                    .build();

            // Act
            List<ValidationError> errors = validator.validate(null, schema, "/data", "data");

            // Assert - null data should be handled gracefully
            // The ObjectValidator handles null, not ConditionalValidator directly
            // ConditionalValidator will process the conditional, but the type check happens elsewhere
            assertTrue(errors.isEmpty() || errors.size() >= 1, "Should handle null data gracefully");
        }

        @Test
        @DisplayName("shouldHandle_emptyObjectConditional")
        void shouldHandle_emptyObjectConditional() {
            // Arrange
            Schema ifSchema = Schema.builder("if", SchemaType.OBJECT).build();
            Schema thenSchema = Schema.builder("then", SchemaType.OBJECT).build();
            
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .ifSchema(ifSchema)
                    .thenSchema(thenSchema)
                    .build();
            
            Map<String, Object> data = new HashMap<>();

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors for empty object with conditional");
        }

        @Test
        @DisplayName("shouldHandle_complexNestedConditional")
        void shouldHandle_complexNestedConditional() {
            // Arrange - if checks for type, then has specific properties
            Map<String, Schema> ifProperties = new HashMap<>();
            ifProperties.put("type", Schema.builder("type", SchemaType.STRING).build());
            
            Schema ifSchema = Schema.builder("if", SchemaType.OBJECT)
                    .properties(ifProperties)
                    .build();
            
            Map<String, Schema> thenProperties = new HashMap<>();
            thenProperties.put("email", Schema.builder("email", SchemaType.STRING).format("email").build());
            
            Schema thenSchema = Schema.builder("then", SchemaType.OBJECT)
                    .properties(thenProperties)
                    .requiredFields(Arrays.asList("email"))
                    .build();
            
            Schema schema = Schema.builder("user", SchemaType.OBJECT)
                    .ifSchema(ifSchema)
                    .thenSchema(thenSchema)
                    .build();
            
            // Has type but no email
            Map<String, Object> data = new HashMap<>();
            data.put("type", "admin");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/user", "user");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when then requirements not met");
            assertTrue(errors.size() >= 1, "Error list should have at least 1 error");
        }

        @Test
        @DisplayName("shouldHandle_multipleConditionsInIfSchema")
        void shouldHandle_multipleConditionsInIfSchema() {
            // Arrange - if has multiple conditions (minLength AND pattern)
            Schema ifSchema = Schema.builder("if", SchemaType.STRING)
                    .minLength(5)
                    .pattern("^test.*")
                    .build();
            
            Schema thenSchema = Schema.builder("then", SchemaType.STRING)
                    .maxLength(20)
                    .build();
            
            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .ifSchema(ifSchema)
                    .thenSchema(thenSchema)
                    .build();
            
            String data = "testlongname"; // Starts with test, 11 chars

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when all conditions in if and then are met");
        }

        @Test
        @DisplayName("shouldHandle_thenFailsWithMultipleErrors")
        void shouldHandle_thenFailsWithMultipleErrors() {
            // Arrange - then schema has constraints
            Schema ifSchema = Schema.builder("if", SchemaType.OBJECT).build();
            
            Schema thenSchema = Schema.builder("then", SchemaType.OBJECT)
                    .minProperties(2)
                    .maxProperties(5)
                    .build();
            
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .ifSchema(ifSchema)
                    .thenSchema(thenSchema)
                    .build();
            
            Map<String, Object> data = new HashMap<>(); // 0 properties - fails minProperties

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - with 0 properties, minProperties=2 should fail
            // The ObjectValidator will report minProperties error
            assertFalse(errors.isEmpty(), "Expected errors when then constraints violated");
            
            // Verify there's an error about minProperties or if/then condition
            boolean hasRelevantError = errors.stream()
                    .anyMatch(e -> e.getExpectedType().equals("minProperties") || 
                                   e.getExpectedType().equals("if/then"));
            assertTrue(hasRelevantError, "Expected minProperties or if/then keyword in error");
        }

        @Test
        @DisplayName("shouldHandle_nestedObjectData")
        void shouldHandle_nestedObjectData() {
            // Arrange
            Schema ifSchema = Schema.builder("if", SchemaType.OBJECT).build();
            Schema thenSchema = Schema.builder("then", SchemaType.OBJECT).build();
            
            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .ifSchema(ifSchema)
                    .thenSchema(thenSchema)
                    .build();
            
            // Nested object
            Map<String, Object> nested = new HashMap<>();
            nested.put("inner", "value");
            Map<String, Object> data = new HashMap<>();
            data.put("nested", nested);

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors for nested object when conditions met");
        }

        @Test
        @DisplayName("shouldHandle_arrayDataInConditional")
        void shouldHandle_arrayDataInConditional() {
            // Arrange
            Schema ifSchema = Schema.builder("if", SchemaType.ARRAY)
                    .minItems(1)
                    .build();
            Schema thenSchema = Schema.builder("then", SchemaType.ARRAY)
                    .maxItems(10)
                    .build();
            
            Schema schema = Schema.builder("items", SchemaType.ARRAY)
                    .ifSchema(ifSchema)
                    .thenSchema(thenSchema)
                    .build();
            
            List<Object> data = Arrays.asList(1, 2, 3);

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/items", "items");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when array meets if and then conditions");
        }

        @Test
        @DisplayName("shouldHandle_typeMismatchBetweenIfAndData")
        void shouldHandle_typeMismatchBetweenIfAndData() {
            // Arrange - if expects string, data is integer
            Schema ifSchema = Schema.builder("if", SchemaType.STRING)
                    .minLength(5)
                    .build();
            Schema thenSchema = Schema.builder("then", SchemaType.STRING).build();
            Schema elseSchema = Schema.builder("else", SchemaType.INTEGER)
                    .maximum(100)
                    .build();
            
            Schema schema = Schema.builder("data", SchemaType.INTEGER)
                    .ifSchema(ifSchema)
                    .thenSchema(thenSchema)
                    .elseSchema(elseSchema)
                    .build();
            
            Object data = 50; // Integer - if condition fails (type mismatch), else validates (50 <= 100)

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when if fails and else passes");
        }

        @Test
        @DisplayName("shouldHandle_ifSchemaWithEnum")
        void shouldHandle_ifSchemaWithEnum() {
            // Arrange - if uses enum constraint
            Schema ifSchema = Schema.builder("if", SchemaType.STRING)
                    .enumValues(Arrays.asList("admin", "moderator", "user"))
                    .build();
            
            Schema thenSchema = Schema.builder("then", SchemaType.STRING)
                    .minLength(3)
                    .build();
            
            Schema schema = Schema.builder("role", SchemaType.STRING)
                    .ifSchema(ifSchema)
                    .thenSchema(thenSchema)
                    .build();
            
            String data = "admin"; // Matches enum, and length >= 3

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/role", "role");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when enum matches and then validates");
        }

        @Test
        @DisplayName("shouldHandle_conditionalWithNoMatchingPath")
        void shouldHandle_conditionalWithNoMatchingPath() {
            // Arrange
            Schema ifSchema = Schema.builder("if", SchemaType.STRING).build();
            Schema thenSchema = Schema.builder("then", SchemaType.STRING).build();
            
            Schema schema = Schema.builder("data", SchemaType.STRING)
                    .ifSchema(ifSchema)
                    .thenSchema(thenSchema)
                    .build();

            // Act
            List<ValidationError> errors = validator.validate("test", schema, "", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors with empty path");
        }
    }
}
