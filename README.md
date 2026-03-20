# Schema Validator

> **Version:** 0.3.1-SNAPSHOT

Schema Validator is a Minecraft (Paper/Spigot) plugin with Skript syntax for validating YAML/JSON data against schema files.

## Compatibility

| Component | Version |
|-----------|---------|
| Minecraft | 1.19+ (1.20.x recommended) |
| Skript | 2.8+ |
| Server | Paper 1.19+ / Spigot 1.19+ |
| Java | 17+ |

## Documentation Policy

- Canonical behavior contract: [`docs/CONTRACT.md`](docs/CONTRACT.md)
- Docs index and structure: [`docs/README.md`](docs/README.md)

If this README conflicts with the contract, `docs/CONTRACT.md` is authoritative.

## Implemented Core Capabilities

- Validate YAML/JSON files from Skript using:
  - `validate yaml %string% using schema %string%`
  - `validate json %string% using schema %string%`
- Retrieve latest validation errors using:
  - `last schema validation errors`
- Schema keyword subset including:
  - object keywords (`properties`, `required`, `additionalProperties`, `patternProperties`)
  - array `items`
  - string/number constraints (`minLength`, `maxLength`, `pattern`, `format`, `minimum`, `maximum`, `multipleOf`, etc.)
  - `enum`, `allOf`, `anyOf`, `oneOf`, `not`
  - conditional validation (`if`, `then`, `else`)
- Custom Minecraft ID format validation:
  - `minecraft-item`, `minecraft-block`, `minecraft-entity`, etc.

## Important Limitations

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

## Runtime Config

`plugins/Schema-Validator/config.yml` supports:

```yaml
schema-directory: "schemas"
auto-load: true
cache-enabled: true
validation-on-load: true
```

See [`docs/guides/integration.md`](docs/guides/integration.md) for details.

## Quick Links

- [Quick Start](docs/quickstart.md)
- [Documentation Index](docs/README.md)
- [API Reference](docs/api-reference.md)
- [Tutorials](docs/tutorials/README.md)
- [Troubleshooting](docs/TROUBLESHOOTING.md)


[![SkriptHubViewTheDocs](http://skripthub.net/static/addon/ViewTheDocsButton.png)](http://skripthub.net/docs/?addon=Schema-Validator)

## Contributing

Contributions are welcome! See [`CONTRIBUTING.md`](CONTRIBUTING.md) for guidelines.

## License

This project is licensed under the MIT License. See [`LICENSE`](LICENSE) for details.
