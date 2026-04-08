package com.maiconjh.schemacr.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.maiconjh.schemacr.core.SchemaValidatorPlugin;
import com.maiconjh.schemacr.core.ValidationMetrics;
import com.maiconjh.schemacr.core.ValidationOrigin;
import com.maiconjh.schemacr.schemes.RegisteredSchemaMetadata;
import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaRegistrationSource;
import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.ValidationResult;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Administrative command entry point for Schema-Validator.
 */
public class SchemaValidatorCommand implements CommandExecutor, TabCompleter {

    private static final int PAGE_SIZE = 10;
    private static final String PERMISSION_ADMIN = "schemavalidator.admin";
    private static final String PERMISSION_USE = "schemavalidator.use";
    private static final String PERMISSION_RELOAD = "schemavalidator.reload";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private final SchemaValidatorPlugin plugin;
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public SchemaValidatorCommand(SchemaValidatorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return handleHelp(sender, label);
        }

        String subcommand = args[0].toLowerCase(Locale.ROOT);
        return switch (subcommand) {
            case "help" -> handleHelp(sender, label);
            case "list" -> handleList(sender, label, args);
            case "info" -> handleInfo(sender, label, args);
            case "validate-file" -> handleValidateFile(sender, label, args);
            case "export" -> handleExport(sender, label, args);
            case "stats" -> handleStats(sender, label);
            case "reload" -> handleReload(sender, label, args);
            default -> {
                sender.sendMessage(ChatColor.RED + "Unknown subcommand: " + args[0]);
                yield handleHelp(sender, label);
            }
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            if (canUse(sender)) {
                suggestions.add("help");
                suggestions.add("list");
                suggestions.add("info");
                suggestions.add("validate-file");
                suggestions.add("export");
                suggestions.add("stats");
            }
            if (canReload(sender)) {
                suggestions.add("reload");
            }
            return filterSuggestions(suggestions, args[0]);
        }

        if (args.length == 2) {
            return switch (args[0].toLowerCase(Locale.ROOT)) {
                case "info", "validate-file", "export" -> filterSuggestions(sortedSchemaNames(), args[1]);
                case "reload" -> canReload(sender)
                        ? filterSuggestions(combineReloadSuggestions(), args[1])
                        : List.of();
                default -> List.of();
            };
        }

        if (args.length == 3 && "export".equalsIgnoreCase(args[0])) {
            return filterSuggestions(List.of("json", "yaml"), args[2]);
        }

        if (args.length >= 3 && "validate-file".equalsIgnoreCase(args[0])) {
            return filterSuggestions(List.of("--verbose"), args[args.length - 1]);
        }

