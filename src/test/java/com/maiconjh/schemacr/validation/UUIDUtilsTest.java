package com.maiconjh.schemacr.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for UUIDUtils.
 * 
 * <p>Tests UUID validation, conversion, and extraction operations
 * for various UUID formats (standard, no-hyphen, version-specific).</p>
 */
@DisplayName("UUIDUtils Tests")
class UUIDUtilsTest {

    // ========== UUID GENERIC VALIDATION TESTS ==========

    @Test
    @DisplayName("shouldReturnTrue_whenValidUuidWithHyphens")
    void shouldReturnTrue_whenValidUuidWithHyphens() {
        // Arrange
        String validUuid = "550e8400-e29b-41d4-a716-446655440000";
        
        // Act
        boolean isValid = UUIDUtils.isValidUuid(validUuid);
        
        // Assert
        assertTrue(isValid, "Expected valid UUID with hyphens to pass validation");
    }

    @Test
    @DisplayName("shouldReturnFalse_whenInvalidUuidTooShort")
    void shouldReturnFalse_whenInvalidUuidTooShort() {
        // Arrange
        String shortUuid = "550e8400-e29b-41d4-a716-44665544000";
        
        // Act
        boolean isValid = UUIDUtils.isValidUuid(shortUuid);
        
        // Assert
        assertFalse(isValid, "Expected UUID too short to fail validation");
    }

    @Test
    @DisplayName("shouldReturnFalse_whenInvalidUuidTooLong")
    void shouldReturnFalse_whenInvalidUuidTooLong() {
        // Arrange
        String longUuid = "550e8400-e29b-41d4-a716-44665544000000";
        
        // Act
        boolean isValid = UUIDUtils.isValidUuid(longUuid);
        
        // Assert
        assertFalse(isValid, "Expected UUID too long to fail validation");
    }

    @Test
    @DisplayName("shouldReturnFalse_whenInvalidUuidWithInvalidChars")
    void shouldReturnFalse_whenInvalidUuidWithInvalidChars() {
        // Arrange
        String invalidUuid = "550e8400-e29b-41d4-a716-44665544000g";
        
        // Act
        boolean isValid = UUIDUtils.isValidUuid(invalidUuid);
        
        // Assert
        assertFalse(isValid, "Expected UUID with invalid chars to fail validation");
    }

    // ========== UUID v1 VALIDATION TESTS ==========

    @Test
    @DisplayName("shouldReturnTrue_whenValidUuidV1")
    void shouldReturnTrue_whenValidUuidV1() {
        // Arrange
        String validUuidV1 = "6ba7b810-9dad-11d1-80b4-00c04fd430c8";
        
        // Act
        boolean isValid = UUIDUtils.isValidUuidV1(validUuidV1);
        
        // Assert
        assertTrue(isValid, "Expected valid UUID v1 to pass validation");
    }

    @Test
    @DisplayName("shouldReturnFalse_whenUuidV4UsedAsV1")
    void shouldReturnFalse_whenUuidV4UsedAsV1() {
        // Arrange
        String uuidV4 = "550e8400-e29b-41d4-a716-446655440000";
        
        // Act
        boolean isValid = UUIDUtils.isValidUuidV1(uuidV4);
        
        // Assert
        assertFalse(isValid, "Expected UUID v4 used as v1 to fail validation");
    }

    // ========== UUID v4 VALIDATION TESTS ==========

    @Test
    @DisplayName("shouldReturnTrue_whenValidUuidV4")
    void shouldReturnTrue_whenValidUuidV4() {
        // Arrange
        String validUuidV4 = "550e8400-e29b-41d4-a716-446655440000";
        
        // Act
        boolean isValid = UUIDUtils.isValidUuidV4(validUuidV4);
        
        // Assert
        assertTrue(isValid, "Expected valid UUID v4 to pass validation");
    }

    @Test
    @DisplayName("shouldReturnFalse_whenUuidV1UsedAsV4")
    void shouldReturnFalse_whenUuidV1UsedAsV4() {
        // Arrange
        String uuidV1 = "6ba7b810-9dad-11d1-80b4-00c04fd430c8";
        
        // Act
        boolean isValid = UUIDUtils.isValidUuidV4(uuidV1);
        
        // Assert
        assertFalse(isValid, "Expected UUID v1 used as v4 to fail validation");
    }

    // ========== UUID v5 VALIDATION TESTS ==========

    @Test
    @DisplayName("shouldReturnTrue_whenValidUuidV5")
    void shouldReturnTrue_whenValidUuidV5() {
        // Arrange
        String validUuidV5 = "2d3b2f50-0bdb-5c48-8097-4c5d8c8c8c8c";
        
        // Act
        boolean isValid = UUIDUtils.isValidUuidV5(validUuidV5);
        
        // Assert
        assertTrue(isValid, "Expected valid UUID v5 to pass validation");
    }

    @Test
    @DisplayName("shouldReturnFalse_whenUuidV4UsedAsV5")
    void shouldReturnFalse_whenUuidV4UsedAsV5() {
        // Arrange
        String uuidV4 = "550e8400-e29b-41d4-a716-446655440000";
        
        // Act
        boolean isValid = UUIDUtils.isValidUuidV5(uuidV4);
        
        // Assert
        assertFalse(isValid, "Expected UUID v4 used as v5 to fail validation");
    }

