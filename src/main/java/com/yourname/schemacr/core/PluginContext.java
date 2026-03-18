package com.yourname.schemacr.core;

import com.yourname.schemacr.schemes.FileSchemaLoader;
import com.yourname.schemacr.schemes.SchemaRegistry;

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
        return plugin;
    }

    public static SchemaRegistry getSchemaRegistry() {
        return schemaRegistry;
    }

    public static FileSchemaLoader getFileSchemaLoader() {
        return fileSchemaLoader;
    }
}
