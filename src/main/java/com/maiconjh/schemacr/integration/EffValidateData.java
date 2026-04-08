package com.maiconjh.schemacr.integration;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;

import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import com.maiconjh.schemacr.core.PluginContext;
import com.maiconjh.schemacr.core.ValidationOrigin;
import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaRegistrationSource;
import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.ValidationResult;

import java.nio.file.Path;
import java.util.List;

/**
 * Skript effect syntax:
 * - validate yaml &lt;file&gt; using schema &lt;file&gt;
 * - validate json &lt;file&gt; using schema &lt;file&gt;
 *
 * <p>Errors are stored in {@link SkriptValidationBridge} and can be read by expression.</p>
 */
public class EffValidateData extends Effect {

    private Expression<String> dataFileExpr;
    private Expression<String> schemaFileExpr;
    private boolean yamlMode;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed,
                        SkriptParser.ParseResult parseResult) {
        this.dataFileExpr = (Expression<String>) expressions[0];
        this.schemaFileExpr = (Expression<String>) expressions[1];
        this.yamlMode = matchedPattern == 0;
        return true;
    }

    @Override
    protected void execute(Event event) {
        String dataFile = dataFileExpr.getSingle(event);
        String schemaFile = schemaFileExpr.getSingle(event);

        if (dataFile == null || schemaFile == null) {
            return;
        }

        try {
            Path dataPath = Path.of(dataFile);
            Path schemaPath = Path.of(schemaFile);

            DataFileLoader dataLoader = new DataFileLoader();
            Object data = dataLoader.load(dataPath, yamlMode);

            String schemaName = schemaPath.getFileName().toString();
            Schema schema = PluginContext.getFileSchemaLoader().load(schemaPath, schemaName);
            PluginContext.getSchemaRegistry().registerSchema(
                    schemaName,
                    schema,
                    SchemaRegistrationSource.SKRIPT,
                    schemaPath
            );

            ValidationResult result = PluginContext.getPlugin().validateTracked(data, schema, ValidationOrigin.SKRIPT);
            SkriptValidationBridge.setLastResult(result);

            if (!result.isSuccess()) {
                List<String> summaries = result.getErrors().stream()
                        .map(ValidationError::toString)
                        .toList();
                PluginContext.getPlugin().getLogger().warning("Validation failed: " + summaries);
            }
        } catch (Exception ex) {
            ValidationResult failed = ValidationResult.from(List.of(
                    new ValidationError("$", "valid file input", "error", ex.getMessage())
            ));
            SkriptValidationBridge.setLastResult(failed);
            PluginContext.getPlugin().getLogger().warning("Validation exception: " + ex.getMessage());
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "validate " + (yamlMode ? "yaml" : "json") + " file using schema";
    }
}
