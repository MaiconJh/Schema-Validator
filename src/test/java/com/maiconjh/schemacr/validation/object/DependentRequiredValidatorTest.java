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
import com.maiconjh.schemacr.validation.ValidationError;

/**
 * Unit tests for DependentRequiredValidator.
 * 
 * <p>Tests the dependentRequired JSON Schema keyword validation logic according to
 * JSON Schema specification. Validates that when a dependent property is present,
 * the required dependent properties must also be present.</p>
 */
@DisplayName("DependentRequiredValidator Tests")
class DependentRequiredValidatorTest {

    private DependentRequiredValidator validator;

    @BeforeEach
    void setUp() {
        // Default dependentRequired: "creditCard" requires ["billingAddress"]
        Map<String, List<String>> dependentRequired = new HashMap<>();
        dependentRequired.put("creditCard", List.of("billingAddress"));
        validator = new DependentRequiredValidator(dependentRequired);
    }

    // ========== POSITIVE TESTS (Dependent properties with required dependencies present) ==========

    @Nested
    @DisplayName("Positive Tests - Dependent properties with required dependencies present")
    class PositiveTests {

        @Test
        @DisplayName("shouldPass_dependentRequired_whenDependentPropertyAndRequiredPresent")
        void shouldPass_dependentRequired_whenDependentPropertyAndRequiredPresent() {
            // Arrange - creditCard is present with billingAddress
            Map<String, List<String>> dependentRequired = new HashMap<>();
            dependentRequired.put("creditCard", List.of("billingAddress"));
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .dependentRequired(dependentRequired)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("creditCard", "1234-5678-9012-3456");
            data.put("billingAddress", "123 Main St");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when dependent property and required are present");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_dependentRequired_whenDependentPropertyNotPresent")
        void shouldPass_dependentRequired_whenDependentPropertyNotPresent() {
            // Arrange - creditCard is NOT present, so billingAddress is not required
            Map<String, List<String>> dependentRequired = new HashMap<>();
            dependentRequired.put("creditCard", List.of("billingAddress"));
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .dependentRequired(dependentRequired)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");
            data.put("email", "john@example.com");
            // creditCard is not present, so billingAddress is not required

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when dependent property is not present");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_dependentRequired_withEmptyObject")
        void shouldPass_dependentRequired_withEmptyObject() {
            // Arrange - empty object, no properties present
            Map<String, List<String>> dependentRequired = new HashMap<>();
            dependentRequired.put("creditCard", List.of("billingAddress"));
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .dependentRequired(dependentRequired)
                    .build();

            Map<String, Object> data = new HashMap<>();
            // Empty object has no dependent properties

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors for empty object");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_dependentRequired_withMultipleRequiredProperties")
        void shouldPass_dependentRequired_withMultipleRequiredProperties() {
            // Arrange - "contact" requires both "email" and "phone"
            Map<String, List<String>> dependentRequired = new HashMap<>();
            dependentRequired.put("contact", List.of("email", "phone"));
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .dependentRequired(dependentRequired)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("contact", true);
            data.put("email", "john@example.com");
            data.put("phone", "1234567890");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when all required properties are present");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldPass_dependentRequired_withMultipleDependentProperties")
        void shouldPass_dependentRequired_withMultipleDependentProperties() {
            // Arrange - multiple dependent properties with their requirements
            Map<String, List<String>> dependentRequired = new HashMap<>();
            dependentRequired.put("creditCard", List.of("billingAddress"));
            dependentRequired.put("paypal", List.of("email"));
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .dependentRequired(dependentRequired)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("creditCard", "1234-5678-9012-3456");
            data.put("billingAddress", "123 Main St");
            // paypal is not present, so email is not required

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertTrue(errors.isEmpty(), "Expected no errors when dependent properties and their requirements are met");
            assertEquals(0, errors.size(), "Error list should be empty");
        }
    }

    // ========== NEGATIVE TESTS (Dependent properties with missing required dependencies) ==========

    @Nested
    @DisplayName("Negative Tests - Dependent properties with missing required dependencies")
    class NegativeTests {

        @Test
        @DisplayName("shouldFail_dependentRequired_whenRequiredPropertyMissing")
        void shouldFail_dependentRequired_whenRequiredPropertyMissing() {
            // Arrange - creditCard is present but billingAddress is missing
            Map<String, List<String>> dependentRequired = new HashMap<>();
            dependentRequired.put("creditCard", List.of("billingAddress"));
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .dependentRequired(dependentRequired)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("creditCard", "1234-5678-9012-3456");
            // billingAddress is missing!

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when required property is missing");
            assertEquals(1, errors.size(), "Error list should have 1 error");

            // Check error details
            ValidationError error = errors.get(0);
            assertEquals("dependentRequired", error.getExpectedType(), "Expected 'dependentRequired' keyword in error");
            assertEquals("missing", error.getActualType(), "Expected 'missing' as actual type");
            assertTrue(error.getMessage().contains("creditCard"), 
                    "Error message should mention the dependent property");
            assertTrue(error.getMessage().contains("billingAddress"), 
                    "Error message should mention the required property");
        }

