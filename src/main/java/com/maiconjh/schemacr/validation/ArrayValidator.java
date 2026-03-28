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
        if (schema.getAdditionalItemsSchema() != null && schema.getPrefixItems() != null && !schema.getPrefixItems().isEmpty()) {
            for (int i = schema.getPrefixItems().size(); i < list.size(); i++) {
                evaluatedItems[i] = true;
            }
        }
        mergeEvaluatedByApplicators(list, schema, path, parentKey, evaluatedItems);

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
        }

        return errors;
    }

    private void mergeEvaluatedByApplicators(List<?> list, Schema schema, String path, String parentKey, boolean[] evaluatedItems) {
        for (Schema subSchema : schema.getAllOf()) {
            boolean[] sub = collectEvaluatedArrayIndices(list, subSchema, path, parentKey);
            mergeEvaluated(evaluatedItems, sub);
        }
        for (Schema subSchema : schema.getAnyOf()) {
            List<ValidationError> subErrors = ValidatorDispatcher.forSchema(subSchema)
                    .validate(list, subSchema, path, parentKey);
            if (subErrors.isEmpty()) {
                boolean[] sub = collectEvaluatedArrayIndices(list, subSchema, path, parentKey);
                mergeEvaluated(evaluatedItems, sub);
            }
        }
        for (Schema subSchema : schema.getOneOf()) {
            List<ValidationError> subErrors = ValidatorDispatcher.forSchema(subSchema)
                    .validate(list, subSchema, path, parentKey);
            if (subErrors.isEmpty()) {
                boolean[] sub = collectEvaluatedArrayIndices(list, subSchema, path, parentKey);
                mergeEvaluated(evaluatedItems, sub);
            }
        }
        if (schema.getIfSchema() != null) {
            List<ValidationError> ifErrors = ValidatorDispatcher.forSchema(schema.getIfSchema())
                    .validate(list, schema.getIfSchema(), path, parentKey);
            Schema branch = ifErrors.isEmpty() ? schema.getThenSchema() : schema.getElseSchema();
            if (branch != null) {
                boolean[] sub = collectEvaluatedArrayIndices(list, branch, path, parentKey);
                mergeEvaluated(evaluatedItems, sub);
            }
        }
    }

    private boolean[] collectEvaluatedArrayIndices(List<?> list, Schema schema, String path, String parentKey) {
        boolean[] evaluated = new boolean[list.size()];
        int prefixSize = schema.getPrefixItems() == null ? 0 : schema.getPrefixItems().size();

        if (prefixSize > 0) {
            for (int i = 0; i < Math.min(prefixSize, list.size()); i++) {
                evaluated[i] = true;
            }
        }
        if (schema.getItemSchema() != null) {
            int start = prefixSize > 0 ? prefixSize : 0;
            for (int i = start; i < list.size(); i++) {
                evaluated[i] = true;
            }
        }
        if (schema.getAdditionalItemsSchema() != null && prefixSize > 0) {
            for (int i = prefixSize; i < list.size(); i++) {
                evaluated[i] = true;
            }
        }
        if (schema.getContainsSchema() != null) {
            for (int i = 0; i < list.size(); i++) {
                Object element = list.get(i);
                String childPath = path + "[" + i + "]";
                List<ValidationError> containsErrors = ValidatorDispatcher.forSchema(schema.getContainsSchema())
                        .validate(element, schema.getContainsSchema(), childPath, parentKey);
                if (containsErrors.isEmpty()) {
                    evaluated[i] = true;
                }
            }
        }
        return evaluated;
    }

    private void mergeEvaluated(boolean[] target, boolean[] source) {
        for (int i = 0; i < target.length && i < source.length; i++) {
            target[i] = target[i] || source[i];
        }
    }
}
