import { NextRequest, NextResponse } from 'next/server';

export async function updateSession(request: NextRequest) {
  const accessToken = request.cookies.get('accessToken')?.value;
  const response = NextResponse.next();

  // 1. 토큰 검사 및 갱신 로직 구현
  // (JWT 만료 시간 디코딩 로직 or 백엔드 호출 로직)

  // 2. 갱신 성공 시:
  // response.cookies.set('accessToken', newToken);

  return response;
}
