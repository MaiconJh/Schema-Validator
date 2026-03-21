# Installation

## Build from source

```bash
./gradlew build
```

This project is configured for Java 21 toolchain and Paper/Skript compile-only APIs.

## Deploy

1. Copy built JAR from `build/libs/` to your server `plugins/` folder.
2. Ensure Skript is installed (hard dependency).
3. Start server.

## Verify load

On enable, plugin logs configuration and schema autoload summary.

## Source mapping

1. Build settings and dependencies: `build.gradle`, `plugin.yml`.  
2. Required dependency (`depend: Skript`): `src/main/resources/plugin.yml`.  
3. Startup behavior/logs: `SchemaValidatorPlugin.onEnable()`, `PluginConfig.load()`.

[← Previous](quickstart.md) | [Next →](configuration.md) | [Home](../README.md)
