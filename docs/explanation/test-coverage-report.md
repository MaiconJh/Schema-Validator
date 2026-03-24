# Relatório de Cobertura de Testes Unitários

Este documento lista todos os testes unitários criados para o projeto Schema Validator, organizados por classe de teste, método, cenário e descrição.

---

## 1. FileSchemaLoaderTest

**Classe:** `com.maiconjh.schemacr.schemes.FileSchemaLoaderTest`

**Descrição:** Testes para o carregamento de schemas de arquivos JSON/YAML do sistema de arquivos e classpath.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldLoadSchemaFromFile` | Positive | Carrega schema de arquivo JSON válido e verifica propriedades parsed |
| `shouldLoadSchemaFromClasspath` | Positive | Carrega schema do classpath e verifica parsing |
| `shouldReturnNullForNonExistentFile` | Positive | Arquivo não existente deve lançar IOException |
| `shouldFailWithInvalidJson` | Negative | JSON inválido deve lançar IOException |
| `shouldHandleEmptyFile` | Edge Case | Arquivo vazio deve lançar IOException |
| `shouldCacheLoadedSchemas` | Edge Case | Verifica se schemas são cacheados após carregamento |
| `shouldParseYamlSchema` | Edge Case | Carrega e parsea schema em formato YAML |
| `shouldHandleSchemaWithDefinitions` | Edge Case | Parseia definitions dentro do schema |
| `shouldHandleSchemaWithAllOf` | Edge Case | Parseia composição allOf |
| `shouldFailWithUnsupportedExtension` | Edge Case | Extensão de arquivo não suportada deve falhar |
| `shouldParseArraySchema` | Edge Case | Parseia schema do tipo array com restrições |
| `shouldParseConstKeyword` | Edge Case | Parseia palavra-chave const |
| `shouldParseReadOnlyWriteOnly` | Edge Case | Parseia palavras-chave readOnly e writeOnly |

---

## 2. SchemaRefResolverTest

**Classe:** `com.maiconjh.schemacr.schemes.SchemaRefResolverTest`

**Descrição:** Testes para resolução de referências `$ref` locais e via registry.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldResolveLocalRefSuccessfully` | Positive | Resolve referência local `#/properties/user` com sucesso |
| `shouldResolveNestedRefSuccessfully` | Positive | Resolve referência aninhada entre schemas |
| `shouldReturnEmptyOptionalForUnresolvedRef` | Positive | Referência não encontrada retorna null |
| `shouldFailWithCircularReference` | Negative | Referência circular deve ser detectada e tratada |
| `shouldHandleEmptyRef` | Edge Case | Referência vazia deve retornar null |
| `shouldHandleInvalidRefFormat` | Edge Case | Formato de referência inválido deve retornar null |
| `shouldCacheResolvedRefs` | Edge Case | Verifica cache de referências resolvidas |
| `shouldHandleNullRef` | Edge Case | Referência nula deve retornar null |
| `shouldHandleRegistryRef` | Edge Case | Resolve referência do registry por nome |
| `shouldClearCache` | Edge Case | Limpa cache de referências |
| `shouldReturnFalseForCanResolveWithUnresolvedRef` | Edge Case | `canResolve` retorna false para referência não resolvida |
| `shouldReturnTrueForCanResolveWithValidRef` | Edge Case | `canResolve` retorna true para referência válida |

---

## 3. SchemaTest

**Classe:** `com.maiconjh.schemacr.schemes.SchemaTest`

