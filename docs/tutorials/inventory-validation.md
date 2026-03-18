# Tutorial: Inventory System

Learn how to create an inventory system with validation of slots, items, and enchantments.

## Goal

Create a system that:
- Validates player inventory
- Controls items by slot
- Supports enchantments and attributes

---

## Step 1: Create the Schema

Create `inventory.schema.json`:

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Inventory Schema",
  "type": "object",
  "properties": {
    "owner": {
      "type": "string"
    },
    "size": {
      "type": "integer",
      "enum": [9, 18, 27, 36, 45, 54]
    },
    "items": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "slot": {
            "type": "integer",
            "minimum": 0,
            "maximum": 53
          },
          "material": {
            "type": "string",
            "pattern": "^[A-Z_]+$"
          },
          "amount": {
            "type": "integer",
            "minimum": 1,
            "maximum": 64
          },
          "durability": {
            "type": "integer",
            "minimum": 0
          },
          "display_name": {
            "type": "string"
          },
          "lore": {
            "type": "array",
            "items": {
              "type": "string"
            }
          },
          "enchantments": {
            "type": "array",
            "items": {
              "type": "object",
              "properties": {
                "name": {
                  "type": "string",
                  "enum": ["sharpness", "protection", "efficiency", "unbreaking", "fortune", "looting", "fire_aspect", "knockback", "power", "punch"]
                },
                "level": {
                  "type": "integer",
                  "minimum": 1,
                  "maximum": 5
                }
              },
              "required": ["name", "level"]
            }
          },
          "attributes": {
            "type": "array",
            "items": {
              "type": "object",
              "properties": {
                "name": {
                  "type": "string",
                  "enum": ["generic.attack_damage", "generic.attack_speed", "generic.max_health", "generic.movement_speed", "generic.armor", "generic.knockback_resistance"]
                },
                "operation": {
                  "type": "integer",
                  "enum": [0, 1, 2]
                },
                "amount": {
                  "type": "number"
                },
                "slot": {
                  "type": "string",
                  "enum": ["mainhand", "offhand", "feet", "legs", "chest", "head", "any"]
                }
              },
              "required": ["name", "operation", "amount"]
            }
          }
        },
        "required": ["slot", "material", "amount"]
      }
    }
  },
  "required": ["owner", "size", "items"],
  "additionalProperties": false
}
```

---

## Step 2: Create an Example

Create `inventory-example.yml`:

```yaml
owner: "Player123"
size: 36
items:
  - slot: 0
    material: "DIAMOND_SWORD"
    amount: 1
    durability: 100
    display_name: "§6Legendary Sword"
    lore:
      - "§7A sword of immense power"
      - "§c+50 damage"
    enchantments:
      - name: "sharpness"
        level: 5
      - name: "unbreaking"
        level: 3
      - name: "fire_aspect"
        level: 2
    attributes:
      - name: "generic.attack_damage"
        operation: 0
        amount: 50.0
        slot: "mainhand"
  - slot: 1
    material: "GOLDEN_APPLE"
    amount: 64
    durability: 0
  - slot: 8
    material: "BOW"
    amount: 1
    enchantments:
      - name: "power"
        level: 5
      - name: "punch"
        level: 2
```

---

## Step 3: Create the Skript Script

```skript
# inventory.sk

# Function to validate inventory
function validateInventory(inventoryFile: string) :: boolean:
    validate yaml {_inventoryFile} using schema "inventory.schema.json"
    
    set {_errors::*} to last schema validation errors
    if size of {_errors::*} > 0:
        return false
    return true

# Load inventory
function loadInventory(inventoryFile: string) :: object:
    if validateInventory({_inventoryFile}) is false:
        return null
    
    return yaml value "inventory" from file {_inventoryFile}

# Check item at specific slot
function getItemAtSlot(inventory: object, slot: integer) :: object:
    loop {_inventory::"items"}:
        if loop-value's slot is {_slot}:
            return loop-value
    return null

