---
title: Java API
description: Reference for the public Java/Bukkit integration facade and result views.
doc_type: reference
order: 4
sequence: 13
permalink: /java-api.html
---

## Availability

`SchemaValidatorAPI` is available when the `Schema-Validator` plugin is installed and enabled.

Since `v1.2.0`, Skript is optional. You only need Skript if you want the Skript syntax documented in [Skript API](skript-api.html).

## Consumer plugin setup

Declare a soft dependency so your plugin loads after Schema-Validator when it is present:

```yaml
softdepend:
  - Schema-Validator
```

Then guard runtime usage:

```java
if (!SchemaValidatorAPI.isAvailable()) {
    return;
}
```

## Public facade

`SchemaValidatorAPI` currently exposes:

- `isAvailable()`
- `validate(Object data, String schemaName)`
- `validateBatch(List<Object> dataList, String schemaName)`
- `hasSchema(String schemaName)`
- `getSchemaNames()`
- `registerSchemaFromFile(String schemaName, Path schemaFile)`

## Minimal validation example

```java
if (SchemaValidatorAPI.isAvailable()) {
    SchemaValidationResultView result = SchemaValidatorAPI.validate(data, "player");
    if (!result.isSuccess()) {
        for (SchemaValidationErrorView error : result.getErrors()) {
            plugin.getLogger().warning(error.getMessage());
        }
    }
}
```

## Result contract

`SchemaValidationResultView` exposes:

- `isSuccess()`
- `getErrors()`

`SchemaValidationErrorView` exposes:

- `getNodePath()`
- `getExpectedType()`
- `getActualType()`
- `getDescription()`
- `getMessage()`

## Registering schemas from another plugin

Use `registerSchemaFromFile` to add a schema into the shared runtime registry:

```java
Path schemaPath = plugin.getDataFolder().toPath().resolve("schemas/player.schema.json");
boolean registered = SchemaValidatorAPI.registerSchemaFromFile("player", schemaPath);
```

If registration succeeds, the schema becomes available through `validate(...)`, `hasSchema(...)`, and `getSchemaNames()`.

## Operational notes

- The API uses the same shared `SchemaRegistry` as the main plugin.
- Validation results returned by this API do not use `SkriptValidationBridge`.
- API validations are counted in the plugin runtime metrics surfaced by `/sv stats`.
- Schema lookup is case-insensitive because it delegates to `SchemaRegistry`.
- When a schema is missing or the plugin is unavailable, the facade returns a structured failure result instead of throwing unchecked exceptions to the caller.

## Related pages

- Runtime execution order: [Validation behavior](validation-behavior.html)
- Startup and integration boundaries: [Architecture](architecture.html)
- Administrative inspection and reload: [Commands](commands.html)
- Optional syntax-based integration: [Skript API](skript-api.html)
