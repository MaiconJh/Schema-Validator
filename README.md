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

## Supported JSON Schema Features

### Reference Resolution (`$ref`)
- Full JSON Pointer support with navigation by keyword/object/list
- Support for `definitions` and `$defs` with proper indexing
- Escaping support for `~0` (escape of `~`) and `~1` (escape of `/`)
- Indices support for `allOf`/`anyOf`/`oneOf` arrays

### Array Constraints
- `minItems` — Minimum array length validation
- `maxItems` — Maximum array length validation
- `uniqueItems` — Uniqueness constraint for array elements
- `prefixItems` — Tuple validation (2019-09/2020-12)
- `items` — Schema validation for array elements

### Object Constraints
- `minProperties` — Minimum property count
- `maxProperties` — Maximum property count
- `dependencies` — Property and schema dependency modes
- `dependentRequired` — Required properties when dependency is present
- `dependentSchemas` — Schema constraints when dependency is present
- `additionalProperties` — Supports both boolean and schema forms

### Numeric Constraints
- `exclusiveMinimum`/`exclusiveMaximum` — Modern numeric form (2019-09/2020-12)
- Legacy boolean compatibility maintained for Draft-04/06/07

### Metadata Support
- `$schema` — Schema dialect identification
- `$id` — Base URI and identification for reference resolution
- `title` — Schema title
- `description` — Schema description

### Type System
- Modern type array support (e.g., `["string", "null"]`)
- Runtime dispatch by actual data type
- Support for 2019-09/2020-12 keywords: `$defs`, `prefixItems`, `dependentRequired`, `dependentSchemas`

## Project docs layout

- Site source: `docs/pages/`
- Shared layout: `docs/pages/_layouts/default.html`
- Shared includes: `docs/pages/_includes/`
- Styles and scripts: `docs/pages/assets/`
