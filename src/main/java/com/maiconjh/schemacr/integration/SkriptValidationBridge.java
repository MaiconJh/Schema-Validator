package com.maiconjh.schemacr.integration;

import com.maiconjh.schemacr.validation.ValidationResult;

/**
 * Shared bridge to expose the most recent validation result to Skript expressions.
 */
public final class SkriptValidationBridge {

    private static volatile ValidationResult lastResult;

    private SkriptValidationBridge() {
    }

    public static void setLastResult(ValidationResult result) {
        lastResult = result;
    }

    public static ValidationResult getLastResult() {
        return lastResult;
    }
}
