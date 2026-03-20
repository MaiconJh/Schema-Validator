# Plano de Nova Arquitetura - Suporte Completo a Referências Schema

> **Data de Criação:** 2026-03-20  
> **Versão:** 1.0  
> **Status:** Proposta de Implementação  
> **Referência:** [deep-system-audit-2026-03-20.md](audits/deep-system-audit-2026-03-20.md)

---

## 1. Sumário Executivo

Este documento apresenta um plano detalhado para implementar suporte completo a referências JSON Schema (`$ref`, `definitions` e `$defs`) no sistema Schema-Validator, sem reescrever a arquitetura atual. O plano segue a metodologia **Safe-Architecture** e contempla uma estratégia de migração progressiva que mantém a compatibilidade retroativa.

### Objetivos Principais

1. **Resolução completa de `$ref`**: Suporte a todos os formatos de referência JSON Pointer (`#/definitions/Name`, `#/$defs/Name`, etc.)
2. **Armazenamento de definições locais**: Persistir `definitions` e `$defs` diretamente nos objetos Schema
3. **Resolução recursiva**: Suporte a referências aninhadas e circulares
4. **Compatibilidade retroativa**: Manter funcionamento de schemas existentes

---

## 2. Análise do Estado Atual

### 2.1 Componentes Analisados

| Componente | Arquivo | Status Atual |
|------------|---------|--------------|
| **SchemaRefResolver** | `src/.../schemes/SchemaRefResolver.java` | Suporta resolução básica, mas falha em definitions locais |
| **FileSchemaLoader** | `src/.../schemes/FileSchemaLoader.java` | Extrai definitions/$defs, mas não os armazena no Schema |
| **Schema** | `src/.../schemes/Schema.java` | Não possui campo para armazenar definições locais |
| **ValidationService** | `src/.../core/ValidationService.java` | Já utiliza ValidatorDispatcher corretamente |
| **ObjectValidator** | `src/.../validation/ObjectValidator.java` | Resolve $ref via refResolver, mas não encontra definitions locais |
| **SchemaRegistry** | `src/.../schemes/SchemaRegistry.java` | Armazena schemas por nome, não suporta lookup por $id |

### 2.2 Problemas Identificados

#### Problema 1: Arquitetura Atual de Referências

```
┌─────────────────────────────────────────────────────────────────────┐
│                    ARQUITETURA ATUAL - PROBLEMA                     │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  FileSchemaLoader.load()                                           │
│         │                                                           │
│         ├──► definitions/$defs extraídos ──► Map<String, Schema>   │
│         │     (armazenado localmente no loader, perdido após       │
│         │      retorno do método)                                   │
│         │                                                           │
│         └──► Schema criado ──────────────────────────────────────    │
│               (NÃO contém referências a definitions/$defs)         │
│                                                                     │
│  SchemaRefResolver.resolveLocalRef("#/definitions/Player")        │
│         │                                                           │
│         ├──► registry.getSchema(currentSchemaName) ──► OK          │
│         │                                                           │
│         └──► navigateTo("definitions/Player", root) ──► FALHA     │
│               (Schema não tem campo 'definitions')                 │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

**Causa Raiz:** O `Schema` não armazena as definições locais, e o `FileSchemaLoader` as mantém apenas em um mapa temporário que é perdido após a criação do Schema.

#### Problema 2: Navegação JSON Pointer Incompleta

Em [`SchemaRefResolver.java:317-327`](src/main/java/com/maiconjh/schemacr/schemes/SchemaRefResolver.java:317), o método `navigateTo()` só procura em:
- `properties`
- `items`

Não procura em `definitions` ou `$defs`.

#### Problema 3: $defs Suportado Parcialmente

O `FileSchemaLoader` ([linhas 101-109](src/main/java/com/maiconjh/schemacr/schemes/FileSchemaLoader.java:101)) extrai `$defs`, mas não os utiliza na resolução de referências.

### 2.3 Fluxo Atual de Validação

```
ValidationService.validate(data, schema)
        │
        ▼
ValidatorDispatcher.forSchema(schema)
        │
        ▼
ObjectValidator.validate()
        │
        ├──► schema.isRef() ──► refResolver.resolveRef()
        │                           │
        │                           ├──► Local: "#/definitions/Name"
        │                           │         └──► FALHA (Schema não tem definitions)
        │                           │
        │                           ├──► External: "path/to/schema.json#..."
        │                           │         └──► Funciona parcialmente
        │                           │
        │                           └──► URL: "https://..."
        │                                     └──► Funciona
        │
        └──► Validação normal de propriedades...
```

---

## 3. Padrões de Implementação Recomendados

### 3.1 Schema Storage/Registry

O padrão **Schema Registry** já está implementado parcialmente via `SchemaRegistry`. A melhoria necessária é adicionar suporte para:

- Lookup por `$id` (identificador único do schema)
- Namespace de definições (evitar conflitos de nomes)
- Cache hierárquico (definitions agrupadas por schema)

### 3.2 Canonical Resolution

Implementar resolução canônica onde cada `$ref` é resolvido para um Schema imutável e canônico:

```
$ref "#/definitions/Player" 
    │
    ▼
