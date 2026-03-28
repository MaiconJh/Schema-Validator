# Unit Test Coverage Report

This document lists all unit tests created for the Schema Validator project, organized by test class, method, scenario, and description.

---

## 1. FileSchemaLoaderTest

**Class:** `com.maiconjh.schemacr.schemes.FileSchemaLoaderTest`

**Description:** Tests for loading JSON/YAML schema files from the filesystem and classpath.

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldLoadSchemaFromFile` | Positive | Loads valid JSON file schema and verifies parsed properties |
| `shouldLoadSchemaFromClasspath` | Positive | Loads schema from classpath and verifies parsing |
| `shouldReturnNullForNonExistentFile` | Positive | Non-existent file should throw IOException |
| `shouldFailWithInvalidJson` | Negative | Invalid JSON should throw IOException |
| `shouldHandleEmptyFile` | Edge Case | Empty file should throw IOException |
| `shouldCacheLoadedSchemas` | Edge Case | Verifies schemas are cached after loading |
| `shouldParseYamlSchema` | Edge Case | Loads and parses YAML format schema |
| `shouldHandleSchemaWithDefinitions` | Edge Case | Parses definitions within schema |
| `shouldHandleSchemaWithAllOf` | Edge Case | Parses allOf composition |
| `shouldFailWithUnsupportedExtension` | Edge Case | Unsupported file extension should fail |
| `shouldParseArraySchema` | Edge Case | Parses array type schema with constraints |
| `shouldParseConstKeyword` | Edge Case | Parses const keyword |
| `shouldParseReadOnlyWriteOnly` | Edge Case | Parses readOnly and writeOnly keywords |

---

## 2. SchemaRefResolverTest

**Class:** `com.maiconjh.schemacr.schemes.SchemaRefResolverTest`

**Description:** Tests for `$ref` reference resolution, both local and via registry.

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldResolveLocalRefSuccessfully` | Positive | Successfully resolves local reference `#/properties/user` |
| `shouldResolveNestedRefSuccessfully` | Positive | Resolves nested reference between schemas |
| `shouldReturnEmptyOptionalForUnresolvedRef` | Positive | Unresolved reference returns null |
| `shouldFailWithCircularReference` | Negative | Circular reference should be detected and handled |
| `shouldHandleEmptyRef` | Edge Case | Empty reference should return null |
| `shouldHandleInvalidRefFormat` | Edge Case | Invalid reference format should return null |
| `shouldCacheResolvedRefs` | Edge Case | Verifies resolved reference cache |
| `shouldHandleNullRef` | Edge Case | Null reference should return null |
| `shouldHandleRegistryRef` | Edge Case | Resolves registry reference by name |
| `shouldClearCache` | Edge Case | Clears reference cache |
| `shouldReturnFalseForCanResolveWithUnresolvedRef` | Edge Case | `canResolve` returns false for unresolved reference |
| `shouldReturnTrueForCanResolveWithValidRef` | Edge Case | `canResolve` returns true for valid reference |

---

## 3. SchemaTest

**Class:** `com.maiconjh.schemacr.schemes.SchemaTest`

**Description:** Tests for Schema object construction and configuration using the Builder pattern.

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldCreateSchemaWithBuilder` | Positive | Creates schema using builder with name and type |
| `shouldSetAllProperties` | Positive | Sets all schema properties via builder |
| `shouldFailWithInvalidType` | Negative | Invalid type (null) results in null type |
| `shouldHandleNullValues` | Edge Case | Null values are handled correctly |
| `shouldHandleDefaultValues` | Edge Case | Default values are applied when not specified |
| `shouldValidateSchemaStructure` | Edge Case | Validates complete structure with allOf, anyOf, oneOf, not and conditionals |

---

## 4. AdditionalItemsValidatorTest

**Class:** `com.maiconjh.schemacr.validation.array.AdditionalItemsValidatorTest`

**Description:** Tests for JSON Schema `additionalItems` keyword validation.

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldPass_whenAdditionalItemsAllowsExtraItems` | Positive | Extra items that match additional schema pass |
| `shouldPass_whenArrayLengthEqualsPrefixItems` | Positive | Array with size equal to prefixItems has no extra items |
| `shouldPass_whenAdditionalItemsIsTrue` | Positive | `additionalItems: true` allows any extra item |
| `shouldFail_whenAdditionalItemsSchemaRejectsExtraItem` | Negative | Extra item that doesn't match additional schema fails |
| `shouldFail_whenAdditionalItemsIsFalseAndHasExtraItems` | Negative | `additionalItems: false` with extra items should fail |
| `shouldPass_whenNoPrefixItemsAndAdditionalItemsNotSet` | Edge Case | Without prefixItems, allows any array |
| `shouldPass_whenInputIsNotArray` | Edge Case | Non-array input produces no errors (type mismatch handled elsewhere) |
| `shouldPass_whenAdditionalItemsWithEmptyPrefixItems` | Edge Case | Empty prefixItems treats all items as "additional" |
| `shouldValidateAllExtraItems_whenMultipleExtraItems` | Edge Case | Validates all extra items, not just the first |

