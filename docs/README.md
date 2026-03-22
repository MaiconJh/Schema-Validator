# Repository Documentation

This documentation covers `docs/**` except `docs/pages/**`.

## Audit Scope

This set was rebuilt against practical guidance from Write the Docs:

- audience-first writing
- one page, one purpose
- scannable structure
- examples tied to real behavior
- docs maintained as code

## Intended Readers

- Operators who install and run the plugin.
- Contributors who need code-accurate behavior.

## Structure

- [Quickstart](quickstart.md)
- [Installation](installation.md)
- [Configuration](configuration.md)
- [Guides](guides/README.md)
- [Tutorials](tutorials/README.md)
- [Reference](reference/README.md)
- [Explanation](explanation/README.md)

## What Changed In This Audit

- Removed stale navigation text and encoding artifacts.
- Reduced duplicate content between guides, tutorials, and reference.
- Corrected claims to match `src/main/java/com/maiconjh/schemacr/**`.
- Added a mandatory metadata footer to every file in this doc tree.

## Source Of Truth

- Runtime behavior: `src/main/java/com/maiconjh/schemacr/**`
- Default config values: `src/main/resources/config.yml`
- User-facing published portal: `docs/pages/**`

---
Last updated: 2026-03-22  
Documentation version: 0.3.5