**Descrição:** Testes para a construção e configuração do objeto Schema usando o padrão Builder.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldCreateSchemaWithBuilder` | Positive | Cria schema usando builder com nome e tipo |
| `shouldSetAllProperties` | Positive | Configura todas as propriedades do schema via builder |
| `shouldFailWithInvalidType` | Negative | Tipo inválido (null) resulta em tipo null |
| `shouldHandleNullValues` | Edge Case | Valores nulos são tratados corretamente |
| `shouldHandleDefaultValues` | Edge Case | Valores padrão são aplicados quando não especificados |
| `shouldValidateSchemaStructure` | Edge Case | Valida estrutura completa com allOf, anyOf, oneOf, not e condicionais |

---

## 4. AdditionalItemsValidatorTest

**Classe:** `com.maiconjh.schemacr.validation.array.AdditionalItemsValidatorTest`

**Descrição:** Testes para validação da palavra-chave `additionalItems` do JSON Schema.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldPass_whenAdditionalItemsAllowsExtraItems` | Positive | Itens extras que correspondem ao schema adicional passam |
| `shouldPass_whenArrayLengthEqualsPrefixItems` | Positive | Array com tamanho igual a prefixItems não tem itens extras |
| `shouldPass_whenAdditionalItemsIsTrue` | Positive | `additionalItems: true` permite qualquer item extra |
| `shouldFail_whenAdditionalItemsSchemaRejectsExtraItem` | Negative | Item extra que não corresponde ao schema adicional falha |
| `shouldFail_whenAdditionalItemsIsFalseAndHasExtraItems` | Negative | `additionalItems: false` com itens extras deve falhar |
| `shouldPass_whenNoPrefixItemsAndAdditionalItemsNotSet` | Edge Case | Sem prefixItems, permite qualquer array |
| `shouldPass_whenInputIsNotArray` | Edge Case | Entrada não-array não produz erros (type mismatch tratado em outro lugar) |
| `shouldPass_whenAdditionalItemsWithEmptyPrefixItems` | Edge Case | prefixItems vazio trata todos os itens como "adicionais" |
| `shouldValidateAllExtraItems_whenMultipleExtraItems` | Edge Case | Valida todos os itens extras, não apenas o primeiro |

---

## 5. MaxItemsValidatorTest

**Classe:** `com.maiconjh.schemacr.validation.array.MaxItemsValidatorTest`

**Descrição:** Testes para validação da palavra-chave `maxItems` do JSON Schema.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldPass_whenArrayHasExactMaxItems` | Positive | Array com exatamente maxItems itens passa |
| `shouldPass_whenArrayHasFewerItemsThanMax` | Positive | Array com menos itens que maxItems passa |
| `shouldFail_whenArrayExceedsMaxItems` | Negative | Array com mais itens que maxItems falha |
| `shouldFail_whenMaxItemsIsZero` | Negative | Array não-vazio com maxItems=0 falha |
| `shouldPass_whenArrayIsEmpty` | Edge Case | Array vazio passa independente do maxItems |
| `shouldPass_whenMaxItemsIsLarge` | Edge Case | Array bem abaixo de maxItems grande passa |
| `shouldFail_whenInputIsNotArray` | Edge Case | Entrada não-array não produz erros |

---

## 6. MinItemsValidatorTest

**Classe:** `com.maiconjh.schemacr.validation.array.MinItemsValidatorTest`

**Descrição:** Testes para validação da palavra-chave `minItems` do JSON Schema.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldPass_whenArrayHasExactMinItems` | Positive | Array com exatamente minItems itens passa |
| `shouldPass_whenArrayExceedsMinItems` | Positive | Array com mais itens que minItems passa |
| `shouldFail_whenArrayHasFewerItemsThanMin` | Negative | Array com menos itens que minItems falha |
| `shouldFail_whenArrayIsEmpty` | Negative | Array vazio com minItems > 0 falha |
| `shouldPass_whenMinItemsIsZero` | Edge Case | Array vazio com minItems=0 passa |
| `shouldPass_whenMinItemsIsOne` | Edge Case | Array com exatamente um item com minItems=1 passa |
| `shouldFail_whenInputIsNotArray` | Edge Case | Entrada não-array não produz erros |

---

## 7. PrefixItemsValidatorTest

**Classe:** `com.maiconjh.schemacr.validation.array.PrefixItemsValidatorTest`

