Schema-Validator — Links de Referência por Feature Não Implementada (v3)
Gerado em: 2026-03-24
Projeto: MaiconJh/Schema-Validator
Baseado em: Auditoria técnica code-audit-2026-03.md
Versão: 3 — links convertidos para raw e corrigidos


PRIORIDADE ALTA — Array Constraints (ArrayValidator.java)

Atualmente o projeto valida apenas items. Os arquivos abaixo cobrem minItems, maxItems, uniqueItems, prefixItems e additionalItems.

everit-org/json-schema
- ArraySchema.java (minItems, maxItems, uniqueItems, additionalItems):
  https://raw.githubusercontent.com/everit-org/json-schema/master/core/src/main/java/org/everit/json/schema/ArraySchema.java
- ArraySchemaTest.java (testes completos de array):
  https://raw.githubusercontent.com/everit-org/json-schema/master/core/src/test/java/org/everit/json/schema/ArraySchemaTest.java

networknt/json-schema-validator
- MinItemsValidator.java:
  https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/MinItemsValidator.java
- MaxItemsValidator.java:
  https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/MaxItemsValidator.java
- UniqueItemsValidator.java:
  https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/UniqueItemsValidator.java
- PrefixItemsValidator.java (Draft 2020-12):
  https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/PrefixItemsValidator.java


PRIORIDADE ALTA — Object Constraints (ObjectValidator.java)

minProperties, maxProperties, dependentRequired, dependentSchemas e additionalProperties como schema estão ausentes.

everit-org/json-schema
- ObjectSchema.java (minProperties, maxProperties, additionalProperties como schema, dependencies):
  https://raw.githubusercontent.com/everit-org/json-schema/master/core/src/main/java/org/everit/json/schema/ObjectSchema.java
- Visitor.java (como o visitor percorre minProperties / maxProperties):
  https://raw.githubusercontent.com/everit-org/json-schema/master/core/src/main/java/org/everit/json/schema/Visitor.java

networknt/json-schema-validator
- MinPropertiesValidator.java:
  https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/MinPropertiesValidator.java
- MaxPropertiesValidator.java:
  https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/MaxPropertiesValidator.java
- DependentRequiredValidator.java:
  https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/DependentRequiredValidator.java
- DependentSchemasValidator.java:
  https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/DependentSchemasValidator.java


PRIORIDADE MÉDIA — Keyword const e Metadata

everit-org/json-schema
- ConstSchema.java (const modelo):
  https://raw.githubusercontent.com/everit-org/json-schema/master/core/src/main/java/org/everit/json/schema/ConstSchema.java
- ValidatingVisitor.java (const validação via visitor):
  https://raw.githubusercontent.com/everit-org/json-schema/master/core/src/main/java/org/everit/json/schema/ValidatingVisitor.java

networknt/json-schema-validator
- ConstValidator.java:
  https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/ConstValidator.java
- ReadOnlyValidator.java (readOnly / writeOnly):
  https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/ReadOnlyValidator.java


PRIORIDADE MÉDIA — Navegação no SchemaRefResolver.java

O navigateTo() atual não suporta prefixItems, allOf, anyOf. Os arquivos abaixo mostram como resolver isso.

networknt/json-schema-validator
- RefValidator.java (resolução de $ref com navegação completa):
  https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/RefValidator.java
- JsonSchema.java (core do schema com resolução de referências):
  https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/JsonSchema.java

everit-org/json-schema
- SchemaLoaderTest.java (testes cobrem todos os casos de $ref):
  https://raw.githubusercontent.com/everit-org/json-schema/master/core/src/test/java/org/everit/json/schema/loader/SchemaLoaderTest.java


NOTAS
- Todos os links apontam diretamente para o conteúdo raw dos repositórios oficiais.
- Os arquivos da networknt são os mais diretos para implementação: cada keyword tem seu próprio validator isolado, fácil de adaptar ao ArrayValidator.java e ObjectValidator.java do seu projeto.
- Os arquivos da everit-org são úteis para entender a estrutura completa do modelo (como os campos minItems, maxItems ficam no ArraySchema.java junto com a lógica de validação).
- Em caso de erro 404, verifique se o branch continua sendo master; caso contrário, substitua por main na URL.

Gerado com base na auditoria code-audit-2026-03.md — Schema-Validator v0.5.0
Links convertidos para raw em: 2026-03-24