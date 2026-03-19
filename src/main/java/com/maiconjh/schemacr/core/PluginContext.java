package com.maiconjh.schemacr.core;

import com.maiconjh.schemacr.schemes.FileSchemaLoader;
import com.maiconjh.schemacr.schemes.SchemaRegistry;

/**
 * Minimal static context holder for syntax classes that are not dependency-injected yet.
 *
 * <p>When you evolve this skeleton, replace this with a service locator or DI container.</p>
 */
public final class PluginContext {

    private static SchemaValidatorPlugin plugin;
    private static SchemaRegistry schemaRegistry;
    private static FileSchemaLoader fileSchemaLoader;

    private PluginContext() {
    }

    public static void initialize(SchemaValidatorPlugin pluginInstance,
                                  SchemaRegistry registry,
                                  FileSchemaLoader loader) {
        plugin = pluginInstance;
        schemaRegistry = registry;
        fileSchemaLoader = loader;
    }

    public static SchemaValidatorPlugin getPlugin() {
        if (plugin == null) {
            throw new IllegalStateException("PluginContext not initialized. Call initialize() first.");
        }
        return plugin;
    }

    public static SchemaRegistry getSchemaRegistry() {
        if (schemaRegistry == null) {
            throw new IllegalStateException("PluginContext not initialized. Call initialize() first.");
        }
        return schemaRegistry;
    }

    public static FileSchemaLoader getFileSchemaLoader() {
        if (fileSchemaLoader == null) {
            throw new IllegalStateException("PluginContext not initialized. Call initialize() first.");
        }
        return fileSchemaLoader;
    }
}
