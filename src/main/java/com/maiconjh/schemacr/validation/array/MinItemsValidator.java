package com.maiconjh.schemacr.validation.array;

import java.util.ArrayList;
import java.util.List;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.Validator;

/**
 * Validator for the minItems JSON Schema keyword.
 * Ensures that an array contains a minimum number of elements.
 */
public class MinItemsValidator implements Validator {
    
    private final int minItems;
    
    public MinItemsValidator(int minItems) {
        this.minItems = minItems;
    }
    
    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();
        if (data instanceof List<?> list && list.size() < minItems) {
            errors.add(new ValidationError(path, "minItems", String.valueOf(list.size()),
                    "Array must have at least " + minItems + " items"));
        }
        return errors;
    }
}