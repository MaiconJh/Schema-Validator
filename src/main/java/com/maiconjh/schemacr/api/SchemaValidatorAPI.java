package com.maiconjh.schemacr.api;

import com.maiconjh.schemacr.core.SchemaValidatorPlugin;
import com.maiconjh.schemacr.core.ValidationOrigin;
import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaRegistrationService;
import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.ValidationResult;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

/**
 * Stable public facade for programmatic access to Schema-Validator.
 */
public final class SchemaValidatorAPI {

    private static final String PLUGIN_NAME = "Schema-Validator";

    private SchemaValidatorAPI() {
    }

    /**
     * Returns whether the Schema-Validator plugin is loaded and enabled.
     */
    public static boolean isAvailable() {
        return resolvePlugin() != null;
    }

    /**
     * Validates a single data object against a registered schema.
     */
    public static SchemaValidationResultView validate(Object data, String schemaName) {
        SchemaResolution resolution = resolveSchema(schemaName);
        if (resolution.failureResult() != null) {
            return resolution.failureResult();
        }

        try {
            ValidationResult result = resolution.plugin().validateTracked(data, resolution.schema(), ValidationOrigin.API);
            return adapt(result);
        } catch (Exception ex) {
            return handleValidationException(resolution.plugin(), ex);
        }
    }

    /**
     * Validates multiple data objects against a registered schema.
     */
    public static List<SchemaValidationResultView> validateBatch(List<Object> dataList, String schemaName) {
        if (dataList == null) {
            return List.of(failureResult("$", "dataList", "null", "Data list must not be null."));
        }

        SchemaResolution resolution = resolveSchema(schemaName);
        if (resolution.failureResult() != null) {
            return List.of(resolution.failureResult());
        }

        try {
            return dataList.stream()
                    .map(data -> adapt(resolution.plugin().validateTracked(data, resolution.schema(), ValidationOrigin.API)))
                    .toList();
        } catch (Exception ex) {
            return List.of(handleValidationException(resolution.plugin(), ex));
        }
    }

    /**
     * Returns whether a schema is currently registered.
     */
    public static boolean hasSchema(String schemaName) {
        SchemaValidatorPlugin plugin = resolvePlugin();
        return plugin != null && schemaName != null && plugin.getSchemaRegistry().contains(schemaName);
    }

    /**
     * Returns all registered schema names, or an empty set when the plugin is unavailable.
     */
    public static Set<String> getSchemaNames() {
        SchemaValidatorPlugin plugin = resolvePlugin();
        if (plugin == null) {
            return Set.of();
        }
        return Set.copyOf(plugin.getSchemaRegistry().getAllSchemaNames());
    }

    /**
     * Registers a schema from a file path.
     */
    public static boolean registerSchemaFromFile(String schemaName, Path schemaFile) {
        SchemaValidatorPlugin plugin = resolvePlugin();
        if (plugin == null || schemaName == null || schemaName.isBlank() || schemaFile == null) {
            return false;
        }

        try {
            SchemaRegistrationService registrationService =
                    new SchemaRegistrationService(plugin.getSchemaRegistry(), plugin.getFileSchemaLoader());
            registrationService.registerFromFile(schemaName, schemaFile);
            return true;
        } catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING,
                    "Failed to register schema '" + schemaName + "' from " + schemaFile, ex);
            return false;
        }
    }

    private static SchemaResolution resolveSchema(String schemaName) {
        SchemaValidatorPlugin plugin = resolvePlugin();
        if (plugin == null) {
            return new SchemaResolution(null, null,
                    failureResult("$", "plugin", "unavailable",
                            "Schema-Validator is not loaded or is disabled."));
        }

        if (schemaName == null || schemaName.isBlank()) {
            return new SchemaResolution(plugin, null,
                    failureResult("$", "schema", "missing", "Schema name must not be blank."));
        }

        Optional<Schema> schema = plugin.getSchemaRegistry().getSchema(schemaName);
        if (schema.isEmpty()) {
            return new SchemaResolution(plugin, null,
                    failureResult("$", "schema", schemaName, "Unknown schema: " + schemaName));
        }

        return new SchemaResolution(plugin, schema.get(), null);
    }

    private static SchemaValidatorPlugin resolvePlugin() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
        if (plugin instanceof SchemaValidatorPlugin schemaValidatorPlugin && plugin.isEnabled()) {
            return schemaValidatorPlugin;
        }
        return null;
    }

    private static SchemaValidationResultView handleValidationException(SchemaValidatorPlugin plugin, Exception ex) {
        plugin.getLogger().log(Level.SEVERE, "Unexpected error while validating schema.", ex);
        String message = ex.getMessage() == null || ex.getMessage().isBlank()
                ? "Unexpected validation error."
                : ex.getMessage();
        return failureResult("$", "internal", ex.getClass().getSimpleName(), message);
    }

    private static SchemaValidationResultView failureResult(String nodePath,
                                                            String expectedType,
                                                            String actualType,
                                                            String description) {
        ValidationError error = new ValidationError(nodePath, expectedType, actualType, description);
        return adapt(ValidationResult.from(List.of(error)));
    }

    private static SchemaValidationResultView adapt(ValidationResult result) {
        return new ValidationResultAdapter(result);
    }

    private record SchemaResolution(SchemaValidatorPlugin plugin,
                                    Schema schema,
                                    SchemaValidationResultView failureResult) {
    }
}
