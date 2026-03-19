# Data Types

> Canonical behavior: [`../CONTRACT.md`](../CONTRACT.md).

## Supported types

- `string`
- `integer`
- `number`
- `boolean`
- `null`
- `array`
- `object`
- `any`

## Numeric semantics

### `integer`

Accepts numbers without fractional part.

Valid examples: `42`, `0`, `-10`, `2.0`

### `number`

Current implementation accepts Java `Number` values except `Integer` class instances.

Practical implication:
- decimal values are expected (`3.14`)
- plain integer instances are rejected for `number`

If you require whole numbers, use `integer`.

## Root validation caveat

Default validation path requires object-like root data. See contract for exact behavior.

## Not supported type forms

- Multi-type arrays in `type`, for example:

```json
{ "type": ["string", "integer"] }
```

This is not part of the implemented parser contract.
