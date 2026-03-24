# Schema-Validator — Plano de Implementação de Funcionalidades Faltantes (v3)

**Gerado em:** 2026-03-24

**Projeto:** MaiconJh/Schema-Validator

**Versão do Documento:** 3

**Baseado em:** reference-links-audit-v2.md e code-audit-2026-03.md

---

## Resumo Executivo

Este documento apresenta um plano de implementação abrangente para as funcionalidades de validação JSON Schema que estão faltando no Schema-Validator versão 0.5.0. O plano foi desenvolvido com base na análise de bibliotecas de referência (everit-org/json-schema e networknt/json-schema-validator) e auditoria técnica do código existente.

### Visão Geral das Lacunas Identificadas

O projeto atual oferece suporte básico para validação de estruturas JSON, porém apresenta lacunas significativas em áreas essenciais para conformidade completa com as especificações JSON Schema Draft 2019-09 e 2020-12. As principais áreas que necessitam implementação são:

**Validação de Arrays:** O [`ArrayValidator.java`](src/main/java/com/maiconjh/schemacr/validation/ArrayValidator.java:1) atualmente suporta apenas a keyword `items` para validação de elementos. Faltam implementações para controle de quantidade (minItems, maxItems), verificação de unicidade (uniqueItems), validação posicional (prefixItems) e controle de itens adicionais (additionalItems).

**Validação de Objetos:** O [`ObjectValidator.java`](src/main/java/com/maiconjh/schemacr/validation/ObjectValidator.java:1) possui implementações parciais para required, properties, patternProperties e additionalProperties (booleano). Necessita adicionar validação de quantidade de propriedades (minProperties, maxProperties), dependências condicionais (dependentRequired, dependentSchemas) e suporte a additionalProperties como schema.

**Keywords Complementares:** Falta a keyword `const` para validação de valores constantes exatos, além das keywords de metadados `readOnly` e `writeOnly` para controle de operações de leitura/escrita.

**Resolução de Referências:** O [`SchemaRefResolver.java`](src/main/java/com/maiconjh/schemacr/schemes/SchemaRefResolver.java:1) precisa expandir sua capacidade de navegação para suportar referências dentro de estruturas complexas como `prefixItems`, `allOf` e `anyOf`.

### Estatísticas de Funcionalidades Faltantes por Prioridade

| Prioridade | Área | Funcionalidades Faltantes | Total |
|------------|------|---------------------------|-------|
| **Crítica** | Array Constraints | minItems, maxItems, uniqueItems, prefixItems, additionalItems | 5 |
| **Crítica** | Object Constraints | minProperties, maxProperties, dependentRequired, dependentSchemas, additionalProperties (schema) | 5 |
| **Importante** | Keywords Complementares | const, readOnly, writeOnly | 3 |
| **Importante** | Navegação SchemaRefResolver | prefixItems, allOf, anyOf no navigateTo() | 3 |
| **Total** | — | — | **16** |

---

## Prioridade Crítica

### 1. Array Constraints (ArrayValidator.java)

#### Descrição

Implementar validações de array que estão faltando no módulo de validação de arrays do Schema-Validator. Esta funcionalidade é classificada como crítica porque arrays são estruturas fundamentais em JSON e a validação completa de suas características é essencial para conformidade com JSON Schema Draft 2019-09/2020-12.

#### Funcionalidades Faltantes

