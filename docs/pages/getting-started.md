---
title: Getting started
description: Build the mental model for how schemas are loaded, validated, and exposed to Skript.
doc_type: tutorial
order: 1
sequence: 2
permalink: /getting-started.html
---

## What this plugin does

Schema Validator validates JSON or YAML data files against schema files and exposes the result to Skript.

At runtime, the flow is:

1. A schema is loaded and parsed (`FileSchemaLoader`).
2. Data is loaded (`DataFileLoader`).
3. Validation dispatches by schema type (`ValidatorDispatcher`).
4. Errors are stored in a bridge (`SkriptValidationBridge`).
5. Skript reads compact errors with `last schema validation errors`.

## Runtime building blocks

- `SchemaValidatorPlugin`: plugin lifecycle and startup wiring.
- `PluginConfig`: reads `config.yml` and exposes options.
- `SchemaRegistry`: in-memory schema registry with optional cache expiration.
- `ValidationService`: facade that runs validators and returns `ValidationResult`.
- `EffValidateData` and `ExprLastValidationErrors`: Skript-facing API.

## Prerequisites

- Java 21 runtime.
- Paper server with Skript installed.
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
