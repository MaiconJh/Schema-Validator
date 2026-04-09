---
title: Commands
description: Administrative command reference for schema inspection, file validation, selective reload, export, and runtime stats.
doc_type: reference
order: 5
sequence: 14
permalink: /commands.html
---

## Command root

Schema-Validator registers:

```text
/schemavalidator
/sv
/schema
```

## Permissions

- `schemavalidator.use`: read-only commands
- `schemavalidator.reload`: directory reload command
- `schemavalidator.admin`: all commands

All permissions default to operators.

## Available subcommands

### Help

```text
/sv help
```

Shows the command list available to the sender.

### List

```text
/sv list [page]
```

Lists registered schemas in sorted order with type, schema version, and registration source when available.

### Info

```text
/sv info <schemaName>
```

Shows schema metadata currently available from the in-memory model:

- type
- version
- title
- description
- property count
- required field count
- pattern property count
- registration source
- registration timestamp
- source path when available
- detected keyword groups

### Validate file

```text
/sv validate-file <schemaName> <path> [--verbose]
```

Validates a JSON or YAML file against a registered schema.

Operational notes:

- Supported data file extensions: `.json`, `.yml`, `.yaml`
- This command parses the payload as generic JSON/YAML data, so object, array, and primitive roots are supported
- Without `--verbose`, only the first validation error is shown
- With `--verbose`, all collected validation errors are shown

### Reload

```text
/sv reload <schemaName>
/sv reload --all
```

Reloads one file-backed schema or updates all schemas from the configured schema directory and validates data files.

Current behavior:

- Reload reads the current `config.yml`
- Reload reapplies `cache-enabled` and `strict-mode`
- `reload <schemaName>` requires the schema to have a file-backed source path
- `reload --all` updates or adds schemas from the schema directory
- `reload --all` also validates data files from the data directory (if `auto-validate-data-files: true`)
- Reload does not remove schemas that were registered from other sources

### Export

```text
/sv export <schemaName> [json|yaml]
```

Exports a file-backed registered schema to `plugins/Schema-Validator/exports/`.

Current behavior:

- Export uses the registered source file as input
- Export supports `json` and `yaml`
- Schemas without a file-backed source cannot be exported through this command

### Stats

```text
/sv stats
```

Shows:

- configured schema directory
- cache status
- total registered schemas
- file-backed schema count
- registration counts by source
- validation totals, successes, failures
- validation counts by origin (`api`, `command`, `skript`)
- average validation time

Metrics scope:

- Stats include validations executed through `SchemaValidatorAPI`, built-in commands, and the built-in Skript integration
- Direct external use of `getValidationService()` is not tracked in these counters

## Examples

```text
/sv list
/sv info player
/sv validate-file player plugins/MyPlugin/data/player.yml
/sv validate-file player plugins/MyPlugin/data/player.yml --verbose
/sv export player json
/sv stats
/sv reload player
/sv reload --all
```

## Related pages

- Startup and runtime wiring: [Architecture](architecture.html)
- Public plugin integration facade: [Java API](java-api.html)
- Optional syntax-based integration: [Skript API](skript-api.html)
