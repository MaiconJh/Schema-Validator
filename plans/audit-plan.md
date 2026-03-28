# Schema-Validator Source Code to Documentation Audit Plan

## Objective
Identify drift between source code implementation and documentation for the Schema-Validator project by comparing actual behavior/API with documented behavior.

## Overview
This audit will:
1. Extract key behaviors and APIs from source code
2. Locate corresponding documentation
3. Compare source code behavior/API with documentation to find inconsistencies (drift)
4. Document the drift found

## Subtasks

### 1. Source Code Analysis
**Mode:** code
**Objective:** Extract key behaviors, APIs, and implementation details from source code
**Files to focus on:**
- `src/main/java/com/maiconjh/schemacr/core/ValidationService.java`
- `src/main/java/com/maiconjh/schemacr/schemes/FileSchemaLoader.java`
- `src/main/java/com/maiconjh/schemacr/validation/ObjectValidator.java`
- `src/main/java/com/maiconjh/schemacr/validation/ArrayValidator.java`
- `src/main/java/com/maiconjh/schemacr/validation/PrimitiveValidator.java`
- `src/main/java/com/maiconjh/schemacr/validation/FormatValidator.java`
- `src/main/java/com/maiconjh/schemacr/validation/ValidatorDispatcher.java`
- `src/main/java/com/maiconjh/schemacr/schemes/SchemaRefResolver.java`
- `src/main/java/com/maiconjh/schemacr/schemes/Schema.java`
- `src/main/java/com/maiconjh/schemacr/schemes/SupportedKeywordsRegistry.java`

**Expected output:** 
- List of key classes and their public methods
- Behavioral descriptions extracted from code comments and implementation
- API contracts and validation rules implemented
- Keyword support matrix (which JSON Schema keywords are implemented)

### 2. Documentation Analysis
**Mode:** ask
**Objective:** Locate and extract documented behavior and API information
**Files to focus on:**
- `docs/pages/validation-behavior.md`
- `docs/pages/schema-keywords.md`
- `docs/pages/config-reference.md`
- `docs/pages/dev-guide.md`
- `docs/pages/getting-started.md`
- `docs/pages/first-validation.md`
- `docs/pages/examples-and-schema-construction.md`
- `docs/reference/schema-keywords.md`
- `docs/reference/validation-behavior.md`
- `docs/tutorials/first-validation.md`

**Expected output:**
- Documented behavior descriptions
- API usage instructions
- Keyword support claims
- Configuration options and their effects
- Validation flow descriptions

### 3. Comparison and Drift Identification
**Mode:** debug
**Objective:** Compare source code behavior with documentation to identify inconsistencies
**Approach:**
- For each key behavior/API identified in source code:
  1. Find corresponding documentation
  2. Compare implementation details with documented behavior
  3. Note any discrepancies (missing documentation, incorrect documentation, unimplemented features)
- For each documented feature:
  1. Verify implementation exists in source code
  2. Check if implementation matches documentation
  3. Note any gaps or mismatches

**Expected output:**
- Drift report documenting:
  - Features implemented but not documented
  - Features documented but not implemented
  - Features implemented differently than documented
  - Documentation that is outdated or incorrect
  - Missing documentation for key features

### 4. Drift Documentation
**Mode:** architect
**Objective:** Create a comprehensive drift report
**Format:**
- Executive summary
- Detailed drift findings categorized by:
  - ValidationService discrepancies
  - Schema loading discrepancies
  - Object validation discrepancies
  - Array validation discrepancies
  - Primitive validation discrepancies
  - Keyword support discrepancies
  - Configuration discrepancies
- Recommendations for fixing drift
- Prioritization of fixes

**Expected output:**
- `docs/explanation/audit-drift-report.md` (or similar) containing the complete drift analysis

## Execution Steps

1. **Prepare** - Set up audit environment and tools
2. **Extract Source Information** - Run source code analysis subtask
3. **Extract Documentation Information** - Run documentation analysis subtask
4. **Compare and Identify Drift** - Run comparison subtask
5. **Document Findings** - Create drift report
6. **Review** - Validate findings with stakeholders

## Tools and Techniques

- Use regex searches to extract method signatures and comments
- Use code navigation to understand call flows
- Cross-reference JavaDoc comments with implementation
- Compare against JSON Schema specification where relevant
- Create traceability matrices linking code to documentation

## Success Criteria

- Complete mapping of key source components to documentation
- Identification of all significant drift (>5 minor issues or >1 major issue)
- Clear, actionable recommendations for resolving drift
- Drift report suitable for guiding documentation updates