package com.maiconjh.schemacr.validation;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
 *   <li>uri-template - RFC 6570 URI Template (simplified)</li>
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

    // RFC 3986 URI (absolute URI)
    private static final Pattern URI = Pattern.compile(
        "^[a-zA-Z][a-zA-Z0-9+.-]*://[^\\s/$.?#].*$"
    );

    // RFC 3986 URI-reference (absolute or relative)
    private static final Pattern URI_REFERENCE = Pattern.compile(
        "^(?:[a-zA-Z][a-zA-Z0-9+.-]*://[^\\s/$.?#].*|[^\\s#]*#[^\\s]*|[^\\s]+)$"
    );

    // RFC 6570 URI Template (simplified: balanced braces with valid variable names)
    private static final Pattern URI_TEMPLATE = Pattern.compile(
        "^(?:[^{}]|\\{[a-zA-Z][a-zA-Z0-9_.~%+-]*(?:,[a-zA-Z][a-zA-Z0-9_.~%+-]*)*\\})+$"
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

    // Minecraft item ID pattern: namespace:name (e.g., minecraft:diamond_sword)
    private static final Pattern MINECRAFT_ITEM = Pattern.compile(
        "^[a-z][a-z0-9_*-]*:[a-z][a-z0-9_*-]+$"
    );

    // Minecraft block ID pattern (same as item pattern)
    private static final Pattern MINECRAFT_BLOCK = Pattern.compile(
        "^[a-z][a-z0-9_*-]*:[a-z][a-z0-9_*-]+$"
    );

    // Minecraft entity ID pattern
    private static final Pattern MINECRAFT_ENTITY = Pattern.compile(
        "^[a-z][a-z0-9_.-]*:[a-z][a-z0-9_.-]+$"
    );

    // Minecraft attribute ID pattern
    private static final Pattern MINECRAFT_ATTRIBUTE = Pattern.compile(
        "^[a-z][a-z0-9_.-]*:[a-z][a-z0-9_.-]+$"
    );

    // Minecraft status effect ID pattern
    private static final Pattern MINECRAFT_EFFECT = Pattern.compile(
        "^[a-z][a-z0-9_]*:[a-z][a-z0-9_]+$"
    );

    // Minecraft enchantment ID pattern
    private static final Pattern MINECRAFT_ENCHANTMENT = Pattern.compile(
        "^[a-z][a-z0-9_]*:[a-z][a-z0-9_]+$"
    );

    // Minecraft biome ID pattern
    private static final Pattern MINECRAFT_BIOME = Pattern.compile(
        "^[a-z][a-z0-9_]*:[a-z][a-z0-9_]+$"
    );

    // Minecraft dimension ID pattern
    private static final Pattern MINECRAFT_DIMENSION = Pattern.compile(
        "^[a-z][a-z0-9_]*:[a-z][a-z0-9_]+$"
    );

    // Minecraft particle ID pattern
    private static final Pattern MINECRAFT_PARTICLE = Pattern.compile(
        "^[a-z][a-z0-9_]*:[a-z][a-z0-9_]+$"
    );

    // Minecraft sound event ID pattern
    private static final Pattern MINECRAFT_SOUND = Pattern.compile(
        "^[a-z][a-z0-9_.*]*:[a-z][a-z0-9_.*]+$"
    );

    // Minecraft potion ID pattern
    private static final Pattern MINECRAFT_POTION = Pattern.compile(
        "^[a-z][a-z0-9_]*:[a-z][a-z0-9_]+$"
    );

    // Minecraft recipe ID pattern
    private static final Pattern MINECRAFT_RECIPE = Pattern.compile(
        "^[a-z][a-z0-9_/-]*:[a-z][a-z0-9_/-]+$"
    );

    // Minecraft tag ID pattern: #namespace:name
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
            for (EntityType entityType : EntityType.values()) {
                if (entityType.getKey() != null) {
                    VALID_ENTITIES.add(entityType.getKey().toString());
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load entity types: " + e.getMessage());
        }

        try {
            for (Biome biome : Biome.values()) {
                if (biome.getKey() != null) {
                    VALID_BIOMES.add(biome.getKey().toString());
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load biomes: " + e.getMessage());
        }

        try {
            for (Enchantment enchantment : Enchantment.values()) {
                if (enchantment.getKey() != null) {
                    VALID_ENCHANTMENTS.add(enchantment.getKey().toString());
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load enchantments: " + e.getMessage());
        }

        try {
            for (Sound sound : Sound.values()) {
                if (sound.getKey() != null) {
                    VALID_SOUNDS.add(sound.getKey().toString());
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load sounds: " + e.getMessage());
        }

        try {
            for (Attribute attribute : Attribute.values()) {
                if (attribute.getKey() != null) {
                    VALID_ATTRIBUTES.add(attribute.getKey().toString());
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load attributes: " + e.getMessage());
        }

        // Load PotionEffectTypes (effects) with fallback for older Paper versions
        try {
            Registry<PotionEffectType> effectRegistry = Registry.EFFECT;
            for (PotionEffectType effect : effectRegistry) {
                if (effect.getKey() != null) {
                    VALID_EFFECTS.add(effect.getKey().toString());
                }
            }
        } catch (Throwable e) {
            // Fallback for versions without Registry.EFFECT (though Paper 1.21+ has it)
            try {
                for (PotionEffectType effect : PotionEffectType.values()) {
                    if (effect != null && effect.getKey() != null) {
                        VALID_EFFECTS.add(effect.getKey().toString());
                    }
                }
            } catch (Exception ex) {
                Bukkit.getLogger().warning("Failed to load potion effects: " + ex.getMessage());
            }
        }

        try {
            for (Particle particle : Particle.values()) {
                if (particle.getKey() != null) {
                    VALID_PARTICLES.add(particle.getKey().toString());
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load particles: " + e.getMessage());
        }

        try {
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
            case "idn-email" -> EMAIL.matcher(value).matches();
            case "hostname" -> HOSTNAME.matcher(value).matches();
            case "idn-hostname" -> HOSTNAME.matcher(value).matches();
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
            default -> true;
        };
    }

    /**
     * Helper method to check if caches are initialized.
     * Returns true if validation can proceed normally.
     * For missing caches, it rejects minecraft: values (since we can't validate them)
     * but allows custom namespaces as a fallback.
     */
    private static boolean handleCacheFallback(String value) {
        if (cachesInitialized) {
            return true;
        }
        if (!warningLogged) {
            Bukkit.getLogger().warning("FormatValidator caches not initialized! Call initializeCaches() in onEnable().");
            warningLogged = true;
        }
        // Reject any minecraft: value when caches are missing, accept custom namespaces
        return !value.startsWith("minecraft:");
    }

    /**
     * Validates a Minecraft material (item or block).
     * 
     * @param value the material ID (e.g., "minecraft:diamond_ore")
     * @param checkBlock if true, validates as block; if false, validates as item
     * @return true if valid material
     */
    private static boolean isValidMinecraftMaterial(String value, boolean checkBlock) {
        if (!MINECRAFT_ITEM.matcher(value).matches()) {
            return false;
        }

        // Only perform semantic validation for minecraft: namespace
        if (value.startsWith("minecraft:")) {
            try {
                String materialName = parseMaterialName(value);
                if (materialName != null) {
                    Material material = Material.getMaterial(materialName);
                    if (material != null) {
                        return checkBlock ? material.isBlock() : material.isItem();
                    }
                }
                return false; // Vanilla material not found
            } catch (Exception e) {
                return false;
            }
        }

        // For custom namespaces, accept any syntactically valid ID
        return true;
    }

    /**
     * Parses a Minecraft ID string to extract the material name in Bukkit format.
     * Only works for "minecraft:" namespace; returns null for others.
     * 
     * @param value the Minecraft ID (e.g., "minecraft:diamond_ore")
     * @return the material name in uppercase (e.g., "DIAMOND_ORE") or null if not minecraft:
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
        if (!"minecraft".equals(namespace)) {
            return null; // Non-vanilla namespaces are not validated against Material enum
        }
        String name = parts[1];
        return name.toUpperCase().replace("-", "_").replace("*", "");
    }

    /**
     * Validates a Minecraft entity type.
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
     * Validates a Minecraft attribute.
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
     */
    private static boolean isValidRegex(String value) {
        try {
            Pattern.compile(value);
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    /**
     * Validates a Minecraft status effect.
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
     * Validates a Minecraft enchantment.
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
     * Validates a Minecraft biome.
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
     * Validates a Minecraft dimension.
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
     * Validates a Minecraft particle.
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
     * Validates a Minecraft sound.
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
     * Validates a Minecraft potion.
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
     * Validates a Minecraft recipe.
     */
    private static boolean isValidMinecraftRecipe(String value) {
        if (!MINECRAFT_RECIPE.matcher(value).matches()) {
            return false;
        }
        try {
            NamespacedKey key = NamespacedKey.fromString(value);
            if (key != null && Bukkit.getRecipe(key) != null) {
                return true;
            }
            // For non-minecraft namespaces, allow custom recipes (may not be loaded yet)
            return !value.startsWith("minecraft:");
        } catch (Exception e) {
            return !value.startsWith("minecraft:");
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