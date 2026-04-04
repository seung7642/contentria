# Contentria

A multi-user blog platform where anyone can create their own blog, write posts in Markdown, and manage content through a dashboard.

## Features

- **Blog creation** with custom slug (`/@your-slug`)
- **Markdown editor** (MDXEditor) with live preview, code blocks, tables, and admonitions
- **Category management** with drag-and-drop ordering (max 2 levels)
- **Authentication**: Email/Password, Email OTP, Google OAuth2 (OIDC)
- **reCAPTCHA** (v2 + v3) for bot protection
- **Dashboard** with traffic analytics, post management, and profile settings
- **Visitor analytics** with daily PV/UV statistics (batch aggregation)
- **SEO**: Dynamic metadata, Open Graph, slug-based URLs

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | Next.js 15 (App Router), React 19, TypeScript, Tailwind CSS |
| Backend | Kotlin, Spring Boot 4.0, Spring Security, Spring Batch |
| Database | PostgreSQL, JPA/Hibernate |
| Auth | JWT + Opaque Refresh Token (RTR), OAuth2/OIDC |
| State | Zustand (client), TanStack Query (server state) |
| Infra | Docker, Kubernetes (containerd) |

## Project Structure

```
contentria/
├── backend/           # Kotlin + Spring Boot (Gradle multi-module)
│   ├── blog-api/      # REST API server
│   ├── blog-batch/    # Scheduled batch jobs (daily statistics)
│   ├── blog-common/   # Shared domain, config, error codes
│   └── docs/          # Backend developer conventions
├── frontend/          # Next.js 15 + React 19
│   └── src/
│       ├── app/       # App Router pages (home, auth, dashboard, blog)
│       ├── actions/   # Server Actions (API calls)
│       ├── components/ # UI components
│       ├── hooks/     # React Query hooks, auth flows
│       ├── lib/       # API clients, utilities
│       └── store/     # Zustand stores
└── docs/              # General reference notes
```

## Getting Started

### Prerequisites

- Java 21+
- Node.js 20+
- PostgreSQL 16+

### Backend

```bash
cd backend
./gradlew :blog-api:bootRun
```

See [backend/README.md](backend/README.md) for architecture details, module structure, and deployment instructions.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend runs on `http://localhost:3000` and proxies API requests to `http://localhost:8080`.

## Architecture Overview

```
┌─────────────┐       ┌──────────────┐       ┌────────────┐
│   Browser    │──────>│   Next.js    │──────>│  Spring    │
│              │       │  (SSR + CSR) │       │  Boot API  │
│              │<──────│              │<──────│            │
└─────────────┘       └──────────────┘       └─────┬──────┘
                                                   │
                                             ┌─────┴──────┐
                                             │ PostgreSQL  │
                                             └────────────┘
```

- **Frontend** handles SSR for public pages (blog posts, SEO) and CSR for interactive pages (dashboard, editor)
- **Server Actions** call the backend API from Next.js server-side, managing auth cookies transparently
- **Backend** follows layered DDD with Facade pattern for cross-domain orchestration
- **Batch** runs daily to aggregate visit logs into statistics

## Documentation

| Document | Description |
|----------|-------------|
| [Backend README](backend/README.md) | Architecture, modules, DDD layers, auth flow, build & deploy |
| [Logging Conventions](backend/docs/logging-conventions.md) | Log levels, PII rules, message format |
| [Security Conventions](backend/docs/security-conventions.md) | Token strategy, cookie security, input validation |

## License

See [LICENSE](LICENSE) for details.
