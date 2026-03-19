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
        return "ValidationError{" +
                "nodePath='" + nodePath + '\'' +
                ", expectedType='" + expectedType + '\'' +
                ", actualType='" + actualType + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
