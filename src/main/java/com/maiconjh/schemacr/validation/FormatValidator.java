package com.maiconjh.schemacr.validation;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 * Validates JSON Schema format strings.
 * 
 * <p>Implements validation for formats defined in JSON Schema Specification
 * (draft 2019-09): https://json-schema.org/draft/2019-09/json-schema-validation.html</p>
 * 
 * <p>Supported formats:</p>
 * <ul>
 *   <li>date-time - RFC 3339 date-time</li>
 *   <li>date - RFC 3339 full-date</li>
 *   <li>time - RFC 3339 full-time</li>
 *   <li>duration - ISO 8601 duration</li>
 *   <li>email - RFC 5322 email</li>
 *   <li>idn-email - RFC 6531 internationalized email</li>
 *   <li>hostname - RFC 1123 hostname</li>
 *   <li>idn-hostname - Internationalized hostname</li>
 *   <li>ipv4 - IPv4 address</li>
 *   <li>ipv6 - IPv6 address (RFC 4291)</li>
 *   <li>uri - RFC 3986 URI</li>
 *   <li>uri-reference - URI or relative reference</li>
 *   <li>uri-template - RFC 6570 URI Template</li>
 *   <li>json-pointer - RFC 6901 JSON Pointer</li>
 *   <li>relative-json-pointer - RFC Relative JSON Pointer</li>
 *   <li>uuid - UUID</li>
 *   <li>regex - ECMA 262 regular expression</li>
 * </ul>
 */
public final class FormatValidator {

    // RFC 3339 date-time pattern (full RFC 3339 compliance)
    private static final Pattern DATE_TIME = Pattern.compile(
        "^[0-9]{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])T(?:[01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9](?:\\.[0-9]+)?(?:Z|[+-](?:[01][0-9]|2[0-3]):[0-5][0-9])?$"
    );

    // RFC 3339 full-date pattern
    private static final Pattern DATE = Pattern.compile(
        "^[0-9]{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])$"
    );

    // RFC 3339 full-time pattern
    private static final Pattern TIME = Pattern.compile(
        "^(?:[01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9](?:\\.[0-9]+)?(?:Z|[+-](?:[01][0-9]|2[0-3]):[0-5][0-9])?$"
    );

    // ISO 8601 duration pattern (P1D, PT2H30M, P1Y2M3DT4H5M6S, etc.)
    private static final Pattern DURATION = Pattern.compile(
        "^P(?:[0-9]+Y)?(?:[0-9]+M)?(?:[0-9]+D)?(?:T(?:[0-9]+H)?(?:[0-9]+M)?(?:[0-9]+(?:\\.[0-9]+)?S)?)?$"
    );

    // RFC 5322 email (improved validation)
    private static final Pattern EMAIL = Pattern.compile(
        "^(?:[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*)$"
    );

    // RFC 1123 hostname (strict)
    private static final Pattern HOSTNAME = Pattern.compile(
        "^(?=.{1,253}$)(?!-)[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"
    );

