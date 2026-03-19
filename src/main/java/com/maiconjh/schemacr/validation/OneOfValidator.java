package com.maiconjh.schemacr.validation;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaRefResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for oneOf JSON Schema keyword.
 * 
 * <p>Validates that exactly one of the subschemas MUST be valid.</p>
 * 
 * <p>JSON Schema 2019-09 Specification:</p>
 * <ul>
 *     <li>An instance validates successfully against oneOf if it validates 
 *     successfully against exactly one of the schemas in oneOf</li>
 * </ul>
 */
public class OneOfValidator implements Validator {

    private SchemaRefResolver refResolver;

    public OneOfValidator() {
    }

    public OneOfValidator(SchemaRefResolver refResolver) {
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

        if (!schema.hasOneOf()) {
            return errors;
        }

        List<Schema> oneOfSchemas = schema.getOneOf();
        int validCount = 0;
        int firstValidIndex = -1;
        List<ValidationError> allErrors = new ArrayList<>();

        for (int i = 0; i < oneOfSchemas.size(); i++) {
            Schema oneOfSchema = oneOfSchemas.get(i);
            
            // Handle schema references
            if (oneOfSchema.isRef() && refResolver != null) {
                Schema resolvedSchema = refResolver.resolveRef(oneOfSchema.getRef(), oneOfSchema.getName());
                if (resolvedSchema != null) {
                    oneOfSchema = resolvedSchema;
                }
            }

            Validator validator = ValidatorDispatcher.forSchema(oneOfSchema);
            List<ValidationError> schemaErrors = validator.validate(data, oneOfSchema, path, parentKey);

            if (schemaErrors.isEmpty()) {
                validCount++;
                if (firstValidIndex == -1) {
                    firstValidIndex = i;
                }
            } else {
                // Collect errors for reporting
                for (ValidationError error : schemaErrors) {
                    allErrors.add(new ValidationError(
                        error.getNodePath(),
                        "oneOf[" + i + "]." + error.getExpectedType(),
                        error.getActualType(),
                        "oneOf[" + i + "]: " + error.getDescription()
                    ));
                }
            }
        }

        // Exactly one schema must match
        if (validCount == 0) {
            // No schema matched - add all collected errors
            errors.addAll(allErrors);
            errors.add(new ValidationError(
                path,
                "oneOf",
                "no match",
                "Data must match exactly one schema in oneOf (matched 0 of " + oneOfSchemas.size() + ")"
            ));
        } else if (validCount > 1) {
            // More than one schema matched - this is an error
            errors.add(new ValidationError(
                path,
                "oneOf",
                "multiple matches",
                "Data must match exactly one schema in oneOf (matched " + validCount + " of " + oneOfSchemas.size() + ")"
            ));
        }

        return errors;
    }
}
