package com.maiconjh.schemacr.core;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.validation.ObjectValidator;
import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.ValidationResult;
import com.maiconjh.schemacr.validation.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Facade for validating arbitrary data against a registered schema.
 *
 * <p>Swap the root validator to a composed strategy if you need richer rule dispatching.</p>
 */
public class ValidationService {

    private final Validator rootValidator;

    public ValidationService() {
        this.rootValidator = new ObjectValidator();
    }

    public ValidationResult validate(Object data, Schema schema) {
        List<ValidationError> errors = rootValidator.validate(data, schema, "$", null);
        return ValidationResult.from(errors);
    }

    /**
     * Validates multiple data objects against the same schema.
     * 
     * @param dataList list of data objects to validate
     * @param schema schema to validate against
     * @return list of validation results, one for each data object
     */
    public List<ValidationResult> validateBatch(List<Object> dataList, Schema schema) {
        List<ValidationResult> results = new ArrayList<>();
        for (Object data : dataList) {
            results.add(validate(data, schema));
        }
        return results;
    }

    /**
     * Validates multiple data objects against the same schema and returns
     * a summary of all validation results.
     * 
     * @param dataList list of data objects to validate
     * @param schema schema to validate against
     * @return true if all validations passed, false otherwise
     */
    public boolean validateAll(List<Object> dataList, Schema schema) {
        for (Object data : dataList) {
            ValidationResult result = validate(data, schema);
            if (!result.isSuccess()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the count of failed validations in a batch.
     * 
     * @param dataList list of data objects to validate
     * @param schema schema to validate against
     * @return number of failed validations
     */
    public int getFailedCount(List<Object> dataList, Schema schema) {
        int count = 0;
        for (Object data : dataList) {
            ValidationResult result = validate(data, schema);
            if (!result.isSuccess()) {
                count++;
            }
        }
        return count;
    }
}
