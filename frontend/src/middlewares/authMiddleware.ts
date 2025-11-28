import { PATHS } from '@/constants/paths';
import { authService } from '@/services/authService';
import { NextRequest, NextResponse } from 'next/server';

const PUBLIC_ROUTES = ['/login', '/signup'];

export async function updateSession(request: NextRequest) {
  const { pathname } = request.nextUrl;

  if (PUBLIC_ROUTES.some(route => pathname === route || pathname.startsWith(`${route}/`))) {
    return NextResponse.next();
  }

  const accessToken = request.cookies.get('accessToken')?.value;
  const refreshToken = request.cookies.get('refreshToken')?.value;

  if (!accessToken) {
    const url = request.nextUrl.clone();
    url.pathname = PATHS.LOGIN;

    // 로그인 후 원래 가려던 페이지로 돌아오게 하려면 쿼리 파라미터를 추가한다.
    url.searchParams.set('redirect', pathname);

    return NextResponse.redirect(url);
  }

  try {
    const isExpiringSoon = checkTokenExpiration(accessToken);

    if (isExpiringSoon) {
      if (!refreshToken) {
        throw new Error('No refresh token available');
      }

      const newTokens = await authService.refreshAccessToken(refreshToken);

      const response = NextResponse.next();

      response.cookies.set('accessToken', 'newAccessTokenValue', {
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        maxAge: 60 * 15, // 15 minutes
        path: '/',
      });

      response.cookies.set('refreshToken', 'newRefreshTokenValue', {
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        maxAge: 60 * 60 * 24 * 7, // 7 days
        path: '/',
      });
    }
  } catch (error) {
    // 갱신 실패 시 (리프레시 토큰도 만료됨): 강제 로그아웃 처리
    console.error('Token refresh failed:', error);
    const url = request.nextUrl.clone();
    url.pathname = PATHS.LOGIN;

    const response = NextResponse.redirect(url);
    response.cookies.delete('accessToken');
    response.cookies.delete('refreshToken');

    return response;
  }

  // 토큰이 유효하고 갱신도 필요 없으면 그냥 통과
  return NextResponse.next();
}

// JWT 디코딩 없이 만료 시간만 체크하는 로직 (혹은 jose 같은 경량 라이브러리 사용 가능)
function checkTokenExpiration(token: string): boolean {
  try {
    // JWT는 payload 부분이 두 번째 조각이다.
    const payloadBase64 = token.split('.')[1];
    if (!payloadBase64) {
      return true;
    }

    const decodedJson = JSON.parse(atob(payloadBase64));
    const exp = decodedJson.exp * 1000; // JWT의 exp는 초 단위이므로 밀리초로 변환

    return Date.now() > exp - (5 * 60 * 1000); // 만료 5분 전부터 갱신 필요로 간주
  } catch (error) {
    return true; // 파싱 에러나면 만료된 것으로 간주
  }
}
