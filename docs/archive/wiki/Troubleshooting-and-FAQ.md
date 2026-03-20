# Troubleshooting and FAQ

> Canonical references: [`docs/faq.md`](../docs/faq.md), [`docs/configuration.md`](../docs/configuration.md), [`docs/CONTRACT.md`](../docs/CONTRACT.md).

## Common issues

### Validation always fails with object-related errors

**Cause:** default validator path expects object-like root data.

**Fix:** ensure data root is a YAML/JSON object (mapping), not a bare array/string/number.

### `last schema validation errors` is always empty

**Cause:** validation may not be running due to bad file paths or script flow.

**Fix:** confirm both data and schema file paths are correct and the effect executes before reading the expression.

### `$ref` schema references do not resolve

**Cause:** default Skript path does not inject `SchemaRefResolver`.

**Fix:** use Java integration with `ValidationService(refResolver)` for `$ref`-dependent flows.

### Config changes are ignored

**Cause:** using unsupported legacy keys.

**Fix:** only use:

```yaml
schema-directory: "schemas"
auto-load: true
cache-enabled: true
validation-on-load: true
```

### Why are `minItems` and `uniqueItems` not enforced?

They are currently not implemented runtime features. Treat them as planned/experimental only.

## FAQ

### Is the wiki the source of truth?

No. `docs/CONTRACT.md` is the authoritative contract.

### Where is architecture documented?

See [`docs/architecture.md`](../docs/architecture.md) and the audit set in `/docs`.

### Where can I track consistency work?

See:

- [`docs/source-of-truth-audit-2026-03-19.md`](../docs/source-of-truth-audit-2026-03-19.md)
- [`docs/deep-system-audit-2026-03-19.md`](../docs/deep-system-audit-2026-03-19.md)
- [`wiki/Audit-Summary-2026-03-19.md`](Audit-Summary-2026-03-19)
