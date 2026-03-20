package com.maiconjh.schemacr.integration;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.ValidationResult;

import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

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
    public boolean init(ch.njol.skript.lang.Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected String[] get(Event event) {
        ValidationResult result = SkriptValidationBridge.getLastResult();
        if (result == null || result.isSuccess()) {
            return new String[0];
        }

        List<String> messages = result.getErrors().stream()
                .map(ValidationError::toCompactString)
                .collect(Collectors.toList());

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
