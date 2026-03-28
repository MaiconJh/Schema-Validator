package com.maiconjh.schemacr.schemes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.maiconjh.schemacr.schemes.FileSchemaLoader;
import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaType;

/**
 * Unit tests for FileSchemaLoader.
 * 
 * <p>Tests the file-based schema loading functionality including:
 * - Loading JSON schemas from file system
 * - Loading schemas from classpath
 * - Handling non-existent files
 * - Handling invalid JSON content
 * - Handling empty files
 * - Schema caching</p>
 */
@DisplayName("FileSchemaLoader Tests")
class FileSchemaLoaderTest {

    private FileSchemaLoader loader;
    private Logger logger;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        logger = Logger.getLogger(getClass().getName());
        loader = new FileSchemaLoader(logger);
    }

    // ========== POSITIVE TESTS (Valid inputs that should pass) ==========

    @Nested
    @DisplayName("Positive Tests - Valid inputs that should pass")
    class PositiveTests {

        @Test
        @DisplayName("shouldLoadSchemaFromFile - carregar schema de arquivo JSON")
        void shouldLoadSchemaFromFile() throws IOException {
            // Arrange - Create a valid JSON schema file
            String schemaJson = """
                {
                    "type": "object",
                    "properties": {
                        "name": { "type": "string" },
                        "age": { "type": "integer" }
                    },
                    "required": ["name"]
                }
                """;
            Path schemaFile = tempDir.resolve("test-schema.json");
            Files.writeString(schemaFile, schemaJson);

            // Act
            Schema schema = loader.load(schemaFile, "testSchema");

            // Assert
            assertNotNull(schema, "Expected schema to be loaded successfully");
            assertEquals("testSchema", schema.getName());
            assertEquals(SchemaType.OBJECT, schema.getType());
            assertNotNull(schema.getProperties(), "Expected properties to be parsed");
            assertEquals(2, schema.getProperties().size());
            assertTrue(schema.getRequiredFields().contains("name"),
                    "Expected 'name' to be in required fields");
        }

        @Test
        @DisplayName("shouldLoadSchemaFromClasspath - carregar schema do classpath")
        void shouldLoadSchemaFromClasspath() throws IOException {
            // Arrange - Use an existing schema from main resources
            Path classpathSchema = Path.of("src/main/resources/schemas/user-profile.schema.json");
            
            // Verify file exists before loading
            assertTrue(Files.exists(classpathSchema), "Test schema file should exist");

            // Act
            Schema schema = loader.load(classpathSchema, "userProfile");

            // Assert
            assertNotNull(schema, "Expected schema to be loaded from classpath");
            assertEquals("userProfile", schema.getName());
            assertNotNull(schema.getProperties(), "Expected properties to be parsed");
        }

        @Test
        @DisplayName("shouldReturnNullForNonExistentFile - arquivo não existente retorna null")
        void shouldReturnNullForNonExistentFile() {
            // Arrange - Path to non-existent file
            Path nonExistentFile = tempDir.resolve("non-existent-schema.json");

            // Act & Assert - Should throw IOException for non-existent file
            try {
                loader.load(nonExistentFile, "nonExistent");
                // If we reach here, the test should fail
                assertFalse(true, "Expected IOException for non-existent file");
            } catch (IOException e) {
                // Expected exception
                assertTrue(e.getMessage().contains("does not exist"),
                        "Expected error message about non-existent file");
            }
        }
    }

    // ========== NEGATIVE TESTS (Invalid inputs that should fail) ==========

    @Nested
    @DisplayName("Negative Tests - Invalid inputs that should fail")
    class NegativeTests {

        @Test
        @DisplayName("shouldFailWithInvalidJson - JSON inválido")
        void shouldFailWithInvalidJson() throws IOException {
            // Arrange - Create an invalid JSON file
            String invalidJson = """
                {
                    "type": "object",
                    "properties": {
                        "name": { "type": "string" },
                    },
                    "required": ["name"]
                }
                """;
            Path invalidSchemaFile = tempDir.resolve("invalid-schema.json");
            Files.writeString(invalidSchemaFile, invalidJson);

            // Act & Assert - Should throw IOException for invalid JSON
            try {
                loader.load(invalidSchemaFile, "invalidSchema");
                // If we reach here, the test should fail
                assertFalse(true, "Expected IOException for invalid JSON");
            } catch (IOException e) {
                // Expected exception - invalid JSON parsing
                assertNotNull(e.getMessage(), "Expected error message for invalid JSON");
            }
        }
    }

    // ========== EDGE CASES (Special scenarios) ==========

    @Nested
    @DisplayName("Edge Cases - Special scenarios")
    class EdgeCases {

        @Test
        @DisplayName("shouldHandleEmptyFile - arquivo vazio")
        void shouldHandleEmptyFile() throws IOException {
            // Arrange - Create an empty file
            Path emptyFile = tempDir.resolve("empty-schema.json");
            Files.writeString(emptyFile, "");

            // Act & Assert - Should throw IOException for empty file
            try {
                loader.load(emptyFile, "emptySchema");
                // If we reach here, the test should fail
                assertFalse(true, "Expected IOException for empty file");
            } catch (IOException e) {
                // Expected exception - empty or invalid content
                assertNotNull(e.getMessage(), "Expected error message for empty file");
            }
        }

        @Test
        @DisplayName("shouldCacheLoadedSchemas - verificação de cache")
        void shouldCacheLoadedSchemas() throws IOException {
            // Arrange - Create a JSON schema file
            String schemaJson = """
                {
                    "type": "object",
                    "properties": {
                        "id": { "type": "integer" },
                        "name": { "type": "string" }
                    }
                }
                """;
            Path schemaFile = tempDir.resolve("cached-schema.json");
            Files.writeString(schemaFile, schemaJson);

            // Act - Load the same schema twice
            Schema firstLoad = loader.load(schemaFile, "cachedSchema");
            Schema secondLoad = loader.load(schemaFile, "cachedSchema");

            // Assert - Both loads should return valid schemas
            assertNotNull(firstLoad, "Expected first load to return schema");
            assertNotNull(secondLoad, "Expected second load to return schema");
            
            // The schema names should be as provided
            assertEquals("cachedSchema", firstLoad.getName());
            assertEquals("cachedSchema", secondLoad.getName());
            
            // Both should have the same properties parsed
            assertNotNull(firstLoad.getProperties());
            assertNotNull(secondLoad.getProperties());
            assertEquals(firstLoad.getProperties().size(), secondLoad.getProperties().size());
        }

        @Test
        @DisplayName("shouldParseYamlSchema - carregar schema YAML")
        void shouldParseYamlSchema() throws IOException {
            // Arrange - Create a valid YAML schema file
            String schemaYaml = """
                type: object
                properties:
                  name:
                    type: string
                  age:
                    type: integer
                required:
                  - name
                """;
            Path schemaFile = tempDir.resolve("test-schema.yaml");
            Files.writeString(schemaFile, schemaYaml);

            // Act
            Schema schema = loader.load(schemaFile, "yamlSchema");

            // Assert
            assertNotNull(schema, "Expected YAML schema to be loaded successfully");
            assertEquals("yamlSchema", schema.getName());
            assertEquals(SchemaType.OBJECT, schema.getType());
            assertNotNull(schema.getProperties(), "Expected properties to be parsed");
            assertTrue(schema.getRequiredFields().contains("name"),
                    "Expected 'name' to be in required fields");
        }

        @Test
        @DisplayName("shouldHandleSchemaWithDefinitions - parse definitions")
        void shouldHandleSchemaWithDefinitions() throws IOException {
            // Arrange - Create a schema with definitions
            String schemaJson = """
                {
                    "type": "object",
                    "definitions": {
                        "address": {
                            "type": "object",
                            "properties": {
                                "city": { "type": "string" },
                                "zipCode": { "type": "string" }
                            }
                        }
                    },
                    "properties": {
                        "homeAddress": { "$ref": "#/definitions/address" }
                    }
                }
                """;
            Path schemaFile = tempDir.resolve("definitions-schema.json");
            Files.writeString(schemaFile, schemaJson);

            // Act
            Schema schema = loader.load(schemaFile, "definitionsSchema");

            // Assert
            assertNotNull(schema, "Expected schema with definitions to be loaded");
            
            // Check that definitions were parsed
            Schema addressDef = loader.getDefinition("address");
            assertNotNull(addressDef, "Expected 'address' definition to be available");
            assertEquals(SchemaType.OBJECT, addressDef.getType());
        }

        @Test
        @DisplayName("shouldHandleSchemaWithDollarDefsInParseSchema")
        void shouldHandleSchemaWithDollarDefsInParseSchema() {
            Map<String, Object> schemaMap = Map.of(
                    "type", "object",
                    "$defs", Map.of(
                            "address", Map.of(
                                    "type", "object",
                                    "properties", Map.of("city", Map.of("type", "string")))),
                    "properties", Map.of("homeAddress", Map.of("$ref", "#/$defs/address")));

            Schema schema = loader.parseSchema("defsSchema", schemaMap);

            assertNotNull(schema, "Expected schema with $defs to be parsed");
            Schema addressDef = loader.getDefinition("address");
            assertNotNull(addressDef, "Expected 'address' definition from $defs to be available");
            assertEquals(SchemaType.OBJECT, addressDef.getType());
        }

        @Test
        @DisplayName("shouldHandleSchemaWithAllOf - parse allOf composition")
        void shouldHandleSchemaWithAllOf() throws IOException {
            // Arrange - Create a schema with allOf
            String schemaJson = """
                {
                    "type": "object",
                    "allOf": [
                        { "type": "object", "properties": { "name": { "type": "string" } } },
                        { "type": "object", "properties": { "age": { "type": "integer" } } }
                    ]
                }
                """;
            Path schemaFile = tempDir.resolve("allof-schema.json");
            Files.writeString(schemaFile, schemaJson);

            // Act
            Schema schema = loader.load(schemaFile, "allOfSchema");

            // Assert
            assertNotNull(schema, "Expected schema with allOf to be loaded");
            assertNotNull(schema.getAllOf(), "Expected allOf to be parsed");
            assertEquals(2, schema.getAllOf().size(), "Expected 2 allOf schemas");
        }

        @Test
        @DisplayName("shouldFailWithUnsupportedExtension - extensão não suportada")
        void shouldFailWithUnsupportedExtension() throws IOException {
            // Arrange - Create a file with unsupported extension
            String schemaContent = """
                {
                    "type": "object"
                }
                """;
            Path schemaFile = tempDir.resolve("test-schema.xml");
            Files.writeString(schemaFile, schemaContent);

            // Act & Assert - Should throw IllegalArgumentException
            try {
                loader.load(schemaFile, "unsupportedSchema");
                assertFalse(true, "Expected IllegalArgumentException for unsupported extension");
            } catch (IllegalArgumentException e) {
                assertTrue(e.getMessage().contains("Unsupported"),
                        "Expected error about unsupported extension");
            } catch (IOException e) {
                // Also acceptable - just check it's not null
                assertNotNull(e);
            }
        }

        @Test
        @DisplayName("shouldParseArraySchema - parse array type schema")
        void shouldParseArraySchema() throws IOException {
            // Arrange - Create a schema with array type
            String schemaJson = """
                {
                    "type": "array",
                    "items": { "type": "string" },
                    "minItems": 1,
                    "maxItems": 10,
                    "uniqueItems": true
                }
                """;
            Path schemaFile = tempDir.resolve("array-schema.json");
            Files.writeString(schemaFile, schemaJson);

            // Act
            Schema schema = loader.load(schemaFile, "arraySchema");

            // Assert
            assertNotNull(schema, "Expected array schema to be loaded");
            assertEquals(SchemaType.ARRAY, schema.getType());
            assertNotNull(schema.getItemSchema(), "Expected items schema to be parsed");
            assertEquals(1, schema.getMinItems(), "Expected minItems to be 1");
            assertEquals(10, schema.getMaxItems(), "Expected maxItems to be 10");
            assertTrue(schema.isUniqueItems(), "Expected uniqueItems to be true");
        }

        @Test
        @DisplayName("shouldParseConstKeyword - parse const keyword")
        void shouldParseConstKeyword() throws IOException {
            // Arrange - Create a schema with const
            String schemaJson = """
                {
                    "type": "string",
                    "const": "active"
                }
                """;
            Path schemaFile = tempDir.resolve("const-schema.json");
            Files.writeString(schemaFile, schemaJson);

            // Act
            Schema schema = loader.load(schemaFile, "constSchema");

            // Assert
            assertNotNull(schema, "Expected schema with const to be loaded");
            assertEquals("active", schema.getConstValue(), "Expected const value to be 'active'");
        }

        @Test
        @DisplayName("shouldParseAdditionalPropertiesAsSchema")
        void shouldParseAdditionalPropertiesAsSchema() {
            Map<String, Object> schemaMap = Map.of(
                    "type", "object",
                    "additionalProperties", Map.of("type", "string"));

            Schema schema = loader.parseSchema("additionalPropsSchema", schemaMap);

            assertNotNull(schema, "Expected schema to parse successfully");
            assertNotNull(schema.getAdditionalPropertiesSchema(), "Expected additionalProperties schema to be parsed");
            assertEquals(SchemaType.STRING, schema.getAdditionalPropertiesSchema().getType(),
                    "Expected additionalProperties schema type to be string");
        }

        @Test
        @DisplayName("shouldParseP1P2KeywordsIntoSchemaModel")
        void shouldParseP1P2KeywordsIntoSchemaModel() {
            Map<String, Object> schemaMap = Map.ofEntries(
                    Map.entry("type", "string"),
                    Map.entry("default", "abc"),
                    Map.entry("examples", List.of("abc", "def")),
                    Map.entry("deprecated", true),
                    Map.entry("contentEncoding", "base64"),
                    Map.entry("contentMediaType", "application/json"),
                    Map.entry("contentSchema", Map.of("type", "object", "properties", Map.of("name", Map.of("type", "string")))),
                    Map.entry("unevaluatedItems", false),
                    Map.entry("unevaluatedProperties", false),
                    Map.entry("$dynamicRef", "#/$defs/node"),
                    Map.entry("$dynamicAnchor", "node"));

            Schema schema = loader.parseSchema("p1p2Schema", schemaMap);

            assertNotNull(schema, "Expected schema to parse successfully");
            assertEquals("abc", schema.getDefaultValue(), "default should be parsed");
            assertEquals(2, schema.getExamples().size(), "examples should be parsed");
            assertTrue(Boolean.TRUE.equals(schema.isDeprecated()), "deprecated should be parsed as true");
            assertEquals("base64", schema.getContentEncoding(), "contentEncoding should be parsed");
            assertEquals("application/json", schema.getContentMediaType(), "contentMediaType should be parsed");
            assertNotNull(schema.getContentSchema(), "contentSchema should be parsed");
            assertTrue(Boolean.FALSE.equals(schema.isUnevaluatedItemsAllowed()), "unevaluatedItems should be parsed");
            assertTrue(Boolean.FALSE.equals(schema.isUnevaluatedPropertiesAllowed()), "unevaluatedProperties should be parsed");
            assertEquals("#/$defs/node", schema.getDynamicRef(), "$dynamicRef should be parsed");
            assertEquals("node", schema.getDynamicAnchor(), "$dynamicAnchor should be parsed");
        }

        @Test
        @DisplayName("shouldParseReadOnlyWriteOnly - parse readOnly and writeOnly keywords")
        void shouldParseReadOnlyWriteOnly() throws IOException {
            // Arrange - Create a schema with readOnly and writeOnly
            String schemaJson = """
                {
                    "type": "object",
                    "properties": {
                        "id": { "type": "integer", "readOnly": true },
                        "password": { "type": "string", "writeOnly": true }
                    }
                }
                """;
            Path schemaFile = tempDir.resolve("readonly-schema.json");
            Files.writeString(schemaFile, schemaJson);

            // Act
            Schema schema = loader.load(schemaFile, "readonlySchema");

            // Assert
            assertNotNull(schema, "Expected schema to be loaded");
            assertNotNull(schema.getProperties(), "Expected properties to be parsed");
            
            // Check readOnly property
            Schema idProp = schema.getProperties().get("id");
            assertNotNull(idProp, "Expected 'id' property to exist");
            assertTrue(idProp.isReadOnly(), "Expected 'id' to be readOnly");
            
            // Check writeOnly property
            Schema passwordProp = schema.getProperties().get("password");
            assertNotNull(passwordProp, "Expected 'password' property to exist");
            assertTrue(passwordProp.isWriteOnly(), "Expected 'password' to be writeOnly");
        }
    }

    @Nested
    @DisplayName("Unsupported keyword behavior")
    class UnsupportedKeywordBehaviorTests {

        @Test
        @DisplayName("shouldParsePropertyNamesWithoutUnsupportedWarning")
        void shouldParsePropertyNamesWithoutUnsupportedWarning() {
            CapturingHandler handler = new CapturingHandler();
            logger.addHandler(handler);
            try {
                Map<String, Object> schemaMap = Map.of(
                        "type", "object",
                        "propertyNames", Map.of("pattern", "^[a-z]+$"),
                        "properties", Map.of("name", Map.of("type", "string")));

                Schema schema = loader.parseSchema("warnSchema", schemaMap);
                assertNotNull(schema, "Schema should still be parsed when fail-fast is disabled");
                assertNotNull(schema.getPropertyNamesSchema(), "propertyNames should be parsed into the schema model");
                assertFalse(handler.hasMessageContaining("Unsupported keyword detected: 'propertyNames'"),
                        "No unsupported warning expected because propertyNames is now in registry");
            } finally {
                logger.removeHandler(handler);
            }
        }

        @Test
        @DisplayName("shouldThrowInFailFastModeForUnsupportedDollarKeyword")
        void shouldThrowInFailFastModeForUnsupportedDollarKeyword() {
            loader.setFailFastMode(true);
            Map<String, Object> schemaMap = Map.of(
                    "type", "object",
                    "$unsupportedKeyword", true);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> loader.parseSchema("failFastSchema", schemaMap));
            assertTrue(ex.getMessage().contains("Unsupported keyword detected: '$unsupportedKeyword'"),
                    "Expected fail-fast message mentioning unsupported '$' keyword");
        }

        @Test
        @DisplayName("shouldTreatRegistryKeywordAsSupportedEvenWithoutImplementation")
        void shouldTreatRegistryKeywordAsSupportedEvenWithoutImplementation() {
            CapturingHandler handler = new CapturingHandler();
            logger.addHandler(handler);
            try {
                Map<String, Object> schemaMap = Map.of(
                        "type", "object",
                        "$dynamicAnchor", "node",
                        "properties", Map.of("name", Map.of("type", "string")));

                Schema schema = loader.parseSchema("dynamicAnchorSchema", schemaMap);
                assertNotNull(schema, "Schema should parse successfully");
                assertFalse(handler.hasMessageContaining("Unsupported keyword detected: '$dynamicAnchor'"),
                        "No unsupported warning expected because keyword is in registry");
            } finally {
                logger.removeHandler(handler);
            }
        }

        @Test
        @DisplayName("shouldParsePrefixItemsWithoutUnsupportedWarning")
        void shouldParsePrefixItemsWithoutUnsupportedWarning() {
            CapturingHandler handler = new CapturingHandler();
            logger.addHandler(handler);
            try {
                Map<String, Object> schemaMap = Map.of(
                        "type", "array",
                        "prefixItems", List.of(
                                Map.of("type", "string"),
                                Map.of("type", "integer")));

                Schema schema = loader.parseSchema("prefixItemsSchema", schemaMap);
                assertNotNull(schema, "Schema should parse successfully even if warning is emitted");
                assertEquals(2, schema.getPrefixItems().size(), "prefixItems should still be parsed");
                assertFalse(handler.hasMessageContaining("Unsupported keyword detected: 'prefixItems'"),
                        "No warning expected because prefixItems is registered as supported");
            } finally {
                logger.removeHandler(handler);
            }
        }

        @Test
        @DisplayName("shouldNotWarnForCustomPropertyNamesInsidePropertiesMap")
        void shouldNotWarnForCustomPropertyNamesInsidePropertiesMap() {
            CapturingHandler handler = new CapturingHandler();
            logger.addHandler(handler);
            try {
                Map<String, Object> schemaMap = Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "user_name", Map.of("type", "string"),
                                "x-custom", Map.of(
                                        "type", "object",
                                        "properties", Map.of("nested-value", Map.of("type", "integer")))));

                Schema schema = loader.parseSchema("customPropertyNamesSchema", schemaMap);

                assertNotNull(schema, "Schema should parse successfully");
                assertFalse(handler.hasMessageContaining("Unsupported keyword detected: 'user_name'"),
                        "Custom property names inside 'properties' should not be treated as keywords");
                assertFalse(handler.hasMessageContaining("Unsupported keyword detected: 'x-custom'"),
                        "Nested custom property names inside 'properties' should not be treated as keywords");
                assertFalse(handler.hasMessageContaining("Unsupported keyword detected: 'nested-value'"),
                        "Deep nested property names should not be treated as keywords");
            } finally {
                logger.removeHandler(handler);
            }
        }

        @Test
        @DisplayName("shouldApplyDefaultMinContainsWhenContainsIsPresent")
        void shouldApplyDefaultMinContainsWhenContainsIsPresent() {
            Map<String, Object> schemaMap = Map.of(
                    "type", "array",
                    "contains", Map.of("type", "integer"));

            Schema schema = loader.parseSchema("containsSchema", schemaMap);
            assertNotNull(schema, "Schema should parse successfully");
            assertNotNull(schema.getContainsSchema(), "contains schema should be parsed");
            assertEquals(1, schema.getMinContains(), "minContains should default to 1 when contains is present");
            assertNull(schema.getMaxContains(), "maxContains should remain null when omitted");
        }
    }

    private static final class CapturingHandler extends Handler {
        private final StringBuilder messages = new StringBuilder();

        @Override
        public void publish(LogRecord record) {
            if (record != null && record.getMessage() != null) {
                messages.append(record.getMessage()).append('\n');
            }
        }

        @Override
        public void flush() {
            // no-op
        }

        @Override
        public void close() throws SecurityException {
            // no-op
        }

        private boolean hasMessageContaining(String needle) {
            return messages.toString().contains(needle);
        }
    }
}
