package com.maiconjh.schemacr.validation;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validates primitive nodes (string/number/boolean/null/any).
 */
public class PrimitiveValidator implements Validator {

    @Override
    public List<ValidationError> validate(Object data, Schema schema, String path, String parentKey) {
        List<ValidationError> errors = new ArrayList<>();
        SchemaType type = schema.getType();

        if (type == SchemaType.ANY) {
            return errors;
        }

        boolean valid = switch (type) {
            case STRING -> data instanceof String;
            // NUMBER accepts any numeric type (Integer, Long, Float, Double, etc.)
            // Per JSON Schema spec, "number" includes both integers and decimals
            case NUMBER -> data instanceof Number;
            // INTEGER accepts any valid integer (Long, Integer, Short, Byte, or decimal with no fractional part)
            case INTEGER -> data instanceof Number && isValidInteger((Number) data);
            case BOOLEAN -> data instanceof Boolean;
            case NULL -> data == null;
            default -> true;
        };

        if (!valid) {
            errors.add(new ValidationError(
                    path,
                    type.name().toLowerCase(),
                    ValidationUtils.typeName(data),
                    "Primitive value does not match schema type."
            ));
            return errors;
        }

        // Validate enum constraints
        if (!schema.getEnumValues().isEmpty()) {
            if (!schema.isValidEnum(data)) {
                errors.add(new ValidationError(
                        path,
                        "enum",
                        String.valueOf(data),
                        "Value must be one of: " + schema.getEnumValues()
                ));
            }
            return errors;
        }

        // Validate numeric constraints (min/max)
        if ((type == SchemaType.NUMBER || type == SchemaType.INTEGER) && data instanceof Number number) {
            if (schema.getMinimum() != null) {
                boolean minValid = schema.isExclusiveMinimum()
                        ? number.doubleValue() > schema.getMinimum().doubleValue()
                        : number.doubleValue() >= schema.getMinimum().doubleValue();
                if (!minValid) {
                    String operator = schema.isExclusiveMinimum() ? ">" : ">=";
                    errors.add(new ValidationError(
                            path,
                            "minimum",
                            String.valueOf(number),
                            "Value must be " + operator + " " + schema.getMinimum()
                    ));
                }
            }
            if (schema.getMaximum() != null) {
                boolean maxValid = schema.isExclusiveMaximum()
                        ? number.doubleValue() < schema.getMaximum().doubleValue()
                        : number.doubleValue() <= schema.getMaximum().doubleValue();
                if (!maxValid) {
                    String operator = schema.isExclusiveMaximum() ? "<" : "<=";
                    errors.add(new ValidationError(
                            path,
                            "maximum",
                            String.valueOf(number),
                            "Value must be " + operator + " " + schema.getMaximum()
                    ));
                }
            }
            // Validate multipleOf
            if (schema.getMultipleOf() != null) {
                double divisor = schema.getMultipleOf().doubleValue();
                if (divisor != 0) {
                    double result = number.doubleValue() / divisor;
                    if (result != Math.floor(result)) {
                        errors.add(new ValidationError(
                                path,
                                "multipleOf",
                                String.valueOf(number),
                                "Value must be a multiple of " + schema.getMultipleOf()
                        ));
                    }
                }
            }
        }

        // Validate string constraints (minLength, maxLength, pattern)
        if (type == SchemaType.STRING && data instanceof String str) {
            if (schema.getMinLength() != null && str.length() < schema.getMinLength()) {
                errors.add(new ValidationError(
                        path,
                        "minLength",
                        String.valueOf(str.length()),
                        "String must be at least " + schema.getMinLength() + " characters"
                ));
            }
            if (schema.getMaxLength() != null && str.length() > schema.getMaxLength()) {
                errors.add(new ValidationError(
                        path,
                        "maxLength",
                        String.valueOf(str.length()),
                        "String must be at most " + schema.getMaxLength() + " characters"
                ));
            }
            if (schema.getPattern() != null) {
                Pattern regex = Pattern.compile(schema.getPattern());
                if (!regex.matcher(str).matches()) {
                    errors.add(new ValidationError(
                            path,
                            "pattern",
                            str,
                            "String must match pattern: " + schema.getPattern()
                    ));
                }
            }
            // Validate format
            if (schema.hasFormat() && !FormatValidator.isValid(schema.getFormat(), str)) {
                errors.add(new ValidationError(
                        path,
                        "format",
                        str,
                        FormatValidator.getErrorMessage(schema.getFormat())
                ));
            }
        }

        return errors;
    }
    
    /**
     * Checks if a number is a valid integer (no decimal part).
     * In JSON Schema, integers are numbers that have no fractional or decimal part.
     * 
     * @param number the number to check
     * @return true if the number is a valid integer
     */
    private boolean isValidInteger(Number number) {
        if (number instanceof Integer) {
            return true;
        }
        if (number instanceof Long) {
            return true;
        }
        if (number instanceof Short) {
            return true;
        }
        if (number instanceof Byte) {
            return true;
        }
        // For BigDecimal, BigInteger, Float, Double - check if has no decimal part
        double value = number.doubleValue();
        return value == Math.floor(value) && !Double.isInfinite(value);
    }
}
