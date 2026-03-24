package com.maiconjh.schemacr.validation.array;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.Validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validator for the uniqueItems JSON Schema keyword.
 * Ensures that all elements in an array are unique.
 */
public class UniqueItemsValidator implements Validator {
    
    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();
        // Only validate if uniqueItems is explicitly set to true
        if (data instanceof List<?> list && Boolean.TRUE.equals(schema.isUniqueItems())) {
            Set<Object> seen = new HashSet<>();
            for (Object item : list) {
                // For numeric types, convert to double for proper equality comparison
                // This ensures that 1 and 1.0 are considered duplicates
                Object normalizedItem = normalizeForComparison(item);
                if (!seen.add(normalizedItem)) {
                    errors.add(new ValidationError(path, "uniqueItems", "duplicate",
                            "Array items must be unique; duplicate found: " + item));
                    break;
                }
            }
        }
        return errors;
    }
    
    /**
     * Normalizes a value for comparison in uniqueItems validation.
     * Numeric types are converted to Double to ensure proper equality.
     */
    private Object normalizeForComparison(Object item) {
        if (item instanceof Number) {
            return ((Number) item).doubleValue();
        }
        return item;
    }
}