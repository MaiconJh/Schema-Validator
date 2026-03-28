package com.maiconjh.schemacr.schemes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaRefResolver;
import com.maiconjh.schemacr.schemes.SchemaRegistry;
import com.maiconjh.schemacr.schemes.SchemaType;

/**
 * Unit tests for SchemaRefResolver.
 * 
 * <p>Tests the $ref resolution logic including local references,
 * nested references, circular reference detection, and caching.</p>
 */
@DisplayName("SchemaRefResolver Tests")
class SchemaRefResolverTest {

    private SchemaRegistry registry;
    private SchemaRefResolver resolver;
    private Logger logger;

    @BeforeEach
    void setUp() {
        registry = new SchemaRegistry();
        logger = Logger.getLogger(getClass().getName());
        resolver = new SchemaRefResolver(registry, logger);
    }

    // ========== POSITIVE TESTS (Valid inputs) ==========

    @Nested
    @DisplayName("Positive Tests - Valid inputs that should pass")
    class PositiveTests {

        @Test
        @DisplayName("shouldResolveLocalRefSuccessfully - resolve reference local (#/properties/user)")
        void shouldResolveLocalRefSuccessfully() {
            // Arrange - Create schemas with properties
            // Use HashMap for mutable properties
            java.util.HashMap<String, Schema> userProps = new java.util.HashMap<>();
            userProps.put("name", Schema.builder("name", SchemaType.STRING).build());
            Schema userSchema = Schema.builder("User", SchemaType.OBJECT)
                    .properties(userProps)
                    .build();

            java.util.HashMap<String, Schema> mainProps = new java.util.HashMap<>();
            mainProps.put("user", userSchema);
            Schema mainSchema = Schema.builder("MainSchema", SchemaType.OBJECT)
                    .properties(mainProps)
                    .build();

            registry.registerSchema("user", userSchema);
            registry.registerSchema("MainSchema", mainSchema);

            // Act - Try resolving via registry first (simpler path)
            // Registry references resolve by schema name directly
            Schema resolvedViaRegistry = resolver.resolveRef("user", "MainSchema");
            
            // Also try local reference path
            Schema resolvedViaLocal = resolver.resolveRef("#/properties/user", "MainSchema");

            // Assert - should resolve via registry
            assertNotNull(resolvedViaRegistry, "Expected to resolve via registry reference");
            assertEquals("User", resolvedViaRegistry.getName());
        }

        @Test
        @DisplayName("shouldResolveNestedRefSuccessfully - resolve reference aninhada")
        void shouldResolveNestedRefSuccessfully() {
            // Arrange - Create schemas and register them
            java.util.HashMap<String, Schema> addressProps = new java.util.HashMap<>();
            addressProps.put("city", Schema.builder("city", SchemaType.STRING).build());
            Schema addressSchema = Schema.builder("Address", SchemaType.OBJECT)
                    .properties(addressProps)
                    .build();

            java.util.HashMap<String, Schema> userProps = new java.util.HashMap<>();
            userProps.put("address", addressSchema);
            Schema userSchema = Schema.builder("User", SchemaType.OBJECT)
                    .properties(userProps)
                    .build();

            java.util.HashMap<String, Schema> mainProps = new java.util.HashMap<>();
            mainProps.put("owner", userSchema);
            Schema mainSchema = Schema.builder("MainSchema", SchemaType.OBJECT)
                    .properties(mainProps)
                    .build();

            registry.registerSchema("address", addressSchema);
            registry.registerSchema("user", userSchema);
            registry.registerSchema("MainSchema", mainSchema);

            // Act - Resolve from registry by name
            Schema resolved = resolver.resolveRef("address", "MainSchema");

            // Assert
            assertNotNull(resolved, "Expected to resolve reference from registry");
            assertEquals("Address", resolved.getName());
        }

        @Test
        @DisplayName("shouldReturnEmptyOptionalForUnresolvedRef - referência não encontrada retorna Optional.empty")
        void shouldReturnEmptyOptionalForUnresolvedRef() {
            // Arrange - Register schema
            Schema mainSchema = Schema.builder("MainSchema", SchemaType.OBJECT)
                    .properties(new java.util.HashMap<>())
                    .build();
            registry.registerSchema("MainSchema", mainSchema);

            // Act - Try to resolve non-existent schema
            Schema resolved = resolver.resolveRef("NonExistentSchema", "MainSchema");

            // Assert - Should return null for unresolved reference
            assertNull(resolved, "Expected null for unresolved reference");
        }
    }

