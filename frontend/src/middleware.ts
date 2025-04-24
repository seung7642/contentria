import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';
const ACCESS_TOKEN_COOKIE_NAME = 'auth_token';
const REFRESH_TOKEN_COOKIE_NAME = 'refresh_token';
const DEFAULT_LOGGED_IN_REDIRECT = '/dashboard';
const LOGIN_PATH = '/login';

async function validateAccessToken(request: NextRequest): Promise<boolean> {
  const authTokenCookie = request.cookies.get(ACCESS_TOKEN_COOKIE_NAME);
  if (!authTokenCookie) {
    return false;
  }

  try {
    const response = await fetch(`${BACKEND_API_URL}/api/users/me`, {
      headers: {
        Cookie: `${ACCESS_TOKEN_COOKIE_NAME}=${authTokenCookie.value}`,
      },
      redirect: 'manual', // 백엔드 리디렉션 방지
    });
    return response.ok;
  } catch (e) {
    console.error('[Middleware] Error validating access token:', e);
    return false;
  }
}

async function attemptRefreshSession(request: NextRequest): Promise<NextResponse | null> {
  const refreshTokenCookie = request.cookies.get(REFRESH_TOKEN_COOKIE_NAME);
  if (!refreshTokenCookie) {
    return null;
  }

  console.log('[Middleware] Access token invalid/missing. Attempting refresh with refresh token.');
  try {
    const refreshResponse = await fetch(`${BACKEND_API_URL}/api/auth/refresh`, {
      method: 'POST',
      headers: {
        Cookie: `${REFRESH_TOKEN_COOKIE_NAME}=${refreshTokenCookie.value}`,
      },
    });

    if (refreshResponse.ok) {
      console.log('[Middleware] Refresh successful. Redirecting to dashboard.');
      const redirectUrl = request.nextUrl.clone();
      redirectUrl.pathname = DEFAULT_LOGGED_IN_REDIRECT;
      redirectUrl.search = '';
      const response = NextResponse.redirect(redirectUrl);
      return response;
    } else {
      console.log('[Middleware] Refresh failed.');
      return null;
    }
  } catch (e) {
    console.error('[Middleware] Error during refresh attempt:', e);
    return null;
  }
}

export async function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;
  const authTokenCookie = request.cookies.get(ACCESS_TOKEN_COOKIE_NAME);
  const refreshTokenCookie = request.cookies.get(REFRESH_TOKEN_COOKIE_NAME);

  if (pathname.startsWith(LOGIN_PATH)) {
    console.log('[Middleware] Accessing login page:', pathname);

    if (authTokenCookie) {
      const isAccessTokenValid = await validateAccessToken(request);
      if (isAccessTokenValid) {
        console.log(
          `[Middleware] Valid access token found. Redirecting to ${DEFAULT_LOGGED_IN_REDIRECT}`
        );
        const url = request.nextUrl.clone();
        url.pathname = DEFAULT_LOGGED_IN_REDIRECT;
        return NextResponse.redirect(url);
      }
    }

    if (refreshTokenCookie) {
      const refreshResultResponse = await attemptRefreshSession(request);
      if (refreshResultResponse) {
        return refreshResultResponse;
      }
    }

    console.log(
      `[Middleware] No valid session found or refresh failed. Allowing access to ${LOGIN_PATH} and clearing cookies.`
    );
    const response = NextResponse.next();
    response.cookies.set(ACCESS_TOKEN_COOKIE_NAME, '', { maxAge: -1, path: '/' });
    response.cookies.set(REFRESH_TOKEN_COOKIE_NAME, '', { maxAge: -1, path: '/api/auth/refresh' });
    return response;
  }

  if (pathname.startsWith('/dashboard')) {
    if (authTokenCookie) {
      const isValid = await validateAccessToken(request);
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
        response.cookies.set(ACCESS_TOKEN_COOKIE_NAME, '', { maxAge: -1, path: '/' });
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