        return List.of();
    }

    private boolean handleHelp(CommandSender sender, String label) {
        if (!canAny(sender)) {
            return deny(sender);
        }

        sender.sendMessage(ChatColor.GOLD + "Schema-Validator commands:");
        if (canUse(sender)) {
            sender.sendMessage(ChatColor.YELLOW + "/" + label + " list [page]" + ChatColor.GRAY + " - List registered schemas.");
            sender.sendMessage(ChatColor.YELLOW + "/" + label + " info <schemaName>" + ChatColor.GRAY + " - Show schema details.");
            sender.sendMessage(ChatColor.YELLOW + "/" + label + " validate-file <schemaName> <path> [--verbose]"
                    + ChatColor.GRAY + " - Validate a JSON/YAML file.");
            sender.sendMessage(ChatColor.YELLOW + "/" + label + " export <schemaName> [json|yaml]"
                    + ChatColor.GRAY + " - Export a file-backed schema to plugin exports.");
            sender.sendMessage(ChatColor.YELLOW + "/" + label + " stats"
                    + ChatColor.GRAY + " - Show registry and validation metrics.");
        }
        if (canReload(sender)) {
            sender.sendMessage(ChatColor.YELLOW + "/" + label + " reload --all"
                    + ChatColor.GRAY + " - Reload schemas from the configured schema directory.");
            sender.sendMessage(ChatColor.YELLOW + "/" + label + " reload <schemaName>"
                    + ChatColor.GRAY + " - Reload one file-backed registered schema.");
        }
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " help" + ChatColor.GRAY + " - Show this help.");
        return true;
    }

    private boolean handleList(CommandSender sender, String label, String[] args) {
        if (!canUse(sender)) {
            return deny(sender);
        }

        int page = 1;
        if (args.length >= 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " list [page]");
                return true;
            }
        }

        if (page < 1) {
            sender.sendMessage(ChatColor.RED + "Page must be 1 or greater.");
            return true;
        }

        List<String> schemaNames = sortedSchemaNames();
        if (schemaNames.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No schemas are currently registered.");
            return true;
        }

        int totalPages = Math.max(1, (int) Math.ceil(schemaNames.size() / (double) PAGE_SIZE));
        if (page > totalPages) {
            sender.sendMessage(ChatColor.RED + "Page " + page + " does not exist. Total pages: " + totalPages + ".");
            return true;
        }

        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, schemaNames.size());
        sender.sendMessage(ChatColor.GOLD + "Registered schemas (" + schemaNames.size() + ") - page "
                + page + "/" + totalPages + ":");

        for (String schemaName : schemaNames.subList(start, end)) {
            Schema schema = plugin.getSchemaRegistry().getSchema(schemaName).orElse(null);
            RegisteredSchemaMetadata metadata = plugin.getSchemaRegistry().getSchemaMetadata(schemaName).orElse(null);
            if (schema == null) {
                continue;
            }

            String version = schema.getVersion() == null || schema.getVersion().isBlank()
                    ? "n/a"
                    : schema.getVersion();
            sender.sendMessage(ChatColor.YELLOW + "- " + schemaName
                    + ChatColor.GRAY + " [" + schema.getType().name().toLowerCase(Locale.ROOT) + "]"
                    + " v" + version
                    + " source=" + formatSource(metadata));
        }
        return true;
    }

    private boolean handleInfo(CommandSender sender, String label, String[] args) {
        if (!canUse(sender)) {
            return deny(sender);
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " info <schemaName>");
            return true;
        }

        String schemaName = args[1];
        Schema schema = plugin.getSchemaRegistry().getSchema(schemaName).orElse(null);
        RegisteredSchemaMetadata metadata = plugin.getSchemaRegistry().getSchemaMetadata(schemaName).orElse(null);
        if (schema == null) {
            sender.sendMessage(ChatColor.RED + "Unknown schema: " + schemaName);
            return true;
        }

        sender.sendMessage(ChatColor.GOLD + "Schema info: " + ChatColor.YELLOW + schemaName);
        sender.sendMessage(ChatColor.GRAY + "Type: " + ChatColor.WHITE + schema.getType().name().toLowerCase(Locale.ROOT));
        sender.sendMessage(ChatColor.GRAY + "Version: " + ChatColor.WHITE + valueOrFallback(schema.getVersion(), "n/a"));
        sender.sendMessage(ChatColor.GRAY + "Title: " + ChatColor.WHITE + valueOrFallback(schema.getTitle(), "n/a"));
        sender.sendMessage(ChatColor.GRAY + "Description: " + ChatColor.WHITE + valueOrFallback(schema.getDescription(), "n/a"));
        sender.sendMessage(ChatColor.GRAY + "Properties: " + ChatColor.WHITE + schema.getProperties().size());
        sender.sendMessage(ChatColor.GRAY + "Required fields: " + ChatColor.WHITE + schema.getRequiredFields().size());
        sender.sendMessage(ChatColor.GRAY + "Pattern properties: " + ChatColor.WHITE + schema.getPatternProperties().size());
        if (metadata != null) {
            sender.sendMessage(ChatColor.GRAY + "Registered from: " + ChatColor.WHITE + formatSource(metadata));
            sender.sendMessage(ChatColor.GRAY + "Registered at: " + ChatColor.WHITE
                    + TIMESTAMP_FORMATTER.format(Instant.ofEpochMilli(metadata.registeredAtEpochMillis())));
            sender.sendMessage(ChatColor.GRAY + "Source path: " + ChatColor.WHITE
                    + (metadata.sourcePath() == null ? "n/a" : metadata.sourcePath().toString()));
        }
        sender.sendMessage(ChatColor.GRAY + "Keywords: " + ChatColor.WHITE + String.join(", ", describeKeywords(schema)));
        return true;
    }

    private boolean handleValidateFile(CommandSender sender, String label, String[] args) {
        if (!canUse(sender)) {
            return deny(sender);
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " validate-file <schemaName> <path> [--verbose]");
            return true;
        }

        String schemaName = args[1];
        Schema schema = plugin.getSchemaRegistry().getSchema(schemaName).orElse(null);
        if (schema == null) {
            sender.sendMessage(ChatColor.RED + "Unknown schema: " + schemaName);
            return true;
        }

        boolean verbose = false;
        List<String> pathParts = new ArrayList<>();
        for (int i = 2; i < args.length; i++) {
            if ("--verbose".equalsIgnoreCase(args[i])) {
                verbose = true;
            } else {
                pathParts.add(args[i]);
            }
        }

        if (pathParts.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " validate-file <schemaName> <path> [--verbose]");
            return true;
        }

        Path path = Path.of(String.join(" ", pathParts));
        try {
            Object data = loadDataFromFile(path);
            ValidationResult result = plugin.validateTracked(data, schema, ValidationOrigin.COMMAND);
            if (result.isSuccess()) {
                sender.sendMessage(ChatColor.GREEN + "Validation passed for schema '" + schemaName + "'.");
                return true;
            }

            List<ValidationError> errors = result.getErrors();
            sender.sendMessage(ChatColor.RED + "Validation failed with " + errors.size() + " error(s).");

            List<ValidationError> toDisplay = verbose ? errors : List.of(errors.getFirst());
            for (ValidationError error : toDisplay) {
                sender.sendMessage(ChatColor.GRAY + "- " + error.toCompactString());
            }

            if (!verbose && errors.size() > 1) {
                sender.sendMessage(ChatColor.YELLOW + "Use --verbose to show all validation errors.");
            }
        } catch (IOException ex) {
            sender.sendMessage(ChatColor.RED + "Could not read data file: " + ex.getMessage());
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "Validation failed unexpectedly: " + ex.getMessage());
            plugin.getLogger().warning("Command validation failure for path '" + path + "': " + ex.getMessage());
        }
        return true;
    }

    private boolean handleExport(CommandSender sender, String label, String[] args) {
        if (!canUse(sender)) {
            return deny(sender);
        }

        if (args.length < 2 || args.length > 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " export <schemaName> [json|yaml]");
            return true;
        }

        String schemaName = args[1];
        RegisteredSchemaMetadata metadata = plugin.getSchemaRegistry().getSchemaMetadata(schemaName).orElse(null);
        if (metadata == null) {
            sender.sendMessage(ChatColor.RED + "Unknown schema: " + schemaName);
            return true;
        }
        if (metadata.sourcePath() == null) {
            sender.sendMessage(ChatColor.RED + "Schema '" + schemaName + "' is not file-backed and cannot be exported.");
            return true;
        }

        String requestedFormat = args.length == 3 ? args[2].toLowerCase(Locale.ROOT) : inferFormat(metadata.sourcePath());
        if (!"json".equals(requestedFormat) && !"yaml".equals(requestedFormat)) {
            sender.sendMessage(ChatColor.RED + "Supported export formats: json, yaml");
            return true;
        }

        Path exportsDir = plugin.getDataFolder().toPath().resolve("exports");
        Path exportPath = exportsDir.resolve(schemaName + ".schema." + ("json".equals(requestedFormat) ? "json" : "yml"));

        try {
            Files.createDirectories(exportsDir);
            Object rawSchema = readStructuredFile(metadata.sourcePath());
            if ("json".equals(requestedFormat)) {
                jsonMapper.writerWithDefaultPrettyPrinter().writeValue(exportPath.toFile(), rawSchema);
            } else {
                yamlMapper.writeValue(exportPath.toFile(), rawSchema);
            }
            sender.sendMessage(ChatColor.GREEN + "Exported schema '" + schemaName + "' to " + exportPath + ".");
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "Failed to export schema: " + ex.getMessage());
            plugin.getLogger().warning("Schema export failure for '" + schemaName + "': " + ex.getMessage());
        }
        return true;
    }

    private boolean handleStats(CommandSender sender, String label) {
        if (!canUse(sender)) {
            return deny(sender);
        }

        ValidationMetrics metrics = plugin.getValidationMetrics();
        List<RegisteredSchemaMetadata> metadata = plugin.getSchemaRegistry().getAllSchemaMetadata().stream()
                .sorted(Comparator.comparing(RegisteredSchemaMetadata::name, String.CASE_INSENSITIVE_ORDER))
                .toList();

        long fileBacked = metadata.stream().filter(item -> item.sourcePath() != null).count();
        long autoLoaded = countBySource(metadata, SchemaRegistrationSource.AUTOLOAD);
        long apiRegistered = countBySource(metadata, SchemaRegistrationSource.API);
        long skriptRegistered = countBySource(metadata, SchemaRegistrationSource.SKRIPT);
        long unknownRegistered = countBySource(metadata, SchemaRegistrationSource.UNKNOWN);

        sender.sendMessage(ChatColor.GOLD + "Schema-Validator stats:");
        sender.sendMessage(ChatColor.GRAY + "Configured schema directory: " + ChatColor.WHITE
                + plugin.getPluginConfig().getSchemaDirectory());
        sender.sendMessage(ChatColor.GRAY + "Cache enabled: " + ChatColor.WHITE + plugin.getSchemaRegistry().isCacheEnabled());
        sender.sendMessage(ChatColor.GRAY + "Registered schemas: " + ChatColor.WHITE + plugin.getSchemaRegistry().getSchemaCount());
        sender.sendMessage(ChatColor.GRAY + "File-backed schemas: " + ChatColor.WHITE + fileBacked);
        sender.sendMessage(ChatColor.GRAY + "Registration sources: " + ChatColor.WHITE
                + "autoload=" + autoLoaded
                + ", api=" + apiRegistered
                + ", skript=" + skriptRegistered
                + ", unknown=" + unknownRegistered);
        sender.sendMessage(ChatColor.GRAY + "Validations: " + ChatColor.WHITE
                + "total=" + metrics.getTotalValidations()
                + ", success=" + metrics.getSuccessfulValidations()
                + ", failed=" + metrics.getFailedValidations());
        sender.sendMessage(ChatColor.GRAY + "Validation origins: " + ChatColor.WHITE
                + "api=" + metrics.getCountForOrigin(ValidationOrigin.API)
                + ", command=" + metrics.getCountForOrigin(ValidationOrigin.COMMAND)
                + ", skript=" + metrics.getCountForOrigin(ValidationOrigin.SKRIPT));
        sender.sendMessage(ChatColor.GRAY + "Average validation time: " + ChatColor.WHITE
                + String.format(Locale.ROOT, "%.3f ms", metrics.getAverageValidationMillis()));
        return true;
    }

    private boolean handleReload(CommandSender sender, String label, String[] args) {
        if (!canReload(sender)) {
            return deny(sender);
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " reload <schemaName|--all>");
            return true;
        }

        if ("--all".equalsIgnoreCase(args[1])) {
            SchemaValidatorPlugin.SchemaReloadSummary summary = plugin.reloadSchemasFromConfiguredDirectory();
            sender.sendMessage(ChatColor.GREEN + "Reload complete from " + summary.schemaDirectory() + ".");
            sender.sendMessage(ChatColor.GRAY + "Loaded/updated schemas: " + summary.loadedCount());
            sender.sendMessage(ChatColor.GRAY + "Failed loads: " + summary.failedCount());
            sender.sendMessage(ChatColor.GRAY + "Registry size now: " + plugin.getSchemaRegistry().getSchemaCount());
            sender.sendMessage(ChatColor.YELLOW + "Note: reload --all updates schemas from the configured directory and keeps other registered schemas.");
            return true;
        }

        String schemaName = args[1];
        SchemaValidatorPlugin.SchemaSingleReloadResult result = plugin.reloadSchema(schemaName);
        if (result.success()) {
            sender.sendMessage(ChatColor.GREEN + "Reloaded schema '" + schemaName + "' from " + result.sourcePath() + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "Could not reload schema '" + schemaName + "': " + result.errorMessage());
        }
        return true;
    }

    private Object loadDataFromFile(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new IOException("File does not exist: " + path);
        }

        return readStructuredFile(path);
    }

    private Object readStructuredFile(Path path) throws IOException {
        String lowerFileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
        if (lowerFileName.endsWith(".json")) {
            return jsonMapper.readValue(path.toFile(), Object.class);
        }
        if (lowerFileName.endsWith(".yml") || lowerFileName.endsWith(".yaml")) {
            return yamlMapper.readValue(path.toFile(), Object.class);
        }
        throw new IOException("Unsupported file extension. Use .json, .yml, or .yaml.");
    }

    private List<String> describeKeywords(Schema schema) {
        List<String> keywords = new ArrayList<>();
        if (!schema.getProperties().isEmpty()) keywords.add("properties");
        if (!schema.getPatternProperties().isEmpty()) keywords.add("patternProperties");
        if (!schema.getRequiredFields().isEmpty()) keywords.add("required");
        if (schema.hasAdditionalPropertiesSchema() || !schema.isAdditionalPropertiesAllowed()) keywords.add("additionalProperties");
        if (schema.hasFormat()) keywords.add("format");
        if (schema.isRef()) keywords.add("$ref");
        if (schema.getDynamicRef() != null) keywords.add("$dynamicRef");
        if (schema.hasAllOf()) keywords.add("allOf");
        if (schema.hasAnyOf()) keywords.add("anyOf");
        if (schema.hasOneOf()) keywords.add("oneOf");
        if (schema.hasNot()) keywords.add("not");
        if (schema.hasConditional()) keywords.add("if/then/else");
        if (schema.hasArrayConstraints()) keywords.add("array-constraints");
        if (schema.hasObjectConstraints()) keywords.add("object-constraints");
        if (schema.hasConst()) keywords.add("const");
        if (schema.hasReadWriteOnly()) keywords.add("readOnly/writeOnly");
        if (schema.hasContentVocabulary()) keywords.add("content");
        if (schema.hasTypeUnion()) keywords.add("type-union");
        return keywords.isEmpty() ? List.of("type") : keywords;
    }

    private List<String> sortedSchemaNames() {
        Set<String> names = plugin.getSchemaRegistry().getAllSchemaNames();
        return names.stream()
                .sorted(Comparator.naturalOrder())
                .toList();
    }

    private List<String> combineReloadSuggestions() {
        List<String> suggestions = new ArrayList<>(sortedSchemaNames());
        suggestions.add("--all");
        return suggestions;
    }

    private List<String> filterSuggestions(List<String> suggestions, String input) {
        String normalized = input.toLowerCase(Locale.ROOT);
        return suggestions.stream()
                .filter(value -> value.toLowerCase(Locale.ROOT).startsWith(normalized))
                .sorted()
                .toList();
    }

    private String valueOrFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String formatSource(RegisteredSchemaMetadata metadata) {
        if (metadata == null) {
            return "unknown";
        }
        String source = metadata.source().name().toLowerCase(Locale.ROOT);
        if (metadata.sourcePath() == null) {
            return source;
        }
        return source + "@" + metadata.sourcePath().getFileName();
    }

    private long countBySource(List<RegisteredSchemaMetadata> metadata, SchemaRegistrationSource source) {
        return metadata.stream().filter(item -> item.source() == source).count();
    }

    private String inferFormat(Path path) {
        String lower = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return lower.endsWith(".json") ? "json" : "yaml";
    }

    private boolean canAny(CommandSender sender) {
        return canUse(sender) || canReload(sender);
    }

    private boolean canUse(CommandSender sender) {
        return sender.hasPermission(PERMISSION_USE) || sender.hasPermission(PERMISSION_ADMIN);
    }

    private boolean canReload(CommandSender sender) {
        return sender.hasPermission(PERMISSION_RELOAD) || sender.hasPermission(PERMISSION_ADMIN);
    }

    private boolean deny(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
        return true;
    }
}
