---
title: Developer guide
description: Internal guide for adding and maintaining documentation pages in docs/pages.
nav_exclude: true
toc: true
permalink: /dev-guide.html
---

# Developer guide for docs/pages

## Goal

This page explains exactly how to add, edit, test, and publish documentation pages in the current Jekyll format used by `docs/pages`.

This is an internal authoring guide and is intentionally not indexed in the sidebar.

## Architecture summary

- Source of truth is Markdown in `docs/pages/*.md`.
- Shared UI comes from `_layouts` and `_includes`.
- Sidebar is generated from page metadata (`doc_type`, `order`).
- Back/Next buttons are generated from `sequence`.
- Pages are built by Jekyll and deployed by GitHub Actions.

## Required tools

1. Ruby + Jekyll installed.
2. A terminal that can run `jekyll`.
3. Access to this repository and push permission to `main`.

If `jekyll` is not available in your terminal session:

```powershell
$env:Path='C:\Ruby33-x64\bin;'+$env:Path
jekyll -v
```

## Add a new page (step by step)

### 1. Choose the page type

Pick one `doc_type`:

- `tutorial`
- `how-to`
- `reference`
- `explanation`

### 2. Create the file

Create `docs/pages/<slug>.md`.

Use this template:

```yaml
---
title: Page title
description: One sentence summary.
doc_type: reference
order: 3
sequence: 10
permalink: /your-page.html
---
```

Rules:

- `title`: short and clear.
- `description`: what the page solves.
- `doc_type`: controls sidebar group.
- `order`: controls order inside the group.
- `sequence`: controls Back/Next flow.
- `permalink`: public URL, keep `.html`.

### 3. Write the content

Use a single page purpose:

- Tutorial: learning path.
- How-to: task execution.
- Reference: exact behavior and values.
- Explanation: architecture and reasoning.

Formatting rules:

- Start sections with `##`.
- Use fenced blocks with language, for example `json`, `yaml`, `bash`, `skript`.
- Use callouts with GitHub alert syntax when needed:
  `> [!NOTE]`, `> [!TIP]`, `> [!IMPORTANT]`, `> [!WARNING]`, `> [!CAUTION]`.
- Keep links relative, for example `[Installation](installation.html)`.

### 4. Decide if the page should be indexed

For normal docs pages:

- keep `nav_exclude` unset (or `false`)
- include `doc_type`, `order`, `sequence`

For internal pages (like this one):

- set `nav_exclude: true`
- omit `doc_type`, `order`, `sequence` unless you want Back/Next

### 5. Build locally

```powershell
$env:Path='C:\Ruby33-x64\bin;'+$env:Path
jekyll build --source docs/pages --destination docs/pages/_site_test
```

### 6. Validate generated output

Open generated files in `docs/pages/_site_test/` and confirm:

1. No Liquid errors during build.
2. Sidebar placement is correct.
3. Back/Next appears and points to expected pages.
4. Code blocks show copy button.
5. URL matches `permalink`.

### 7. Commit and push

```powershell
git add docs/pages
git commit -m "docs: add <page-name>"
git push
```

Do not commit `docs/pages/_site_test/`.

## Edit an existing page

1. Update the target `.md` file.
2. If the title or purpose changed, review links in related pages.
3. If sequence changed, review Back/Next neighbors.
4. Build locally and verify.
5. Commit and push.

## Add a new feature to docs (recommended update order)

When a new plugin feature is added, update docs in this order:

1. Reference page first (`schema-keywords`, `validation-behavior`, or new reference page).
2. How-to page for operational usage.
3. Tutorial/examples page showing a practical scenario.
4. Architecture page only if internal flow changed.

## Version labels (docs vs plugin)

Version settings live in `docs/pages/_config.yml`:

- `docs_version`
- `latest_docs_version`
- `latest_docs_url`
- `plugin_version_range`

Behavior:

- Header badge uses `docs_version`.
- Article meta line uses `plugin_version_range`.
- Warning banner appears only if `docs_version != latest_docs_version`.

## Common mistakes and fixes

1. Sidebar entry missing:
   - check `doc_type`, `order`, and `nav_exclude`.
2. Back/Next missing:
   - check `sequence` exists and is unique.
3. Page opens but style/script is broken:
   - check `baseurl` and deployment path.
4. 404 on GitHub Pages:
   - check Actions run status and Jekyll build output.
5. Code block not copyable:
   - ensure fenced block syntax is valid and builds into `<pre><code>`.

## Files you will edit most often

- `docs/pages/*.md`
- `docs/pages/_config.yml`
- `docs/pages/_layouts/default.html`
- `docs/pages/_includes/sidebar.html`
- `docs/pages/_includes/nav-arrows.html`
- `docs/pages/assets/js/modern-docs.js`
- `docs/pages/assets/css/modern-docs.css`
