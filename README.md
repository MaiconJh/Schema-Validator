# Schema Validator

> ⚠️ **Important Notice**: This addon and repository were created with **Artificial Intelligence assistance**. While efforts have been made to ensure quality, there may be **bugs, inconsistencies, or errors** in the code. The project may not be perfect. Please use with caution and report any issues you find.

<div align="center">

![Java](https://img.shields.io/badge/Java-21-blue?style=for-the-badge&logo=java)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21-green?style=for-the-badge&logo=minecraft)
![Skript](https://img.shields.io/badge/Skript-2.14-orange?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

A Minecraft (Paper/Spigot) plugin that allows validating YAML/JSON data using schemas, integrated with Skript.

</div>

---

## 📋 Description

**Schema Validator** is a Skript addon that provides powerful data validation using JSON Schema-like schemas. It allows you to define complex data structures and validate YAML or JSON configuration files against them.

### ✨ Features

- 📄 Support for **YAML** and **JSON**
- 🔍 Complex schema validation with **patternProperties**
- ⚡ Native **Skript** integration
- 🎯 Validation of objects, arrays, strings, numbers, and booleans
- 📊 Detailed error system
- 🔄 Schema reference support (`$ref`)

---

## 🚀 Installation

### Prerequisites

- [Paper](https://papermc.io/) or [Spigot](https://www.spigotmc.org/) 1.21+
- [Skript](https://github.com/SkriptLang/Skript) 2.14+
- Java 21

### Installation Steps

1. **Download the plugin**
   - Build the project: `gradlew build`
   - Or download the latest version from [Releases](../../releases)

2. **Install on server**
   ```
   /plugins/
   ├── Schema-Validator-0.1.0-SNAPSHOT.jar
   ├── Skript.jar
   └── [other plugins]
   ```

3. **Configure**
   - Create a `schemas/` folder in `plugins/Schema-Validator/`
   - Place your schema files (.json/.yml) in the folder

4. **Restart the server**

---

## ⚙️ Configuration

### Folder Structure

```
plugins/
├── Schema-Validator/
│   ├── schemas/
│   │   ├── player-profile.schema.json
│   │   ├── custom-block.schema.json
│   │   └── ...
│   ├── examples/
│   │   └── ...
│   └── config.yml
└── [your Skript scripts]
```

### config.yml

```yaml
# Schema Validator Settings
settings:
  # Cache loaded schemas
  cache-enabled: true
  # Cache expiry time (in milliseconds)
  cache-expiry: 3600000
```

---

## 📖 Usage

### Skript Syntax

#### Validate YAML

```skript
validate yaml <path> using schema <path>
```

#### Validate JSON

```skript
validate json <path> using schema <path>
```

#### Get Validation Errors

```skript
set {_errors::*} to last schema validation errors
```

### Examples

#### Example 1: Basic Validation

```skript
command /validate:
    trigger:
        validate yaml "examples/player.yml" using schema "schemas/player-profile.schema.json"
        
        set {_errors::*} to last schema validation errors
        
        if size of {_errors::*} is 0:
            broadcast "✓ Valid data!"
        else:
            broadcast "✗ Errors found:"
            loop {_errors::*}:
                broadcast "- %loop-value%"
```

#### Example 2: Custom Blocks System

```skript
on script load:
    # Load schemas on startup
    validate yaml "schemas/custom-blocks.yml" using schema "schemas/custom-block.schema.json"

on player break diamond ore:
    # Validate block configuration
    set {_block-id} to "diamond_ore_custom"
    validate yaml "blocks/%{_block-id}%.yml" using schema "schemas/custom-block.schema.json"
    
    if size of {validation::errors::*} is 0:
        # Proceed with custom logic
        broadcast "Valid block! Processing drops..."
    else:
        broadcast "Invalid block configuration!"
```

#### Example 3: Player Data Validation

```skript
function validatePlayerData(player: player) :: boolean:
    set {_file} to "playerdata/%uuid of {_player}%.yml"
    validate yaml {_file} using schema "schemas/player-profile.schema.json"
    
    set {_errors::*} to last schema validation errors
    if size of {_errors::*} is 0:
        return true
    else:
        loop {_errors::*}:
            send "&cError: %loop-value%" to {_player}
        return false
```

---

## 📝 Schema Reference

### Supported Types

| Type | Description | Example |
|------|-------------|---------|
| `string` | Text | `"hello"` |
| `number` | Number | `42`, `3.14` |
| `integer` | Integer | `42` |
| `boolean` | Boolean | `true`, `false` |
| `object` | Object | `{ "key": "value" }` |
| `array` | List | `[1, 2, 3]` |
| `null` | Null | `null` |
| `any` | Any type | any value |

### Schema Properties

```json
{
  "type": "object",
  "properties": {
    "name": { "type": "string" },
    "age": { "type": "number", "minimum": 0 },
    "email": { "type": "string", "pattern": "^[^@]+@[^@]+$" }
  },
  "required": ["name", "email"],
  "additionalProperties": false
}
```

### Validation Properties

| Property | Description |
|----------|-------------|
| `type` | Data type |
| `properties` | Object properties |
| `patternProperties` | Regex properties |
| `items` | Array item schema |
| `required` | Required fields |
| `minimum` / `maximum` | Numeric limits |
| `minLength` / `maxLength` | String limits |
| `pattern` | String regex |
| `enum` | Allowed values |
| `additionalProperties` | Allow extra properties |

### Complex Schema Example

```json
{
  "type": "object",
  "patternProperties": {
    "^[a-zA-Z0-9_-]+$": {
      "type": "object",
      "properties": {
        "block-id": { "type": "string" },
        "info": {
          "type": "object",
          "properties": {
            "name": { "type": "string" },
            "category": {
              "type": "string",
              "enum": ["blocks", "ores", "metals", "crystals"]
            }
          },
          "required": ["name", "category"]
        },
        "hardness": {
          "type": "object",
          "properties": {
            "base": { "type": "number", "minimum": 0 }
          },
          "required": ["base"]
        }
      },
      "required": ["block-id", "info"]
    }
  }
}
```

---

## 📁 Project Structure

```
Schema-Validator/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/maiconjh/schemacr/
│   │   │       ├── config/          # Plugin configuration
│   │   │       ├── core/            # Core logic
│   │   │       ├── integration/    # Skript integration
│   │   │       ├── schemes/        # Schema loading
│   │   │       └── validation/     # Validators
│   │   └── resources/
│   │       ├── examples/            # Usage examples
│   │       └── schemas/             # Example schemas
│   └── test/
├── docs/                            # Documentation
├── build.gradle
└── settings.gradle
```

---

## 🛠️ Building

### Build the Plugin

```bash
# Build with Gradle
gradlew build

# Clean previous build
gradlew clean build

# Generate JAR with dependencies
gradlew shadowJar
```

### Output

The compiled JAR will be at:
```
build/libs/Schema-Validator-0.1.0-SNAPSHOT.jar
```

---

## 🤝 Contribution Guidelines

### How to Contribute

1. **Fork** the repository
2. Create a **branch** for your feature (`git checkout -b feature/MyFeature`)
3. **Commit** your changes (`git commit -m 'Add new feature'`)
4. **Push** to the branch (`git push origin feature/MyFeature`)
5. Open a **Pull Request**

### Code Standards

- Use **Java 21**
- Follow existing code style
- Add **JavaDoc** for new classes/methods
- Use **descriptive names** for variables and methods

### Commit Structure

```
feat:    New feature
fix:     Bug fix
docs:    Documentation
refactor: Code refactoring
test:    Add tests
chore:   Maintenance tasks
```

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- [Skript](https://github.com/SkriptLang/Skript) - For creating an amazing language
- [Paper](https://papermc.io/) - For the modern API
- [Jackson](https://github.com/FasterXML/jackson) - For the JSON/YAML parsing library

---

<div align="center">

Made with ❤️ by [MaiconJH](https://github.com/MaiconJH)

</div>
