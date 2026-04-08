---
title: Skript API
description: Reference for registered Skript syntax, path resolution, and error retrieval semantics.
doc_type: reference
order: 3
sequence: 12
permalink: /skript-api.html
---

## Availability

This page applies only when Skript is installed on the server.

Since `v1.2.0`, Schema-Validator can start without Skript. When Skript is absent, none of the syntax on this page is registered.

## Registered syntax

### Effects

```text
validate yaml %string% using schema %string%
validate json %string% using schema %string%
```

### Expression

```text
last schema validation errors
```

Source: `SkriptSyntaxRegistration.register()`.

## Parameter semantics

The effect accepts two string arguments:

1. data file path
2. schema file path

At runtime these are converted with `Path.of(...)` in `EffValidateData`.

> [!NOTE]
> Paths are not automatically prefixed with the plugin data folder. Use explicit paths from server root (for example `plugins/Schema-Validator/...`) to avoid path confusion.

## Execution behavior

On each effect execution (`EffValidateData.execute`):

1. Data file is loaded via `DataFileLoader`.
2. Schema file is loaded via `FileSchemaLoader`.
3. Loaded schema is registered in `SchemaRegistry` by file name.
4. Validation runs through `ValidationService`.
5. Result is stored in `SkriptValidationBridge`.

## Reading errors

`last schema validation errors` returns a `String[]` where each entry is compact and path-aware.

If there is no last result or validation succeeded, the expression returns an empty array.

Compact message format:

```text
[nodePath] expectedType: actualType - description
```

## Known constraints

- Data loader currently deserializes root payload as `Map<String, Object>`.
- Bridge state is global and stores only the latest validation result.

## Minimal practical command

```skript
command /validatesample:
    trigger:
        validate yaml "plugins/Schema-Validator/examples/simple-block-example.yml" using schema "plugins/Schema-Validator/examples/schemas/simple-block-schema.json"
        set {_errors::*} to last schema validation errors

        if size of {_errors::*} is 0:
            send "Validation passed" to player
        else:
            loop {_errors::*}:
                send "- %loop-value%" to player
```

## Related pages

- Public programmatic entry point: [Java API](java-api.html)
- Administrative command entry point: [Commands](commands.html)
- Runtime validation details: [Validation behavior](validation-behavior.html)
- Keyword support scope: [Schema keywords](schema-keywords.html)
