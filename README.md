# Schema Validator

Schema Validator is a Minecraft (Paper/Spigot) plugin with Skript syntax for validating YAML/JSON data against schema files.

## Documentation policy

- Canonical behavior contract: [`docs/CONTRACT.md`](docs/CONTRACT.md)
- Docs index and structure: [`docs/README.md`](docs/README.md)

If this README conflicts with the contract, `docs/CONTRACT.md` is authoritative.

## Implemented core capabilities

- Validate YAML/JSON files from Skript using:
  - `validate yaml %string% using schema %string%`
  - `validate json %string% using schema %string%`
- Retrieve latest validation errors using:
  - `last schema validation errors`
- Schema keyword subset including:
  - object keywords (`properties`, `required`, `additionalProperties`, `patternProperties`)
  - array `items`
  - string/number constraints (`minLength`, `maxLength`, `pattern`, `format`, `minimum`, `maximum`, `multipleOf`, etc.)
  - `enum`, `allOf`, `anyOf`

## Important limitations

- Default validation entrypoint expects object-like root data.
- `$ref` exists but is only active when validation uses resolver wiring (`ValidationService(refResolver)`).
- `minItems`, `maxItems`, and `uniqueItems` are not currently enforced.

## Installation

1. Build with Gradle:

```bash
./gradlew build
```

2. Copy generated JAR to your server `plugins/` directory.
3. Ensure Skript is installed.
4. Start/restart the server.

## Runtime config

`plugins/Schema-Validator/config.yml` supports:

```yaml
schema-directory: "schemas"
auto-load: true
cache-enabled: true
validation-on-load: true
```

See [`docs/configuration.md`](docs/configuration.md) for details.
