package com.yourname.schemacr.validation;

import com.yourname.schemacr.schemes.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validates object/map nodes.
 */
public class ObjectValidator implements Validator {

    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();

        if (!(data instanceof Map<?, ?> map)) {
            errors.add(new ValidationError(path, "object", typeName(data), "Expected an object/map node."));
            return errors;
        }

        // Validate declared schema properties.
        for (Map.Entry<String, Schema> property : schema.getProperties().entrySet()) {
            String key = property.getKey();
            Schema propertySchema = property.getValue();
            Object child = map.get(key);
            String childPath = path + "." + key;

            Validator validator = ValidatorDispatcher.forSchema(propertySchema);
            errors.addAll(validator.validate(child, propertySchema, childPath, key));
        }

        // TODO: Add support for required/optional fields and unknown field policy.
        return errors;
    }

    private String typeName(Object data) {
        return data == null ? "null" : data.getClass().getSimpleName();
    }
}