        @Test
        @DisplayName("shouldFail_dependentRequired_whenOneOfMultipleRequiredMissing")
        void shouldFail_dependentRequired_whenOneOfMultipleRequiredMissing() {
            // Arrange - "contact" requires both "email" and "phone", but only email is present
            Map<String, List<String>> dependentRequired = new HashMap<>();
            dependentRequired.put("contact", List.of("email", "phone"));
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .dependentRequired(dependentRequired)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("contact", true);
            data.put("email", "john@example.com");
            // phone is missing!

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when one of required properties is missing");
            assertEquals(1, errors.size(), "Error list should have 1 error for missing phone");

            // Check error details
            ValidationError error = errors.get(0);
            assertEquals("dependentRequired", error.getExpectedType(), "Expected 'dependentRequired' keyword in error");
            assertTrue(error.getMessage().contains("contact"), 
                    "Error message should mention the dependent property");
            assertTrue(error.getMessage().contains("phone"), 
                    "Error message should mention the missing required property");
        }

        @Test
        @DisplayName("shouldFail_dependentRequired_whenAllMultipleRequiredMissing")
        void shouldFail_dependentRequired_whenAllMultipleRequiredMissing() {
            // Arrange - "contact" requires both "email" and "phone", both are missing
            Map<String, List<String>> dependentRequired = new HashMap<>();
            dependentRequired.put("contact", List.of("email", "phone"));
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .dependentRequired(dependentRequired)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("contact", true);
            // Both email and phone are missing!

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when all required properties are missing");
            assertEquals(2, errors.size(), "Error list should have 2 errors (one for each missing required property)");

            // Check error details
            ValidationError error1 = errors.get(0);
            assertEquals("dependentRequired", error1.getExpectedType(), "Expected 'dependentRequired' keyword in error");
            
            ValidationError error2 = errors.get(1);
            assertEquals("dependentRequired", error2.getExpectedType(), "Expected 'dependentRequired' keyword in error");
        }

        @Test
        @DisplayName("shouldFail_dependentRequired_multipleDependentPropertiesWithMissingRequirements")
        void shouldFail_dependentRequired_multipleDependentPropertiesWithMissingRequirements() {
            // Arrange - both creditCard and paypal are present but their requirements are missing
            Map<String, List<String>> dependentRequired = new HashMap<>();
            dependentRequired.put("creditCard", List.of("billingAddress"));
            dependentRequired.put("paypal", List.of("email"));
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .dependentRequired(dependentRequired)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("creditCard", "1234-5678-9012-3456");
            data.put("paypal", "john@paypal.com");
            // Both billingAddress and email are missing!

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when multiple dependent requirements are missing");
            assertEquals(2, errors.size(), "Error list should have 2 errors");
        }

        @Test
        @DisplayName("shouldFail_dependentRequired_errorMessageContainsBothProperties")
        void shouldFail_dependentRequired_errorMessageContainsBothProperties() {
            // Arrange
            Map<String, List<String>> dependentRequired = new HashMap<>();
            dependentRequired.put("creditCard", List.of("billingAddress", "zipcode"));
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .dependentRequired(dependentRequired)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("creditCard", "1234-5678-9012-3456");
            // Both billingAddress and zipcode are missing!

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert
            assertFalse(errors.isEmpty(), "Expected errors when required properties are missing");
            assertEquals(2, errors.size(), "Error list should have 2 errors");

            // Check first error message
            ValidationError error = errors.get(0);
            assertTrue(error.getMessage().contains("creditCard"), 
                    "Error message should mention the dependent property 'creditCard'");
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
            Map<String, List<String>> dependentRequired = new HashMap<>();
            dependentRequired.put("creditCard", List.of("billingAddress"));
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .dependentRequired(dependentRequired)
                    .build();

            // Act
            List<ValidationError> errors = validator.validate(null, schema, "/data", "data");

            // Assert - null should be handled gracefully (not an object)
            assertTrue(errors.isEmpty(), "Expected no errors for null data");
            assertEquals(0, errors.size(), "Error list should be empty for null");
        }

        @Test
        @DisplayName("shouldHandle_nonMapData")
        void shouldHandle_nonMapData() {
            // Arrange - data is a string, not an object
            Map<String, List<String>> dependentRequired = new HashMap<>();
            dependentRequired.put("creditCard", List.of("billingAddress"));
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .dependentRequired(dependentRequired)
                    .build();

            String data = "not an object";

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - non-map data should be handled gracefully
            assertTrue(errors.isEmpty(), "Expected no errors for non-map data");
            assertEquals(0, errors.size(), "Error list should be empty for non-map");
        }

