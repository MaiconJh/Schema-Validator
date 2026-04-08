---
title: Design constraints
description: Known runtime constraints and tradeoffs in the current implementation.
doc_type: explanation
order: 3
sequence: 19
permalink: /design-constraints.html
---

## Global result bridge

`SkriptValidationBridge` stores one static `lastResult`. Parallel or interleaved validations can overwrite each other. This constraint affects only the Skript path; the Java/Bukkit API returns results directly.

## Root payload type in Skript path

`DataFileLoader` currently deserializes data as `Map<String, Object>` root for effect execution. Root arrays and scalar roots are not supported in this path.

## Cache eviction model

`SchemaRegistry.getSchema()` performs time-based eviction on read. There is no background refresh or preload scheduler.

## Supported keywords

All major JSON Schema keywords are now supported including:

- `$ref` resolution with JSON Pointer
- Array constraints (minItems, maxItems, uniqueItems, prefixItems)
- Object constraints (minProperties, maxProperties, dependencies, dependentRequired, dependentSchemas)
- Metadata ($schema, $id, title, description)

## Reload scope

`/sv reload --all` updates or adds schemas from the configured schema directory, but it does not remove unrelated schemas that were registered from other sources.

## Metrics scope

`/sv stats` reflects validations tracked through the built-in command path, Skript path, and `SchemaValidatorAPI`. Direct external use of `getValidationService()` is outside those counters.

## Why this page matters

These constraints should be treated as current operational boundaries when writing schemas, debugging failures, or planning feature extensions.