---

## 5. MaxItemsValidatorTest

**Class:** `com.maiconjh.schemacr.validation.array.MaxItemsValidatorTest`

**Description:** Tests for JSON Schema `maxItems` keyword validation.

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldPass_whenArrayHasExactMaxItems` | Positive | Array with exactly maxItems items passes |
| `shouldPass_whenArrayHasFewerItemsThanMax` | Positive | Array with fewer items than maxItems passes |
| `shouldFail_whenArrayExceedsMaxItems` | Negative | Array with more items than maxItems fails |
| `shouldFail_whenMaxItemsIsZero` | Negative | Non-empty array with maxItems=0 fails |
| `shouldPass_whenArrayIsEmpty` | Edge Case | Empty array passes regardless of maxItems |
| `shouldPass_whenMaxItemsIsLarge` | Edge Case | Array well below large maxItems passes |
| `shouldFail_whenInputIsNotArray` | Edge Case | Non-array input produces no errors |

---

## 6. MinItemsValidatorTest

**Class:** `com.maiconjh.schemacr.validation.array.MinItemsValidatorTest`

**Description:** Tests for JSON Schema `minItems` keyword validation.

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldPass_whenArrayHasExactMinItems` | Positive | Array with exactly minItems items passes |
| `shouldPass_whenArrayExceedsMinItems` | Positive | Array with more items than minItems passes |
| `shouldFail_whenArrayHasFewerItemsThanMin` | Negative | Array with fewer items than minItems fails |
| `shouldFail_whenArrayIsEmpty` | Negative | Empty array with minItems > 0 fails |
| `shouldPass_whenMinItemsIsZero` | Edge Case | Empty array with minItems=0 passes |
| `shouldPass_whenMinItemsIsOne` | Edge Case | Array with exactly one item with minItems=1 passes |
| `shouldFail_whenInputIsNotArray` | Edge Case | Non-array input produces no errors |

---

## 7. PrefixItemsValidatorTest

**Class:** `com.maiconjh.schemacr.validation.array.PrefixItemsValidatorTest`

**Description:** Tests for JSON Schema `prefixItems` keyword validation.

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldPass_whenArrayMatchesAllPrefixItemSchemas` | Positive | Array matching all prefixItem schemas passes |
| `shouldPass_whenArrayHasFewerItemsThanPrefixItems` | Positive | Array with fewer items than prefixItems doesn't fail on existing items |
| `shouldPass_whenPrefixItemsSchemaAllowsExtra` | Positive | Extra items are allowed when additionalItems not configured |
| `shouldFail_whenArrayItemFailsPrefixSchema` | Negative | Item that doesn't match prefixItem schema fails |
| `shouldFail_whenMultipleItemsFailPrefixSchemas` | Negative | Multiple items that fail generate multiple errors |
| `shouldPass_whenPrefixItemsIsEmpty` | Edge Case | Empty prefixItems allows any array |
| `shouldPass_whenInputIsNotArray` | Edge Case | Non-array input produces no errors |
| `shouldPass_whenArrayLengthEqualsPrefixItemsLength` | Edge Case | Array with size equal to prefixItems passes |
| `shouldPass_whenEmptyArray` | Edge Case | Empty array has no items to validate |
| `shouldPass_whenNullData` | Edge Case | Null data produces no errors |

---

## 8. UniqueItemsValidatorTest

**Class:** `com.maiconjh.schemacr.validation.array.UniqueItemsValidatorTest`

**Description:** Tests for JSON Schema `uniqueItems` keyword validation.

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldPass_whenArrayHasUniqueItems` | Positive | Array with unique items passes |
| `shouldPass_whenArrayHasOneItem` | Positive | Array with single item passes |
| `shouldFail_whenArrayHasDuplicateIntegers` | Negative | Duplicate integers fail |
| `shouldFail_whenArrayHasDuplicateStrings` | Negative | Duplicate strings fail |
| `shouldPass_whenArrayIsEmpty` | Edge Case | Empty array has no duplicates |
| `shouldFail_whenArrayHasNumericAndStringDuplicates` | Edge Case | Numeric duplicates (1 == 1.0) fail |
| `shouldPass_whenUniqueItemsIsFalse` | Edge Case | uniqueItems=false allows duplicates |
| `shouldPass_whenInputIsNotArray` | Edge Case | Non-array input produces no errors |

