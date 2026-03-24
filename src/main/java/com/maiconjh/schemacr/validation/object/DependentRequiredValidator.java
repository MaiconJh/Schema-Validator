package com.maiconjh.schemacr.validation.object;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validator for the dependentRequired JSON Schema keyword.
 * Ensures that required properties are present when a dependent property exists.
 */
public class DependentRequiredValidator implements Validator {
    
    private final Map<String, List<String>> dependentRequired;
    
    public DependentRequiredValidator(Map<String, List<String>> dependentRequired) {
        this.dependentRequired = dependentRequired;
    }
    
    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();
        if (data instanceof Map<?, ?> map) {
            for (Map.Entry<String, List<String>> entry : dependentRequired.entrySet()) {
                String property = entry.getKey();
                if (map.containsKey(property)) {
                    for (String required : entry.getValue()) {
                        if (!map.containsKey(required)) {
                            errors.add(new ValidationError(path, "dependentRequired",
                                    "missing", "Property '" + property + "' requires '" + required + "'"));
                        }
                    }
                }
            }
        }
        return errors;
    }
}
