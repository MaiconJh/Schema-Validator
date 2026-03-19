## 🚨 Warning

**This is a pre-release version (v0.1.0).** This is an early release and may contain bugs, incomplete features, or unexpected behavior. **Not recommended for production use.** Please test thoroughly in a development environment before using on a live server.

---

## ✨ Features

- **YAML & JSON Validation** — Validate configuration files in both YAML and JSON formats against defined schemas
- **Schema-Based Validation** — Use JSON Schema-like structure to define expected data formats
- **Complex Schema Support** — Nested objects, arrays, enums, required fields, and patternProperties
- **Detailed Error Reporting** — Get clear error messages including: field path, expected type, actual value received
- **Native Skript Integration** — Simple, easy-to-use Skript syntax for all validation operations
- **Schema References** — Support for `$ref` to reuse schema definitions across multiple files
- **Caching System** — Built-in cache to improve performance for repeated validations

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
