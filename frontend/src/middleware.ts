import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';
const AUTH_COOKIE_NAME = 'auth_token';
const DEFAULT_LOGGED_IN_REDIRECT = '/dashboard';
const LOGIN_PATH = '/login';

async function validateSession(request: NextRequest): Promise<boolean> {
  const authTokenCookie = request.cookies.get(AUTH_COOKIE_NAME);
  if (!authTokenCookie) {
    return false;
  }

  try {
    const response = await fetch(`${BACKEND_API_URL}/api/users/me`, {
      headers: {
        Cookie: `${AUTH_COOKIE_NAME}=${authTokenCookie.value}`,
      },
      redirect: 'manual', // 백엔드 리디렉션 방지
    });

    // 200 OK 응답이면 세션 유효 (JWT 유효)
    return response.ok;
  } catch (e) {
    console.error('[Middleware] Error validating session:', e);
    return false;
  }
}

export async function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;
  const authTokenCookie = request.cookies.get(AUTH_COOKIE_NAME);

  if (pathname.startsWith(LOGIN_PATH)) {
    if (authTokenCookie) {
      const isValid = await validateSession(request);
      if (isValid) {
        console.log(
          `[Middleware] Valid session found. Redirecting from ${LOGIN_PATH} to ${DEFAULT_LOGGED_IN_REDIRECT}`
        );
        const url = request.nextUrl.clone();
        url.pathname = DEFAULT_LOGGED_IN_REDIRECT;
        return NextResponse.redirect(url);
      } else {
        console.log(
          `[Middleware] Invalid/Expired session found on ${LOGIN_PATH}. Allowing access and clearing cookie.`
        );
        const response = NextResponse.next();
        response.cookies.set(AUTH_COOKIE_NAME, '', { maxAge: -1, path: '/' });
        return response;
      }
    }
  }

  if (pathname.startsWith('/dashboard')) {
    if (authTokenCookie) {
      const isValid = await validateSession(request);
      if (isValid) {
        console.log(`[Middleware] Valid session. Allowing access to ${pathname}`);
        return NextResponse.next();
      } else {
        console.log(
          `[Middleware] Invalid/Expired session. Redirecting to ${LOGIN_PATH} from ${pathname}`
        );
        const loginUrl = new URL(LOGIN_PATH, request.url);
        loginUrl.searchParams.set('redirectedFrom', pathname);
        const response = NextResponse.redirect(loginUrl);
        response.cookies.set(AUTH_COOKIE_NAME, '', { maxAge: -1, path: '/' });
        return response;
      }
    } else {
      console.log(
        `[Middleware] No auth token found. Redirecting to ${LOGIN_PATH} from ${pathname}`
      );
      const loginUrl = new URL(LOGIN_PATH, request.url);
      loginUrl.searchParams.set('redirectedFrom', pathname);
      return NextResponse.redirect(loginUrl);
    }
  }

  // 그 외 모든 경로는 그대로 통과
  return NextResponse.next();
}

// Middleware가 실행될 경로를 지정하는 Matcher 설정
export const config = {
  matcher: [
    /**
     * Match all request paths except for the ones starting with:
     * - api
     * - _next/static
     * - _next/image
     * - favicon.ico
     * - public
     */
    '/((?!api|_next/static|_next/image|favicon.ico|public).*)',
  ],
};
