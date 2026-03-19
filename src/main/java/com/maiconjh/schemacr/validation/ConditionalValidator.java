package com.maiconjh.schemacr.validation;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaRefResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for if/then/else JSON Schema keywords.
 * 
 * <p>Implements conditional validation based on if/then/else keywords.</p>
 * 
 * <p>JSON Schema 2019-09 Specification:</p>
 * <ul>
 *     <li>When if validates successfully, then MUST also validate successfully</li>
 *     <li>When if fails validation, else MUST validate successfully (if present)</li>
 *     <li>If then is not present, the if schema must simply be valid</li>
 *     <li>If else is not present, the else validation is considered to pass</li>
 * </ul>
 */
public class ConditionalValidator implements Validator {

    private SchemaRefResolver refResolver;

    public ConditionalValidator() {
    }

    public ConditionalValidator(SchemaRefResolver refResolver) {
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

        if (!schema.hasConditional()) {
            return errors;
        }

        Schema ifSchema = schema.getIfSchema();
        Schema thenSchema = schema.getThenSchema();
        Schema elseSchema = schema.getElseSchema();

        // Handle schema references for if schema
        if (ifSchema.isRef() && refResolver != null) {
            Schema resolvedSchema = refResolver.resolveRef(ifSchema.getRef(), ifSchema.getName());
            if (resolvedSchema != null) {
                ifSchema = resolvedSchema;
            }
        }

        // Validate against the if schema
        Validator ifValidator = ValidatorDispatcher.forSchema(ifSchema);
        List<ValidationError> ifErrors = ifValidator.validate(data, ifSchema, path, parentKey);

        boolean ifValidates = ifErrors.isEmpty();

        if (ifValidates) {
            // If validates successfully - then must also validate
            if (thenSchema != null) {
                // Handle schema references for then schema
                Schema resolvedThenSchema = thenSchema;
                if (thenSchema.isRef() && refResolver != null) {
                    resolvedThenSchema = refResolver.resolveRef(thenSchema.getRef(), thenSchema.getName());
                    if (resolvedThenSchema == null) {
                        resolvedThenSchema = thenSchema;
                    }
                }

                Validator thenValidator = ValidatorDispatcher.forSchema(resolvedThenSchema);
                List<ValidationError> thenErrors = thenValidator.validate(data, resolvedThenSchema, path, parentKey);

                if (!thenErrors.isEmpty()) {
                    // Then failed - this is an error
                    for (ValidationError error : thenErrors) {
                        errors.add(new ValidationError(
                            error.getNodePath(),
                            "then." + error.getExpectedType(),
                            error.getActualType(),
                            "then: " + error.getDescription()
                        ));
                    }
                    errors.add(new ValidationError(
                        path,
                        "if/then",
                        "then failed",
                        "If condition passed but 'then' schema validation failed"
                    ));
                }
            }
        } else {
            // If fails validation - else must validate (if present)
            if (elseSchema != null) {
                // Handle schema references for else schema
                Schema resolvedElseSchema = elseSchema;
                if (elseSchema.isRef() && refResolver != null) {
                    resolvedElseSchema = refResolver.resolveRef(elseSchema.getRef(), elseSchema.getName());
                    if (resolvedElseSchema == null) {
                        resolvedElseSchema = elseSchema;
                    }
                }

                Validator elseValidator = ValidatorDispatcher.forSchema(resolvedElseSchema);
                List<ValidationError> elseErrors = elseValidator.validate(data, resolvedElseSchema, path, parentKey);

                if (!elseErrors.isEmpty()) {
                    // Else failed - this is an error
                    for (ValidationError error : elseErrors) {
                        errors.add(new ValidationError(
                            error.getNodePath(),
                            "else." + error.getExpectedType(),
                            error.getActualType(),
                            "else: " + error.getDescription()
                        ));
                    }
                    errors.add(new ValidationError(
                        path,
                        "if/else",
                        "else failed",
                        "If condition failed but 'else' schema validation also failed"
                    ));
                }
            }
        }

        return errors;
    }
}
