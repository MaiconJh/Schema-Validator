# Skript Syntax Reference

> Normative behavior is defined in [`../CONTRACT.md`](../CONTRACT.md).

## Effects (implemented)

### Validate YAML file

```skript
validate yaml %string% using schema %string%
```

### Validate JSON file

```skript
validate json %string% using schema %string%
```

The effect loads:
- data file from first string path
- schema file from second string path

## Expressions (implemented)

### Last validation errors

```skript
last schema validation errors
```

Returns:
- list of strings (stringified validation errors)
- empty list when validation succeeded or no previous result exists

## Not supported

- `last schema validation result`
- `last validation errors`
- boolean success expression for last result

## Example

```skript
command /validateplayer:
    trigger:
        validate yaml "plugins/Schema-Validator/player.yml" using schema "plugins/Schema-Validator/schemas/player-profile.schema.json"
        set {_errors::*} to last schema validation errors

        if size of {_errors::*} is 0:
            send "&aValid data"
        else:
            send "&cInvalid data"
            loop {_errors::*}:
                send "&7- %loop-value%"
```
