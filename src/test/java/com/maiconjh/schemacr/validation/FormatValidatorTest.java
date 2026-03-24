package com.maiconjh.schemacr.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaType;

/**
 * Unit tests for FormatValidator.
 * 
 * <p>Tests the format validation logic including email, URI, date-time,
 * IPv4, IPv6, hostname formats and edge cases.</p>
 */
@DisplayName("FormatValidator Tests")
class FormatValidatorTest {

    private PrimitiveValidator validator;
    private Schema schema;

    @BeforeEach
    void setUp() {
        validator = new PrimitiveValidator();
    }

    // ========== POSITIVE TESTS (Valid formats) ==========

    @Test
    @DisplayName("shouldPass_whenEmailFormatValid")
    void shouldPass_whenEmailFormatValid() {
        // Arrange
        // Schema: type string with format "email"
        // Valid: email format is valid
        
        schema = Schema.builder("emailField", SchemaType.STRING)
                .format("email")
                .build();
        
        String validEmail = "test@example.com";

        // Act
        // Using FormatValidator directly for format validation
        boolean isValid = FormatValidator.isValid("email", validEmail);

        // Assert
        assertTrue(isValid, "Expected valid email format to pass validation");
    }

    @Test
    @DisplayName("shouldPass_whenUriFormatValid")
    void shouldPass_whenUriFormatValid() {
        // Arrange
        // Schema: type string with format "uri"
        // Valid: URI format is valid
        
        schema = Schema.builder("uriField", SchemaType.STRING)
                .format("uri")
                .build();
        
        String validUri = "https://example.com/path";

        // Act
        boolean isValid = FormatValidator.isValid("uri", validUri);

        // Assert
        assertTrue(isValid, "Expected valid URI format to pass validation");
    }

    @Test
    @DisplayName("shouldPass_whenDateTimeFormatValid")
    void shouldPass_whenDateTimeFormatValid() {
        // Arrange
        // Schema: type string with format "date-time"
        // Valid: date-time format is valid
        
        schema = Schema.builder("datetimeField", SchemaType.STRING)
                .format("date-time")
                .build();
        
        String validDateTime = "2024-01-15T10:30:00Z";

        // Act
        boolean isValid = FormatValidator.isValid("date-time", validDateTime);

        // Assert
        assertTrue(isValid, "Expected valid date-time format to pass validation");
    }

    @Test
    @DisplayName("shouldPass_whenIpv4FormatValid")
    void shouldPass_whenIpv4FormatValid() {
        // Arrange
        // Schema: type string with format "ipv4"
        // Valid: IPv4 format is valid
        
        schema = Schema.builder("ipv4Field", SchemaType.STRING)
                .format("ipv4")
                .build();
        
        String validIpv4 = "192.168.1.1";

        // Act
        boolean isValid = FormatValidator.isValid("ipv4", validIpv4);

        // Assert
        assertTrue(isValid, "Expected valid IPv4 format to pass validation");
    }

    @Test
    @DisplayName("shouldPass_whenIpv6FormatValid")
    void shouldPass_whenIpv6FormatValid() {
        // Arrange
        // Schema: type string with format "ipv6"
        // Valid: IPv6 format is valid
        
        schema = Schema.builder("ipv6Field", SchemaType.STRING)
                .format("ipv6")
                .build();
        
        String validIpv6 = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";

        // Act
        boolean isValid = FormatValidator.isValid("ipv6", validIpv6);

        // Assert
        assertTrue(isValid, "Expected valid IPv6 format to pass validation");
    }

    @Test
    @DisplayName("shouldPass_whenHostnameFormatValid")
    void shouldPass_whenHostnameFormatValid() {
        // Arrange
        // Schema: type string with format "hostname"
        // Valid: hostname format is valid
        
        schema = Schema.builder("hostnameField", SchemaType.STRING)
                .format("hostname")
                .build();
        
        String validHostname = "example.com";

        // Act
        boolean isValid = FormatValidator.isValid("hostname", validHostname);

        // Assert
        assertTrue(isValid, "Expected valid hostname format to pass validation");
    }

