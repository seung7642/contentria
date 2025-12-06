import { NextRequest, NextResponse } from 'next/server';

export async function middleware(request: NextRequest) {
  console.log('Middleware executed for path:', request.nextUrl.pathname);
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
