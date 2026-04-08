package com.maiconjh.schemacr.core;

import com.maiconjh.schemacr.validation.ValidationResult;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Collects lightweight runtime validation metrics.
 */
public class ValidationMetrics {

    private final AtomicLong totalValidations = new AtomicLong();
    private final AtomicLong successfulValidations = new AtomicLong();
    private final AtomicLong failedValidations = new AtomicLong();
    private final AtomicLong totalValidationNanos = new AtomicLong();
    private final Map<ValidationOrigin, AtomicLong> countsByOrigin = new EnumMap<>(ValidationOrigin.class);

    public ValidationMetrics() {
        for (ValidationOrigin origin : ValidationOrigin.values()) {
            countsByOrigin.put(origin, new AtomicLong());
        }
    }

    public void record(ValidationOrigin origin, long elapsedNanos, ValidationResult result) {
        totalValidations.incrementAndGet();
        totalValidationNanos.addAndGet(Math.max(0L, elapsedNanos));
        countsByOrigin.get(origin).incrementAndGet();

        if (result.isSuccess()) {
            successfulValidations.incrementAndGet();
        } else {
            failedValidations.incrementAndGet();
        }
    }

    public long getTotalValidations() {
        return totalValidations.get();
    }

    public long getSuccessfulValidations() {
        return successfulValidations.get();
    }

    public long getFailedValidations() {
        return failedValidations.get();
    }

    public long getTotalValidationNanos() {
        return totalValidationNanos.get();
    }

    public long getCountForOrigin(ValidationOrigin origin) {
        return countsByOrigin.get(origin).get();
    }

    public double getAverageValidationMillis() {
        long total = totalValidations.get();
        if (total == 0) {
            return 0.0d;
        }
        return totalValidationNanos.get() / 1_000_000.0d / total;
    }
}
