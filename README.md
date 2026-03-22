# Schema-Validator

Schema-Validator is a Paper/Skript add-on that validates JSON or YAML data files against schema files.

If documentation and code diverge, code is authoritative.

## Documentation Home

### Guided docs (for non-technical users)

- Online docs home: [Schema Validator Docs](https://maiconjh.github.io/Schema-Validator/)
- Guided start page in repository: [docs/pages/index.md](docs/pages/index.md)

Recommended guided sequence:

1. [Getting started](docs/pages/getting-started.md)
2. [Installation](docs/pages/installation.md)
3. [Quickstart](docs/pages/quickstart.md)
4. [Configuration](docs/pages/configuration.md)

### Developer and reference docs

- [Schema keywords](docs/pages/schema-keywords.md)
- [Validation behavior](docs/pages/validation-behavior.md)
- [Architecture](docs/pages/architecture.md)
- [Authoring guide (internal)](docs/pages/dev-guide.md)

## Exposed Skript API

- Effect: `validate yaml %string% using schema %string%`
- Effect: `validate json %string% using schema %string%`
- Expression: `last schema validation errors`

## Important runtime constraints

- Skript validation loads data as `Map<String, Object>` root values.
- `$ref` in Skript effect path is not resolver-wired by default.
- `minItems`, `maxItems`, and `uniqueItems` are parsed but not enforced by `ArrayValidator`.

## Project docs layout

- Site source: `docs/pages/`
- Shared layout: `docs/pages/_layouts/default.html`
- Shared includes: `docs/pages/_includes/`
- Styles and scripts: `docs/pages/assets/`
