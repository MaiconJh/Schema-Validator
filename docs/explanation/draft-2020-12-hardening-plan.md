# Draft 2020-12 Hardening Plan (Post P1/P2)

This plan captures follow-up hardening after the first implementation pass of P1/P2 keywords.

## Goals

1. Improve semantic correctness for `unevaluated*` and dynamic references.
2. Strengthen validator behavior around item/property evaluation boundaries.
3. Expand regression coverage for newly introduced keywords.

## Phase H1 — Semantic corrections

- [x] Ensure `unevaluatedItems` is enforced even when `items`/`prefixItems` are absent.
- [x] Mark items evaluated by `additionalItems` to avoid false `unevaluatedItems` violations.
- [x] Add dedicated resolution path for `$dynamicRef` anchor form (e.g. `#node`).

## Phase H2 — Regression tests

- [x] Add array regression test for `unevaluatedItems=false` without `items`/`prefixItems`.
- [x] Add dynamic anchor/ref resolution test (`$dynamicAnchor` + `$dynamicRef`).
- [x] Add dynamicRef JSON-pointer fallback regression (`#/properties/...`).
- [x] Add `additionalItems` + `unevaluatedItems` interplay regression.

## Next hardening candidates

- [ ] Full spec-accurate dynamic scope stack across nested applicators.
- [ ] Full annotation-collection semantics for unevaluated tracking across `allOf`/`anyOf`/`oneOf`.
- [ ] Additional media types and encodings in content vocabulary.
