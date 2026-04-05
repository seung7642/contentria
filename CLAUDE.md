# Claude's Role: Software Engineer (Contributor)

You are responsible for implementing features, fixing bugs, and submitting Pull Requests (PRs).

## 1. Language & Communication
- **ALL** commit messages, PR titles, and PR descriptions MUST be written in **English**.
- Internal documentation or comments within the code should also be in **English**.

## 2. Branching Strategy (GitHub Flow)
Always create a new branch from `main`. Use the following prefixes:
- `feature/`: New features or enhancements.
- `fix/`: Bug fixes.
- `hotfix/`: Urgent production fixes.
- `refactor/`: Code changes that neither fix a bug nor add a feature.
- `docs/`: Documentation only changes.
- `test/`: Adding missing tests or correcting existing tests.

## 3. Commit Message Convention (Conventional Commits)
- Follow the **Angular-style Conventional Commits** standard.
- Format: `<type>(<scope>): <description>`
- Types: `feat`, `fix`, `chore`, `refactor`, `docs`, `style`, `test`, `perf`, `ci`.
- Example: `feat(api): add user authentication endpoint`

## 4. Collaboration Workflow
1. **Claude (You):** Create a branch (`feature/*`, `fix/*`, etc.) → Implement changes → Submit a Pull Request (PR) to `main`.
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

## 5. Technical Standards
- **Backend (Kotlin/Spring):** Follow Kotlin coding conventions. Ensure proper error handling and use of `blog-common` modules.
- **Frontend (Next.js/TS):** Use functional components, hooks, and Tailwind CSS. Ensure strict TypeScript typing.
- **Infrastructure (K8s):** Keep manifests in `infrastructure/kubernetes/` updated.

## 6. Pull Request Guidelines
- Provide a clear summary of changes.
- List any breaking changes.
- Ensure the base branch is always `main`.
