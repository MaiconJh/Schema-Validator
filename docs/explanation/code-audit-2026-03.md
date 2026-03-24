# Auditoria Técnica Abrangente — Schema-Validator

**Data da auditoria:** 2026-03-24 (UTC)  
**Última atualização:** 2026-03-24  
**Projeto auditado:** `Schema-Validator` (Plugin Minecraft/Bukkit)  
**Versão estimada:** 0.5.0  
**Diretório base:** `src/main/java/com/maiconjh/schemacr/`

---

## 1. Escopo e Metodologia

Esta auditoria foi conduzida por **inspeção direta do código-fonte** do plugin, analisando:

- **Parser** (`FileSchemaLoader.java`): keywords reconhecidas no parsing
- **Modelo** (`Schema.java`): campos disponíveis para representar constraints
- **Validadores** (`PrimitiveValidator`, `ObjectValidator`, `ArrayValidator`, etc.): enforcement em runtime
- **Registro de keywords** (`SupportedKeywordsRegistry.java`): keywords declaradas como "suportadas"
- **Documentação** (`docs/pages/`, `docs/explanation/`): Claims de funcionalidades

O objetivo é identificar gaps entre o que está **documentado**, o que está **registrado como suportado**, e o que está **realmente implementado**.

---

## 2. Análise Detalhada por Componente

### 2.1 Camada de Parsing (`FileSchemaLoader.java`)

**Arquivo:** [`FileSchemaLoader.java`](src/main/java/com/maiconjh/schemacr/schemes/FileSchemaLoader.java) (479 linhas)

#### Keywords Realmente Parseadas

| Keyword | Status | Evidência |
|---------|--------|----------|
| `type` | ✅ Parseado | Linha 151: `parseType()` |
| `properties` | ✅ Parseado | Linhas 340-350 |
| `required` | ✅ Parseado | Linhas 158-163 |
| `additionalProperties` | ✅ Parseado (bool) | Linhas 166-172 |
| `patternProperties` | ✅ Parseado | Linhas 327-338 |
| `items` | ✅ Parseado | Linhas 352-354 |
| `minimum`, `maximum` | ✅ Parseado | Linhas 174-186 |
| `exclusiveMinimum`, `exclusiveMaximum` | ✅ Parseado (bool) | Linhas 190-195 |
| `multipleOf` | ✅ Parseado | Linha 187-189 |
| `minLength`, `maxLength` | ✅ Parseado | Linhas 197-208 |
| `pattern` | ✅ Parseado | Linhas 209-217 |
| `format` | ✅ Parseado | Linhas 218-220 |
| `enum` | ✅ Parseado | Linhas 222-226 |
| `$ref` | ✅ Parseado | Linhas 261-265 |
| `allOf`, `anyOf`, `oneOf` | ✅ Parseado | Linhas 277-305 |
| `not` | ✅ Parseado | Linhas 307-311 |
| `if`, `then`, `else` | ✅ Parseado | Linhas 313-325 |
| `definitions`, `$defs` | ✅ Parseado | Linhas 90-109 |
| `$schema`, `$id` | ✅ Parseado | Linhas 228-238 |
| `title`, `description` | ✅ Parseado | Linhas 240-250 |
| `type` (array) | ✅ Parseado | Linhas 252-259 |

#### Keywords NÃO Parseadas (Ausentes no Parser)

| Keyword | Status | Observação |
|---------|--------|----------|
| `minItems` | ❌ **NÃO parseado** | Presente apenas em `SupportedKeywordsRegistry`, mas não há código de parsing |
| `maxItems` | ❌ **NÃO parseado** | Mesmo caso |
| `uniqueItems` | ❌ **NÃO parseado** | Mesmo caso |
| `prefixItems` | ❌ **NÃO parseado** | Mesmo caso |
| `additionalItems` | ❌ **NÃO parseado** | Mesmo caso |
| `const` | ❌ **NÃO parseado** | Declarado em registry mas não há parsing |
| `default` | ❌ **NÃO parseado** | Declarado em registry mas não há parsing |
| `examples` | ❌ **NÃO parseado** | Declarado em registry mas não há parsing |
| `readOnly`, `writeOnly` | ❌ **NÃO parseado** | Declarado em registry mas não há parsing |
| `deprecated` | ❌ **NÃO parseado** | Declarado em registry mas não há parsing |
| `comment` | ❌ **NÃO parseado** | Declarado em registry mas não há parsing |
| `dependentRequired` | ❌ **NÃO parseado** | Declarado em registry mas não há parsing |
| `dependentSchemas` | ❌ **NÃO parseado** | Declarado em registry mas não há parsing |
| `dependencies` | ❌ **NÃO parseado** | Declarado em registry mas não há parsing |
| `version` | ⚠️ Parseado (custom) | Linha 270-272, mas não é keyword padrão JSON Schema |
| `compatibility` | ⚠️ Parseado (custom) | Linha 273-275, não é keyword padrão |