    // ========== NEGATIVE TESTS (Invalid formats) ==========

    @Test
    @DisplayName("shouldFail_whenEmailFormatInvalid")
    void shouldFail_whenEmailFormatInvalid() {
        // Arrange
        // Schema: type string with format "email"
        // Invalid: email format is not valid
        
        schema = Schema.builder("emailField", SchemaType.STRING)
                .format("email")
                .build();
        
        String invalidEmail = "not-an-email";

        // Act
        boolean isValid = FormatValidator.isValid("email", invalidEmail);

        // Assert
        assertFalse(isValid, "Expected invalid email format to fail validation");
    }

    @Test
    @DisplayName("shouldFail_whenUriFormatInvalid")
    void shouldFail_whenUriFormatInvalid() {
        // Arrange
        // Schema: type string with format "uri"
        // Invalid: URI format is not valid
        
        schema = Schema.builder("uriField", SchemaType.STRING)
                .format("uri")
                .build();
        
        String invalidUri = "not-a-uri";

        // Act
        boolean isValid = FormatValidator.isValid("uri", invalidUri);

        // Assert
        assertFalse(isValid, "Expected invalid URI format to fail validation");
    }

    @Test
    @DisplayName("shouldFail_whenDateTimeFormatInvalid")
    void shouldFail_whenDateTimeFormatInvalid() {
        // Arrange
        // Schema: type string with format "date-time"
        // Invalid: date-time format is not valid
        
        schema = Schema.builder("datetimeField", SchemaType.STRING)
                .format("date-time")
                .build();
        
        String invalidDateTime = "not-a-date";

        // Act
        boolean isValid = FormatValidator.isValid("date-time", invalidDateTime);

        // Assert
        assertFalse(isValid, "Expected invalid date-time format to fail validation");
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    @DisplayName("shouldPass_whenFormatNotSet")
    void shouldPass_whenFormatNotSet() {
        // Arrange
        // Schema: type string with no format set
        // Valid: no format constraint should pass
        
        schema = Schema.builder("stringField", SchemaType.STRING)
                .build();
        
        String value = "any string value";

        // Act
        // Using Schema directly to test through PrimitiveValidator
        List<ValidationError> errors = validator.validate(value, schema, "/stringField", "stringField");

        // Assert
        assertTrue(errors.isEmpty(), "Expected no errors when format is not set");
    }

    @Test
    @DisplayName("shouldHandleNullData")
    void shouldHandleNullData() {
        // Arrange
        // Schema: type string with format "email"
        // Edge: data is null
        
        schema = Schema.builder("emailField", SchemaType.STRING)
                .format("email")
                .build();
        
        Object data = null;

        // Act
        // When data is null, format validation is skipped (returns true)
        boolean isValid = FormatValidator.isValid("email", null);

        // Assert
        // FormatValidator returns true for null value (skips validation)
        assertTrue(isValid, "Expected format validation to skip for null data");
    }

    @Test
    @DisplayName("shouldHandleUnsupportedFormat")
    void shouldHandleUnsupportedFormat() {
        // Arrange
        // Schema: type string with unsupported format
        // Edge: unknown format should be skipped
        
        schema = Schema.builder("customField", SchemaType.STRING)
                .format("unsupported-format")
                .build();
        
        String value = "any value";

        // Act
        // FormatValidator returns true for unknown formats (skips validation)
        boolean isValid = FormatValidator.isValid("unsupported-format", value);

        // Assert
        assertTrue(isValid, "Expected unsupported format to be skipped (returns true)");
    }

    // ========== ADDITIONAL FORMAT TESTS ==========

    @Test
    @DisplayName("shouldPass_whenMultipleEmailFormatsValid")
    void shouldPass_whenMultipleEmailFormatsValid() {
        // Arrange & Act & Assert
        assertTrue(FormatValidator.isValid("email", "user@domain.com"), "user@domain.com should be valid");
        assertTrue(FormatValidator.isValid("email", "user.name@domain.com"), "user.name@domain.com should be valid");
        assertTrue(FormatValidator.isValid("email", "user+tag@domain.co.uk"), "user+tag@domain.co.uk should be valid");
    }

    @Test
    @DisplayName("shouldPass_whenMultipleUriFormatsValid")
    void shouldPass_whenMultipleUriFormatsValid() {
        // Arrange & Act & Assert
        assertTrue(FormatValidator.isValid("uri", "http://example.com"), "http should be valid");
        assertTrue(FormatValidator.isValid("uri", "https://example.com/path"), "https with path should be valid");
        assertTrue(FormatValidator.isValid("uri", "ftp://files.example.org"), "ftp should be valid");
    }

    @Test
    @DisplayName("shouldPass_whenMultipleDateTimeFormatsValid")
    void shouldPass_whenMultipleDateTimeFormatsValid() {
        // Arrange & Act & Assert
        assertTrue(FormatValidator.isValid("date-time", "2024-01-15T10:30:00Z"), "UTC format should be valid");
        assertTrue(FormatValidator.isValid("date-time", "2024-01-15T10:30:00+05:30"), "with timezone offset should be valid");
        assertTrue(FormatValidator.isValid("date-time", "2024-01-15T10:30:00.123Z"), "with milliseconds should be valid");
    }

    @Test
    @DisplayName("shouldPass_whenCompressedIpv6Valid")
    void shouldPass_whenCompressedIpv6Valid() {
        // Arrange & Act & Assert
        assertTrue(FormatValidator.isValid("ipv6", "::1"), "loopback should be valid");
        assertTrue(FormatValidator.isValid("ipv6", "2001:db8::1"), "compressed format should be valid");
    }

    @Test
    @DisplayName("shouldPass_whenHostnameWithSubdomainValid")
    void shouldPass_whenHostnameWithSubdomainValid() {
        // Arrange & Act & Assert
        assertTrue(FormatValidator.isValid("hostname", "localhost"), "localhost should be valid");
        assertTrue(FormatValidator.isValid("hostname", "sub.example.com"), "subdomain should be valid");
        assertTrue(FormatValidator.isValid("hostname", "my-server.example.org"), "with hyphen should be valid");
    }

    @Test
    @DisplayName("shouldFail_whenEmailHasNoAtSymbol")
    void shouldFail_whenEmailHasNoAtSymbol() {
        // Arrange & Act & Assert
        assertFalse(FormatValidator.isValid("email", "invalidemail.com"), "missing @ should fail");
        assertFalse(FormatValidator.isValid("email", "user@"), "missing domain should fail");
    }

    @Test
    @DisplayName("shouldFail_whenUriMissingScheme")
    void shouldFail_whenUriMissingScheme() {
        // Arrange & Act & Assert
        assertFalse(FormatValidator.isValid("uri", "example.com"), "missing scheme should fail");
        assertFalse(FormatValidator.isValid("uri", "/path/to/resource"), "relative path should fail");
    }

    @Test
    @DisplayName("shouldFail_whenDateTimeInvalidFormat")
    void shouldFail_whenDateTimeInvalidFormat() {
        // Arrange & Act & Assert
        assertFalse(FormatValidator.isValid("date-time", "2024/01/15 10:30:00"), "wrong separator should fail");
        assertFalse(FormatValidator.isValid("date-time", "01-15-2024T10:30:00"), "US date format should fail");
        assertFalse(FormatValidator.isValid("date-time", "not a date"), "text should fail");
    }

    @Test
    @DisplayName("shouldFail_whenIpv4OutOfRange")
    void shouldFail_whenIpv4OutOfRange() {
        // Arrange & Act & Assert
        assertFalse(FormatValidator.isValid("ipv4", "256.1.1.1"), "octet > 255 should fail");
        assertFalse(FormatValidator.isValid("ipv4", "192.168.1"), "incomplete IP should fail");
        assertFalse(FormatValidator.isValid("ipv4", "192.168.1.1.1"), "too many octets should fail");
    }

    @Test
    @DisplayName("shouldHandleNullFormat")
    void shouldHandleNullFormat() {
        // Arrange & Act & Assert
        // FormatValidator returns true when format is null (skips validation)
        assertTrue(FormatValidator.isValid(null, "any value"), "null format should return true");
    }
}