package com.yourname.schemacr.integration;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.yourname.schemacr.core.PluginContext;
import com.yourname.schemacr.core.ValidationService;
import com.yourname.schemacr.schemes.Schema;
import com.yourname.schemacr.validation.ValidationError;
import com.yourname.schemacr.validation.ValidationResult;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Skript effect syntax:
 * - validate yaml <file> using schema <file>
 * - validate json <file> using schema <file>
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
            DataFileLoader dataLoader = new DataFileLoader();
            Object data = dataLoader.load(Path.of(dataFile), yamlMode);

            String schemaName = Path.of(schemaFile).getFileName().toString();
            Schema schema = PluginContext.getFileSchemaLoader().load(Path.of(schemaFile), schemaName);
            PluginContext.getSchemaRegistry().registerSchema(schemaName, schema);

            ValidationService service = new ValidationService();
            ValidationResult result = service.validate(data, schema);
            SkriptValidationBridge.setLastResult(result);

            if (!result.isSuccess()) {
                List<String> summaries = result.getErrors().stream()
                        .map(ValidationError::toString)
                        .collect(Collectors.toList());
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
