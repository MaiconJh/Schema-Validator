package com.maiconjh.schemacr.validation;

import java.util.Collections;
import java.util.List;

/**
 * Wrapper for validator outputs.
 */
public class ValidationResult {

    private final boolean success;
    private final List<ValidationError> errors;

    private ValidationResult(boolean success, List<ValidationError> errors) {
        this.success = success;
        this.errors = errors == null ? List.of() : Collections.unmodifiableList(errors);
    }

    public static ValidationResult from(List<ValidationError> errors) {
        boolean success = errors == null || errors.isEmpty();
        return new ValidationResult(success, errors != null ? errors : List.of());
    }

    public boolean isSuccess() {
        return success;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
