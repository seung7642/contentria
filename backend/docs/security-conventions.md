# Security Conventions

This document defines security practices for the Contentria backend. These rules apply to authentication, cookie handling, token management, and data protection.

## Authentication Architecture

### Token Strategy

| Token | Type | Storage | Expiry | Validation |
|-------|------|---------|--------|-----------|
| Access Token | JWT (signed) | HttpOnly cookie + Authorization header | 15 minutes | Stateless (signature check) |
| Refresh Token | Opaque UUID | HttpOnly cookie + DB | 7 days | Stateful (DB lookup) |

### Why Two Token Types?

- **JWT for access**: No DB lookup required per request. Fast validation via signature. Short-lived to limit damage if leaked.
- **Opaque for refresh**: Stored in DB, enabling server-side revocation. Can be rotated on each use (Refresh Token Rotation) to detect token theft.

### Refresh Token Rotation (RTR)

On every token refresh:
1. Validate the old refresh token against DB
2. Delete/update the old token
3. Issue a new refresh token
4. Return both new access + refresh tokens

If an attacker uses a stolen refresh token after the legitimate user has already rotated it, the token won't be found in DB, and the refresh fails. This limits the window of token theft.

## Cookie Security

### Required Attributes

Every authentication cookie **must** have:

| Attribute | Value | Why |
|-----------|-------|-----|
| `HttpOnly` | `true` | Prevents JavaScript access. Mitigates XSS-based token theft |
| `Secure` | `true` (in production) | Cookie only sent over HTTPS. Prevents interception over HTTP |
| `SameSite` | `Lax` or `Strict` | CSRF protection. Prevents cookies from being sent with cross-origin requests |
| `Path` | `/` or scoped | Limits which paths receive the cookie |
| `Max-Age` | Match token expiry | Cookie expires when the token does |

### Common Pitfalls

#### Conditional Secure Flag

```kotlin
// Dangerous - Secure flag depends on request
secure = request.isSecure

// Problem: Behind a reverse proxy (nginx, ALB), the backend sees HTTP,
// not HTTPS. Even with forward-headers-strategy: native, misconfigured
// proxies will result in cookies without the Secure flag.

// Safer - use environment-based configuration
secure = appProperties.auth.cookie.secure  // true in prod, false in dev
```

#### Missing SameSite

Without `SameSite`, browsers may default to `Lax` (modern browsers) or `None` (older browsers). Explicitly set it to avoid inconsistent behavior across browsers.

#### CSRF and SameSite Relationship

This project disables Spring Security's CSRF protection (`csrf.disable()`) because:
- The API is stateless (JWT-based), not session-based
- Cookies use `SameSite` attribute as the primary CSRF defense

**This means `SameSite` is the only CSRF protection**. If `SameSite` is missing, the API is vulnerable to CSRF attacks.

## Sensitive Data Handling

### Never Expose in Logs or Responses

See [Logging Conventions](logging-conventions.md) for the full list. Summary:

- Token values (JWT, refresh token, API keys)
- Passwords (raw or hashed)
- Email addresses (PII)
- Full request/response bodies

### Error Responses

Error messages returned to clients should be generic. Internal details (stack traces, SQL errors, internal service names) must never leak to the client.

```kotlin
// Good - generic message from ErrorCode enum
ErrorCode.INVALID_CREDENTIALS → "Invalid email or password."

// Bad - reveals internal implementation
"User not found in credentials table for email: john@example.com"
```

### OAuth2 Error Handling

When OAuth2 authentication fails, **do not pass the raw exception message to the frontend URL**:

```kotlin
// Dangerous - exception.message could contain anything
val url = "https://example.com/login?error=${exception.message}"

// Safe - use a predefined error code
val url = "https://example.com/login?error=oauth_failed"
```

Raw exception messages may contain internal details, and inserting them into URLs can enable open redirect or reflected XSS attacks.

## Input Validation

### Controller Layer

- Always use `@Valid` on `@RequestBody` parameters
- Define constraints on DTO fields (`@Email`, `@NotBlank`, `@Size`, etc.)
- Validate path variables and query parameters for expected formats

### Authorization Checks

Every write operation must verify ownership:

```kotlin
// Pattern: Facade validates ownership before delegating
blogService.validateBlogOwner(blogId, userId)  // throws if not owner
categoryService.validateCategoryBelongsToBlog(categoryId, blogId)
postInternalService.createPost(...)
```

### URL Path Variables vs Request Body

When both a path variable and request body contain the same ID (e.g., `postId`), **use the path variable** or verify they match. Otherwise, a client could send a different ID in the body to modify another user's resource:

```kotlin
// Vulnerable - path variable ignored, body's postId used
@PostMapping("/posts/{postId}")
fun updatePost(@PathVariable postId: UUID, @RequestBody request: UpdatePostRequest) {
    postFacade.updatePost(userId, request.toCommand())  // uses request.postId, not path postId
}

// Safe - explicitly use path variable or verify match
@PostMapping("/posts/{postId}")
fun updatePost(@PathVariable postId: UUID, @RequestBody request: UpdatePostRequest) {
    require(postId == request.postId) { "Path and body postId mismatch" }
    postFacade.updatePost(userId, request.toCommand())
}
```

## Password Handling

### Design Context

This project supports **two authentication paths**:

1. **Email + Password login**: User sets a password during signup and uses it to log in.
2. **Email OTP login**: User signs up without a password and authenticates via a one-time verification code sent to their email.

Because of this, `password` is **intentionally nullable** in both `LoginRequest` and `Credential`. A null password is not a bug — it means the user registered via OTP-only flow.

### Rules

- Always use `PasswordEncoder` (BCrypt) for hashing
- Never log raw or hashed passwords
- **Check for null before calling `passwordEncoder.matches()`** — passing null may throw or behave unpredictably depending on the encoder implementation. This also prevents OTP-only users (who have no password) from accidentally passing through password-based authentication.

```kotlin
// Safe: null password is rejected before reaching the encoder
fun authenticate(email: String, rawPassword: String?): Credential {
    val credential = credentialRepository.findByEmail(email)
        ?: throw ContentriaException(ErrorCode.INVALID_CREDENTIALS)

    if (rawPassword == null || !passwordEncoder.matches(rawPassword, credential.password)) {
        throw ContentriaException(ErrorCode.INVALID_CREDENTIALS)
    }
    return credential
}
```

## Rate Limiting

The API uses Bucket4j for rate limiting on authentication endpoints (`/api/auth/**`):
- 10 requests per minute per IP
- Only successful responses (HTTP 200) consume tokens

This protects against brute-force login attempts and OTP abuse.
