# Schema-Validator v1.0.0 — Verificação de Implementação Draft 2020-12

## Objetivo

Este documento verifica o status real de implementação das features marcadas como "✅ Completed" no audit original `draft-2020-12-feature-gap-audit.md`, confirmando o que está de fato implementado no código-fonte.

---

## Resultado Geral

**Todas as features marcadas como completas no phase status snapshot estão de fato implementadas**, com uma única exceção noteda abaixo.

---

## Verificação Detalhada por Feature

### P0 - Alto Impacto

| Feature | Claim do Audit | Status Real | Evidence |
|---|---|---|---|
| `propertyNames` | ✅ Completed | ✅ Implementado | `Schema.java:63` - campo `propertyNamesSchema` existe; `ObjectValidator.java:264-273` - validação implementada; `FileSchemaLoader.java` - parsing presente |
| `contains`/`minContains`/`maxContains` | ✅ Completed | ✅ Implementado | `Schema.java:54-56` - campos `containsSchema`, `minContains`, `maxContains`; `ArrayValidator.java:64-94` - lógica completa de validação com default `minContains=1` |
| Registry sincronização | ✅ Completed | ✅ Implementado | `SupportedKeywordsRegistry.java` contém: `prefixItems` (linha 100), `dependentRequired` (90), `dependentSchemas` (91), `$defs` (134), `$comment` (151) |

### P1 - Médio Impacto

| Feature | Claim do Audit | Status Real | Evidence |
|---|---|---|---|
| `unevaluatedProperties` | ✅ Completed | ✅ Implementado | `Schema.java:64-65` - campos `unevaluatedPropertiesAllowed`, `unevaluatedPropertiesSchema`; `ObjectValidator.java:345-360` - tracking e validação |
| `unevaluatedItems` | ✅ Completed | ✅ Implementado | `Schema.java:77-78` - campos `unevaluatedItemsAllowed`, `unevaluatedItemsSchema`; `ArrayValidator.java:127-147` - tracking e validação |
| `$dynamicRef`/`$dynamicAnchor` | ✅ Completed | ✅ Implementado | `Schema.java:79-80` - campos `dynamicRef`, `dynamicAnchor`; `ObjectValidator.java:57-66` - resolução via `SchemaRefResolver`; `FileSchemaLoader.java:65,90` - parsing |
| Content vocabulary | ✅ Completed | ✅ Implementado | `Schema.java:74-76` - campos `contentEncoding`, `contentMediaType`, `contentSchema`; `SupportedKeywordsRegistry.java:153-155` - registrado; `FileSchemaLoader.java:369-372` - parsing; `PrimitiveValidator.java:149-183` - validação |

### P2 - Baixo Impacto

| Feature | Claim do Audit | Status Real | Evidence |
|---|---|---|---|
| Metadata (`default`, `examples`, `deprecated`) | ✅ Completed | ✅ Implementado | `Schema.java:71-73` - campos `defaultValue`, `examples`, `deprecated`; getters presentes; `FileSchemaLoader.java` - parsing implementado |

---

## Verificação de Lógica de Implementação

Esta seção detalha a análise da lógica de cada implementação, verificando conformidade com a especificação JSON Schema Draft 2020-12.

### 1. `propertyNames` - Lógica Verificada ✅

**Implementação:** `ObjectValidator.java:264-273`

**Análise:**
- ✅ Cada chave do objeto é validada contra o schema
- ✅ Usa `ValidatorDispatcher` para permitir qualquer tipo de validação (string, pattern, enum, etc.)
- ✅ Path correto: `path + "." + key`
- ✅ keyword correta no erro: `"propertyNames"`

**Cobertura de Tests:** `ObjectValidatorTest.java:461-495`

---

### 2. `contains`/`minContains`/`maxContains` - Lógica Verificada ✅

**Implementação:** `ArrayValidator.java:64-94`

**Análise:**
- ✅ Itera todos os elementos e conta matches
- ✅ Marca itens evaluated corretamente (`evaluatedItems[i] = true`)
- ✅ Default `minContains=1` aplicado no parsing (`FileSchemaLoader.java:306`)
- ✅ Erros reportados com path, expectedType e description corretos

