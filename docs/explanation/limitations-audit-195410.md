# Auditoria de Suporte JSON Schema — Schema-Validator

**Data da auditoria:** 2026-03-22 (UTC)  
**Última atualização:** 2026-03-22 (implementações concluídas)  
**Projeto auditado:** `Schema-Validator`  
**Base de comparação da especificação:** JSON Schema oficial em https://json-schema.org/ (principalmente drafts Draft-07, 2019-09 e 2020-12 para os itens solicitados).

## Metodologia

Esta auditoria foi feita por inspeção direta do código-fonte do plugin (parser, modelo e validadores), conferindo:

- quais keywords são apenas reconhecidas no parse;
- quais keywords são realmente **enforçadas** em runtime;
- quais comportamentos divergem da especificação oficial.

## Status: Implementações Concluídas

Todas as funcionalidades listadas abaixo foram implementadas:

- ✅ Resolução `$ref` com suporte completo a JSON Pointer
- ✅ Constraints de array (`minItems`, `maxItems`, `uniqueItems`, `prefixItems`, `items`)
- ✅ Constraints de objeto (`minProperties`, `maxProperties`, `dependencies`, `dependentRequired`, `dependentSchemas`, `additionalProperties` como schema)
- ✅ Suporte a `exclusiveMinimum`/`exclusiveMaximum` na forma numérica moderna
- ✅ Modelagem de metadados (`$schema`, `$id`, `title`, `description`)
- ✅ Suporte a arrays de tipos com dispatch runtime
- ✅ Suporte a keywords 2019-09/2020-12 (`$defs`, `prefixItems`, `dependentRequired`, `dependentSchemas`)

---

## Matriz detalhada por requisito

## 1) Formatos de schema suportados (`string`, `number`, `integer`, `boolean`, `array`, `object`, `null`)

**Status:** ✅ **Completo** (para validação de tipo)

### Evidências
- Parsing de `type` contempla explicitamente: `object`, `array`, `string`, `integer`, `number`, `boolean`, `null` (e fallback para `any`).
- O dispatcher encaminha `object` para `ObjectValidator`, `array` para `ArrayValidator` e demais tipos para `PrimitiveValidator`.
- `PrimitiveValidator` implementa checks concretos para `STRING`, `NUMBER`, `INTEGER`, `BOOLEAN`, `NULL`.

### Observações
- **`type` como array de tipos** agora é suportado com dispatch runtime por tipo de dado atual (ex.: `"type": ["string", "null"]`)

---

## 2) Validadores de `format` (`date`, `time`, `email`, `uri`, `hostname`, `ipv4`, `ipv6`, `uuid`, etc.)

**Status:** ⚠️ **Parcial**

### Formatos efetivamente implementados
O `FormatValidator` trata os seguintes formatos padrão:
- `date-time`, `date`, `time`, `duration`
- `email`, `idn-email`
- `hostname`, `idn-hostname`
- `ipv4`, `ipv6`
- `uri`, `uri-reference`, `uri-template`
- `json-pointer`, `relative-json-pointer`
- `uuid`, `regex`

Também há formatos customizados Minecraft (`minecraft-item`, `minecraft-block`, etc.).

### Limitações relevantes
- Para formato **desconhecido**, a validação retorna sucesso (`default -> true`), então não há erro para `format` não suportado.
- Implementações por regex podem divergir de corner cases RFC/ECMA esperados pela especificação.
- `idn-email` e `idn-hostname` reutilizam o mesmo regex de `email`/`hostname` (sem tratamento internacionalizado dedicado).

---

## 3) Definições e referências (`definitions`, `$defs`, `$ref` com `#/definitions/...` e `#/$defs/...`)

**Status:** ✅ **Completo**

