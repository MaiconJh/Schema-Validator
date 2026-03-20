# Contributing to Schema-Validator

Thank you for your interest in contributing to Schema-Validator!

---

## Code of Conduct

By participating in this project, you are expected to uphold our [Code of Conduct](CODE_OF_CONDUCT.md).

---

## How to Contribute

### Reporting Bugs

1. **Search existing issues** to avoid duplicates
2. **Use the bug report template** when opening a new issue
3. **Include detailed information**:
   - Server version (Paper/Spigot)
   - Skript version
   - Steps to reproduce
   - Any relevant error logs

### Suggesting Features

1. Open a discussion first to gauge interest
2. Use the feature request template
3. Explain the use case and potential benefits

### Pull Requests

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/my-feature`
3. **Make your changes** following the code style
4. **Write tests** for new functionality
5. **Update documentation** if needed
6. **Submit a Pull Request**

---

## Development Setup

### Prerequisites

- Java 17+
- Gradle 8.x
- Git
- A local Minecraft test server (Paper/Spigot 1.19+)

### Building the Project

```bash
# Clone the repository
git clone https://github.com/MaiconJh/Schema-Validator.git
cd Schema-Validator

# Build the project
./gradlew build

# The JAR will be in build/libs/
```

### Running Tests

```bash
./gradlew test
```

---

## Code Style

- Follow existing Java conventions
- Add Javadoc to new public methods
- Keep methods focused and small
- Use meaningful variable names

### Java Conventions

```java
// Good
public ValidationResult validate(Object data, Schema schema) {
    return dispatcher.dispatch(schema, data);
}

// Avoid
public Object v(Object d, Schema s) {
    return d.dispatch(s);
}
```

---

## Documentation

When contributing, please update relevant documentation:

- **API changes** → Update `docs/api-reference.md`
- **New keywords** → Update `docs/reference/json-schema.md`
- **New formats** → Update `docs/minecraft-formats.md`
- **New features** → Update `docs/CHANGELOG.md`

---

## Commit Messages

Use clear, descriptive commit messages:

```
feat: add minecraft-item format validation
fix: resolve integer type validation for number schemas
docs: update quickstart with new examples
```

---

## License

By contributing to Schema-Validator, you agree that your contributions will be licensed under the [MIT License](LICENSE).

---

## Questions?

- Open an issue for bugs or feature requests
- For general questions, start a discussion

Thank you for helping improve Schema-Validator!
