# 🚨 Warning

**This is a pre-release version (v0.3.1-SNAPSHOT).** This is an early release and may contain bugs, incomplete features, or unexpected behavior. **Not recommended for production use.** Please test thoroughly in a development environment before using on a live server.

---

# ✨ What's New in v0.3.1

## 🎯 Major Features Added

### 1. Validação Condicional (oneOf, not, if/then/else)

Suporte completo para keywords de validação condicional do JSON Schema:

- **oneOf** — O dado deve corresponder a exatamente UM dos schemas definidos
- **not** — O dado NÃO deve corresponder ao schema especificado
- **if/then/else** — Validação condicional: se a condição "if" for satisfeita, valida contra "then"; caso contrário, valida contra "else"

#### Exemplo de uso:

```json
{
  "oneOf": [
    { "properties": { "type": { "const": "weapon" } }, "required": ["damage"] },
    { "properties": { "type": { "const": "armor" } }, "required": ["defense"] }
  ],
  "not": { "properties": { "type": { "const": "banned" } } },
  "if": { "properties": { "rarity": { "const": "legendary" } } },
  "then": { "required": ["abilities"] }
}
```

### 2. Validação de Formatos (format)

Suporte para validação de formatos conforme JSON Schema:

| Formato | Descrição |
|---------|-----------|
| `email` | Endereços de email |
| `uri` | URIs completas |
| `uri-reference` | Referências URI (relativas ou absolutas) |
| `date-time` | Data/hora ISO 8601 |
| `date` | Data ISO 8601 (YYYY-MM-DD) |
| `time` | Hora ISO 8601 (HH:MM:SS) |
| `ipv4` | Endereços IPv4 |
| `ipv6` | Endereços IPv6 |
| `hostname` | Hostnames |
| `unix-time` | Timestamp Unix (segundos desde epoch) |
| `json-pointer` | JSON Pointer |
| `relative-json-pointer` | Relative JSON Pointer |

### 3. Validação multipleOf

Suporte para validação de múltiplos:

```json
{
  "type": "number",
  "multipleOf": 0.5
}
```

---

## 📦 Installation

1. Download the latest release from the **Assets** section below
2. Place the `.jar` file into your server's `/plugins` folder
3. Ensure **Skript** is already installed on your server
4. Restart the server

The plugin will create a `Schema-Validator/` folder in your plugins directory with:
- `schemas/` — Place your schema files here
- `config.yml` — Plugin settings

---

## 🧪 Basic Usage (Skript)

### Validate a YAML file

```skript
validate yaml "playerdata/myplayer.yml" using schema "schemas/player-profile.schema.json"
```

### Validate a JSON file

```skript
validate json "config/settings.json" using schema "schemas/settings.schema.json"
```

### Check for validation errors

```skript
set {_errors::*} to last schema validation errors

if size of {_errors::*} is 0:
    broadcast "✓ Validation passed!"
else:
    broadcast "✗ Validation failed:"
    loop {_errors::*}:
        broadcast "- %loop-value%"
```

---

## 🧪 Advanced Usage

### Validating with oneOf

```skript
set {_schema} to schema "my-schema" from "schemas/player.schema"
set {_player} to object from json "{""type"":""warrior"",""damage"":50}"

if validate {_player} against {_schema}:
    broadcast "Valid warrior!"
else:
    broadcast "Errors: %{last validation errors}%"
```

### Validating formats

```skript
set {_schema} to schema "user-data" from "schemas/user.schema"
set {_user} to object from json "{""email"":""player@example.com"",""ip"":""192.168.1.1""}"

if validate {_user} against {_schema}:
    broadcast "All formats valid!"
```

### Batch validation

```skript
set {_players::1} to object from json "{""name"":""Player1""}"
set {_players::2} to object from json "{""name"":""Player2""}"
set {_results::*} to validate {_players::*} against {_schema}
```

---

## 📁 New Example Files

### Schemas
- `schemas/conditional-validation.schema.json` — Demonstra oneOf, not, if/then/else
- `schemas/data-types-formats.schema.json` — Demonstra formatos e multipleOf
- `schemas/complex-item.schema.json` — Schema complexo combinando múltiplas features

### Data Examples (Valid)
- `conditional-valid-examples.yml` — Exemplos válidos de validação condicional
- `formats-valid-examples.yml` — Exemplos válidos com formatos
- `complex-item-valid.yml` — Exemplos válidos de itens Minecraft

### Data Examples (Invalid)
- `conditional-invalid-examples.yml` — Casos de erro para validação condicional
- `formats-invalid-examples.yml` — Casos de erro para formatos
- `complex-item-invalid.yml` — Casos de erro para schema de itens

### Skript Examples
- `validate-conditional-example.sk` — Script de exemplo para validação condicional
- `validate-formats-example.sk` — Script de exemplo para validação de formatos

---

## 🔧 Technical Changes

### Added Classes
- `OneOfValidator.java` — Validador para oneOf
- `NotValidator.java` — Validador para not
- `ConditionalValidator.java` — Validador para if/then/else
- `FormatValidator.java` — Validador de formatos

### Modified Files
- `Schema.java` — Adicionados campos e métodos para oneOf, not, if/then/else
- `FileSchemaLoader.java` — Adicionado parsing para novas keywords
- `ObjectValidator.java` — Adicionada lógica de validação condicional
- `PrimitiveValidator.java` — Adicionada validação de formatos

### API Changes
- Novas classes de validador integradas ao `ValidatorDispatcher`
- Todos os validadores implementam `setRefResolver()` para suporte a $ref

---

## 🐞 Bug Reports & Contributions

Found a bug or have a feature request? Please open an issue on the GitHub repository:

🔗 **GitHub Issues:** https://github.com/MaiconJh/Schema-Validator/issues

Your feedback and contributions are welcome! When reporting bugs, please include:
- Server version (Paper/Spigot)
- Skript version
- Steps to reproduce the issue
- Any relevant error logs

---

## 🙌 Credits

**Created by** — MaiconJH

**Powered by:**
- [Skript](https://github.com/SkriptLang/Skript) — Amazing scripting platform for Minecraft
- [Paper](https://papermc.io/) — Modern Minecraft server software
- [Jackson](https://github.com/FasterXML/jackson) — JSON/YAML parsing library

---

## 📄 License

This project is licensed under the **MIT License**.

---

*Thank you for trying Schema-Validator!*
