package com.maiconjh.schemacr.core;

import com.maiconjh.schemacr.config.PluginConfig;
import com.maiconjh.schemacr.integration.SkriptSyntaxRegistration;
import com.maiconjh.schemacr.schemes.FileSchemaLoader;
import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaRegistry;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
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

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Initialize configuration
        this.config = new PluginConfig(this);

        this.schemaRegistry = new SchemaRegistry(config.isCacheEnabled(), 5 * 60 * 1000);
        this.fileSchemaLoader = new FileSchemaLoader(getLogger());

        // Auto-load schemas if enabled
        if (config.isAutoLoad()) {
            autoLoadSchemas();
        }

        // Register Skript syntax and expose simple singleton-style access for skeleton usage.
        SkriptSyntaxRegistration.register(this);
        PluginContext.initialize(this, schemaRegistry, fileSchemaLoader);

        getLogger().info("Schema-Validator enabled with " + schemaRegistry.getSchemaCount() + " schemas loaded.");
    }

    @Override
    public void onDisable() {
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

    /**
     * Automatically loads all schema files from the configured directory.
     */
    private void autoLoadSchemas() {
        Path schemaDir = config.getSchemaDirectory();
        
        if (!Files.exists(schemaDir)) {
            try {
                Files.createDirectories(schemaDir);
                getLogger().info("Created schema directory: " + schemaDir);
            } catch (IOException e) {
                getLogger().log(Level.WARNING, "Failed to create schema directory: " + schemaDir, e);
                return;
            }
        }

        getLogger().info("Loading schemas from: " + schemaDir);
        
        int loadedCount = 0;
        int failedCount = 0;

        // Load JSON schemas
        loadedCount += loadSchemasFromDirectory(schemaDir, ".json");
        
        // Load YAML schemas
        loadedCount += loadSchemasFromDirectory(schemaDir, ".yml");
        loadedCount += loadSchemasFromDirectory(schemaDir, ".yaml");

        getLogger().info("Auto-load complete: " + loadedCount + " schemas loaded, " + failedCount + " failed.");
    }

    /**
     * Loads all schema files with the given extension from the directory.
     */
    private int loadSchemasFromDirectory(Path directory, String extension) {
        int count = 0;
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, 
                path -> path.toString().toLowerCase().endsWith(extension))) {
            
            for (Path path : stream) {
                try {
                    String fileName = path.getFileName().toString();
                    String schemaName = fileName.substring(0, fileName.lastIndexOf('.'));
                    
                    Schema schema = fileSchemaLoader.load(path, schemaName);
                    schemaRegistry.registerSchema(schemaName, schema);
                    
                    getLogger().info("Loaded schema: " + schemaName + " from " + fileName);
                    count++;
                } catch (Exception e) {
                    getLogger().log(Level.WARNING, "Failed to load schema: " + path.getFileName(), e);
                }
            }
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Failed to read schema directory: " + directory, e);
        }
        
        return count;
    }
}