    // ========== UUID WITHOUT HYPHENS VALIDATION TESTS ==========

    @Test
    @DisplayName("shouldReturnTrue_whenValidUuidNoHyphen")
    void shouldReturnTrue_whenValidUuidNoHyphen() {
        // Arrange
        String validUuidNoHyphen = "550e8400e29b41d4a716446655440000";
        
        // Act
        boolean isValid = UUIDUtils.isValidUuidNoHyphen(validUuidNoHyphen);
        
        // Assert
        assertTrue(isValid, "Expected valid UUID without hyphens to pass validation");
    }

    @Test
    @DisplayName("shouldReturnFalse_whenInvalidUuidNoHyphen")
    void shouldReturnFalse_whenInvalidUuidNoHyphen() {
        // Arrange
        String invalidUuidNoHyphen = "550e8400e29b41d4a7164466554400"; // menos de 32 chars
        
        // Act
        boolean isValid = UUIDUtils.isValidUuidNoHyphen(invalidUuidNoHyphen);
        
        // Assert
        assertFalse(isValid, "Expected UUID without hyphens with less than 32 chars to fail validation");
    }

    // ========== CONVERSION TESTS ==========

    @Test
    @DisplayName("shouldRemoveHyphens_whenConvertingToNoHyphen")
    void shouldRemoveHyphens_whenConvertingToNoHyphen() {
        // Arrange
        String uuidWithHyphens = "550e8400-e29b-41d4-a716-446655440000";
        String expected = "550e8400e29b41d4a716446655440000";
        
        // Act
        String result = UUIDUtils.toNoHyphen(uuidWithHyphens);
        
        // Assert
        assertEquals(expected, result, "Expected hyphens to be removed");
    }

    @Test
    @DisplayName("shouldAddHyphens_whenConvertingFromNoHyphen")
    void shouldAddHyphens_whenConvertingFromNoHyphen() {
        // Arrange
        String uuidNoHyphen = "550e8400e29b41d4a716446655440000";
        String expected = "550e8400-e29b-41d4-a716-446655440000";
        
        // Act
        String result = UUIDUtils.addHyphens(uuidNoHyphen);
        
        // Assert
        assertEquals(expected, result, "Expected hyphens to be added in correct positions");
    }

    @Test
    @DisplayName("shouldConvertToUpperCase")
    void shouldConvertToUpperCase() {
        // Arrange
        String uuidLower = "550e8400-e29b-41d4-a716-446655440000";
        String expected = "550E8400-E29B-41D4-A716-446655440000";
        
        // Act
        String result = UUIDUtils.toUpperCase(uuidLower);
        
        // Assert
        assertEquals(expected, result, "Expected UUID to be converted to uppercase");
    }

    @Test
    @DisplayName("shouldConvertToLowerCase")
    void shouldConvertToLowerCase() {
        // Arrange
        String uuidUpper = "550E8400-E29B-41D4-A716-446655440000";
        String expected = "550e8400-e29b-41d4-a716-446655440000";
        
        // Act
        String result = UUIDUtils.toLowerCase(uuidUpper);
        
        // Assert
        assertEquals(expected, result, "Expected UUID to be converted to lowercase");
    }

    // ========== EXTRACTION TESTS ==========

    @Test
    @DisplayName("shouldExtractVersion_fromUuidV1")
    void shouldExtractVersion_fromUuidV1() {
        // Arrange
        String uuidV1 = "6ba7b810-9dad-11d1-80b4-00c04fd430c8";
        int expectedVersion = 1;
        
        // Act
        int version = UUIDUtils.getVersion(uuidV1);
        
        // Assert
        assertEquals(expectedVersion, version, "Expected version 1 to be extracted from UUID v1");
    }

    @Test
    @DisplayName("shouldExtractVersion_fromUuidV4")
    void shouldExtractVersion_fromUuidV4() {
        // Arrange
        String uuidV4 = "550e8400-e29b-41d4-a716-446655440000";
        int expectedVersion = 4;
        
        // Act
        int version = UUIDUtils.getVersion(uuidV4);
        
        // Assert
        assertEquals(expectedVersion, version, "Expected version 4 to be extracted from UUID v4");
    }

    @Test
    @DisplayName("shouldExtractVersion_fromUuidV5")
    void shouldExtractVersion_fromUuidV5() {
        // Arrange
        String uuidV5 = "2d3b2f50-0bdb-5c48-8097-4c5d8c8c8c8c";
        int expectedVersion = 5;
        
        // Act
        int version = UUIDUtils.getVersion(uuidV5);
        
        // Assert
        assertEquals(expectedVersion, version, "Expected version 5 to be extracted from UUID v5");
    }

    @Test
    @DisplayName("shouldExtractVariant")
    void shouldExtractVariant() {
        // Arrange
        String uuid = "550e8400-e29b-41d4-a716-446655440000";
        int expectedVariant = 1; // RFC 4122 variant (8, 9, a, b)
        
        // Act
        int variant = UUIDUtils.getVariant(uuid);
        
        // Assert
        assertEquals(expectedVariant, variant, "Expected RFC 4122 variant to be extracted");
    }
}
