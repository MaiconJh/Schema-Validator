package com.maiconjh.schemacr.validation.object;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validator for the minProperties JSON Schema keyword.
 * Ensures that an object contains a minimum number of properties.
 */
public class MinPropertiesValidator implements Validator {
    
    private final int minProperties;
    
    public MinPropertiesValidator(int minProperties) {
        this.minProperties = minProperties;
    }
    
    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();
        if (data instanceof Map<?, ?> map && map.size() < minProperties) {
            errors.add(new ValidationError(path, "minProperties", String.valueOf(map.size()),
                    "Object must have at least " + minProperties + " properties"));
        }
        return errors;
    }
}
