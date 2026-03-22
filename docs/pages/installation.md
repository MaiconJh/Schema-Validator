---
title: Installation
description: Build, deploy, and verify Schema Validator on a Paper server with Skript.
doc_type: how-to
order: 1
sequence: 3
permalink: /installation.html
---

## Requirements

- Java 21 toolchain
- Paper server
- Skript plugin installed

> [!NOTE]
> Keep Java and Paper versions aligned with your production server to avoid runtime incompatibilities.

## Build artifact

From repository root:

```bash
./gradlew build
```

Expected output artifact:

- `build/libs/Schema-Validator-<version>.jar`

## Deploy

1. Stop the server.
2. Copy the built JAR to `plugins/`.
3. Start the server.
4. Confirm that `plugins/Schema-Validator/` was created.

## Verify startup

Check console logs for:

- config load summary (`schema-directory`, `auto-load`, `strict-mode`)
- Skript syntax registration
- final plugin enable line with loaded schema count

## Verify resources and baseline files

After first startup, verify these files exist:

- `plugins/Schema-Validator/config.yml`
- `plugins/Schema-Validator/schemas/`
- `plugins/Schema-Validator/examples/`

The bundled examples are useful for first validation runs and troubleshooting.

## Troubleshooting

> [!TIP]
> If the plugin fails after an update, keep the config folder and replace only the JAR first.

### Plugin does not enable

- Confirm Java 21 is used by the server process.
- Confirm Skript is installed and enabled before this plugin.
- Confirm no duplicate old plugin JAR remains in `plugins/`.

### Schemas are not loaded on startup

- Check `schema-directory` in `plugins/Schema-Validator/config.yml`.
- Confirm schema files use `.json`, `.yml`, or `.yaml`.
- If `strict-mode: true`, check logs for unsupported keyword exceptions.

### Build succeeded but runtime still loads old behavior

- Confirm you copied the latest JAR from `build/libs/`.
- Restart the server fully (not only `/reload`).

## Next task

Proceed to [Quickstart](quickstart.html) for an end-to-end validation flow.