---

### 2.2 Modelo de Dados (`Schema.java`)

**Arquivo:** [`Schema.java`](src/main/java/com/maiconjh/schemacr/schemes/Schema.java) (463 linhas)

#### Campos Implementados

```java
// Tipo e estrutura
private final SchemaType type;
private final Map<String, Schema> properties;
private final Map<String, Schema> patternProperties;
private final Schema itemSchema;
private final List<String> requiredFields;
private final boolean additionalProperties;

// Constraints numéricos
private final Number minimum;
private final Number maximum;
private final boolean exclusiveMinimum;
private final boolean exclusiveMaximum;
private final Number multipleOf;

// Constraints de string
private final Integer minLength;
private final Integer maxLength;
private final String pattern;
private final String format;

// Constraints diversos
private final List<Object> enumValues;

// Metadata
private final String schemaDialect;
private final String id;
private final String title;
private final String description;
private final List<String> typeList;
private final String ref;
private final String version;
private final String compatibility;

// Composição
private final List<Schema> allOf;
private final List<Schema> anyOf;
private final List<Schema> oneOf;
private final Schema notSchema;

// Condicional
private final Schema ifSchema;
private final Schema thenSchema;
private final Schema elseSchema;
```

#### Campos AUSENTES (não implementados no modelo)

| Campo | Tipo esperado | Keyword JSON Schema |
|-------|---------------|---------------------|
| `minItems` | `Integer` | `minItems` |
| `maxItems` | `Integer` | `maxItems` |
| `uniqueItems` | `Boolean` | `uniqueItems` |
| `prefixItems` | `List<Schema>` | `prefixItems` (2019-09+) |
| `additionalItems` | `Schema` ou `Boolean` | `additionalItems` |
| `const` | `Object` | `const` |
| `default` | `Object` | `default` |
| `examples` | `List<Object>` | `examples` |
| `readOnly` | `Boolean` | `readOnly` |
| `writeOnly` | `Boolean` | `writeOnly` |
| `deprecated` | `Boolean` | `deprecated` |
| `comment` | `String` | `comment` |
| `minProperties` | `Integer` | `minProperties` |
| `maxProperties` | `Integer` | `maxProperties` |
| `dependentRequired` | `Map<String, List<String>>` | `dependentRequired` |
| `dependentSchemas` | `Map<String, Schema>` | `dependentSchemas` |

---

### 2.3 Validadores (`validation/`)

#### 2.3.1 `PrimitiveValidator.java` (173 linhas)

**Status:** ✅ **Funcional**

Valida:
- Tipos primitivos (STRING, NUMBER, INTEGER, BOOLEAN, NULL, ANY)
- Enum constraints
- Constraints numéricos (minimum, maximum, exclusiveMinimum, exclusiveMaximum, multipleOf)
- Constraints de string (minLength, maxLength, pattern, format)

#### 2.3.2 `ObjectValidator.java` (268 linhas)

**Status:** ✅ **Funcional** (com gaps)

Valida:
- ✅ `$ref` com resolução
- ✅ `allOf`, `anyOf`, `oneOf`, `not`
- ✅ `if`/`then`/`else`
- ✅ `required`, `properties`, `patternProperties`
- ✅ `additionalProperties` (boolean)
- ❌ **NÃO valida** `minProperties` — Campo não existe em Schema
- ❌ **NÃO valida** `maxProperties` — Campo não existe em Schema
- ❌ **NÃO valida** `dependencies` — Campo não existe em Schema
- ❌ **NÃO valida** `dependentRequired` — Campo não existe em Schema
- ❌ **NÃO valida** `dependentSchemas` — Campo não existe em Schema
- ❌ **NÃO valida** `additionalProperties` como schema (apenas boolean)