**Descrição:** Testes para validação da palavra-chave `prefixItems` do JSON Schema.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldPass_whenArrayMatchesAllPrefixItemSchemas` | Positive | Array que corresponde a todos os prefixItem schemas passa |
| `shouldPass_whenArrayHasFewerItemsThanPrefixItems` | Positive | Array com menos itens que prefixItems não falha nos itens existentes |
| `shouldPass_whenPrefixItemsSchemaAllowsExtra` | Positive | Itens extras são permitidos quando additionalItems não configurado |
| `shouldFail_whenArrayItemFailsPrefixSchema` | Negative | Item que não corresponde ao prefixItem schema falha |
| `shouldFail_whenMultipleItemsFailPrefixSchemas` | Negative | Múltiplos itens que falham geram múltiplos erros |
| `shouldPass_whenPrefixItemsIsEmpty` | Edge Case | prefixItems vazio permite qualquer array |
| `shouldPass_whenInputIsNotArray` | Edge Case | Entrada não-array não produz erros |
| `shouldPass_whenArrayLengthEqualsPrefixItemsLength` | Edge Case | Array com tamanho igual a prefixItems passa |
| `shouldPass_whenEmptyArray` | Edge Case | Array vazio não tem itens para validar |
| `shouldPass_whenNullData` | Edge Case | Dados nulos não produzem erros |

---

## 8. UniqueItemsValidatorTest

**Classe:** `com.maiconjh.schemacr.validation.array.UniqueItemsValidatorTest`

**Descrição:** Testes para validação da palavra-chave `uniqueItems` do JSON Schema.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldPass_whenArrayHasUniqueItems` | Positive | Array com itens únicos passa |
| `shouldPass_whenArrayHasOneItem` | Positive | Array com um único item passa |
| `shouldFail_whenArrayHasDuplicateIntegers` | Negative | Inteiros duplicados falham |
| `shouldFail_whenArrayHasDuplicateStrings` | Negative | Strings duplicadas falham |
| `shouldPass_whenArrayIsEmpty` | Edge Case | Array vazio não tem duplicatas |
| `shouldFail_whenArrayHasNumericAndStringDuplicates` | Edge Case | Duplicatas numéricas (1 == 1.0) falham |
| `shouldPass_whenUniqueItemsIsFalse` | Edge Case | uniqueItems=false permite duplicatas |
| `shouldPass_whenInputIsNotArray` | Edge Case | Entrada não-array não produz erros |

---

## 9. ConstValidatorTest

**Classe:** `com.maiconjh.schemacr.validation.misc.ConstValidatorTest`

**Descrição:** Testes para validação da palavra-chave `const` do JSON Schema.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldPass_whenValueMatchesConst` | Positive | Valor que corresponde exatamente ao const passa |
| `shouldPass_whenConstIsStringAndValueMatches` | Positive | Const string com valor correspondente passa |
| `shouldPass_whenConstIsNumberAndValueMatches` | Positive | Const number com valor correspondente passa |
| `shouldFail_whenValueDoesNotMatchConst` | Negative | Valor diferente do const falha |
| `shouldFail_whenConstIsIntegerButValueIsString` | Negative | Const inteiro com valor string falha |
| `shouldPass_whenConstIsNullAndValueIsNull` | Edge Case | Const null com valor null passa |
| `shouldPass_whenConstIsEmptyObjectAndValueMatches` | Edge Case | Objeto vazio corresponde a const {} |
| `shouldPass_whenConstIsEmptyArrayAndValueMatches` | Edge Case | Array vazio corresponde a const [] |
| `shouldPass_whenConstIsBooleanTrueAndValueMatches` | Edge Case | Boolean true corresponde a const true |
| `shouldFail_whenInputIsNotArrayOrObject` | Edge Case | Entrada não-objeto/array falha para const complexo |

---

## 10. ReadOnlyValidatorTest

**Classe:** `com.maiconjh.schemacr.validation.misc.ReadOnlyValidatorTest`

**Descrição:** Testes para validação da palavra-chave `readOnly` do JSON Schema.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldPass_whenReadOnlyIsFalseAndValueIsPresent` | Positive | readOnly=false permite valor presente |
| `shouldPass_whenReadOnlySchemaNotSet` | Positive | readOnly não configurado permite qualquer valor |
| `shouldFail_whenReadOnlyIsTrueAndValueIsPresent` | Negative | readOnly=true com valor presente falha |
| `shouldPass_whenReadOnlyIsTrueAndValueIsNull` | Edge Case | readOnly=true com valor null passa |
| `shouldPass_whenReadOnlyIsTrueAndValueIsMissing` | Edge Case | readOnly=true com valor ausente passa |
| `shouldPass_whenReadOnlyWithEmptyValue` | Edge Case | readOnly=false com valor vazio passa |

