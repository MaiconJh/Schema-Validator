package com.maiconjh.schemacr.validation.misc;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for the writeOnly JSON Schema keyword metadata.
 * Validates that writeOnly properties are not present in responses.
 * Note: This is typically used in API response validation scenarios.
 */
public class WriteOnlyValidator implements Validator {
    
    private final boolean writeOnly;
    
    public WriteOnlyValidator(boolean writeOnly) {
        this.writeOnly = writeOnly;
    }
    
    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();
        // writeOnly validation typically applies to output (read) operations
        // If writeOnly is true and data is provided in a response, it's an error
        if (writeOnly && data != null) {
            // This is informational - actual writeOnly enforcement depends on context
            errors.add(new ValidationError(path, "writeOnly", String.valueOf(data),
                    "Property is write-only and should not be returned"));
        }
        return errors;
    }
}