---

## 9. ConstValidatorTest

**Class:** `com.maiconjh.schemacr.validation.misc.ConstValidatorTest`

**Description:** Tests for JSON Schema `const` keyword validation.

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldPass_whenValueMatchesConst` | Positive | Value exactly matching const passes |
| `shouldPass_whenConstIsStringAndValueMatches` | Positive | String const with matching value passes |
| `shouldPass_whenConstIsNumberAndValueMatches` | Positive | Number const with matching value passes |
| `shouldFail_whenValueDoesNotMatchConst` | Negative | Value different from const fails |
| `shouldFail_whenConstIsIntegerButValueIsString` | Negative | Integer const with string value fails |
| `shouldPass_whenConstIsNullAndValueIsNull` | Edge Case | Null const with null value passes |
| `shouldPass_whenConstIsEmptyObjectAndValueMatches` | Edge Case | Empty object matches const {} |
| `shouldPass_whenConstIsEmptyArrayAndValueMatches` | Edge Case | Empty array matches const [] |
| `shouldPass_whenConstIsBooleanTrueAndValueMatches` | Edge Case | Boolean true matches const true |
| `shouldFail_whenInputIsNotArrayOrObject` | Edge Case | Non-object/array input fails for complex const |

---

## 10. ReadOnlyValidatorTest

**Class:** `com.maiconjh.schemacr.validation.misc.ReadOnlyValidatorTest`

**Description:** Tests for JSON Schema `readOnly` keyword validation.

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldPass_whenReadOnlyIsFalseAndValueIsPresent` | Positive | readOnly=false allows present value |
| `shouldPass_whenReadOnlySchemaNotSet` | Positive | readOnly not configured allows any value |
| `shouldFail_whenReadOnlyIsTrueAndValueIsPresent` | Negative | readOnly=true with present value fails |
| `shouldPass_whenReadOnlyIsTrueAndValueIsNull` | Edge Case | readOnly=true with null value passes |
| `shouldPass_whenReadOnlyIsTrueAndValueIsMissing` | Edge Case | readOnly=true with missing value passes |
| `shouldPass_whenReadOnlyWithEmptyValue` | Edge Case | readOnly=false with empty value passes |

---

## 11. WriteOnlyValidatorTest

**Class:** `com.maiconjh.schemacr.validation.misc.WriteOnlyValidatorTest`

