---
title: Schema directory workflow
description: Configure startup schema autoload, verify registration, and avoid common runtime pitfalls.
doc_type: how-to
order: 4
sequence: 8
permalink: /schema-directory-workflow.html
---

## Goal

Load schemas automatically at startup from one configured directory.

## Steps

1. Set `schema-directory` in `config.yml`.
2. Keep `auto-load: true`.
3. Place schema files in that directory (`.json`, `.yml`, `.yaml`).
4. Restart server and verify logs.

## What startup does

- Creates schema directory if missing.
- Scans files by extension (non-recursive).
- Registers schema names as filename without extension.
- Optionally runs startup self-check when `validation-on-load: true`.

## Reload behavior

`/sv reload --all` uses the same configured schema directory and updates or adds schemas found there.

Current limitation:

- Reload does not remove unrelated schemas that were registered from other sources.
- Single-schema reload depends on the schema having a file-backed registration source.

## Verification checklist

- Startup log shows directory path used.
- Loaded schema count is greater than zero.
- `/sv list` shows the loaded schemas.
- `/sv validate-file` can resolve and execute against one loaded schema.

## Related pages

- Administrative inspection and reload: [Commands](commands.html)
- Key behavior and defaults: [Config reference](config-reference.html)
- Startup architecture details: [Architecture](architecture.html)