**Cobertura de Tests:** `ArrayValidatorTest.java:211-386`

---

### 3. `unevaluatedProperties` - Lógica Verificada ✅

**Implementação:** `ObjectValidator.java:345-360`

**Helper:** `collectEvaluatedObjectKeys()` - método dedicado para coletar chaves avaliadas por:
- properties (diretas)
- patternProperties (via regex)
- allOf (sempre)
- anyOf/oneOf (apenas branch bem-sucedida)
- if/then/else (branch selecionada)

**Análise:**
- ✅ Tracking completo de chaves avaliadas
- ✅ allOf sempre conta (correto)
- ✅ anyOf/oneOf só conta se matched (correto)
- ✅ conditional branching correto

**Cobertura de Tests:** `ObjectValidatorTest.java:47-103`

---

### 4. `unevaluatedItems` - Lógica Verificada ✅

**Implementação:** `ArrayValidator.java:127-147`

**Análise:**
- ✅ Array de boolean tracking por índice
- ✅ Merge de evaluated de allOf/anyOf/oneOf/conditional
- ✅ contains marca matches como evaluated

**Cobertura de Tests:** `ArrayValidatorTest.java:389-449`

---

### 5. `$dynamicRef`/`$dynamicAnchor` - Lógica Verificada ✅

**Implementação:** `ObjectValidator.java:57-66` + `SchemaRefResolver.java:177-218`

**Resolução:**
1. Primeiro tenta dynamic scope stack (nearest scope wins)
2. Fallback para current schema + registry

**Análise:**
- ✅ Dynamic scope stack tracking (`enterDynamicScope`/`exitDynamicScope`)
- ✅ Resolução via dynamic anchor
- ✅ Fallback para $ref se schema resolver para outro $ref

**Cobertura de Tests:** `ObjectValidatorTest.java:65-81`, `SchemaRefResolverTest.java:265-303`

---

### 6. Content Vocabulary - Lógica Verificada ✅

**Implementação:** `PrimitiveValidator.java:149-186`

**Análise:**
- ✅ Suporta apenas `base64` e `base64url` (outros encodings não validados)
- ✅ `contentSchema` só aplica para JSON media types (`application/json`, `*+json`)
- ✅ Decodifica antes de validar se encoding presente

**Cobertura de Tests:** `PrimitiveValidatorTest.java:374-435`

---

## Gaps/Bugs Identificados

### Gap 1: `contains` com valor booleano (SPEC VIOLATION) - Medium

**Especificação:** JSON Schema permite `contains` como schema booleano (não apenas objeto). Se `contains: true`, qualquer array passa; se `contains: false`, nenhuma array passa.

**Status atual:** Não suportado. O código assume `contains` sempre como Schema object (`Map`).

**Arquivo:** `FileSchemaLoader.java:304-307`

**Impacto:** Schema `{"contains": true}` não será parseado corretamente.

---

### Gap 2: `minContains=0` não possível - Low

**Problema:** `minContains` default é 1, mas não há como desabilitar o comportamento `contains` (equivalent a `minContains=0`).

**Workaround:** Usar `if/then/else` para Esquiva, mas não é limpo.

---

### Gap 3: Content Vocabulary - encoding limitado - Low

**Problema:** Apenas `base64` e `base64url` são validados. Outros encodings são ignorados silenciosamente.

**Impacto:** Baixo - spec diz que format annotation é opt-in

---

## Exceção Identificada

### `$comment` - Parcialmente Implementado

| Aspecto | Status |
|---|---|
| Registry | ✅ Presente |
| Model/Schema | ❌ Não existe campo |
| Parser/Loader | ❌ Não é parseado |

**Severity:** Baixa - keyword de anotação

---

## Conclusão

O codebase está **alinhado com as claims** do phase status snapshot.

**Gaps encontrados:**
- 1 Medium: `contains` boolean schema não suportado
- 2 Low: `minContains=0` não possível, content encoding limitado

**Recomendação:**
1. Adicionar suporte a `contains: boolean` no parsing
2. Adicionar campo `comment` ao Schema para consistência
