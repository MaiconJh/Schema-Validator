package com.maiconjh.schemacr.validation.misc;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Validator for the const JSON Schema keyword.
 * Ensures that a value equals exactly the specified constant value.
 */
public class ConstValidator implements Validator {
    
    private final Object constValue;
    
    public ConstValidator(Object constValue) {
        this.constValue = constValue;
    }
    
    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();
        if (!Objects.equals(data, constValue)) {
            errors.add(new ValidationError(path, "const", String.valueOf(data),
                    "Value must be exactly: " + constValue));
        }
        return errors;
    }
}
