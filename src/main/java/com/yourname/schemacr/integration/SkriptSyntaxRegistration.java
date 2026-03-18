package com.yourname.schemacr.integration;

import ch.njol.skript.Skript;
import com.yourname.schemacr.core.SchemaValidatorPlugin;

/**
 * Central registration point for Skript syntax.
 */
public final class SkriptSyntaxRegistration {

    private SkriptSyntaxRegistration() {
    }

    public static void register(SchemaValidatorPlugin plugin) {
        Skript.registerEffect(EffValidateData.class,
                "validate yaml %string% using schema %string%",
                "validate json %string% using schema %string%");

        Skript.registerExpression(ExprLastValidationErrors.class, String.class,
                ch.njol.skript.lang.ExpressionType.SIMPLE,
                "last schema validation errors");

        plugin.getLogger().info("Registered Skript syntax for Schema-Validator.");
    }
}
