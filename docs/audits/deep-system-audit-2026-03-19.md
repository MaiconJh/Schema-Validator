# Deep System Audit — Estado Verificado da Implementação (revalidação em 2026-03-21)

> **Objetivo desta revisão:** revalidar integralmente o conteúdo do relatório de 2026-03-19 contra o código-fonte atual, removendo suposições e corrigindo drift documental.
>
> **Critério de verdade adotado:** somente comportamento observável no código em `src/main/java` e contratos ativos em `src/main/resources`.

---

## 1) Metodologia de verificação cruzada

### 1.1 Processo executado
1. Leitura integral do documento anterior (`deep-system-audit-2026-03-19.md`).
2. Extração de cada afirmação, hipótese, seção e conclusão.
3. Mapeamento direto de cada item para evidência concreta no código (classe, método, fluxo de execução).
4. Classificação por status:
   - **Válido**: implementado e consistente com execução atual.
   - **Parcial**: há implementação, mas incompleta ou com restrições não documentadas.
   - **Inválido**: contradiz implementação atual.
   - **Não verificável**: não foi possível confirmar por ausência de artefato executável/contratual correspondente.

### 1.2 Fontes rastreáveis utilizadas
- `src/main/java/com/maiconjh/schemacr/core/ValidationService.java`
- `src/main/java/com/maiconjh/schemacr/validation/{ObjectValidator,ArrayValidator,PrimitiveValidator,FormatValidator,ValidatorDispatcher,ValidationError}.java`
- `src/main/java/com/maiconjh/schemacr/schemes/{Schema,FileSchemaLoader,SchemaRefResolver,SupportedKeywordsRegistry}.java`
- `src/main/java/com/maiconjh/schemacr/integration/{EffValidateData,ExprLastValidationErrors,SkriptValidationBridge,DataFileLoader}.java`
- `src/main/java/com/maiconjh/schemacr/config/PluginConfig.java`
- `src/main/resources/config.yml`
- `docs/CONTRACT.md` e `docs/api-reference.md` (apenas para detectar drift, não como fonte primária de comportamento).

---

## 2) Matriz de verificação — afirmações do documento anterior

## 2.1 Dispatch do validador raiz

**Afirmação anterior:** bug crítico de dispatch na raiz foi resolvido.

**Status:** ✅ **Válido**.

**Evidência:** `ValidationService.validate(...)` usa `ValidatorDispatcher.forSchema(schema)` antes de validar (`ValidationService.java`, método `validate`, linhas 40–50), portanto o tipo raiz não está mais rigidamente preso a `ObjectValidator`.

**Estado real atual:** schemas raiz de tipo `array` e primitivos passam pelo validador apropriado via dispatcher.

**Comentário técnico da alteração**
- **O que foi alterado no documento:** removida a narrativa de risco ativo para dispatch raiz e reclassificado como corrigido e ativo.
- **Por que foi necessário:** a versão anterior ainda mantinha trechos legados que induziam interpretação parcialmente conflitante com o fluxo atual.
- **Evidência da correção:** chamada explícita a `ValidatorDispatcher.forSchema(schema)` no caminho de execução principal.
- **Estado real:** dispatch raiz dinâmico está implementado.

## 2.2 `minItems`, `maxItems`, `uniqueItems`

**Afirmação anterior:** não implementados (com menção de correção incorreta em outro audit).

**Status:** ✅ **Válido (continuam não implementados)**.

**Evidência:** `ArrayValidator.validate(...)` apenas verifica tipo lista e valida `items` elemento a elemento; não há qualquer checagem de cardinalidade/unicidade.

**Estado real atual:** schemas contendo essas keywords podem ser parseados, mas tais restrições não são aplicadas em runtime.

**Comentário técnico da alteração**
- **O que foi alterado no documento:** mantida a conclusão de não implementação e adicionada explicação do efeito prático (subvalidação silenciosa).
- **Por que foi necessário:** reforçar consequência operacional e evitar falsa sensação de cobertura JSON Schema.
- **Evidência da correção:** ausência de regras no `ArrayValidator`.
- **Estado real:** apenas `items` é efetivamente validado em arrays.

