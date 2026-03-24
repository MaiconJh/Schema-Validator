package com.maiconjh.schemacr.validation.array;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.Validator;
import com.maiconjh.schemacr.validation.ValidatorDispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for the additionalItems keyword.
 * Validates items beyond the defined prefixItems schemas.
 */
public class AdditionalItemsValidator implements Validator {
    
    private final Schema additionalSchema;
    private final int prefixCount;
    
    public AdditionalItemsValidator(Schema additionalSchema, int prefixCount) {
        this.additionalSchema = additionalSchema;
        this.prefixCount = prefixCount;
    }
    
    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();
        if (data instanceof List<?> list) {
            for (int i = prefixCount; i < list.size(); i++) {
                Object item = list.get(i);
                Validator validator = ValidatorDispatcher.forSchema(additionalSchema);
                errors.addAll(validator.validate(item, additionalSchema, path + "[" + i + "]", parentKey));
            }
        }
        return errors;
    }
}