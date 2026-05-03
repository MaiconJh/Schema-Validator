package com.maiconjh.schemacr.schemes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Schema class.
 */
@DisplayName("Schema")
class SchemaTest {

    // ========== POSITIVE TESTS ==========

    @Nested
    @DisplayName("Positive Scenarios")
    class PositiveScenarios {

        @Test
        @DisplayName("shouldCreateSchemaWithBuilder - criar schema usando builder")
        void shouldCreateSchemaWithBuilder() {
            // Arrange
            String schemaName = "userSchema";
            SchemaType schemaType = SchemaType.OBJECT;

            // Act
            Schema schema = Schema.builder(schemaName, schemaType)
                    .title("User Schema")
                    .description("Schema for user validation")
                    .build();

            // Assert
            assertNotNull(schema, "Schema should not be null");
            assertEquals(schemaName, schema.getName(), "Name should match");
            assertEquals(schemaType, schema.getType(), "Type should match");
            assertEquals("User Schema", schema.getTitle(), "Title should match");
            assertEquals("Schema for user validation", schema.getDescription(), "Description should match");
        }

        @Test
        @DisplayName("shouldSetAllProperties - configurar todas as propriedades do schema")
        void shouldSetAllProperties() {
            // Arrange
            Map<String, Schema> properties = new HashMap<>();
            properties.put("name", Schema.builder("name", SchemaType.STRING)
                    .minLength(1)
                    .maxLength(100)
                    .build());
            properties.put("age", Schema.builder("age", SchemaType.INTEGER)
                    .minimum(0)
                    .maximum(150)
                    .build());

            Map<String, Schema> patternProperties = new HashMap<>();
            patternProperties.put("^s_", Schema.builder("s_property", SchemaType.STRING).build());

            Schema itemSchema = Schema.builder("item", SchemaType.STRING).build();
            Schema propertyNamesSchema = Schema.builder("propertyNames", SchemaType.STRING).pattern("^[a-z]+$").build();
            Schema containsSchema = Schema.builder("contains", SchemaType.INTEGER).build();

            // Act
            Schema schema = Schema.builder("testSchema", SchemaType.OBJECT)
                    .title("Test Schema")
                    .description("A comprehensive test schema")
                    .id("http://example.com/schema/test")
                    .version("1.0.0")
                    .properties(properties)
                    .patternProperties(patternProperties)
                    .itemSchema(itemSchema)
                    .requiredFields(Arrays.asList("name", "age"))
                    .additionalProperties(false)
                    .minimum(10)
                    .maximum(100)
                    .exclusiveMinimum(true)
                    .exclusiveMaximum(false)
                    .minLength(5)
                    .maxLength(200)
                    .pattern("^[a-zA-Z]+$")
                    .format("email")
                    .multipleOf(2)
                    .enumValues(Arrays.asList("value1", "value2", "value3"))
                    .schemaDialect("http://json-schema.org/draft-07/schema#")
                    .ref("#/definitions/user")
                    .compatibility("compatible")
                    .minItems(1)
                    .maxItems(10)
                    .uniqueItems(true)
                    .containsSchema(containsSchema)
                    .minContains(1)
                    .maxContains(2)
                    .minProperties(2)
                    .maxProperties(10)
                    .propertyNamesSchema(propertyNamesSchema)
                    .readOnly(false)
                    .writeOnly(false)
                    .constValue("fixedValue")
                    .build();

            // Assert
            assertNotNull(schema);
            assertEquals("testSchema", schema.getName());
            assertEquals(SchemaType.OBJECT, schema.getType());
            assertEquals("Test Schema", schema.getTitle());
            assertEquals("A comprehensive test schema", schema.getDescription());
            assertEquals("http://example.com/schema/test", schema.getId());
            assertEquals("1.0.0", schema.getVersion());
            assertEquals(2, schema.getProperties().size());
            assertTrue(schema.getProperties().containsKey("name"));
            assertTrue(schema.getProperties().containsKey("age"));
            assertEquals(1, schema.getPatternProperties().size());
            assertNotNull(schema.getItemSchema());
            assertEquals(2, schema.getRequiredFields().size());
            assertFalse(schema.isAdditionalPropertiesAllowed());
            assertEquals(10, schema.getMinimum());
            assertEquals(100, schema.getMaximum());
            assertTrue(schema.isExclusiveMinimum());
            assertFalse(schema.isExclusiveMaximum());
            assertEquals(5, schema.getMinLength());
            assertEquals(200, schema.getMaxLength());
            assertEquals("^[a-zA-Z]+$", schema.getPattern());
            assertEquals("email", schema.getFormat());
            assertEquals(2, schema.getMultipleOf());
            assertEquals(3, schema.getEnumValues().size());
            assertEquals("http://json-schema.org/draft-07/schema#", schema.getSchemaDialect());
            assertEquals("#/definitions/user", schema.getRef());
            assertTrue(schema.isRef());
            assertEquals("compatible", schema.getCompatibility());
            assertEquals(1, schema.getMinItems());
            assertEquals(10, schema.getMaxItems());
            assertTrue(schema.isUniqueItems());
            assertNotNull(schema.getContainsSchema());
            assertEquals(1, schema.getMinContains());
            assertEquals(2, schema.getMaxContains());
            assertEquals(2, schema.getMinProperties());
            assertEquals(10, schema.getMaxProperties());
            assertNotNull(schema.getPropertyNamesSchema());
            assertFalse(schema.isReadOnly());
            assertFalse(schema.isWriteOnly());
            assertEquals("fixedValue", schema.getConstValue());
            assertTrue(schema.hasConst());
        }
    }

