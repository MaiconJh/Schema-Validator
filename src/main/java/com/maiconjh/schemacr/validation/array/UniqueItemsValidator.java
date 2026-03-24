package com.maiconjh.schemacr.validation.array;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.Validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validator for the uniqueItems JSON Schema keyword.
 * Ensures that all elements in an array are unique.
 */
public class UniqueItemsValidator implements Validator {
    
    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();
        if (data instanceof List<?> list) {
            Set<Object> seen = new HashSet<>();
            for (Object item : list) {
                if (!seen.add(item)) {
                    errors.add(new ValidationError(path, "uniqueItems", "duplicate",
                            "Array items must be unique; duplicate found: " + item));
                    break;
                }
            }
        }
        return errors;
    }
}