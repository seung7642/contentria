import { NextRequest, NextResponse } from 'next/server';
import { updateSession } from './middlewares/authMiddleware';

// The file must export a single function, either as a default export or named `middleware`.
export async function middleware(request: NextRequest) {
  return await updateSession(request);
}

// Optionally, a config object can be exported alongside the Middleware function.
// This object includes the `matcher` to specify paths where the Middleware applies.
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
