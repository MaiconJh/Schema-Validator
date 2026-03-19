# Quick Start

> Canonical behavior contract: [CONTRACT.md](CONTRACT.md).

Learn to use Schema Validator in 5 minutes!

## What you'll learn

- Create a simple schema
- Validate a YAML file
- Handle validation errors

---

## Step 1: Create a Schema

Create a file named `player.schema.json` in `plugins/Schema-Validator/schemas/`:

```json
{
  "type": "object",
  "properties": {
    "name": {
      "type": "string"
    },
    "level": {
      "type": "integer",
      "minimum": 1,
      "maximum": 100
    },
    "health": {
      "type": "number",
      "minimum": 0
    },
    "class": {
      "type": "string",
      "enum": ["warrior", "mage", "archer"]
    }
  },
  "required": ["name", "level"],
  "additionalProperties": false
}
```

## Step 2: Create a Data File

Create `player.yml` in `plugins/Schema-Validator/`:

```yaml
name: "Player123"
level: 50
health: 100.0
class: "warrior"
```

## Step 3: Create the Skript Script

Create a file `validation.sk` in `plugins/Skript/scripts/`:

```skript
command /validarplayer:
    trigger:
        # Validate YAML file using schema
        validate yaml "player.yml" using schema "player.schema.json"
        
        # Get validation errors
        set {_errors::*} to last schema validation errors
        
        # Check result
        if size of {_errors::*} is 0:
            broadcast "✓ Valid player!"
        else:
            broadcast "✗ Errors found:"
            loop {_errors::*}:
                broadcast "- %loop-value%"
```

## Step 4: Test!

1. Reload the script: `/sk reload validation.sk`
2. Run the command: `/validarplayer`
3. You'll see: `✓ Valid player!`

---

## Experimenting with Errors

Change `player.yml` to cause an error:

```yaml
name: "Player123"
level: 500  # Error: greater than maximum (100)
health: -10  # Error: less than minimum (0)
class: "unknown"  # Error: not in enum
```

Run `/validarplayer` again:

```
✗ Errors found:
- ValidationError{nodePath='$.level', expectedType='maximum', actualType='500', description='Number exceeds maximum value.'}
- ValidationError{nodePath='$.health', expectedType='minimum', actualType='-10', description='Number is below minimum value.'}
- ValidationError{nodePath='$.class', expectedType='enum', actualType='unknown', description='Value not in enum list.'}
```

---

## Syntax Summary

| Command | Description |
|---------|------------|
| `validate yaml <file> using schema <schema>` | Validate YAML |
| `validate json <file> using schema <schema>` | Validate JSON |
| `last schema validation errors` | Get error list |

---

## Next Steps

Now that you learned the basics:

1. Read [Skript Syntax Reference](reference/skript-syntax.md)
2. See [Custom Blocks Tutorial](tutorials/custom-blocks.md)
3. Study [JSON Schema Guide](reference/json-schema.md)

---

[← Back](README.md) | [Previous: Installation](installation.md) | [Next: Configuration →](configuration.md)
