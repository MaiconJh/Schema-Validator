package com.maiconjh.schemacr.core;

import com.maiconjh.schemacr.command.SchemaValidatorCommand;
import com.maiconjh.schemacr.config.PluginConfig;
import com.maiconjh.schemacr.integration.DataFileLoader;
import com.maiconjh.schemacr.schemes.FileSchemaLoader;
import com.maiconjh.schemacr.schemes.RegisteredSchemaMetadata;
import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaRegistrationSource;
import com.maiconjh.schemacr.schemes.SchemaRefResolver;
import com.maiconjh.schemacr.schemes.SchemaRegistry;
import com.maiconjh.schemacr.schemes.SchemaType;
import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.ValidationResult;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Main plugin entrypoint.
 *
 * <p>This class wires all core systems (registry, loaders, and integrations).
 * It intentionally keeps business logic out of Bukkit lifecycle methods.</p>
 */
public class SchemaValidatorPlugin extends JavaPlugin {

    private SchemaRegistry schemaRegistry;
    private FileSchemaLoader fileSchemaLoader;
    private PluginConfig config;
    private ValidationService validationService;
    private ValidationMetrics validationMetrics;
    private AsyncValidationService asyncValidationService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Initialize configuration
        this.config = new PluginConfig(this);

        this.schemaRegistry = new SchemaRegistry(config.isCacheEnabled(), 5 * 60 * 1000);
        this.fileSchemaLoader = new FileSchemaLoader(getLogger());
        this.validationService = new ValidationService(new SchemaRefResolver(schemaRegistry, getLogger()));
        this.validationMetrics = new ValidationMetrics();
        if (config.isAsyncEnabled()) {
            this.asyncValidationService = new AsyncValidationService(this, config.getAsyncPoolSize(), config.getAsyncQueueCapacity());
        }
        applyRuntimeSettings();

        // Auto-load schemas if enabled
        if (config.isAutoLoad()) {
            loadSchemasFromConfiguredDirectory(config.isValidateOnLoad());
        }

        // Auto-load and validate data files if enabled
        if (config.isAutoValidateDataFiles()) {
            loadAndValidateDataFiles();
        }

        // Expose singleton-style access for existing integrations and optional APIs.
        PluginContext.initialize(this, schemaRegistry, fileSchemaLoader);
        registerOptionalSkriptIntegration();
        registerCommandHandler();

        // Initialize format validation caches for semantic Minecraft validation
        com.maiconjh.schemacr.validation.FormatValidator.initializeCaches();