#### 2.3.3 `ArrayValidator.java` (36 linhas)

**Status:** ❌ **MUITO LIMITADO**

```java
// Apenas isso é validado:
if (schema.getItemSchema() == null) {
    return errors; // Sem validação se não há items
}

for (int i = 0; i < list.size(); i++) {
    // Valida cada elemento com schema.getItemSchema()
}
```

**O que NÃO está implementado:**

| Keyword | Status | Motivo |
|---------|--------|--------|
| `minItems` | ❌ | Campo não existe em Schema.java |
| `maxItems` | ❌ | Campo não existe em Schema.java |
| `uniqueItems` | ❌ | Campo não existe em Schema.java |
| `prefixItems` | ❌ | Campo não existe em Schema.java |
| `additionalItems` | ❌ | Campo não existe em Schema.java |

#### 2.3.4 `FormatValidator.java` (~681 linhas)

**Status:** ✅ **Completo para formatos suportados**

Formatos padrão implementados (expressões regulares):
- `date-time`, `date`, `time`, `duration`
- `email`, `idn-email`
- `hostname`, `idn-hostname`
- `ipv4`, `ipv6`
- `uri`, `uri-reference`, `uri-template`
- `json-pointer`, `relative-json-pointer`
- `uuid`, `regex`

Formatos Minecraft implementados (semântico com registries):
- `minecraft-item`, `minecraft-block`, `minecraft-entity`
- `minecraft-attribute`, `minecraft-effect`, `minecraft-enchantment`
- `minecraft-biome`, `minecraft-dimension`, `minecraft-particle`
- `minecraft-sound`, `minecraft-potion`, `minecraft-recipe`, `minecraft-tag`

**Limitações:**
- Formatos desconhecidos retornam `true` (passam sem validação)
- `idn-email` e `idn-hostname` reutilizam regex ASCII

#### 2.3.5 Validadores Especializados

| Validador | Arquivo | Status |
|-----------|---------|--------|
| `ConditionalValidator` | [`ConditionalValidator.java`](src/main/java/com/maiconjh/schemacr/validation/ConditionalValidator.java) | ✅ Implementado (duplicado no ObjectValidator) |
| `OneOfValidator` | [`OneOfValidator.java`](src/main/java/com/maiconjh/schemacr/validation/OneOfValidator.java) | ✅ Implementado (duplicado no ObjectValidator) |
| `NotValidator` | [`NotValidator.java`](src/main/java/com/maiconjh/schemacr/validation/NotValidator.java) | ✅ Implementado (duplicado no ObjectValidator) |

**Observação:** Os validadores especializados existem mas não são utilizados — o `ObjectValidator` já implementa toda a lógica diretamente.

---

### 2.4 Resolução de Referências (`SchemaRefResolver.java`)

**Arquivo:** [`SchemaRefResolver.java`](src/main/java/com/maiconjh/schemacr/schemes/SchemaRefResolver.java) (391 linhas)

**Status:** ✅ **Parcialmente funcional**

Implementado:
- ✅ Referências locais `#/definitions/...`
- ✅ Referências com JSON Pointer `#/properties/name`
- ✅ Suporte a `definitions` e `$defs`
- ✅ Escaping `~0` e `~1`
- ✅ Referências externas por path
- ✅ Referências por URL com cache
- ✅ Detecção de referências circulares

Limitação:
- ⚠️ `navigateTo()` (linhas 317-327) apenas suporta navegação por:
  - `properties/<key>`
  - `items`
- Não suporta navegação por `prefixItems`, `allOf`, `anyOf`, etc.

---

### 2.5 Registro de Keywords (`SupportedKeywordsRegistry.java`)

**Arquivo:** [`SupportedKeywordsRegistry.java`](src/main/java/com/maiconjh/schemacr/schemes/SupportedKeywordsRegistry.java) (342 linhas)

**Status:** ⚠️ **Desatualizado / Inconsistente**

Este registry lista **51 keywords** como "suportadas", mas muitas delas **NÃO estão implementadas**:

