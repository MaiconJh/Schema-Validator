# Schema-Validator v1.0.5


## Online documentation

- Published docs: <https://maiconjh.github.io/Schema-Validator/>
- Source index page: [docs/pages/index.md](docs/pages/index.md)
- Release notes: [RELEASE_NOTES.md](RELEASE_NOTES.md)

[![SkriptHubViewTheDocs](http://skripthub.net/static/addon/ViewTheDocsButton.png)](http://skripthub.net/docs/?addon=Schema-Validator)


Schema-Validator is a Paper/Skript plugin that validates JSON or YAML data files against JSON Schema files and ships with a full documentation site for users, schema authors, and maintainers.

> If documentation and code diverge, the code is authoritative.

## Project at a glance

- Paper plugin for validating JSON and YAML data against schemas
- Skript integration for running validations and retrieving compact errors
- JSON Schema support across object, array, primitive, logical, conditional, reference, and format keywords
- Jekyll documentation site in `docs/pages/`
- Interactive documentation features: search, dark mode, table of contents, breadcrumbs, page rating, privacy policy, and AI assistant chat
- Cloudflare-backed services for documentation feedback and the AI assistant

## Core plugin functionality

Schema-Validator is focused on operational validation workflows inside a Paper server with Skript.

### Validation workflow

At runtime, the main flow is:

1. A schema is loaded and parsed by `FileSchemaLoader`
2. Data is loaded by `DataFileLoader`
3. Validation is dispatched by schema type
4. Errors are stored in `SkriptValidationBridge`
5. Skript reads the last validation result through `last schema validation errors`

### Skript API

Registered syntax:

```text
validate yaml %string% using schema %string%
validate json %string% using schema %string%
last schema validation errors
```

### Configuration features

The plugin supports these runtime controls through `plugins/Schema-Validator/config.yml`:

- `schema-directory`
- `auto-load`
- `cache-enabled`
- `validation-on-load`
- `strict-mode`

### Supported validation capabilities

The implementation covers the major JSON Schema groups documented in the site:

- Object keywords such as `properties`, `patternProperties`, `required`, `additionalProperties`, `minProperties`, `maxProperties`, `dependencies`, `dependentRequired`, `dependentSchemas`
- Array keywords such as `items`, `prefixItems`, `additionalItems`, `minItems`, `maxItems`, `uniqueItems`, `contains`, `minContains`, `maxContains`, `unevaluatedItems`
- Primitive and numeric keywords such as `type`, `enum`, `const`, `minimum`, `maximum`, `exclusiveMinimum`, `exclusiveMaximum`, `multipleOf`, `minLength`, `maxLength`, `pattern`
- Logical and conditional keywords such as `allOf`, `anyOf`, `oneOf`, `not`, `if`, `then`, `else`
- Reference and metadata keywords such as `$ref`, `$dynamicRef`, `definitions`, `$defs`, `$id`, `$schema`, `title`, `description`
- Format validation with standard and Minecraft-specific formats

## Documentation site functionality

The documentation is not just a page collection. It is a structured product with navigation, discovery, feedback, and AI-assisted help.

### Information architecture

The site follows Diataxis and separates content into:

- Tutorials
- How-to guides
- Reference
- Explanation

This structure is introduced in [docs/pages/index.md](docs/pages/index.md) and reinforced by the sidebar, breadcrumbs, previous/next navigation, and per-page table of contents.

### Navigation and reading experience

The documentation UI includes:

- Responsive sidebar navigation grouped by document type
- Breadcrumb trail for current page context
- Previous and next page arrows
- On-page table of contents
- Version banner support when the reader is not on the latest docs version
- Responsive mobile menu and mobile search toggle
- Light and dark theme toggle

Relevant implementation files:

- [docs/pages/_layouts/default.html](docs/pages/_layouts/default.html)
- [docs/pages/_includes/sidebar.html](docs/pages/_includes/sidebar.html)
- [docs/pages/_includes/breadcrumbs.html](docs/pages/_includes/breadcrumbs.html)
- [docs/pages/_includes/nav-arrows.html](docs/pages/_includes/nav-arrows.html)
- [docs/pages/_includes/version-banner.html](docs/pages/_includes/version-banner.html)
- [docs/pages/assets/js/modern-docs.js](docs/pages/assets/js/modern-docs.js)
- [docs/pages/assets/css/modern-docs.css](docs/pages/assets/css/modern-docs.css)

### Search

The site provides client-side search backed by [docs/pages/search.json](docs/pages/search.json), with fallback to sidebar navigation links if the search index is unavailable.

Search behavior includes:

- Ranked client-side results
- Highlighted snippets
- Keyboard navigation
- Mobile-friendly search toggle

### Feedback and privacy

Each documentation page includes a feedback widget with:

- 1 to 5 star page rating
- Current average rating and rating count
- Privacy-policy link beside the rating component

The feedback backend is configured through a Cloudflare Worker URL in [docs/pages/_config.yml](docs/pages/_config.yml) and rendered from [docs/pages/_includes/help-support.html](docs/pages/_includes/help-support.html).

### AI Assistant chat

The documentation site includes an AI assistant dedicated to the Schema-Validator docs.

Current chat functionality includes:

- Floating assistant widget available across documentation pages
- Scope restriction to Schema-Validator documentation only
- Grounding using current page context, visible sections, breadcrumbs, `search.json`, and retrieved documentation matches
- `@mention` support for page sections and visible headings
- Continuity across page navigation in the same browser tab
- Same-tab opening for internal documentation references so context is preserved
- Code block rendering with language-aware styling
- Copy and in-place edit support for generated code blocks
- Start-new-chat control to clear current tab conversation
- Privacy-policy link directly in the chat header
- Abuse protection with rate limits, cooldowns, temporary blocks, and optional Turnstile verification

Relevant files:

- [docs/pages/_includes/ai-assistant.html](docs/pages/_includes/ai-assistant.html)
- [docs/pages/assets/js/ai-assistant.js](docs/pages/assets/js/ai-assistant.js)
- [docs/pages/assets/css/ai-assistant.css](docs/pages/assets/css/ai-assistant.css)
- [docs/pages/privacy-policy.md](docs/pages/privacy-policy.md)
- [.cloudflare/chat-assistant/worker.js](.cloudflare/chat-assistant/worker.js)

## Documentation map

The documentation source currently covers these pages and workflows.

### Tutorials

- [Getting started](docs/pages/getting-started.md): mental model for schema loading, validation dispatch, and Skript exposure
- [Quickstart](docs/pages/quickstart.md): one successful and one failing validation
- [First validation workflow](docs/pages/first-validation.md): intentionally fail a validation and interpret the errors
- [Examples](docs/pages/examples.md): practical examples for common validation tasks

### How-to guides

- [Installation](docs/pages/installation.md): build, deploy, and verify the plugin on Paper with Skript
- [Configuration](docs/pages/configuration.md): schema loading, cache behavior, and strictness controls
- [Validate JSON file](docs/pages/validate-json-file.md): JSON-specific execution path
- [Schema directory workflow](docs/pages/schema-directory-workflow.md): startup autoload and schema registration workflow

### Reference

- [Schema keywords](docs/pages/schema-keywords.md): parsed and enforced keyword behavior
- [Validation behavior](docs/pages/validation-behavior.md): dispatch order, evaluation order, and error model
- [Skript API](docs/pages/skript-api.md): registered syntax and runtime semantics
- [Format reference](docs/pages/format-reference.md): supported formats and examples
- [Config reference](docs/pages/config-reference.md): canonical config table
- [Examples and schema construction](docs/pages/examples-and-schema-construction.md): reference patterns for building schemas

### Explanation

- [Overview](docs/pages/index.md): documentation model and reading paths
- [Architecture](docs/pages/architecture.md): components, boundaries, and runtime flow
- [Design constraints](docs/pages/design-constraints.md): known runtime constraints and tradeoffs

### Documentation governance and maintenance

- [Pages architecture](docs/pages/PAGES_ARCHITECTURE.md): structure of the documentation site itself
- [Writing guide](docs/pages/WRITING_GUIDE.md): standards for writing and maintaining pages
- [Developer guide](docs/pages/dev-guide.md): contributor workflow inside `docs/pages`
- [Privacy Policy](docs/pages/privacy-policy.md): data handling for ratings and AI assistant interactions

## Quality and testing

The project currently documents and ships:

- 373 passing unit tests
- 23 test classes
- Full coverage on implemented validators

Run the main build and test workflow with:

```powershell
.\gradlew.bat test
.\gradlew.bat build
```

## Repository structure

```text
Schema-Validator/
|-- src/main/java/               Plugin source
|-- src/main/resources/          Plugin resources and plugin.yml
|-- src/test/java/               Unit tests
|-- docs/pages/                  Documentation source site
|-- .cloudflare/                 Workers for docs feedback and chat
|-- README.md
|-- RELEASE_NOTES.md
|-- build.gradle
`-- settings.gradle
```
## License

This project is licensed under the [MIT License](LICENSE).
