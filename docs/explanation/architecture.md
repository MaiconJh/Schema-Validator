# Explanation: Runtime Architecture

## Startup Pipeline

1. `SchemaValidatorPlugin.onEnable()` saves default config and loads `PluginConfig`.
2. Creates `SchemaRegistry` with 5-minute cache expiry.
3. Creates `FileSchemaLoader` and applies strict-mode fail-fast setting.
4. Runs auto-load flow when enabled.
5. Registers Skript syntax and initializes `PluginContext`.

## Request Pipeline (Skript Path)

1. Skript effect receives data path + schema path.
2. `DataFileLoader` reads data as object map.
3. `FileSchemaLoader` parses schema.
4. Schema is registered in `SchemaRegistry`.
5. `ValidationService` runs validator dispatch.
6. Result is stored in `SkriptValidationBridge`.
7. Expression exposes compact errors to scripts.

## Core Components

- `SchemaValidatorPlugin`: composition root for runtime services.
- `PluginConfig`: config reading and typed access.
- `SchemaRegistry`: schema storage + expiry behavior.
- `FileSchemaLoader`: schema parsing and unsupported-keyword checks.
- `ValidationService`: validation facade.
- `Skript*` classes: integration boundary.

## Why This Shape

The current architecture favors minimal coupling to Skript and keeps validator logic in pure Java classes that can be tested independently.

---
Last updated: 2026-03-22  
Documentation version: 0.3.5
