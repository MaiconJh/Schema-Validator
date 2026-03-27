# Prompt de Plano de Implementação + Guia de Orientação (Prioridades)

> Base obrigatória: `docs/explanation/draft-2020-12-feature-gap-audit.md`.
> 
> Este documento transforma a auditoria em um **plano executável**, com **mapa de decisões**, **ordem de prioridade** e **critérios de aceite**, preservando todos os pontos levantados no relatório original.

---

## 1) Prompt mestre (copiar e usar com o agente de implementação)

```md
Você é responsável por implementar as correções do Schema-Validator v1.0.0 com base no arquivo:
- docs/explanation/draft-2020-12-feature-gap-audit.md

Objetivo:
- Executar o roadmap completo mantendo tudo o que foi auditado, sem perder escopo funcional.
- Implementar em fases (P0 -> P1 -> P2), com testes por keyword e validação de compatibilidade.

Regras obrigatórias:
1. Tratar como fonte de verdade o conteúdo integral do audit (matriz de keywords, comportamentos atuais, roadmap e sugestões).
2. Não remover funcionalidades já existentes; corrigir drift entre registry/parser/validator.
3. Cada PR deve conter:
   - escopo pequeno e coeso;
   - testes automatizados cobrindo sucesso/falha;
   - atualização de documentação de suporte de keywords.
4. Sempre que um keyword for implementado:
   - atualizar Schema model;
   - atualizar FileSchemaLoader parser;
   - atualizar validator correspondente;
   - atualizar SupportedKeywordsRegistry;
   - adicionar/ajustar testes unitários e de integração.
5. Manter compatibilidade progressiva:
   - aliases legados (`definitions`, `dependencies`, `comment`) devem ser tratados explicitamente com estratégia de migração/depreciação.
6. Para keywords começando com `$`, revisar lógica atual de detecção que ignora `$...` e alinhar com política definida de warning/fail-fast.

Entregáveis por fase:
- Lista de mudanças em código.
- Lista de testes novos e resultados.
- Tabela de status da fase (keywords concluídos, parciais, pendentes).
- Riscos e decisões tomadas.

Comece pela Fase P0 (propertyNames, contains/minContains/maxContains e sincronização do registry).
```

---

## 2) Mapa de decisões (Decision Map)

Use este fluxo para qualquer keyword do audit:

1. **Keyword está na matriz do Draft 2020-12?**
   - Não -> tratar como extensão/compatibilidade, fora do core do draft.
   - Sim -> continuar.

2. **Está no registry?**
   - Não -> decidir entre:
     - (a) implementar e adicionar ao registry, ou
     - (b) manter não suportado com warning/fail-fast explícito.

3. **Está no parser (`FileSchemaLoader`) ?**
   - Não -> adicionar parsing + testes de carregamento.

4. **Está no model (`Schema`) ?**
   - Não -> adicionar campo/getter/builder + testes de model.

5. **Está no validator?**
   - Não -> implementar semântica + testes de validação.
   - Parcial -> completar semântica de acordo com Draft 2020-12.

6. **Comportamento de keyword não suportado está coerente?**
   - Validar warning/fail-fast.
   - Revisar exceção atual para `$...` (hoje ignorados pela detecção).

7. **Compatibilidade legada necessária?**
   - Sim -> suportar alias com flag/documentação e plano de depreciação.

---

## 3) Plano por prioridade (preservando o audit)

## P0 — Alto impacto

### P0.1 `propertyNames`
- **Implementar** no model, parser e `ObjectValidator`.
- **Teste mínimo**:
  - chave válida passa;
  - chave inválida falha com caminho claro.
- **Critério de aceite**:
  - validação de nomes de propriedade funcional e coberta por testes.

### P0.2 `contains`, `minContains`, `maxContains`
- **Implementar** no model, parser e `ArrayValidator`.
- **Semântica obrigatória**:
  - `contains` conta matches;
  - `minContains`/`maxContains` restringem contagem;
  - default de `minContains = 1` quando `contains` existir sem `minContains`.
