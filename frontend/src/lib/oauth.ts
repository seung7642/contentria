/**
 * 구글 OAuth 로그인 시작 — 로그인/회원가입 화면이 공유하는 단일 진입점.
 *
 * window.location.href는 "브라우저"가 이동하는 것이므로 반드시 외부 공개 URL
 * (NEXT_PUBLIC_EXTERNAL_API_BASE_URL)을 써야 한다. 내부용 NEXT_PUBLIC_API_BASE_URL은
 * 배포 환경에서 클러스터 내부 주소라 브라우저가 접근할 수 없다 — 두 훅에 복붙된
 * 함수가 서로 다른 변수로 드리프트해 회원가입 쪽만 깨졌던 원인.
 */
export function startGoogleLogin(): void {
  const baseUrl = process.env.NEXT_PUBLIC_EXTERNAL_API_BASE_URL;
  if (baseUrl) {
    window.location.href = `${baseUrl}/api/oauth2/authorization/google`;
  } else {
    console.error('External API base URL is not configured.');
  }
}
