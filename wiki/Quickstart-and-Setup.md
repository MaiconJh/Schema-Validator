# Quickstart and Setup

> Normative details: [`docs/installation.md`](../docs/installation.md), [`docs/quickstart.md`](../docs/quickstart.md), [`docs/configuration.md`](../docs/configuration.md).

## 1) Install

1. Build the plugin JAR: `./gradlew build`
2. Copy the JAR into your server `plugins/` directory.
3. Ensure Skript is installed.
4. Start the server once to generate plugin data files.

## 2) Configure

Edit `plugins/Schema-Validator/config.yml`:

```yaml
schema-directory: "schemas"
auto-load: true
cache-enabled: true
validation-on-load: true
```

> Do not use legacy nested keys like `settings.*`; they are not read by runtime.

## 3) Create schema and data

Create schema at `plugins/Schema-Validator/schemas/player.schema.json`:

```json
{
  "type": "object",
  "properties": {
    "name": { "type": "string", "minLength": 3 },
    "level": { "type": "integer", "minimum": 1 }
  },
  "required": ["name", "level"],
  "additionalProperties": false
}
```

Create data file at `plugins/Schema-Validator/player.yml`:

```yaml
name: "Player123"
level: 50
```

## 4) Validate from Skript

```skript
command /validateplayer:
    trigger:
        validate yaml "plugins/Schema-Validator/player.yml" using schema "plugins/Schema-Validator/schemas/player.schema.json"
        set {_errors::*} to last schema validation errors

        if size of {_errors::*} is 0:
            send "&aValidation passed"
        else:
            send "&cValidation failed"
            loop {_errors::*}:
                send "&7- %loop-value%"
```

## 5) Verify outcomes

- If valid: error list is empty.
- If invalid: `last schema validation errors` returns one string per failure.

## Next

- [Schema and Validator Reference](Schema-and-Validator-Reference)
- [Skript Integration](Skript-Integration)
