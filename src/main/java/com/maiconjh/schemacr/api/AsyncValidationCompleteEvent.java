package com.maiconjh.schemacr.api;

import com.maiconjh.schemacr.validation.ValidationResult;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncValidationCompleteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String schemaName;
    private final ValidationResult result;
    private final long durationNanos;

    public AsyncValidationCompleteEvent(String schemaName, ValidationResult result, long durationNanos) {
        super(true);
        this.schemaName = schemaName;
        this.result = result;
        this.durationNanos = durationNanos;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public ValidationResult getResult() {
        return result;
    }

    public long getDurationNanos() {
        return durationNanos;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
