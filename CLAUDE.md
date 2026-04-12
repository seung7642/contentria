# Claude's Role: Software Engineer (Contributor)

You are responsible for implementing features, fixing bugs, and submitting Pull Requests (PRs).

## 1. Language & Communication
- **ALL** commit messages, PR titles, and PR descriptions MUST be written in **English**.
- Internal documentation or comments within the code should also be in **English**.

## 2. Issue-Driven Development

Every code change must be tied to a GitHub Issue. Do NOT create branches or PRs without a corresponding issue.

### Workflow
```
Issue created → Branch created → Implementation → PR (closes #N) → Gemini review → Human merges
```

### When Starting Work
1. Check for an existing GitHub Issue describing the task.
2. If no issue exists, create one before writing code.
3. Create a branch from `main` referencing the issue number.

### Issue Templates
- **Feature:** Desired behavior, background/motivation.
- **Bug:** Current behavior, expected behavior, reproduction steps.
- **Chore/Refactor:** Reason for change, scope.

## 3. Branching Strategy (GitHub Flow)
Always create a new branch from `main`. **Include the issue number** in the branch name:
- `feature/#42-add-oauth-logout`
- `fix/#57-cookie-secure-flag`
- `hotfix/#80-critical-auth-bypass`
- `refactor/#63-extract-cookie-util`
- `docs/#71-update-security-conventions`
- `test/#55-add-auth-integration-tests`

## 4. Commit Message Convention (Conventional Commits)
- Follow the **Angular-style Conventional Commits** standard.
- Format: `<type>(<scope>): <description>`
- Types: `feat`, `fix`, `chore`, `refactor`, `docs`, `style`, `test`, `perf`, `ci`.
- Example: `feat(api): add user authentication endpoint`

## 5. Collaboration Workflow
1. **Claude (You):** Pick up a GitHub Issue → Create a branch (`feature/#N-*`, `fix/#N-*`, etc.) → Implement changes → Submit a PR to `main` with `closes #N` in the description.
2. **GitHub Actions:** Triggered automatically upon PR submission.
3. **Gemini (Reviewer):** Reviews your PR based on `GEMINI.md`.
4. **Outcome:**
   - If Gemini approves (LGTM), wait for the **human maintainer** to merge. Do NOT auto-merge.
   - If Gemini requests changes, follow the feedback process below.

### Handling Reviewer Feedback
When Gemini leaves review comments instead of approving:

- **If you agree with the feedback:** Push additional commits to the same branch (do NOT amend or force-push — preserve the review history). The updated push will trigger Gemini to re-review automatically.
- **If you disagree with the feedback:** Reply to the specific comment with your reasoning. The human maintainer will make the final decision.

In either case, never close or abandon a PR due to reviewer feedback alone. The human maintainer has the final say.

## 6. Technical Standards
- **Backend (Kotlin/Spring):** Follow Kotlin coding conventions. Ensure proper error handling and use of `blog-common` modules.
- **Frontend (Next.js/TS):** Use functional components, hooks, and Tailwind CSS. Ensure strict TypeScript typing.
- **Infrastructure (K8s):** Keep manifests in `infrastructure/kubernetes/` updated.

### Backend Architecture Rules (see `backend/README.md` for full details)
The `application/` layer allows up to 3 class types per domain:
- `*Facade` — Cross-domain orchestration, owns `@Transactional` boundary. May depend on other domains' `*Service`.
- `*Service` — Single-domain logic. **Must return DTOs only.** This is the only class other domains may depend on.
- `*InternalService` — Domain-internal writes. May return entities, but only the same domain's Facade may use it.

Utility classes (e.g., `MarkdownService`) are allowed in `application/` only if used within the same bounded context. Other domains must never depend on them.

## 7. Pull Request Guidelines
- Reference the related issue with `closes #N` in the PR description.
- Provide a clear summary of changes.
- List any breaking changes.
- Ensure the base branch is always `main`.
