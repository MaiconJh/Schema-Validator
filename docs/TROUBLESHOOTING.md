# Troubleshooting Guide

Common issues and their solutions for Schema-Validator plugin.

---

## Table of Contents

1. [General Issues](#general-issues)
2. [Schema Loading Problems](#schema-loading-problems)
3. [Validation Errors](#validation-errors)
4. [Skript Integration](#skript-integration)
5. [Performance Issues](#performance-issues)

---

## General Issues

### Plugin not loading

**Symptoms:** Plugin doesn't appear in `/pl` list or server console shows errors

**Solutions:**
1. Verify **Skript** is installed and loaded before Schema-Validator
2. Check Java version (requires Java 17+)
3. Ensure you're using a supported server: Paper 1.19+ or Spigot 1.19+
4. Check server console for specific error messages

### Configuration not found

**Symptoms:** "Configuration file not found" warnings

**Solutions:**
1. Restart server to generate default config
2. Verify `config.yml` exists in `plugins/Schema-Validator/`
3. Check file permissions on config directory

---

## Schema Loading Problems

### "Unsupported keyword detected" Warnings

**Symptoms:** Console shows warnings like:
```
[Schema-Validator] [my-schema] Unsupported keyword detected: 'custom-field'. This keyword will be ignored during validation.
```

**Solutions:**
1. **This is expected behavior** for custom properties in your schema
2. The schema still works correctly - custom keywords are just not validated
3. If you want to define custom properties, ensure they're in the `properties` or `patternProperties` section of your schema:
   ```json
   {
     "type": "object",
     "properties": {
       "custom-field": { "type": "string" }
     }
   }
   ```

### Schema validation fails on load

**Symptoms:** "Schema validation failed" during startup

**Solutions:**
1. Validate your schema file against JSON Schema spec
2. Check for syntax errors (missing quotes, commas, etc.)
3. Use an online JSON validator to check schema syntax
4. Enable debug mode in config to see detailed errors

### Schema not found

**Symptoms:** "Schema not found" when trying to validate

**Solutions:**
1. Verify schema file exists in `plugins/Schema-Validator/schemas/`
2. Check file extension is `.json` or `.yml`
3. Ensure auto-load is enabled in config
4. Use correct schema name (without file extension)

---

## Validation Errors

### "Type mismatch" errors

**Symptoms:** Validation fails with "expected {type} but got {type}"

**Solutions:**
1. Check the `type` field in your schema matches your data
2. Remember `number` accepts both integers and floats in recent updates
3. Use `integer` specifically for whole numbers

### Format validation failures

**Symptoms:** "Format validation failed" for valid-looking data

**Solutions:**
- **email:** Must be valid email format (user@domain.com)
- **uri:** Must be complete URI (https://example.com)
- **date-time:** Must be ISO 8601 format (2024-01-15T10:30:00Z)
- **ipv4/ipv6:** Must be valid IP address
- **minecraft-item/block:** Must use namespace format (minecraft:diamond, not just diamond)

### Conditional validation not working

**Symptoms:** `oneOf`, `if/then/else` validations not functioning

**Solutions:**
1. Ensure schema uses JSON Schema Draft-07 format
2. Check `oneOf` schemas are mutually exclusive
3. Verify `if` conditions use `const` or `enum` (not `type`)
4. Test each branch individually in `if/then/else`

---

## Skript Integration

### "Invalid syntax" errors in Skript

**Symptoms:** Skript fails to parse validation commands

**Solutions:**
1. Ensure Skript is fully loaded before running validation
2. Use correct syntax:
   ```
   validate yaml "file.yml" using schema "schema-name"
   validate json "file.json" using schema "schema-name"
   ```
3. Check schema name doesn't include file extension
4. Verify the `Schema-Validator` effect is registered (check onEnable log)

### Last validation errors empty

**Symptoms:** `last schema validation errors` returns empty even after failed validation

**Solutions:**
1. Store errors immediately after validation:
   ```skript
   validate yaml "file.yml" using schema "my-schema"
   set {_errors::*} to last schema validation errors
   ```
2. Errors are cleared on each new validation
3. Check validation actually failed (errors only exist if validation fails)

### Performance issues with large files

**Symptoms:** Server lags during validation of large YAML/JSON files

**Solutions:**
1. Enable caching in config.yml:
   ```yaml
   cache:
     enabled: true
     duration: 300
   ```
2. Use batch processing for multiple files
3. Consider validating on-change rather than on-read
4. Increase `batch-size` for bulk operations

---

## Performance Issues

### Slow schema loading

**Symptoms:** Server takes long time to start with many schemas

**Solutions:**
1. Disable `validate-on-load` in config if schemas are trusted
2. Enable caching for frequently used schemas
3. Consider splitting large schemas into smaller, modular ones

### High memory usage

**Symptoms:** Server memory spikes during validation

**Solutions:**
1. Limit concurrent validations
2. Use `cache-duration` to control cache size
3. Process large files in batches
4. Monitor with profiling tools

---

## Getting Help

If your issue isn't listed here:

1. Check the [FAQ](faq.md) page
2. Review [Example Files](../src/main/resources/examples/)
3. Open an issue on GitHub with:
   - Server version (Paper/Spigot)
   - Skript version
   - Full error message
   - Steps to reproduce

---

## Debug Mode

Enable detailed logging in `config.yml`:

```yaml
debug: true
```

This provides detailed information about:
- Schema parsing
- Validation steps
- Error details
- Cache operations

---

*Last updated: 2026-03-20*
