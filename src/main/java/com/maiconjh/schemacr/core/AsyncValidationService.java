package com.maiconjh.schemacr.core;

import com.maiconjh.schemacr.api.AsyncValidationCompleteEvent;
import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.validation.ValidationResult;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncValidationService {

    private final SchemaValidatorPlugin plugin;
    private final ExecutorService executor;

    public AsyncValidationService(SchemaValidatorPlugin plugin, int poolSize) {
        this(plugin, poolSize, 1000);
    }

    public AsyncValidationService(SchemaValidatorPlugin plugin, int poolSize, int queueCapacity) {
        this.plugin = plugin;
        AtomicInteger threadCounter = new AtomicInteger();
        this.executor = new ThreadPoolExecutor(
                poolSize,
                poolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                runnable -> {
                    Thread thread = new Thread(runnable, "schemacr-async-" + threadCounter.incrementAndGet());
                    thread.setDaemon(true);
                    return thread;
                }
        );
    }

    public CompletableFuture<ValidationResult> validateAsync(Object data, String schemaName) {
        Schema schema = plugin.getSchemaRegistry().getSchema(schemaName).orElseThrow(
                () -> new IllegalArgumentException("Unknown schema: " + schemaName));

        long startedAt = System.nanoTime();
        return CompletableFuture.supplyAsync(() -> plugin.getValidationService().validate(data, schema), executor)
                .whenComplete((result, throwable) -> {
                    if (throwable == null) {
                        long duration = System.nanoTime() - startedAt;
                        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager()
                                .callEvent(new AsyncValidationCompleteEvent(schemaName, result, duration)));
                    }
                });
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