| Keyword | Descrição | Referência Principal |
|---------|-----------|---------------------|
| **minItems** | Valida que o array contém pelo menos N elementos | [`MinItemsValidator.java`](https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/MinItemsValidator.java) |
| **maxItems** | Valida que o array contém no máximo N elementos | [`MaxItemsValidator.java`](https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/MaxItemsValidator.java) |
| **uniqueItems** | Valida que todos os elementos do array são únicos | [`UniqueItemsValidator.java`](https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/UniqueItemsValidator.java) |
| **prefixItems** | Valida elementos em posições específicas (Draft 2020-12) | [`PrefixItemsValidator.java`](https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/PrefixItemsValidator.java) |
| **additionalItems** | Controla itens além dos definidos em prefixItems | [`ArraySchema.java`](https://raw.githubusercontent.com/everit-org/json-schema/master/core/src/main/java/org/everit/json/schema/ArraySchema.java) |

#### Benefícios Esperados

- Conformidade com JSON Schema Draft 2019-09/2020-12
- Validação completa de estruturas de arrays
- Suporte a schemas complexos com validação posicional
- Capacidade de garantir unicidade em collections
- Implementação de padrões de indústria testados em bibliotecas maduras

#### Guia de Implementação

##### 1.1. Criar MinItemsValidator.java

Localização sugerida: `src/main/java/com/maiconjh/schemacr/validation/array/`

```java
package com.maiconjh.schemacr.validation.array;

import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.ValidationResult;
import com.maiconjh.schemacr.validation.Validator;
import com.maiconjh.schemacr.schemes.Schema;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for the minItems JSON Schema keyword.
 * Ensures that an array contains a minimum number of elements.
 */
public class MinItemsValidator implements Validator {
    
    private final int minItems;
    
    public MinItemsValidator(int minItems) {
        this.minItems = minItems;
    }
    
    @Override
    public ValidationResult validate(Object value, Schema schema) {
        List<ValidationError> errors = new ArrayList<>();
        
        if (value == null) {
            return new ValidationResult(errors);
        }
        
        if (value instanceof List) {
            List<?> array = (List<?>) value;
            if (array.size() < minItems) {
                errors.add(new ValidationError(
                    "minItems",
                    String.format("array must contain at least %d items, but contains %d", 
                        minItems, array.size())
                ));
            }
        }
        
        return new ValidationResult(errors);
    }
    
    @Override
    public String getKeyword() {
        return "minItems";
    }
}
```

##### 1.2. Criar MaxItemsValidator.java

```java
package com.maiconjh.schemacr.validation.array;

import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.ValidationResult;
import com.maiconjh.schemacr.validation.Validator;
import com.maiconjh.schemacr.schemes.Schema;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for the maxItems JSON Schema keyword.
 * Ensures that an array contains at most a specified number of elements.
 */
public class MaxItemsValidator implements Validator {
    
    private final int maxItems;
    
    public MaxItemsValidator(int maxItems) {
        this.maxItems = maxItems;
    }
    
    @Override
    public ValidationResult validate(Object value, Schema schema) {
        List<ValidationError> errors = new ArrayList<>();
        
        if (value == null) {
            return new ValidationResult(errors);
        }
        
        if (value instanceof List) {
            List<?> array = (List<?>) value;
            if (array.size() > maxItems) {
                errors.add(new ValidationError(
                    "maxItems",
                    String.format("array must contain at most %d items, but contains %d", 
                        maxItems, array.size())
                ));
            }
        }
        
        return new ValidationResult(errors);
    }
    
    @Override
    public String getKeyword() {
        return "maxItems";
    }
}
```

##### 1.3. Criar UniqueItemsValidator.java

```java
package com.maiconjh.schemacr.validation.array;

import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.ValidationResult;
import com.maiconjh.schemacr.validation.Validator;
import com.maiconjh.schemacr.schemes.Schema;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validator for the uniqueItems JSON Schema keyword.
 * Ensures that all elements in an array are unique.
 */
public class UniqueItemsValidator implements Validator {
    
    public UniqueItemsValidator() {
    }
    
    @Override
    public ValidationResult validate(Object value, Schema schema) {
        List<ValidationError> errors = new ArrayList<>();
        
        if (value == null) {
            return new ValidationResult(errors);
        }
        
        if (value instanceof List) {
            List<?> array = (List<?>) value;
            List<Object> duplicates = findDuplicates(array);
            
            if (!duplicates.isEmpty()) {
                errors.add(new ValidationError(
                    "uniqueItems",
                    String.format("array items must be unique, but found duplicates: %s", duplicates)
                ));
            }
        }
        
        return new ValidationResult(errors);
    }
    
    private List<Object> findDuplicates(List<?> array) {
        List<Object> duplicates = new ArrayList<>();
        Set<Object> seen = new HashSet<>();
        
        for (Object item : array) {
            // For complex objects, consider using a deep equality check
            // For now, use simple equality
            if (!seen.add(item) && !duplicates.contains(item)) {
                duplicates.add(item);
            }
        }
        
        return duplicates;
    }
    
    @Override
    public String getKeyword() {
        return "uniqueItems";
    }
}
```

##### 1.4. Criar PrefixItemsValidator.java

```java
package com.maiconjh.schemacr.validation.array;

import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.ValidationResult;
import com.maiconjh.schemacr.validation.Validator;
import com.maiconjh.schemacr.schemes.Schema;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for the prefixItems keyword (Draft 2020-12).
 * Validates array items against schemas at specific positions.
 */
public class PrefixItemsValidator implements Validator {
    
    private final List<Schema> itemSchemas;
    
    public PrefixItemsValidator(List<Schema> itemSchemas) {
        this.itemSchemas = itemSchemas;
    }
    
    @Override
    public ValidationResult validate(Object value, Schema schema) {
        List<ValidationError> errors = new ArrayList<>();
        
        if (value == null) {
            return new ValidationResult(errors);
        }
        
        if (value instanceof List) {
            List<?> array = (List<?>) value;
            
            // Validate each position up to the number of schemas defined
            for (int i = 0; i < Math.min(array.size(), itemSchemas.size()); i++) {
                Object item = array.get(i);
                Schema itemSchema = itemSchemas.get(i);
                
                // Use existing validation logic
                ValidationResult itemResult = itemSchema.validate(item);
                if (!itemResult.isValid()) {
                    // Prefix errors with position index
                    for (ValidationError error : itemResult.getErrors()) {
                        errors.add(new ValidationError(
                            "prefixItems[" + i + "]." + error.getKeyword(),
                            "at position " + i + ": " + error.getMessage(),
                            error.getPath()
                        ));
                    }
                }
            }
        }
        
        return new ValidationResult(errors);
    }
    
    @Override
    public String getKeyword() {
        return "prefixItems";
    }
}
```

##### 1.5. Criar AdditionalItemsValidator.java

```java
package com.maiconjh.schemacr.validation.array;

import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.ValidationResult;
import com.maiconjh.schemacr.validation.Validator;
import com.maiconjh.schemacr.schemes.Schema;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for the additionalItems keyword.
 * Validates items beyond the defined prefixItems schemas.
 */
public class AdditionalItemsValidator implements Validator {
    
    private final Schema additionalSchema;
    private final int prefixItemsCount;
    
    public AdditionalItemsValidator(Schema additionalSchema, int prefixItemsCount) {
        this.additionalSchema = additionalSchema;
        this.prefixItemsCount = prefixItemsCount;
    }
    
    @Override
    public ValidationResult validate(Object value, Schema schema) {
        List<ValidationError> errors = new ArrayList<>();
        
        if (value == null || additionalSchema == null) {
            return new ValidationResult(errors);
        }
        
        if (value instanceof List) {
            List<?> array = (List<?>) value;
            
            // Validate items beyond the prefixItems count
            for (int i = prefixItemsCount; i < array.size(); i++) {
                Object item = array.get(i);
                ValidationResult itemResult = additionalSchema.validate(item);
                
                if (!itemResult.isValid()) {
                    for (ValidationError error : itemResult.getErrors()) {
                        errors.add(new ValidationError(
                            "additionalItems." + error.getKeyword(),
                            "at position " + i + ": " + error.getMessage(),
                            error.getPath()
                        ));
                    }
                }
            }
        }
        
        return new ValidationResult(errors);
    }
    
    @Override
    public String getKeyword() {
        return "additionalItems";
    }
}
```

##### 1.6. Integrar no ArrayValidator.java Existente

Modificar [`ArrayValidator.java`](src/main/java/com/maiconjh/schemacr/validation/ArrayValidator.java:1) para incluir os novos validators via ValidatorDispatcher:

```java
package com.maiconjh.schemacr.validation;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validator for array type schemas.
 */
public class ArrayValidator implements Validator {
    
    private final Schema itemsSchema;
    private final Schema additionalItemsSchema;
    private final Integer minItems;
    private final Integer maxItems;
    private final Boolean uniqueItems;
    private final List<Schema> prefixItemsSchemas;
    private final Boolean additionalItems;
    
    // Existing validators collection
    private final List<Validator> validators = new ArrayList<>();
    
    public ArrayValidator(Map<String, Object> schema) {
        // Parse schema properties
        Object items = schema.get("items");
        if (items instanceof Map) {
            this.itemsSchema = SchemaType.OBJECT.fromJson((Map<String, Object>) items);
        } else {
            this.itemsSchema = null;
        }
        
        // minItems
        Object minItemsVal = schema.get("minItems");
        this.minItems = minItemsVal instanceof Integer ? (Integer) minItemsVal : null;
        
        // maxItems
        Object maxItemsVal = schema.get("maxItems");
        this.maxItems = maxItemsVal instanceof Integer ? (Integer) maxItemsVal : null;
        
        // uniqueItems
        this.uniqueItems = Boolean.TRUE.equals(schema.get("uniqueItems"));
        
        // prefixItems (Draft 2020-12)
        Object prefixItems = schema.get("prefixItems");
        if (prefixItems instanceof List) {
            this.prefixItemsSchemas = new ArrayList<>();
            for (Object item : (List<?>) prefixItems) {
                if (item instanceof Map) {
                    this.prefixItemsSchemas.add(SchemaType.OBJECT.fromJson((Map<String, Object>) item));
                }
            }
        } else {
            this.prefixItemsSchemas = null;
        }
        
        // additionalItems
        Object additionalItemsVal = schema.get("additionalItems");
        if (additionalItemsVal instanceof Map) {
            this.additionalItemsSchema = SchemaType.OBJECT.fromJson((Map<String, Object>) additionalItemsVal);
        } else {
            this.additionalItemsSchema = null;
        }
        this.additionalItems = additionalItemsVal instanceof Boolean ? (Boolean) additionalItemsVal : null;
        
        // Initialize validators based on available schema properties
        initializeValidators();
    }
    
    private void initializeValidators() {
        // Add items validator if present
        if (itemsSchema != null) {
            // Reuse existing items validation logic
        }
        
        // Add minItems validator
        if (minItems != null) {
            validators.add(new com.maiconjh.schemacr.validation.array.MinItemsValidator(minItems));
        }
        
        // Add maxItems validator
        if (maxItems != null) {
            validators.add(new com.maiconjh.schemacr.validation.array.MaxItemsValidator(maxItems));
        }
        
        // Add uniqueItems validator
        if (uniqueItems != null && uniqueItems) {
            validators.add(new com.maiconjh.schemacr.validation.array.UniqueItemsValidator());
        }
        
        // Add prefixItems validator
        if (prefixItemsSchemas != null && !prefixItemsSchemas.isEmpty()) {
            validators.add(new com.maiconjh.schemacr.validation.array.PrefixItemsValidator(prefixItemsSchemas));
        }
        
        // Add additionalItems validator
        if (additionalItemsSchema != null) {
            int prefixCount = prefixItemsSchemas != null ? prefixItemsSchemas.size() : 0;
            validators.add(new com.maiconjh.schemacr.validation.array.AdditionalItemsValidator(
                additionalItemsSchema, prefixCount));
        }
    }
    
    @Override
    public ValidationResult validate(Object value, Schema schema) {
        List<ValidationError> errors = new ArrayList<>();
        
        if (value == null) {
            return new ValidationResult(errors);
        }
        
        if (!(value instanceof List)) {
            errors.add(new ValidationError("type", "expected array"));
            return new ValidationResult(errors);
        }
        
        List<?> array = (List<?>) value;
        
        // Run all validators
        for (Validator validator : validators) {
            ValidationResult result = validator.validate(value, schema);
            errors.addAll(result.getErrors());
        }
        
        return new ValidationResult(errors);
    }
    
    @Override
    public String getKeyword() {
        return "array";
    }
}
```

#### Critérios de Teste

##### Testes Unitários

| Cenário | Entrada | Resultado Esperado |
|---------|---------|-------------------|
| minItems válido | [1, 2, 3] com minItems=2 | Válido |
| minItems inválido | [1] com minItems=2 | Erro: "array must contain at least 2 items" |
| maxItems válido | [1, 2] com maxItems=3 | Válido |
| maxItems inválido | [1, 2, 3, 4] com maxItems=3 | Erro: "array must contain at most 3 items" |
| uniqueItems válido | [1, 2, 3] | Válido |
| uniqueItems inválido | [1, 2, 2] | Erro: "array items must be unique" |
| prefixItems válido | [{"type": "string"}, {"type": "number"}] com ["hello", 42] | Válido |
| prefixItems inválido | [{"type": "string"}, {"type": "number"}] com ["hello", "world"] | Erro na posição 1 |

##### Testes de Integração

- Schema complexo contendo todas as keywords de array combinadas
- Arrays aninhados com validação em múltiplos níveis
- prefixItems combinado com additionalItems schema

##### Casos de Borda (Edge Cases)

| Cenário | Comportamento Esperado |
|---------|----------------------|
| Array vazio [] com minItems=0 | Válido |
| Array vazio [] com minItems=1 | Inválido |
| Array com 1 elemento | uniqueItems deve considerar não-duplicado |
| Objetos aninhados em uniqueItems | Usar comparação profunda |
| prefixItems com array menor que schemas | Apenas validar até o tamanho do array |

#### Riscos e Mitigação

| Risco | Probabilidade | Impacto | Mitigação |
|-------|---------------|---------|-----------|
| Colisão com validação existente de items | Baixa | Médio | Testes de regressão antes da integração |
| Performance com uniqueItems em grandes arrays | Média | Médio | Considerar otimização com HashSet customizado |
| Incompatibilidade com Draft 2019-09 | Baixa | Alto | Documentar versão do Draft suportada |

#### Procedimento de Rollback

1. Remover arquivos criados (`MinItemsValidator.java`, `MaxItemsValidator.java`, etc.)
2. Reverter alterações no `ArrayValidator.java`
3. Executar testes existentes para garantir que a funcionalidade original não foi afetada
4. Se testes falharem, manter a versão anterior do ArrayValidator.java

---

### 2. Object Constraints (ObjectValidator.java)

#### Descrição

Implementar validações de objeto que estão faltando no módulo de validação de objetos do Schema-Validator. Esta funcionalidade é crítica para validação completa de estruturas de dados complexas em aplicações reais.

#### Funcionalidades Faltantes

| Keyword | Descrição | Referência Principal |
|---------|-----------|---------------------|
| **minProperties** | Valida que o objeto possui pelo menos N propriedades | [`MinPropertiesValidator.java`](https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/MinPropertiesValidator.java) |
| **maxProperties** | Valida que o objeto possui no máximo N propriedades | [`MaxPropertiesValidator.java`](https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/MaxPropertiesValidator.java) |
| **dependentRequired** | Valida propriedades requeridas condicionalmente baseadas em outras propriedades | [`DependentRequiredValidator.java`](https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/DependentRequiredValidator.java) |
| **dependentSchemas** | Aplica schemas baseados em propriedades presentes | [`DependentSchemasValidator.java`](https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/DependentSchemasValidator.java) |
| **additionalProperties (schema)** | Suporte a additionalProperties como schema (não apenas booleano) | [`ObjectSchema.java`](https://raw.githubusercontent.com/everit-org/json-schema/master/core/src/main/java/org/everit/json/schema/ObjectSchema.java) |

#### Benefícios Esperados

- Validação completa de estrutura de objetos
- Suporte a dependências entre propriedades (cenários do mundo real)
- Conformidade com JSON Schema Draft 2019-09/2020-12
- Flexibilidade para validação condicional baseada no estado do objeto

#### Guia de Implementação

##### 2.1. Criar MinPropertiesValidator.java

```java
package com.maiconjh.schemacr.validation.object;

import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.ValidationResult;
import com.maiconjh.schemacr.validation.Validator;
import com.maiconjh.schemacr.schemes.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validator for the minProperties JSON Schema keyword.
 * Ensures that an object contains a minimum number of properties.
 */
public class MinPropertiesValidator implements Validator {
    
    private final int minProperties;
    
    public MinPropertiesValidator(int minProperties) {
        this.minProperties = minProperties;
    }
    
    @Override
    public ValidationResult validate(Object value, Schema schema) {
        List<ValidationError> errors = new ArrayList<>();
        
        if (value == null) {
            return new ValidationResult(errors);
        }
        
        if (value instanceof Map) {
            Map<?, ?> object = (Map<?, ?>) value;
            if (object.size() < minProperties) {
                errors.add(new ValidationError(
                    "minProperties",
                    String.format("object must have at least %d properties, but has %d", 
                        minProperties, object.size())
                ));
            }
        }
        
        return new ValidationResult(errors);
    }
    
    @Override
    public String getKeyword() {
        return "minProperties";
    }
}
```

##### 2.2. Criar MaxPropertiesValidator.java

```java
package com.maiconjh.schemacr.validation.object;

import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.ValidationResult;
import com.maiconjh.schemacr.validation.Validator;
import com.maiconjh.schemacr.schemes.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validator for the maxProperties JSON Schema keyword.
 * Ensures that an object contains at most a specified number of properties.
 */
public class MaxPropertiesValidator implements Validator {
    
    private final int maxProperties;
    
    public MaxPropertiesValidator(int maxProperties) {
        this.maxProperties = maxProperties;
    }
    
    @Override
    public ValidationResult validate(Object value, Schema schema) {
        List<ValidationError> errors = new ArrayList<>();
        
        if (value == null) {
            return new ValidationResult(errors);
        }
        
        if (value instanceof Map) {
            Map<?, ?> object = (Map<?, ?>) value;
            if (object.size() > maxProperties) {
                errors.add(new ValidationError(
                    "maxProperties",
                    String.format("object must have at most %d properties, but has %d", 
                        maxProperties, object.size())
                ));
            }
        }
        
        return new ValidationResult(errors);
    }
    
    @Override
    public String getKeyword() {
        return "maxProperties";
    }
}
```

##### 2.3. Criar DependentRequiredValidator.java

```java
package com.maiconjh.schemacr.validation.object;

import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.ValidationResult;
import com.maiconjh.schemacr.validation.Validator;
import com.maiconjh.schemacr.schemes.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validator for the dependentRequired JSON Schema keyword (Draft 2019-09).
 * Validates that required properties are present when a dependent property exists.
 */
public class DependentRequiredValidator implements Validator {
    
    private final Map<String, List<String>> dependencies;
    
    public DependentRequiredValidator(Map<String, List<String>> dependencies) {
        this.dependencies = dependencies;
    }
    
    @Override
    public ValidationResult validate(Object value, Schema schema) {
        List<ValidationError> errors = new ArrayList<>();
        
        if (value == null || dependencies == null || dependencies.isEmpty()) {
            return new ValidationResult(errors);
        }
        
        if (value instanceof Map) {
            Map<?, ?> object = (Map<?, ?>) value;
            
            // Check each dependency
            for (Map.Entry<String, List<String>> entry : dependencies.entrySet()) {
                String dependentProperty = entry.getKey();
                List<String> requiredProperties = entry.getValue();
                
                // If the dependent property exists in the object
                if (object.containsKey(dependentProperty)) {
                    // Check if all required properties are present
                    for (String requiredProp : requiredProperties) {
                        if (!object.containsKey(requiredProp)) {
                            errors.add(new ValidationError(
                                "dependentRequired",
                                String.format("property '%s' requires property '%s' to be present", 
                                    dependentProperty, requiredProp)
                            ));
                        }
                    }
                }
            }
        }
        
        return new ValidationResult(errors);
    }
    
    @Override
    public String getKeyword() {
        return "dependentRequired";
    }
}
```

##### 2.4. Criar DependentSchemasValidator.java

```java
package com.maiconjh.schemacr.validation.object;

import com.maiconjh.schemacr.validation.ValidationError;
import com.maiconjh.schemacr.validation.ValidationResult;
import com.maiconjh.schemacr.validation.Validator;
import com.maiconjh.schemacr.schemes.Schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Validator for the dependentSchemas JSON Schema keyword (Draft 2019-09).
 * Applies a schema based on the presence of certain properties.
 */
public class DependentSchemasValidator implements Validator {
    
    private final Map<String, Schema> dependentSchemas;
    
    public DependentSchemasValidator(Map<String, Schema> dependentSchemas) {
        this.dependentSchemas = dependentSchemas;
    }
    
    @Override
    public ValidationResult validate(Object value, Schema schema) {
        List<ValidationError> errors = new ArrayList<>();
        
        if (value == null || dependentSchemas == null || dependentSchemas.isEmpty()) {
            return new ValidationResult(errors);
        }
        
        if (value instanceof Map) {
            Map<?, ?> object = (Map<?, ?>) value;
            
            // Check each dependent schema
            for (Map.Entry<String, Schema> entry : dependentSchemas.entrySet()) {
                String dependentProperty = entry.getKey();
                Schema dependentSchema = entry.getValue();
                
                // If the dependent property exists in the object
                if (object.containsKey(dependentProperty)) {
                    // Apply the dependent schema to the entire object
                    ValidationResult result = dependentSchema.validate(object);
                    if (!result.isValid()) {
                        for (ValidationError error : result.getErrors()) {
                            errors.add(new ValidationError(
                                "dependentSchemas." + dependentProperty + "." + error.getKeyword(),
                                error.getMessage(),
                                error.getPath()
                            ));
                        }
                    }
                }
            }
        }
        
        return new ValidationResult(errors);
    }
    
    @Override
    public String getKeyword() {
        return "dependentSchemas";
    }
}
```

##### 2.5. Atualizar ObjectValidator.java

Modificar [`ObjectValidator.java`](src/main/java/com/maiconjh/schemacr/validation/ObjectValidator.java:1) para suportar additionalProperties como schema:

```java
package com.maiconjh.schemacr.validation;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.schemes.SchemaType;
import com.maiconjh.schemacr.validation.object.MinPropertiesValidator;
import com.maiconjh.schemacr.validation.object.MaxPropertiesValidator;
import com.maiconjh.schemacr.validation.object.DependentRequiredValidator;
import com.maiconjh.schemacr.validation.object.DependentSchemasValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validator for object type schemas.
 */
public class ObjectValidator implements Validator {
    
    private final List<String> required;
    private final Map<String, Schema> properties;
    private final Map<String, Schema> patternProperties;
    private final Schema additionalPropertiesSchema;
    private final Boolean additionalPropertiesBoolean;
    private final Integer minProperties;
    private final Integer maxProperties;
    private final Map<String, List<String>> dependentRequired;
    private final Map<String, Schema> dependentSchemas;
    
    private final List<Validator> validators = new ArrayList<>();
    
    public ObjectValidator(Map<String, Object> schema) {
        // Parse required
        Object requiredObj = schema.get("required");
        if (requiredObj instanceof List) {
            this.required = new ArrayList<>();
            for (Object item : (List<?>) requiredObj) {
                if (item instanceof String) {
                    this.required.add((String) item);
                }
            }
        } else {
            this.required = new ArrayList<>();
        }
        
        // Parse properties
        Object propertiesObj = schema.get("properties");
        if (propertiesObj instanceof Map) {
            this.properties = new HashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) propertiesObj).entrySet()) {
                if (entry.getKey() instanceof String && entry.getValue() instanceof Map) {
                    String key = (String) entry.getKey();
                    Schema propSchema = SchemaType.OBJECT.fromJson((Map<String, Object>) entry.getValue());
                    this.properties.put(key, propSchema);
                }
            }
        } else {
            this.properties = new HashMap<>();
        }
        
        // Parse patternProperties
        Object patternPropsObj = schema.get("patternProperties");
        if (patternPropsObj instanceof Map) {
            this.patternProperties = new HashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) patternPropsObj).entrySet()) {
                if (entry.getKey() instanceof String && entry.getValue() instanceof Map) {
                    String pattern = (String) entry.getKey();
                    Schema propSchema = SchemaType.OBJECT.fromJson((Map<String, Object>) entry.getValue());
                    this.patternProperties.put(pattern, propSchema);
                }
            }
        } else {
            this.patternProperties = new HashMap<>();
        }
        
        // Parse additionalProperties - support both boolean and schema
        Object additionalPropsObj = schema.get("additionalProperties");
        if (additionalPropsObj instanceof Boolean) {
            this.additionalPropertiesBoolean = (Boolean) additionalPropsObj;
            this.additionalPropertiesSchema = null;
        } else if (additionalPropsObj instanceof Map) {
            this.additionalPropertiesSchema = SchemaType.OBJECT.fromJson((Map<String, Object>) additionalPropsObj);
            this.additionalPropertiesBoolean = null;
        } else {
            this.additionalPropertiesBoolean = true;
            this.additionalPropertiesSchema = null;
        }
        
        // Parse minProperties
        Object minPropsObj = schema.get("minProperties");
        this.minProperties = minPropsObj instanceof Integer ? (Integer) minPropsObj : null;
        
        // Parse maxProperties
        Object maxPropsObj = schema.get("maxProperties");
        this.maxProperties = maxPropsObj instanceof Integer ? (Integer) maxPropsObj : null;
        
        // Parse dependentRequired
        Object depReqObj = schema.get("dependentRequired");
        if (depReqObj instanceof Map) {
            this.dependentRequired = new HashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) depReqObj).entrySet()) {
                if (entry.getKey() instanceof String && entry.getValue() instanceof List) {
                    String key = (String) entry.getKey();
                    List<String> reqProps = new ArrayList<>();
                    for (Object item : (List<?>) entry.getValue()) {
                        if (item instanceof String) {
                            reqProps.add((String) item);
                        }
                    }
                    this.dependentRequired.put(key, reqProps);
                }
            }
        } else {
            this.dependentRequired = new HashMap<>();
        }
        
        // Parse dependentSchemas
        Object depSchemasObj = schema.get("dependentSchemas");
        if (depSchemasObj instanceof Map) {
            this.dependentSchemas = new HashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) depSchemasObj).entrySet()) {
                if (entry.getKey() instanceof String && entry.getValue() instanceof Map) {
                    String key = (String) entry.getKey();
                    Schema depSchema = SchemaType.OBJECT.fromJson((Map<String, Object>) entry.getValue());
                    this.dependentSchemas.put(key, depSchema);
                }
            }
        } else {
            this.dependentSchemas = new HashMap<>();
        }
        
        initializeValidators();
    }
    
    private void initializeValidators() {
        // Add minProperties validator
        if (minProperties != null) {
            validators.add(new MinPropertiesValidator(minProperties));
        }
        
        // Add maxProperties validator
        if (maxProperties != null) {
            validators.add(new MaxPropertiesValidator(maxProperties));
        }
        
        // Add dependentRequired validator
        if (!dependentRequired.isEmpty()) {
            validators.add(new DependentRequiredValidator(dependentRequired));
        }
        
        // Add dependentSchemas validator
        if (!dependentSchemas.isEmpty()) {
            validators.add(new DependentSchemasValidator(dependentSchemas));
        }
    }
    
    @Override
    public ValidationResult validate(Object value, Schema schema) {
        List<ValidationError> errors = new ArrayList<>();
        
        if (value == null) {
            return new ValidationResult(errors);
        }
        
        if (!(value instanceof Map)) {
            errors.add(new ValidationError("type", "expected object"));
            return new ValidationResult(errors);
        }
        
        Map<?, ?> object = (Map<?, ?>) value;
        Set<?> objectKeys = object.keySet();
        
        // Validate required properties
        for (String reqProp : required) {
            if (!object.containsKey(reqProp)) {
                errors.add(new ValidationError("required", 
                    String.format("property '%s' is required", reqProp)));
            }
        }
        
        // Validate properties
        for (Map.Entry<?, ?> entry : object.entrySet()) {
            String key = entry.getKey().toString();
            Object propValue = entry.getValue();
            
            // Check if key matches any property definition
            if (properties.containsKey(key)) {
                Schema propSchema = properties.get(key);
                ValidationResult propResult = propSchema.validate(propValue);
                if (!propResult.isValid()) {
                    for (ValidationError error : propResult.getErrors()) {
                        errors.add(new ValidationError(
                            "properties." + key + "." + error.getKeyword(),
                            error.getMessage(),
                            error.getPath()
                        ));
                    }
                }
            }
            // Check if key matches any pattern property
            else if (patternProperties != null) {
                for (Map.Entry<String, Schema> patternEntry : patternProperties.entrySet()) {
                    if (Pattern.matches(patternEntry.getKey(), key)) {
                        Schema patternSchema = patternEntry.getValue();
                        ValidationResult patternResult = patternSchema.validate(propValue);
                        if (!patternResult.isValid()) {
                            errors.addAll(patternResult.getErrors());
                        }
                        break;
                    }
                }
            }
            // Handle additionalProperties
            else if (additionalPropertiesBoolean != null && !additionalPropertiesBoolean) {
                errors.add(new ValidationError("additionalProperties",
                    String.format("property '%s' is not allowed", key)));
            }
            else if (additionalPropertiesSchema != null) {
                ValidationResult additionalResult = additionalPropertiesSchema.validate(propValue);
                if (!additionalResult.isValid()) {
                    for (ValidationError error : additionalResult.getErrors()) {
                        errors.add(new ValidationError(
                            "additionalProperties." + error.getKeyword(),
                            error.getMessage(),
                            error.getPath()
                        ));
                    }
                }
            }
        }
        
        // Run all additional validators
        for (Validator validator : validators) {
            ValidationResult result = validator.validate(value, schema);
            errors.addAll(result.getErrors());
        }
        
        return new ValidationResult(errors);
    }
    
    @Override
    public String getKeyword() {
        return "object";
    }
}
```

#### Critérios de Teste

##### Testes de Dependências

| Cenário | Schema | Dados | Resultado |
|---------|--------|-------|-----------|
| dependentRequired válido | {"dependentRequired": {"address": ["street", "city"]}} | {"address": {}, "street": "x", "city": "y"} | Válido |
| dependentRequired inválido | {"dependentRequired": {"address": ["street", "city"]}} | {"address": {}} | Erro: street e city requeridos |
| dependentSchemas válido | {"dependentSchemas": {"hasAddress": {"required": ["street"]}}} | {"hasAddress": true, "street": "x"} | Válido |
| dependentSchemas inválido | {"dependentSchemas": {"hasAddress": {"required": ["street"]}}} | {"hasAddress": true} | Erro: street requerido |

##### Testes de Propriedades

| Cenário | Schema | Resultado |
|---------|--------|-----------|
| minProperties válido | {"minProperties": 2} com {"a": 1, "b": 2} | Válido |
| minProperties inválido | {"minProperties": 3} com {"a": 1, "b": 2} | Erro |
| maxProperties válido | {"maxProperties": 2} com {"a": 1, "b": 2} | Válido |
| maxProperties inválido | {"maxProperties": 1} com {"a": 1, "b": 2} | Erro |

##### Testes de additionalProperties como Schema

| Cenário | Schema | Dados | Resultado |
|---------|--------|-------|-----------|
| additionalProperties schema válido | {"additionalProperties": {"type": "string"}} | {"unknown": "value"} | Válido |
| additionalProperties schema inválido | {"additionalProperties": {"type": "string"}} | {"unknown": 123} | Erro |

##### Casos de Borda

| Cenário | Comportamento Esperado |
|---------|----------------------|
| Objeto vazio {} com minProperties=0 | Válido |
| Objeto vazio {} com minProperties=1 | Inválido |
| Propriedades em excesso | Validar contra maxProperties |
| Propriedades com padrões múltiplos | Aplicar todos os patternProperties que correspondem |

#### Riscos e Mitigação

| Risco | Probabilidade | Impacto | Mitigação |
|-------|---------------|---------|-----------|
| Impacto na validação existente de additionalProperties | Média | Alto | Testes de regressão extensivos |
| Complexidade nas dependências circulares | Baixa | Médio | Limitar profundidade de validação |
| Performance com many properties | Média | Médio | Implementar lazy evaluation se necessário |

#### Procedimento de Rollback

1. Remover arquivos criados na pasta validation/object/
2. Reverter ObjectValidator.java para versão anterior
3. Verificar se todos os testes existentes passam
4. Documentar a regresão se algum teste falhar

---

## Prioridade Importante

### 3. Keyword const

#### Descrição

Implementar a keyword `const` do JSON Schema que permite validar que um valor é exatamente igual a um valor especificado. Diferente de `enum` que permite múltiplos valores, `const` aceita apenas um valor exato.

#### Funcionalidades Faltantes

| Keyword | Descrição | Referência Principal |
|---------|-----------|---------------------|
| **const** | Valida valor constante exato | [`ConstValidator.java`](https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/ConstValidator.java) |

#### Benefícios

- Permite validar valores exatos específicos
- Complementa enum para casos onde apenas um valor é permitido
- Suporte completo a Draft 2019-09/2020-12

#### Guia de Implementação

```java
package com.maiconjh.schemacr.validation;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.validation.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for the const JSON Schema keyword.
 * Validates that a value is exactly equal to a specified constant value.
 */
public class ConstValidator implements Validator {
    
    private final Object constValue;
    
    public ConstValidator(Object constValue) {
        this.constValue = constValue;
    }
    
    @Override
    public ValidationResult validate(Object value, Schema schema) {
        List<ValidationError> errors = new ArrayList<>();
        
        if (value == null && constValue == null) {
            return new ValidationResult(errors);
        }
        
        if (value == null || !deepEquals(value, constValue)) {
            errors.add(new ValidationError(
                "const",
                String.format("value must be equal to %s", constValue)
            ));
        }
        
        return new ValidationResult(errors);
    }
    
    private boolean deepEquals(Object a, Object b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        
        // Handle arrays
        if (a instanceof List && b instanceof List) {
            List<?> listA = (List<?>) a;
            List<?> listB = (List<?>) b;
            if (listA.size() != listB.size()) return false;
            for (int i = 0; i < listA.size(); i++) {
                if (!deepEquals(listA.get(i), listB.get(i))) return false;
            }
            return true;
        }
        
        // Handle objects
        if (a instanceof Map && b instanceof Map) {
            Map<?, ?> mapA = (Map<?, ?>) a;
            Map<?, ?> mapB = (Map<?, ?>) b;
            if (mapA.size() != mapB.size()) return false;
            for (Object key : mapA.keySet()) {
                if (!deepEquals(mapA.get(key), mapB.get(key))) return false;
            }
            return true;
        }
        
        return a.equals(b);
    }
    
    @Override
    public String getKeyword() {
        return "const";
    }
}
```

#### Critérios de Teste

| Cenário | Entrada | Resultado |
|---------|---------|-----------|
| const com valor simples | value=5, const=5 | Válido |
| const com valor diferente | value=5, const=10 | Inválido |
| const com objeto | value={"a":1}, const={"a":1} | Válido |
| const com array | value=[1,2], const=[1,2] | Válido |
| const com null | value=null, const=null | Válido |

---

### 4. Keyword readOnly/writeOnly (Metadata)

#### Descrição

Implementar as keywords de metadados `readOnly` e `writeOnly` do JSON Schema. Estas keywords não realizam validação ativa, mas são importantes para gerar erros apropriados em contextos de API.

#### Funcionalidades Faltantes

| Keyword | Descrição | Referência Principal |
|---------|-----------|---------------------|
| **readOnly** | Propriedade não deve ser enviada pelo cliente | [`ReadOnlyValidator.java`](https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/ReadOnlyValidator.java) |
| **writeOnly** | Propriedade não deve ser enviada pelo servidor | — |

#### Benefícios

- Suporte a validação de metadados de API
- Conformidade com JSON Schema
- Prevenção de envio de dados incorretos em operações de escrita/leitura

#### Guia de Implementação

```java
package com.maiconjh.schemacr.validation;

import com.maiconjh.schemacr.schemes.Schema;
import com.maiconjh.schemacr.validation.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for readOnly/writeOnly JSON Schema keywords.
 * These are metadata keywords that affect validation in specific contexts.
 */
public class MetadataValidator implements Validator {
    
    private final boolean readOnly;
    private final boolean writeOnly;
    private final boolean isInputValidation; // true for client->server, false for server->client
    
    public MetadataValidator(boolean readOnly, boolean writeOnly, boolean isInputValidation) {
        this.readOnly = readOnly;
        this.writeOnly = writeOnly;
        this.isInputValidation = isInputValidation;
    }
    
    @Override
    public ValidationResult validate(Object value, Schema schema) {
        List<ValidationError> errors = new ArrayList<>();
        
        // readOnly: property should not be sent by client (input validation)
        if (readOnly && isInputValidation && value != null) {
            errors.add(new ValidationError(
                "readOnly",
                "property is read-only and should not be sent by client"
            ));
        }
        
        // writeOnly: property should not be sent by server (response validation)
        if (writeOnly && !isInputValidation && value != null) {
            errors.add(new ValidationError(
                "writeOnly",
                "property is write-only and should not be sent to client"
            ));
        }
        
        return new ValidationResult(errors);
    }
    
    @Override
    public String getKeyword() {
        return readOnly ? "readOnly" : "writeOnly";
    }
}
```

#### Critérios de Teste

| Cenário | Keyword | Contexto | Resultado |
|---------|---------|----------|-----------|
| readOnly em input | readOnly | validação input | Erro |
| readOnly em output | readOnly | validação output | Válido |
| writeOnly em input | writeOnly | validação input | Válido |
| writeOnly em output | writeOnly | validação output | Erro |

---

### 5. Navegação SchemaRefResolver

#### Descrição

Expandir a funcionalidade `navigateTo()` no [`SchemaRefResolver.java`](src/main/java/com/maiconjh/schemacr/schemes/SchemaRefResolver.java:1) para suportar navegação completa em todas as keywords que podem conter `$ref`, incluindo `prefixItems`, `allOf` e `anyOf`.

#### Funcionalidades Faltantes

| Keyword | Descrição | Referência Principal |
|---------|-----------|---------------------|
| **prefixItems navigation** | Suporte a $ref dentro de prefixItems | [`RefValidator.java`](https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/RefValidator.java) |
| **allOf navigation** | Suporte a $ref dentro de allOf | [`JsonSchema.java`](https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/JsonSchema.java) |
| **anyOf navigation** | Suporte a $ref dentro de anyOf | [`SchemaLoaderTest.java`](https://raw.githubusercontent.com/everit-org/json-schema/master/core/src/test/java/org/everit/json/schema/loader/SchemaLoaderTest.java) |

#### Benefícios

- Resolução completa de $ref para schemas complexos
- Suporte a referências aninhadas em todas as estruturas
- Conformidade com especificações de referência do JSON Schema

#### Guia de Implementação

Atualizar o método `navigateTo()` no SchemaRefResolver.java:

```java
public Schema navigateTo(Map<String, Object> schema, String jsonPointer) {
    // Existing implementation
    
    // Add support for prefixItems
    Object prefixItems = schema.get("prefixItems");
    if (prefixItems instanceof List) {
        // Navigate through prefixItems array
        for (int i = 0; i < ((List<?>) prefixItems).size(); i++) {
            String prefixPath = "/prefixItems/" + i;
            if (jsonPointer.startsWith(prefixPath)) {
                Object item = ((List<?>) prefixItems).get(i);
                if (item instanceof Map) {
                    return navigateTo((Map<String, Object>) item, 
                        jsonPointer.substring(prefixPath.length()));
                }
            }
        }
    }
    
    // Add support for allOf
    Object allOf = schema.get("allOf");
    if (allOf instanceof List) {
        for (int i = 0; i < ((List<?>) allOf).size(); i++) {
            String allOfPath = "/allOf/" + i;
            if (jsonPointer.startsWith(allOfPath)) {
                Object item = ((List<?>) allOf).get(i);
                if (item instanceof Map) {
                    return navigateTo((Map<String, Object>) item, 
                        jsonPointer.substring(allOfPath.length()));
                }
            }
        }
    }
    
    // Add support for anyOf
    Object anyOf = schema.get("anyOf");
    if (anyOf instanceof List) {
        for (int i = 0; i < ((List<?>) anyOf).size(); i++) {
            String anyOfPath = "/anyOf/" + i;
            if (jsonPointer.startsWith(anyOfPath)) {
                Object item = ((List<?>) anyOf).get(i);
                if (item instanceof Map) {
                    return navigateTo((Map<String, Object>) item, 
                        jsonPointer.substring(anyOfPath.length()));
                }
            }
        }
    }
    
    return null;
}
```

#### Critérios de Teste

| Cenário | $ref指向 | Resultado |
|---------|---------|-----------|
| $ref pointing to prefixItems | #/prefixItems/0 | Schema resolvido corretamente |
| $ref pointing to allOf | #/definitions/SchemaName → allOf | Funciona em composições |
| $ref pointing to anyOf | #/definitions/SchemaName → anyOf | Funciona em composições |
| $ref nested | allOf[0].items.properties.data | Resolve corretamente |

---

## Formato Final: Guia de Implementação Prático

### Tabela Comparativa de Referências

| Funcionalidade | everit-org | networknt | Recomendado |
|----------------|------------|-----------|-------------|
| minItems | ArraySchema.java | MinItemsValidator.java | networknt (isolado) |
| maxItems | ArraySchema.java | MaxItemsValidator.java | networknt (isolado) |
| uniqueItems | ArraySchema.java | UniqueItemsValidator.java | networknt (isolado) |
| prefixItems | ArraySchema.java | PrefixItemsValidator.java | networknt |
| additionalItems | ArraySchema.java | — | everit (como referência) |
| minProperties | ObjectSchema.java | MinPropertiesValidator.java | networknt |
| maxProperties | ObjectSchema.java | MaxPropertiesValidator.java | networknt |
| dependentRequired | ObjectSchema.java | DependentRequiredValidator.java | networknt |
| dependentSchemas | ObjectSchema.java | DependentSchemasValidator.java | networknt |
| const | ConstSchema.java | ConstValidator.java | networknt |
| readOnly | — | ReadOnlyValidator.java | networknt |
| navigateTo | SchemaLoaderTest.java | RefValidator.java | networknt |

### Checklist de Implementação

#### Fase 1: Array Constraints (Semana 1-2)
- [ ] Criar pasta src/main/java/com/maiconjh/schemacr/validation/array/
- [ ] Implementar MinItemsValidator.java
- [ ] Implementar MaxItemsValidator.java
- [ ] Implementar UniqueItemsValidator.java
- [ ] Implementar PrefixItemsValidator.java
- [ ] Implementar AdditionalItemsValidator.java
- [ ] Atualizar ArrayValidator.java para usar novos validators
- [ ] Criar testes unitários para MinItemsValidator
- [ ] Criar testes unitários para MaxItemsValidator
- [ ] Criar testes unitários para UniqueItemsValidator
- [ ] Criar testes unitários para PrefixItemsValidator
- [ ] Criar testes unitários para AdditionalItemsValidator
- [ ] Executar testes de regressão

#### Fase 2: Object Constraints (Semana 3-4)
- [ ] Criar pasta src/main/java/com/maiconjh/schemacr/validation/object/
- [ ] Implementar MinPropertiesValidator.java
- [ ] Implementar MaxPropertiesValidator.java
- [ ] Implementar DependentRequiredValidator.java
- [ ] Implementar DependentSchemasValidator.java
- [ ] Atualizar ObjectValidator.java para usar novos validators
- [ ] Adicionar suporte a additionalProperties como schema
- [ ] Criar testes unitários para todos os validators
- [ ] Testar cenários de dependência
- [ ] Executar testes de regressão

#### Fase 3: Keywords Complementares (Semana 5)
- [ ] Implementar ConstValidator.java
- [ ] Integrar no ValidatorDispatcher
- [ ] Implementar MetadataValidator.java
- [ ] Criar testes para const
- [ ] Criar testes para readOnly/writeOnly

#### Fase 4: Navegação SchemaRefResolver (Semana 6)
- [ ] Atualizar navigateTo() em SchemaRefResolver.java
- [ ] Adicionar suporte a prefixItems
- [ ] Adicionar suporte a allOf
- [ ] Adicionar suporte a anyOf
- [ ] Criar testes de integração para $ref

### Métricas de Sucesso

| Métrica | Meta | Método de Medição |
|---------|------|-------------------|
| Cobertura de testes | >90% | Jacoco/Cobertura |
| Compatibilidade Draft 2019-09 | 100% | Testes de conformance |
| Performance (arrays grandes) | <100ms | Benchmark |
| Performance (objetos complexos) | <100ms | Benchmark |
| Bugs críticos abertos | 0 | Issue tracker |
| Pull requests mergeados | 12 | Git history |

### Procedimentos de Rollback

Para cada funcionalidade implementada, siga este procedimento:

1. **Identificar a versão anterior**: Verificar commits anteriores no git
2. **Criar branch de rollback**: `git checkout -b rollback-funcionalidade`
3. **Reverter alterações**: `git revert <commit>`
4. **Testar**: Executar suite de testes completa
5. **Se passar**: Fazer merge do branch de rollback
6. **Se falhar**: Analisar causa raiz, corrigir ou manter anterior

### Estimativa de Esforço

| Funcionalidade | Complexidade | Estimativa |
|----------------|--------------|------------|
| minItems | Baixa | 2 horas |
| maxItems | Baixa | 2 horas |
| uniqueItems | Média | 4 horas |
| prefixItems | Alta | 8 horas |
| additionalItems | Média | 4 horas |
| minProperties | Baixa | 2 horas |
| maxProperties | Baixa | 2 horas |
| dependentRequired | Média | 6 horas |
| dependentSchemas | Alta | 8 horas |
| additionalProperties schema | Média | 6 horas |
| const | Baixa | 3 horas |
| readOnly/writeOnly | Baixa | 3 horas |
| navigateTo improvements | Alta | 12 horas |
| **Total** | — | **~62 horas** |

---

## Histórico de Versões

| Versão | Data | Autor | Descrição |
|--------|------|-------|-----------|
| 1.0 | 2026-03-24 | Schema-Validator Team | Versão inicial do documento |

## Referências

- [JSON Schema Draft 2019-09](https://json-schema.org/draft/2019-09/json-schema-core.html)
- [JSON Schema Draft 2020-12](https://json-schema.org/draft/2020-12/json-schema-core.html)
- [everit-org/json-schema](https://github.com/everit-org/json-schema)
- [networknt/json-schema-validator](https://github.com/networknt/json-schema-validator)
- [reference-links-audit-v2.md](docs/explanation/reference-links-audit-v2.md)
- [code-audit-2026-03.md](docs/explanation/code-audit-2026-03.md)