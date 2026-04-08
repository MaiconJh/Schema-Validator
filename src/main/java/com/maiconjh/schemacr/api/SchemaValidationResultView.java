package com.maiconjh.schemacr.api;

import java.util.List;

/**
 * Stable public view over a validation result.
 */
public interface SchemaValidationResultView {

    boolean isSuccess();

    List<SchemaValidationErrorView> getErrors();
}
