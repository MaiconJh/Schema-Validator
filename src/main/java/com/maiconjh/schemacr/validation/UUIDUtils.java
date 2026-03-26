package com.maiconjh.schemacr.validation;

import java.util.regex.Pattern;

/**
 * Utility class for UUID validation, conversion, and extraction operations.
 * Provides pre-compiled regex patterns and methods for working with UUIDs
 * in various formats (standard, no-hyphen, version-specific).
 */
public final class UUIDUtils {

    // ==================== CONSTANTS ====================

    /**
     * Standard UUID format: 8-4-4-4-12 with hyphens (xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx)
     */
    public static final Pattern UUID_STANDARD = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * UUID format without hyphens: 32 hexadecimal characters
     */
    public static final Pattern UUID_NO_HYPHEN = Pattern.compile(
        "^[0-9a-fA-F]{32}$",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * UUID v1 (time-based) pattern.
     * Version 1 is identified by having '1' at the 13th character position.
     */
    public static final Pattern UUID_V1 = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-1[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * UUID v4 (random) pattern.
     * Version 4 is identified by having '4' at the 13th character position.
     * Variant is at position 17 (first char of 4th group): 8, 9, a, or b
     */
    public static final Pattern UUID_V4 = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * UUID v5 (name-based SHA-1) pattern.
     * Version 5 is identified by having '5' at the 13th character position.
     * Variant is at position 17 (first char of 4th group): 8, 9, a, or b
     */
    public static final Pattern UUID_V5 = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-5[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
        Pattern.CASE_INSENSITIVE
    );

    // ==================== CONSTRUCTOR ====================

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class.
     */
    private UUIDUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ==================== VALIDATION METHODS ====================

    /**
     * Validates if the given string is a valid UUID (any version).
     * Accepts both standard format (with hyphens) and no-hyphen format.
     *
     * @param value the string to validate
     * @return true if valid UUID, false otherwise
     */
    public static boolean isValidUuid(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return UUID_STANDARD.matcher(value).matches() || UUID_NO_HYPHEN.matcher(value).matches();
    }

    /**
     * Validates if the given string is a valid UUID v1 (time-based).
     *
     * @param value the string to validate
     * @return true if valid UUID v1, false otherwise
     */
    public static boolean isValidUuidV1(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return UUID_V1.matcher(value).matches();
    }

    /**
     * Validates if the given string is a valid UUID v4 (random).
     *
     * @param value the string to validate
     * @return true if valid UUID v4, false otherwise
     */
    public static boolean isValidUuidV4(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return UUID_V4.matcher(value).matches();
    }

    /**
     * Validates if the given string is a valid UUID v5 (name-based SHA-1).
     *
     * @param value the string to validate
     * @return true if valid UUID v5, false otherwise
     */
    public static boolean isValidUuidV5(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return UUID_V5.matcher(value).matches();
    }

    /**
     * Validates if the given string is a valid UUID without hyphens.
     *
     * @param value the string to validate
     * @return true if valid UUID without hyphens, false otherwise
     */
    public static boolean isValidUuidNoHyphen(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return UUID_NO_HYPHEN.matcher(value).matches();
    }

    // ==================== CONVERSION METHODS ====================

    /**
     * Normalizes a UUID by removing hyphens and converting to lowercase.
     *
     * @param uuid the UUID to normalize
     * @return normalized UUID (lowercase, no hyphens), or null if input is null/invalid
     */
    public static String normalize(String uuid) {
        if (uuid == null) {
            return null;
        }
        String normalized = uuid.replace("-", "");
        if (!UUID_NO_HYPHEN.matcher(normalized).matches()) {
            return null;
        }
        return normalized.toLowerCase();
    }

    /**
     * Converts a UUID to standard format (8-4-4-4-12 with hyphens).
     *
     * @param uuid the UUID to convert
     * @return UUID in standard format, or null if input is null/invalid
     */
    public static String toStandard(String uuid) {
        if (uuid == null) {
            return null;
        }
        String normalized = uuid.replace("-", "");
        if (!UUID_NO_HYPHEN.matcher(normalized).matches()) {
            return null;
        }
        return addHyphens(normalized);
    }

    /**
     * Converts a UUID to uppercase.
     *
     * @param uuid the UUID to convert
     * @return UUID in uppercase, or null if input is null
     */
    public static String toUpperCase(String uuid) {
        if (uuid == null) {
            return null;
        }
        return uuid.toUpperCase();
    }

    /**
     * Converts a UUID to lowercase.
     *
     * @param uuid the UUID to convert
     * @return UUID in lowercase, or null if input is null
     */
    public static String toLowerCase(String uuid) {
        if (uuid == null) {
            return null;
        }
        return uuid.toLowerCase();
    }

    /**
     * Removes hyphens from a UUID.
     *
     * @param uuid the UUID to convert
     * @return UUID without hyphens, or null if input is null/invalid
     */
    public static String toNoHyphen(String uuid) {
        if (uuid == null) {
            return null;
        }
        return uuid.replace("-", "");
    }

    /**
     * Adds hyphens to a UUID in standard 8-4-4-4-12 format.
     *
     * @param uuid the UUID (32 characters, no hyphens)
     * @return UUID with hyphens, or null if input is null/invalid
     */
    public static String addHyphens(String uuid) {
        if (uuid == null) {
            return null;
        }
        String normalized = uuid.replace("-", "");
        if (normalized.length() != 32 || !UUID_NO_HYPHEN.matcher(normalized).matches()) {
            return null;
        }
        StringBuilder sb = new StringBuilder(36);
        sb.append(normalized, 0, 8);
        sb.append('-');
        sb.append(normalized, 8, 12);
        sb.append('-');
        sb.append(normalized, 12, 16);
        sb.append('-');
        sb.append(normalized, 16, 20);
        sb.append('-');
        sb.append(normalized, 20, 32);
        return sb.toString();
    }

    // ==================== EXTRACTION METHODS ====================

    /**
     * Extracts the version number from a UUID.
     * The version is located at the 13th character position (index 12).
     *
     * @param uuid the UUID to extract version from
     * @return the version number (1, 4, 5, etc.), or -1 if invalid
     */
    public static int getVersion(String uuid) {
        if (uuid == null || !isValidUuid(uuid)) {
            return -1;
        }
        String normalized = uuid.replace("-", "");
        if (normalized.length() != 32) {
            return -1;
        }
        char versionChar = normalized.charAt(12);
        switch (versionChar) {
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case 'a':
            case 'A':
                return 10;
            case 'b':
            case 'B':
                return 11;
            case 'c':
            case 'C':
                return 12;
            case 'd':
            case 'D':
                return 13;
            case 'e':
            case 'E':
                return 14;
            case 'f':
            case 'F':
                return 15;
            default:
                return -1;
        }
    }

    /**
     * Extracts the variant from a UUID.
     * The variant is located at the 17th character position (index 16).
     *
     * @param uuid the UUID to extract variant from
     * @return the variant number (0, 1, 2, 3 for NCS, RFC, Microsoft, Future),
     *         or -1 if invalid
     */
    public static int getVariant(String uuid) {
        if (uuid == null || !isValidUuid(uuid)) {
            return -1;
        }
        String normalized = uuid.replace("-", "");
        if (normalized.length() != 32) {
            return -1;
        }
        char variantChar = Character.toLowerCase(normalized.charAt(16));
        
        // RFC 4122 variant encoding:
        // 0xxx - NCS backward compatibility
        // 10xx - RFC 4122
        // 11x - Microsoft backward compatibility
        // 111 - Reserved for future
        switch (variantChar) {
            case '0':
            case '1':
            case '2':
            case '3':
                return 0; // NCS
            case '4':
            case '5':
            case '6':
            case '7':
                return 1; // RFC 4122
            case '8':
            case '9':
            case 'a':
            case 'b':
                return 1; // RFC 4122 (most common)
            case 'c':
            case 'd':
                return 2; // Microsoft
            case 'e':
            case 'f':
                return 3; // Reserved
            default:
                return -1;
        }
    }
}
