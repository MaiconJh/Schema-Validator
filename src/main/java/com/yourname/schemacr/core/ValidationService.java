package com.yourname.schemacr.core;

import com.yourname.schemacr.schemes.Schema;
import com.yourname.schemacr.validation.ObjectValidator;
import com.yourname.schemacr.validation.ValidationError;
import com.yourname.schemacr.validation.ValidationResult;
import com.yourname.schemacr.validation.Validator;

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
