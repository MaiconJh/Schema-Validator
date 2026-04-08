package com.maiconjh.schemacr.api;

import com.maiconjh.schemacr.validation.ValidationResult;

import java.util.List;

final class ValidationResultAdapter implements SchemaValidationResultView {

    private final ValidationResult delegate;
    private final List<SchemaValidationErrorView> errors;

    ValidationResultAdapter(ValidationResult delegate) {
        this.delegate = delegate;
        this.errors = delegate.getErrors().stream()
                .map(ValidationErrorAdapter::new)
                .map(SchemaValidationErrorView.class::cast)
                .toList();
    }

    @Override
    public boolean isSuccess() {
        return delegate.isSuccess();
    }

    @Override
    public List<SchemaValidationErrorView> getErrors() {
        return errors;
    }
}