    // ========== NEGATIVE TESTS ==========

    @Nested
    @DisplayName("Negative Scenarios")
    class NegativeScenarios {

        @Test
        @DisplayName("shouldFailWithInvalidType - tipo inválido")
        void shouldFailWithInvalidType() {
            // Arrange & Act - criar schema com tipo null
            Schema schema = Schema.builder("invalidSchema", null)
                    .title("Invalid Schema")
                    .build();

            // Assert - tipo deve ser null (inválido)
            assertNotNull(schema);
            assertNull(schema.getType(), "Type should be null for invalid type");
            assertFalse(schema.getType() == SchemaType.OBJECT, "Type should not be OBJECT");
            assertFalse(schema.getType() == SchemaType.STRING, "Type should not be STRING");
        }
    }

    // ========== EDGE CASES ==========

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("shouldHandleNullValues - valores nulos")
        void shouldHandleNullValues() {
            // Arrange & Act - criar schema com valores null
            Schema schema = Schema.builder("nullTestSchema", SchemaType.OBJECT)
                    .title(null)
                    .description(null)
                    .id(null)
                    .version(null)
                    .pattern(null)
                    .format(null)
                    .ref(null)
                    .properties(null)
                    .patternProperties(null)
                    .itemSchema(null)
                    .requiredFields(null)
                    .additionalProperties(null)
                    .minimum(null)
                    .maximum(null)
                    .minLength(null)
                    .maxLength(null)
                    .multipleOf(null)
                    .enumValues(null)
                    .build();

            // Assert - valores nulos devem ser tratados corretamente
            assertNotNull(schema);
            assertNull(schema.getTitle(), "Title should be null");
            assertNull(schema.getDescription(), "Description should be null");
            assertNull(schema.getId(), "Id should be null");
            assertNull(schema.getVersion(), "Version should be null");
            assertNull(schema.getPattern(), "Pattern should be null");
            assertNull(schema.getFormat(), "Format should be null");
            assertNull(schema.getRef(), "Ref should be null");
            assertTrue(schema.getProperties().isEmpty(), "Properties should be empty map (not null)");
            assertTrue(schema.getPatternProperties().isEmpty(), "Pattern properties should be empty map");
            assertNull(schema.getItemSchema(), "Item schema should be null");
            assertTrue(schema.getRequiredFields().isEmpty(), "Required fields should be empty list");
            assertTrue(schema.isAdditionalPropertiesAllowed(), "Additional properties should default to true");
            assertNull(schema.getMinimum(), "Minimum should be null");
            assertNull(schema.getMaximum(), "Maximum should be null");
            assertNull(schema.getMinLength(), "Min length should be null");
            assertNull(schema.getMaxLength(), "Max length should be null");
            assertNull(schema.getMultipleOf(), "MultipleOf should be null");
            assertTrue(schema.getEnumValues().isEmpty(), "Enum values should be empty list");
        }

