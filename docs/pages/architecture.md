---
title: Architecture
description: Understand runtime components, integration boundaries, and validation data flow.
doc_type: explanation
order: 2
sequence: 9
permalink: /architecture.html
---

## Main components

- `SchemaValidatorPlugin`: lifecycle and startup wiring.
- `PluginConfig`: configuration parsing and defaults.
- `FileSchemaLoader`: schema parsing and unsupported keyword checks.
- `SchemaRegistry`: schema storage and retrieval.
- `ValidationService`: dispatch and validation result assembly.
- Validators: `ObjectValidator`, `ArrayValidator`, `PrimitiveValidator`.

## Runtime flow

1. Plugin enables.
2. Config is loaded.
3. Registry and schema loader are initialized.
4. Optional schema autoload runs.
5. Skript syntax is registered.
6. Validation requests are executed and result is stored in bridge state.

## Skript integration boundary

- `EffValidateData` handles effect execution.
- `ExprLastValidationErrors` exposes current error list.
- `SkriptValidationBridge` stores latest validation result.

## Known constraints

- Skript path currently loads data as object root (`Map<String, Object>`).
- `$ref` resolution requires resolver wiring in validation service.
- Some parsed keywords are not yet enforced at runtime.

## Related pages

- Interface and behavior details: [Validation behavior](validation-behavior.html)
- Supported keyword scope: [Schema keywords](schema-keywords.html)
