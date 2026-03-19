# JSON Schema Keyword Reference (Implemented Subset)

> Canonical contract: [`../CONTRACT.md`](../CONTRACT.md).

This project implements a **subset** of JSON Schema-like behavior.

## Keyword status matrix

| Keyword | Status | Validator path | Failure semantics |
|---|---|---|---|
| `type` | Implemented | `ValidatorDispatcher` | validation error |
| `properties` | Implemented | `ObjectValidator` | validation error |
| `required` | Implemented | `ObjectValidator` | validation error |
| `additionalProperties` | Implemented | `ObjectValidator` | validation error |
| `patternProperties` | Implemented | `ObjectValidator` | validation error |
| `items` | Implemented | `ArrayValidator` | validation error |
| `minimum` / `maximum` | Implemented | `PrimitiveValidator` | validation error |
| `exclusiveMinimum` / `exclusiveMaximum` | Implemented | `PrimitiveValidator` | validation error |
| `multipleOf` | Implemented | `PrimitiveValidator` | validation error |
| `minLength` / `maxLength` | Implemented | `PrimitiveValidator` | validation error |
| `pattern` | Implemented | `PrimitiveValidator` | validation error |
| `format` | Implemented | `PrimitiveValidator` + `FormatValidator` | validation error |
| `enum` | Implemented | `PrimitiveValidator` | validation error |
| `allOf` | Implemented | `ObjectValidator` | validation error |
| `anyOf` | Implemented | `ObjectValidator` | validation error |
| `$ref` | Partial | `ObjectValidator` + `SchemaRefResolver` | validation error when unresolved |
| `minItems` / `maxItems` / `uniqueItems` | Not implemented | N/A | ignored |
| `minProperties` / `maxProperties` / `dependencies` | Not implemented | N/A | ignored |

## Important behavior notes

1. `format` failures are hard errors (not warnings).
2. `$ref` requires resolver wiring (`new ValidationService(refResolver)`).
3. Default validation entrypoint expects object-like root data.

## Executable examples

### Object with required fields

```json
{
  "type": "object",
  "properties": {
    "name": { "type": "string", "minLength": 1 },
    "level": { "type": "integer", "minimum": 1 }
  },
  "required": ["name", "level"],
  "additionalProperties": false
}
```

### String format enforcement

```json
{
  "type": "object",
  "properties": {
    "email": { "type": "string", "format": "email" }
  },
  "required": ["email"]
}
```

### Array item validation (supported)

```json
{
  "type": "array",
  "items": { "type": "string" }
}
```

### Array count/uniqueness (not supported)

```json
{
  "type": "array",
  "items": { "type": "string" },
  "minItems": 1,
  "uniqueItems": true
}
```

`minItems` and `uniqueItems` are currently ignored.
