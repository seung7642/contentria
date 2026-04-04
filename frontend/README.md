# Contentria Frontend

A Next.js 15 application serving the Contentria blog platform with SSR for public content and CSR for interactive dashboard features.

## Tech Stack

| Category | Technology |
|----------|-----------|
| Framework | Next.js 15 (App Router, Turbopack) |
| Language | TypeScript, React 19 |
| Styling | Tailwind CSS 3, tailwindcss-animate |
| UI Components | shadcn/ui (Radix UI primitives) |
| Server State | TanStack Query v5 (React Query) |
| Client State | Zustand |
| Forms | React Hook Form + Zod validation |
| Editor | MDXEditor (Markdown WYSIWYG) |
| Charts | Recharts |
| Drag & Drop | dnd-kit |
| Auth | HttpOnly cookies, axios-auth-refresh (token rotation) |

## Project Structure

```
src/
├── app/                    # Next.js App Router
│   ├── (home)/             # Landing page, policy
│   ├── (auth)/             # Login, signup, OAuth callback
│   ├── dashboard/
│   │   ├── (main)/         # Dashboard, post list, categories, settings
│   │   └── (editor)/       # Markdown post editor (new / edit)
│   └── user/[blogSlug]/    # Public blog view (parallel route for sidebar)
│
├── actions/                # Server Actions (backend API calls)
│   ├── auth.ts             # Login, signup, OTP, logout
│   ├── post.ts             # Post CRUD with cache revalidation
│   ├── category.ts         # Category sync
│   ├── blog.ts             # Blog CRUD
│   ├── dashboard.ts        # Dashboard data aggregation
│   ├── analytics.ts        # Visit logging
│   └── user/               # User profile (with Zod schemas)
│
├── components/
│   ├── ui/                 # shadcn/ui base components
│   ├── common/             # Shared components (pagination, data table, input fields)
│   ├── auth/               # Auth flow components (login, signup, OTP, reCAPTCHA)
│   ├── blog/               # Public blog components (post card, sidebar, TOC)
│   ├── dashboard/          # Dashboard components (editor, categories, stats, settings)
│   ├── home/               # Landing page components (hero, header, footer)
│   └── analytics/          # Visit tracker
│
├── hooks/
│   ├── queries/            # TanStack Query hooks (keys, user, dashboard)
│   ├── mutations/          # Mutation hooks (auth, user)
│   ├── useLoginFlow.tsx    # Multi-step login state machine
│   ├── useSignUpFlow.tsx   # Multi-step signup state machine
│   └── useAuthRedirect.ts  # Post-auth redirect logic
│
├── lib/
│   ├── apiClient.ts        # Axios client (browser-side, token refresh interceptor)
│   ├── apiServer.ts        # Fetch wrapper (server-side, cookie-based auth)
│   ├── cookieManager.ts    # Client-side cookie helpers
│   ├── schemas/            # Zod validation schemas
│   └── utils.ts            # Tailwind cn() utility
│
├── store/
│   ├── authStore.ts        # Auth state (Zustand)
│   └── uiStore.ts          # UI state (Zustand)
│
├── types/api/              # API response/request type definitions
├── constants/              # Paths, error codes, messages
└── middleware.ts            # Auth middleware (token refresh, route protection)
```

## Architecture

### Rendering Strategy

| Route | Rendering | Why |
|-------|-----------|-----|
| `/@blogSlug/*` (blog posts) | SSR | SEO, fast initial load for public content |
| `(home)` (landing page) | SSR | SEO |
| `dashboard/*` | SSR layout + CSR content | Layout fetches user/blog info server-side; interactive content is client-side |
| `(auth)/*` (login, signup) | CSR | Multi-step forms with client state |

### Data Fetching Pattern

```
Server Components / Server Actions
  └── apiServer.ts (fetch wrapper)
        ├── Reads cookies via next/headers
        ├── Attaches Authorization header
        ├── Handles 401 → token refresh → retry
        └── Calls backend API (server-to-server)

Client Components
  └── TanStack Query hooks
        └── apiClient.ts (Axios)
              ├── Request interceptor: attaches access token
              ├── Response interceptor: standardizes errors
              └── axios-auth-refresh: 401 → refresh → retry (with queue)
```

### Auth Flow

1. **Server-side (middleware.ts)**: On every navigation, checks cookies. If access token is missing but refresh token exists, refreshes server-side and sets new cookies via redirect.
2. **Server Actions (apiServer.ts)**: On 401 response, attempts token refresh transparently before returning error.
3. **Client-side (apiClient.ts)**: On 401, `axios-auth-refresh` handles token rotation with a request queue to prevent race conditions during concurrent requests.
4. **State sync (AuthInitializer)**: Root layout prefetches user profile via React Query, and `AuthInitializer` hydrates the Zustand auth store on first render.

### URL Routing

Public blog URLs use a vanity format via Next.js rewrites:

```
/@blogSlug           →  /user/[blogSlug]           (blog home)
/@blogSlug/postSlug  →  /user/[blogSlug]/posts/[postSlug]  (post detail)
```

## Build & Run

```bash
# Development (with Turbopack)
npm run dev

# Production build
npm run build
npm start

# Lint
npm run lint
```

### Environment Variables

| Variable | Description |
|----------|-------------|
| `NEXT_PUBLIC_API_BASE_URL` | Backend API base URL (e.g., `http://localhost:8080`) |
| `NEXT_PUBLIC_RECAPTCHA_V2_SITE_KEY` | Google reCAPTCHA v2 site key |
| `NEXT_PUBLIC_RECAPTCHA_V3_SITE_KEY` | Google reCAPTCHA v3 site key |
| `NEXT_PUBLIC_GOOGLE_OAUTH_URL` | Google OAuth2 authorization URL |

## Docker Build & Deploy

```bash
# 1. Build Docker image
docker build -t contentria/blog-frontend:1.0 .

# 2. Save image to tar
docker save -o blog-frontend.tar contentria/blog-frontend:1.0

# 3. Transfer to remote server
scp blog-frontend.tar <username>@<remote-host>:~
```

On the remote server:

```bash
# 1. Load the Docker image
sudo ctr -n k8s.io images import blog-frontend.tar

# 2. Verify
sudo crictl images
```
