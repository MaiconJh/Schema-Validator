package com.yourname.schemacr.integration;

import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.util.SimpleExpression;
import com.yourname.schemacr.validation.ValidationError;
import com.yourname.schemacr.validation.ValidationResult;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Exposes validation errors as a string array to Skript.
 *
 * <p>Example in Skript:
 * <pre>
 * set {_errors::*} to last schema validation errors
 * if size of {_errors::*} > 0:
 *     broadcast "%{_errors::1}%"
 * </pre>
 */
public class ExprLastValidationErrors extends SimpleExpression<String> {

    @Override
    protected String[] get(Event event) {
        ValidationResult result = SkriptValidationBridge.getLastResult();
        if (result == null || result.isSuccess()) {
            return new String[0];
        }

        List<String> messages = result.getErrors().stream()
                .map(ValidationError::toString)
                .toList();

        return messages.toArray(new String[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "last schema validation errors";
    }
}
