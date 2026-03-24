package com.maiconjh.schemacr.validation.object;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validator for the maxProperties JSON Schema keyword.
 * Ensures that an object contains at most a specified number of properties.
 */
public class MaxPropertiesValidator implements Validator {
    
    private final int maxProperties;
    
    public MaxPropertiesValidator(int maxProperties) {
        this.maxProperties = maxProperties;
    }
    
    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();
        // Only validate if data is a Map
        if (!(data instanceof Map<?, ?>)) {
            return errors;
        }
        Map<?, ?> map = (Map<?, ?>) data;
        if (map.size() > maxProperties) {
            errors.add(new ValidationError(path, "maxProperties", String.valueOf(map.size()),
                    "Object must have at most " + maxProperties + " properties, but found " + map.size()));
        }
        return errors;
    }
}
