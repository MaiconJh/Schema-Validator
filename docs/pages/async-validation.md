---
title: Async Validation
description: How to configure and use asynchronous validation in Schema-Validator.
doc_type: how-to
order: 20
sequence: 1
permalink: /async-validation.html
---

# Async Validation

This page describes how to enable and use asynchronous validation in Schema-Validator, preventing server lag during validation of large or many files.

## Quick summary

- Configure async validation in `config.yml`.
- Server command: `/sv validate-async <file> <schema>`
- Programmatic API: `AsyncSchemaValidatorAPI` with `CompletableFuture<ValidationResult>`
- Event: `AsyncValidationCompleteEvent` dispatched on the main thread

## Configuration (config.yml)

Add the `async-validation` section to your `config.yml`:
```yaml
async-validation:
  enabled: false          # set to true to enable
  thread-pool-size: 2     # number of threads for concurrent validations
  queue-capacity: 1000    # maximum queue size before rejection policy applies
```

> [!NOTE]
> Async validation is **disabled by default** to maintain backward compatibility.

> [!TIP]
> `thread-pool-size` controls how many validations can run in parallel. A value between 1 and 4 is recommended for small servers; up to 8 for dedicated servers.

> [!WARNING]
> `queue-capacity` prevents memory overload during request spikes. If the queue fills up, the `CallerRunsPolicy` will execute the task on the caller thread as a fallback.

## Server command

**Syntax:** `/sv validate-async <file_path> <schema_name>`

**Example:**
```text
/sv validate-async plugins/MyPlugin/config.yml my-schema
```

**Behavior:**
- The command immediately returns: "Async validation started for [file]".
- When validation completes (on the main thread), a success or error message will be displayed in chat.

**Required permission:** `schemavalidator.admin` (same as other commands)

## Programmatic API

For plugin developers, Schema-Validator exposes an async API.

### Obtain the async service

```java
Optional<AsyncValidationService> asyncService = SchemaValidatorPlugin.getInstance().getAsyncValidationService();
if (asyncService.isPresent()) {
    CompletableFuture<ValidationResult> future = asyncService.get().validateAsync(request);
} else {
    // fallback to sync or warn that async is disabled
}
```

### AsyncSchemaValidatorAPI interface

```java
public interface AsyncSchemaValidatorAPI {
    CompletableFuture<ValidationResult> validateAsync(ValidationRequest request);
    CompletableFuture<List<ValidationResult>> validateBatchAsync(List<ValidationRequest> requests);
}
```

### AsyncValidationCompleteEvent

This event is called on the main thread after validation completes.

```java
@EventHandler
public void onAsyncValidationComplete(AsyncValidationCompleteEvent event) {
    if (event.getResult().isValid()) {
        Bukkit.getLogger().info("Async validation succeeded: " + event.getFilePath());
    } else {
        Bukkit.getLogger().warning("Validation errors for " + event.getFilePath() + ": " + event.getResult().getErrors());
    }
}
```

> [!IMPORTANT]
> Do not use the async API for validations that modify server state; the async service is read-only.

## Best practices

- Keep `thread-pool-size` between 1 and 4 for small servers; up to 8 for dedicated servers.
- Do not use the async API for validations that modify server state (the service is read-only).
- If your server has many files to validate on startup, enable `async-validation.enabled` to avoid locking the main thread.

## Tests

Unit and integration tests are located at:

- [AsyncValidationServiceTest.java](/src/test/java/com/maiconjh/schemacr/core/AsyncValidationServiceTest.java)
- [ValidateAsyncCommandTest.java](/src/test/java/com/maiconjh/schemacr/command/ValidateAsyncCommandTest.java)

To run locally (if you have a Java development environment set up):

```bash
./gradlew test
```

## Troubleshooting

| Problem                      | Possible cause                           | Solution                               |
|------------------------------|------------------------------------------|----------------------------------------|
| Command does not respond     | Async disabled                           | Verify `async-validation.enabled: true` |
| "Pool exhausted"             | Too many simultaneous validations        | Increase `thread-pool-size` or `queue-capacity` |
| Lag even with async enabled  | Sync validation being used elsewhere     | Ensure scripts/plugins use the async API |
| Event not called             | Async service not registered             | Confirm `getAsyncValidationService()` returns a value |

## Final notes

- Async validation was introduced in version **1.5.0**.
- To maintain compatibility with legacy Skript scripts, synchronous behavior remains unchanged.
- Async validation is **optional**; servers that do not enable it will continue with traditional synchronous validation.

> [!CAUTION]
> If you disable async validation, the async APIs, commands, and events will not be available. Ensure your plugins and scripts handle this gracefully.
```
