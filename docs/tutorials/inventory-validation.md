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
            "format": "minecraft-item"
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
                  "format": "minecraft-enchantment"
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
                  "format": "minecraft-attribute"
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
    material: "minecraft:diamond_sword"
    amount: 1
    durability: 100
    display_name: "§6Legendary Sword"
    lore:
      - "§7A sword of immense power"
      - "§c+50 damage"
    enchantments:
      - name: "minecraft:sharpness"
        level: 5
      - name: "minecraft:unbreaking"
        level: 3
      - name: "minecraft:fire_aspect"
        level: 2
    attributes:
      - name: "minecraft:generic.attack_damage"
        operation: 0
        amount: 50.0
        slot: "mainhand"
  - slot: 1
    material: "minecraft:golden_apple"
    amount: 64
    durability: 0
  - slot: 8
    material: "minecraft:bow"
    amount: 1
    enchantments:
      - name: "minecraft:power"
        level: 5
      - name: "minecraft:punch"
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
Slot 0: minecraft:diamond_sword x1
   Name: §6Legendary Sword
   Enchantments: minecraft:sharpness 5, minecraft:unbreaking 3, minecraft:fire_aspect 2
   Attributes: minecraft:generic.attack_damage: 50.0 (op: 0)
Slot 1: minecraft:golden_apple x64
Slot 8: minecraft:bow x1
   Enchantments: minecraft:power 5, minecraft:punch 2
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
      "format": "minecraft-item",
      "enum": ["minecraft:diamond_helmet", "minecraft:diamond_chestplate", "minecraft:diamond_leggings", "minecraft:diamond_boots"]
    },
    "armor_bonus": {
      "type": "number",
      "minimum": 0
    }
  }
}
```

---

## Minecraft Format Validation

This tutorial uses the Minecraft-specific format validators:

| Format | Description | Example |
|--------|-------------|---------|
| `minecraft-item` | Validates Minecraft item IDs | `minecraft:diamond_sword` |
| `minecraft-enchantment` | Validates enchantment IDs | `minecraft:sharpness` |
| `minecraft-attribute` | Validates attribute IDs | `minecraft:generic.attack_damage` |

> **Important:** Always use the `minecraft:` namespace prefix. Using just `diamond_sword` will fail validation.

---

## Summary

| Feature | Description |
|---------|-------------|
| `enum` | List of allowed values |
| `format` | Minecraft-specific validation (minecraft-item, minecraft-enchantment, etc.) |
| `minimum` / `maximum` | Numeric limits |
| Nested arrays | Lists of enchantments and attributes |

---

## Next Steps

- Study [JSON Schema Reference](reference/json-schema.md)
- Go back to [Quick Start](../quickstart.md)
- See more [Examples](../README.md)

---

[← Back](../README.md) | [Previous: Custom Blocks](custom-blocks.md)
