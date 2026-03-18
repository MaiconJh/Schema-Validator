# Skript Syntax Reference

Complete reference of all expressions and effects in Schema Validator.

---

## Effects

### validate

Validates a YAML or JSON file using a schema.

```skript
# Validate YAML
validate yaml <file> using schema <schema>

# Validate JSON
validate json <file> using schema <schema>
```

| Parameter | Type | Description |
|-----------|------|-------------|
| `<file>` | string | File name (without extension) |
| `<schema>` | string | Schema file name |

**Example:**
```skript
validate yaml "player-data" using schema "player.schema.json"
validate json "quest-data" using schema "quest.schema.json"
```

---

## Expressions

### last schema validation errors

Returns the list of errors from the last validation.

```skript
set {_errors::*} to last schema validation errors
```

**Type:** Object (list of ValidationError)

**Example:**
```skript
validate yaml "data" using schema "schema"
set {_errors::*} to last schema validation errors
if size of {_errors::*} is 0:
    broadcast "Valid!"
else:
    broadcast "Invalid:"
    loop {_errors::*}:
        broadcast "- %loop-value%"
```

---

## ValidationError

Object representing a validation error.

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `nodePath` | string | Node path (e.g., `$.player.level`) |
| `expectedType` | string | Expected type |
| `actualType` | string | Actual value |
| `description` | string | Error description |

### Usage Example

```skript
loop {_errors::*}:
    set {_path} to loop-value's nodePath
    set {_desc} to loop-value's description
    broadcast "Error at %{_path}%: %{_desc}%"
```

---

## Useful Functions

### Get YAML value

```skript
yaml value <key> from file <file>
```

**Example:**
```skript
set {_name} to yaml value "name" from file "player-data"
set {_player::*} to yaml list "inventory.items" from file "player-data"
```

### Get JSON value

```skript
json value <key> from file <file>
```

---

## Complete Examples

### Example 1: Basic Validation

```skript
command /validar:
    trigger:
        validate yaml "my-data" using schema "player.schema"
        
        if size of (last schema validation errors) is 0:
            message "§aValid data!"
        else:
            message "§cInvalid data:"
            loop last schema validation errors:
                message "§c- %loop-value%"
```

### Example 2: Load Data After Validation

```skript
function loadAndValidate(file: string, schema: string) :: object:
    validate yaml {_file} using schema {_schema}
    
    if size of (last schema validation errors) > 0:
        return null
    
    return yaml value "data" from {_file}
```

### Example 3: Validate Multiple Files

```skript
function validateAll() :: boolean:
    set {_files::*} to "player", "inventory", "quests"
    set {_schemas::*} to "player.schema", "inventory.schema", "quests.schema"
    
    loop {_files::*}:
        validate yaml loop-value using schema {_schemas::%loop-index%}
        
        if size of (last schema validation errors) > 0:
            broadcast "Error in %loop-value%!"
            return false
    
    return true
```

---

## Error Handling

### Get Error Details

```skript
validate yaml "data" using schema "schema"
set {_errors::*} to last schema validation errors

loop {_errors::*}:
    # Access error properties
    set {_node} to loop-value.nodePath
    set {_expected} to loop-value.expectedType
    set {_actual} to loop-value.actualType
    set {_desc} to loop-value.description
    
    broadcast "Error at %{_node}%"
    broadcast "  Expected: %{_expected}%"
    broadcast "  Actual: %{_actual}%"
    broadcast "  Description: %{_desc}%"
```

---

## Common Errors

| Error | Cause | Solution |
|-------|-------|----------|
| `Schema not found` | Schema doesn't exist | Check if file exists in `plugins/Schema-Validator/schemas/` |
| `File not found` | Data file doesn't exist | Check file path |
| `Invalid JSON/YAML` | Malformed file | Check file syntax |

---

## Tips

1. **Always check errors** after a validation
2. **Use functions** to reuse validation logic
3. **Separate schemas** by data type (players, inventories, etc)
4. **Validate first** before using data

---

[← Back](../README.md) | [Previous: Tutorials](tutorials/README.md) | [Next: JSON Schema →](json-schema.md)