#### Keywords listadas como "suportadas" mas NÃO implementadas

| Keyword | Parser | Modelo | Validador |
|---------|--------|--------|-----------|
| `minItems` | ❌ | ❌ | ❌ |
| `maxItems` | ❌ | ❌ | ❌ |
| `uniqueItems` | ❌ | ❌ | ❌ |
| `additionalItems` | ❌ | ❌ | ❌ |
| `minProperties` | ❌ | ❌ | ❌ |
| `maxProperties` | ❌ | ❌ | ❌ |
| `dependencies` | ❌ | ❌ | ❌ |
| `dependentRequired` | ❌ | ❌ | ❌ |
| `dependentSchemas` | ❌ | ❌ | ❌ |
| `const` | ❌ | ❌ | ❌ |
| `default` | ❌ | ❌ | ❌ |
| `examples` | ❌ | ❌ | ❌ |
| `readOnly` | ❌ | ❌ | ❌ |
| `writeOnly` | ❌ | ❌ | ❌ |
| `deprecated` | ❌ | ❌ | ❌ |
| `comment` | ❌ | ❌ | ❌ |

---

## 3. Matriz: Documentado vs Implementado

### 3.1 Afirmações do Audit Anterior (`limitations-audit-195410.md`)

| Afirmação | Status Real | Evidência |
|-----------|-------------|----------|
| "✅ Constraints de array (`minItems`, `maxItems`, `uniqueItems`, `prefixItems`, `items`)" | ❌ **FALSO** | ArrayValidator NÃO valida minItems, maxItems, uniqueItems, prefixItems. Schema.java NÃO tem esses campos. |
| "✅ Constraints de objeto (`minProperties`, `maxProperties`, `dependencies`, `dependentRequired`, `dependentSchemas`, `additionalProperties` como schema)" | ❌ **PARCIALMENTE FALSO** | ObjectValidator NÃO valida minProperties, maxProperties. Schema.java NÃO tem esses campos. |
| "✅ Modelagem de metadata (`$schema`, `$id`, `title`, `description`)" | ✅ **VERDADEIRO** | Implementado |
| "✅ Suporte a arrays de tipos com dispatch runtime" | ✅ **VERDADEIRO** | Implementado |

### 3.2 Documentação (`docs/pages/schema-keywords.md`)

| Seção | Afirmação | Status |
|-------|-----------|--------|
| "Fully Implemented Keywords - Array Keywords" | `minItems` implementado | ❌ **FALSO** |
| "Fully Implemented Keywords - Array Keywords" | `maxItems` implementado | ❌ **FALSO** |
| "Fully Implemented Keywords - Array Keywords" | `uniqueItems` implementado | ❌ **FALSO** |
| "Fully Implemented Keywords - Array Keywords" | `prefixItems` implementado | ❌ **FALSO** |
| "Fully Implemented Keywords - Array Keywords" | `additionalItems` limitado | ❌ **FALSO** (não implementado) |
| "Fully Implemented Keywords - Object Keywords" | `minProperties` implementado | ❌ **FALSO** |
| "Fully Implemented Keywords - Object Keywords" | `maxProperties` implementado | ❌ **FALSO** |
| "Fully Implemented Keywords - Object Keywords" | `dependencies` implementado | ❌ **FALSO** |
| "Fully Implemented Keywords - Object Keywords" | `dependentRequired` implementado | ❌ **FALSO** |
| "Fully Implemented Keywords - Object Keywords" | `dependentSchemas` implementado | ❌ **FALSO** |

---

## 4. Comparação com Bibliotecas Sugeridas

### 4.1 everit-org/json-schema (Java)

| Feature | Schema-Validator | everit-org |
|---------|-----------------|------------|
| Drafts suportados | ⚠️ Parcial | ✅ Todos (07, 2019-09, 2020-12) |
| minItems/maxItems | ❌ Não | ✅ Sim |
| uniqueItems | ❌ Não | ✅ Sim |
| prefixItems | ❌ Não | ✅ Sim |
| minProperties/maxProperties | ❌ Não | ✅ Sim |
| dependencies | ❌ Não | ✅ Parcial |
| $ref externo | ⚠️ Limitado | ✅ Completo |
| Formatos customizáveis | ❌ Não | ✅ Sim |
| Performance | ⚠️ Desconhecido | ✅ Otimizado |

