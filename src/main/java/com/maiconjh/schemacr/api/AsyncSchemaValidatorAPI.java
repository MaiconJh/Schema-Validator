package com.maiconjh.schemacr.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Async facade for schema validation operations.
 */
public interface AsyncSchemaValidatorAPI {

    CompletableFuture<SchemaValidationResultView> validateAsync(Object data, String schemaName);

    default CompletableFuture<List<SchemaValidationResultView>> validateBatchAsync(List<?> dataList, String schemaName) {
        if (dataList == null) {
            return CompletableFuture.completedFuture(List.of());
        }

        List<CompletableFuture<SchemaValidationResultView>> futures = dataList.stream()
                .map(data -> validateAsync(data, schemaName))
                .toList();

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(ignored -> futures.stream().map(CompletableFuture::join).toList());
    }
}