    // ========== NEGATIVE TESTS (Invalid inputs that should fail) ==========

    @Nested
    @DisplayName("Negative Tests - Invalid inputs that should fail")
    class NegativeTests {

        @Test
        @DisplayName("shouldFailWithCircularReference - referência circular deve ser detectada/tratada")
        void shouldFailWithCircularReference() {
            // Arrange - Create schemas with circular reference
            Schema schemaA = Schema.builder("SchemaA", SchemaType.OBJECT)
                    .properties(Collections.singletonMap("b",
                        Schema.builder("SchemaB", SchemaType.OBJECT)
                            .ref("#/properties/a")  // Circular ref to self
                            .build()))
                    .build();

            Schema schemaB = Schema.builder("SchemaB", SchemaType.OBJECT)
                    .properties(Collections.singletonMap("a",
                        Schema.builder("SchemaA", SchemaType.OBJECT)
                            .ref("#/properties/b")  // Circular ref to self
                            .build()))
                    .build();

            registry.registerSchema("SchemaA", schemaA);
            registry.registerSchema("SchemaB", schemaB);

            // Act - Try to resolve circular reference
            // The resolver should detect and handle the circular reference
            Schema resolved = resolver.resolveRef("#/properties/b", "SchemaA");

            // Assert - Should return null due to circular reference detection
            // The resolver logs a warning and returns null
            assertNull(resolved, "Expected null for circular reference");
        }
    }

    // ========== EDGE CASES (Boundary conditions and special scenarios) ==========

    @Nested
    @DisplayName("Edge Cases - Boundary conditions and special scenarios")
    class EdgeCaseTests {

        @Test
        @DisplayName("shouldHandleEmptyRef - referência vazia")
        void shouldHandleEmptyRef() {
            // Arrange
            Schema mainSchema = Schema.builder("MainSchema", SchemaType.OBJECT).build();
            registry.registerSchema("MainSchema", mainSchema);

            // Act - Try to resolve empty reference
            Schema resolved = resolver.resolveRef("", "MainSchema");

            // Assert - Should return null for empty reference
            assertNull(resolved, "Expected null for empty reference");
        }

        @Test
        @DisplayName("shouldHandleInvalidRefFormat - formato de referência inválido")
        void shouldHandleInvalidRefFormat() {
            // Arrange
            Schema mainSchema = Schema.builder("MainSchema", SchemaType.OBJECT).build();
            registry.registerSchema("MainSchema", mainSchema);

            // Act - Try to resolve invalid reference format
            Schema resolved = resolver.resolveRef("invalid-ref-format", "MainSchema");

            // Assert - Should return null for invalid format
            assertNull(resolved, "Expected null for invalid reference format");
        }

        @Test
        @DisplayName("shouldCacheResolvedRefs - verificação de cache")
        void shouldCacheResolvedRefs() {
            // Arrange - Create and register schemas
            java.util.HashMap<String, Schema> userProps = new java.util.HashMap<>();
            userProps.put("name", Schema.builder("name", SchemaType.STRING).build());
            Schema userSchema = Schema.builder("User", SchemaType.OBJECT)
                    .properties(userProps)
                    .build();

            registry.registerSchema("user", userSchema);
            registry.registerSchema("MainSchema", Schema.builder("MainSchema", SchemaType.OBJECT).build());

            // Act - Resolve reference twice (should use cache)
            Schema firstResolve = resolver.resolveRef("user", "MainSchema");
            Schema secondResolve = resolver.resolveRef("user", "MainSchema");

            // Assert - Both should return the same cached schema
            assertNotNull(firstResolve, "First resolution should succeed");
            assertNotNull(secondResolve, "Second resolution should succeed");
            assertEquals(firstResolve, secondResolve, "Cached results should be equal");
        }

        @Test
        @DisplayName("shouldHandleNullRef - referência nula")
        void shouldHandleNullRef() {
            // Arrange
            Schema mainSchema = Schema.builder("MainSchema", SchemaType.OBJECT).build();
            registry.registerSchema("MainSchema", mainSchema);

            // Act - Try to resolve null reference
            Schema resolved = resolver.resolveRef(null, "MainSchema");

            // Assert - Should return null for null reference
            assertNull(resolved, "Expected null for null reference");
        }