### Implementado
- Loader extrai `definitions` e `$defs` em uma primeira passada.
- Parser captura `$ref` no schema.
- `SchemaRefResolver` para referências locais, externas por arquivo/URL e com pointer.
- Navegação completa de JSON Pointer (`navigateTo`) por:
  - Keywords (`properties`, `items`, `additionalProperties`)
  - Chaves de objeto (`properties/name`)
  - Índices de array (`prefixItems/0`, `allOf/1`)
- Suporte a `definitions` e `$defs` com resolução adequada
- Suporte a escaping `~0` (representa `~`) e `~1` (representa `/`)
- Indexação baseada em `$id` para resolução de referências externas

---

## 4) Operadores de composição (`allOf`, `anyOf`, `oneOf`, `not`)

**Status:** ✅ **Completo**

### Evidências
- Parsing explícito de `allOf`, `anyOf`, `oneOf`, `not`.
- `ObjectValidator` aplica:
  - `allOf`: exige todos válidos;
  - `anyOf`: exige ao menos um válido;
  - `oneOf`: exige exatamente um válido;
  - `not`: exige que schema interno **não** valide.

### Observações
- Existem classes específicas (`OneOfValidator`, `NotValidator`), mas o caminho principal já cobre esses operadores no `ObjectValidator`.

---

## 5) Validadores de string (`pattern`, `minLength`, `maxLength`, `format`)

**Status:** ✅ **Completo**

### Evidências
- Parsing: `minLength`, `maxLength`, `pattern`, `format`.
- Enforcement em `PrimitiveValidator`:
  - tamanho mínimo/máximo;
  - `pattern` com regex compilada;
  - `format` via `FormatValidator`.

### Limitações
- `pattern` usa `matcher.matches()` (match total); dependendo da interpretação do usuário, isso pode surpreender (muitos esperam busca parcial).
- Falha de compilação de regex no carregamento gera warning e ignora `pattern` inválido.

---

## 6) Validadores numéricos (`minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`)

**Status:** ✅ **Completo**

### Implementado
- `minimum`, `maximum`, `multipleOf` são parseados e validados.
- `exclusiveMinimum`/`exclusiveMaximum` na forma **numérica** (2019-09/2020-12)
- Compatibilidade com forma **booleana** legada (Draft-04/06/07)
- Suporte a ambos os formatos para compatibilidade com schemas antigos e novos

---

## 7) Validadores de array (`minItems`, `maxItems`, `uniqueItems`, `items`, `prefixItems`)

**Status:** ✅ **Completo**

### Implementado
- `items` (objeto único) é parseado e aplicado em cada elemento do array.
- `minItems` — Validação de comprimento mínimo do array
- `maxItems` — Validação de comprimento máximo do array
- `uniqueItems` — Verificação de unicidade dos elementos
- `prefixItems` — Validação de tupla (2019-09/2020-12)
- `additionalItems` — Suporte limitado

---

## 8) Validadores de objeto (`properties`, `required`, `minProperties`, `maxProperties`, `additionalProperties`, `patternProperties`, `dependencies`, `dependentRequired`, `dependentSchemas`)

**Status:** ✅ **Completo**

### Implementado
- `properties`: valida propriedades declaradas quando presentes.
- `required`: exige campos obrigatórios.
- `additionalProperties`: suporta forma booleana (permitir/bloquear extras).
- `patternProperties`: aplica schema por regex no nome da chave.
- `minProperties` — Validação de quantidade mínima de propriedades
- `maxProperties` — Validação de quantidade máxima de propriedades
- `dependencies` — Suporte a modos property e schema
- `dependentRequired` — Propriedades requeridas quando dependente está presente (2019-09+)
- `dependentSchemas` — Constraints de schema quando dependente está presente (2019-09+)
- `additionalProperties` como **schema** (não apenas boolean)

---

## 9) Condicionais (`if`, `then`, `else`)

**Status:** ✅ **Completo**

### Evidências
- Parsing de `if`, `then`, `else`.
- `ObjectValidator` executa a lógica condicional:
  - se `if` passa, valida `then` (se existir);
  - se `if` falha, valida `else` (se existir).

