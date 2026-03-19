# Configuration

> Normative config contract is defined in [`CONTRACT.md`](CONTRACT.md).

Plugin config file location:

`plugins/Schema-Validator/config.yml`

## Supported keys

```yaml
schema-directory: "schemas"
auto-load: true
cache-enabled: true
validation-on-load: true
```

## Key details

### `schema-directory`

- Type: `string`
- Default: `"schemas"`
- Behavior: directory for schema files (`.json`, `.yml`, `.yaml`).

### `auto-load`

- Type: `boolean`
- Default: `true`
- Behavior: if enabled, plugin loads schemas from `schema-directory` at startup.

### `cache-enabled`

- Type: `boolean`
- Default: `true`
- Behavior: enables schema cache in `SchemaRegistry`.

### `validation-on-load`

- Type: `boolean`
- Default: `true`
- Behavior: validates loaded schemas with minimal generated sample data.

## Not supported keys

These keys are not read by runtime code:

- `settings.cache-expiry`
- `settings.schemas-folder`
- `settings.examples-folder`
- any nested `settings.*` contract

## Example folder layout

```text
plugins/
└── Schema-Validator/
    ├── config.yml
    └── schemas/
        ├── player-profile.schema.json
        └── custom-block-schema.json
```
