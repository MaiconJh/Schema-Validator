package com.yourname.schemacr.validation;

import com.yourname.schemacr.schemes.Schema;
import com.yourname.schemacr.schemes.SchemaType;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates primitive nodes (string/number/boolean/null/any).
 */
public class PrimitiveValidator implements Validator {

    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();
        SchemaType type = schema.getType();

        if (type == SchemaType.ANY) {
            return errors;
        }

        boolean valid = switch (type) {
            case STRING -> data instanceof String;
            case NUMBER -> data instanceof Number;
            case BOOLEAN -> data instanceof Boolean;
            case NULL -> data == null;
            default -> true;
        };

        if (!valid) {
            errors.add(new ValidationError(
                    path,
                    type.name().toLowerCase(),
                    typeName(data),
                    "Primitive value does not match schema type."
            ));
        }

        return errors;
    }

    private String typeName(Object data) {
        return data == null ? "null" : data.getClass().getSimpleName();
    }
}
