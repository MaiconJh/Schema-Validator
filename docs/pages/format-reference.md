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

| Format | Example |
|---|---|
| `minecraft-item` | `minecraft:diamond_sword` |
| `minecraft-block` | `minecraft:gold_ore` |
| `minecraft-entity` | `minecraft:zombie` |
| `minecraft-attribute` | `minecraft:generic.max_health` |
| `minecraft-effect` | `minecraft:speed` |
| `minecraft-enchantment` | `minecraft:efficiency` |
| `minecraft-biome` | `minecraft:plains` |
| `minecraft-dimension` | `minecraft:overworld` |
| `minecraft-particle` | `minecraft:block_crack` |
| `minecraft-sound` | `minecraft:block.gold_ore.break` |
| `minecraft-potion` | `minecraft:strength` |
| `minecraft-recipe` | `minecraft:diamond_sword` |
| `minecraft-tag` | `#minecraft:pickaxes` |

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

## Important caveat

> [!WARNING]
> If you use a custom format name that is not implemented in `FormatValidator`, validation will not fail on that field. Pair custom format intent with `pattern` when strict enforcement is required.

## Source mapping

- Enforced from primitive path: `PrimitiveValidator.java`
- Format catalog and regexes: `FormatValidator.java`

## Related pages

- Keyword support matrix: [Schema keywords](schema-keywords.html)
- Practical schema recipes: [Examples](examples.html)
