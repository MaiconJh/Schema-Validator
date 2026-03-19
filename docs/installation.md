# Installation

> Canonical behavior contract: [CONTRACT.md](CONTRACT.md).

Learn how to install the Schema Validator plugin on your Minecraft server.

## Requirements

| Requirement | Version |
|-------------|---------|
| Minecraft Server | Paper 1.21+ or Spigot 1.21+ |
| Skript | 2.9+ |
| Java | 21 or higher |

## Installation Steps

### Step 1: Build the Plugin

Clone the repository and build:

```bash
git clone https://github.com/your-repo/Schema-Validator.git
cd Schema-Validator
./gradlew build
```

The compiled JAR will be at `build/libs/Schema-Validator-0.1.0-SNAPSHOT.jar`

### Step 2: Install the Plugin

1. Copy the JAR file to `plugins/` on your server
2. Start or restart the server
3. The plugin will create the necessary folders

### Step 3: Install Skript (if not installed)

Download Skript from:
- [Skript Hub](https://skripthub.github.io/)
- [Github](https://github.com/SkriptLang/Skript)

Place it in `plugins/` and restart.

### Step 4: Verify Installation

Run this command:

```
/pl Schema-Validator
```

You should see the plugin in the list.

---

## Folder Structure

After installation, the plugin creates:

```
plugins/
└── Schema-Validator/
    ├── config.yml           # Plugin configuration
    └── schemas/             # Your schema files
        └── (created automatically if missing)
```

---

## Next Steps

1. [Quick Start](quickstart.md) - Learn the basics
2. [Configuration](configuration.md) - Configure the plugin
3. [FAQ](faq.md) - Common questions

---

[← Back](README.md) | [Next: Quick Start →](quickstart.md)
