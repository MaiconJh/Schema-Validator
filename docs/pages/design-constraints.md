---
title: Design constraints
description: Known runtime constraints and tradeoffs in the current implementation.
doc_type: explanation
order: 3
sequence: 17
permalink: /design-constraints.html
---

## Global result bridge

`SkriptValidationBridge` stores one static `lastResult`. Parallel or interleaved validations can overwrite each other.

## Root payload type in Skript path

`DataFileLoader` currently deserializes data as `Map<String, Object>` root for effect execution. Root arrays and scalar roots are not supported in this path.

## `$ref` path dependency

`SchemaRefResolver` exists and is used in resolver-aware flows, but default Skript effect execution constructs `new ValidationService()` without resolver wiring.

## Cache eviction model

`SchemaRegistry.getSchema()` performs time-based eviction on read. There is no background refresh or preload scheduler.

## Registry/support mismatch

`SupportedKeywordsRegistry` lists recognized keywords, but validator enforcement is narrower. Some keywords are parse-recognized only.

## Startup failure count summary

`SchemaValidatorPlugin.autoLoadSchemas()` reports `failedCount` in summary logs, but current implementation does not increment that counter.

## Why this page matters

These constraints should be treated as current operational boundaries when writing schemas, debugging failures, or planning feature extensions.