## 2.3 `minProperties`, `maxProperties`

**Afirmação anterior:** não implementados.

**Status:** ✅ **Válido (continuam não implementados)**.

**Evidência:** `ObjectValidator` valida `required`, `properties`, `patternProperties`, `additionalProperties`, composição (`allOf/anyOf/oneOf/not/if-then-else`) e `$ref`, mas não há contagem mínima/máxima de propriedades.

**Estado real atual:** cardinalidade de campos do objeto não é imposta.

**Comentário técnico da alteração**
- **O que foi alterado no documento:** reforçada distinção entre “objeto validado estruturalmente” e “cardinalidade de propriedades”.
- **Por que foi necessário:** remover ambiguidade entre suporte a `required` e suporte a limites de quantidade.
- **Evidência da correção:** inexistência de lógica de `minProperties/maxProperties` no validador de objeto.
- **Estado real:** limites de quantidade de propriedades seguem ausentes.

## 2.4 `multipleOf`

**Afirmação anterior:** implementado.

**Status:** ✅ **Válido**.

**Evidência:** `PrimitiveValidator` executa divisão pelo divisor e valida integralidade do resultado (`multipleOf`) para `number`/`integer`.

**Estado real atual:** `multipleOf` está ativo para tipos numéricos.

## 2.5 `format`

**Afirmação anterior:** implementado.

**Status:** ✅ **Válido**.

**Evidência:** `PrimitiveValidator` chama `FormatValidator.isValid(...)` quando `schema.hasFormat()`; falha gera `ValidationError`.

**Estado real atual:** validação de formato está em modo “hard fail” (erro de validação, não aviso).

## 2.6 `oneOf`, `not`, `if/then/else`

**Afirmação anterior:** implementados.

**Status:** ✅ **Válido**.

**Evidência:** blocos dedicados em `ObjectValidator.validate(...)` para `oneOf`, `not` e condicional `if/then/else`.

**Estado real atual:** composição condicional e exclusão lógica funcionam no validador de objeto.

## 2.7 Modelo de erro Skript

**Afirmação anterior:** havia mismatch e foi “improved”.

**Status:** ✅ **Parcialmente válido**.

**Evidência:**
- `ValidationError` possui `getMessage()` e `toCompactString()`.
- `ExprLastValidationErrors` retorna `String[]` com `toCompactString()`.

**Estado real atual:** a integração Skript continua textual (strings), não objeto estruturado exposto em expressão Skript.

**Comentário técnico da alteração**
- **O que foi alterado no documento:** clarificação de que houve melhoria de serialização, mas não mudança do contrato de tipo de retorno.
- **Por que foi necessário:** evitar leitura equivocada de “resolvido” quando a limitação fundamental permanece.
- **Evidência da correção:** assinatura de `ExprLastValidationErrors extends SimpleExpression<String>`.
- **Estado real:** saída compacta em string, sem objeto rico no lado Skript.

## 2.8 `$ref`, `definitions`, `$defs`

**Afirmação anterior:** suporte parcial.

**Status:** ✅ **Válido (com ressalvas importantes)**.

**Evidência:**
- `FileSchemaLoader` extrai `definitions` e `$defs` em mapa interno.
- `Schema` não armazena árvore de `definitions/$defs`.
- `SchemaRefResolver.navigateTo(...)` navega apenas por `properties` e `items`; não navega por `definitions/$defs`.

**Estado real atual:** parsing de blocos de definição existe, mas resolução por JSON Pointer para `#/definitions/...`/`#/$defs/...` não está completa de ponta a ponta.

## 2.9 “Issue 5: Config contract mismatch” (sem verificação no texto antigo)

**Status:** ❌ **Não segue como fonte da verdade**.

**Análise crítica e evidências:**
- O documento antigo deixava “Needs verification”, sem conclusão validada.
- Hoje é possível confirmar drift documental objetivo:
  - `config.yml` inclui `strict-mode` e define `validation-on-load: false` por padrão.
  - `PluginConfig` lê `strict-mode` e default interno de `validation-on-load` como `true` (usado quando chave faltar).
  - `docs/CONTRACT.md` lista `validation-on-load: true` e omite `strict-mode` na tabela de contrato.