        @Test
        @DisplayName("shouldHandleDefaultValues - valores padrão")
        void shouldHandleDefaultValues() {
            // Arrange & Act - criar schema sem propriedades opcionais
            Schema schema = Schema.builder("defaultsSchema", SchemaType.STRING)
                    .build();

            // Assert - valores padrão devem ser aplicados
            assertNotNull(schema);
            assertEquals(SchemaType.STRING, schema.getType());
            assertTrue(schema.getProperties().isEmpty(), "Properties should default to empty");
            assertTrue(schema.getPatternProperties().isEmpty(), "Pattern properties should default to empty");
            assertNull(schema.getItemSchema(), "Item schema should be null");
            assertTrue(schema.getRequiredFields().isEmpty(), "Required fields should default to empty");
            assertTrue(schema.isAdditionalPropertiesAllowed(), "Additional properties should default to true");
            assertFalse(schema.isExclusiveMinimum(), "Exclusive minimum should default to false");
            assertFalse(schema.isExclusiveMaximum(), "Exclusive maximum should default to false");
            assertTrue(schema.getEnumValues().isEmpty(), "Enum values should default to empty");
            assertFalse(schema.isRef(), "Ref should default to false");
            assertFalse(schema.hasAllOf(), "allOf should default to false");
            assertFalse(schema.hasAnyOf(), "anyOf should default to false");
            assertFalse(schema.hasOneOf(), "oneOf should default to false");
            assertFalse(schema.hasNot(), "not should default to false");
            assertFalse(schema.hasConditional(), "Conditional should default to false");
            assertFalse(schema.hasArrayConstraints(), "Array constraints should default to false");
            assertFalse(schema.hasObjectConstraints(), "Object constraints should default to false");
            assertFalse(schema.hasConst(), "Const should default to false");
            assertFalse(schema.hasReadWriteOnly(), "Read/Write only should default to false");
            assertFalse(schema.hasFormat(), "Format should default to false");
        }

        @Test
        @DisplayName("shouldKeepConstNullAsPresent - const null deve ser tratado como presente")
        void shouldKeepConstNullAsPresent() {
            Schema schema = Schema.builder("nullConst", SchemaType.NULL)
                    .constValue(null)
                    .build();

            assertTrue(schema.hasConst(), "Const should be marked as present even when null");
            assertNull(schema.getConstValue(), "Const value should remain null");
        }

        @Test
        @DisplayName("shouldDefensivelyCopyCollections - coleções devem ser copiadas defensivamente")
        void shouldDefensivelyCopyCollections() {
            List<String> required = new ArrayList<>(List.of("name"));
            Map<String, List<String>> dependentRequired = new HashMap<>();
            dependentRequired.put("credit_card", new ArrayList<>(List.of("billing_address")));

            Schema schema = Schema.builder("defensiveCopy", SchemaType.OBJECT)
                    .requiredFields(required)
                    .dependentRequired(dependentRequired)
                    .build();

            required.add("mutated");
            dependentRequired.get("credit_card").add("mutated");

            assertEquals(List.of("name"), schema.getRequiredFields());
            assertEquals(List.of("billing_address"), schema.getDependentRequired().get("credit_card"));
        }

