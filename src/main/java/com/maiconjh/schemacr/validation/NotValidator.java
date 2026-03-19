package com.maiconjh.schemacr.validation;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaRefResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for not JSON Schema keyword.
 * 
 * <p>Validates that the instance MUST NOT be valid against the provided schema.</p>
 * 
 * <p>JSON Schema 2019-09 Specification:</p>
 * <ul>
 *     <li>An instance is valid against a not keyword if it fails to validate 
 *     successfully against the schema defined by the value of not</li>
 * </ul>
 */
public class NotValidator implements Validator {

    private SchemaRefResolver refResolver;

    public NotValidator() {
    }

    public NotValidator(SchemaRefResolver refResolver) {
        this.refResolver = refResolver;
    }

    /**
     * Sets the schema reference resolver.
     */
    public void setRefResolver(SchemaRefResolver refResolver) {
        this.refResolver = refResolver;
    }

    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();

        if (!schema.hasNot()) {
            return errors;
        }

        Schema notSchema = schema.getNot();
        
        // Handle schema references
        if (notSchema.isRef() && refResolver != null) {
            Schema resolvedSchema = refResolver.resolveRef(notSchema.getRef(), notSchema.getName());
            if (resolvedSchema != null) {
                notSchema = resolvedSchema;
            }
        }

        Validator validator = ValidatorDispatcher.forSchema(notSchema);
        List<ValidationError> schemaErrors = validator.validate(data, notSchema, path, parentKey);

        // The instance must NOT validate against the not schema
        // If there are no errors, it means the instance matched the not schema - this is an error
        if (schemaErrors.isEmpty()) {
            errors.add(new ValidationError(
                path,
                "not",
                "matched",
                "Data must NOT match the schema defined in 'not'"
            ));
        }
        // If there are errors, the instance correctly did NOT match the not schema - this is valid

        return errors;
    }
}
