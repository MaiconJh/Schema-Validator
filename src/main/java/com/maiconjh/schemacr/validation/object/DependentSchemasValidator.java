package com.maiconjh.schemacr.validation.object;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.Validator;
import com.maiconjh.schemacr.validation.ValidatorDispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validator for the dependentSchemas JSON Schema keyword.
 * Applies a schema validation when a dependent property is present.
 */
public class DependentSchemasValidator implements Validator {
    
    private final Map<String, Schema> dependentSchemas;
    
    public DependentSchemasValidator(Map<String, Schema> dependentSchemas) {
        this.dependentSchemas = dependentSchemas;
    }
    
    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();
        if (data instanceof Map<?, ?> map) {
            for (Map.Entry<String, Schema> entry : dependentSchemas.entrySet()) {
                String property = entry.getKey();
                if (map.containsKey(property)) {
                    Schema depSchema = entry.getValue();
                    Validator validator = ValidatorDispatcher.forSchema(depSchema);
                    errors.addAll(validator.validate(data, depSchema, path, parentKey));
                }
            }
        }
        return errors;
    }
}
