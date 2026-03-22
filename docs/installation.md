# Installation

## Build

```bash
./gradlew build
```

The project uses Java 21 toolchain and compile-only Paper/Skript APIs.

## Deploy To Server

1. Copy jar from `build/libs/` into `plugins/`.
2. Ensure Skript is present (`depend: Skript` in `plugin.yml`).
3. Start the server.

## Verify Startup

On enable, the plugin:

- loads `config.yml`
- creates schema registry and schema loader
- optionally auto-loads schemas
- registers Skript syntax

## Code Mapping

- Plugin lifecycle: `SchemaValidatorPlugin.onEnable()`
- Dependency declaration: `src/main/resources/plugin.yml`
- Build version: `build.gradle`

---
Last updated: 2026-03-22  
Documentation version: 0.3.5
