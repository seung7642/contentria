import { deleteCookie, getCookie, setCookie } from 'cookies-next';

const ACCESS_TOKEN_COOKIE = 'accessToken';
const REFRESH_TOKEN_COOKIE = 'refreshToken';

export const cookieManager = {
  getAccessToken: () => getCookie(ACCESS_TOKEN_COOKIE),
  getRefreshToken: () => getCookie(REFRESH_TOKEN_COOKIE),

  setAccessToken: (token: string) => setCookie(ACCESS_TOKEN_COOKIE, token),
  setRefreshToken: (token: string) => setCookie(REFRESH_TOKEN_COOKIE, token),

  // 로그아웃 시 사용할 쿠키 삭제 함수
  clearAuthCookies: () => {
    deleteCookie(ACCESS_TOKEN_COOKIE);
    deleteCookie(REFRESH_TOKEN_COOKIE);
  },
};
