import { NextRequest, NextResponse } from 'next/server';
import { PATHS } from './constants/paths';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';
const COOKIE_SECURE = process.env.COOKIE_SECURE === 'true';

export async function middleware(request: NextRequest) {
  console.log('[Middleware] executed for path:', request.nextUrl.pathname);

  const { pathname } = request.nextUrl;

  const accessToken = request.cookies.get('accessToken')?.value;
  const refreshToken = request.cookies.get('refreshToken')?.value;

  const isAuthPage = pathname.startsWith('/login') || pathname.startsWith('/signup');
  const isProtectedPage = pathname.startsWith('/dashboard');

  const isSessionExpired = request.nextUrl.searchParams.get('alert') === 'session_expired';

  if (isAuthPage) {
    console.log(
      `[Middleware] Auth page. accessToken: ${accessToken?.substring(0, 10) ?? ''}, refreshToken: ${refreshToken?.substring(0, 10) ?? ''}`
    );

    if (isSessionExpired) {
      console.log('[Middleware] Session expired signal detected. Clearing cookies.');
      const response = NextResponse.next();
      response.cookies.delete('accessToken');
      response.cookies.delete('refreshToken');
      return response;
    }

    if (accessToken || refreshToken) {
      console.log('[Middleware] User already logged in. Redirecting to dashboard.');
      return NextResponse.redirect(new URL('/dashboard', request.url));
    }
    return NextResponse.next();
  }

  if (!accessToken && refreshToken) {
    const refreshResponse = await tryRefreshTokens(request, refreshToken);
    if (refreshResponse.status === 307) {
      return refreshResponse;
    }
    if (isProtectedPage) {
      return redirectToLogin(request, pathname);
    }
    return refreshResponse;
  }

  if (isProtectedPage && !accessToken) {
    console.log('[Middleware] No access token for protected page. Redirecting to login.');
    return redirectToLogin(request, pathname);
  }

  console.log('[Middleware] Completed checks. Proceeding to next middleware or route.');
  return NextResponse.next();
}

async function tryRefreshTokens(request: NextRequest, refreshToken: string): Promise<NextResponse> {
  console.log('[Middleware] accessToken expired. Attempting refresh.');

  try {
    const refreshResponse = await fetch(`${API_BASE_URL}/api/auth/refresh`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Cookie: `refreshToken=${refreshToken}`,
      },
    });

    if (!refreshResponse.ok) {
      throw new Error('Refresh token invalid or expired');
    }

    console.log('[Middleware] Refresh successful. Reloading...');
    const { accessToken: newAccessToken, refreshToken: newRefreshToken } =
      await refreshResponse.json();

    const response = NextResponse.redirect(request.url);
    setAuthCookies(response, newAccessToken, newRefreshToken);
    return response;
  } catch (error) {
    console.error('[Middleware] Refresh failed. Clearing cookies.', error);
    const response = NextResponse.next();
    response.cookies.delete('accessToken');
    response.cookies.delete('refreshToken');
    return response;
  }
}

function redirectToLogin(request: NextRequest, fromPath: string) {
  const loginUrl = new URL(PATHS.LOGIN, request.url);
  loginUrl.searchParams.set('redirect', fromPath);

  const response = NextResponse.redirect(loginUrl);
  response.cookies.delete('accessToken');
  response.cookies.delete('refreshToken');
  return response;
}

function setAuthCookies(response: NextResponse, accessToken: string, refreshToken: string) {
  response.cookies.set('accessToken', accessToken, {
    httpOnly: true,
    secure: COOKIE_SECURE,
    path: '/',
    maxAge: 15 * 60, // 15 minutes
    sameSite: 'lax',
  });
  response.cookies.set('refreshToken', refreshToken, {
    httpOnly: true,
    secure: COOKIE_SECURE,
    path: '/',
    maxAge: 7 * 24 * 60 * 60, // 7 days
    sameSite: 'lax',
  });
}

export const config = {
  /**
   * Match all request paths except for the ones starting with:
   *   1. api
   *   2. _next/static
   *   3. _next/image
   *   4. favicon.ico
   *   5. icons
   *   6. any file with an extension (e.g., .css, .js, .png, etc.)
   */
  matcher: [
    {
      source: '/((?!_next/static|_next/image|favicon.ico|icons|.*\\..*).*)',
      missing: [
        { type: 'header', key: 'next-router-prefetch' },
        { type: 'header', key: 'purpose', value: 'prefetch' },
      ],
    },
  ],
};