        getLogger().info("Schema-Validator enabled with " + schemaRegistry.getSchemaCount() + " schemas loaded.");
    }

    @Override
    public void onDisable() {
        if (asyncValidationService != null) {
            asyncValidationService.shutdown();
        }
        getLogger().info("Schema-Validator disabled.");
    }

    public SchemaRegistry getSchemaRegistry() {
        return schemaRegistry;
    }

    public FileSchemaLoader getFileSchemaLoader() {
        return fileSchemaLoader;
    }

    public PluginConfig getPluginConfig() {
        return config;
    }

    public ValidationService getValidationService() {
        return validationService;
    }

    public ValidationMetrics getValidationMetrics() {
        return validationMetrics;
    }

    public Optional<AsyncValidationService> getAsyncValidationService() {
        return Optional.ofNullable(asyncValidationService);
    }

    public ValidationResult validateTracked(Object data, Schema schema, ValidationOrigin origin) {
        long startedAt = System.nanoTime();
        ValidationResult result = validationService.validate(data, schema);
        validationMetrics.record(origin, System.nanoTime() - startedAt, result);
        return result;
    }

    /**
     * Reloads config-backed runtime settings and scans the configured schema directory.
     *
     * <p>This operation updates or adds schemas from the configured directory and
     * keeps other already registered schemas intact.</p>
     */
    public SchemaReloadSummary reloadSchemasFromConfiguredDirectory() {
        config.reload();
        applyRuntimeSettings();
        return loadSchemasFromConfiguredDirectory(config.isValidateOnLoad());
    }

    public SchemaSingleReloadResult reloadSchema(String schemaName) {
        config.reload();
        applyRuntimeSettings();

        RegisteredSchemaMetadata metadata = schemaRegistry.getSchemaMetadata(schemaName).orElse(null);
        if (metadata == null) {
            return new SchemaSingleReloadResult(schemaName, false, null, "Unknown schema.");
        }
        if (metadata.sourcePath() == null) {
            return new SchemaSingleReloadResult(schemaName, false, null,
                    "Schema has no file-backed source and cannot be reloaded individually.");
        }

        try {
            Schema schema = fileSchemaLoader.load(metadata.sourcePath(), schemaName);
            schemaRegistry.registerSchema(schemaName, schema, metadata.source(), metadata.sourcePath());
            return new SchemaSingleReloadResult(schemaName, true, metadata.sourcePath(), null);
        } catch (Exception ex) {
            getLogger().log(Level.WARNING, "Failed to reload schema '" + schemaName + "' from " + metadata.sourcePath(), ex);
            String message = ex.getMessage() == null || ex.getMessage().isBlank()
                    ? ex.getClass().getSimpleName()
                    : ex.getMessage();
            return new SchemaSingleReloadResult(schemaName, false, metadata.sourcePath(), message);
        }
    }

    /**
     * Registers the optional Skript integration only when Skript is present.
     */
    private void registerOptionalSkriptIntegration() {
        Plugin skriptPlugin = getServer().getPluginManager().getPlugin("Skript");
        if (skriptPlugin == null || !skriptPlugin.isEnabled()) {
            getLogger().info("Skript not detected. Continuing without Skript syntax registration.");
            return;
        }

        try {
            Class<?> registrationClass = Class.forName(
                    "com.maiconjh.schemacr.integration.SkriptSyntaxRegistration",
                    true,
                    getClassLoader()
            );
            registrationClass.getMethod("register", SchemaValidatorPlugin.class).invoke(null, this);
        } catch (ReflectiveOperationException | LinkageError ex) {
            getLogger().log(Level.WARNING,
                    "Skript is present, but Schema-Validator could not register Skript syntax. Continuing without Skript integration.",
                    ex);
        }
    }

    /**
     * Registers the administrative command handler.
     */
    private void registerCommandHandler() {
        PluginCommand command = getCommand("schemavalidator");
        if (command == null) {
            getLogger().severe("Command 'schemavalidator' is missing from plugin.yml.");
            return;
        }

        SchemaValidatorCommand handler = new SchemaValidatorCommand(this);
        command.setExecutor(handler);
        command.setTabCompleter(handler);
    }

    private void applyRuntimeSettings() {
        schemaRegistry.setCacheEnabled(config.isCacheEnabled());
        fileSchemaLoader.setFailFastMode(config.isStrictMode());
    }

    /**
     * Loads all schema files from the configured directory.
     */
    private SchemaReloadSummary loadSchemasFromConfiguredDirectory(boolean validateLoadedSchemas) {
        Path schemaDir = config.getSchemaDirectory();
        
        if (!Files.exists(schemaDir)) {
            try {
                Files.createDirectories(schemaDir);
                getLogger().info("Created schema directory: " + schemaDir);
            } catch (IOException e) {
                getLogger().log(Level.WARNING, "Failed to create schema directory: " + schemaDir, e);
                return new SchemaReloadSummary(schemaDir, 0, 1, false);
            }
        }

        getLogger().info("Loading schemas from: " + schemaDir);
        
        int loadedCount = 0;
        int failedCount = 0;

        // Load JSON schemas
        SchemaDirectoryLoadResult jsonLoad = loadSchemasFromDirectory(schemaDir, ".json");
        loadedCount += jsonLoad.loadedCount();
        failedCount += jsonLoad.failedCount();
        
        // Load YAML schemas
        SchemaDirectoryLoadResult ymlLoad = loadSchemasFromDirectory(schemaDir, ".yml");
        loadedCount += ymlLoad.loadedCount();
        failedCount += ymlLoad.failedCount();

        SchemaDirectoryLoadResult yamlLoad = loadSchemasFromDirectory(schemaDir, ".yaml");
        loadedCount += yamlLoad.loadedCount();
        failedCount += yamlLoad.failedCount();

        // Note: Schema self-validation was removed - it produced false positives
        // because generated test data couldn't satisfy enum, format, oneOf constraints.
        // Use 'sv validate <schema>' command to manually test schemas instead.

        getLogger().info("Auto-load complete: " + loadedCount + " schemas loaded, " + failedCount + " failed.");
        return new SchemaReloadSummary(schemaDir, loadedCount, failedCount, false);
    }

    /**
     * Loads and validates data files from the configured data directory.
     * Files with 'schema-validation-path' key will be validated against the referenced schema.
     */
    private DataFileValidationSummary loadAndValidateDataFilesInternal() {
        Path dataDir = config.getDataDirectory();
        
        if (!Files.exists(dataDir)) {
            try {
                Files.createDirectories(dataDir);
                getLogger().info("Created data directory: " + dataDir);
            } catch (IOException e) {
                getLogger().log(Level.WARNING, "Failed to create data directory: " + dataDir, e);
                return new DataFileValidationSummary(dataDir, 0, 0, 0, 0);
            }
        }

        getLogger().info("Loading data files from: " + dataDir);
        
        int totalFiles = 0;
        int loadedCount = 0;
        int validatedCount = 0;
        int validationFailedCount = 0;

        DataFileLoader dataFileLoader = new DataFileLoader();

        // Load JSON data files
        DataFileLoadResult jsonResult = loadDataFilesFromDirectory(dataDir, ".json", dataFileLoader);
        totalFiles += jsonResult.totalFiles();
        loadedCount += jsonResult.loadedCount();
        validatedCount += jsonResult.validatedCount();
        validationFailedCount += jsonResult.validationFailedCount();

        // Load YAML data files
        DataFileLoadResult ymlResult = loadDataFilesFromDirectory(dataDir, ".yml", dataFileLoader);
        totalFiles += ymlResult.totalFiles();
        loadedCount += ymlResult.loadedCount();
        validatedCount += ymlResult.validatedCount();
        validationFailedCount += ymlResult.validationFailedCount();

        DataFileLoadResult yamlResult = loadDataFilesFromDirectory(dataDir, ".yaml", dataFileLoader);
        totalFiles += yamlResult.totalFiles();
        loadedCount += yamlResult.loadedCount();
        validatedCount += yamlResult.validatedCount();
        validationFailedCount += yamlResult.validationFailedCount();

        getLogger().info("Data files processed: " + totalFiles + " total, " + loadedCount + " loaded, " + 
                validatedCount + " validated, " + validationFailedCount + " validation failed.");
        return new DataFileValidationSummary(dataDir, totalFiles, loadedCount, validatedCount, validationFailedCount);
    }

    /**
     * Loads data files with the given extension from the directory and validates them if they have schema-validation-path.
     */
    private DataFileLoadResult loadDataFilesFromDirectory(Path directory, String extension, DataFileLoader loader) {
        int totalFiles = 0;
        int loadedCount = 0;
        int validatedCount = 0;
        int validationFailedCount = 0;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory,
                path -> path.toString().toLowerCase().endsWith(extension) && !path.toString().contains(".schema"))) {
            
            for (Path path : stream) {
                totalFiles++;
                try {
                    boolean isYaml = extension.equals(".yml") || extension.equals(".yaml");
                    DataFileLoader.DataLoadResult loadResult = loader.load(path, isYaml);
                    loadedCount++;

                    if (loadResult.hasSchemaValidationPath()) {
                        String schemaPath = loadResult.schemaValidationPath();
                        Path fullSchemaPath = config.getSchemaDirectory().resolve(schemaPath);

                        if (!Files.exists(fullSchemaPath)) {
                            getLogger().warning("Schema file not found for '" + path.getFileName() + "': " + fullSchemaPath);
                            continue;
                        }

                        String schemaName = fullSchemaPath.getFileName().toString();
                        Schema schema = fileSchemaLoader.load(fullSchemaPath, schemaName);
                        schemaRegistry.registerSchema(schemaName, schema, SchemaRegistrationSource.AUTOLOAD, fullSchemaPath);

                        ValidationResult result = validationService.validate(loadResult.data(), schema);

                        if (result.isSuccess()) {
                            validatedCount++;
                            getLogger().info("Validated data file: " + path.getFileName() + " against schema: " + schemaName);
                        } else {
                            validationFailedCount++;
                            getLogger().warning("Validation failed for data file '" + path.getFileName() + "':");
                            for (ValidationError error : result.getErrors()) {
                                getLogger().warning("  - " + error.getNodePath() + ": " + error.getDescription());
                            }
                        }
                    }
                } catch (Exception e) {
                    getLogger().log(Level.WARNING, "Failed to load data file: " + path.getFileName(), e);
                }
            }
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Failed to read data directory: " + directory, e);
        }

        return new DataFileLoadResult(totalFiles, loadedCount, validatedCount, validationFailedCount);
    }

    private record DataFileLoadResult(int totalFiles, int loadedCount, int validatedCount, int validationFailedCount) {
    }

    /**
     * Validates all registered schemas against themselves to ensure structural correctness.
     */
    private void validateLoadedSchemas() {
        getLogger().info("Validating loaded schemas...");
        
        // Create SchemaRefResolver with the registry for $ref resolution
        SchemaRefResolver refResolver = new SchemaRefResolver(schemaRegistry, getLogger());
        ValidationService validationService = new ValidationService(refResolver);
        
        int validCount = 0;
        int invalidCount = 0;

        for (String schemaName : schemaRegistry.getAllSchemaNames()) {
            Schema schema = schemaRegistry.getSchema(schemaName).orElse(null);
            if (schema != null) {
                // Create a minimal valid data to test schema structure
                Object testData = createMinimalTestData(schema);
                ValidationResult result = validationService.validate(testData, schema);

                if (result.isSuccess()) {
                    validCount++;
                } else {
                    invalidCount++;
                    getLogger().warning("Schema '" + schemaName + "' failed validation:");
                    for (ValidationError error : result.getErrors()) {
                        getLogger().warning("  - " + error.getNodePath() + ": " + error.getDescription());
                    }
                }
            }
        }

        getLogger().info("Schema validation complete: " + validCount + " valid, " + invalidCount + " invalid.");
    }

    /**
     * Creates minimal test data for schema validation testing.
     */
    private Object createMinimalTestData(Schema schema) {
        return switch (schema.getType()) {
            case OBJECT -> {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                for (String required : schema.getRequiredFields()) {
                    map.put(required, createMinimalTestData(
                        schema.getProperties().getOrDefault(required, 
                            Schema.builder("temp", SchemaType.STRING).build())));
                }
                yield map;
            }
            case ARRAY -> java.util.List.of();
            case STRING -> "";
            case NUMBER, INTEGER -> 0;
            case BOOLEAN -> false;
            case NULL -> null;
            case ANY -> "test";
        };
    }

    /**
     * Loads all schema files with the given extension from the directory.
     */
    private SchemaDirectoryLoadResult loadSchemasFromDirectory(Path directory, String extension) {
        int loadedCount = 0;
        int failedCount = 0;
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, 
                path -> path.toString().toLowerCase().endsWith(extension))) {
            
            for (Path path : stream) {
                try {
                    String fileName = path.getFileName().toString();
                    String schemaName = fileName.substring(0, fileName.lastIndexOf('.'));
                    
                    Schema schema = fileSchemaLoader.load(path, schemaName);
                    schemaRegistry.registerSchema(schemaName, schema, SchemaRegistrationSource.AUTOLOAD, path);
                    
                    getLogger().info("Loaded schema: " + schemaName + " from " + fileName);
                    loadedCount++;
                } catch (Exception e) {
                    failedCount++;
                    getLogger().log(Level.WARNING, "Failed to load schema: " + path.getFileName(), e);
                }
            }
        } catch (IOException e) {
            failedCount++;
            getLogger().log(Level.WARNING, "Failed to read schema directory: " + directory, e);
        }
        
        return new SchemaDirectoryLoadResult(loadedCount, failedCount);
    }

    public record SchemaReloadSummary(Path schemaDirectory,
                                      int loadedCount,
                                      int failedCount,
                                      boolean validationRan) {
    }

    public record DataFileValidationSummary(Path dataDirectory, int totalFiles, int loadedCount, 
            int validatedCount, int validationFailedCount) {
    }

    public record SchemaSingleReloadResult(String schemaName,
                                           boolean success,
                                           Path sourcePath,
                                           String errorMessage) {
    }

    public DataFileValidationSummary loadAndValidateDataFiles() {
        return loadAndValidateDataFilesInternal();
    }

    private record SchemaDirectoryLoadResult(int loadedCount, int failedCount) {
    }
}