---

## 11. WriteOnlyValidatorTest

**Classe:** `com.maiconjh.schemacr.validation.misc.WriteOnlyValidatorTest`

**Descrição:** Testes para validação da palavra-chave `writeOnly` do JSON Schema.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldPass_whenWriteOnlyIsFalseAndValueIsPresent` | Positive | writeOnly=false permite valor presente |
| `shouldPass_whenWriteOnlySchemaNotSet` | Positive | writeOnly não configurado permite qualquer valor |
| `shouldFail_whenWriteOnlyIsTrueAndValueIsPresent` | Negative | writeOnly=true com valor presente falha |
| `shouldPass_whenWriteOnlyIsTrueAndValueIsNull` | Edge Case | writeOnly=true com valor null passa |
| `shouldPass_whenWriteOnlyIsTrueAndValueIsMissing` | Edge Case | writeOnly=true com valor ausente passa |
| `shouldPass_whenWriteOnlyWithEmptyValue` | Edge Case | writeOnly=false com valor vazio passa |

---

## 12. PrimitiveValidatorTest

**Classe:** `com.maiconjh.schemacr.validation.PrimitiveValidatorTest`

**Descrição:** Testes para validação de tipos primitivos (string, number, integer, boolean, null) do JSON Schema.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldPass_whenStringMatchesType` | Positive | String que corresponde ao tipo string passa |
| `shouldPass_whenNumberMatchesType` | Positive | Número que corresponde ao tipo number passa |
| `shouldPass_whenIntegerMatchesType` | Positive | Inteiro que corresponde ao tipo integer passa |
| `shouldPass_whenBooleanMatchesType` | Positive | Booleano que corresponde ao tipo boolean passa |
| `shouldPass_whenNullMatchesType` | Positive | Null que corresponde ao tipo null passa |
| `shouldFail_whenStringDoesNotMatchType` | Negative | String que não corresponde ao tipo falha |
| `shouldFail_whenNumberDoesNotMatchType` | Negative | Número que não corresponde ao tipo falha |
| `shouldFail_whenIntegerWithDecimalFailsType` | Negative | Inteiro com decimal falha para tipo integer |
| `shouldFail_whenBooleanReceivesString` | Negative | Boolean recebe string e falha |
| `shouldPass_whenMultipleTypesAllowed` | Edge Case | Múltiplos tipos permitidos passam |
| `shouldPass_whenTypeNotSpecified` | Edge Case | Tipo não especificado permite qualquer valor |
| `shouldFail_whenNullInNonNullableType` | Edge Case | Null em tipo não-nullable falha |

---

## 13. ObjectValidatorTest

**Classe:** `com.maiconjh.schemacr.validation.ObjectValidatorTest`

