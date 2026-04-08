package com.maiconjh.schemacr.api;

/**
 * Stable public view over a validation error.
 */
public interface SchemaValidationErrorView {

    String getNodePath();

    String getExpectedType();

    String getActualType();

    String getDescription();

    String getMessage();
}
