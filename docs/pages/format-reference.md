---
title: Format reference
description: Supported string formats in FormatValidator with practical examples.
doc_type: reference
order: 6
sequence: 15
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
| `regex` | `^[a-z0-9_-]+` |

### date-time

Validates date and time in ISO 8601 format.

```json
{
  "type": "string",
  "format": "date-time"
}
```

**Valid examples:**
- `2026-03-22T18:40:10Z` (UTC)
- `2026-03-22T18:40:10+00:00` (UTC with offset)
- `2026-03-22T18:40:10-03:00` (São Paulo time)
- `2026-03-22T18:40:10.123Z` (with milliseconds)

**Invalid examples:**
- `2026-03-22` (missing time)
- `18:40:10Z` (missing date)
- `2026/03/22T18:40:10Z` (wrong date separator)
- `2026-03-22 18:40:10Z` (missing T separator)

### date

Validates date in YYYY-MM-DD format.

```json
{
  "type": "string",
  "format": "date"
}
```

**Valid examples:**
- `2026-03-22`
- `2024-02-29` (leap year)
- `2000-01-01`

**Invalid examples:**
- `2026-03-22T18:40:10Z` (includes time)
- `2026/03/22` (wrong separator)
- `22-03-2026` (wrong format)
- `2023-02-29` (not a leap year)

### time

Validates time in HH:MM:SS format with optional timezone.

```json
{
  "type": "string",
  "format": "time"
}
```

**Valid examples:**
- `18:40:10Z` (UTC)
- `18:40:10+00:00` (UTC with offset)
- `18:40:10-03:00` (São Paulo time)
- `18:40:10.123Z` (with milliseconds)

**Invalid examples:**
- `18:40` (missing seconds)
- `25:00:00` (invalid hour)
- `18:60:00` (invalid minute)
- `18:40:60` (invalid second)

### duration

Validates duration in ISO 8601 duration format.

```json
{
  "type": "string",
  "format": "duration"
}
```

**Valid examples:**
- `P3DT4H30M` (3 days, 4 hours, 30 minutes)
- `PT1H30M` (1 hour, 30 minutes)
- `P1Y2M3DT4H5M6S` (1 year, 2 months, 3 days, 4 hours, 5 minutes, 6 seconds)
- `PT30M` (30 minutes)

**Invalid examples:**
- `3DT4H30M` (missing P prefix)
- `P3D4H30M` (missing T separator for time components)
- `P` (empty duration)
- `3 days` (not ISO 8601 format)

### email / idn-email

Validates email address format.

```json
{
  "type": "string",
  "format": "email"
}
```

**Valid examples:**
- `player@example.com`
- `user.name@domain.co.uk`
- `user+tag@example.org`
- `user@subdomain.example.com`

**Invalid examples:**
- `player` (missing @)
- `player@` (missing domain)
- `@example.com` (missing local part)
- `player@.com` (invalid domain)
- `player@example` (missing TLD)

### hostname / idn-hostname

Validates hostname format.

```json
{
  "type": "string",
  "format": "hostname"
}
```

**Valid examples:**
- `api.example.local`
- `www.example.com`
- `subdomain.example.org`
- `localhost`

**Invalid examples:**
- `-example.com` (starts with hyphen)
- `example-.com` (ends with hyphen)
- `example..com` (double dot)
- `.example.com` (starts with dot)
- `example.com.` (ends with dot)

### ipv4

Validates IPv4 address format.

```json
{
  "type": "string",
  "format": "ipv4"
}
```

**Valid examples:**
- `192.168.0.24`
- `10.0.0.1`
- `127.0.0.1`
- `255.255.255.255`
- `0.0.0.0`

**Invalid examples:**
- `256.0.0.1` (octet > 255)
- `192.168.0` (missing octet)
- `192.168.0.1.1` (extra octet)
- `192.168.0.1/24` (includes CIDR notation)
- `192.168.0.1:8080` (includes port)

### ipv6

Validates IPv6 address format.

```json
{
  "type": "string",
  "format": "ipv6"
}
```

**Valid examples:**
- `2001:db8::1`
- `::1` (loopback)
- `fe80::1` (link-local)
- `2001:0db8:85a3:0000:0000:8a2e:0370:7334` (full form)

**Invalid examples:**
- `2001:db8::1::1` (multiple ::)
- `2001:db8::gggg` (invalid hex)
- `2001:db8::1/64` (includes CIDR notation)
- `2001:db8::1:8080` (includes port)

