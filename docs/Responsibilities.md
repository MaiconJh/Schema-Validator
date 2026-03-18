# Schema-Validator Addon – Concept and Responsibilities

**Overview:**

The **Schema-Validator** addon is a robust Skript addon for Minecraft 1.21.11, designed to provide advanced schema validation and parsing for both **JSON** and **YAML** configuration files. Its primary goal is to allow server developers and Skript authors to define complex, structured schemas for game content (e.g., blocks, drops, effects, respawns) and validate player-provided or server-loaded data against these schemas in a safe, predictable, and extendable manner.

---

## Core Responsibilities:

1. **Schema Parsing & Registration**
   - Accepts `.json`, `.yml`, and `.yaml` schema files.
   - Supports nested objects, arrays, enums, optional fields, pattern properties, and primitive type constraints.
   - Registers schemas by unique name, making them accessible via Skript syntax.

2. **Validation Engine**
   - Validates incoming data (JSON/YAML files) against the registered schema.
   - Supports:
     - `type` enforcement (string, integer, number, boolean, object, array)
     - `patternProperties` for dynamic keys matching regex
     - `required` fields and `additionalProperties: false`
     - `enum` constraints for controlled vocabularies
     - Nested object validation
     - Array element validation
     - Complex number constraints (min, max, exclusive bounds)
   - Provides a detailed **ValidationError** model including:
     - Node path in the data structure
     - Expected type and constraints
     - Actual type/value
     - Human-readable error description

3. **Skript Integration**
   - Registers user-friendly Skript syntax:
     - `validate yaml %string% using schema %string%`
     - `validate json %string% using schema %string%`
     - Expressions to fetch validation errors:
       - `last validation errors`
   - Allows automatic or manual validation triggers in Skript scripts.
   - Returns validation results directly usable for Skript control flow, messaging, or logic handling.

4. **File Management**
   - Loads and parses schema files from configurable directories.
   - Supports caching for performance.
   - Handles both JSON and YAML files transparently.
   - Provides optional batch-processing for large data sets.

5. **Advanced Features**
   - Versioning and compatibility flags within schemas.
   - Integration with server plugins (e.g., WorldGuard, AuraSkills, Collections) for context-aware validation.
   - Supports advanced mathematical expressions or conditional logic evaluation (for drops, hardness, or effects).
   - Optional runtime validation on plugin load for automatic error reporting.
   - Modular architecture enabling easy extension with custom validators or new Skript syntax.

---

## Example of Validated Structure

The addon is designed to fully support the schema example provided, including complex nested properties such as:

- **Block Info**: `name`, `description`, `category`, `class`, `rarity`
- **Verification**: `block-type`, `worlds`, `y-range`, `regions`, `permissions`
- **Hardness**: `base`, `change` (tool usage, environment, modifiers)
- **Drops**: primary/rare drops, conditions, NBT, experience, evaluate code
- **Sounds & Effects**: per action (cracking, break, mining, rare-drop)
- **Stages & Respawn Mechanics**
- **Integrations**: worldguard flags, skill requirements, collections
- **Advanced Options**: caching, batch-processing, version, compatibility

All of these are validated recursively, enforcing types, allowed values, required fields, and structural correctness.

---

## Design Principles

1. **Extensibility**
   - Modular validator classes (PrimitiveValidator, ObjectValidator, ArrayValidator)
   - Schema registry for runtime dynamic loading
   - Skript syntax registration separated from core validation logic

2. **Safety**
   - Prevents invalid or incomplete configurations from breaking server logic
   - Detailed error reporting for script authors

3. **Performance**
   - Supports caching and batch-validation
   - Lazy-loading of schemas only when needed
   - Handles large, nested structures efficiently

4. **Usability**
   - Easy-to-use Skript syntax
   - Human-readable error messages
   - Works seamlessly with both JSON and YAML input files

---

## Expected Workflow

1. Developer creates a JSON/YAML schema file describing block or game data.
2. Developer writes configuration files in JSON/YAML matching the schema.
3. Skript author triggers validation using provided syntax.
4. Schema-Validator parses the schema, validates data, and returns structured error objects.
5. Errors can be displayed to server admins, logged, or used to halt script execution until data is corrected.

---

**Conclusion:**

The **Schema-Validator** addon is a foundational tool for complex Minecraft server development, enabling structured data management, validation, and reliable integration with Skript scripts. Its goal is to enforce schema correctness, reduce runtime errors, and provide Skript developers with a powerful yet intuitive API to validate game configurations.

It is designed with modularity, JSON/YAML support, and advanced nested structure validation in mind, making it suitable for large-scale, high-complexity Minecraft servers.