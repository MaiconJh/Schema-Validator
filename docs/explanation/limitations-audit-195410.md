# Auditoria de Suporte JSON Schema — Schema-Validator

**Data da auditoria:** 2026-03-22 (UTC)  
**Projeto auditado:** `Schema-Validator`  
**Base de comparação da especificação:** JSON Schema oficial em https://json-schema.org/ (principalmente drafts Draft-07, 2019-09 e 2020-12 para os itens solicitados).

## Metodologia

Esta auditoria foi feita por inspeção direta do código-fonte do plugin (parser, modelo e validadores), conferindo:

- quais keywords são apenas reconhecidas no parse;
- quais keywords são realmente **enforçadas** em runtime;
- quais comportamentos divergem da especificação oficial.

---

## Resumo executivo

- O plugin já cobre o núcleo de validação por tipo (`string`, `number`, `integer`, `boolean`, `array`, `object`, `null`) e várias keywords clássicas de string/número/composição.  
- Há suporte de `format` relativamente amplo (incluindo formatos padrão e formatos customizados de Minecraft).  
- Existe parsing de `definitions` e `$defs`, e parsing de `$ref`, mas a **resolução por JSON Pointer para `#/definitions/...` e `#/$defs/...` não está completa** (o resolver navega basicamente por `properties`/`items`).  
- Keywords importantes de array/objeto (`minItems`, `maxItems`, `uniqueItems`, `minProperties`, `maxProperties`, `dependencies`) aparecem como suportadas em registro/documentação, porém **não são aplicadas pelos validadores atuais**.  
- Metadata (`$schema`, `$id`, `title`, `description`) é aceita/considerada em nível de keyword suportada, mas não tem modelagem/enforcement semântico completo.

---

## Matriz detalhada por requisito

## 1) Formatos de schema suportados (`string`, `number`, `integer`, `boolean`, `array`, `object`, `null`)

**Status:** ✅ **Completo** (para validação de tipo)

### Evidências
- Parsing de `type` contempla explicitamente: `object`, `array`, `string`, `integer`, `number`, `boolean`, `null` (e fallback para `any`).
- O dispatcher encaminha `object` para `ObjectValidator`, `array` para `ArrayValidator` e demais tipos para `PrimitiveValidator`.
- `PrimitiveValidator` implementa checks concretos para `STRING`, `NUMBER`, `INTEGER`, `BOOLEAN`, `NULL`.

### Observações
- `type` como **array de tipos** (ex.: `"type": ["string", "null"]`) não foi encontrado no parser atual; o parser espera `type` textual.

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

**Status:** ⚠️ **Parcial**

### O que existe
- Loader extrai `definitions` e `$defs` em uma primeira passada.
- Parser captura `$ref` no schema.
- Existe `SchemaRefResolver` para referências locais, externas por arquivo/URL e com pointer.

### Lacunas críticas
- A navegação de JSON Pointer (`navigateTo`) só percorre essencialmente `properties` e `items`.
- Não há ramo explícito para resolver segmentos `definitions` ou `$defs` em `resolveLocalRef/resolveJsonPointer`.
- Resultado prático: referências como `#/definitions/MinhaDef` e `#/$defs/MinhaDef` tendem a **falhar** ou depender de estrutura não padrão.

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

**Status:** ⚠️ **Parcial**

### O que funciona
- `minimum`, `maximum`, `multipleOf` são parseados e validados.
- `exclusiveMinimum`/`exclusiveMaximum` são aplicados como flags booleanas em conjunto com min/max.

### Divergência de draft
- Em drafts modernos (2019-09/2020-12), `exclusiveMinimum` e `exclusiveMaximum` são **numéricos** (não booleanos).
- O parser atual só aceita esses campos quando são `Boolean`, modelo também usa booleans.

Conclusão: comportamento alinhado ao estilo antigo (Draft-04/06/07 com semântica booleana associada a min/max), mas não completo para drafts novos.

---

## 7) Validadores de array (`minItems`, `maxItems`, `uniqueItems`, `items`)

**Status:** ⚠️ **Parcial**

### O que funciona
- `items` (objeto único) é parseado e aplicado em cada elemento do array.

### O que falta
- `minItems`, `maxItems`, `uniqueItems` não são aplicados no `ArrayValidator` atual.
- Não há suporte visível para `items` como lista/tupla (estilo draft antigo) nem para keywords modernas de tupla (`prefixItems`).