### 4.2 networknt/json-schema-validator (Java)

| Feature | Schema-Validator | networknt |
|---------|-----------------|-----------|
| Drafts suportados | ⚠️ Parcial | ✅ Todos |
| minItems/maxItems | ❌ Não | ✅ Sim |
| uniqueItems | ❌ Não | ✅ Sim |
| prefixItems | ❌ Não | ✅ Sim |
| Cache de schemas | ❌ Limitado | ✅ Completo |
| Performance | ⚠️ Desconhecido | ✅ Alta |

### 4.3 AJV (JavaScript)

| Feature | Schema-Validator | AJV |
|---------|-----------------|-----|
| Compilação ahead-of-time | ❌ Não | ✅ Sim |
| Plugins customizados | ❌ Não | ✅ Sim |
| $ref circular | ❌ Limitado | ✅ Suportado |
| Validação assíncrona | ❌ Não | ✅ Sim |

---

## 5. Plano de Implementação

### 5.1 Prioridade Alta (Features Faltantes Críticas)

#### 5.1.1 Array Constraints

**Impacto:** Alto — afetam validação de dados array

**Implementação sugerida:**

1. **Adicionar campos ao Schema.java:**
```java
private final Integer minItems;
private final Integer maxItems;
private final Boolean uniqueItems;
private final List<Schema> prefixItems;
private final Schema additionalItems; // pode ser Boolean ou Schema
```

2. **Adicionar parsing no FileSchemaLoader.java:**
```java
// Após linha 352 (items parsing)
if (raw.containsKey("minItems") && raw.get("minItems") instanceof Number minI) {
    minItems = minI.intValue();
}
// ... maxItems, uniqueItems similar
if (raw.containsKey("prefixItems") && raw.get("prefixItems") instanceof List<?> prefixRaw) {
    // parsear lista de schemas
}
```

3. **Atualizar ArrayValidator.java:**
```java
// Validar minItems
if (schema.getMinItems() != null && list.size() < schema.getMinItems()) {
    errors.add(new ValidationError(...));
}
// Validar maxItems
// Validar uniqueItems (usar Set para comparação)
// Validar prefixItems (tupla)
// Validar additionalItems
```

#### 5.1.2 Object Constraints

**Impacto:** Alto

**Implementação sugerida:**

1. **Adicionar campos ao Schema.java:**
```java
private final Integer minProperties;
private final Integer maxProperties;
private final Map<String, List<String>> dependentRequired;
private final Map<String, Schema> dependentSchemas;
```

2. **Adicionar parsing no FileSchemaLoader.java:**
```java
// Similar ao pattern de parsing existente
if (raw.containsKey("minProperties") && raw.get("minProperties") instanceof Number minP) {
    minProperties = minP.intValue();
}
```

3. **Atualizar ObjectValidator.java:**
```java
// Validar minProperties
if (schema.getMinProperties() != null && map.size() < schema.getMinProperties()) {
    // error
}
// Validar maxProperties
// Validar dependentRequired
// Validar dependentSchemas
```

### 5.2 Prioridade Média (Features Úteis)

#### 5.2.1 Keyword `const`

**Implementação:**
1. Adicionar campo `constValue` ao Schema.java
2. Adicionar parsing no FileSchemaLoader
3. Validar no PrimitiveValidator

#### 5.2.2 Metadata Keywords

keywords como `default`, `examples`, `readOnly`, `writeOnly`, `deprecated`, `comment` podem ser parseadas mas **não precisam ser validadas** — são informações apenas. Podem ser adicionadas ao Schema.java para exposição via API.

### 5.3 Prioridade Baixa (Melhorias)

- Formatos customizáveis (extensibilidade)
- Suporte a `$ref` com navegação completa (prefixItems, allOf, anyOf)
- Performance optimization com cache de validadores compilados

---

## 6. Sugestões de Implementação Sem Quebrar Código Existente

### 6.1 Princípios

1. **Adição incremental:** Sempre adicionar novos campos ao final da classe Schema.java e do construtor
2. **Backward compatibility:** Novos campos devem ter valores padrão seguros (null, empty list)
3. **Graceful degradation:** Se uma keyword não é reconhecida, Warn mas continue (comportamento atual)
4. **Testes unitários:** Adicionar testes para cada nova feature antes de integrar

