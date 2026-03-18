package com.yourname.schemacr.core;

import com.yourname.schemacr.integration.SkriptSyntaxRegistration;
import com.yourname.schemacr.schemes.FileSchemaLoader;
import com.yourname.schemacr.schemes.SchemaRegistry;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin entrypoint.
 *
 * <p>This class wires all core systems (registry, loaders, and integrations).
 * It intentionally keeps business logic out of Bukkit lifecycle methods.</p>
 */
public class SchemaValidatorPlugin extends JavaPlugin {

    private SchemaRegistry schemaRegistry;
    private FileSchemaLoader fileSchemaLoader;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.schemaRegistry = new SchemaRegistry();
        this.fileSchemaLoader = new FileSchemaLoader(getLogger());

        // Register Skript syntax and expose simple singleton-style access for skeleton usage.
        SkriptSyntaxRegistration.register(this);
        PluginContext.initialize(this, schemaRegistry, fileSchemaLoader);

        getLogger().info("Schema-Validator enabled.");
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
}
