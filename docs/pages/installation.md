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

## Build artifact

```bash
./gradlew build
```

Expected output artifact:

- `build/libs/Schema-Validator-<version>.jar`

## Deploy

1. Stop the server.
2. Copy the built JAR to `plugins/`.
3. Confirm Skript is present as a hard dependency.
4. Start the server.

## Verify startup

After startup, confirm logs for:

- Plugin enable success
- Configuration load
- Schema autoload summary

## Troubleshooting

### Plugin does not load

- Confirm Java version and server compatibility.
- Confirm Skript is installed and enabled.

### Schemas are not discovered

- Check `schema-directory` in `plugins/Schema-Validator/config.yml`.
- Confirm files are under the expected plugin data folder.

## Next task

Proceed to [Configuration](configuration.html) to tune runtime behavior.