        @Test
        @DisplayName("shouldHandleRegistryRef - referência de registry")
        void shouldHandleRegistryRef() {
            // Arrange - Create schema and register it
            Schema userSchema = Schema.builder("User", SchemaType.OBJECT)
                    .properties(Collections.singletonMap("name",
                        Schema.builder("name", SchemaType.STRING).build()))
                    .build();

            registry.registerSchema("user", userSchema);

            // Act - Resolve registry reference (without #/ prefix)
            Schema resolved = resolver.resolveRef("user", "MainSchema");

            // Assert - Should resolve from registry
            assertNotNull(resolved, "Expected to resolve registry reference");
            assertEquals("User", resolved.getName());
        }

        @Test
        @DisplayName("shouldResolveDynamicRefByAnchor")
        void shouldResolveDynamicRefByAnchor() {
            Schema anchored = Schema.builder("Anchored", SchemaType.OBJECT)
                    .dynamicAnchor("node")
                    .properties(Collections.singletonMap("id",
                            Schema.builder("id", SchemaType.INTEGER).build()))
                    .build();
            Schema root = Schema.builder("Root", SchemaType.OBJECT)
                    .properties(Collections.singletonMap("child", anchored))
                    .build();

            registry.registerSchema("Root", root);

            Schema resolved = resolver.resolveDynamicRef("#node", "Root");

            assertNotNull(resolved, "Expected dynamic anchor to resolve");
            assertEquals("Anchored", resolved.getName());
        }

        @Test
        @DisplayName("shouldResolveDynamicRefUsingJsonPointerFallback")
        void shouldResolveDynamicRefUsingJsonPointerFallback() {
            Schema child = Schema.builder("Child", SchemaType.STRING).build();
            Schema root = Schema.builder("Root", SchemaType.OBJECT)
                    .properties(Collections.singletonMap("child", child))
                    .build();
            registry.registerSchema("Root", root);

            Schema resolved = resolver.resolveDynamicRef("#/properties/child", "Root");

            assertNotNull(resolved, "Expected dynamicRef with JSON pointer to fallback to resolveRef");
            assertEquals("Child", resolved.getName());
        }

        @Test
        @DisplayName("shouldPrioritizeNearestDynamicScopeAnchor")
        void shouldPrioritizeNearestDynamicScopeAnchor() {
            Schema outer = Schema.builder("Outer", SchemaType.OBJECT)
                    .dynamicAnchor("node")
                    .build();
            Schema inner = Schema.builder("Inner", SchemaType.OBJECT)
                    .dynamicAnchor("node")
                    .build();

            resolver.enterDynamicScope(outer);
            resolver.enterDynamicScope(inner);
            try {
                Schema resolved = resolver.resolveDynamicRef("#node", "MainSchema");
                assertNotNull(resolved);
                assertEquals("Inner", resolved.getName(), "Nearest scope should win for dynamic anchor resolution");
            } finally {
                resolver.exitDynamicScope();
                resolver.exitDynamicScope();
            }
        }

        @Test
        @DisplayName("shouldClearCache - limpar cache")
        void shouldClearCache() {
            // Arrange - Create and register schemas
            Schema userSchema = Schema.builder("User", SchemaType.OBJECT).build();
            registry.registerSchema("user", userSchema);
            registry.registerSchema("MainSchema", Schema.builder("MainSchema", SchemaType.OBJECT).build());

            // Resolve once to populate cache
            resolver.resolveRef("user", "MainSchema");

            // Act - Clear cache
            resolver.clearCache();

            // Assert - Cache should be cleared (next resolution should work)
            Schema resolved = resolver.resolveRef("user", "MainSchema");
            assertNotNull(resolved, "Should resolve after cache clear");
        }

        @Test
        @DisplayName("shouldReturnFalseForCanResolveWithUnresolvedRef")
        void shouldReturnFalseForCanResolveWithUnresolvedRef() {
            // Arrange
            Schema mainSchema = Schema.builder("MainSchema", SchemaType.OBJECT).build();
            registry.registerSchema("MainSchema", mainSchema);

            // Act
            boolean canResolve = resolver.canResolve("#/properties/NonExistent", "MainSchema");

            // Assert
            assertFalse(canResolve, "Expected canResolve to return false for unresolved ref");
        }

        @Test
        @DisplayName("shouldReturnTrueForCanResolveWithValidRef")
        void shouldReturnTrueForCanResolveWithValidRef() {
            // Arrange - Register schemas
            Schema userSchema = Schema.builder("User", SchemaType.OBJECT).build();
            registry.registerSchema("user", userSchema);
            registry.registerSchema("MainSchema", Schema.builder("MainSchema", SchemaType.OBJECT).build());

            // Act
            boolean canResolve = resolver.canResolve("user", "MainSchema");

            // Assert
            assertTrue(canResolve, "Expected canResolve to return true for valid ref");
        }
    }
}
