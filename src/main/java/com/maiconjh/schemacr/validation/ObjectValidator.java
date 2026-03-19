package com.maiconjh.schemacr.validation;

import com.maiconjh.schemacr.schemes.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Validates object/map nodes.
 */
public class ObjectValidator implements Validator {

    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();

        if (!(data instanceof Map<?, ?> map)) {
            errors.add(new ValidationError(path, "object", ValidationUtils.typeName(data), "Expected an object/map node."));
            return errors;
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
            if (!matchedPattern && !schema.isAdditionalPropertiesAllowed()) {
                errors.add(new ValidationError(
                        path + "." + key,
                        "additionalProperties",
                        "forbidden",
                        "Unknown field '" + key + "' is not allowed"
                ));
            }
        }

        return errors;
    }
}
