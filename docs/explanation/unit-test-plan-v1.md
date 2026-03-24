# Unit Test Plan - JSON Schema Validation Implementations

**Version:** 1.0  
**Last Updated:** 2026-03-24  
**Author:** Schema-CR Development Team  
**Test Framework:** JUnit Jupiter 5.10.2

---

## Table of Contents

1. [Overview](#overview)
2. [Test Organization](#test-organization)
3. [Array Validators](#array-validators)
   - [MinItemsValidator](#minitemsvalidator)
   - [MaxItemsValidator](#maxitemsvalidator)
   - [UniqueItemsValidator](#uniqueitemsvalidator)
   - [PrefixItemsValidator](#prefixitemsvalidator)
   - [AdditionalItemsValidator](#additionalitemsvalidator)
4. [Primitive Validators](#primitive-validators)
   - [ConstValidator](#constvalidator)
   - [ReadOnlyValidator](#readonlyvalidator)
   - [WriteOnlyValidator](#writeonlyvalidator)
5. [SchemaRefResolver](#schemarefresolver)
6. [Schema.java Updates](#schemajava-updates)
7. [FileSchemaLoader](#fileschemaloader)
8. [Test Infrastructure Recommendations](#test-infrastructure-recommendations)

---

## Overview

This document outlines a comprehensive unit test plan for all new JSON Schema validation implementations. The test suite is designed using JUnit Jupiter 5.10.2 and follows standard validation testing patterns.

### Test Categories

- **Positive Tests:** Verify that valid inputs pass validation
- **Negative Tests:** Verify that invalid inputs are properly rejected
- **Edge Cases:** Boundary conditions and special scenarios

---

## Test Organization

### Directory Structure

```
src/test/java/com/maiconjh/schemacr/
├── validation/
│   ├── array/
│   │   ├── MinItemsValidatorTest.java
│   │   ├── MaxItemsValidatorTest.java
│   │   ├── UniqueItemsValidatorTest.java
│   │   ├── PrefixItemsValidatorTest.java
│   │   └── AdditionalItemsValidatorTest.java
│   ├── misc/
│   │   ├── ConstValidatorTest.java
│   │   ├── ReadOnlyValidatorTest.java
│   │   └── WriteOnlyValidatorTest.java
│   └── ValidatorDispatcherTest.java
├── schemes/
│   ├── SchemaTest.java
│   ├── SchemaRefResolverTest.java
│   └── FileSchemaLoaderTest.java
└── TestFixtures.java
```

### Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Test Class | `[ComponentName]Test` | `MinItemsValidatorTest` |
| Test Method | `[scenario]_[expectedBehavior]` | `shouldPass_whenExactMinItems` |
| Test Fixture Class | `TestFixtures` | `TestFixtures` |

### Assertion Patterns

```java
// Positive test - should pass with no errors
List<ValidationError> errors = validator.validate(validData, schema, "/path", "field");
assertTrue(errors.isEmpty(), "Expected no validation errors for valid input");

// Negative test - should return errors
List<ValidationError> errors = validator.validate(invalidData, schema, "/path", "field");
assertFalse(errors.isEmpty(), "Expected validation errors for invalid input");
assertEquals(1, errors.size());
assertEquals("minItems", errors.get(0).getKeyword());
```

---

## Array Validators

### MinItemsValidator

**Class Location:** `src/main/java/com/maiconjh/schemacr/validation/array/MinItemsValidator.java`

**Test Class:** `MinItemsValidatorTest`

#### Test Methods

| Method Name | Description |
|-------------|-------------|
| `shouldPass_whenArrayHasExactMinItems` | Positive: Array with exact minimum items |
| `shouldPass_whenArrayExceedsMinItems` | Positive: Array exceeding minimum |
| `shouldFail_whenArrayHasFewerItems` | Negative: Array below minimum |
| `shouldPass_whenEmptyArrayAndMinItemsZero` | Edge: minItems=0 with empty array |
| `shouldFail_whenEmptyArrayAndMinItemsPositive` | Edge: minItems>0 with empty array |
| `shouldPass_whenNullData` | Edge: null data should not trigger error |
| `shouldPass_whenNonArrayData` | Edge: non-array data should not trigger error |

#### Test Scenarios

**Positive Cases:**
```java
// minItems = 3
[1, 2, 3]                    // Valid - exactly 3 items
[1, 2, 3, 4, 5]              // Valid - more than 3 items

// minItems = 0
[]                            // Valid - zero items allowed
[1]                           // Valid
```

**Negative Cases:**
```java
// minItems = 3
[1, 2]                        // Invalid - only 2 items
[1]                           // Invalid - only 1 item
[]                            // Invalid - empty array
```

**Edge Cases:**
```java
// Null and non-array handling
null                           // Should not fail (no constraint violation)
"not an array"                 // Should not fail (type mismatch handled elsewhere)
```

#### Suggested Assertions

```java
assertTrue(errors.isEmpty());
assertEquals(1, errors.size());
assertEquals("minItems", error.getKeyword());
assertTrue(error.getMessage().contains("at least"));
```

---

### MaxItemsValidator

**Class Location:** `src/main/java/com/maiconjh/schemacr/validation/array/MaxItemsValidator.java`

**Test Class:** `MaxItemsValidatorTest`

#### Test Methods

| Method Name | Description |
|-------------|-------------|
| `shouldPass_whenArrayHasExactMaxItems` | Positive: Array with exact maximum |
| `shouldPass_whenArrayBelowMaxItems` | Positive: Array below maximum |
| `shouldFail_whenArrayExceedsMaxItems` | Negative: Array above maximum |
| `shouldPass_whenEmptyArrayAndMaxItemsZero` | Edge: maxItems=0 with empty array |
| `shouldPass_whenNullData` | Edge: null data |
| `shouldPass_whenNonArrayData` | Edge: non-array data |

#### Test Scenarios

**Positive Cases:**
```java
// maxItems = 3
[1, 2, 3]                     // Valid - exactly 3 items
[1, 2]                        // Valid - less than 3 items

// maxItems = 0
[]                            // Valid - only empty allowed
```

**Negative Cases:**
```java
// maxItems = 3
[1, 2, 3, 4]                  // Invalid - 4 items exceeds max
[1, 2, 3, 4, 5, 6]           // Invalid - 6 items exceeds max
```

---

### UniqueItemsValidator

**Class Location:** `src/main/java/com/maiconjh/schemacr/validation/array/UniqueItemsValidator.java`

**Test Class:** `UniqueItemsValidatorTest`

#### Test Methods

| Method Name | Description |
|-------------|-------------|
| `shouldPass_whenAllItemsUnique` | Positive: All items different |
| `shouldFail_whenDuplicateItemsExist` | Negative: Duplicates found |
| `shouldPass_whenEmptyArray` | Edge: Empty array |
| `shouldPass_whenSingleItem` | Edge: Single item always valid |
| `shouldPass_whenNullData` | Edge: null data |
| `shouldPass_whenNonArrayData` | Edge: non-array data |
| `shouldDetectFirstDuplicate_only` | Edge: Should stop at first duplicate |

#### Test Scenarios

**Positive Cases:**
```java
// uniqueItems = true
[1, 2, 3, 4, 5]               // Valid - all unique
["a", "b", "c"]               // Valid - strings unique
[1.0, 2.0, 3.0]               // Valid - floats unique
[true, false]                 // Valid - booleans unique

// Complex types
[{"id": 1}, {"id": 2}]        // Valid - objects are different references
```

**Negative Cases:**
```java
// uniqueItems = true
[1, 2, 3, 2]                  // Invalid - duplicate 2
[1, 1, 1]                     // Invalid - duplicate 1
["a", "b", "a"]               // Invalid - duplicate "a"
```

**Edge Cases:**
```java
// Important: Java equals() behavior
[1, 1.0]                      // Should be considered equal (1 == 1.0)
[new Integer(1), 1]           // Should be considered equal
```

#### Suggested Assertions

```java
assertTrue(errors.isEmpty());
assertEquals(1, errors.size()); // Should report only first duplicate
assertTrue(errors.get(0).getMessage().contains("duplicate"));
```

---

### PrefixItemsValidator

**Class Location:** `src/main/java/com/maiconjh/schemacr/validation/array/PrefixItemsValidator.java`

**Test Class:** `PrefixItemsValidatorTest`

#### Test Methods

| Method Name | Description |
|-------------|-------------|
| `shouldPass_whenAllPrefixItemsValid` | Positive: All positional schemas valid |
| `shouldFail_whenFirstPrefixItemInvalid` | Negative: First position fails |
| `shouldFail_whenMiddlePrefixItemInvalid` | Negative: Middle position fails |
| `shouldFail_whenLastPrefixItemInvalid` | Negative: Last position fails |
| `shouldPass_whenArrayShorterThanPrefixSchemas` | Edge: Fewer items than schemas |
| `shouldPass_whenArrayLongerThanPrefixSchemas` | Edge: Extra items allowed (use additionalItems) |
| `shouldPass_whenEmptyArray` | Edge: Empty array - no validation |
| `shouldPass_whenNullData` | Edge: null data |
| `shouldPass_whenNonArrayData` | Edge: non-array data |

#### Test Scenarios

**Positive Cases:**
```java
// prefixItems: [{"type": "string"}, {"type": "number"}]
["hello", 42]                 // Valid - string then number
["test", 100]                 // Valid
["a", 1]                      // Valid
```

**Negative Cases:**
```java
// prefixItems: [{"type": "string"}, {"type": "number"}]
[42, "hello"]                 // Invalid - wrong order, wrong types
["hello", "world"]            // Invalid - second item not a number
[123]                         // Invalid - first item not a string
```

**Edge Cases:**
```java
// Array shorter than schemas - only validate existing items
["hello"]                     // Valid - only first position checked

// Multiple prefix schemas
["a", 1, true, {"x": 1}]      // Valid - first 4 validated against their schemas
```

#### Suggested Assertions

```java
assertTrue(errors.isEmpty());
assertEquals(1, errors.size());
assertTrue(errors.get(0).getPath().contains("[0]")); // Position in path
assertEquals("type", errors.get(0).getKeyword());
```

---

### AdditionalItemsValidator

**Class Location:** `src/main/java/com/maiconjh/schemacr/validation/array/AdditionalItemsValidator.java`

**Test Class:** `AdditionalItemsValidatorTest`

#### Test Methods

| Method Name | Description |
|-------------|-------------|
| `shouldPass_whenAdditionalItemsValid` | Positive: Extra items match schema |
| `shouldFail_whenAdditionalItemsInvalid` | Negative: Extra items fail schema |
| `shouldPass_whenNoAdditionalItems` | Edge: No extra items present |
| `shouldPass_whenAdditionalItemsSchemaIsNull` | Edge: additionalItems not defined |
| `shouldPass_whenAdditionalItemsFalse` | Edge: additionalItems: false blocks extras |
| `shouldFail_whenExtrasBlockedByFalse` | Negative: additionalItems: false with extras |
| `shouldValidateAllExtraItems` | Edge: Multiple extra items validated |

#### Test Scenarios

**Positive Cases:**
```java
// prefixItems: [{"type": "string"}], additionalItems: {"type": "number"}
["hello", 42, 100]            // Valid - extra items are numbers
["name", 1, 2, 3]             // Valid - all extras are numbers
```

**Negative Cases:**
```java
// prefixItems: [{"type": "string"}], additionalItems: {"type": "number"}
["hello", "world"]            // Invalid - extra is string, not number
["name", true]                // Invalid - extra is boolean
```

**Edge Cases:**
```java
// additionalItems: false - no extra items allowed
["hello"]                     // Valid - no extras
["hello", 1]                  // Invalid - extra item present

// additionalItems: null/undefined - treats as true (no restriction)
["hello", "world", 123]        // Valid - no restriction
```

---

## Primitive Validators

### ConstValidator

**Class Location:** `src/main/java/com/maiconjh/schemacr/validation/misc/ConstValidator.java`

**Test Class:** `ConstValidatorTest`

#### Test Methods

| Method Name | Description |
|-------------|-------------|
| `shouldPass_whenValueEqualsConst` | Positive: Exact match |
| `shouldFail_whenValueDiffersFromConst` | Negative: Different value |
| `shouldPass_whenConstIsNullAndDataIsNull` | Edge: Both null |
| `shouldFail_whenConstIsNullButDataIsNot` | Edge: Null const, non-null data |
| `shouldPass_whenConstIsEmptyString` | Edge: Empty string const |
| `shouldPass_withComplexTypes` | Edge: Objects and arrays |
| `shouldHandleNumericTypes` | Edge: Integer vs float equality |

#### Test Scenarios

**Positive Cases:**
```java
// const: "active"
"active"                      // Valid

// const: 42
42                             // Valid
42.0                           // Valid - numeric coercion

// const: true
true                           // Valid

// const: {"key": "value"}
{"key": "value"}              // Valid - exact match
```

**Negative Cases:**
```java
// const: "active"
"inactive"                    // Invalid
"ACTIVE"                      // Invalid - case sensitive

// const: 42
43                             // Invalid
"42"                          // Invalid - wrong type
```

**Edge Cases:**
```java
// Object equality - must be exactly the same
{"a": 1} != {"a": 1}          // Different object instances - may fail depending on equals()

// Numeric equality
42 == 42.0                     // Should be equal in Java
```

#### Suggested Assertions

```java
assertTrue(errors.isEmpty());
assertEquals(1, errors.size());
assertEquals("const", errors.get(0).getKeyword());
assertTrue(error.getMessage().contains("exactly"));
```

---

### ReadOnlyValidator

**Class Location:** `src/main/java/com/maiconjh/schemacr/validation/misc/ReadOnlyValidator.java`

**Test Class:** `ReadOnlyValidatorTest`

#### Test Methods

| Method Name | Description |
|-------------|-------------|
| `shouldFail_whenReadOnlyPropertyProvided` | Negative: Read-only field has data |
| `shouldPass_whenReadOnlyPropertyOmitted` | Positive: Read-only field not provided |
| `shouldPass_whenReadOnlyIsFalse` | Positive: Not a read-only field |
| `shouldPass_whenDataIsNull` | Edge: Null data for read-only |
| `shouldPass_whenNonReadOnlySchema` | Edge: No readOnly keyword |

#### Test Scenarios

**Positive Cases:**
```java
// readOnly: true
null                           // Valid - property not sent
{}                             // Valid - property omitted

// readOnly: false
"any value"                    // Valid - not read-only
```

**Negative Cases:**
```java
// readOnly: true
"some value"                   // Invalid - read-only property sent
123                            // Invalid - read-only property sent
```

#### Important Note

The `ReadOnlyValidator` is a metadata validator that flags violations. In typical API request validation scenarios, read-only properties should not be sent by clients. The validator reports an error when data is provided for read-only fields.

---

### WriteOnlyValidator

**Class Location:** `src/main/java/com/maiconjh/schemacr/validation/misc/WriteOnlyValidator.java`

**Test Class:** `WriteOnlyValidatorTest`

#### Test Methods

| Method Name | Description |
|-------------|-------------|
| `shouldFail_whenWriteOnlyPropertyInResponse` | Negative: Write-only in response |
| `shouldPass_whenWriteOnlyPropertyOmitted` | Positive: Write-only not in response |
| `shouldPass_whenWriteOnlyIsFalse` | Positive: Not write-only |
| `shouldPass_whenDataIsNull` | Edge: Null data |
| `shouldPass_whenNonWriteOnlySchema` | Edge: No writeOnly keyword |

#### Test Scenarios

**Positive Cases:**
```java
// writeOnly: true
null                           // Valid - property not returned
{}                             // Valid - property omitted

// writeOnly: false
"any value"                    // Valid
```

**Negative Cases:**
```java
// writeOnly: true
"secret value"                 // Invalid - write-only property in response
123                            // Invalid
```

#### Important Note

The `WriteOnlyValidator` is a metadata validator for API response validation. Write-only properties (like passwords) should not be included in responses. The validator reports an error when data is present for write-only fields.

---

## SchemaRefResolver

**Class Location:** `src/main/java/com/maiconjh/schemacr/schemes/SchemaRefResolver.java`

**Test Class:** `SchemaRefResolverTest`

### Test Methods - Navigation

#### Navigation to prefixItems

| Method Name | Description |
|-------------|-------------|
| `shouldResolvePrefixItemsRef` | Positive: Resolve $ref to prefixItems |
| `shouldHandleNestedPrefixItemsRef` | Edge: Nested schema references |
| `shouldReturnNullForInvalidPrefixItemsRef` | Negative: Invalid reference path |

#### Navigation to allOf

| Method Name | Description |
|-------------|-------------|
| `shouldResolveAllOfRef` | Positive: Resolve $ref inside allOf |
| `shouldMergeAllOfSchemas` | Edge: Multiple allOf references |
| `shouldHandleCircularAllOf` | Edge: Circular reference detection |

#### Navigation to anyOf

| Method Name | Description |
|-------------|-------------|
| `shouldResolveAnyOfRef` | Positive: Resolve $ref inside anyOf |
| `shouldValidateAnyOfBranch` | Edge: Test each anyOf branch |
| `shouldHandleEmptyAnyOf` | Edge: Empty anyOf array |

### Test Scenarios

**Positive Cases:**
```json
// Schema with prefixItems reference
{
  "prefixItems": [{ "$ref": "#/definitions/ItemSchema" }]
}

// AllOf reference
{
  "allOf": [{ "$ref": "#/definitions/BaseSchema" }]
}

// AnyOf reference  
{
  "anyOf": [
    { "$ref": "#/definitions/TypeA" },
    { "$ref": "#/definitions/TypeB" }
  ]
}
```

**Negative Cases:**
```json
// Invalid reference paths
{ "$ref": "#/definitions/NonExistent" }
{ "$ref": "#/invalidPath" }
```

### Test Fixtures

```java
// Create test schemas
Schema prefixItemSchema = Schema.builder("ItemSchema", SchemaType.OBJECT)
    .build();

Schema mainSchema = Schema.builder("Main", SchemaType.ARRAY)
    .prefixItems(List.of(prefixItemSchema))
    .build();
```

---

## Schema.java Updates

**Class Location:** `src/main/java/com/maiconjh/schemacr/schemes/Schema.java`

**Test Class:** `SchemaTest`

### Test Methods - New Fields Validation

| Method Name | Description |
|-------------|-------------|
| `shouldStoreMinItemsCorrectly` | Array: minItems field |
| `shouldStoreMaxItemsCorrectly` | Array: maxItems field |
| `shouldStoreUniqueItemsCorrectly` | Array: uniqueItems field |
| `shouldStorePrefixItemsCorrectly` | Array: prefixItems list |
| `shouldStoreAdditionalItemsSchema` | Array: additionalItems schema |
| `shouldStoreConstValue` | Misc: const value |
| `shouldStoreReadOnlyFlag` | Misc: readOnly boolean |
| `shouldStoreWriteOnlyFlag` | Misc: writeOnly boolean |

### Test Methods - Builder Pattern

| Method Name | Description |
|-------------|-------------|
| `shouldBuildWithArrayConstraints` | Builder: All array constraints |
| `shouldBuildWithMiscConstraints` | Builder: Const and metadata |
| `shouldReturnEmptyCollectionsForNullValues` | Builder: Null safety |
| `shouldMakeCollectionsImmutable` | Builder: Defensive copy |

### Test Scenarios

**Builder Pattern:**
```java
Schema schema = Schema.builder("TestSchema", SchemaType.ARRAY)
    .minItems(1)
    .maxItems(10)
    .uniqueItems(true)
    .prefixItems(List.of(itemSchema))
    .additionalItemsSchema(additionalSchema)
    .build();

assertEquals(1, schema.getMinItems());
assertEquals(10, schema.getMaxItems());
assertTrue(schema.isUniqueItems());
assertEquals(1, schema.getPrefixItems().size());
```

**Edge Cases:**
```java
// Null handling
Schema schema = Schema.builder("Test", SchemaType.OBJECT)
    .build();

assertTrue(schema.getMinItems() == null);
assertTrue(schema.getPrefixItems().isEmpty());
assertFalse(schema.hasArrayConstraints());
```

---

## FileSchemaLoader

**Class Location:** `src/main/java/com/maiconjh/schemacr/schemes/FileSchemaLoader.java`

**Test Class:** `FileSchemaLoaderTest`

### Test Methods - Parsing New Keywords

| Method Name | Description |
|-------------|-------------|
| `shouldParseMinItems` | Parse minItems keyword |
| `shouldParseMaxItems` | Parse maxItems keyword |
| `shouldParseUniqueItems` | Parse uniqueItems keyword |
| `shouldParsePrefixItems` | Parse prefixItems (Draft 2020-12) |
| `shouldParseAdditionalItems` | Parse additionalItems |
| `shouldParseConst` | Parse const keyword |
| `shouldParseReadOnly` | Parse readOnly keyword |
| `shouldParseWriteOnly` | Parse writeOnly keyword |
| `shouldCreateSchemaWithAllNewFields` | Integration: All fields together |

### Test Scenarios

**JSON Schema Input:**
```json
{
  "type": "array",
  "minItems": 2,
  "maxItems": 5,
  "uniqueItems": true,
  "prefixItems": [
    { "type": "string" },
    { "type": "number" }
  ],
  "additionalItems": { "type": "integer" }
}
```

```json
{
  "type": "object",
  "properties": {
    "id": { "type": "integer", "readOnly": true },
    "password": { "type": "string", "writeOnly": true },
    "status": { "const": "active" }
  }
}
```

**Test Methods:**
```java
@Test
void shouldParseMinItems() throws IOException {
    Path path = Paths.get("test-schema.json");
    Schema schema = loader.load(path, "test");
    
    assertEquals(2, schema.getMinItems());
    assertEquals(5, schema.getMaxItems());
    assertTrue(schema.isUniqueItems());
}
```

---

## Test Infrastructure Recommendations

### Test Dependencies

Add to `build.gradle`:

```groovy
testImplementation 'org.junit.jupiter:junit-jupiter:5.10.2'
testImplementation 'org.mockito:mockito-core:5.8.0'
testImplementation 'org.mockito:mockito-junit-jupiter:5.8.0'
```

### Base Test Class

Create a base class for common test utilities:

```java
package com.maiconjh.schemacr.validation;

import java.util.List;
import java.util.logging.Logger;

public abstract class ValidatorTestBase {
    protected Logger logger = Logger.getLogger(getClass().getName());
    
    protected void assertNoErrors(List<ValidationError> errors) {
        assertTrue(errors.isEmpty(), 
            "Expected no errors but got: " + errors.size());
    }
    
    protected void assertHasErrors(List<ValidationError> errors, int count) {
        assertEquals(count, errors.size());
    }
}
```

### Test Fixtures

Create reusable test data:

```java
package com.maiconjh.schemacr;

public class TestFixtures {
    // Schema fixtures
    public static Schema stringSchema() {
        return Schema.builder("StringSchema", SchemaType.STRING).build();
    }
    
    public static Schema numberSchema() {
        return Schema.builder("NumberSchema", SchemaType.NUMBER).build();
    }
    
    // Data fixtures
    public static List<String> stringArray() {
        return List.of("a", "b", "c");
    }
    
    public static List<Integer> intArray() {
        return List.of(1, 2, 3);
    }
}
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests MinItemsValidatorTest

# Run with coverage (if JaCoCo configured)
./gradlew test jacocoTestReport
```

---

## Summary

This test plan covers:

1. **5 Array Validators**: MinItems, MaxItems, UniqueItems, PrefixItems, AdditionalItems
2. **3 Primitive Validators**: Const, ReadOnly, WriteOnly
3. **SchemaRefResolver**: Reference navigation for prefixItems, allOf, anyOf
4. **Schema.java**: New fields and builder pattern tests
5. **FileSchemaLoader**: Parsing new keywords

Total test methods estimated: **60-80 test methods** covering positive, negative, and edge cases.

---

*End of Test Plan v1.0*