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

    /**
     * Gets a short error message (just the description).
     * Useful for Skript integration where detailed info may be too verbose.
     * 
     * @return short error message
     */
    public String getMessage() {
        if (description != null && !description.isEmpty()) {
            return description;
        }
        if (expectedType != null && !expectedType.isEmpty()) {
            return "Expected " + expectedType + " at " + nodePath;
        }
        return "Validation failed at " + nodePath;
    }

    /**
     * Gets a compact error string for list display in Skript.
     * Format: "[path] expected type: actual type - description"
     * 
     * @return compact error string
     */
    public String toCompactString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(nodePath).append("] ");
        sb.append(expectedType);
        if (actualType != null && !actualType.isEmpty()) {
            sb.append(": ").append(actualType);
        }
        if (description != null && !description.isEmpty()) {
            sb.append(" - ").append(description);
        }
        return sb.toString();
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
