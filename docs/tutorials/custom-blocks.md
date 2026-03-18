# Tutorial: Custom Blocks System

Learn how to create a custom blocks system with dynamic properties using `patternProperties`.

## What is patternProperties?

`patternProperties` allows validating properties based on patterns (regex). This is useful for:
- Dynamic properties
- Keys that follow a specific pattern
- Multiple properties with the same structure

---

## Step 1: Create the Schema

Create `custom-block.schema.json`:

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Custom Block Schema",
  "type": "object",
  "properties": {
    "id": {
      "type": "string",
      "pattern": "^[a-z][a-z0-9_]*$"
    },
    "name": {
      "type": "string"
    },
    "type": {
      "type": "string",
      "enum": ["ore", "block", "plant", "machine"]
    },
    "hardness": {
      "type": "number",
      "minimum": 0
    },
    "drops": {
      "type": "array",
      "items": {
        "type": "string"
      }
    }
  },
  "required": ["id", "name", "type"],
  "patternProperties": {
    "^stat_\\w+$": {
      "type": "integer",
      "minimum": 0,
      "maximum": 100
    },
    "^bonus_\\w+$": {
      "type": "number"
    },
    "^req_\\w+$": {
      "type": "string"
    }
  },
  "additionalProperties": false
}
```

## Schema Explanation

| Pattern | Description | Example |
|---------|-------------|---------|
| `^stat_\w+$` | Any property starting with `stat_` | `stat_strength`, `stat_speed` |
| `^bonus_\w+$` | Any property starting with `bonus_` | `bonus_damage`, `bonus_exp` |
| `^req_\w+$` | Any property starting with `req_` | `req_level`, `req_permission` |

---

## Step 2: Create an Example

Create `custom-block-example.yml`:

```yaml
id: "mega_ore"
name: "Mega Ore"
type: "ore"
hardness: 50.0
drops:
  - "DIAMOND"
  - "EMERALD"
  - "IRON_INGOT"
stat_strength: 80
stat_durability: 95
bonus_damage: 5.5
bonus_exp: 2.0
req_level: 50
```

## Step 3: Create the Skript Script

```skript
# custom-blocks.sk

# Load block configuration
function loadBlockConfig(blockId: string) :: object:
    validate yaml "%{_blockId}%.yml" using schema "custom-block.schema.json"
    
    set {_errors::*} to last schema validation errors
    if size of {_errors::*} > 0:
        broadcast "§cError loading block %{_blockId}%"
        loop {_errors::*}:
            broadcast "§c- %loop-value%"
        return null
    
    return yaml value "block" from file "%{_blockId}%.yml"

# Check block requirements
function checkBlockRequirements(player: player, block: object) :: boolean:
    if {_block::"req_level"} is set:
        set {_playerLevel} to 1  # Get player's level
        if {_playerLevel} < {_block::"req_level"}:
            message "§cYou need level %{_block::"req_level"}% to break this block!"
            return false
    
    if {_block::"req_permission"} is set:
        if player doesn't have permission {_block::"req_permission"}:
            message "§cYou need permission %{_block::"req_permission"}%!"
            return false
    
    return true

# Apply bonuses to player
function applyBlockBonuses(player: player, block: object):
    loop {_block::*}:
        if loop-key starts with "bonus_":
            set {_bonusName} to loop-key after "bonus_"
            set {_bonusValue} to loop-value
            
            # Apply bonus based on type
            if {_bonusName} contains "damage":
                message "§aBonus damage: +%{_bonusValue}%"
            else if {_bonusName} contains "exp":
                message "§aBonus experience: +%{_bonusValue}%"
            else:
                message "§aBonus %{_bonusName}%: +%{_bonusValue}%"

# Get block stat
function getBlockStat(block: object, statName: string) :: integer:
    set {_key} to "stat_%{_statName}%"
    if {_block::%{_key}%} is set:
        return {_block::%{_key}%}
    return 0

# Command to test
command /testblock <text>:
    trigger:
        set {_block} to loadBlockConfig(arg-1)
        if {_block} is null:
            message "§cBlock not found or invalid!"
            stop
        
        message "§6=== Block: %{_block::"name"}% ==="
        message "§aID: §f%{_block::"id"}%"
        message "§aType: §f%{_block::"type"}%"
        message "§aHardness: §f%{_block::"hardness"}%"
        
        message "§6--- Stats ---"
        loop {_block::*}:
            if loop-key starts with "stat_":
                message "§e%loop-key%: §f%loop-value%"
        
        message "§6--- Bonuses ---"
        loop {_block::*}:
            if loop-key starts with "bonus_":
                message "§b%loop-key%: §f%loop-value%"
        
        message "§6--- Requirements ---"
        loop {_block::*}:
            if loop-key starts with "req_":
                message "§c%loop-key%: §f%loop-value%"
        
        # Check requirements
        if checkBlockRequirements(player, {_block}):
            message "§aYou can break this block!"
            applyBlockBonuses(player, {_block})
```

---

## Example Usage

```
/testblock mega_ore
```

Output:
```
=== Block: Mega Ore ===
ID: mega_ore
Type: ore
Hardness: 50.0
--- Stats ---
stat_strength: 80
stat_durability: 95
--- Bonuses ---
bonus_damage: 5.5
bonus_exp: 2.0
--- Requirements ---
req_level: 50
You can break this block!
Bonus damage: +5.5
Bonus experience: +2.0
```

---

## Creating More Blocks

### Plant Block

```yaml
id: "magic_plant"
name: "Magic Plant"
type: "plant"
hardness: 0.5
drops:
  - "MAGIC_SEED"
stat_growth: 75
bonus_hunger: 1.5
req biome: "MAGICAL_FOREST"
```

### Machine Block

```yaml
id: "enchant_amplifier"
name: "Enchant Amplifier"
type: "machine"
hardness: 100.0
drops: []
stat_efficiency: 90
bonus_enchant_level: 3
bonus_exp: 5.0
req permission: "customblocks.amplifier"
```

---

## Summary

| Feature | Usage |
|---------|-------|
| `patternProperties` | Validate dynamic properties |
| Regex `^stat_\w+$` | Any key starting with "stat_" |
| Multiple patterns | Multiple patterns in same schema |

---

## Next Steps

- See [Inventory Tutorial](inventory-validation.md)
- Study [JSON Schema Reference](reference/json-schema.md)
- Explore more [Tutorials](README.md)

---

[← Back](../README.md) | [Previous: Player Data](player-data-validation.md) | [Next: Inventory →](inventory-validation.md)
