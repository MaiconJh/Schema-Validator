---
title: Writing guide
description: Standards for creating and maintaining documentation pages in docs/pages.
nav_exclude: true
permalink: /writing-guide.html
---

## Purpose

This guide defines the writing and structure standards for the Jekyll site in `docs/pages`.

## Information architecture

Use the Diataxis model for every page:

- Tutorial: learning-oriented and step-by-step.
- How-to guide: task-oriented and goal-driven.
- Reference: exact facts, rules, and interfaces.
- Explanation: conceptual and architectural reasoning.

Each page must set front matter fields:

- `title`
- `description`
- `doc_type`
- `order`
- `permalink`

## Procedure writing standard

For procedural pages:

1. State goal and prerequisites before steps.
2. Use explicit numbered steps.
3. Keep one action per step.
4. Put expected result after commands.
5. Prefer concrete examples over generic placeholders.

## Style baseline

- Use direct language and short sentences.
- Keep terminology consistent across pages.
- Avoid mixing conceptual explanation into reference sections.
- Prefer stable links and avoid duplicate pages for the same topic.

## Maintenance policy

- `docs/pages/*.md` is the source of truth for published docs pages.
- `_layouts/default.html` and `_includes/*` control shared UI and navigation.
- Sidebar grouping is generated from front matter metadata.

## External guidance

- Diataxis framework: https://diataxis.fr/
- Write the Docs guide: https://www.writethedocs.org/guide/
- Google developer style procedures: https://developers.google.com/style/procedures
- Microsoft writing style guide: https://learn.microsoft.com/en-us/style-guide/welcome/
