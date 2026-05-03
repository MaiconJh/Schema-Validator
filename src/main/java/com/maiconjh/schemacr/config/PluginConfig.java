package com.maiconjh.schemacr.config;

import com.maiconjh.schemacr.core.SchemaValidatorPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PluginConfig {

    private final SchemaValidatorPlugin plugin;
    private String schemaDirectory;
    private String dataDirectory;
    private boolean autoLoad;
    private boolean cacheEnabled;
    private boolean validateOnLoad;
    private boolean strictMode;
    private boolean autoValidateDataFiles;
    private boolean asyncEnabled;
    private int asyncPoolSize;
    private int asyncQueueCapacity;

    public PluginConfig(SchemaValidatorPlugin plugin) {
        this.plugin = plugin;
        load();
    }

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
        this.asyncEnabled = config.getBoolean("async-validation.enabled", false);
        this.asyncPoolSize = config.getInt("async-validation.thread-pool-size", 2);
        this.asyncQueueCapacity = config.getInt("async-validation.queue-capacity", 1000);
    }

    public Path getSchemaDirectory() { return Paths.get(plugin.getDataFolder().toString(), schemaDirectory); }
    public Path getDataDirectory() { return Paths.get(plugin.getDataFolder().toString(), dataDirectory); }
    public String getDataDirectoryName() { return dataDirectory; }
    public boolean isAutoLoad() { return autoLoad; }
    public boolean isCacheEnabled() { return cacheEnabled; }
    public boolean isValidateOnLoad() { return validateOnLoad; }
    public void reload() { plugin.reloadConfig(); load(); }
    public String getSchemaDirectoryName() { return schemaDirectory; }
    public boolean isStrictMode() { return strictMode; }
    public boolean isAutoValidateDataFiles() { return autoValidateDataFiles; }
    public boolean isAsyncEnabled() { return asyncEnabled; }
    public int getAsyncPoolSize() { return asyncPoolSize; }
    public int getAsyncQueueCapacity() { return asyncQueueCapacity; }
}
