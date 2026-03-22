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

## Known caveat

`autoLoadSchemas()` logs `failedCount` in summary, but current implementation does not increment this counter. Failed files are still logged individually as warnings.

## Verification checklist

- Startup log shows directory path used.
- Loaded schema count is greater than zero.
- A manual validation command can resolve and execute against one loaded schema.

## Related pages

- Key behavior and defaults: [Config reference](config-reference.html)
- Startup architecture details: [Architecture](architecture.html)