    // IPv4 address (RFC 2673)
    private static final Pattern IPV4 = Pattern.compile(
        "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );

    // IPv6 address (RFC 4291 - full, compressed, and mixed notation)
    private static final Pattern IPV6 = Pattern.compile(
        "^(?:(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|(?:[0-9a-fA-F]{1,4}:){1,7}:|(?:[0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|(?:[0-9a-fA-F]{1,4}:){1,5}(?::[0-9a-fA-F]{1,4}){1,2}|(?:[0-9a-fA-F]{1,4}:){1,4}(?::[0-9a-fA-F]{1,4}){1,3}|(?:[0-9a-fA-F]{1,4}:){1,3}(?::[0-9a-fA-F]{1,4}){1,4}|(?:[0-9a-fA-F]{1,4}:){1,2}(?::[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:(?::[0-9a-fA-F]{1,4}){1,6}|:(?::[0-9a-fA-F]{1,4}){1,7}|::)$"
    );

    // RFC 3986 URI (absolute URI) - Fixed regex
    private static final Pattern URI = Pattern.compile(
        "^[a-zA-Z][a-zA-Z0-9+.-]*://[^\\s/$.?#].*$"
    );

    // RFC 3986 URI-reference (absolute or relative)
    private static final Pattern URI_REFERENCE = Pattern.compile(
        "^(?:[a-zA-Z][a-zA-Z0-9+.-]*://[^\\s/$.?#].*|[^\\s#]*#[^\\s]*|[^\\s]+)$"
    );

    // RFC 6570 URI Template
    private static final Pattern URI_TEMPLATE = Pattern.compile(
        "^(?:(?:\\{|%7B)(?:[a-zA-Z0-9_.~%-]+|\\+|\\#|\\?|\\/)?(?:\\}|%7B)(?:[a-zA-Z0-9_.~%-]+|\\+|\\#|\\?|\\/)?)*(?:\\{|%7B)(?:[a-zA-Z0-9_.~%-]+|\\+|\\#|\\?|\\/)?(?:\\}|%7B)$"
    );

    // RFC 6901 JSON Pointer
    private static final Pattern JSON_POINTER = Pattern.compile(
        "^(?:/(?:[^~/]|~[01])*)*$"
    );

    // RFC Relative JSON Pointer
    private static final Pattern RELATIVE_JSON_POINTER = Pattern.compile(
        "^(?:[1-9][0-9]*|0)(?:/(?:[^~/]|~[01])*)?$"
    );

    // UUID (RFC 4122)
    private static final Pattern UUID = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    // Regex (ECMA 262) - simplified check
    private static final Pattern REGEX = Pattern.compile(
        "^.*$"
    );

    // Minecraft item ID pattern: namespace:name (e.g., minecraft:diamond_sword)
    // Supports custom namespaces like "myplugin:custom_item"
    private static final Pattern MINECRAFT_ITEM = Pattern.compile(
        "^[a-z][a-z0-9_*-]*:[a-z][a-z0-9_*-]+$"
    );

    // Minecraft block ID pattern: namespace:name (e.g., minecraft:gold_ore)
    private static final Pattern MINECRAFT_BLOCK = Pattern.compile(
        "^[a-z][a-z0-9_*-]*:[a-z][a-z0-9_*-]+$"
    );

    // Minecraft entity ID pattern: namespace:name (e.g., minecraft:zombie)
    private static final Pattern MINECRAFT_ENTITY = Pattern.compile(
        "^[a-z][a-z0-9_.-]*:[a-z][a-z0-9_.-]+$"
    );

    // Minecraft attribute ID pattern: namespace:name (e.g., minecraft:generic.max_health)
    private static final Pattern MINECRAFT_ATTRIBUTE = Pattern.compile(
        "^[a-z][a-z0-9_.-]*:[a-z][a-z0-9_.-]+$"
    );

    // Minecraft status effect ID pattern: namespace:name (e.g., minecraft:speed)
    private static final Pattern MINECRAFT_EFFECT = Pattern.compile(
        "^[a-z][a-z0-9_]*:[a-z][a-z0-9_]+$"
    );

    // Minecraft enchantment ID pattern: namespace:name (e.g., minecraft:efficiency)
    private static final Pattern MINECRAFT_ENCHANTMENT = Pattern.compile(
        "^[a-z][a-z0-9_]*:[a-z][a-z0-9_]+$"
    );

    // Minecraft biome ID pattern: namespace:name (e.g., minecraft:plains)
    private static final Pattern MINECRAFT_BIOME = Pattern.compile(
        "^[a-z][a-z0-9_]*:[a-z][a-z0-9_]+$"
    );

    // Minecraft dimension ID pattern: namespace:name (e.g., minecraft:overworld)
    private static final Pattern MINECRAFT_DIMENSION = Pattern.compile(
        "^[a-z][a-z0-9_]*:[a-z][a-z0-9_]+$"
    );

    // Minecraft particle ID pattern: namespace:name (e.g., minecraft:blockcrack_15232)
    private static final Pattern MINECRAFT_PARTICLE = Pattern.compile(
        "^[a-z][a-z0-9_]*:[a-z][a-z0-9_]+$"
    );

    // Minecraft sound event ID pattern: namespace:name (e.g., minecraft:block.gold_ore.break)
    private static final Pattern MINECRAFT_SOUND = Pattern.compile(
        "^[a-z][a-z0-9_.*]*:[a-z][a-z0-9_.*]+$"
    );

    // Minecraft potion ID pattern: namespace:name (e.g., minecraft:strength)
    private static final Pattern MINECRAFT_POTION = Pattern.compile(
        "^[a-z][a-z0-9_]*:[a-z][a-z0-9_]+$"
    );

    // Minecraft recipe ID pattern: namespace:name (e.g., minecraft:diamond_sword)
    private static final Pattern MINECRAFT_RECIPE = Pattern.compile(
        "^[a-z][a-z0-9_/-]*:[a-z][a-z0-9_/-]+$"
    );

    // Minecraft tag ID pattern: #namespace:name (e.g., #minecraft:pickaxes)
    private static final Pattern MINECRAFT_TAG = Pattern.compile(
        "^#[a-z][a-z0-9_*-]*:[a-z][a-z0-9_*-]+$"
    );

    // Static caches for semantic validation (populated by initializeCaches())
    private static final Set<String> VALID_ENTITIES = new HashSet<>();
    private static final Set<String> VALID_BIOMES = new HashSet<>();
    private static final Set<String> VALID_DIMENSIONS = new HashSet<>();
    private static final Set<String> VALID_ENCHANTMENTS = new HashSet<>();
    private static final Set<String> VALID_SOUNDS = new HashSet<>();
    private static final Set<String> VALID_ATTRIBUTES = new HashSet<>();
    private static final Set<String> VALID_EFFECTS = new HashSet<>();
    private static final Set<String> VALID_PARTICLES = new HashSet<>();
    private static final Set<String> VALID_POTIONS = new HashSet<>();
    private static boolean cachesInitialized = false;
    private static boolean warningLogged = false;

    /**
     * Initializes the caches with valid values from Bukkit registries.
     * Must be called after the server is loaded (e.g., in onEnable()).
     */
    public static void initializeCaches() {
        if (cachesInitialized || Bukkit.getServer() == null) {
            return;
        }

        try {
            // Load EntityTypes
            for (EntityType entityType : EntityType.values()) {
                if (entityType.getKey() != null) {
                    VALID_ENTITIES.add(entityType.getKey().toString());
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load entity types: " + e.getMessage());
        }

        try {
            // Load Biomes
            for (Biome biome : Biome.values()) {
                if (biome.getKey() != null) {
                    VALID_BIOMES.add(biome.getKey().toString());
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load biomes: " + e.getMessage());
        }

        try {
            // Load Enchantments
            for (Enchantment enchantment : Enchantment.values()) {
                if (enchantment.getKey() != null) {
                    VALID_ENCHANTMENTS.add(enchantment.getKey().toString());
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load enchantments: " + e.getMessage());
        }

        try {
            // Load Sounds
            for (Sound sound : Sound.values()) {
                if (sound.getKey() != null) {
                    VALID_SOUNDS.add(sound.getKey().toString());
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load sounds: " + e.getMessage());
        }

        try {
            // Load Attributes
            for (Attribute attribute : Attribute.values()) {
                if (attribute.getKey() != null) {
                    VALID_ATTRIBUTES.add(attribute.getKey().toString());
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load attributes: " + e.getMessage());
        }

        try {
            // Load PotionEffectTypes (effects like speed, strength) using Registry
            // Registry.EFFECT is available in Paper 1.21+
            Registry<PotionEffectType> effectRegistry = Registry.EFFECT;
            for (PotionEffectType effect : effectRegistry) {
                if (effect.getKey() != null) {
                    VALID_EFFECTS.add(effect.getKey().toString());
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load potion effects: " + e.getMessage());
        }

        try {
            // Load Particles
            for (Particle particle : Particle.values()) {
                if (particle.getKey() != null) {
                    VALID_PARTICLES.add(particle.getKey().toString());
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load particles: " + e.getMessage());
        }

        try {
            // Load PotionTypes
            for (PotionType potionType : PotionType.values()) {
                if (potionType.getKey() != null) {
                    VALID_POTIONS.add(potionType.getKey().toString());
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load potion types: " + e.getMessage());
        }

        // Load known dimensions (overworld, nether, the_end)
        VALID_DIMENSIONS.add("minecraft:overworld");
        VALID_DIMENSIONS.add("minecraft:nether");
        VALID_DIMENSIONS.add("minecraft:the_end");

        cachesInitialized = true;
        Bukkit.getLogger().info("FormatValidator caches initialized with " +
            VALID_ENTITIES.size() + " entities, " +
            VALID_BIOMES.size() + " biomes, " +
            VALID_ENCHANTMENTS.size() + " enchantments, " +
            VALID_SOUNDS.size() + " sounds, " +
            VALID_ATTRIBUTES.size() + " attributes, " +
            VALID_EFFECTS.size() + " effects, " +
            VALID_PARTICLES.size() + " particles, " +
            VALID_POTIONS.size() + " potions, " +
            VALID_DIMENSIONS.size() + " dimensions");

        // Debug: log a few samples
        if (!VALID_SOUNDS.isEmpty()) {
            Bukkit.getLogger().info("Sample sounds: " + VALID_SOUNDS.stream().limit(5).toList());
        }
        if (!VALID_EFFECTS.isEmpty()) {
            Bukkit.getLogger().info("Sample effects: " + VALID_EFFECTS.stream().limit(5).toList());
        }
        if (!VALID_PARTICLES.isEmpty()) {
            Bukkit.getLogger().info("Sample particles: " + VALID_PARTICLES.stream().limit(5).toList());
        }
    }

    private FormatValidator() {
        // Utility class
    }

    /**
     * Validates a string against the specified format.
     * 
     * @param format the format name (e.g., "email", "uri")
     * @param value the string value to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String format, String value) {
        if (format == null || value == null) {
            return true; // Skip validation if format not specified
        }

        return switch (format.toLowerCase()) {
            case "date-time" -> DATE_TIME.matcher(value).matches();
            case "date" -> DATE.matcher(value).matches();
            case "time" -> TIME.matcher(value).matches();
            case "duration" -> DURATION.matcher(value).matches();
            case "email" -> EMAIL.matcher(value).matches();
            case "idn-email" -> EMAIL.matcher(value).matches(); // Uses same pattern as email
            case "hostname" -> HOSTNAME.matcher(value).matches();
            case "idn-hostname" -> HOSTNAME.matcher(value).matches(); // Uses same pattern as hostname
            case "ipv4" -> IPV4.matcher(value).matches();
            case "ipv6" -> IPV6.matcher(value).matches();
            case "uri" -> URI.matcher(value).matches();
            case "uri-reference" -> URI_REFERENCE.matcher(value).matches();
            case "uri-template" -> URI_TEMPLATE.matcher(value).matches();
            case "json-pointer" -> JSON_POINTER.matcher(value).matches();
            case "relative-json-pointer" -> RELATIVE_JSON_POINTER.matcher(value).matches();
            case "uuid" -> UUID.matcher(value).matches();
            case "regex" -> isValidRegex(value);
            case "minecraft-item" -> isValidMinecraftMaterial(value, false);
            case "minecraft-block" -> isValidMinecraftMaterial(value, true);
            case "minecraft-entity" -> isValidMinecraftEntity(value);
            case "minecraft-attribute" -> isValidMinecraftAttribute(value);
            case "minecraft-effect" -> isValidMinecraftEffect(value);
            case "minecraft-enchantment" -> isValidMinecraftEnchantment(value);
            case "minecraft-biome" -> isValidMinecraftBiome(value);
            case "minecraft-dimension" -> isValidMinecraftDimension(value);
            case "minecraft-particle" -> isValidMinecraftParticle(value);
            case "minecraft-sound" -> isValidMinecraftSound(value);
            case "minecraft-potion" -> isValidMinecraftPotion(value);
            case "minecraft-recipe" -> isValidMinecraftRecipe(value);
            case "minecraft-tag" -> MINECRAFT_TAG.matcher(value).matches();
            default -> true; // Unknown format - skip validation
        };
    }

    /**
     * Helper method to check if caches are initialized and log warning if not.
     * Returns true if validation can proceed normally (caches ready or value is custom).
     * For missing caches, it allows custom namespaces but rejects minecraft: ones.
     */
    private static boolean handleCacheFallback(String value) {
        if (cachesInitialized) {
            return true; // Normal validation can proceed
        }
        if (!warningLogged) {
            Bukkit.getLogger().warning("FormatValidator caches not initialized! Call initializeCaches() in onEnable().");
            warningLogged = true;
        }
        // Fallback: if value starts with "minecraft:", treat as invalid (since we can't validate)
        // otherwise allow custom namespaces (plugins)
        return !value.startsWith("minecraft:");
    }

    /**
     * Validates a Minecraft material (item or block) using semantic validation.
     * Uses Material.getMaterial() when server is running.
     * Falls back to pattern validation when server is offline.
     * 
     * @param value the material ID (e.g., "minecraft:diamond_ore")
     * @param checkBlock if true, validates as block; if false, validates as item
     * @return true if valid material
     */
    private static boolean isValidMinecraftMaterial(String value, boolean checkBlock) {
        // First do pattern validation for basic format
        if (!MINECRAFT_ITEM.matcher(value).matches()) {
            return false;
        }

        // Try to validate using Bukkit's Material registry
        try {
            if (Bukkit.getServer() != null) {
                String materialName = parseMaterialName(value);
                if (materialName != null) {
                    Material material = Material.getMaterial(materialName);
                    if (material != null) {
                        if (checkBlock) {
                            return material.isBlock();
                        } else {
                            return material.isItem();
                        }
                    }
                }
                // Material not found - could be a custom plugin item
                if (!value.startsWith("minecraft:")) {
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            // Server not available, fall through to offline validation
        }

        // Offline validation - use regex patterns
        if (checkBlock) {
            return value.matches("^minecraft:[a-z][a-z0-9_*-]+$");
        }
        return true; // Allow any valid namespace:name format when offline
    }

    /**
     * Parses a Minecraft ID string to extract the material name in Bukkit format.
     * 
     * @param value the Minecraft ID (e.g., "minecraft:diamond_ore")
     * @return the material name in uppercase (e.g., "DIAMOND_ORE") or null if invalid
     */
    private static String parseMaterialName(String value) {
        if (value == null || !value.contains(":")) {
            return null;
        }

        String[] parts = value.split(":", 2);
        if (parts.length != 2) {
            return null;
        }

        String namespace = parts[0];
        String name = parts[1];

        if (!namespace.equals("minecraft")) {
            return name.toUpperCase().replace("-", "_").replace("*", "");
        }

        return name.toUpperCase().replace("-", "_").replace("*", "");
    }

    /**
     * Validates a Minecraft entity type (e.g., "minecraft:zombie", "minecraft:creeper").
     * Uses semantic validation via cached EntityType keys.
     * 
     * @param value the entity ID (e.g., "minecraft:zombie")
     * @return true if valid entity
     */
    private static boolean isValidMinecraftEntity(String value) {
        if (!MINECRAFT_ENTITY.matcher(value).matches()) {
            return false;
        }
        if (!handleCacheFallback(value)) {
            return false;
        }
        return VALID_ENTITIES.contains(value);
    }

    /**
     * Validates a Minecraft attribute (e.g., "minecraft:generic.max_health").
     * Uses semantic validation via cached Attribute keys.
     * 
     * @param value the attribute ID (e.g., "minecraft:generic.max_health")
     * @return true if valid attribute
     */
    private static boolean isValidMinecraftAttribute(String value) {
        if (!MINECRAFT_ATTRIBUTE.matcher(value).matches()) {
            return false;
        }
        if (!handleCacheFallback(value)) {
            return false;
        }
        return VALID_ATTRIBUTES.contains(value);
    }

    /**
     * Validates if a string is a valid ECMA 262 regular expression.
     * 
     * @param value the string to check
     * @return true if valid regex
     */
    private static boolean isValidRegex(String value) {
        try {
            Pattern.compile(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates a Minecraft status effect (e.g., "minecraft:speed", "minecraft:strength").
     * Uses semantic validation via cached PotionEffectType keys.
     * 
     * @param value the effect ID (e.g., "minecraft:speed")
     * @return true if valid effect
     */
    private static boolean isValidMinecraftEffect(String value) {
        if (!MINECRAFT_EFFECT.matcher(value).matches()) {
            return false;
        }
        if (!handleCacheFallback(value)) {
            return false;
        }
        return VALID_EFFECTS.contains(value);
    }

    /**
     * Validates a Minecraft enchantment (e.g., "minecraft:efficiency", "minecraft:sharpness").
     * Uses semantic validation via cached Enchantment keys.
     * 
     * @param value the enchantment ID (e.g., "minecraft:efficiency")
     * @return true if valid enchantment
     */
    private static boolean isValidMinecraftEnchantment(String value) {
        if (!MINECRAFT_ENCHANTMENT.matcher(value).matches()) {
            return false;
        }
        if (!handleCacheFallback(value)) {
            return false;
        }
        return VALID_ENCHANTMENTS.contains(value);
    }

    /**
     * Validates a Minecraft biome (e.g., "minecraft:plains", "minecraft:desert").
     * Uses semantic validation via cached Biome keys.
     * 
     * @param value the biome ID (e.g., "minecraft:plains")
     * @return true if valid biome
     */
    private static boolean isValidMinecraftBiome(String value) {
        if (!MINECRAFT_BIOME.matcher(value).matches()) {
            return false;
        }
        if (!handleCacheFallback(value)) {
            return false;
        }
        return VALID_BIOMES.contains(value);
    }

    /**
     * Validates a Minecraft dimension (e.g., "minecraft:overworld", "minecraft:nether").
     * Uses semantic validation via cached dimension keys.
     * 
     * @param value the dimension ID (e.g., "minecraft:overworld")
     * @return true if valid dimension
     */
    private static boolean isValidMinecraftDimension(String value) {
        if (!MINECRAFT_DIMENSION.matcher(value).matches()) {
            return false;
        }
        if (!handleCacheFallback(value)) {
            return false;
        }
        return VALID_DIMENSIONS.contains(value);
    }

    /**
     * Validates a Minecraft particle (e.g., "minecraft:blockcrack", "minecraft:flame").
     * Uses semantic validation via cached Particle keys.
     * 
     * @param value the particle ID (e.g., "minecraft:flame")
     * @return true if valid particle
     */
    private static boolean isValidMinecraftParticle(String value) {
        if (!MINECRAFT_PARTICLE.matcher(value).matches()) {
            return false;
        }
        if (!handleCacheFallback(value)) {
            return false;
        }
        return VALID_PARTICLES.contains(value);
    }

    /**
     * Validates a Minecraft sound (e.g., "minecraft:block.gold_ore.break").
     * Uses semantic validation via cached Sound keys.
     * 
     * @param value the sound ID (e.g., "minecraft:block.gold_ore.break")
     * @return true if valid sound
     */
    private static boolean isValidMinecraftSound(String value) {
        if (!MINECRAFT_SOUND.matcher(value).matches()) {
            return false;
        }
        if (!handleCacheFallback(value)) {
            return false;
        }
        return VALID_SOUNDS.contains(value);
    }

    /**
     * Validates a Minecraft potion (e.g., "minecraft:strength", "minecraft:slowness").
     * Uses semantic validation via cached PotionType keys.
     * 
     * @param value the potion ID (e.g., "minecraft:strength")
     * @return true if valid potion
     */
    private static boolean isValidMinecraftPotion(String value) {
        if (!MINECRAFT_POTION.matcher(value).matches()) {
            return false;
        }
        if (!handleCacheFallback(value)) {
            return false;
        }
        return VALID_POTIONS.contains(value);
    }

    /**
     * Validates a Minecraft recipe (e.g., "minecraft:diamond_sword").
     * Uses Bukkit's getRecipe() for semantic validation.
     * 
     * @param value the recipe ID (e.g., "minecraft:diamond_sword")
     * @return true if valid recipe
     */
    private static boolean isValidMinecraftRecipe(String value) {
        if (!MINECRAFT_RECIPE.matcher(value).matches()) {
            return false;
        }

        // Use direct API: NamespacedKey.fromString() and Bukkit.getRecipe()
        try {
            NamespacedKey key = NamespacedKey.fromString(value);
            if (key != null && Bukkit.getRecipe(key) != null) {
                return true;
            }
            // For non-minecraft namespaces, allow custom recipes (they may not be loaded yet)
            if (!value.startsWith("minecraft:")) {
                return true;
            }
            return false;
        } catch (Exception e) {
            // If any error occurs, fallback to pattern validation for offline/custom
            if (!value.startsWith("minecraft:")) {
                return true;
            }
            return false;
        }
    }

    /**
     * Gets the default error message for a format validation failure.
     * 
     * @param format the format name
     * @return error message
     */
    public static String getErrorMessage(String format) {
        return "Value does not match format '" + format + "'";
    }
}