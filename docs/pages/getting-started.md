---
title: Getting started
description: Build core mental models before installation so implementation decisions stay consistent.
doc_type: tutorial
order: 1
permalink: /getting-started.html
---

## Core concepts

Schema Validator checks data files against schema rules and reports structured errors.

- Data file: JSON or YAML payload to validate.
- Schema file: contract that defines allowed data shape and constraints.
- Validation result: pass or list of errors with path and expected rule.

## Prerequisites

- Java 21 runtime in your server environment.
- Paper server with Skript installed.
- Access to `plugins/Schema-Validator/` for schema and data files.

## Learning objective

By the end of this tutorial, you should be able to:

- Recognize where schema parsing ends and runtime validation begins.
- Understand where validation errors are captured in Skript.
- Move from conceptual setup to a working run in [Quickstart](quickstart.html).

## Terminology baseline

### Schema type

The validator dispatches by schema type:

- `object` -> `ObjectValidator`
- `array` -> `ArrayValidator`
- primitive types -> `PrimitiveValidator`

### Error path

Each validation error includes a path that identifies exactly where a rule failed, for example `player.inventory[2].material`.

### Strict parsing

Strict mode affects unsupported keyword handling at schema load time, not runtime data checks.

## Continue

- Build and deploy with [Installation](installation.html)
- Execute your first validation with [Quickstart](quickstart.html)