### uri

Validates URI format.

```json
{
  "type": "string",
  "format": "uri"
}
```

**Valid examples:**
- `https://example.com/player/42`
- `http://example.com`
- `ftp://files.example.com`
- `mailto:user@example.com`
- `file:///path/to/file`

**Invalid examples:**
- `example.com` (missing scheme)
- `://example.com` (missing scheme)
- `http://` (missing host)
- `http://example.com:99999` (invalid port)

### uri-reference

Validates URI reference (can be relative or absolute).

```json
{
  "type": "string",
  "format": "uri-reference"
}
```

**Valid examples:**
- `/player/42` (absolute path)
- `player/42` (relative path)
- `./player/42` (relative path with dot)
- `../player/42` (relative path with parent)
- `https://example.com/player/42` (absolute URI)

**Invalid examples:**
- `http://` (invalid URI)
- `://example.com` (invalid URI)

### uri-template

Validates URI template format (RFC 6570).

```json
{
  "type": "string",
  "format": "uri-template"
}
```

**Valid examples:**
- `https://api.example.com/{playerId}`
- `https://api.example.com/users/{userId}/posts/{postId}`
- `https://api.example.com/{?page,limit}`
- `https://api.example.com/{+path}`

**Invalid examples:**
- `https://api.example.com/{` (unclosed brace)
- `https://api.example.com/}` (unmatched brace)
- `https://api.example.com/{invalid var}` (space in variable)

### json-pointer

Validates JSON Pointer format (RFC 6901).

```json
{
  "type": "string",
  "format": "json-pointer"
}
```

**Valid examples:**
- `/inventory/0/id`
- `/users/0/name`
- `/` (root)
- `/foo/bar/baz`

**Invalid examples:**
- `inventory/0/id` (missing leading slash)
- `/foo/bar/` (trailing slash)
- `/foo//bar` (empty segment)

### relative-json-pointer

Validates relative JSON pointer format.

```json
{
  "type": "string",
  "format": "relative-json-pointer"
}
```

**Valid examples:**
- `0/name` (current object)
- `1/name` (parent object)
- `2/name` (grandparent object)
- `0` (current value)

**Invalid examples:**
- `/name` (absolute pointer)
- `-1/name` (negative level)
- `0/name/` (trailing slash)

### regex

Validates regular expression format.

```json
{
  "type": "string",
  "format": "regex"
}
```

**Valid examples:**
- `^[a-z0-9_-]+`
- `^\d{3}-\d{2}-\d{4}---
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

- `^[A-Z][a-z]+---
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

- `.*`

**Invalid examples:**
- `[a-z` (unclosed bracket)
- `(abc` (unclosed parenthesis)
- `*abc` (invalid quantifier)
- `abc\` (trailing backslash)

---

## UUID Formats

Schema-Validator supports UUID validation according to RFC 4122, with support for different versions and representations:

### uuid
Generic UUID validation (standard format with hyphens).

```json
{ "type": "string", "format": "uuid" }
```

Valid examples:
- `550e8400-e29b-41d4-a716-446655440000`
- `6ba7b810-9dad-11d1-80b4-00c04fd430c8`

### uuid-v1
UUID version 1 (time-based) - based on timestamp and MAC address.

```json
{ "type": "string", "format": "uuid-v1" }
```

Valid example: `6ba7b810-9dad-11d1-80b4-00c04fd430c8`

### uuid-v4
UUID version 4 (random) - randomly generated.

```json
{ "type": "string", "format": "uuid-v4" }
```

Valid example: `550e8400-e29b-41d4-a716-446655440000`

### uuid-v5
UUID version 5 (name-based) - generated from namespace and name using SHA-1.

```json
{ "type": "string", "format": "uuid-v5" }
```

Valid example: `2d3b2f50-0bdb-5c48-8097-4c5d8c8c8c8c`

### uuid-nohyphen
UUID without hyphens (32-character compact format).

```json
{ "type": "string", "format": "uuid-nohyphen" }
```

Valid example: `550e8400e29b41d4a716446655440000`

### Conversion Utilities

The `UUIDUtils` class provides methods for conversion between formats:
- `UUIDUtils.normalize(uuid)` - normalizes to lowercase without hyphens
- `UUIDUtils.toStandard(uuid)` - converts to 8-4-4-4-12 format
- `UUIDUtils.toUpperCase(uuid)` - converts to uppercase
- `UUIDUtils.toNoHyphen(uuid)` - removes hyphens

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
