package com.maiconjh.schemacr.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaRefResolver;
import com.maiconjh.schemacr.validation.object.MinPropertiesValidator;
import com.maiconjh.schemacr.validation.object.MaxPropertiesValidator;
import com.maiconjh.schemacr.validation.object.DependentRequiredValidator;
import com.maiconjh.schemacr.validation.object.DependentSchemasValidator;

/**
 * Validates object/map nodes.
 */
public class ObjectValidator implements Validator {

    private SchemaRefResolver refResolver;

    public ObjectValidator() {
    }

    public ObjectValidator(SchemaRefResolver refResolver) {
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

        // Handle schema references ($ref)
        if (schema.isRef() && refResolver != null) {
            Schema resolvedSchema = refResolver.resolveRef(schema.getRef(), schema.getName());
            if (resolvedSchema != null) {
                return validate(data, resolvedSchema, path, parentKey);
            } else {
                errors.add(new ValidationError(path, "$ref", schema.getRef(), 
                    "Could not resolve reference: " + schema.getRef()));
                return errors;
            }
        }

        // Verify data is a Map before proceeding with object validation
        if (!(data instanceof Map<?, ?> map)) {
            errors.add(new ValidationError(path, "object", ValidationUtils.typeName(data), "Expected an object/map node."));
            return errors;
        }

        // Handle allOf composition - data must validate against ALL schemas
        if (schema.hasAllOf()) {
            for (int i = 0; i < schema.getAllOf().size(); i++) {
                Schema allOfSchema = schema.getAllOf().get(i);
                Validator validator = ValidatorDispatcher.forSchema(allOfSchema);
                List<ValidationError> allOfErrors = validator.validate(data, allOfSchema, path, parentKey);
                if (!allOfErrors.isEmpty()) {
                    // Add prefix to identify which allOf failed
                    for (ValidationError error : allOfErrors) {
                        errors.add(new ValidationError(
                            error.getNodePath(),
                            "allOf[" + i + "]." + error.getExpectedType(),
                            error.getActualType(),
                            "allOf[" + i + "]: " + error.getDescription()
                        ));
                    }
                }
            }
            // If there are allOf errors, we still continue to validate other constraints
            // but the final result will include these errors
        }

        // Handle anyOf composition - data must validate against AT LEAST ONE schema
        if (schema.hasAnyOf()) {
            boolean anyOfMatched = false;
            List<ValidationError> anyOfErrors = new ArrayList<>();
            
            for (int i = 0; i < schema.getAnyOf().size(); i++) {
                Schema anyOfSchema = schema.getAnyOf().get(i);
                Validator validator = ValidatorDispatcher.forSchema(anyOfSchema);
                List<ValidationError> schemaErrors = validator.validate(data, anyOfSchema, path, parentKey);
                
                if (schemaErrors.isEmpty()) {
                    anyOfMatched = true;
                    break;
                } else {
                    // Collect errors for reporting if no schema matches
                    for (ValidationError error : schemaErrors) {
                        anyOfErrors.add(new ValidationError(
                            error.getNodePath(),
                            "anyOf[" + i + "]." + error.getExpectedType(),
                            error.getActualType(),
                            "anyOf[" + i + "]: " + error.getDescription()
                        ));
                    }
                }
            }
            
            if (!anyOfMatched) {
                errors.addAll(anyOfErrors);
                // Add a summary error for anyOf
                errors.add(new ValidationError(
                    path,
                    "anyOf",
                    "no match",
                    "Data must match at least one schema in anyOf (matched 0 of " + schema.getAnyOf().size() + ")"
                ));
            }
        }

        // Handle oneOf composition - exactly one schema must match
        if (schema.hasOneOf()) {
            List<Schema> oneOfSchemas = schema.getOneOf();
            int validCount = 0;
            List<ValidationError> oneOfAllErrors = new ArrayList<>();
            
            for (int i = 0; i < oneOfSchemas.size(); i++) {
                Schema oneOfSchema = oneOfSchemas.get(i);
                Validator validator = ValidatorDispatcher.forSchema(oneOfSchema);
                List<ValidationError> schemaErrors = validator.validate(data, oneOfSchema, path, parentKey);
                
                if (schemaErrors.isEmpty()) {
                    validCount++;
                } else {
                    for (ValidationError error : schemaErrors) {
                        oneOfAllErrors.add(new ValidationError(
                            error.getNodePath(),
                            "oneOf[" + i + "]." + error.getExpectedType(),
                            error.getActualType(),
                            "oneOf[" + i + "]: " + error.getDescription()
                        ));
                    }
                }
            }
            
            if (validCount == 0) {
                errors.addAll(oneOfAllErrors);
                errors.add(new ValidationError(
                    path,
                    "oneOf",
                    "no match",
                    "Data must match exactly one schema in oneOf (matched 0 of " + oneOfSchemas.size() + ")"
                ));
            } else if (validCount > 1) {
                errors.add(new ValidationError(
                    path,
                    "oneOf",
                    "multiple matches",
                    "Data must match exactly one schema in oneOf (matched " + validCount + " of " + oneOfSchemas.size() + ")"
                ));
            }
        }

        // Handle not - instance must NOT match this schema
        if (schema.hasNot()) {
            Schema notSchema = schema.getNot();
            Validator validator = ValidatorDispatcher.forSchema(notSchema);
            List<ValidationError> notErrors = validator.validate(data, notSchema, path, parentKey);
            
            if (notErrors.isEmpty()) {
                // Instance matched the not schema, which is an error
                errors.add(new ValidationError(
                    path,
                    "not",
                    "matched",
                    "Data must NOT match the schema defined in 'not'"
                ));
            }
        }

        // Handle if/then/else conditional validation
        if (schema.hasConditional()) {
            Schema ifSchema = schema.getIfSchema();
            Schema thenSchema = schema.getThenSchema();
            Schema elseSchema = schema.getElseSchema();
            
            // Evaluate if condition
            Validator ifValidator = ValidatorDispatcher.forSchema(ifSchema);
            List<ValidationError> ifErrors = ifValidator.validate(data, ifSchema, path, parentKey);
            
            boolean ifMatched = ifErrors.isEmpty();
            
            if (ifMatched && thenSchema != null) {
                // If matched, validate against then schema
                Validator thenValidator = ValidatorDispatcher.forSchema(thenSchema);
                errors.addAll(thenValidator.validate(data, thenSchema, path, parentKey));
            } else if (!ifMatched && elseSchema != null) {
                // If didn't match, validate against else schema
                Validator elseValidator = ValidatorDispatcher.forSchema(elseSchema);
                errors.addAll(elseValidator.validate(data, elseSchema, path, parentKey));
            }
        }

        // Validate minProperties constraint
        if (schema.getMinProperties() != null) {
            MinPropertiesValidator minPropertiesValidator = new MinPropertiesValidator(schema.getMinProperties());
            errors.addAll(minPropertiesValidator.validate(data, schema, path, parentKey));
        }

        // Validate maxProperties constraint
        if (schema.getMaxProperties() != null) {
            MaxPropertiesValidator maxPropertiesValidator = new MaxPropertiesValidator(schema.getMaxProperties());
            errors.addAll(maxPropertiesValidator.validate(data, schema, path, parentKey));
        }

        // Validate dependentRequired constraint
        if (schema.getDependentRequired() != null && !schema.getDependentRequired().isEmpty()) {
            DependentRequiredValidator dependentRequiredValidator = new DependentRequiredValidator(schema.getDependentRequired());
            errors.addAll(dependentRequiredValidator.validate(data, schema, path, parentKey));
        }

        // Validate dependentSchemas constraint
        if (schema.getDependentSchemas() != null && !schema.getDependentSchemas().isEmpty()) {
            DependentSchemasValidator dependentSchemasValidator = new DependentSchemasValidator(schema.getDependentSchemas());
            errors.addAll(dependentSchemasValidator.validate(data, schema, path, parentKey));
        }

        // Validate required fields
        for (String requiredField : schema.getRequiredFields()) {
            if (!map.containsKey(requiredField)) {
                errors.add(new ValidationError(
                        path + "." + requiredField,
                        "required",
                        "missing",
                        "Required field '" + requiredField + "' is missing"
                ));
            }
        }

        // Validate declared schema properties (only if present in data)
        for (Map.Entry<String, Schema> property : schema.getProperties().entrySet()) {
            String key = property.getKey();
            
            // Skip validation if property is not present in data
            if (!map.containsKey(key)) {
                continue;
            }
            
            Schema propertySchema = property.getValue();
            Object child = map.get(key);
            String childPath = path + "." + key;

            Validator validator = ValidatorDispatcher.forSchema(propertySchema);
            errors.addAll(validator.validate(child, propertySchema, childPath, key));
        }

        // Validate unknown fields using patternProperties or additionalProperties
        for (Object keyObj : map.keySet()) {
            String key = String.valueOf(keyObj);
            
            // Skip if already validated in properties
            if (schema.getProperties().containsKey(key)) {
                continue;
            }
            
            // Try to match against patternProperties
            boolean matchedPattern = false;
            for (Map.Entry<String, Schema> ppEntry : schema.getPatternProperties().entrySet()) {
                String pattern = ppEntry.getKey();
                try {
                    if (Pattern.matches(pattern, key)) {
                        matchedPattern = true;
                        Schema ppSchema = ppEntry.getValue();
                        Object child = map.get(key);
                        String childPath = path + "." + key;
                        
                        Validator validator = ValidatorDispatcher.forSchema(ppSchema);
                        errors.addAll(validator.validate(child, ppSchema, childPath, key));
                        break;
                    }
                } catch (Exception e) {
                    // Invalid regex pattern, skip
                }
            }
            
            // If not matched by patternProperties, check additionalProperties
            if (!matchedPattern) {
                // Check if additionalProperties is a Schema (object) or boolean
                if (schema.getAdditionalPropertiesSchema() != null) {
                    // additionalProperties is a Schema - validate against it
                    Schema additionalPropsSchema = schema.getAdditionalPropertiesSchema();
                    Validator validator = ValidatorDispatcher.forSchema(additionalPropsSchema);
                    Object child = map.get(key);
                    String childPath = path + "." + key;
                    errors.addAll(validator.validate(child, additionalPropsSchema, childPath, key));
                } else if (!schema.isAdditionalPropertiesAllowed()) {
                    // additionalProperties is false - not allowed
                    errors.add(new ValidationError(
                            path + "." + key,
                            "additionalProperties",
                            "forbidden",
                            "Unknown field '" + key + "' is not allowed"
                    ));
                }
            }
        }

        return errors;
    }
}