- **Critério de aceite**:
  - arrays validam cardinalidade corretamente com casos positivos/negativos.

### P0.3 Sincronização de registry (drift)
- **Adicionar**: `prefixItems`, `dependentRequired`, `dependentSchemas`, `$defs`, `$comment`.
- **Tratar legado explicitamente**: `definitions`, `dependencies`, `comment`, `additionalItems`.
- **Critério de aceite**:
  - nenhum warning falso para keyword implementado;
  - warnings corretos para não implementados.

---

## P1 — Médio impacto

### P1.1 `unevaluatedProperties` e `unevaluatedItems`
- Implementar rastreamento de avaliação para applicators.
- Critério: elementos/propriedades não avaliados respeitam constraints finais.

### P1.2 `$dynamicRef` / `$dynamicAnchor`
- Implementar resolução dinâmica por escopo.
- Critério: referências dinâmicas funcionam em schemas aninhados.

### P1.3 Vocabulário de conteúdo
- `contentEncoding`, `contentMediaType`, `contentSchema`.
- Critério: modo anotação/assertivo definido e coberto por testes.

---

## P2 — Baixo impacto

### P2.1 Metadados de anotação completos
- `default`, `examples`, `deprecated`.
- Critério: parser/model armazenam corretamente, APIs expõem.

### P2.2 Evoluções adicionais de vocabulário/output
- Expandir conforme necessidade de produto.

---

## 4) Backlog de issues sugerido (pronto para abrir no repositório)

1. Implement `propertyNames` end-to-end (model/parser/validator/tests)
2. Implement `contains` + `minContains` + `maxContains`
3. Align `SupportedKeywordsRegistry` with Draft 2020-12 canonical keywords
4. Implement `unevaluatedProperties` / `unevaluatedItems`
5. Add dynamic reference support (`$dynamicRef`, `$dynamicAnchor`)
6. Implement content vocabulary keywords (`contentEncoding`, `contentMediaType`, `contentSchema`)
7. Add metadata parity for `default`, `examples`, `deprecated`
8. Define and enforce unsupported-keyword policy for `$...` keywords
9. Introduce compatibility strategy for legacy aliases (`definitions`, `dependencies`, `comment`)

---

## 5) Estratégia de execução em PRs pequenos

- **PR-1 (P0.3 parcial):** corrigir registry + testes de warnings/fail-fast.
- **PR-2 (P0.1):** `propertyNames` completo.
- **PR-3 (P0.2):** `contains/minContains/maxContains` completo.
- **PR-4 (P1.1):** unevaluated keywords.
- **PR-5 (P1.2):** dynamic refs.
- **PR-6 (P1.3):** content vocabulary.
- **PR-7 (P2.1):** annotations metadata.

Cada PR deve atualizar a tabela de status no audit original para manter rastreabilidade.

---

## 6) Checklist de qualidade (Definition of Done)

- [ ] Keyword implementado no model, parser, validator e registry (quando aplicável).
- [ ] Testes unitários de sucesso e falha adicionados.
- [ ] Testes de regressão para warning/fail-fast atualizados.
- [ ] Documentação de keywords suportados atualizada.
- [ ] Comportamento para aliases legados documentado.
- [ ] Mudança validada contra os casos de exemplo do audit.

---

## 7) Riscos e mitigação

- **Risco:** quebrar compatibilidade com schemas legados.
  - **Mitigação:** camada de aliases + aviso de depreciação.

- **Risco:** implementação incompleta entre parser/validator.
  - **Mitigação:** checklist obrigatório por keyword e testes end-to-end.

- **Risco:** comportamento inconsistente de keywords `$...` não suportados.
  - **Mitigação:** política única (warn/fail-fast) e cobertura de testes específica.

---

## 8) Resultado esperado

Ao final da execução deste guia, o projeto terá:
- roadmap implementado por prioridade;
- suporte significativamente mais próximo do Draft 2020-12;
- menor drift entre registry e comportamento real;
- base de testes e documentação pronta para evolução contínua.
