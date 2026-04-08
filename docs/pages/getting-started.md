---
title: Getting started
description: Build the mental model for how schemas are loaded, validated, and exposed through Skript or the Java/Bukkit API.
doc_type: tutorial
order: 1
sequence: 2
permalink: /getting-started.html
---

## What this plugin does

Schema Validator validates JSON or YAML data files against schema files and exposes the result through two entry points:

- the public Java/Bukkit API (`SchemaValidatorAPI`)
- the optional Skript integration
- the administrative command interface (`/schemavalidator`)

At runtime, the common flow is:

1. A schema is loaded and parsed (`FileSchemaLoader`).
2. Validation dispatches by schema type (`ValidatorDispatcher`).
3. `ValidationService` returns a `ValidationResult`.
4. If the request came from Skript, the latest result is also stored in `SkriptValidationBridge`.

## Runtime building blocks

- `SchemaValidatorPlugin`: plugin lifecycle and startup wiring.
- `PluginConfig`: reads `config.yml` and exposes options.
- `SchemaRegistry`: in-memory schema registry with optional cache expiration.
- `ValidationService`: facade that runs validators and returns `ValidationResult`.
- `SchemaValidatorAPI`: public Java/Bukkit integration facade.
- `EffValidateData` and `ExprLastValidationErrors`: optional Skript-facing API.
- `SchemaValidatorCommand`: administrative command handler.

## Prerequisites

- Java 21 runtime.
- Paper server.
- Skript installed only if you want the Skript syntax.
- Write access to `plugins/Schema-Validator/`.

> [!NOTE]
> In the current Skript effect path, data is loaded as `Map<String, Object>`. That means root arrays and root primitive payloads are not supported through this effect.

## Core terms

### Schema type dispatch

The validator used depends on schema type:

- `object` -> `ObjectValidator`
- `array` -> `ArrayValidator`
- primitive types (`string`, `number`, `integer`, `boolean`, `null`, `any`) -> `PrimitiveValidator`

### Error path

Every error includes a path, for example `$.player.inventory[2].material`.

### Strict mode

`strict-mode` controls unsupported keyword handling during schema loading:

- `false`: log warning and continue.
- `true`: throw an exception and fail load.

## What to do next

1. Deploy safely with [Installation](installation.html).
2. Run your first end-to-end validation in [Quickstart](quickstart.html).
3. Tune behavior in [Configuration](configuration.html).
4. Integrate from another plugin through [Java API](java-api.html).
5. Inspect runtime state through [Commands](commands.html).
