package com.maiconjh.schemacr.core;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.validation.ObjectValidator;
import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.ValidationResult;
import com.maiconjh.schemacr.validation.Validator;

import java.util.List;

/**
 * Facade for validating arbitrary data against a registered schema.
 *
 * <p>Swap the root validator to a composed strategy if you need richer rule dispatching.</p>
 */
public class ValidationService {

    private final Validator rootValidator;

    public ValidationService() {
        this.rootValidator = new ObjectValidator();
    }

    public ValidationResult validate(Object data, Schema schema) {
        List<ValidationError> errors = rootValidator.validate(data, schema, "$", null);
        return ValidationResult.from(errors);
    }
}
