package com.maiconjh.schemacr.validation;

import java.util.regex.Pattern;

/**
 * Validates JSON Schema format strings.
 * 
 * <p>Implements validation for formats defined in JSON Schema Specification
 * (draft-07): https://json-schema.org/draft-07/json-schema-validation.html#rfc.section.7</p>
 * 
 * <p>Supported formats:</p>
 * <ul>
 *   <li>date-time - RFC 3339 date-time</li>
 *   <li>date - RFC 3339 full-date</li>
 *   <li>time - RFC 3339 full-time</li>
 *   <li>email - RFC 5322 email</li>
 *   <li>idn-email - RFC 6531 internationalized email</li>
 *   <li>hostname - RFC 1123 hostname</li>
 *   <li>idn-hostname - Internationalized hostname</li>
 *   <li>ipv4 - IPv4 address</li>
 *   <li>ipv6 - IPv6 address</li>
 *   <li>uri - RFC 3986 URI</li>
 *   <li>uri-reference - URI or relative reference</li>
 *   <li>uuid - UUID</li>
 *   <li>regex - ECMA 262 regular expression</li>
 * </ul>
 */
public final class FormatValidator {

    // RFC 3339 date-time pattern
    private static final Pattern DATE_TIME = Pattern.compile(
        "^(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})(?:\\.\\d+)?(?:Z|[+-](\\d{2}):?(\\d{2})|)?$"
    );

    // RFC 3339 full-date pattern
    private static final Pattern DATE = Pattern.compile(
        "^(\\d{4})-(\\d{2})-(\\d{2})$"
    );

    // RFC 3339 full-time pattern
    private static final Pattern TIME = Pattern.compile(
        "^(\\d{2}):(\\d{2}):(\\d{2})(?:\\.\\d+)?(?:Z|[+-](\\d{2}):?(\\d{2})|)?$"
    );

    // RFC 5322 email (simplified)
    private static final Pattern EMAIL = Pattern.compile(
        "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"
    );

    // RFC 1123 hostname
    private static final Pattern HOSTNAME = Pattern.compile(
        "^(?=.{1,253}$)(?!-)[A-Za-z0-9-]{1,63}(?<!-)(\\.[A-Za-z0-9-]{1,63})*$"
    );

    // IPv4 address
    private static final Pattern IPV4 = Pattern.compile(
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );

    // IPv6 address (simplified)
    private static final Pattern IPV6 = Pattern.compile(
        "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$|^::([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4}$|^([0-9a-fA-F]{1,4}:){1,7}:$|^([0-9a-fA-F]{1,4}:){1}(:[0-9a-fA-F]{1,4}){1,6}$|^([0-9a-fA-F]{1,4}:){2}(:[0-9a-fA-F]{1,4}){1,5}$|^([0-9a-fA-F]{1,4}:){3}(:[0-9a-fA-F]{1,4}){1,4}$|^([0-9a-fA-F]{1,4}:){4}(:[0-9a-fA-F]{1,4}){1,3}$|^([0-9a-fA-F]{1,4}:){5}(:[0-9a-fA-F]{1,4}){1,2}$|^([0-9a-fA-F]{1,4}:){6}:[0-9a-fA-F]{1,4}$"
    );

    // URI (RFC 3986)
    private static final Pattern URI = Pattern.compile(
        "^[a-zA-Z][a-zA-Z0-9+.-]*://[^\\s/$.?#].[^s]*$"
    );

    // UUID
    private static final Pattern UUID = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    // Regex (ECMA 262)
    private static final Pattern REGEX = Pattern.compile(
        "^(?:.*/)?([^/]+)$"
    );

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
            case "email" -> EMAIL.matcher(value).matches();
            case "idn-email" -> EMAIL.matcher(value).matches(); // Simplified
            case "hostname" -> HOSTNAME.matcher(value).matches();
            case "idn-hostname" -> HOSTNAME.matcher(value).matches(); // Simplified
            case "ipv4" -> IPV4.matcher(value).matches();
            case "ipv6" -> IPV6.matcher(value).matches();
            case "uri" -> URI.matcher(value).matches();
            case "uri-reference" -> URI.matcher(value).matches(); // Simplified
            case "uuid" -> UUID.matcher(value).matches();
            case "regex" -> isValidRegex(value);
            default -> true; // Unknown format - skip validation
        };
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
     * Gets the default error message for a format validation failure.
     * 
     * @param format the format name
     * @return error message
     */
    public static String getErrorMessage(String format) {
        return "Value does not match format '" + format + "'";
    }
}
