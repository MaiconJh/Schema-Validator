package com.maiconjh.schemacr.validation.array;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.Validator;
import com.maiconjh.schemacr.validation.ValidatorDispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for the prefixItems keyword (Draft 2020-12).
 * Validates array items against schemas at specific positions.
 */
public class PrefixItemsValidator implements Validator {
    
    private final List<Schema> prefixSchemas;
    
    public PrefixItemsValidator(List<Schema> prefixSchemas) {
        this.prefixSchemas = prefixSchemas;
    }
    
    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();
        if (data instanceof List<?> list) {
            for (int i = 0; i < Math.min(list.size(), prefixSchemas.size()); i++) {
                Object item = list.get(i);
                Schema itemSchema = prefixSchemas.get(i);
                Validator validator = ValidatorDispatcher.forSchema(itemSchema);
                errors.addAll(validator.validate(item, itemSchema, path + "[" + i + "]", parentKey));
            }
        }
        return errors;
    }
}