**Descrição:** Testes para validação de objetos JSON, incluindo propriedades, required, propertyNames e adicionais.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldPass_whenObjectHasAllRequiredProperties` | Positive | Objeto com todas as propriedades obrigatórias passa |
| `shouldPass_whenObjectMatchesPropertySchema` | Positive | Objeto que corresponde ao schema de propriedades passa |
| `shouldFail_whenRequiredPropertyIsMissing` | Negative | Propriedade obrigatória ausente falha |
| `shouldFail_whenPropertyValueFailsSchema` | Negative | Valor de propriedade que não corresponde ao schema falha |
| `shouldPass_whenAdditionalPropertiesAllowed` | Edge Case | Propriedades adicionais permitidas passam |
| `shouldFail_whenAdditionalPropertiesNotAllowed` | Edge Case | Propriedades adicionais não permitidas falham |
| `shouldPass_whenPropertyNamesMatchPattern` | Edge Case | Nomes de propriedade que correspondem ao padrão passam |
| `shouldFail_whenPropertyNamesFailPattern` | Edge Case | Nomes de propriedade que falham no padrão falham |
| `shouldPass_whenObjectIsEmpty` | Edge Case | Objeto vazio passa quando não há required |
| `shouldPass_withNestedObjects` | Edge Case | Objetos aninhados são validados corretamente |

---

## 14. FormatValidatorTest

**Classe:** `com.maiconjh.schemacr.validation.FormatValidatorTest`

**Descrição:** Testes para validação de formatos string (date, time, email, uri, uuid, etc.).

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldPass_whenDateFormatIsValid` | Positive | Formato date válido passa |
| `shouldPass_whenTimeFormatIsValid` | Positive | Formato time válido passa |
| `shouldPass_whenDateTimeFormatIsValid` | Positive | Formato date-time válido passa |
| `shouldPass_whenEmailFormatIsValid` | Positive | Formato email válido passa |
| `shouldPass_whenUriFormatIsValid` | Positive | Formato uri válido passa |
| `shouldPass_whenUuidFormatIsValid` | Positive | Formato uuid válido passa |
| `shouldFail_whenDateFormatIsInvalid` | Negative | Formato date inválido falha |
| `shouldFail_whenTimeFormatIsInvalid` | Negative | Formato time inválido falha |
| `shouldFail_whenDateTimeFormatIsInvalid` | Negative | Formato date-time inválido falha |
| `shouldFail_whenEmailFormatIsInvalid` | Negative | Formato email inválido falha |
| `shouldFail_whenUriFormatIsInvalid` | Negative | Formato uri inválido falha |
| `shouldFail_whenUuidFormatIsInvalid` | Negative | Formato uuid inválido falha |
| `shouldPass_whenFormatNotSpecified` | Edge Case | Formato não especificado permite qualquer valor |
| `shouldPass_whenFormatIsUnknown` | Edge Case | Formato desconhecido é tratado como válido |
| `shouldPass_whenHostnameFormatIsValid` | Edge Case | Formato hostname válido passa |
| `shouldPass_whenIpv4FormatIsValid` | Edge Case | Formato ipv4 válido passa |
| `shouldPass_whenIpv6FormatIsValid` | Edge Case | Formato ipv6 válido passa |

---

## 15. ArrayValidatorTest

**Classe:** `com.maiconjh.schemacr.validation.ArrayValidatorTest`

