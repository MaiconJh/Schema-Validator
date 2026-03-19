package com.maiconjh.schemacr.validation;

import java.util.regex.Pattern;

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
