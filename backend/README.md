# Contentria Backend

A multi-user blog platform backend built with **Kotlin**, **Spring Boot 4.0**, and **Gradle** multi-module architecture.

## Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Kotlin |
| Framework | Spring Boot 4.0, Spring Security, Spring Batch |
| Database | PostgreSQL, JPA/Hibernate |
| Auth | JWT (Access Token) + Opaque Refresh Token + OAuth2 (Google OIDC) |
| Caching | Caffeine (JCache) |
| Rate Limiting | Bucket4j |
| Email | Mailgun (SMTP) + Thymeleaf templates |
| Build | Gradle (Kotlin DSL), multi-module |

## Module Structure

```
backend/
├── blog-api/          # Main API server
├── blog-batch/        # Spring Batch jobs (scheduled statistics)
├── blog-common/       # Shared domain, config, error codes
├── buildSrc/          # Gradle build conventions
└── infrastructure/    # Infrastructure config (reserved)
```

### blog-api

The primary REST API module. Contains all business domains and serves HTTP requests.

Database schema files are located at `blog-api/src/main/resources/db/`:
- `schema.sql` — Full DDL (tables, indexes, constraints). Serves as the source of truth for the current database structure. Not auto-executed by Spring Boot (only `resources/schema.sql` is).
- `data.sql` — Seed data (e.g., default roles).

> **Note**: The project uses `ddl-auto: validate`, so Hibernate only validates that entities match the existing schema. All schema changes must be applied manually via these SQL files.

### blog-batch

Runs scheduled batch jobs. Currently contains:
- **DailyStatisticsJob**: Aggregates visit logs into daily PV/UV statistics per blog and post. Runs daily via `BatchScheduler`.

### blog-common

Shared code across modules:
- `BaseEntity` (JPA auditing: createdAt, updatedAt)
- `ErrorCode` enum and `ContentriaException`
- Analytics domain (VisitLog, DailyStatistics)
- Email service (Thymeleaf-based HTML emails)
- JPA config (UUIDv7 generator)
- AOP logging (`@ApiLog`)

## Architecture

### Layered Architecture with DDD Influence

Each domain in `blog-api` follows a consistent 4-layer structure:

```
domain-name/
├── controller/        # REST endpoints, request/response DTOs
│   └── dto/           # Request/Response objects (API boundary)
├── application/       # Business logic orchestration
│   └── dto/           # Command/Info objects (service boundary)
├── domain/            # Entities, value objects, repository interfaces
│   └── query/         # Read-model projections
└── infrastructure/    # Repository implementations, external service clients
```

**Key principles:**
- **Controller** handles HTTP concerns only (validation, cookie setting, response mapping)
- **Application** contains business logic. No direct JPA or HTTP dependencies
- **Domain** defines entities and repository interfaces. No framework annotations except JPA
- **Infrastructure** implements repository interfaces and external integrations

### Facade Pattern

Cross-domain orchestration uses the **Facade** pattern to coordinate multiple services within a single transaction:

```
PostFacade
├── BlogService.validateBlogOwner()
├── CategoryService.validateCategoryBelongsToBlog()
├── MarkdownService.extractSummary()
└── PostInternalService.createPost()
```

**Why Facade?**
- **Prevents circular dependencies** between bounded contexts. Services only know their own domain; cross-domain orchestration is lifted to the Facade layer. Without this, `PostService` ↔ `CategoryService` mutual dependencies become inevitable.
- **Single transaction boundary** for operations spanning multiple domains.

**Conventions:**
- `*Facade` classes own the `@Transactional` boundary for write operations
- `*Service` classes contain single-domain logic and **must return DTOs only** (never entities)
- `*InternalService` classes handle domain-internal write operations (e.g., slug generation + save). May return entities, but **only the Facade within the same bounded context** may depend on them
- Facades may depend on **services (`*Service`) from other domains**; direct dependency on another domain's Facade or InternalService is not allowed. This prevents circular dependencies
- Utility classes (e.g., `MarkdownService`) are allowed in the `application/` layer, but **only within the same bounded context**. They must not be depended on by other domains

### Authentication Flow

```
Client Request
  │
  ├── JWT in Authorization header or accessToken cookie
  │     └── JwtAuthenticationFilter validates and sets SecurityContext
  │
  ├── Email/Password Login
  │     └── AuthController → AuthFacade → CredentialService.authenticate()
  │         → generates JWT access token + opaque refresh token
  │
  ├── Google OAuth2 (OIDC)
  │     └── Spring Security OAuth2 → CustomOidcAuthenticationSuccessHandler
  │         → AuthFacade.loginWithSocial() → redirect to frontend
  │
  └── Token Refresh
        └── AuthController.refreshToken() → validates opaque token in DB
            → rotates refresh token (RTR) → issues new access + refresh tokens
```

**Token strategy:**
- Access token: JWT, 15-minute expiry, stateless validation
- Refresh token: Opaque UUID, 7-day expiry, stored in DB, rotated on each use (RTR)

## Business Domains

| Domain | Description |
|--------|-------------|
| `auth` | Authentication (email/password, OAuth2, OTP verification, reCAPTCHA) |
| `user` | User management (profile, roles, nickname generation) |
| `blog` | Blog CRUD (slug validation, sample content creation) |
| `post` | Post CRUD (markdown content, slug generation, draft/published status) |
| `category` | Category management (hierarchical, drag-and-drop sync, max 2 levels) |
| `analytics` | Visit logging, daily statistics aggregation |

## Developer Conventions

See the `docs/` directory for detailed guidelines:

- [Logging Conventions](docs/logging-conventions.md) - Log levels, PII rules, message format
- [Security Conventions](docs/security-conventions.md) - Token handling, cookie security, auth rules

## Build & Run

```bash
# Run API server
./gradlew :blog-api:bootRun

# Run batch
./gradlew :blog-batch:bootRun

# Build all modules
./gradlew build
```

## Docker Build & Deploy

Build and deploy Docker images to a remote server:

```bash
# 1. Navigate to backend root
cd backend/

# 2. Build Docker image
docker build -t contentria/blog-api:1.0 -f blog-api/Dockerfile .

# 3. Save image to tar
docker save -o blog-api.tar contentria/blog-api:1.0

# 4. Transfer to remote server
scp blog-api.tar <username>@<remote-host>:~
```

> The same steps apply to `blog-batch`.

On the remote server:

```bash
# 1. SSH into the server
ssh <username>@<remote-host>

# 2. Load the Docker image
sudo ctr -n k8s.io images import blog-api.tar

# 3. Verify
sudo crictl images
```