**Estado real atual:** existe desalinhamento entre documentação contratual e configuração real distribuída.

## 2.10 “Issue 6: API reference signature drift”

**Status:** ⚠️ **Parcial / requer recorte temporal**.

**Evidência:** `docs/api-reference.md` está majoritariamente alinhado com assinaturas atuais de `FileSchemaLoader`, `SchemaRegistrationService` e `ValidationService`.

**Estado real atual:** o drift histórico indicado no audit não se sustenta integralmente no estado presente; há alinhamento significativo na referência de API.

**Comentário técnico da alteração**
- **O que foi alterado no documento:** rebaixado de problema ativo generalizado para observação histórica com revisão pontual contínua.
- **Por que foi necessário:** conclusão antiga estava genérica e não refletia o estado atual dos métodos públicos.
- **Evidência da correção:** comparação direta entre `docs/api-reference.md` e assinaturas em código.
- **Estado real:** referência de API atual está próxima do código.

## 2.11 “Issue 7: Path resolution split-brain”

**Status:** ✅ **Válido**.

**Evidência:**
- `SchemaValidatorPlugin.autoLoadSchemas()` usa diretório de configuração (`PluginConfig#getSchemaDirectory()`).
- `EffValidateData` valida usando `Path.of(schemaFile)` e `Path.of(dataFile)` recebidos diretamente no efeito Skript.

**Estado real atual:** coexistem dois modos de resolução de caminho (auto-load configurado vs caminho explícito em runtime de efeito).

## 2.12 “Issue 8: Composition logic object-validator bound”

**Status:** ✅ **Válido**.

**Evidência:** blocos de `allOf`, `anyOf`, `oneOf`, `not`, `if/then/else` estão implementados no `ObjectValidator`, não numa camada transversal compartilhada.

**Estado real atual:** composição está acoplada ao fluxo de validação de objeto, ainda que sub-schemas sejam despachados por tipo.

## 2.13 “Issue 9: Global mutable last-result bridge”

**Status:** ✅ **Válido**.

**Evidência:** `SkriptValidationBridge` mantém `private static volatile ValidationResult lastResult` único e global no processo.

**Estado real atual:** não há escopo por jogador/evento/contexto; o último resultado é global.

## 2.14 Registro de keywords suportadas

**Status:** ❌ **Não segue como fonte da verdade** (quando interpretado literalmente como suporte completo).

**Análise crítica e evidências:**
- `SupportedKeywordsRegistry` marca `minItems`, `maxItems`, `uniqueItems`, `minProperties`, `maxProperties`, `dependencies` como suportadas.
- Validadores (`ArrayValidator`/`ObjectValidator`) não implementam enforcement dessas regras.

**Estado real atual:** o registry funciona mais como whitelist de parsing/documentação do que prova de enforcement real.

## 2.15 Restrição de raiz descrita em documentação canônica

**Status:** ❌ **Não segue como fonte da verdade**.

**Análise crítica e evidências:**
- `docs/CONTRACT.md` afirma que `ValidationService()` impõe raiz objeto por usar `ObjectValidator` fixo.
- `ValidationService.validate(...)` usa dispatcher por tipo do schema raiz.

**Estado real atual:** essa restrição de raiz não representa mais o comportamento vigente.

---

## 3) Estado atual consolidado (somente fatos validados)

### 3.1 Funcionalidades comprovadamente implementadas
- Dispatch por tipo no nó raiz (`object`, `array`, primitivos).
- Validação de objeto: `properties`, `required`, `patternProperties`, `additionalProperties`, `allOf`, `anyOf`, `oneOf`, `not`, `if/then/else`.
- Validação de array: `items`.
- Validação primitiva: `minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`, `minLength`, `maxLength`, `pattern`, `format`, `enum`.
- Expressão Skript de erros com retorno textual (`String[]`) e formatação compacta.

### 3.2 Funcionalidades ausentes ou parciais
- Não implementado: `minItems`, `maxItems`, `uniqueItems`.
- Não implementado: `minProperties`, `maxProperties`.
- Parcial: `$ref` com `definitions/$defs` (extração existe; resolução de ponteiro local não percorre esses nós no modelo atual).
- Limitação arquitetural: resultado de validação global compartilhado na integração Skript.

