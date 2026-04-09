package com.maiconjh.schemacr.config;

import com.maiconjh.schemacr.core.SchemaValidatorPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Plugin configuration manager.
 * Handles loading and accessing configuration values from config.yml.
 */
public class PluginConfig {

    private final SchemaValidatorPlugin plugin;
    private String schemaDirectory;
    private String dataDirectory;
    private boolean autoLoad;
    private boolean cacheEnabled;
    private boolean validateOnLoad;
    private boolean strictMode;
    private boolean autoValidateDataFiles;

    public PluginConfig(SchemaValidatorPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    /**
     * Loads configuration from config.yml.
     */
    public void load() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        
        this.schemaDirectory = config.getString("schema-directory", "schemas");
        this.dataDirectory = config.getString("data-directory", "data");
        this.autoLoad = config.getBoolean("auto-load", true);
        this.cacheEnabled = config.getBoolean("cache-enabled", true);
        this.validateOnLoad = config.getBoolean("validation-on-load", true);
        this.strictMode = config.getBoolean("strict-mode", false);
        this.autoValidateDataFiles = config.getBoolean("auto-validate-data-files", true);
        
        plugin.getLogger().info("Configuration loaded:");
        plugin.getLogger().info("  Schema directory: " + schemaDirectory);
        plugin.getLogger().info("  Data directory: " + dataDirectory);
        plugin.getLogger().info("  Auto-load: " + autoLoad);
        plugin.getLogger().info("  Cache enabled: " + cacheEnabled);
        plugin.getLogger().info("  Validate on load: " + validateOnLoad);
        plugin.getLogger().info("  Strict mode: " + strictMode);
        plugin.getLogger().info("  Auto-validate data files: " + autoValidateDataFiles);
    }

    /**
     * Gets the schema directory path relative to plugin data folder.
     * 
     * @return Path to schema directory
     */
    public Path getSchemaDirectory() {
        return Paths.get(plugin.getDataFolder().toString(), schemaDirectory);
    }

    /**
     * Gets the data directory path relative to plugin data folder.
     * 
     * @return Path to data directory
     */
    public Path getDataDirectory() {
        return Paths.get(plugin.getDataFolder().toString(), dataDirectory);
    }

    /**
     * Gets the data directory as a string.
     * 
     * @return directory name
     */
    public String getDataDirectoryName() {
        return dataDirectory;
    }

    /**
     * Checks if schemas should be automatically loaded on plugin enable.
     * 
     * @return true if auto-load is enabled
     */
    public boolean isAutoLoad() {
        return autoLoad;
    }

    /**
     * Checks if schema caching is enabled.
     * 
     * @return true if caching is enabled
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    /**
     * Checks if schemas should be validated when loaded.
     * 
     * @return true if validation on load is enabled
     */
    public boolean isValidateOnLoad() {
        return validateOnLoad;
    }

    /**
     * Reloads configuration from disk.
     */
    public void reload() {
        plugin.reloadConfig();
        load();
    }

    /**
     * Gets the schema directory as a string.
     * 
     * @return directory name
     */
    public String getSchemaDirectoryName() {
        return schemaDirectory;
    }

    /**
     * Checks if strict mode is enabled.
     * When enabled, unsupported keywords will throw exceptions.
     * 
     * @return true if strict mode is enabled
     */
    public boolean isStrictMode() {
        return strictMode;
    }

    /**
     * Checks if data files should be automatically validated when loaded.
     * When enabled, files with 'schema-validation-path' key will be validated.
     * 
     * @return true if auto-validate data files is enabled
     */
    public boolean isAutoValidateDataFiles() {
        return autoValidateDataFiles;
    }
}
