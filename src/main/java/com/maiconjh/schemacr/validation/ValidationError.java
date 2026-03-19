package com.maiconjh.schemacr.validation;

/**
 * Detailed validation error model for scripting-friendly reporting.
 */
public class ValidationError {

    private final String nodePath;
    private final String expectedType;
    private final String actualType;
    private final String description;

    public ValidationError(String nodePath, String expectedType, String actualType, String description) {
        this.nodePath = nodePath;
        this.expectedType = expectedType;
        this.actualType = actualType;
        this.description = description;
    }

    public String getNodePath() {
        return nodePath;
    }

    public String getExpectedType() {
        return expectedType;
    }

    public String getActualType() {
        return actualType;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Validation failed at: ").append(nodePath).append("\n");
        sb.append("Expected type: ").append(expectedType).append("\n");
        
        if (actualType != null && !actualType.isEmpty()) {
            sb.append("Actual value: ").append(actualType).append("\n");
        }
        
        if (description != null && !description.isEmpty()) {
            sb.append("Details: ").append(description);
        }
        
        return sb.toString();
    }
}
