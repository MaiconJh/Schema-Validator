package com.maiconjh.schemacr.api;

import com.maiconjh.schemacr.validation.ValidationError;

final class ValidationErrorAdapter implements SchemaValidationErrorView {

    private final ValidationError delegate;

    ValidationErrorAdapter(ValidationError delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getNodePath() {
        return delegate.getNodePath();
    }

    @Override
    public String getExpectedType() {
        return delegate.getExpectedType();
    }

    @Override
    public String getActualType() {
        return delegate.getActualType();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    @Override
    public String getMessage() {
        return delegate.getMessage();
    }
}