---

## 8) Validadores de objeto (`properties`, `required`, `minProperties`, `maxProperties`, `additionalProperties`, `patternProperties`, `dependencies`)

**Status:** ⚠️ **Parcial**

### O que funciona
- `properties`: valida propriedades declaradas quando presentes.
- `required`: exige campos obrigatórios.
- `additionalProperties`: suporta forma booleana (permitir/bloquear extras).
- `patternProperties`: aplica schema por regex no nome da chave.

### O que falta
- `minProperties` e `maxProperties` não são enforçados.
- `dependencies` não é enforçado (apesar de constar como suportado no registry).
- `additionalProperties` como **schema** (não boolean) não aparece suportado no parser atual.

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

**Status:** ❌ **Não suportado** (como recurso semântico completo)

### Situação atual
- Essas keywords aparecem no registro de keywords suportadas (evitam warning de “keyword desconhecida”).
- Contudo, não há campos dedicados no modelo `Schema` para `$schema`, `$id`, `title`, `description`, nem uso semântico desses metadados na validação.

### Impacto
- O plugin não altera comportamento conforme dialeto em `$schema`.
- `$id` não está modelado para base URI e resolução de referência conforme especificação.
- `title`/`description` não são preservados como metadata útil de runtime no objeto de schema principal.

---

## Comparação consolidada (status)

| Requisito | Status | Observação curta |
|---|---|---|
| 1) Tipos base (`string`, `number`, `integer`, `boolean`, `array`, `object`, `null`) | ✅ Completo | Cobertos no parse + validação de tipo |
| 2) `format` (`date`, `time`, `email`, `uri`, `hostname`, `ipv4`, `ipv6`, `uuid`, etc.) | ⚠️ Parcial | Catálogo amplo, mas com simplificações e “unknown format = pass” |
| 3) `definitions`, `$defs`, `$ref` com `#/definitions` e `#/$defs` | ⚠️ Parcial | Parse existe; resolução pointer para `definitions/$defs` incompleta |
| 4) `allOf`, `anyOf`, `oneOf`, `not` | ✅ Completo | Implementados no validador de objeto |
| 5) String (`pattern`, `minLength`, `maxLength`, `format`) | ✅ Completo | Implementados em `PrimitiveValidator` |
| 6) Numérico (`minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`) | ⚠️ Parcial | `exclusive*` tratado como boolean, divergente de drafts modernos |
| 7) Array (`minItems`, `maxItems`, `uniqueItems`, `items`) | ⚠️ Parcial | Só `items` aplicado atualmente |
| 8) Objeto (`properties`, `required`, `minProperties`, `maxProperties`, `additionalProperties`, `patternProperties`, `dependencies`) | ⚠️ Parcial | `min/maxProperties` e `dependencies` sem enforcement |
| 9) Condicionais (`if`, `then`, `else`) | ✅ Completo | Implementação funcional no `ObjectValidator` |
| 10) Metadata (`$schema`, `$id`, `description`, `title`) | ❌ Não suportado | Apenas reconhecidos no registry; sem semântica/modelo completos |

---

## Backlog técnico recomendado (prioridade)

1. **Resolver `$ref` por JSON Pointer completo** (`definitions`, `$defs`, escaping e navegação geral por keyword/objeto).  
2. **Implementar constraints de array** (`minItems`, `maxItems`, `uniqueItems`).  
3. **Implementar constraints de objeto faltantes** (`minProperties`, `maxProperties`, `dependencies`; e `additionalProperties` como schema).  
4. **Alinhar `exclusiveMinimum`/`exclusiveMaximum` ao draft moderno** (valor numérico).  
5. **Modelar metadata essencial** (`$schema`, `$id`, `title`, `description`) para dialeto/resolução/documentação.  
6. **Cobrir `type` em array** e keywords mais modernas quando o alvo for 2019-09/2020-12.

---

## Nota final

O projeto já possui uma base sólida para validação prática de schemas em cenários comuns, mas ainda há um gap entre “keyword reconhecida” e “keyword efetivamente validada” em várias áreas. Para conformidade mais estrita com JSON Schema moderno, o foco deve estar em `$ref`/`$defs`, constraints de arrays/objetos e semântica de metadados/dialetos.