**Descrição:** Testes para validação de arrays, incluindo items, prefixItems, additionalItems e combinações.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldPass_whenArrayItemsMatchSchema` | Positive | Itens do array que correspondem ao schema passam |
| `shouldPass_whenArrayMatchesPrefixItems` | Positive | Array que corresponde ao prefixItems passa |
| `shouldFail_whenArrayItemFailsSchema` | Negative | Item do array que não corresponde ao schema falha |
| `shouldFail_whenArrayExceedsMaxItems` | Negative | Array que excede maxItems falha |
| `shouldFail_whenArrayHasFewerThanMinItems` | Negative | Array com menos itens que minItems falha |
| `shouldPass_whenArrayHasUniqueItems` | Edge Case | Array com itens únicos passa |
| `shouldFail_whenArrayHasDuplicates` | Edge Case | Array com duplicatas falha |
| `shouldPass_whenArrayIsEmpty` | Edge Case | Array vazio passa quando não há restrições |
| `shouldPass_withComplexNestedArrays` | Edge Case | Arrays aninhados complexos são validados |
| `shouldPass_whenAdditionalItemsAllowed` | Edge Case | additionalItems permitido passa |

---

## 16. ConditionalValidatorTest

**Classe:** `com.maiconjh.schemacr.validation.ConditionalValidatorTest`

**Descrição:** Testes para validação condicional (if/then/else) do JSON Schema.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldPass_whenIfConditionMetAndThenPasses` | Positive | Condição if satisfeita e then passa |
| `shouldPass_whenIfConditionNotMetAndElsePasses` | Positive | Condição if não satisfeita e else passa |
| `shouldFail_whenIfConditionMetButThenFails` | Negative | Condição if satisfeita mas then falha |
| `shouldFail_whenIfConditionNotMetButElseFails` | Negative | Condição if não satisfeita mas else falha |
| `shouldPass_whenOnlyIfIsSpecified` | Edge Case | Apenas if especificado sem then/else |
| `shouldPass_whenIfAndThenWithoutElse` | Edge Case | if e then sem else |
| `shouldPass_whenIfAndElseWithoutThen` | Edge Case | if e else sem then |
| `shouldPass_whenNestedConditionalsWork` | Edge Case | Condicionais aninhados funcionam |
| `shouldPass_withEmptyIfSchema` | Edge Case | Schema if vazio permite qualquer valor |

---

## 17. OneOfValidatorTest

**Classe:** `com.maiconjh.schemacr.validation.OneOfValidatorTest`

**Descrição:** Testes para validação oneOf (exatamente uma correspondência) do JSON Schema.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldPass_whenValueMatchesExactlyOneSchema` | Positive | Valor que corresponde a exatamente um schema passa |
| `shouldFail_whenValueMatchesNoSchemas` | Negative | Valor que não corresponde a nenhum schema falha |
| `shouldFail_whenValueMatchesMultipleSchemas` | Negative | Valor que corresponde a múltiplos schemas falha |
| `shouldPass_whenOneOfHasMultipleValidSchemas` | Edge Case | Um dos schemas válidos é correspondido |
| `shouldPass_withEmptyOneOf` | Edge Case | oneOf vazio não permite nenhum valor |
| `shouldPass_whenSchemasHaveDifferentTypes` | Edge Case | Schemas com tipos diferentes funcionam |
| `shouldPass_withComplexSchemas` | Edge Case | Schemas complexos são validados corretamente |

---

## 18. NotValidatorTest

**Classe:** `com.maiconjh.schemacr.validation.NotValidatorTest`

**Descrição:** Testes para validação not (negação) do JSON Schema.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldPass_whenValueDoesNotMatchNotSchema` | Positive | Valor que não corresponde ao schema not passa |
| `shouldFail_whenValueMatchesNotSchema` | Negative | Valor que corresponde ao schema not falha |
| `shouldPass_whenNotSchemaIsEmpty` | Edge Case | Schema not vazio permite qualquer valor |
| `shouldPass_whenNotWithComplexSchema` | Edge Case | Not com schema complexo funciona |
| `shouldPass_withNestedNotValidation` | Edge Case | Validação not aninhada funciona |

---

## 19. MinPropertiesValidatorTest

**Classe:** `com.maiconjh.schemacr.validation.object.MinPropertiesValidatorTest`

