# blog-common

Shared library module. Not a Spring Boot application (`bootJar` disabled). Depended on by `blog-api`, `blog-batch`, `blog-worker`.

## Package structure

```
domain/
  <domain-name>/
    <entity, projection, pure domain logic, repository interface>
    infrastructure/
      <JpaRepository, RepositoryImpl>
infrastructure/
  <non-domain shared infrastructure, e.g. email>
global/
  config/   shared Spring configuration (JPA, R2/S3, mail properties)
  error/    ErrorCode, ContentriaException
  aop/      cross-cutting aspects (@ApiLog)
  model/    technical base types used across domains (BaseEntity)
```

### `domain/<domain-name>/`

A domain lives here only if 2+ modules need it. Currently: `analytics` (`VisitLog`, `DailyStatistics`) — used by `blog-api` for read/display and `blog-batch` for aggregation.

- Top level of the domain folder: entity, projection, repository interface, pure domain logic (no framework annotations except JPA on entities).
- `infrastructure/` subfolder: Spring Data JPA interface and repository implementation (`@Repository`).

This mirrors blog-api's per-domain layering (`domain/` vs `infrastructure/`), scoped down since blog-common domains have no `controller/` or `application/` layer — there's no HTTP surface and no cross-domain orchestration here, that's blog-api's job.

Don't add a domain here just because it's convenient. It needs an actual second consumer module.

### `infrastructure/`

Shared infrastructure that isn't tied to a specific domain entity. Currently: `email` (`EmailService`, Thymeleaf-based HTML mail).

### `global/`

Cross-cutting technical concerns with no domain meaning of their own: Spring configuration, error types, AOP, and shared base classes like `BaseEntity` (JPA auditing). If it's a framework/technical concept rather than a business concept, it goes here, not under `domain/`.
