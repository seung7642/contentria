import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

const AUTH_COOKIE_NAME = 'auth_token';

export function middleware(request: NextRequest) {
  // 1. 요청 URL 경로 확인
  const { pathname } = request.nextUrl;

  // 2. /dashboard 경로 접근 시 인증 쿠키 확인
  if (pathname.startsWith('/dashboard')) {
    const authToken = request.cookies.get(AUTH_COOKIE_NAME);

    if (!authToken) {
      console.log('Middleware: No auth token found, redirecting to login fo /dashboard');
      const loginUrl = new URL('/login', request.url);

      // 리디렉션 시 현재 경로를 쿼리 파라미터로 넘겨서 로그인 후 돌아올 수 있게 함
      loginUrl.searchParams.set('redirectedFrom', pathname);
      return NextResponse.redirect(loginUrl);
    }

    console.log('Middleware: Auth token found, allowing access to /dashboard');
  }

  // 3. 그 외 모든 경로는 그대로 통과
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
     * - login
     * - public
     */
    '/((?!api|_next/static|_next/image|favicon.ico|login|public).*)',
  ],
};