# Check item enchantments
function getItemEnchantments(item: object) :: text:
    if {_item::"enchantments"} is not set:
        return "None"
    
    set {_result} to ""
    loop {_item::"enchantments"}:
        set {_result} to "%{_result}%%loop-value's name% %{loop-value's level}%, "
    return {_result} before ", "

# Check item attributes
function getItemAttributes(item: object) :: text:
    if {_item::"attributes"} is not set:
        return "None"
    
    set {_result} to ""
    loop {_item::"attributes"}:
        set {_result} to "%{_result}%%loop-value's name%: %{loop-value's amount}% (op: %{loop-value's operation}%), "
    return {_result} before ", "

# Count items by material
function countItemsByMaterial(inventory: object, material: string) :: integer:
    set {_count} to 0
    loop {_inventory::"items"}:
        if loop-value's material is {_material}:
            add loop-value's amount to {_count}
    return {_count}

# Command to test inventory
command /testinventory <text>:
    trigger:
        set {_inv} to loadInventory(arg-1)
        if {_inv} is null:
            message "§cInvalid inventory!"
            set {_errors::*} to last schema validation errors
            loop {_errors::*}:
                message "§c- %loop-value%"
            stop
        
        message "§6=== Inventory of %{_inv::"owner"}% ==="
        message "§aSize: §f%{_inv::"size"}% slots"
        message "§aTotal items: §f%size of {_inv::"items"}%"
        
        message "§6--- Items ---"
        loop {_inv::"items"}:
            set {_item} to loop-value
            message "§eSlot %{_item::"slot"}%: §f%{_item::"material"}% x%{_item::"amount"}%"
            
            if {_item::"display_name"} is set:
                message "   §7Name: §f%{_item::"display_name"}%"
            
            if {_item::"lore"} is set:
                message "   §7Lore: §f%{_item::"lore"}%"
            
            set {_enchs} to getItemEnchantments({_item})
            if {_enchs} is not "None":
                message "   §9Enchantments: §f%{_enchs}%"
            
            set {_attrs} to getItemAttributes({_item})
            if {_attrs} is not "None":
                message "   §bAttributes: §f%{_attrs}%"

# Command to count materials
command /countitems <text>:
    trigger:
        set {_inv} to loadInventory("player-inventory.yml")
        if {_inv} is null:
            message "§cInvalid inventory!"
            stop
        
        set {_count} to countItemsByMaterial({_inv}, arg-1)
        message "§aYou have §f%{_count}% §aof %arg-1%"
```

---

## Example Usage

```
/testinventory player-inventory
```

Output:
```
=== Inventory of Player123 ===
Size: 36 slots
Total items: 3
--- Items ---
Slot 0: DIAMOND_SWORD x1
   Name: §6Legendary Sword
   Enchantments: sharpness 5, unbreaking 3, fire_aspect 2
   Attributes: generic.attack_damage: 50.0 (op: 0)
Slot 1: GOLDEN_APPLE x64
Slot 8: BOW x1
   Enchantments: power 5, punch 2
```

---

## Item Validation by Type

### Armor Schema

```json
{
  "type": "object",
  "properties": {
    "slot": {
      "type": "integer",
      "enum": [36, 37, 38, 39]
    },
    "material": {
      "type": "string",
      "enum": ["DIAMOND_HELMET", "DIAMOND_CHESTPLATE", "DIAMOND_LEGGINGS", "DIAMOND_BOOTS"]
    },
    "armor_bonus": {
      "type": "number",
      "minimum": 0
    }
  }
}
```

---

## Summary

| Feature | Description |
|---------|-------------|
| `enum` | List of allowed values |
| `pattern` | Regex for validation |
| `minimum` / `maximum` | Numeric limits |
| Nested arrays | Lists of enchantments and attributes |

---

## Next Steps

- Study [JSON Schema Reference](reference/json-schema.md)
- Go back to [Quick Start](../quickstart.md)
- See more [Examples](../README.md)

---

[← Back](../README.md) | [Previous: Custom Blocks](custom-blocks.md)
