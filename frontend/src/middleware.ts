import { NextRequest, NextResponse } from 'next/server';

export async function middleware(request: NextRequest) {
  console.log('Middleware executed for path:', request.nextUrl.pathname);

  const { pathname } = request.nextUrl;
  const hasToken = request.cookies.has('accessToken');

  const isAuthPage = pathname.startsWith('/login') || pathname.startsWith('/signup');
  if (isAuthPage && hasToken) {
    return NextResponse.redirect(new URL('/dashboard', request.url));
  }

  if (pathname.startsWith('/dashboard') && !hasToken) {
    return NextResponse.redirect(new URL('/login', request.url));
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
