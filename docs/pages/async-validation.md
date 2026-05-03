# Async Validation

Configure `async-validation` in `config.yml` and use `/sv validate-async <arquivo> <schema> [--verbose]`.

## API
Implement `AsyncSchemaValidatorAPI` and call `validateAsync(data, schemaName)`.

## Tests
Run `./gradlew test jacocoTestReport`.
