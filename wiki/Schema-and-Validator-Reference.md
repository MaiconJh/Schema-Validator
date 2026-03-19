# Schema and Validator Reference

> Canonical reference: [`docs/reference/json-schema.md`](../docs/reference/json-schema.md) and [`docs/CONTRACT.md`](../docs/CONTRACT.md).

This page is intentionally concise and links to canonical `/docs` pages.

## Implemented keyword groups

- **Object**: `properties`, `required`, `additionalProperties`, `patternProperties`, `allOf`, `anyOf`
- **Array**: `items`
- **Primitive constraints**: `minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`, `minLength`, `maxLength`, `pattern`, `format`, `enum`
- **Reference (partial)**: `$ref` (requires resolver wiring via `ValidationService(refResolver)`)

## Important runtime constraint

Default `ValidationService()` validates with `ObjectValidator` as root. Root data must be object-like in default usage paths (including Skript effect).

## Explicitly not implemented (planned/experimental)

- `minItems`
- `maxItems`
- `uniqueItems`
- `minProperties`
- `maxProperties`
- `dependencies`
- multi-type arrays in `type` (e.g., `"type": ["string", "integer"]`)

## Failure semantics

- Failed implemented constraints produce validation errors.
- `format` failures are hard errors.
- Unsupported keywords are ignored by runtime.

## Canonical deep links

- Contract list of implemented/non-implemented features: [`docs/CONTRACT.md`](../docs/CONTRACT.md)
- Detailed keyword examples: [`docs/reference/json-schema.md`](../docs/reference/json-schema.md)
- Type behavior: [`docs/reference/data-types.md`](../docs/reference/data-types.md)
