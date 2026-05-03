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
        schema = Schema.builder("flexField", SchemaType.STRING)
                .typeList(Arrays.asList("string", "number"))
                .build();

        List<ValidationError> errorsString = validator.validate("hello", schema, "/flexField", "flexField");
        List<ValidationError> errorsNumber = validator.validate(42, schema, "/flexField", "flexField");
        List<ValidationError> errorsBoolean = validator.validate(true, schema, "/flexField", "flexField");

        assertTrue(errorsString.isEmpty(), "Expected no validation errors for string value");
        assertTrue(errorsNumber.isEmpty(), "Expected no validation errors for number value");
        assertFalse(errorsBoolean.isEmpty(), "Expected type-union validation failure for boolean");
        assertEquals("type", errorsBoolean.get(0).getExpectedType(), "Expected type union keyword");
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
    @DisplayName("shouldValidateNumericExclusiveMinimumDraft202012")
    void shouldValidateNumericExclusiveMinimumDraft202012() {
        schema = Schema.builder("score", SchemaType.NUMBER)
                .exclusiveMinimum(10)
                .build();

        List<ValidationError> invalid = validator.validate(10, schema, "/score", "score");
        List<ValidationError> valid = validator.validate(10.1, schema, "/score", "score");

        assertFalse(invalid.isEmpty(), "Expected equality to fail for exclusiveMinimum");
        assertTrue(valid.isEmpty(), "Expected values above exclusiveMinimum to pass");
    }

    @Test
    @DisplayName("shouldSupportLegacyBooleanExclusiveMaximum")
    void shouldSupportLegacyBooleanExclusiveMaximum() {
        schema = Schema.builder("limit", SchemaType.NUMBER)
                .maximum(5)
                .exclusiveMaximum(true)
                .build();

        List<ValidationError> invalid = validator.validate(5, schema, "/limit", "limit");
        List<ValidationError> valid = validator.validate(4.99, schema, "/limit", "limit");

        assertFalse(invalid.isEmpty(), "Expected equality to fail when legacy exclusiveMaximum=true");
        assertTrue(valid.isEmpty(), "Expected values below max to pass");
    }

    @Test
    @DisplayName("shouldValidateConstNullWhenConstKeywordIsPresent")
    void shouldValidateConstNullWhenConstKeywordIsPresent() {
        schema = Schema.builder("nullableConst", SchemaType.ANY)
                .constValue(null)
                .build();

        List<ValidationError> valid = validator.validate(null, schema, "/nullableConst", "nullableConst");
        List<ValidationError> invalid = validator.validate("x", schema, "/nullableConst", "nullableConst");

        assertTrue(valid.isEmpty(), "Expected null to pass when const is null");
        assertFalse(invalid.isEmpty(), "Expected non-null to fail when const is null");
        assertEquals("const", invalid.get(0).getExpectedType(), "Expected const keyword validation error");
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

    @Test
    @DisplayName("shouldValidateBase64ContentEncoding")
    void shouldValidateBase64ContentEncoding() {
        schema = Schema.builder("payload", SchemaType.STRING)
                .contentEncoding("base64")
                .build();

        List<ValidationError> validErrors = validator.validate("aGVsbG8=", schema, "/payload", "payload");
        List<ValidationError> invalidErrors = validator.validate("not-base64", schema, "/payload", "payload");

        assertTrue(validErrors.isEmpty(), "Expected valid base64 to pass");
        assertTrue(invalidErrors.stream().anyMatch(e -> "contentEncoding".equals(e.getExpectedType())),
                "Expected contentEncoding error for invalid base64");
    }

    @Test
    @DisplayName("shouldValidateContentSchemaForJsonMediaType")
    void shouldValidateContentSchemaForJsonMediaType() {
        Schema contentSchema = Schema.builder("content", SchemaType.OBJECT)
                .properties(java.util.Map.of("name", Schema.builder("name", SchemaType.STRING).build()))
                .requiredFields(List.of("name"))
                .build();
        schema = Schema.builder("payload", SchemaType.STRING)
                .contentMediaType("application/json")
                .contentSchema(contentSchema)
                .build();

        List<ValidationError> validErrors = validator.validate("{\"name\":\"alice\"}", schema, "/payload", "payload");
        List<ValidationError> invalidErrors = validator.validate("{\"age\":10}", schema, "/payload", "payload");

        assertTrue(validErrors.isEmpty(), "Expected JSON content matching contentSchema to pass");
        assertFalse(invalidErrors.isEmpty(), "Expected JSON content violating contentSchema to fail");
    }

    @Test
    @DisplayName("shouldValidateBase64UrlContentEncoding")
    void shouldValidateBase64UrlContentEncoding() {
        schema = Schema.builder("payload", SchemaType.STRING)
                .contentEncoding("base64url")
                .build();

        List<ValidationError> validErrors = validator.validate("eyJuYW1lIjoiYWxpY2UifQ", schema, "/payload", "payload");
        List<ValidationError> invalidErrors = validator.validate("###", schema, "/payload", "payload");

        assertTrue(validErrors.isEmpty(), "Expected valid base64url to pass");
        assertTrue(invalidErrors.stream().anyMatch(e -> "contentEncoding".equals(e.getExpectedType())),
                "Expected contentEncoding error for invalid base64url");
    }

    @Test
    @DisplayName("shouldValidateContentSchemaForPlusJsonMediaType")
    void shouldValidateContentSchemaForPlusJsonMediaType() {
        Schema contentSchema = Schema.builder("content", SchemaType.OBJECT)
                .properties(java.util.Map.of("name", Schema.builder("name", SchemaType.STRING).build()))
                .requiredFields(List.of("name"))
                .build();
        schema = Schema.builder("payload", SchemaType.STRING)
                .contentMediaType("application/ld+json")
                .contentSchema(contentSchema)
                .build();

        List<ValidationError> validErrors = validator.validate("{\"name\":\"alice\"}", schema, "/payload", "payload");
        List<ValidationError> invalidErrors = validator.validate("{\"age\":10}", schema, "/payload", "payload");

        assertTrue(validErrors.isEmpty(), "Expected +json media type content to be validated as JSON");
        assertFalse(invalidErrors.isEmpty(), "Expected +json content violating contentSchema to fail");
    }
}
