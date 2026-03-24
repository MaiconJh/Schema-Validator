package com.maiconjh.schemacr.validation;

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
 * Unit tests for PrimitiveValidator.
 * 
 * <p>Tests the primitive type validation logic (string, number, integer, boolean, null).</p>
 */
@DisplayName("PrimitiveValidator Tests")
class PrimitiveValidatorTest {

    private PrimitiveValidator validator;
    private Schema schema;

    @BeforeEach
    void setUp() {
        validator = new PrimitiveValidator();
    }

    // ========== POSITIVE TESTS (Valid inputs) ==========

    @Test
    @DisplayName("shouldPass_whenValueMatchesStringType")
    void shouldPass_whenValueMatchesStringType() {
        // Arrange
        // Schema: type: "string"
        // Valid: value is a string
        
        schema = Schema.builder("name", SchemaType.STRING)
                .build();
        
        Object data = "hello world";

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/name", "name");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when value is a string");
    }

    @Test
    @DisplayName("shouldPass_whenValueMatchesIntegerType")
    void shouldPass_whenValueMatchesIntegerType() {
        // Arrange
        // Schema: type: "integer"
        // Valid: value is an integer
        
        schema = Schema.builder("age", SchemaType.INTEGER)
                .build();
        
        Object data = 42;

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/age", "age");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when value is an integer");
    }

    @Test
    @DisplayName("shouldPass_whenValueMatchesNumberType")
    void shouldPass_whenValueMatchesNumberType() {
        // Arrange
        // Schema: type: "number"
        // Valid: value is a number (including decimals)
        
        schema = Schema.builder("price", SchemaType.NUMBER)
                .build();
        
        Object data = 19.99;

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/price", "price");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when value is a number");
    }

    @Test
    @DisplayName("shouldPass_whenValueMatchesBooleanType")
    void shouldPass_whenValueMatchesBooleanType() {
        // Arrange
        // Schema: type: "boolean"
        // Valid: value is a boolean
        
        schema = Schema.builder("active", SchemaType.BOOLEAN)
                .build();
        
        Object data = true;

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/active", "active");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when value is a boolean");
    }

    @Test
    @DisplayName("shouldPass_whenValueMatchesNullType")
    void shouldPass_whenValueMatchesNullType() {
        // Arrange
        // Schema: type: "null"
        // Valid: value is null
        
        schema = Schema.builder("optional", SchemaType.NULL)
                .build();
        
        Object data = null;

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/optional", "optional");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when value is null");
    }

    // ========== NEGATIVE TESTS (Invalid inputs) ==========

    @Test
    @DisplayName("shouldFail_whenValueTypeMismatch")
    void shouldFail_whenValueTypeMismatch() {
        // Arrange
        // Schema: type: "string"
        // Invalid: value is integer
        
        schema = Schema.builder("name", SchemaType.STRING)
                .build();
        
        Object data = 123;

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/name", "name");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors when value type mismatch");
        assertEquals(1, errors.size(), "Expected exactly one error");
        
        ValidationError error = errors.get(0);
        assertEquals("string", error.getExpectedType(), "Expected keyword to be 'string'");
        assertEquals("Primitive value does not match schema type.", error.getMessage(), "Expected correct error message");
    }

    @Test
    @DisplayName("shouldFail_whenInvalidTypeForString")
    void shouldFail_whenInvalidTypeForString() {
        // Arrange
        // Schema: type: "string"
        // Invalid: value is boolean instead of string
        
        schema = Schema.builder("status", SchemaType.STRING)
                .build();
        
        Object data = true;

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/status", "status");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors when boolean is provided for string type");
        assertEquals(1, errors.size(), "Expected exactly one error");
        
        ValidationError error = errors.get(0);
        assertEquals("string", error.getExpectedType(), "Expected keyword to be 'string'");
        assertEquals("Primitive value does not match schema type.", error.getMessage(), "Expected correct error message");
    }

    // ========== EDGE CASES (Boundary conditions) ==========

    @Test
    @DisplayName("shouldPass_whenNullableAllowsNull")
    void shouldPass_whenNullableAllowsNull() {
        // Arrange
        // Schema: type: "null"
        // Valid: null value is allowed for null type
        
        schema = Schema.builder("nullableField", SchemaType.NULL)
                .build();
        
        Object data = null;

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/nullableField", "nullableField");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when null is provided for null type");
    }

    @Test
    @DisplayName("shouldHandleMultipleTypes")
    void shouldHandleMultipleTypes() {
        // Arrange
        // Schema: type: ["string", "number"] (using typeList for union type)
        // Note: PrimitiveValidator currently only validates the primary type, not typeList
        
        schema = Schema.builder("flexField", SchemaType.STRING)
                .typeList(Arrays.asList("string", "number"))
                .build();
        
        // First test: string value should pass (matches primary type)
        Object dataString = "hello";

        // Act
        List<ValidationError> errorsString = validator.validate(dataString, schema, "/flexField", "flexField");

        // Assert - string value should pass (type matches primary type)
        assertTrue(errorsString.isEmpty(), "Expected no validation errors for string value");
        
        // Second test: number value - fails because validator only checks primary type (STRING)
        // This documents current behavior where typeList is NOT validated by PrimitiveValidator
        Object dataNumber = 42;
        
        // Act
        List<ValidationError> errorsNumber = validator.validate(dataNumber, schema, "/flexField", "flexField");
        
        // Assert - Current behavior: fails because primary type is STRING
        // The typeList is ignored - this test documents the limitation
        assertFalse(errorsNumber.isEmpty(), "Expected validation error because typeList is not validated");
        assertEquals(1, errorsNumber.size(), "Expected exactly one error");
        
        ValidationError error = errorsNumber.get(0);
        assertEquals("string", error.getExpectedType(), "Expected keyword to be 'string' (primary type)");
    }

    // Additional edge case tests for comprehensive coverage

    @Test
    @DisplayName("shouldPass_whenAnyTypeAcceptsAnyValue")
    void shouldPass_whenAnyTypeAcceptsAnyValue() {
        // Arrange
        // Schema: type: "any" - accepts any value
        
        schema = Schema.builder("anyField", SchemaType.ANY)
                .build();
        
        // Test with different types
        Object data = "any value can go here";

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/anyField", "anyField");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors for any type");
    }

    @Test
    @DisplayName("shouldPass_whenLongIntegerValueMatchesIntegerType")
    void shouldPass_whenLongIntegerValueMatchesIntegerType() {
        // Arrange
        // Schema: type: "integer"
        // Valid: Long value that is a valid integer
        
        schema = Schema.builder("bigNumber", SchemaType.INTEGER)
                .build();
        
        Object data = 10000000000L; // Long value

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/bigNumber", "bigNumber");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no validation errors when Long integer is provided");
    }

    @Test
    @DisplayName("shouldFail_whenDecimalValueProvidedForIntegerType")
    void shouldFail_whenDecimalValueProvidedForIntegerType() {
        // Arrange
        // Schema: type: "integer"
        // Invalid: decimal value is not a valid integer
        
        schema = Schema.builder("count", SchemaType.INTEGER)
                .build();
        
        Object data = 10.5;

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/count", "count");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors when decimal is provided for integer type");
        assertEquals(1, errors.size(), "Expected exactly one error");
        
        ValidationError error = errors.get(0);
        assertEquals("integer", error.getExpectedType(), "Expected keyword to be 'integer'");
    }

    @Test
    @DisplayName("shouldFail_whenStringProvidedForBooleanType")
    void shouldFail_whenStringProvidedForBooleanType() {
        // Arrange
        // Schema: type: "boolean"
        // Invalid: string "true" is not a boolean
        
        schema = Schema.builder("enabled", SchemaType.BOOLEAN)
                .build();
        
        Object data = "true"; // String, not Boolean

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/enabled", "enabled");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors when string is provided for boolean type");
        assertEquals(1, errors.size(), "Expected exactly one error");
        
        ValidationError error = errors.get(0);
        assertEquals("boolean", error.getExpectedType(), "Expected keyword to be 'boolean'");
    }

    @Test
    @DisplayName("shouldFail_whenNonNullValueProvidedForNullType")
    void shouldFail_whenNonNullValueProvidedForNullType() {
        // Arrange
        // Schema: type: "null"
        // Invalid: non-null value
        
        schema = Schema.builder("nothing", SchemaType.NULL)
                .build();
        
        Object data = "not null";

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/nothing", "nothing");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors when non-null value is provided for null type");
        assertEquals(1, errors.size(), "Expected exactly one error");
        
        ValidationError error = errors.get(0);
        assertEquals("null", error.getExpectedType(), "Expected keyword to be 'null'");
    }

    @Test
    @DisplayName("shouldFail_whenObjectProvidedForPrimitiveType")
    void shouldFail_whenObjectProvidedForPrimitiveType() {
        // Arrange
        // Schema: type: "string"
        // Invalid: object is not a primitive
        
        schema = Schema.builder("data", SchemaType.STRING)
                .build();
        
        Object data = new Object();

        // Act
        List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

        // Assert
        assertFalse(errors.isEmpty(), "Expected validation errors when object is provided for string type");
        assertEquals(1, errors.size(), "Expected exactly one error");
        
        ValidationError error = errors.get(0);
        assertEquals("string", error.getExpectedType(), "Expected keyword to be 'string'");
    }
}