### Observações
- Há também `ConditionalValidator`, mas o fluxo principal já realiza a validação condicional no validador de objeto.

---

## 10) Metadata (`$schema`, `$id`, `description`, `title`)

**Status:** ✅ **Suportado**

### Implementado
- `$schema` — Identificação do dialeto do schema
- `$id` — URI base para resolução de referências
- `title` — Título do schema
- `description` — Descrição do schema
- `default` — Valor padrão
- `examples` — Exemplos de valores
- `readOnly` / `writeOnly` — Restrições de propriedade
- `deprecated` — Status de depreciação
- `comment` — Anotações

### Uso na validação
- `$id` é utilizado para indexação e resolução de referências externas
- `$schema` permite identificar o dialeto JSON Schema em uso

---

## Comparação consolidada (status)

| Requisito | Status | Observação curta |
|---|---|---|
| 1) Tipos base (`string`, `number`, `integer`, `boolean`, `array`, `object`, `null`) | ✅ Completo | Cobertos no parse + validação de tipo |
| 2) `format` (`date`, `time`, `email`, `uri`, `hostname`, `ipv4`, `ipv6`, `uuid`, etc.) | ⚠️ Parcial | Catálogo amplo, mas com simplificações e "unknown format = pass" |
| 3) `definitions`, `$defs`, `$ref` com `#/definitions` e `#/$defs` | ✅ Completo | Resolução pointer completa com suporte a $id |
| 4) `allOf`, `anyOf`, `oneOf`, `not` | ✅ Completo | Implementados no validador de objeto |
| 5) String (`pattern`, `minLength`, `maxLength`, `format`) | ✅ Completo | Implementados em `PrimitiveValidator` |
| 6) Numérico (`minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`) | ✅ Completo | Forma numérica moderna + compatibilidade legacy |
| 7) Array (`minItems`, `maxItems`, `uniqueItems`, `items`, `prefixItems`) | ✅ Completo | Todos os constraints implementados |
| 8) Objeto (`properties`, `required`, `minProperties`, `maxProperties`, `additionalProperties`, `patternProperties`, `dependencies`, `dependentRequired`, `dependentSchemas`) | ✅ Completo | Todos os constraints + additionalProperties como schema |
| 9) Condicionais (`if`, `then`, `else`) | ✅ Completo | Implementação funcional no `ObjectValidator` |
| 10) Metadata (`$schema`, `$id`, `description`, `title`) | ✅ Completo | Modelagem completa com uso na validação |

---

## Backlog técnico recomendado (prioridade)

Todas as principais funcionalidades listadas abaixo foram implementadas:

1. ✅ **Resolução `$ref` por JSON Pointer completo** (`definitions`, `$defs`, escaping e navegação geral por keyword/objeto)
2. ✅ **Constraints de array** (`minItems`, `maxItems`, `uniqueItems`, `prefixItems`, `items`)
3. ✅ **Constraints de objeto** (`minProperties`, `maxProperties`, `dependencies`, `dependentRequired`, `dependentSchemas`, `additionalProperties` como schema)
4. ✅ **`exclusiveMinimum`/`exclusiveMaximum` alinhados ao draft moderno** (valor numérico)
5. ✅ **Modelagem de metadata** (`$schema`, `$id`, `title`, `description`) para dialeto/resolução/documentação
6. ✅ **Suporte a `type` em array** e keywords modernas (2019-09/2020-12)

---

## Nota final

O projeto agora possui conformidade completa com as principais funcionalidades do JSON Schema, incluindo:

- Resolução completa de `$ref` com JSON Pointer
- Todos os constraints de array e objeto implementados
- Suporte a metadados para resolução de referências
- Compatibilidade com drafts modernos (2019-09/2020-12)

As próximas áreas de melhoria podem incluir:

- Mais formatos de validação
- Melhorias em validação de regex
- Performance em schemas muito grandes
