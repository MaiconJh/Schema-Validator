package com.maiconjh.schemacr.validation;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.validation.array.AdditionalItemsValidator;
import com.maiconjh.schemacr.validation.array.MaxItemsValidator;
import com.maiconjh.schemacr.validation.array.MinItemsValidator;
import com.maiconjh.schemacr.validation.array.PrefixItemsValidator;
import com.maiconjh.schemacr.validation.array.UniqueItemsValidator;

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
            errors.add(new ValidationError(path, "array", ValidationUtils.typeName(data), "Expected a list/array node."));
            return errors;
        }
        boolean[] evaluatedItems = new boolean[list.size()];

        // Validate minItems constraint
        if (schema.getMinItems() != null) {
            MinItemsValidator minItemsValidator = new MinItemsValidator(schema.getMinItems());
            errors.addAll(minItemsValidator.validate(data, schema, path, parentKey));
        }

        // Validate maxItems constraint
        if (schema.getMaxItems() != null) {
            MaxItemsValidator maxItemsValidator = new MaxItemsValidator(schema.getMaxItems());
            errors.addAll(maxItemsValidator.validate(data, schema, path, parentKey));
        }

        // Validate uniqueItems constraint
        if (schema.isUniqueItems() != null && schema.isUniqueItems()) {
            UniqueItemsValidator uniqueItemsValidator = new UniqueItemsValidator();
            errors.addAll(uniqueItemsValidator.validate(data, schema, path, parentKey));
        }

        // Validate prefixItems constraint (Draft 2020-12)
        if (schema.getPrefixItems() != null && !schema.getPrefixItems().isEmpty()) {
            PrefixItemsValidator prefixItemsValidator = new PrefixItemsValidator(schema.getPrefixItems());
            errors.addAll(prefixItemsValidator.validate(data, schema, path, parentKey));
            for (int i = 0; i < Math.min(schema.getPrefixItems().size(), list.size()); i++) {
                evaluatedItems[i] = true;
            }
        }

        // Validate additionalItems constraint
        // Only applies when prefixItems is defined
        if (schema.getPrefixItems() != null && !schema.getPrefixItems().isEmpty() && schema.getAdditionalItemsSchema() != null) {
            AdditionalItemsValidator additionalItemsValidator = new AdditionalItemsValidator(
                    schema.getAdditionalItemsSchema(), schema.getPrefixItems().size());
            errors.addAll(additionalItemsValidator.validate(data, schema, path, parentKey));
        }

        // Validate contains/minContains/maxContains constraints
        if (schema.getContainsSchema() != null) {
            int containsMatches = 0;
            for (int i = 0; i < list.size(); i++) {
                Object element = list.get(i);
                String childPath = path + "[" + i + "]";
                List<ValidationError> containsErrors = ValidatorDispatcher.forSchema(schema.getContainsSchema())
                        .validate(element, schema.getContainsSchema(), childPath, parentKey);
                if (containsErrors.isEmpty()) {
                    containsMatches++;
                    evaluatedItems[i] = true;
                }
            }

            int minContains = schema.getMinContains() != null ? schema.getMinContains() : 1;
            if (containsMatches < minContains) {
                errors.add(new ValidationError(
                        path,
                        "minContains",
                        String.valueOf(containsMatches),
                        "Array must contain at least " + minContains + " element(s) matching 'contains'; found " + containsMatches
                ));
            }
            if (schema.getMaxContains() != null && containsMatches > schema.getMaxContains()) {
                errors.add(new ValidationError(
                        path,
                        "maxContains",
                        String.valueOf(containsMatches),
                        "Array must contain at most " + schema.getMaxContains() + " element(s) matching 'contains'; found " + containsMatches
                ));
            }
        }

        // Validate items constraint (standard JSON Schema)
        // Note: If prefixItems is present, 'items' applies to items beyond prefixItems count
        // but for simplicity, we'll use items schema for all items when prefixItems is not present
        if (schema.getItemSchema() == null && (schema.getPrefixItems() == null || schema.getPrefixItems().isEmpty())) {
            // Array schema with no 'items' or 'prefixItems' means no deep validation for now.
            return errors;
        }

        // Apply item validation based on the schema structure
        if ((schema.getPrefixItems() == null || schema.getPrefixItems().isEmpty()) && schema.getItemSchema() != null) {
            // Standard 'items' schema applies to all items
            for (int i = 0; i < list.size(); i++) {
                Object element = list.get(i);
                String childPath = path + "[" + i + "]";
                errors.addAll(ValidatorDispatcher.forSchema(schema.getItemSchema())
                        .validate(element, schema.getItemSchema(), childPath, parentKey));
                evaluatedItems[i] = true;
            }
        } else if (schema.getItemSchema() != null) {
            // When prefixItems is present, 'items' applies only to positions beyond prefixItems length.
            for (int i = schema.getPrefixItems().size(); i < list.size(); i++) {
                Object element = list.get(i);
                String childPath = path + "[" + i + "]";
                errors.addAll(ValidatorDispatcher.forSchema(schema.getItemSchema())
                        .validate(element, schema.getItemSchema(), childPath, parentKey));
                evaluatedItems[i] = true;
            }
        }

        // Validate unevaluatedItems for items not processed by prefixItems/items/contains
        if (schema.getUnevaluatedItemsSchema() != null || Boolean.FALSE.equals(schema.isUnevaluatedItemsAllowed())) {
            for (int i = 0; i < list.size(); i++) {
                if (evaluatedItems[i]) {
                    continue;
                }
                Object element = list.get(i);
                String childPath = path + "[" + i + "]";
                if (schema.getUnevaluatedItemsSchema() != null) {
                    Schema unevaluatedSchema = schema.getUnevaluatedItemsSchema();
                    errors.addAll(ValidatorDispatcher.forSchema(unevaluatedSchema)
                            .validate(element, unevaluatedSchema, childPath, parentKey));
                } else if (Boolean.FALSE.equals(schema.isUnevaluatedItemsAllowed())) {
                    errors.add(new ValidationError(
                            childPath,
                            "unevaluatedItems",
                            "forbidden",
                            "Array item at index " + i + " is not allowed by unevaluatedItems=false"
                    ));
                }
            }
        } else if (schema.getItemSchema() != null) {
            // When prefixItems is present, 'items' applies only to positions beyond prefixItems length.
            for (int i = schema.getPrefixItems().size(); i < list.size(); i++) {
                Object element = list.get(i);
                String childPath = path + "[" + i + "]";
                errors.addAll(ValidatorDispatcher.forSchema(schema.getItemSchema())
                        .validate(element, schema.getItemSchema(), childPath, parentKey));
            }
        }

        return errors;
    }
}