Canonical Resolution
    │
    ├──► 1. Verificar se é referência local (#/...)
    │         │
    │         ├──► Buscar definitions/$defs no Schema atual
    │         │
    │         └──► Se não encontrado, buscar no Schema pai
    │
    ├──► 2. Verificar se é referência externa (path#...)
    │         │
    │         └──► Carregar schema externo e resolver pointer
    │
    └──► 3. Verificar se é referência por nome
              │
              └──► Buscar no SchemaRegistry por nome
```

### 3.3 Reference Caching

O sistema já possui caching básico em `SchemaRefResolver`. A melhoria inclui:

- **Cache de definições locais**: Armazenar definitions de cada schema
- **Cache de resolução**: Evitar resolução repetida da mesma referência
- **Invalidação seletiva**: Limpar apenas caches afetados por mudanças

### 3.4 Distributed Schema Resolution

Para schemas distribuídos (URLs externas), implementar:

- **Preloading de dependências**: Carregar schemas referenciados antecipadamente
- **Resoluçãolazy**: Carregar apenas quando necessário
- **Fallback para cache local**: Usar cache quando resolução externa falhar

---

## 4. Proposta de Nova Arquitetura

### 4.1 Diagrama Conceitual

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                         NOVA ARQUITETURA - VISÃO GERAL                       │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                         SchemaRegistry                                │    │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────────┐     │    │
│  │  │ schemasByName   │  │ schemasById     │  │ definitions     │     │    │
│  │  │ (Map<String,   │  │ (Map<String,    │  │ (Map<schemaName, │     │    │
│  │  │  Schema>)       │  │  Schema>)       │  │  Map<String,    │     │    │
│  │  │                 │  │                 │  │   Schema>>)      │     │    │
│  │  └─────────────────┘  └─────────────────┘  └──────────────────┘     │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                    │                                         │
│                                    ▼                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                      CanonicalSchemaStore                            │    │
│  │  ┌────────────────────────────────────────────────────────────────┐  │    │
│  │  │  Canonical Resolution Engine                                  │  │    │
│  │  │  - Resolve $ref to canonical Schema                           │  │    │
│  │  │  - Handle circular references                                 │  │    │
│  │  │  - Cache resolved schemas                                     │  │    │
│  │  └────────────────────────────────────────────────────────────────┘  │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                    │                                         │
│            ┌───────────────────────┼───────────────────────┐                 │
│            ▼                       ▼                       ▼                 │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐           │
│  │ FileSchemaLoader│    │ SchemaRefResolver│    │ ValidationService│          │
│  │                 │    │                 │    │                 │           │
│  │ - parseSchema()│    │ - resolveLocal()│    │ - validate()    │           │
│  │ - extractDefs()│    │ - resolveExt()  │    │ - validateBatch│           │
│  │ - toSchema()   │    │ - resolveUrl()  │    │                 │           │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘           │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────────────────┐
│                         NOVA ARQUITETURA - SCHEMA                            │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                              Schema                                  │    │
│  │  ┌─────────────────────────────────────────────────────────────────┐ │    │
│  │  │  Campos Existentes                                            │ │    │
│  │  │  - name, type, properties, patternProperties, itemSchema      │ │    │
│  │  │  - requiredFields, additionalProperties, minimum, maximum      │ │    │
│  │  │  - minLength, maxLength, pattern, format, multipleOf           │ │    │
│  │  │  - enumValues, ref, version, compatibility                    │ │    │
│  │  │  - allOf, anyOf, oneOf, notSchema, ifSchema, thenSchema        │ │    │
│  │  └─────────────────────────────────────────────────────────────────┘ │    │
│  │                                                                      │    │
│  │  ┌─────────────────────────────────────────────────────────────────┐ │    │
│  │  │  NOVOS CAMPOS ( Nightingale)                                    │ │    │
│  │  │  - definitions : Map<String, Schema>   (definitions Draft-07)   │ │    │
│  │  │  - $defs : Map<String, Schema>         ($defs 2019-09+)        │ │    │
│  │  │  - $id : String                         (schema identifier)    │ │    │
│  │  │  - $anchor : String                     (anchor declaration)    │ │    │
│  │  │  - isCanonical : boolean               (resolved flag)         │ │    │
│  │  │  - resolvedRef : Schema               (resolved $ref target)   │ │    │
│  │  └─────────────────────────────────────────────────────────────────┘ │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 Componentes a Modificar/Adicionar

| Componente | Ação | Descrição |
|------------|------|-----------|
| **Schema** | MODIFICAR | Adicionar campos `definitions`, `$defs`, `$id`, `$anchor`, `isCanonical`, `resolvedRef` |
| **SchemaBuilder** | ADICIONAR | Factory pattern para criar Schema com definições |
| **FileSchemaLoader** | MODIFICAR | Armazenar definitions/$defs no Schema, não em mapa local |
| **SchemaRefResolver** | MODIFICAR | Buscar definitions locais primeiro, implementar Canonical Resolution |
| **SchemaRegistry** | MODIFICAR | Adicionar índice por `$id`, suportar lookup de definitions |
| **CanonicalSchemaStore** | ADICIONAR | Novo componente para cache de schemas resolvidos |
| **ValidationService** | MANTER | Já funciona corretamente, apenas documentar configuração |

### 4.3 Fluxo de Resolução de Referências (Novo)

```
resolveRef("$ref": "#/definitions/Player", currentSchema="player-profile")
        │
        ▼
┌─────────────────────────────────────────────────────────────────┐
│ CanonicalSchemaStore.getCached("#/definitions/Player")         │
│   │                                                             │
│   ├──► Se encontrado em cache ──► Retorna Schema resolvido      │
│   │                                                             │
│   └──► Se não encontrado ──► Prosseguir para resolução         │
└─────────────────────────────────────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────────────────────────────────────┐
│ SchemaRefResolver.resolveRef()                                 │
│   │                                                             │
│   ├──► ref.startsWith("#/") ──► resolveLocalRef()              │
│   │       │                                                     │
│   │       ├──► path = "definitions/Player"                     │
│   │       │                                                     │
│   │       └──► Buscar no Schema atual:                         │
│   │               ├──► schema.getDefinitions().get("Player")   │
│   │               │       (NOVO - definitions Draft-07)        │
│   │               │                                                     │
│   │               └──► schema.get$Defs().get("Player")         │
│   │                   (NOVO - $defs 2019-09+)                   │
│   │                                                             │
│   ├──► ref.contains("#") ──► resolveExternalRefWithPointer()  │
│   │       │                                                     │
│   │       └──► Carregar schema externo + resolver pointer      │
│   │                                                             │
│   └──► URL reference ──► resolveUrlRef()                        │
│           │                                                     │
│           └──► HTTP fetch + parse + resolve                    │
└─────────────────────────────────────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────────────────────────────────────┐
│ CanonicalSchemaStore.cache()                                    │
│   │                                                             │
│   └──► Armazenar em cache para próximas referências             │
└─────────────────────────────────────────────────────────────────┘
        │
        ▼
        ✓ Retorna Schema resolvido
```

---

## 5. Roadmap de Implementação

### Visão Geral das Fases

```
FASE 1: Fundamentos (Semanas 1-2)
   ├── 5.1.1 - Modificar Schema.java
   ├── 5.1.2 - Modificar FileSchemaLoader
   └── 5.1.3 - Testes unitários básicos

FASE 2: Resolução Local (Semanas 2-3)
   ├── 5.2.1 - Modificar SchemaRefResolver
   ├── 5.2.2 - Implementar busca em definitions/$defs
   └── 5.2.3 - Testes de resolução local

FASE 3: Canonical Resolution (Semanas 3-4)
   ├── 5.3.1 - Criar CanonicalSchemaStore
   ├── 5.3.2 - Implementar cache de referências
   └── 5.3.3 - Testes de cache e performance

FASE 4: Recursos Avançados (Semanas 4-5)
   ├── 5.4.1 - Suporte a $id e $anchor
   ├── 5.4.2 - Detecção de referências circulares
   └── 5.4.3 - Preloading de dependências

FASE 5: Integração e Testes (Semanas 5-6)
   ├── 5.5.1 - Testes de integração completa
   ├── 5.5.2 - Testes de compatibilidade retroativa
   └── 5.5.3 - Documentação e examples
```

---

## 6. Tarefas Detalhadas por Fase

### FASE 1: Fundamentos

#### Tarefa 1.1: Modificar Schema.java
**Prioridade:** CRÍTICA | **Dependências:** Nenhuma | **Estimativa:** 4h

**Descrição:** Adicionar novos campos ao Schema para armazenar definições locais.

**Alterações necessárias:**

```java
// Novos campos a adicionar
private final Map<String, Schema> definitions;      // definitions (Draft-07)
private final Map<String, Schema> $defs;            // $defs (2019-09+)
private final String $id;                            // Schema identifier
private final String $anchor;                        // Anchor declaration

// Novos getters
public Map<String, Schema> getDefinitions() { ... }
public Map<String, Schema> get$Defs() { ... }
public String get$Id() { ... }
public boolean hasDefinitions() { ... }
public boolean has$Defs() { ... }

// Modificar construtor principal para incluir novos campos
public Schema(..., Map<String, Schema> definitions, 
              Map<String, Schema> $defs, String $id, String $anchor) {
    this.definitions = definitions == null ? Collections.emptyMap() : ...;
    this.$defs = $defs == null ? Collections.emptyMap() : ...;
    this.$id = $id;
    this.$anchor = $anchor;
}
```

**Arquivo:** `src/main/java/com/maiconjh/schemacr/schemes/Schema.java`

---

#### Tarefa 1.2: Modificar FileSchemaLoader
**Prioridade:** CRÍTICA | **Dependências:** Tarefa 1.1 | **Estimativa:** 6h

**Descrição:** Modificar para armazenar definitions e $defs no Schema ao invés de descartá-los.

**Alterações necessárias:**

```java
// No método toSchema(), adicionar extração de definitions/$defs:
private Schema toSchema(String name, Map<String, Object> raw) {
    // ... código existente ...
    
    // NOVOS: Extrair definitions
    Map<String, Schema> definitions = new HashMap<>();
    if (raw.containsKey("definitions") && raw.get("definitions") instanceof Map<?, ?> defs) {
        for (Map.Entry<?, ?> entry : defs.entrySet()) {
            if (entry.getValue() instanceof Map<?, ?> defMap) {
                String defName = String.valueOf(entry.getKey());
                definitions.put(defName, toSchema(defName, castMap(defMap)));
            }
        }
    }
    
    // NOVOS: Extrair $defs
    Map<String, Schema> $defs = new HashMap<>();
    if (raw.containsKey("$defs") && raw.get("$defs") instanceof Map<?, ?> defs) {
        for (Map.Entry<?, ?> entry : defs.entrySet()) {
            if (entry.getValue() instanceof Map<?, ?> defMap) {
                String defName = String.valueOf(entry.getKey());
                $defs.put(defName, toSchema(defName, castMap(defMap)));
            }
        }
    }
    
    // NOVOS: Extrair $id
    String $id = null;
    if (raw.containsKey("$id") && raw.get("$id") instanceof String id) {
        $id = id;
    }
    
    // Modificar construtor Schema para incluir novos campos
    return new Schema(name, type, properties, ..., definitions, $defs, $id, ...);
}
```

**Problema identificado:** O método `toSchema()` é recursivo e extrai definitions de cada nível. É necessário refatorar para:
1. Primeiro passo: Extrair definitions/$defs do nível raiz
2. Segundo passo: Criar schema sem resolver referências internas
3. Terceiro passo: Associar definitions ao schema raiz

**Solução proposta:** Usar padrão **Two-Pass Parsing**:
1. Primeiro parse: Extrair todo o conteúdo raw
2. Segundo parse: Criar Schema com definições resolvidas

**Arquivo:** `src/main/java/com/maiconjh/schemacr/schemes/FileSchemaLoader.java`

---

#### Tarefa 1.3: Testes Unitários - Fundamentos
**Prioridade:** ALTA | **Dependências:** Tarefas 1.1, 1.2 | **Estimativa:** 3h

**Criar testes em:** `src/test/java/com/maiconjh/schemacr/schemes/`

| Cenário | Entrada | Resultado Esperado |
|---------|---------|-------------------|
| Parse definitions | `{ "definitions": { "Player": {...} } }` | Schema com getDefinitions().get("Player") != null |
| Parse $defs | `{ "$defs": { "Address": {...} } }` | Schema com get$Defs().get("Address") != null |
| Parse $id | `{ "$id": "https://example.com/schema" }` | Schema.get$Id() == "https://example.com/schema" |
| Definitions vazio | `{}` | Schema.getDefinitions().isEmpty() == true |

---

### FASE 2: Resolução Local

#### Tarefa 2.1: Modificar SchemaRefResolver - Resoluçao Local
**Prioridade:** CRÍTICA | **Dependências:** Tarefas 1.1, 1.2 | **Estimativa:** 8h

**Descrição:** Modificar `resolveLocalRef()` para buscar definitions e $defs diretamente no Schema.

**Alterações necessárias:**

```java
private Schema resolveLocalRef(String ref, String currentSchemaName) {
    String path = ref.substring(2); // Remove "#/"
    String[] parts = path.split("/");
    
    // Obter schema raiz
    Schema current = registry.getSchema(currentSchemaName).orElse(null);
    if (current == null) {
        return null;
    }
    
    // Se o caminho começa com "definitions" ou "$defs"
    // buscar diretamente no mapa de definições
    if (parts.length >= 2) {
        String firstPart = parts[0];
        
        if ("definitions".equals(firstPart) && parts.length == 2) {
            // #/definitions/Player
            return current.getDefinitions().get(parts[1]);
        }
        
        if ("$defs".equals(firstPart) && parts.length == 2) {
            // #/$defs/Address
            return current.get$Defs().get(parts[1]);
        }
        
        if ("definitions".equals(firstPart) || "$defs".equals(firstPart)) {
            // Referência aninhada: #/definitions/Address/street
            String defName = parts[1];
            Schema defSchema = current.getDefinitions().get(defName);
            if (defSchema == null) {
                defSchema = current.get$Defs().get(defName);
            }
            
            if (defSchema != null) {
                // Continuar navegação a partir da definição
                for (int i = 2; i < parts.length; i++) {
                    String part = parts[i].replace("~1", "/").replace("~0", "~");
                    defSchema = navigateTo(part, defSchema);
                    if (defSchema == null) return null;
                }
                return defSchema;
            }
        }
    }
    
    // Fallback: navegação normal pelo schema (para referências a propriedades)
    Schema resolved = current;
    for (String part : parts) {
        if (resolved == null) {
            return null;
        }
        part = part.replace("~1", "/").replace("~0", "~");
        resolved = navigateTo(part, resolved);
    }
    
    return resolved;
}
```

**Também modificar `navigateTo()` para incluir definitions:**

```java
private Schema navigateTo(String part, Schema current) {
    // Verificar properties
    if (current.getProperties() != null && current.getProperties().containsKey(part)) {
        return current.getProperties().get(part);
    }
    
    // Verificar items
    if ("items".equals(part) && current.getItemSchema() != null) {
        return current.getItemSchema();
    }
    
    // NOVOS: Verificar definitions
    if (current.getDefinitions() != null && current.getDefinitions().containsKey(part)) {
        return current.getDefinitions().get(part);
    }
    
    // NOVOS: Verificar $defs
    if (current.get$Defs() != null && current.get$Defs().containsKey(part)) {
        return current.get$Defs().get(part);
    }
    
    return null;
}
```

**Arquivo:** `src/main/java/com/maiconjh/schemacr/schemes/SchemaRefResolver.java`

---

#### Tarefa 2.2: Testes de Resolução Local
**Prioridade:** ALTA | **Dependências:** Tarefa 2.1 | **Estimativa:** 4h

| Cenário | Schema | $ref | Resultado |
|---------|--------|------|-----------|
| definitions básico | `{ "definitions": { "Player": {...} } }` | `#/definitions/Player` | Retorna schema Player |
| $defs básico | `{ "$defs": { "Address": {...}} }` | `#/$defs/Address` | Retorna schema Address |
| definitions aninhado | definitions.Player com properties.street | `#/definitions/Player/street` | Retorna schema street |
| Referência inexistente | sem definitions | `#/definitions/Missing` | Retorna null |
| definitions优先 | ambos definitions e $defs com mesmo nome | `#/definitions/Name` | Retorna de definitions |

---

### FASE 3: Canonical Resolution

#### Tarefa 3.1: Criar CanonicalSchemaStore
**Prioridade:** ALTA | **Dependências:** Tarefa 2.1 | **Estimativa:** 6h

**Descrição:** Criar componente para armazenar em cache schemas já resolvidos.

**Arquivo:** `src/main/java/com/maiconjh/schemacr/schemes/CanonicalSchemaStore.java`

```java
package com.maiconjh.schemacr.schemes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Armazém centralizado para schemas resolvidos canonicamente.
 * 
 * <p>Implementa o padrão Canonical Resolution onde cada $ref é
 * resolvido uma única vez e o resultado é cacheado para uso posterior.</p>
 * 
 * <p>Funcionalidades:</p>
 * <ul>
 *   <li>Cache de definições resolvidas por schema + path</li>
 *   <li>Detecção de referências circulares</li>
 *   <li>Invalidação seletiva de cache</li>
 * </ul>
 */
public class CanonicalSchemaStore {

    // Cache: "schemaName#/definitions/Name" -> Schema resolvido
    private final Map<String, Schema> resolvedCache;
    
    // Cache de definições por schema: schemaName -> Map(defName -> Schema)
    private final Map<String, Map<String, Schema>> definitionsCache;
    
    // Referências em resolução (para detecção de ciclos)
    private final Map<String, Boolean> resolving;
    
    private final Logger logger;
    
    public CanonicalSchemaStore(Logger logger) {
        this.resolvedCache = new ConcurrentHashMap<>();
        this.definitionsCache = new ConcurrentHashMap<>();
        this.resolving = new ConcurrentHashMap<>();
        this.logger = logger;
    }
    
    /**
     * Obtém um schema do cache, se disponível.
     */
    public Schema get(String schemaName, String ref) {
        String key = schemaName + "#" + ref;
        return resolvedCache.get(key);
    }
    
    /**
     * Armazena um schema resolvido no cache.
     */
    public void put(String schemaName, String ref, Schema schema) {
        String key = schemaName + "#" + ref;
        resolvedCache.put(key, schema);
    }
    
    /**
     * Verifica se uma referência está sendo resolvida (circular).
     */
    public boolean isResolving(String schemaName, String ref) {
        String key = schemaName + "#" + ref;
        return resolving.containsKey(key);
    }
    
    /**
     * Marca o início da resolução de uma referência.
     */
    public void startResolving(String schemaName, String ref) {
        String key = schemaName + "#" + ref;
        resolving.put(key, true);
    }
    
    /**
     * Finaliza a resolução de uma referência.
     */
    public void endResolving(String schemaName, String ref) {
        String key = schemaName + "#" + ref;
        resolving.remove(key);
    }
    
    /**
     * Armazena definições de um schema para acesso rápido.
     */
    public void storeDefinitions(String schemaName, Map<String, Schema> definitions) {
        definitionsCache.put(schemaName, new ConcurrentHashMap<>(definitions));
    }
    
    /**
     * Obtém definições de um schema do cache.
     */
    public Map<String, Schema> getDefinitions(String schemaName) {
        return definitionsCache.get(schemaName);
    }
    
    /**
     * Limpa o cache de definições de um schema específico.
     */
    public void invalidate(String schemaName) {
        String prefix = schemaName + "#";
        resolvedCache.keySet().removeIf(key -> key.startsWith(prefix));
        definitionsCache.remove(schemaName);
    }
    
    /**
     * Limpa todo o cache.
     */
    public void clearAll() {
        resolvedCache.clear();
        definitionsCache.clear();
        resolving.clear();
    }
}
```

---

#### Tarefa 3.2: Integrar CanonicalSchemaStore ao SchemaRefResolver
**Prioridade:** ALTA | **Dependências:** Tarefa 3.1 | **Estimativa:** 4h

**Descrição:** Modificar `SchemaRefResolver` para usar o `CanonicalSchemaStore`.

**Alterações:**

```java
public class SchemaRefResolver {
    
    private final CanonicalSchemaStore canonicalStore;
    // ... campos existentes ...
    
    public SchemaRefResolver(SchemaRegistry registry, Logger logger) {
        // ... inicialização existente ...
        this.canonicalStore = new CanonicalSchemaStore(logger);
    }
    
    public Schema resolveRef(String ref, String currentSchemaName) {
        // Verificar cache primeiro
        Schema cached = canonicalStore.get(currentSchemaName, ref);
        if (cached != null) {
            return cached;
        }
        
        // Verificar referência circular
        if (canonicalStore.isResolving(currentSchemaName, ref)) {
            logger.warning("Circular reference detected: " + ref);
            return null;
        }
        
        try {
            canonicalStore.startResolving(currentSchemaName, ref);
            
            // ... lógica de resolução existente ...
            
            // Armazenar no cache
            if (resolved != null) {
                canonicalStore.put(currentSchemaName, ref, resolved);
            }
            
            return resolved;
        } finally {
            canonicalStore.endResolving(currentSchemaName, ref);
        }
    }
}
```

---

#### Tarefa 3.3: Testes de Canonical Resolution
**Prioridade:** ALTA | **Dependências:** Tarefa 3.2 | **Estimativa:** 3h

| Cenário | Descrição | Resultado |
|---------|-----------|-----------|
| Resolução única | Referência resolvida 2x | Segunda resolução usa cache |
| Referência circular | A -> B -> A | Detecta e retorna null com warning |
| Cache invalidado | Schema recarregado | Cache antigo invalidados |

---

### FASE 4: Recursos Avançados

#### Tarefa 4.1: Suporte a $id e $anchor
**Prioridade:** MÉDIA | **Dependências:** Tarefa 2.1 | **Estimativa:** 6h

**Descrição:** Implementar resolução por identificador único `$id`.

**Alterações necessárias:**

1. **Modificar SchemaRegistry:**

```java
public class SchemaRegistry {
    private final Map<String, Schema> schemasByName;    // existente
    private final Map<String, Schema> schemasById;      // NOVO: índice por $id
    
    public void registerSchema(String name, Schema schema) {
        schemasByName.put(name.toLowerCase(), schema);
        
        // Registrar por $id se presente
        if (schema.get$Id() != null) {
            schemasById.put(schema.get$Id(), schema);
        }
    }
    
    public Optional<Schema> getSchemaById(String id) {
        return Optional.ofNullable(schemasById.get(id));
    }
}
```

2. **Modificar SchemaRefResolver para suportar referências por ID:**

```java
// Suportar: {"$ref": "https://example.com/schemas/player.json"}
private Schema resolveIdRef(String ref) {
    // Verificar se é URL ou ID
    if (ref.startsWith("http://") || ref.startsWith("https://")) {
        // Buscar em cache primeiro
        Schema cached = externalSchemaCache.get(ref);
        if (cached != null) return cached;
        
        // Buscar no registry por $id
        Optional<Schema> byId = registry.getSchemaById(ref);
        if (byId.isPresent()) return byId.get();
        
        // Buscar por nome
        return registry.getSchema(ref).orElse(null);
    }
    
    // Referência relativa ou nome simples
    return registry.getSchema(ref).orElse(null);
}
```

---

#### Tarefa 4.2: Detecção de Referências Circulares Avançada
**Prioridade:** MÉDIA | **Dependências:** Tarefa 3.1 | **Estimativa:** 4h

**Descrição:** Melhorar detecção de ciclos com stack trace completo.

**Funcionalidades:**
- Registrar "stack" de resoluções para debugging
- Suporte a referências circulares válidas (recursão infinita controlada)
- Configuração para permitir/bloquear recursão

---

#### Tarefa 4.3: Preloading de Dependências
**Prioridade:** BAIXA | **Dependências:** Tarefa 3.2 | **Estimativa:** 5h

**Descrição:** Carregar schemas referenciados antecipadamente.

```java
public class SchemaRefResolver {
    
    /**
     * Preload de todas as dependências de um schema.
     */
    public void preloadDependencies(String schemaName) {
        Schema schema = registry.getSchema(schemaName).orElse(null);
        if (schema == null) return;
        
        // Recursively find all $ref in schema
        Set<String> refs = findAllRefs(schema);
        
        for (String ref : refs) {
            resolveRef(ref, schemaName);
        }
    }
    
    private Set<String> findAllRefs(Schema schema) {
        Set<String> refs = new HashSet<>();
        
        if (schema.isRef()) {
            refs.add(schema.getRef());
        }
        
        // Recurse into child schemas
        // ... (implementation details)
        
        return refs;
    }
}
```

---

### FASE 5: Integração e Testes

#### Tarefa 5.1: Testes de Integração Completa
**Prioridade:** CRÍTICA | **Dependências:** Todas as fases anteriores | **Estimativa:** 8h

**Criar testes de integração em:** `src/test/java/com/maiconjh/schemacr/integration/`

| Cenário | Descrição |
|---------|-----------|
| definitions + $ref | Schema com definitions resolve $ref corretamente |
| $defs + $ref | Schema com $defs resolve $ref corretamente |
| Referências externas | Schema externo com definitions resolve $ref |
| Misto | definitions local + $ref + allOf |
| Aninhado | definitions contém schema com propriedades que referenciam outras definitions |
| URL resolution | Schema referenciando URL externa |

**Exemplo de teste:**

```java
@Test
void testDefinitionsWithRef() throws Exception {
    // Given: Schema with definitions
    String schemaJson = """
        {
            "type": "object",
            "definitions": {
                "Address": {
                    "type": "object",
                    "properties": {
                        "street": { "type": "string" },
                        "city": { "type": "string" }
                    }
                }
            },
            "properties": {
                "billingAddress": { "$ref": "#/definitions/Address" },
                "shippingAddress": { "$ref": "#/definitions/Address" }
            }
        }
        """;
    
    // When
    Schema schema = loader.parseSchema("test", mapper.readValue(schemaJson, Map.class));
    
    // Then
    assertThat(schema.getDefinitions()).containsKey("Address");
    assertThat(schema.getDefinitions().get("Address").getProperties()).containsKey("street");
    
    // And: Resolution works
    Schema addressRef = refResolver.resolveRef("#/definitions/Address", "test");
    assertThat(addressRef).isNotNull();
    assertThat(addressRef.getType()).isEqualTo(SchemaType.OBJECT);
}
```

---

#### Tarefa 5.2: Testes de Compatibilidade Retroativa
**Prioridade:** CRÍTICA | **Dependências:** Tarefa 5.1 | **Estimativa:** 4h

**Garantir que schemas existentes continuem funcionando:**

| Cenário | Descrição |
|---------|-----------|
| Schema sem definitions | Comportamento inalterado |
| $ref para registry | Funciona como antes |
| Referência externa | Funciona como antes |
| Validação sem $ref | Inalterada |

---

#### Tarefa 5.3: Documentação e Exemplos
**Prioridade:** ALTA | **Dependências:** Tarefa 5.2 | **Estimativa:** 5h

**Atualizar documentação:**

1. **Novo arquivo:** `docs/reference/schema-references.md`
   - Tutorial completo de $ref
   - Exemplos com definitions e $defs
   - Melhores práticas

2. **Atualizar:** `docs/reference/json-schema.md`
   - Marcar $ref como ✅ Completo
   - Adicionar $defs como ✅ Implementado

3. **Criar examples:**
   - `src/main/resources/examples/schemas/definitions-example.schema.json`
   - `src/main/resources/examples/schemas/refs-nested.schema.json`

---

## 7. Estratégia de Migração Progressiva

### 7.1 Abordagem

A migração será feita em **modo de compatibilidade**, onde:

1. **Fase inicial**: Código novo funciona junto com código antigo
2. **Feature flag**: Opção de habilitar nova resolução via configuração
3. **Validação cruzada**: Ambos os métodos executam, comparar resultados
4. **Switch gradual**: Migrar schemas existentes aos poucos

### 7.2 Configuração de Migração

```yaml
# config.yml
schema:
  reference-resolution:
    mode: canonical    # legacy | canonical | hybrid
    cache-enabled: true
    cache-ttl-minutes: 30
    detect-circular: true
    allow-recursion: false
```

- **legacy**: Comportamento original (para testes)
- **canonical**: Nova implementação com cache
- **hybrid**: Tenta canonical, falha para legacy

### 7.3 Verificação de Saúde

```java
// Health check para validar nova arquitetura
public class SchemaArchitectureHealthCheck {
    
    public HealthStatus check() {
        // 1. Verificar que definitions são armazenadas no Schema
        // 2. Verificar que resolução local funciona
        // 3. Verificar que cache está funcionando
        // 4. Verificar que referências circulares são detectadas
        
        return HealthStatus.healthy();
    }
}
```

---

## 8. Plano de Testes Abrangente

### 8.1 Matriz de Testes de Referência

| Categoria | Cenário | Prioridade | Status Atual |
|-----------|---------|------------|--------------|
| **definitions** | Parse definitions simples | CRÍTICA | ❌ Falha |
| **definitions** | Parse definitions aninhado | CRÍTICA | ❌ Falha |
| **definitions** | $ref para definitions | CRÍTICA | ❌ Falha |
| **$defs** | Parse $defs simples | CRÍTICA | ✅ Parcial |
| **$defs** | Parse $defs aninhado | CRÍTICA | ✅ Parcial |
| **$defs** | $ref para $defs | CRÍTICA | ❌ Falha |
| **Local** | #/definitions/Name | CRÍTICA | ❌ Falha |
| **Local** | #/$defs/Name | CRÍTICA | ❌ Falha |
| **Local** | #/properties/name | ALTA | ✅ Funciona |
| **Local** | #/definitions/Name/property | ALTA | ❌ Falha |
| **External** | path/to/schema.json#... | ALTA | ✅ Parcial |
| **External** | URL reference | ALTA | ✅ Funciona |
| **Circular** | Auto-referência | MÉDIA | ✅ Detecta |
| **Circular** | A -> B -> A | MÉDIA | ✅ Detecta |
| **Registry** | Schema name | BAIXA | ✅ Funciona |

### 8.2 Testes Unitários a Criar

**Arquivos de teste:**
- `src/test/java/com/maiconjh/schemacr/schemes/SchemaDefinitionsTest.java`
- `src/test/java/com/maiconjh/schemacr/schemes/SchemaRefResolverLocalTest.java`
- `src/test/java/com/maiconjh/schemacr/schemes/CanonicalSchemaStoreTest.java`
- `src/test/java/com/maiconjh/schemacr/integration/SchemaReferenceIntegrationTest.java`

### 8.3 Testes de Performance

| Métrica | Meta |
|---------|------|
| Primeira resolução | < 10ms |
| Resolução em cache | < 1ms |
| 1000 resoluções | < 100ms total |
| Memória (100 schemas) | < 50MB |

---

## 9. Considerações de Compatibilidade Retroativa

### 9.1 Compromissos

1. **Sem quebra de API pública**:
   - Construtores existentes do `Schema` continuarão funcionando
   - Novos campos serão opcionais (null-safe)

2. **Comportamento inalterado**:
   - Schemas sem definitions/$defs funcionarão exatamente como antes
   - Referências via registry (nome) permanecerão suportadas

3. **Migração suave**:
   - Nova funcionalidade disponível via configuração
   - Legacy mode para diagnóstico de problemas

### 9.2 Possíveis Impactos

| Cenário | Impacto | Mitigação |
|---------|---------|-----------|
| Schema com definitions em nível não-raiz | Pode ter comportamento diferente | Documentar restrição |
| Referência para property vs definition com mesmo nome | Mudança de prioridade | definitions tem prioridade |
| Performance com muitos schemas | Aumento de memória | TTL de cache configurável |

### 9.3 Depreciações Futuras

Após período de estabilidade (sugestão: 2 releases):

| Funcionalidade | Substituta | Versão Planejada |
|----------------|------------|------------------|
| `SchemaRefResolver` legacy | `CanonicalSchemaStore` | v0.4.0 |
| Resolução sem cache | Resolução com cache | v0.4.0 |

---

## 10. Riscos e Mitigações

| Risco | Probabilidade | Impacto | Mitigação |
|-------|---------------|---------|-----------|
| Performance degradada | Baixa | Alto | Benchmarking contínuo |
| Referências circulares não detectadas | Média | Médio | Testes específicos |
| Memória excessiva em cache | Média | Médio | TTL e limites configuráveis |
| Compatibilidade com schemas existentes | Baixa | Alto | Testes de regressão extensivos |
| Complexidade de código | Alta | Médio | Documentação e código limpo |

---

## 11. Critérios de Sucesso

### 11.1 Condições para Release

- [ ] Todos os testes de referência passando
- [ ] Compatibilidade retroativa verificada
- [ ] Performance dentro das metas
- [ ] Documentação atualizada
- [ ] Exemplos funcionais

### 11.2 Métricas de Qualidade

| Métrica | Meta |
|---------|------|
| Cobertura de testes | > 80% |
| Testes de referência passando | 100% |
| Tempo de resolução (cache) | < 1ms |
| Memória por schema | < 1KB adicional |

---

## 12. Conclusão

Este plano fornece um roteiro completo para implementar suporte total a referências JSON Schema no Schema-Validator, mantendo compatibilidade com a arquitetura existente. A abordagem de migração progressiva permite validar cada etapa antes de avançar, minimizando riscos e garantindo qualidade.

### Próximos Passos Imediatos

1. **Revisar e validar** este plano
2. **Iniciar FASE 1**: Modificar Schema.java
3. **Configurar ambiente** de testes
4. **Criar primeiro build** de validação

---

*Documento gerado automaticamente para o projeto Schema-Validator*  
*Para dúvidas, consulte a documentação em docs/*