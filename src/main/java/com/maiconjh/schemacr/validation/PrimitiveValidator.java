package com.maiconjh.schemacr.validation;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaType;
import com.maiconjh.schemacr.validation.misc.ConstValidator;
import com.maiconjh.schemacr.validation.misc.ReadOnlyValidator;
import com.maiconjh.schemacr.validation.misc.WriteOnlyValidator;

import java.util.ArrayList;
import java.util.Base64;
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
            if (schema.getExclusiveMinimum() != null) {
                boolean minValid = number.doubleValue() > schema.getExclusiveMinimum().doubleValue();
                if (!minValid) {
                    errors.add(new ValidationError(
                            path,
                            "exclusiveMinimum",
                            String.valueOf(number),
                            "Value must be > " + schema.getExclusiveMinimum()
                    ));
                }
            } else if (schema.getMinimum() != null) {
                boolean minValid = schema.usesLegacyExclusiveMinimum()
                        ? number.doubleValue() > schema.getMinimum().doubleValue()
                        : number.doubleValue() >= schema.getMinimum().doubleValue();
                if (!minValid) {
                    String operator = schema.usesLegacyExclusiveMinimum() ? ">" : ">=";
                    errors.add(new ValidationError(
                            path,
                            "minimum",
                            String.valueOf(number),
                            "Value must be " + operator + " " + schema.getMinimum()
                    ));
                }
            }
            if (schema.getExclusiveMaximum() != null) {
                boolean maxValid = number.doubleValue() < schema.getExclusiveMaximum().doubleValue();
                if (!maxValid) {
                    errors.add(new ValidationError(
                            path,
                            "exclusiveMaximum",
                            String.valueOf(number),
                            "Value must be < " + schema.getExclusiveMaximum()
                    ));
                }
            } else if (schema.getMaximum() != null) {
                boolean maxValid = schema.usesLegacyExclusiveMaximum()
                        ? number.doubleValue() < schema.getMaximum().doubleValue()
                        : number.doubleValue() <= schema.getMaximum().doubleValue();
                if (!maxValid) {
                    String operator = schema.usesLegacyExclusiveMaximum() ? "<" : "<=";
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

            // Validate contentEncoding/contentMediaType/contentSchema (Draft 2020-12 content vocabulary)
            if (schema.getContentEncoding() != null
                    && ("base64".equalsIgnoreCase(schema.getContentEncoding())
                    || "base64url".equalsIgnoreCase(schema.getContentEncoding()))) {
                try {
                    decodeByEncoding(str, schema.getContentEncoding());
                } catch (IllegalArgumentException ex) {
                    errors.add(new ValidationError(
                            path,
                            "contentEncoding",
                            schema.getContentEncoding(),
                            "String is not valid base64 content"
                    ));
                }
            }
            if (schema.getContentSchema() != null && schema.getContentMediaType() != null
                    && isJsonMediaType(schema.getContentMediaType())) {
                try {
                    String contentToParse = str;
                    if (schema.getContentEncoding() != null
                            && ("base64".equalsIgnoreCase(schema.getContentEncoding())
                            || "base64url".equalsIgnoreCase(schema.getContentEncoding()))) {
                        contentToParse = decodeByEncoding(str, schema.getContentEncoding());
                    }
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    Object parsed = mapper.readValue(contentToParse, Object.class);
                    List<ValidationError> contentSchemaErrors = ValidatorDispatcher.forSchema(schema.getContentSchema())
                            .validate(parsed, schema.getContentSchema(), path + "#content", parentKey);
                    errors.addAll(contentSchemaErrors);
                } catch (Exception ex) {
                    errors.add(new ValidationError(
                            path,
                            "contentMediaType",
                            schema.getContentMediaType(),
                            "String is not valid JSON content for contentSchema validation"
                    ));
                }
            }
        }

        // Validate const constraint
        if (schema.getConstValue() != null) {
            ConstValidator constValidator = new ConstValidator(schema.getConstValue());
            errors.addAll(constValidator.validate(data, schema, path, parentKey));
        }

        // Validate readOnly constraint
        if (Boolean.TRUE.equals(schema.isReadOnly())) {
            ReadOnlyValidator readOnlyValidator = new ReadOnlyValidator(true);
            errors.addAll(readOnlyValidator.validate(data, schema, path, parentKey));
        }

        // Validate writeOnly constraint
        if (Boolean.TRUE.equals(schema.isWriteOnly())) {
            WriteOnlyValidator writeOnlyValidator = new WriteOnlyValidator(true);
            errors.addAll(writeOnlyValidator.validate(data, schema, path, parentKey));
        }

        return errors;
    }

    private boolean isJsonMediaType(String mediaType) {
        if (mediaType == null) return false;
        String normalized = mediaType.toLowerCase();
        return "application/json".equals(normalized) || normalized.endsWith("+json");
    }

    private String decodeByEncoding(String content, String encoding) {
        if ("base64url".equalsIgnoreCase(encoding)) {
            return new String(Base64.getUrlDecoder().decode(content), java.nio.charset.StandardCharsets.UTF_8);
        }
        return new String(Base64.getDecoder().decode(content), java.nio.charset.StandardCharsets.UTF_8);
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
