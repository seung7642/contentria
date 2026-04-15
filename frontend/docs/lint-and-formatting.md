# Frontend Lint & Formatting Rules

This document describes the lint and formatting tooling applied to the
`frontend/` workspace, the individual rule sets that are enabled, and the
conventions contributors are expected to follow when writing code.

## 1. Tooling Overview

| Concern              | Tool                                                  | Config file                     |
| -------------------- | ----------------------------------------------------- | ------------------------------- |
| Static analysis      | ESLint 9 (flat config)                                | `frontend/eslint.config.js`     |
| Code formatting      | Prettier 3 + `prettier-plugin-tailwindcss`            | `frontend/.prettierrc`          |
| Editor normalization | EditorConfig                                          | `frontend/.editorconfig`        |
| TypeScript           | `typescript` + `@typescript-eslint/*`                 | `frontend/tsconfig.json`        |

Run locally:

```bash
npm run lint         # ESLint (reports errors + warnings)
npx prettier --check .   # Verify formatting
npx prettier --write .   # Apply formatting
```

Prettier is wired into ESLint through `eslint-plugin-prettier/recommended`,
so any formatting violation is surfaced as an ESLint error and fails
`npm run lint`.

## 2. ESLint Configuration

The flat config in `eslint.config.js` composes several rule sets, applied
as independent config blocks:

### 2.1 Ignored paths

Lint is skipped for generated or vendor-owned files:

```
tailwind.config.ts
next.config.ts
node_modules/**
.next/**
out/**
public/**
```

### 2.2 React rule set

- `eslint-plugin-react` with the `jsx-runtime` preset — compatible with the
  automatic JSX runtime, so `import React from 'react'` is not required.
- `settings.react.version = 'detect'` — the installed React version is
  inferred automatically.

### 2.3 React Hooks rule set

- `eslint-plugin-react-hooks` with its `recommended` rules — enforces the
  Rules of Hooks and the exhaustive-deps check.

### 2.4 Next.js rule set

- `@next/eslint-plugin-next` with both `recommended` and `core-web-vitals`
  presets — catches Next-specific anti-patterns (e.g. missing `next/image`
  use, `<head>` misuse) and Core Web Vitals regressions.

### 2.5 TypeScript rule set

Applied to `**/*.{ts,tsx}`:

- Parser: `@typescript-eslint/parser` with `project: './tsconfig.json'`
  (type-aware linting).
- Rules: `@typescript-eslint/eslint-recommended` + `recommended`.
- `@typescript-eslint/no-unused-vars` is tightened with an
  underscore-prefix ignore pattern so positional arguments and
  destructured slots that are intentionally discarded can be written as
  `_`, `_err`, etc.:

  ```js
  '@typescript-eslint/no-unused-vars': [
    'error',
    {
      argsIgnorePattern: '^_',
      varsIgnorePattern: '^_',
      destructuredArrayIgnorePattern: '^_',
      caughtErrorsIgnorePattern: '^_',
    },
  ],
  ```

### 2.6 Prettier integration

`eslint-plugin-prettier/recommended` is applied last. It:

1. Disables any stylistic ESLint rule that would conflict with Prettier
   (via `eslint-config-prettier`).
2. Runs Prettier as an ESLint rule, so formatting issues become lint
   errors.

### 2.7 Declaration files

For `**/*.d.ts`, `@typescript-eslint/no-unused-vars` is disabled because
ambient declarations legitimately export types that are not consumed in
the same file.

## 3. Prettier Configuration

`.prettierrc` captures the formatting contract:

| Option          | Value                            | Notes                                        |
| --------------- | -------------------------------- | -------------------------------------------- |
| `semi`          | `true`                           | Always emit trailing semicolons.             |
| `singleQuote`   | `true`                           | Single quotes for strings, double for JSX.   |
| `trailingComma` | `es5`                            | Trailing commas wherever ES5 allows them.    |
| `printWidth`    | `100`                            | Wrap at 100 columns.                         |
| `tabWidth`      | `2`                              | Two-space indentation.                       |
| `useTabs`       | `false`                          | Soft tabs only.                              |
| `endOfLine`     | `lf`                             | Unix line endings across every OS.           |
| `plugins`       | `['prettier-plugin-tailwindcss']` | Auto-sort Tailwind class names in JSX.       |

`prettier-plugin-tailwindcss` reorders Tailwind utility classes to match
the official recommended order, removing noise from reviews that would
otherwise chase class-order churn.

## 4. EditorConfig

`.editorconfig` mirrors the Prettier settings so editors that do not load
the Prettier config still produce compatible output:

```ini
root = true

[*]
charset = utf-8
insert_final_newline = true
trim_trailing_whitespace = true
end_of_line = lf
indent_style = space
indent_size = 2
```

## 5. Contributor Conventions

These follow from the rules above and are enforced by `npm run lint`:

- **Quotes**: single quotes for TypeScript, JSX attributes keep their
  default double quotes.
- **Semicolons**: always terminate statements.
- **Line length**: keep lines at or under 100 columns; let Prettier wrap
  for you.
- **Imports**: remove unused imports. Do not silence the rule with
  disable comments.
- **Intentionally unused bindings**: prefix with `_`
  (e.g. `array.map((_, index) => ...)`, `catch (_err) { ... }`).
- **`any` types**: avoid. Use `unknown` at boundaries and narrow with
  type guards; `@typescript-eslint/no-explicit-any` blocks fresh `any`s.
- **React imports**: no `import React from 'react'` — the JSX runtime is
  automatic.
- **Images**: prefer `next/image`; `<img>` triggers a Next.js warning.
- **Tailwind classes**: do not hand-sort. Prettier owns class order.
- **Line endings**: never commit CRLF; the editor + Prettier + ESLint all
  enforce LF.

## 6. CI Expectations

`npm run lint` must pass before a PR is merged. Prettier violations
appear under the `prettier/prettier` rule in ESLint output. Auto-fixable
issues can be resolved with:

```bash
npm run lint -- --fix
npx prettier --write .
```
