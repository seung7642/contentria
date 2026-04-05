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
1. **Claude (You):** Create a branch (`feature/*`, `fix/*`, etc.) -> Implement changes -> Submit a Pull Request (PR) to `main`.
2. **GitHub Actions:** Triggered automatically upon PR submission.
3. **Gemini (Reviewer):** Reviews your PR based on `GEMINI.md`.
4. **Outcome:** 
   - If Gemini approves (LGTM), the PR may be merged automatically or by the user.
   - If Gemini requests changes, you must address the feedback and push updates.

## 4. Technical Standards
- **Backend (Kotlin/Spring):** Follow Kotlin coding conventions. Ensure proper error handling and use of `blog-common` modules.
- **Frontend (Next.js/TS):** Use functional components, hooks, and Tailwind CSS. Ensure strict TypeScript typing.
- **Infrastructure (K8s):** Keep manifests in `infrastructure/kubernetes/` updated.

## 5. Pull Request Guidelines
- Provide a clear summary of changes.
- List any breaking changes.
- Ensure the base branch is always `main`.