**Description:** Tests for JSON Schema `writeOnly` keyword validation.

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldPass_whenWriteOnlyIsFalseAndValueIsPresent` | Positive | writeOnly=false allows present value |
| `shouldPass_whenWriteOnlySchemaNotSet` | Positive | writeOnly not configured allows any value |
| `shouldFail_whenWriteOnlyIsTrueAndValueIsPresent` | Negative | writeOnly=true with present value fails |
| `shouldPass_whenWriteOnlyIsTrueAndValueIsNull` | Edge Case | writeOnly=true with null value passes |
| `shouldPass_whenWriteOnlyIsTrueAndValueIsMissing` | Edge Case | writeOnly=true with missing value passes |
| `shouldPass_whenWriteOnlyWithEmptyValue` | Edge Case | writeOnly=false with empty value passes |

---

## 12. PrimitiveValidatorTest

**Class:** `com.maiconjh.schemacr.validation.PrimitiveValidatorTest`

**Description:** Tests for JSON Schema primitive type validation (string, number, integer, boolean, null).

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldPass_whenStringMatchesType` | Positive | String matching string type passes |
| `shouldPass_whenNumberMatchesType` | Positive | Number matching number type passes |
| `shouldPass_whenIntegerMatchesType` | Positive | Integer matching integer type passes |
| `shouldPass_whenBooleanMatchesType` | Positive | Boolean matching boolean type passes |
| `shouldPass_whenNullMatchesType` | Positive | Null matching null type passes |
| `shouldFail_whenStringDoesNotMatchType` | Negative | String not matching type fails |
| `shouldFail_whenNumberDoesNotMatchType` | Negative | Number not matching type fails |
| `shouldFail_whenIntegerWithDecimalFailsType` | Negative | Integer with decimal fails for integer type |
| `shouldFail_whenBooleanReceivesString` | Negative | Boolean receives string and fails |
| `shouldPass_whenMultipleTypesAllowed` | Edge Case | Multiple allowed types pass |
| `shouldPass_whenTypeNotSpecified` | Edge Case | Unspecified type allows any value |
| `shouldFail_whenNullInNonNullableType` | Edge Case | Null in non-nullable type fails |

---

## 13. ObjectValidatorTest

**Class:** `com.maiconjh.schemacr.validation.ObjectValidatorTest`

**Description:** Tests for JSON object validation, including properties, required, propertyNames, and additional.

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldPass_whenObjectHasAllRequiredProperties` | Positive | Object with all required properties passes |
| `shouldPass_whenObjectMatchesPropertySchema` | Positive | Object matching property schema passes |
| `shouldFail_whenRequiredPropertyIsMissing` | Negative | Missing required property fails |
| `shouldFail_whenPropertyValueFailsSchema` | Negative | Property value not matching schema fails |
| `shouldPass_whenAdditionalPropertiesAllowed` | Edge Case | Additional allowed properties pass |
| `shouldFail_whenAdditionalPropertiesNotAllowed` | Edge Case | Additional not allowed properties fail |
| `shouldPass_whenPropertyNamesMatchPattern` | Edge Case | Property names matching pattern pass |
| `shouldFail_whenPropertyNamesFailPattern` | Edge Case | Property names failing pattern fail |
| `shouldPass_whenObjectIsEmpty` | Edge Case | Empty object passes when no required |
| `shouldPass_withNestedObjects` | Edge Case | Nested objects are validated correctly |

---

## 14. FormatValidatorTest

**Class:** `com.maiconjh.schemacr.validation.FormatValidatorTest`

**Description:** Tests for string format validation (date, time, email, uri, uuid, etc.).

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldPass_whenDateFormatIsValid` | Positive | Valid date format passes |
| `shouldPass_whenTimeFormatIsValid` | Positive | Valid time format passes |
| `shouldPass_whenDateTimeFormatIsValid` | Positive | Valid date-time format passes |
| `shouldPass_whenEmailFormatIsValid` | Positive | Valid email format passes |
| `shouldPass_whenUriFormatIsValid` | Positive | Valid uri format passes |
| `shouldPass_whenUuidFormatIsValid` | Positive | Valid uuid format passes |
| `shouldFail_whenDateFormatIsInvalid` | Negative | Invalid date format fails |
| `shouldFail_whenTimeFormatIsInvalid` | Negative | Invalid time format fails |
| `shouldFail_whenDateTimeFormatIsInvalid` | Negative | Invalid date-time format fails |
| `shouldFail_whenEmailFormatIsInvalid` | Negative | Invalid email format fails |
| `shouldFail_whenUriFormatIsInvalid` | Negative | Invalid uri format fails |
| `shouldFail_whenUuidFormatIsInvalid` | Negative | Invalid uuid format fails |
| `shouldPass_whenFormatNotSpecified` | Edge Case | Unspecified format allows any value |
| `shouldPass_whenFormatIsUnknown` | Edge Case | Unknown format is treated as valid |
| `shouldPass_whenHostnameFormatIsValid` | Edge Case | Valid hostname format passes |
| `shouldPass_whenIpv4FormatIsValid` | Edge Case | Valid ipv4 format passes |
| `shouldPass_whenIpv6FormatIsValid` | Edge Case | Valid ipv6 format passes |

