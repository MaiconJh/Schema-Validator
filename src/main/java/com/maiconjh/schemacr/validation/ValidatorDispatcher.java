package com.maiconjh.schemacr.validation;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaType;

/**
 * Simple dispatcher returning a validator for the given schema type.
 *
 * <p>Future extension point: inject rule pipelines per type.</p>
 */
public final class ValidatorDispatcher {

    private static final ObjectValidator OBJECT_VALIDATOR = new ObjectValidator();
    private static final ArrayValidator ARRAY_VALIDATOR = new ArrayValidator();
    private static final PrimitiveValidator PRIMITIVE_VALIDATOR = new PrimitiveValidator();

    private ValidatorDispatcher() {
    }

    public static Validator forSchema(Schema schema) {
        SchemaType type = schema.getType();
        return switch (type) {
            case OBJECT -> OBJECT_VALIDATOR;
            case ARRAY -> ARRAY_VALIDATOR;
            default -> PRIMITIVE_VALIDATOR;
        };
    }
}
