import { NextRequest, NextResponse } from 'next/server';
import { updateSession } from './middlewares/authMiddleware';

const PUBLIC_ROUTES = ['/login', '/signup'];

// The file must export a single function, either as a default export or named `middleware`.
export async function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;

  // 현재 경로가 공개 경로(Public Route)에 포함되는지 확인
  // 포함된다면, 토큰 검사 없이 바로 통과
  if (PUBLIC_ROUTES.includes(pathname)) {
    return NextResponse.next();
  }

  const accessToken = request.cookies.get('accessToken')?.value;
  if (!accessToken) {
    const url = request.nextUrl.clone();
    url.pathname = '/login';
    return NextResponse.redirect(url);
  }

  // TODO: 토큰 만료 체크 및 갱신 로직 수행

  return NextResponse.next();
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