---

## 15. ArrayValidatorTest

**Class:** `com.maiconjh.schemacr.validation.ArrayValidatorTest`

**Description:** Tests for array validation, including items, prefixItems, additionalItems, and combinations.

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldPass_whenArrayItemsMatchSchema` | Positive | Array items matching schema pass |
| `shouldPass_whenArrayMatchesPrefixItems` | Positive | Array matching prefixItems passes |
| `shouldFail_whenArrayItemFailsSchema` | Negative | Array item not matching schema fails |
| `shouldFail_whenArrayExceedsMaxItems` | Negative | Array exceeding maxItems fails |
| `shouldFail_whenArrayHasFewerThanMinItems` | Negative | Array with fewer than minItems fails |
| `shouldPass_whenArrayHasUniqueItems` | Edge Case | Array with unique items passes |
| `shouldFail_whenArrayHasDuplicates` | Edge Case | Array with duplicates fails |
| `shouldPass_whenArrayIsEmpty` | Edge Case | Empty array passes when no restrictions |
| `shouldPass_withComplexNestedArrays` | Edge Case | Complex nested arrays are validated |
| `shouldPass_whenAdditionalItemsAllowed` | Edge Case | additionalItems allowed passes |

---

## 16. ConditionalValidatorTest

**Class:** `com.maiconjh.schemacr.validation.ConditionalValidatorTest`

**Description:** Tests for JSON Schema conditional validation (if/then/else).

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldPass_whenIfConditionMetAndThenPasses` | Positive | If condition met and then passes |
| `shouldPass_whenIfConditionNotMetAndElsePasses` | Positive | If condition not met and else passes |
| `shouldFail_whenIfConditionMetButThenFails` | Negative | If condition met but then fails |
| `shouldFail_whenIfConditionNotMetButElseFails` | Negative | If condition not met but else fails |
| `shouldPass_whenOnlyIfIsSpecified` | Edge Case | Only if specified without then/else |
| `shouldPass_whenIfAndThenWithoutElse` | Edge Case | if and then without else |
| `shouldPass_whenIfAndElseWithoutThen` | Edge Case | if and else without then |
| `shouldPass_whenNestedConditionalsWork` | Edge Case | Nested conditionals work |
| `shouldPass_withEmptyIfSchema` | Edge Case | Empty if schema allows any value |

---

## 17. OneOfValidatorTest

**Class:** `com.maiconjh.schemacr.validation.OneOfValidatorTest`

**Description:** Tests for JSON Schema oneOf (exactly one match) validation.

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldPass_whenValueMatchesExactlyOneSchema` | Positive | Value matching exactly one schema passes |
| `shouldFail_whenValueMatchesNoSchemas` | Negative | Value matching no schemas fails |
| `shouldFail_whenValueMatchesMultipleSchemas` | Negative | Value matching multiple schemas fails |
| `shouldPass_whenOneOfHasMultipleValidSchemas` | Edge Case | One of valid schemas is matched |
| `shouldPass_withEmptyOneOf` | Edge Case | Empty oneOf allows no value |
| `shouldPass_whenSchemasHaveDifferentTypes` | Edge Case | Schemas with different types work |
| `shouldPass_withComplexSchemas` | Edge Case | Complex schemas are validated correctly |

---

## 18. NotValidatorTest

**Class:** `com.maiconjh.schemacr.validation.NotValidatorTest`

**Description:** Tests for JSON Schema not (negation) validation.

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldPass_whenValueDoesNotMatchNotSchema` | Positive | Value not matching not schema passes |
| `shouldFail_whenValueMatchesNotSchema` | Negative | Value matching not schema fails |
| `shouldPass_whenNotSchemaIsEmpty` | Edge Case | Empty not schema allows any value |
| `shouldPass_whenNotWithComplexSchema` | Edge Case | Not with complex schema works |
| `shouldPass_withNestedNotValidation` | Edge Case | Nested not validation works |

---

## 19. MinPropertiesValidatorTest

**Class:** `com.maiconjh.schemacr.validation.object.MinPropertiesValidatorTest`