### 6.2 Ordem de Implementação Sugerida

```
1. Schema.java: Adicionar campos (minItems, maxItems, uniqueItems, prefixItems)
2. FileSchemaLoader.java: Adicionar parsing
3. ArrayValidator.java: Adicionar validação
4. Schema.java: Adicionar campos (minProperties, maxProperties)
5. ObjectValidator.java: Adicionar validação
6. SupportedKeywordsRegistry.java: Atualizar status
7. Documentação: Atualizar para refletir implementação real
```

### 6.3 Como Evitar Refatoração

- ** NÃO modificar validators existentes** — apenas adicionar novos blocks condicionais
- ** NÃO modificar o contrato do Schema.java** — apenas adicionar campos opcionais
- ** NÃO modificar FileSchemaLoader.java** — apenas adicionar novos branches de parsing
- **Manter compatibilidade** com schemas existentes que não usam as novas keywords

---

## 7. Resumo Executivo

| Aspecto | Status |
|---------|--------|
| Parser (FileSchemaLoader) | ⚠️ ~50% das keywords padrão implementadas |
| Modelo (Schema) | ⚠️ ~60% dos campos necessários |
| Validador de Objetos | ⚠️ ~70% das features |
| Validador de Arrays | ❌ ~20% das features (apenas items) |
| Validador de Primitivos | ✅ ~95% das features |
| Registro de Keywords | ❌ **Inconsistente** — lista keywords não implementadas |
| Documentação | ❌ **Desatualizada** — afirma funcionalidades inexistentes |

### Principais Descobertas

1. **ArrayValidator é extremamente limitado** — só valida `items`, não valida `minItems`, `maxItems`, `uniqueItems`, `prefixItems`
2. **Schema.java não tem campos** para as constraints de array e object mais comuns
3. **SupportedKeywordsRegistry** lista keywords como "suportadas" sem implementação real
4. **Documentação** afirma que funcionalidades foram implementadas quando não foram
5. **Audit anterior** (`limitations-audit-195410.md`) contém informações desatualizadas/incorretas

### Recomendações Imediatas

1. **Corrigir SupportedKeywordsRegistry** para refletir o estado real
2. **Atualizar documentação** para remover claims de funcionalidades inexistentes
3. **Implementar array constraints** (minItems, maxItems, uniqueItems, prefixItems)
4. **Implementar object constraints** (minProperties, maxProperties, dependentRequired, dependentSchemas)
5. **Criar testes de validação** para cada keyword declarada como suportada

---

## 8. Fonte de Verificação

| Artefato | Caminho |
|----------|---------|
| Parser | [`src/main/java/com/maiconjh/schemacr/schemes/FileSchemaLoader.java`](src/main/java/com/maiconjh/schemacr/schemes/FileSchemaLoader.java) |
| Modelo | [`src/main/java/com/maiconjh/schemacr/schemes/Schema.java`](src/main/java/com/maiconjh/schemacr/schemes/Schema.java) |
| Registro | [`src/main/java/com/maiconjh/schemacr/schemes/SupportedKeywordsRegistry.java`](src/main/java/com/maiconjh/schemacr/schemes/SupportedKeywordsRegistry.java) |
| Object Validator | [`src/main/java/com/maiconjh/schemacr/validation/ObjectValidator.java`](src/main/java/com/maiconjh/schemacr/validation/ObjectValidator.java) |
| Array Validator | [`src/main/java/com/maiconjh/schemacr/validation/ArrayValidator.java`](src/main/java/com/maiconjh/schemacr/validation/ArrayValidator.java) |
| Primitive Validator | [`src/main/java/com/maiconjh/schemacr/validation/PrimitiveValidator.java`](src/main/java/com/maiconjh/schemacr/validation/PrimitiveValidator.java) |
| Documentação | [`docs/pages/schema-keywords.md`](docs/pages/schema-keywords.md) |
| Audit Anterior | [`docs/explanation/limitations-audit-195410.md`](docs/explanation/limitations-audit-195410.md) |

---

*Last updated: 2026-03-24*  
*Documentation version: 0.5.0-audit*