# Skript Integration

> Canonical syntax contract: [`docs/reference/skript-syntax.md`](../docs/reference/skript-syntax.md) and [`docs/CONTRACT.md`](../docs/CONTRACT.md).

## Implemented effects

```skript
validate yaml %string% using schema %string%
validate json %string% using schema %string%
```

- First `%string%`: data file path.
- Second `%string%`: schema file path.

## Implemented expression

```skript
last schema validation errors
```

Returns a list of error strings. Empty list means success or no prior validation.

## Executable example

```skript
command /validatejson:
    trigger:
        validate json "plugins/Schema-Validator/data.json" using schema "plugins/Schema-Validator/schemas/player.schema.json"
        set {_errors::*} to last schema validation errors

        if size of {_errors::*} is 0:
            send "&aJSON valid"
        else:
            send "&cJSON invalid"
            loop {_errors::*}:
                send "&7- %loop-value%"
```

## Not implemented (planned/experimental)

- `last schema validation result`
- `last validation errors`
- built-in boolean result expression for the last validation execution

## Operational notes

- Paths are interpreted as file-system paths by the effect.
- `$ref` is not wired in the default Skript effect path.
