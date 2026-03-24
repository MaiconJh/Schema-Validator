package com.maiconjh.schemacr.validation.misc;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for the readOnly JSON Schema keyword metadata.
 * Validates that readOnly properties are not sent by the client.
 * Note: This is typically used in API request validation scenarios.
 */
public class ReadOnlyValidator implements Validator {
    
    private final boolean readOnly;
    
    public ReadOnlyValidator(boolean readOnly) {
        this.readOnly = readOnly;
    }
    
    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();
        // readOnly validation typically applies to input (write) operations
        // If readOnly is true and data is provided, it's an error
        // This is a metadata validation - actual enforcement depends on use case
        if (readOnly && data != null) {
            // This is informational - actual readOnly enforcement depends on context
            // The schema validator can flag this as a warning or error
            errors.add(new ValidationError(path, "readOnly", String.valueOf(data),
                    "Property is read-only and should not be provided"));
        }
        return errors;
    }
}
