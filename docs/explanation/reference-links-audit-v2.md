Schema-Validator — Reference Links by Not Implemented Feature (v3)
Generated: 2026-03-24
Project: MaiconJh/Schema-Validator
Based on: Technical audit code-audit-2026-03.md
Version: 3 — links converted to raw and corrected


HIGH PRIORITY — Array Constraints (ArrayValidator.java)

Currently the project only validates items. The files below cover minItems, maxItems, uniqueItems, prefixItems, and additionalItems.

everit-org/json-schema
- ArraySchema.java (minItems, maxItems, uniqueItems, additionalItems):
  https://raw.githubusercontent.com/everit-org/json-schema/master/core/src/main/java/org/everit/json/schema/ArraySchema.java
- ArraySchemaTest.java (complete array tests):
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


HIGH PRIORITY — Object Constraints (ObjectValidator.java)

minProperties, maxProperties, dependentRequired, dependentSchemas, and additionalProperties as schema are missing.

everit-org/json-schema
- ObjectSchema.java (minProperties, maxProperties, additionalProperties as schema, dependencies):
  https://raw.githubusercontent.com/everit-org/json-schema/master/core/src/main/java/org/everit/json/schema/ObjectSchema.java
- Visitor.java (how the visitor traverses minProperties / maxProperties):
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


MEDIUM PRIORITY — const Keyword and Metadata

everit-org/json-schema
- ConstSchema.java (const model):
  https://raw.githubusercontent.com/everit-org/json-schema/master/core/src/main/java/org/everit/json/schema/ConstSchema.java
- ValidatingVisitor.java (const validation via visitor):
  https://raw.githubusercontent.com/everit-org/json-schema/master/core/src/main/java/org/everit/json/schema/ValidatingVisitor.java

networknt/json-schema-validator
- ConstValidator.java:
  https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/ConstValidator.java
- ReadOnlyValidator.java (readOnly / writeOnly):
  https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/ReadOnlyValidator.java


MEDIUM PRIORITY — Navigation in SchemaRefResolver.java

The current navigateTo() does not support prefixItems, allOf, anyOf. The files below show how to solve this.

networknt/json-schema-validator
- RefValidator.java ($ref resolution with complete navigation):
  https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/RefValidator.java
- JsonSchema.java (schema core with reference resolution):
  https://raw.githubusercontent.com/networknt/json-schema-validator/master/src/main/java/com/networknt/schema/JsonSchema.java

everit-org/json-schema
- SchemaLoaderTest.java (tests cover all $ref cases):
  https://raw.githubusercontent.com/everit-org/json-schema/master/core/src/test/java/org/everit/json/schema/loader/SchemaLoaderTest.java


NOTES
- All links point directly to raw content from official repositories.
- The networknt files are the most straightforward for implementation: each keyword has its own isolated validator, easy to adapt to your project's ArrayValidator.java and ObjectValidator.java.
- The everit-org files are useful for understanding the complete model structure (how minItems, maxItems fields are in ArraySchema.java along with validation logic).
- In case of 404 error, check if the branch is still master; otherwise, replace with main in the URL.

Generated based on code-audit-2026-03.md — Schema-Validator v0.5.0
Links converted to raw on: 2026-03-24