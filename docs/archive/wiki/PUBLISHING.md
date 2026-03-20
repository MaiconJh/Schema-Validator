# Publishing to the GitHub Wiki

This repository's `wiki/` folder is a source bundle. GitHub wiki pages are stored in a **separate repository** (`<repo>.wiki.git`), so committing to the main code repo alone does not update the remote wiki pages.

## Steps

1. Clone the wiki repository:

```bash
git clone https://github.com/MaiconJh/Schema-Validator.wiki.git
```

2. Copy files from this repo's `wiki/` folder into the cloned wiki repository.
3. Commit and push in the wiki repository.

## Required pages

- `README.md`
- `Quickstart-and-Setup.md`
- `Schema-and-Validator-Reference.md`
- `Skript-Integration.md`
- `Troubleshooting-and-FAQ.md`
- `Audit-Summary-2026-03-19.md`
- `_Sidebar.md`

## Verification

After push, verify the wiki index at:

- <https://github.com/MaiconJh/Schema-Validator/wiki>

You should see `README` plus linked pages.
