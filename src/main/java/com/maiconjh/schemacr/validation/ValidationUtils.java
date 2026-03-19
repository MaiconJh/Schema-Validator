package com.maiconjh.schemacr.validation;

/**
 * Utility methods for validation.
 */
public final class ValidationUtils {

    private ValidationUtils() {
    }

    /**
     * Gets a human-readable type name for debugging/error messages.
     *
     * @param data the object to get type name from
     * @return simple class name or "null"
     */
    public static String typeName(Object data) {
        return data == null ? "null" : data.getClass().getSimpleName();
    }
}