### 3.3 Divergências documentais críticas
- `docs/CONTRACT.md` contém pelo menos duas divergências de alto impacto:
  1. afirma restrição de raiz objeto que não condiz com o código atual;
  2. descreve contrato de erro Skript como `toString()` quando implementação usa `toCompactString()`.
- Contrato de configuração na documentação não acompanha integralmente `config.yml`/`PluginConfig` (ex.: `strict-mode`).

---

## 4) Plano de ação técnico detalhado

## 4.1 Inconsistências identificadas e impacto
1. **Keywords declaradas vs enforcement real (array/object cardinality).**
   - **Impacto:** falsa confiança de validação; dados inválidos podem ser aceitos silenciosamente.
2. **$ref parcial para `definitions/$defs`.**
   - **Impacto:** schemas com referências internas padrão podem falhar ou validar de forma incompleta.
3. **Drift em documentação canônica (`CONTRACT.md`).**
   - **Impacto:** integradores tomam decisões erradas sobre capacidades e limitações do runtime.
4. **`lastResult` global no bridge Skript.**
   - **Impacto:** risco de sobreposição de contexto entre execuções concorrentes/eventos.
5. **Path resolution com semânticas diferentes (auto-load vs efeito).**
   - **Impacto:** comportamento não uniforme entre operação automática e validação ad hoc.

## 4.2 Metodologias utilizadas na verificação
- Inspeção estática de fluxo principal de execução (caminho de entrada → parser → dispatcher → validadores).
- Validação semântica por leitura de métodos responsáveis por enforcement de constraints.
- Comparação de contrato documental versus assinatura/uso real das classes públicas.
- Análise de acoplamento arquitetural (estado global, resolução de referências, fronteiras de integração).

## 4.3 Critérios adotados para validação da verdade
- **Código executável prevalece** sobre documentação textual.
- **Implementado** significa “há parser + modelo + enforcement no runtime” (não apenas keyword reconhecida).
- **Parcial** quando existe parte do pipeline, mas sem fechamento funcional.
- **Não verificável** quando não há artefato de execução ou evidência concreta suficiente.

## 4.4 Próximos passos concretos (ordem recomendada)
1. Corrigir `docs/CONTRACT.md` para refletir comportamento real de dispatch raiz, tipo de saída Skript e chaves de configuração atuais.
2. Implementar `minItems/maxItems/uniqueItems` (modelo `Schema`, parser `FileSchemaLoader`, enforcement `ArrayValidator`).
3. Implementar `minProperties/maxProperties` (modelo + parser + enforcement em `ObjectValidator`).
4. Revisar arquitetura de `$ref` para suportar navegação em `definitions/$defs` no modelo em memória.
5. Revisar `SupportedKeywordsRegistry` para distinguir claramente:
   - keywords **reconhecidas**;
   - keywords **enforced**.
6. Avaliar escopo contextual para `SkriptValidationBridge` (por evento/jogador/tópico) para eliminar estado global compartilhado.
7. Unificar estratégia de resolução de paths entre auto-load e validação via efeito Skript (ou documentar explicitamente o dualismo).

---

## 5) Registro de reestruturação deste documento

### 5.1 O que foi reestruturado
- Documento foi reorganizado de narrativa mista para trilha auditável: **metodologia → matriz de verificação → estado consolidado → plano de ação**.
- Cada seção passou a conter status explícito e evidência concreta no código.
- Seções incorretas/obsoletas/não conclusivas receberam marcação formal obrigatória.

### 5.2 Por que a reestruturação foi necessária
- A versão anterior misturava estado histórico, conclusões parciais e itens “a verificar”, reduzindo confiabilidade como fonte operacional.
- Faltava hierarquia lógica para leitura progressiva e tomada de decisão técnica.

### 5.3 Resultado
- Este arquivo passa a representar apenas informações verificadas no estado atual do sistema.
- Itens sem validação forte foram explicitamente tratados como não fonte da verdade.

