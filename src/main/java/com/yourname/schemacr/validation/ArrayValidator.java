package com.yourname.schemacr.validation;

import com.yourname.schemacr.schemes.Schema;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates list/array nodes.
 */
public class ArrayValidator implements Validator {

    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();

        if (!(data instanceof List<?> list)) {
            errors.add(new ValidationError(path, "array", typeName(data), "Expected a list/array node."));
            return errors;
        }

        if (schema.getItemSchema() == null) {
            // Array schema with no 'items' means no deep validation for now.
            return errors;
        }

        for (int i = 0; i < list.size(); i++) {
            Object element = list.get(i);
            String childPath = path + "[" + i + "]";
            errors.addAll(ValidatorDispatcher.forSchema(schema.getItemSchema())
                    .validate(element, schema.getItemSchema(), childPath, parentKey));
        }

        return errors;
    }

    private String typeName(Object data) {
        return data == null ? "null" : data.getClass().getSimpleName();
    }
}
