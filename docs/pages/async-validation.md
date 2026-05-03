---
title: Async Validation
description: How to configure and use asynchronous schema validation (CLI and API).
doc_type: how-to
order: 20
sequence: 1
permalink: /async-validation.html
---

# Async Validation

This page describes how to enable and use asynchronous schema validation in Schema-Validator, both from the CLI and via the programmatic API.

Quick summary
- Configure async validation in `config.yml`.
- CLI: `./sv validate-async <file> <schema> [--verbose]`
- Programmatic API: implement `AsyncSchemaValidatorAPI` and call `validateAsync(data, schemaName)`.

## Configuration (config.yml)

Add an `async-validation` section to your `config.yml` to enable and tune async behavior. Example:

```yaml
# config.yml
async-validation:
  enabled: true            # enable async validation on CLI/API calls
  timeout-ms: 5000         # default timeout per validation (ms)
  max-concurrency: 8       # max number of concurrent validations
  retry:                   # optional retry policy for transient errors
    attempts: 2
    backoff-ms: 200
```

Notes:
- `timeout-ms` protects against long-running or hung validators.
- `max-concurrency` helps control resource usage when validating many items concurrently.

## CLI usage

Validate a JSON file against a schema asynchronously:

Unix / macOS:
```bash
./sv validate-async data/example.json mySchema --verbose
```

Windows (PowerShell / cmd):
```powershell
.\sv validate-async data\example.json mySchema --verbose
```

Example output (when verbose):
```
Validating data/example.json against schema "mySchema"
[INFO] Started async validation (timeout=5000ms)
[OK] Validation passed (0 errors) - duration: 120ms
```

If validation fails, the tool prints the list of errors and returns a non-zero exit code.

## Programmatic API

Implement the `AsyncSchemaValidatorAPI` interface to support asynchronous validation calls. Below is an example Java-style interface and a suggested `ValidationResult` model.

Java interface example:
```java
import java.util.concurrent.CompletableFuture;

public interface AsyncSchemaValidatorAPI {
    /**
     * Validate `data` against a schema named `schemaName`.
     * Returns a CompletableFuture that completes with a ValidationResult.
     */
    CompletableFuture<ValidationResult> validateAsync(Object data, String schemaName);
}
```

ValidationResult example:
```java
public class ValidationResult {
    private final boolean valid;
    private final List<String> errors;
    // constructor, getters, etc.
}
```

Kotlin suspend-style example:
```kotlin
interface AsyncSchemaValidatorAPI {
    suspend fun validateAsync(data: Any, schemaName: String): ValidationResult
}
```

Example usage (Java, with CompletableFuture):
```java
AsyncSchemaValidatorAPI validator = ...; // obtain your implementation
validator.validateAsync(myJsonNode, "mySchema")
         .orTimeout(5, TimeUnit.SECONDS)
         .thenAccept(result -> {
             if (result.isValid()) {
                 System.out.println("Validation passed");
             } else {
                 System.err.println("Validation failed: " + result.getErrors());
             }
         })
         .exceptionally(ex -> {
             System.err.println("Validation error: " + ex.getMessage());
             return null;
         });
```

Implementation tips:
- Use a thread pool or reactive runtime to avoid blocking application threads.
- Respect the configured timeout and concurrency limits.
- Propagate validation errors as structured messages rather than raw exceptions.

## Tests

Run unit tests and coverage with Gradle:

Unix / macOS:
```bash
./gradlew test jacocoTestReport
```

Windows:
```powershell
.\gradlew.bat test jacocoTestReport
```

If you have tests tagged for async validation, run them selectively:
```bash
./gradlew test --tests "*Async*"
```

CI notes:
- Ensure `max-concurrency` is set conservatively in CI to avoid resource exhaustion.
- Add integration tests that simulate timeouts and transient errors.

## Troubleshooting

- "Schema not found": verify the `schemaName` matches the schema registry or file path used by your application.
- Timeouts: increase `timeout-ms` or inspect the validator implementation for blocking operations.
- Resource exhaustion / OOM: reduce `max-concurrency` or increase worker memory.
- Intermittent errors: enable `retry` in config or add exponential backoff for transient failures.

## Examples & Tests (recommended additions)
- Add a small example implementation class in `examples/` that shows a simple in-memory schema registry and an AsyncSchemaValidator that uses `CompletableFuture.supplyAsync`.
- Add an integration test that validates a set of JSON files concurrently and asserts results.

## Notes & TODOs
- Consider adding a link to the full API Javadoc or package reference.
- If docs are rendered by a site generator (Docusaurus, MkDocs, etc.), add frontmatter at the top (title, sidebar position) if required.
- Provide example JSON and schema files under `docs/examples/async-validation` for copy/paste.
