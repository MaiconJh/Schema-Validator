package com.maiconjh.schemacr.validation.array;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for the maxItems JSON Schema keyword.
 * Ensures that an array contains at most a specified number of elements.
 */
public class MaxItemsValidator implements Validator {
    
    private final int maxItems;
    
    public MaxItemsValidator(int maxItems) {
        this.maxItems = maxItems;
    }
    
    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();
        if (data instanceof List<?> list && list.size() > maxItems) {
            errors.add(new ValidationError(path, "maxItems", String.valueOf(list.size()),
                    "Array must have at most " + maxItems + " items"));
        }
        return errors;
    }
}