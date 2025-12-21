import { NextRequest, NextResponse } from 'next/server';
import { PATHS } from './constants/paths';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

export async function middleware(request: NextRequest) {
  console.log('Middleware executed for path:', request.nextUrl.pathname);

  const { pathname } = request.nextUrl;

  const accessToken = request.cookies.get('accessToken')?.value;
  const refreshToken = request.cookies.get('refreshToken')?.value;

  const isAuthPage = pathname.startsWith('/login') || pathname.startsWith('/signup');
  const isDashboardPage = pathname.startsWith('/dashboard');
  const isBlogPage = pathname.startsWith('/@');

  if (isAuthPage) {
    console.log(
      `[Middleware] Auth page. accessToken: ${accessToken?.substring(0, 10) ?? ''}, refreshToken: ${refreshToken?.substring(0, 10) ?? ''}`
    );
    if (accessToken || refreshToken) {
      console.log(
        '[Middleware] Authenticated user trying to access auth page. Redirecting to dashboard.'
      );
      return NextResponse.redirect(new URL('/dashboard', request.url));
    }
  }

  if (isDashboardPage || isBlogPage) {
    if (accessToken) {
      console.log('[Middleware] accessToken present. Allowing access to dashboard.');
      return NextResponse.next();
    }

    if (refreshToken) {
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
          console.log('[Middleware] Refresh token invalid or expired.');
          throw new Error('Refresh token invalid or expired');
        }

        console.log(
          '[Middleware] Refresh successful. Updating tokens and allowing access to dashboard.'
        );
        const { accessToken: newAccessToken, refreshToken: newRefreshToken } =
          await refreshResponse.json();

        const requestHeaders = new Headers(request.headers);
        requestHeaders.set('Authorization', `Bearer ${newAccessToken}`);
        requestHeaders.set(
          'Cookie',
          `accessToken=${newAccessToken}; refreshToken=${newRefreshToken}`
        );

        const response = NextResponse.next({
          request: {
            headers: requestHeaders,
          },
        });

        response.cookies.set('accessToken', newAccessToken, {
          httpOnly: true,
          path: '/',
          maxAge: 1 * 60, // 15 minutes
          // maxAge: 15 * 60, // 15 minutes
        });

        response.cookies.set('refreshToken', newRefreshToken, {
          httpOnly: true,
          path: '/',
          maxAge: 7 * 24 * 60 * 60, // 7 days
        });

        console.log('[Middleware] Tokens updated successfully.');
        return response;
      } catch (error) {
        console.error('[Middleware] Refresh failed.', error);
      }
    }

    if (isDashboardPage) {
      const loginUrl = new URL(PATHS.LOGIN, request.url);
      loginUrl.searchParams.set('redirect', pathname);

      const response = NextResponse.redirect(loginUrl);
      response.cookies.delete('accessToken');
      response.cookies.delete('refreshToken');

      return response;
    }

    if (isBlogPage) {
      const response = NextResponse.next();
      if (refreshToken) {
        response.cookies.delete('accessToken');
        response.cookies.delete('refreshToken');
      }
      return response;
    }
  }

  return NextResponse.next();
}

export const config = {
  /**
   * Match all request paths except for the ones starting with:
   *   - api
   *   - _next/static
   *   - _next/image
   *   - favicon.ico
   */
  matcher: [
    {
      source: '/((?!_next/static|_next/image|favicon.ico).*)',
      missing: [
        { type: 'header', key: 'next-router-prefetch' },
        { type: 'header', key: 'purpose', value: 'prefetch' },
      ],
    },
  ],
};