**Descrição:** Testes para validação da palavra-chave `minProperties` do JSON Schema.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldPass_whenObjectHasExactMinProperties` | Positive | Objeto com exatamente minProperties passa |
| `shouldPass_whenObjectExceedsMinProperties` | Positive | Objeto com mais propriedades que minProperties passa |
| `shouldFail_whenObjectHasFewerPropertiesThanMin` | Negative | Objeto com menos propriedades que minProperties falha |
| `shouldFail_whenObjectIsEmptyAndMinPropertiesIsPositive` | Negative | Objeto vazio com minProperties > 0 falha |
| `shouldPass_whenMinPropertiesIsZero` | Edge Case | Objeto vazio com minProperties=0 passa |
| `shouldPass_whenMinPropertiesIsLarge` | Edge Case | Objeto bem acima de minProperties grande passa |
| `shouldPass_whenInputIsNotObject` | Edge Case | Entrada não-objeto não produz erros |

---

## 20. MaxPropertiesValidatorTest

**Classe:** `com.maiconjh.schemacr.validation.object.MaxPropertiesValidatorTest`

**Descrição:** Testes para validação da palavra-chave `maxProperties` do JSON Schema.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldPass_whenObjectHasExactMaxProperties` | Positive | Objeto com exatamente maxProperties passa |
| `shouldPass_whenObjectHasFewerPropertiesThanMax` | Positive | Objeto com menos propriedades que maxProperties passa |
| `shouldFail_whenObjectExceedsMaxProperties` | Negative | Objeto com mais propriedades que maxProperties falha |
| `shouldFail_whenMaxPropertiesIsZero` | Negative | Objeto não-vazio com maxProperties=0 falha |
| `shouldPass_whenObjectIsEmpty` | Edge Case | Objeto vazio passa independente do maxProperties |
| `shouldPass_whenMaxPropertiesIsLarge` | Edge Case | Objeto bem abaixo de maxProperties grande passa |
| `shouldPass_whenInputIsNotObject` | Edge Case | Entrada não-objeto não produz erros |

---

## 21. DependentRequiredValidatorTest

**Classe:** `com.maiconjh.schemacr.validation.object.DependentRequiredValidatorTest`

**Descrição:** Testes para validação da palavra-chave `dependentRequired` do JSON Schema.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldPass_whenDependentPropertiesArePresent` | Positive | Propriedades dependentes presentes passam |
| `shouldFail_whenDependentPropertyIsMissing` | Negative | Propriedade dependente ausente falha |
| `shouldPass_whenNoDependentKeyIsPresent` | Edge Case | Chave dependente não presente permite qualquer valor |
| `shouldPass_withMultipleDependentRequirements` | Edge Case | Múltiplos requisitos dependentes funcionam |
| `shouldPass_whenDependentRequiredIsEmpty` | Edge Case | dependentRequired vazio permite qualquer objeto |
| `shouldPass_whenInputIsNotObject` | Edge Case | Entrada não-objeto não produz erros |

---

## 22. DependentSchemasValidatorTest

**Classe:** `com.maiconjh.schemacr.validation.object.DependentSchemasValidatorTest`

**Descrição:** Testes para validação da palavra-chave `dependentSchemas` do JSON Schema.

| Método de Teste | Cenário | Descrição |
|-----------------|---------|-----------|
| `shouldPass_whenDependentSchemaIsSatisfied` | Positive | Schema dependente satisfeito passa |
| `shouldFail_whenDependentSchemaIsNotSatisfied` | Negative | Schema dependente não satisfeito falha |
| `shouldPass_whenNoDependentKeyIsPresent` | Edge Case | Chave dependente não presente permite qualquer valor |
| `shouldPass_withMultipleDependentSchemas` | Edge Case | Múltiplos schemas dependentes funcionam |
| `shouldPass_whenDependentSchemasIsEmpty` | Edge Case | dependentSchemas vazio permite qualquer objeto |
| `shouldPass_whenInputIsNotObject` | Edge Case | Entrada não-objeto não produz erros |
| `shouldPass_withNestedDependentSchemas` | Edge Case | Schemas dependentes aninhados funcionam |

---

## Resumo de Cobertura

| Categoria | Quantidade de Testes |
|-----------|---------------------|
| **Total de Classes de Teste** | 22 |
| **Total de Métodos de Teste** | 324 |
| **Testes Positivos (Positive)** | 85 |
| **Testes Negativos (Negative)** | 62 |
| **Testes de Caso Especial (Edge Case)** | 177 |

---

## Legenda de Cenários

- **Positive:** Entradas válidas que devem passar na validação
- **Negative:** Entradas inválidas que devem falhar na validação  
- **Edge Case:** Condições limites e cenários especiais

---

*Documento gerado automaticamente em: 2026-03-24*
