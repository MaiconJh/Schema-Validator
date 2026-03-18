# Tutorial: Player Data System

Learn how to create a complete player data validation system.

## Goal

Create a system that:
- Stores player data in YAML
- Validates automatically on join
- Prevents invalid data

---

## Step 1: Create the Schema

Create `player-data.schema.json` in `plugins/Schema-Validator/schemas/`:

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Player Data Schema",
  "type": "object",
  "properties": {
    "username": {
      "type": "string",
      "minLength": 3,
      "maxLength": 16
    },
    "level": {
      "type": "integer",
      "minimum": 1,
      "maximum": 100
    },
    "xp": {
      "type": "integer",
      "minimum": 0
    },
    "health": {
      "type": "number",
      "minimum": 0,
      "maximum": 20
    },
    "class": {
      "type": "string",
      "enum": ["warrior", "mage", "archer", "rogue"]
    },
    "inventory": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "slot": { "type": "integer", "minimum": 0, "maximum": 35 },
          "item": { "type": "string" },
          "amount": { "type": "integer", "minimum": 1 }
        },
        "required": ["slot", "item"]
      }
    },
    "location": {
      "type": "object",
      "properties": {
        "world": { "type": "string" },
        "x": { "type": "number" },
        "y": { "type": "number" },
        "z": { "type": "number" }
      },
      "required": ["world", "x", "y", "z"]
    },
    "stats": {
      "type": "object",
      "properties": {
        "strength": { "type": "integer", "minimum": 1, "maximum": 100 },
        "agility": { "type": "integer", "minimum": 1, "maximum": 100 },
        "intelligence": { "type": "integer", "minimum": 1, "maximum": 100 }
      }
    },
    "lastLogin": {
      "type": "string",
      "format": "date-time"
    }
  },
  "required": ["username", "level", "class"],
  "additionalProperties": false
}
```

## Step 2: Create an Example Data File

Create `player-example.yml` in `plugins/Schema-Validator/`:

```yaml
username: "Player123"
level: 25
xp: 1500
health: 20.0
class: "warrior"
inventory:
  - slot: 0
    item: "DIAMOND_SWORD"
    amount: 1
  - slot: 1
    item: "APPLE"
    amount: 64
location:
  world: "world"
  x: 100.5
  y: 64.0
  z: -50.5
stats:
  strength: 50
  agility: 30
  intelligence: 20
lastLogin: "2024-01-15T10:30:00Z"
```

## Step 3: Create the Skript Script

```skript
# player-data.sk

# Load player data
function loadPlayerData(player: player) :: object:
    set {_file} to "player-%uuid of {_player}%.yml"
    
    # Validate file
    validate yaml {_file} using schema "player-data.schema.json"
    
    # Check errors
    set {_errors::*} to last schema validation errors
    if size of {_errors::*} > 0:
        broadcast "§cError loading player data for %{_player}%"
        loop {_errors::*}:
            broadcast "§c- %loop-value%"
        return null
    
    # Load data if valid
    set {_data} to yaml value "player-data" from file {_file}
    return {_data}

# Save player data
function savePlayerData(player: player, data: object):
    set {_file} to "player-%uuid of {_player}%.yml"
    set yaml value "player-data" from file {_file} to {_data}
    save yaml file {_file}

# Event: Player joins
on join:
    set {_data} to loadPlayerData(player)
    if {_data} is null:
        # Create default data
        set {_new::} to new linked hashmap
        set {_new::"username"} to player's name
        set {_new::"level"} to 1
        set {_new::"xp"} to 0
        set {_new::"health"} to 20.0
        set {_new::"class"} to "warrior"
        set {_new::"inventory"} to new arraylist
        set {_new::"location"} to new linked hashmap
        set {_new::"location"::"world"} to "world"
        set {_new::"location"::"x"} to 0
        set {_new::"location"::"y"} to 64
        set {_new::"location"::"z"} to 0
        savePlayerData(player, {_new::})
        broadcast "§aData created for %player%!"
    else:
        broadcast "§aData loaded: %{_data::"username"}% (Level %{_data::"level"}%)"

# Command to view data
command /mydata:
    trigger:
        set {_data} to loadPlayerData(player)
        if {_data} is null:
            message "§cError loading data!"
        else:
            message "§6=== My Data ==="
            message "§aName: §f%{_data::"username"}%"
            message "§aLevel: §f%{_data::"level"}%"
            message "§aClass: §f%{_data::"class"}%"
            message "§aXP: §f%{_data::"xp"}%"
```

---

## Concepts Learned

| Concept | Description |
|---------|-------------|
| `object` | Structure with properties |
| `array` | List of items |
| `enum` | List of allowed values |
| `minimum` / `maximum` | Numeric limits |
| `required` | Mandatory fields |
| `additionalProperties` | Controls extra fields |

---

## Next Steps

Now that you know how to create complex schemas:

1. Learn about [patternProperties](reference/json-schema.md)
2. See the [Custom Blocks Tutorial](custom-blocks.md)
3. Study the [Skript Reference](reference/skript-syntax.md)

---

[← Back](../README.md) | [Previous: Quick Start](../quickstart.md) | [Next: Custom Blocks →](custom-blocks.md)
