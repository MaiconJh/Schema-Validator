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
        }

        // Validate additionalItems constraint
        // Only applies when prefixItems is defined
        if (schema.getPrefixItems() != null && schema.getAdditionalItemsSchema() != null) {
            AdditionalItemsValidator additionalItemsValidator = new AdditionalItemsValidator(
                    schema.getAdditionalItemsSchema(), schema.getPrefixItems().size());
            errors.addAll(additionalItemsValidator.validate(data, schema, path, parentKey));
        }

        // Validate items constraint (standard JSON Schema)
        // Note: If prefixItems is present, 'items' applies to items beyond prefixItems count
        // but for simplicity, we'll use items schema for all items when prefixItems is not present
        if (schema.getItemSchema() == null && schema.getPrefixItems() == null) {
            // Array schema with no 'items' or 'prefixItems' means no deep validation for now.
            return errors;
        }

        // Apply item validation based on the schema structure
        if (schema.getPrefixItems() == null && schema.getItemSchema() != null) {
            // Standard 'items' schema applies to all items
            for (int i = 0; i < list.size(); i++) {
                Object element = list.get(i);
                String childPath = path + "[" + i + "]";
                errors.addAll(ValidatorDispatcher.forSchema(schema.getItemSchema())
                        .validate(element, schema.getItemSchema(), childPath, parentKey));
            }
        }

        return errors;
    }
}