        @Test
        @DisplayName("shouldHandle_emptyDependentRequiredMap")
        void shouldHandle_emptyDependentRequiredMap() {
            // Arrange - empty dependentRequired map
            Map<String, List<String>> dependentRequired = new HashMap<>();
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .dependentRequired(dependentRequired)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("anyProperty", "value");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - no dependentRequired constraints means no validation failures
            assertTrue(errors.isEmpty(), "Expected no errors when dependentRequired map is empty");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldHandle_listDataInsteadOfMap")
        void shouldHandle_listDataInsteadOfMap() {
            // Arrange - data is a list, not a map
            Map<String, List<String>> dependentRequired = new HashMap<>();
            dependentRequired.put("creditCard", List.of("billingAddress"));
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .dependentRequired(dependentRequired)
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
        @DisplayName("shouldHandle_integerData")
        void shouldHandle_integerData() {
            // Arrange - data is an integer, not an object
            Map<String, List<String>> dependentRequired = new HashMap<>();
            dependentRequired.put("creditCard", List.of("billingAddress"));
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .dependentRequired(dependentRequired)
                    .build();

            Integer data = 42;

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - integer data should be handled gracefully
            assertTrue(errors.isEmpty(), "Expected no errors for integer data");
            assertEquals(0, errors.size(), "Error list should be empty for integer");
        }

        @Test
        @DisplayName("shouldHandle_schemaWithoutDependentRequired")
        void shouldHandle_schemaWithoutDependentRequired() {
            // Arrange - schema without dependentRequired constraint
            Map<String, List<String>> dependentRequired = new HashMap<>();
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("creditCard", "1234-5678-9012-3456");
            // No dependentRequired constraint, validation should pass

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - no constraint means no validation failure
            assertTrue(errors.isEmpty(), "Expected no errors when schema has no dependentRequired");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldHandle_singleRequiredPropertyWithEmptyList")
        void shouldHandle_singleRequiredPropertyWithEmptyList() {
            // Arrange - dependent property with empty required list
            Map<String, List<String>> dependentRequired = new HashMap<>();
            dependentRequired.put("creditCard", List.of());
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .dependentRequired(dependentRequired)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("creditCard", "1234-5678-9012-3456");
            // Empty required list means no additional requirements

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - empty required list means no validation failure
            assertTrue(errors.isEmpty(), "Expected no errors when required list is empty");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldHandle_dependentPropertyWithNullValue")
        void shouldHandle_dependentPropertyWithNullValue() {
            // Arrange - dependent property is present but has null value
            Map<String, List<String>> dependentRequired = new HashMap<>();
            dependentRequired.put("creditCard", List.of("billingAddress"));
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .dependentRequired(dependentRequired)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("creditCard", null);  // Property exists but value is null
            // billingAddress is still required!

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - property exists (even with null value), so requirements apply
            assertFalse(errors.isEmpty(), "Expected errors when dependent property exists (even with null value)");
            assertEquals(1, errors.size(), "Error list should have 1 error");
        }

        @Test
        @DisplayName("shouldHandle_dependentPropertyPresentButRequiredPropertyIsNull")
        void shouldHandle_dependentPropertyPresentButRequiredPropertyIsNull() {
            // Arrange - dependent property present, required property key exists but value is null
            Map<String, List<String>> dependentRequired = new HashMap<>();
            dependentRequired.put("creditCard", List.of("billingAddress"));
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .dependentRequired(dependentRequired)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("creditCard", "1234-5678-9012-3456");
            data.put("billingAddress", null);  // Key exists but value is null

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - billingAddress key exists (even with null value), so requirement is met
            assertTrue(errors.isEmpty(), "Expected no errors when required property key exists (even with null value)");
            assertEquals(0, errors.size(), "Error list should be empty");
        }

        @Test
        @DisplayName("shouldHandle_complexNestedDependentRequired")
        void shouldHandle_complexNestedDependentRequired() {
            // Arrange - complex case with multiple dependent properties and requirements
            Map<String, List<String>> dependentRequired = new HashMap<>();
            dependentRequired.put("shipping", List.of("address"));
            dependentRequired.put("billing", List.of("paymentMethod"));
            dependentRequired.put("membership", List.of("name", "email"));
            DependentRequiredValidator validator = new DependentRequiredValidator(dependentRequired);

            Schema schema = Schema.builder("data", SchemaType.OBJECT)
                    .dependentRequired(dependentRequired)
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("shipping", true);
            data.put("address", "123 Main St");
            data.put("billing", true);
            data.put("paymentMethod", "creditCard");
            data.put("membership", true);
            data.put("name", "John");
            data.put("email", "john@example.com");

            // Act
            List<ValidationError> errors = validator.validate(data, schema, "/data", "data");

            // Assert - all present dependent properties have their requirements met
            assertTrue(errors.isEmpty(), "Expected no errors when all dependent requirements are met");
            assertEquals(0, errors.size(), "Error list should be empty");
        }
    }
}