        @Test
        @DisplayName("shouldValidateSchemaStructure - estrutura do schema")
        void shouldValidateSchemaStructure() {
            // Arrange - criar schema com estrutura complexa
            Schema nestedSchema = Schema.builder("nested", SchemaType.STRING)
                    .minLength(1)
                    .build();

            Map<String, Schema> props = new HashMap<>();
            props.put("field1", Schema.builder("field1", SchemaType.STRING).build());
            props.put("field2", Schema.builder("field2", SchemaType.INTEGER).minimum(0).build());
            props.put("nested", nestedSchema);

            Schema allOfItem = Schema.builder("allOfItem", SchemaType.OBJECT)
                    .title("AllOf Item")
                    .build();

            Schema anyOfItem = Schema.builder("anyOfItem", SchemaType.STRING)
                    .enumValues(Arrays.asList("option1", "option2"))
                    .build();

            Schema notSchema = Schema.builder("notSchema", SchemaType.NULL)
                    .build();

            Schema ifSchema = Schema.builder("ifSchema", SchemaType.BOOLEAN)
                    .build();

            Schema thenSchema = Schema.builder("thenSchema", SchemaType.STRING)
                    .minLength(5)
                    .build();

            Schema elseSchema = Schema.builder("elseSchema", SchemaType.STRING)
                    .minLength(1)
                    .build();

            // Act
            Schema schema = Schema.builder("complexSchema", SchemaType.OBJECT)
                    .title("Complex Schema")
                    .description("A complex schema with all structural elements")
                    .properties(props)
                    .requiredFields(Arrays.asList("field1", "field2"))
                    .additionalProperties(false)
                    .allOf(Collections.singletonList(allOfItem))
                    .anyOf(Collections.singletonList(anyOfItem))
                    .oneOf(Arrays.asList(
                            Schema.builder("oneOf1", SchemaType.STRING).build(),
                            Schema.builder("oneOf2", SchemaType.NUMBER).build()
                    ))
                    .notSchema(notSchema)
                    .ifSchema(ifSchema)
                    .thenSchema(thenSchema)
                    .elseSchema(elseSchema)
                    .build();

            // Assert - validar estrutura completa
            assertNotNull(schema);
            assertEquals(SchemaType.OBJECT, schema.getType());
            assertEquals("Complex Schema", schema.getTitle());
            assertEquals("A complex schema with all structural elements", schema.getDescription());

            // Validate properties structure
            assertEquals(3, schema.getProperties().size());
            assertTrue(schema.getProperties().containsKey("field1"));
            assertTrue(schema.getProperties().containsKey("field2"));
            assertTrue(schema.getProperties().containsKey("nested"));

            // Validate nested schema
            Schema retrievedNested = schema.getProperties().get("nested");
            assertNotNull(retrievedNested);
            assertEquals(1, retrievedNested.getMinLength());

            // Validate required fields
            assertEquals(2, schema.getRequiredFields().size());
            assertTrue(schema.getRequiredFields().contains("field1"));
            assertTrue(schema.getRequiredFields().contains("field2"));

            // Validate structural keywords
            assertTrue(schema.hasAllOf());
            assertEquals(1, schema.getAllOf().size());

            assertTrue(schema.hasAnyOf());
            assertEquals(1, schema.getAnyOf().size());

            assertTrue(schema.hasOneOf());
            assertEquals(2, schema.getOneOf().size());

            assertTrue(schema.hasNot());
            assertNotNull(schema.getNot());

            assertTrue(schema.hasConditional());
            assertNotNull(schema.getIfSchema());
            assertNotNull(schema.getThenSchema());
            assertNotNull(schema.getElseSchema());

            // Validate additional properties
            assertFalse(schema.isAdditionalPropertiesAllowed());
        }

        @Test
        @DisplayName("shouldStoreP1P2KeywordsInSchemaModel")
        void shouldStoreP1P2KeywordsInSchemaModel() {
            Schema schema = Schema.builder("p1p2", SchemaType.STRING)
                    .defaultValue("v1")
                    .examples(Arrays.asList("v1", "v2"))
                    .deprecated(true)
                    .contentEncoding("base64")
                    .contentMediaType("application/json")
                    .contentSchema(Schema.builder("content", SchemaType.OBJECT).build())
                    .unevaluatedItemsAllowed(false)
                    .unevaluatedPropertiesAllowed(false)
                    .dynamicRef("#/$defs/node")
                    .dynamicAnchor("node")
                    .build();

            assertEquals("v1", schema.getDefaultValue());
            assertEquals(2, schema.getExamples().size());
            assertTrue(Boolean.TRUE.equals(schema.isDeprecated()));
            assertEquals("base64", schema.getContentEncoding());
            assertEquals("application/json", schema.getContentMediaType());
            assertNotNull(schema.getContentSchema());
            assertTrue(Boolean.FALSE.equals(schema.isUnevaluatedItemsAllowed()));
            assertTrue(Boolean.FALSE.equals(schema.isUnevaluatedPropertiesAllowed()));
            assertEquals("#/$defs/node", schema.getDynamicRef());
            assertEquals("node", schema.getDynamicAnchor());
        }

        @Test
        @DisplayName("shouldStoreDefsAndResolveLocalReferencePath")
        void shouldStoreDefsAndResolveLocalReferencePath() {
            Schema positive = Schema.builder("positiveInteger", SchemaType.INTEGER)
                    .exclusiveMinimum(0)
                    .build();

            Schema root = Schema.builder("root", SchemaType.ARRAY)
                    .defs(Map.of("positiveInteger", positive))
                    .itemSchema(Schema.builder("item", SchemaType.ANY)
                            .ref("#/$defs/positiveInteger")
                            .build())
                    .build();

            assertTrue(root.hasDefs());
            assertSame(positive, root.getDefs().get("positiveInteger"));
        }
    }
}
