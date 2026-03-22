---
title: Writing guide
description: Standards for creating and maintaining documentation pages in docs/pages.
nav_exclude: true
permalink: /writing-guide.html
---

## Purpose

This guide defines writing standards for published docs in `docs/pages`.

## Information architecture

Use Diataxis for every page:

- Tutorial: learning-oriented and sequential.
- How-to guide: task-oriented and outcome-driven.
- Reference: exact behavior, values, and interfaces.
- Explanation: architectural reasoning and tradeoffs.

Required front matter for navigable pages:

- `title`
- `description`
- `doc_type`
- `order`
- `sequence`
- `permalink`

## Procedure writing standard

For procedural pages:

1. State goal and prerequisites before commands.
2. Use explicit numbered steps.
3. Keep one primary action per step.
4. Include expected result after critical steps.
5. Prefer concrete examples over placeholders.

## Style baseline

- Use direct language and short sentences.
- Keep terminology stable across pages.
- Separate reference facts from explanation prose.
- Avoid duplicate pages for the same behavior contract.

## Callouts and code blocks

Use GitHub-style alert blocks:

```md
> [!NOTE]
> Useful context before a step.

> [!TIP]
> Performance recommendation or shortcut.
```

Supported callout types:

- `NOTE`
- `TIP`
- `IMPORTANT`
- `WARNING`
- `CAUTION`

Always set code fence language (`json`, `yaml`, `bash`, `powershell`, `java`, etc.).

## Maintenance policy

- `docs/pages/*.md` is the source of truth for published docs.
- Files outside `docs/pages/**` may exist as internal notes or compatibility pointers.
- If runtime behavior changes, update matching docs page(s) in the same PR.
- Keep permalinks stable unless a migration plan includes redirects or compatibility pointers.

## External guidance

- Diataxis framework: https://diataxis.fr/
- Write the Docs guide: https://www.writethedocs.org/guide/
- Google developer style procedures: https://developers.google.com/style/procedures
- Microsoft writing style guide: https://learn.microsoft.com/en-us/style-guide/welcome/
