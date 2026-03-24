---
title: Format reference
description: Supported string formats in FormatValidator with practical examples.
doc_type: reference
order: 4
sequence: 13
permalink: /format-reference.html
---

## About format validation

`format` is enforced by `PrimitiveValidator` for string schemas and delegated to `FormatValidator.isValid(format, value)`.

Unknown format names currently pass validation (no error).

## Standard formats

| Format | Example |
|---|---|
| `date-time` | `2026-03-22T18:40:10Z` |
| `date` | `2026-03-22` |
| `time` | `18:40:10Z` |
| `duration` | `P3DT4H30M` |
| `email`, `idn-email` | `player@example.com` |
| `hostname`, `idn-hostname` | `api.example.local` |
| `ipv4` | `192.168.0.24` |
| `ipv6` | `2001:db8::1` |
| `uri` | `https://example.com/player/42` |
| `uri-reference` | `/player/42` |
| `uri-template` | `https://api.example.com/{playerId}` |
| `json-pointer` | `/inventory/0/id` |
| `relative-json-pointer` | `0/name` |
| `uuid` | `123e4567-e89b-12d3-a456-426614174000` |
| `regex` | `^[a-z0-9_-]+$` |

## Minecraft-specific formats

| Format | Example | Validation |
|---|---|---|
| `minecraft-item` | `minecraft:diamond_sword` | Semantic (Material registry) |
| `minecraft-block` | `minecraft:gold_ore` | Semantic (Material registry) |
| `minecraft-entity` | `minecraft:zombie` | Semantic (EntityType registry) |
| `minecraft-attribute` | `minecraft:generic.max_health` | Semantic (Attribute registry) |
| `minecraft-effect` | `minecraft:speed` | Semantic (PotionEffectType registry) |
| `minecraft-enchantment` | `minecraft:efficiency` | Semantic (Enchantment registry) |
| `minecraft-biome` | `minecraft:plains` | Semantic (Biome registry) |
| `minecraft-dimension` | `minecraft:overworld` | Semantic (DimensionType registry) |
| `minecraft-particle` | `minecraft:block_crack` | Semantic (Particle registry) |
| `minecraft-sound` | `minecraft:block.gold_ore.break` | Semantic (Sound registry) |
| `minecraft-potion` | `minecraft:strength` | Semantic (PotionType registry) |
| `minecraft-recipe` | `minecraft:diamond_sword` | Semantic (Recipe registry) |
| `minecraft-tag` | `#minecraft:pickaxes` | Pattern only (no semantic validation) |

## Example schema snippet

```json
{
  "type": "object",
  "properties": {
    "email": {"type": "string", "format": "email"},
    "blockId": {"type": "string", "format": "minecraft-block"}
  },
  "required": ["email", "blockId"]
}
```

### Semantic validation for Minecraft formats


> [!IMPORTANT]
Since version **1.0.0**, `FormatValidator` uses **real Bukkit registries** for semantic validation when the server is running. **You must call `FormatValidator.initializeCaches()` in your plugin's `onEnable()`** after the server is fully loaded to populate the internal caches.

### How it works

1. **Initialization**  
   `initializeCaches()` collects all valid keys from Bukkit registries (`EntityType`, `Biome`, `Attribute`, `PotionEffectType`, `Particle`, `Sound`, `PotionType`, `Recipe`, etc.) and stores them in static `Set`s.

2. **Validation**  
   - The format string is first checked against a regex for the correct `namespace:name` pattern.  
   - If the pattern matches, the value is looked up in the appropriate cache.  
   - **Custom namespaces** (e.g., `myplugin:custom_item`) are always allowed – this supports plugin‑added content.  
   - If the caches are not initialized (i.e., `initializeCaches()` was not called), a warning is logged and values with `minecraft:` namespace are rejected (they would be considered invalid). Custom namespaces still pass to avoid blocking plugin content.

3. **Offline / test environment**  
   When the server is not running (e.g., during schema development), the validator falls back to pattern‑based validation, accepting any well‑formed `namespace:name`.

### Examples

| Value | Format | Server Running | Result |
|---|---|---|---|
| `minecraft:stone` | `minecraft-block` | Yes | ✅ Valid |
| `minecraft:diamond_ore` | `minecraft-block` | Yes | ✅ Valid |
| `minecraft:um_bloco` | `minecraft-block` | Yes | ❌ Invalid (not a real block) |
| `myplugin:custom_ore` | `minecraft-block` | Yes | ✅ Valid (custom namespace) |
| `minecraft:stone` | `minecraft-block` | No | ✅ Valid (fallback to pattern) |
| `minecraft:diamond_sword` | `minecraft-item` | Yes | ✅ Valid |
| `minecraft:uma_espada` | `minecraft-item` | Yes | ❌ Invalid (not a real item) |
| `minecraft:zombie` | `minecraft-entity` | Yes | ✅ Valid |
| `minecraft:creeper` | `minecraft-entity` | Yes | ✅ Valid |
| `minecraft:uma_entidade` | `minecraft-entity` | Yes | ❌ Invalid (not a real entity) |
| `myplugin:custom_mob` | `minecraft-entity` | Yes | ✅ Valid (custom namespace) |
| `minecraft:speed` | `minecraft-effect` | Yes | ✅ Valid |
| `minecraft:strength` | `minecraft-effect` | Yes | ✅ Valid |
| `minecraft:invalid_effect` | `minecraft-effect` | Yes | ❌ Invalid (not a real effect) |
| `minecraft:generic.max_health` | `minecraft-attribute` | Yes | ✅ Valid |
| `minecraft:generic.attack_speed` | `minecraft-attribute` | Yes | ✅ Valid |
| `minecraft:block.gold_ore.break` | `minecraft-sound` | Yes | ✅ Valid |
| `minecraft:block.gold_ore.break` | `minecraft-sound` | No | ✅ Valid (fallback) |
| `minecraft:flame` | `minecraft-particle` | Yes | ✅ Valid |
| `minecraft:block_crack` | `minecraft-particle` | Yes | ✅ Valid |
| `minecraft:strength` | `minecraft-potion` | Yes | ✅ Valid |
| `minecraft:slowness` | `minecraft-potion` | Yes | ✅ Valid |
| `minecraft:diamond_sword` | `minecraft-recipe` | Yes | ✅ Valid |
| `minecraft:gold_ingot_from_blasting` | `minecraft-recipe` | Yes | ✅ Valid |
| `minecraft:non_existent_recipe` | `minecraft-recipe` | Yes | ❌ Invalid |

### Important caveats

- **Cache initialization**  
  If you forget to call `initializeCaches()` in your plugin's `onEnable()`, validation will log a warning **once** and treat any `minecraft:` value as invalid (since it cannot verify it). Custom namespaces will still be accepted.

- **Tags** (`minecraft-tag`)  
  Currently only pattern validation is performed. Tag existence is not checked because tags can be defined by plugins and are not stored in a central registry.

- **Recipes**  
  Validation for `minecraft-recipe` uses `Bukkit.getRecipe(NamespacedKey)` and works for all recipes registered by the server.

- **Unknown format names**  
  If you use a custom format name that is not implemented in `FormatValidator`, validation will **not** fail on that field. Pair custom format intent with `pattern` when strict enforcement is required.

## Source mapping

- Enforced from primitive path: `PrimitiveValidator.java`
- Format catalog and validation logic: `FormatValidator.java`
- Cache initialization called from plugin: `onEnable()`

## Related pages

- Keyword support matrix: [Schema keywords](schema-keywords.html)
- Practical schema recipes: [Examples](examples.html)