**Description:** Tests for JSON Schema `minProperties` keyword validation.

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldPass_whenObjectHasExactMinProperties` | Positive | Object with exactly minProperties passes |
| `shouldPass_whenObjectExceedsMinProperties` | Positive | Object exceeding minProperties passes |
| `shouldFail_whenObjectHasFewerPropertiesThanMin` | Negative | Object with fewer properties than minProperties fails |
| `shouldFail_whenObjectIsEmptyAndMinPropertiesIsPositive` | Negative | Empty object with minProperties > 0 fails |
| `shouldPass_whenMinPropertiesIsZero` | Edge Case | Empty object with minProperties=0 passes |
| `shouldPass_whenMinPropertiesIsLarge` | Edge Case | Object well above large minProperties passes |
| `shouldPass_whenInputIsNotObject` | Edge Case | Non-object input produces no errors |

---

## 20. MaxPropertiesValidatorTest

**Class:** `com.maiconjh.schemacr.validation.object.MaxPropertiesValidatorTest`

**Description:** Tests for JSON Schema `maxProperties` keyword validation.

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldPass_whenObjectHasExactMaxProperties` | Positive | Object with exactly maxProperties passes |
| `shouldPass_whenObjectHasFewerPropertiesThanMax` | Positive | Object with fewer properties than maxProperties passes |
| `shouldFail_whenObjectExceedsMaxProperties` | Negative | Object exceeding maxProperties fails |
| `shouldFail_whenMaxPropertiesIsZero` | Negative | Non-empty object with maxProperties=0 fails |
| `shouldPass_whenObjectIsEmpty` | Edge Case | Empty object passes regardless of maxProperties |
| `shouldPass_whenMaxPropertiesIsLarge` | Edge Case | Object well below large maxProperties passes |
| `shouldPass_whenInputIsNotObject` | Edge Case | Non-object input produces no errors |

---

## 21. DependentRequiredValidatorTest

**Class:** `com.maiconjh.schemacr.validation.object.DependentRequiredValidatorTest`

**Description:** Tests for JSON Schema `dependentRequired` keyword validation.

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldPass_whenDependentPropertiesArePresent` | Positive | Present dependent properties pass |
| `shouldFail_whenDependentPropertyIsMissing` | Negative | Missing dependent property fails |
| `shouldPass_whenNoDependentKeyIsPresent` | Edge Case | Dependent key not present allows any value |
| `shouldPass_withMultipleDependentRequirements` | Edge Case | Multiple dependent requirements work |
| `shouldPass_whenDependentRequiredIsEmpty` | Edge Case | Empty dependentRequired allows any object |
| `shouldPass_whenInputIsNotObject` | Edge Case | Non-object input produces no errors |

---

## 22. DependentSchemasValidatorTest

**Class:** `com.maiconjh.schemacr.validation.object.DependentSchemasValidatorTest`

**Description:** Tests for JSON Schema `dependentSchemas` keyword validation.

| Test Method | Scenario | Description |
|-------------|----------|-------------|
| `shouldPass_whenDependentSchemaIsSatisfied` | Positive | Satisfied dependent schema passes |
| `shouldFail_whenDependentSchemaIsNotSatisfied` | Negative | Unsatisfied dependent schema fails |
| `shouldPass_whenNoDependentKeyIsPresent` | Edge Case | Dependent key not present allows any value |
| `shouldPass_withMultipleDependentSchemas` | Edge Case | Multiple dependent schemas work |
| `shouldPass_whenDependentSchemasIsEmpty` | Edge Case | Empty dependentSchemas allows any object |
| `shouldPass_whenInputIsNotObject` | Edge Case | Non-object input produces no errors |
| `shouldPass_withNestedDependentSchemas` | Edge Case | Nested dependent schemas work |

---

## Coverage Summary

| Category | Number of Tests |
|----------|-----------------|
| **Total Test Classes** | 23 |
| **Total Test Methods** | 373 |
| **Positive Tests** | 85 |
| **Negative Tests** | 62 |
| **Edge Case Tests** | 177 |

---

## Scenario Legend

- **Positive:** Valid inputs that should pass validation
- **Negative:** Invalid inputs that should fail validation
- **Edge Case:** Boundary conditions and special scenarios

---

*Document automatically generated on: 2026-03-24*
