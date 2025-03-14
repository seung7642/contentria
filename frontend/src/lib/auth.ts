export const signInWithGoogle = () => {
  // CSRF 방지를 위한 state 생성
  const state = generateRandomString(32);
  sessionStorage.setItem('oauth_state', state);

  // Google OAuth URL 구성
  const googleAuthUrl = new URL('https://accounts.google.com/o/oauth2/v2/auth');
  googleAuthUrl.searchParams.append(
    'client_id',
    process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID as string
  );
  googleAuthUrl.searchParams.append(
    'redirect_uri',
    `${process.env.NEXT_PUBLIC_API_URL}/api/auth/google/callback`
  );
  googleAuthUrl.searchParams.append('response_type', 'code');
  googleAuthUrl.searchParams.append('scope', 'email profile');
  googleAuthUrl.searchParams.append('prompt', 'select_account');
  googleAuthUrl.searchParams.append('state', state);

  // Google 로그인 페이지로 리디렉션
  window.location.href = googleAuthUrl.toString();
};

// JWT 관련 함수들
export const saveToken = (token: string) => {
  localStorage.setItem('auth_token', token);
};

export const getToken = (): string | null => {
  if (typeof window !== 'undefined') {
    return localStorage.getItem('auth_token');
  }
  return null;
};

export const isAuthenticated = (): boolean => {
  return !!getToken();
};

export const logout = () => {
  localStorage.removeItem('auth_token');
  window.location.href = '/';
};

export const generateRandomString = (length: number): string => {
  const possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let text = '';
  for (let i = 0; i < length; i++) {
    text += possible.charAt(Math.floor(Math.random() * possible.length));
  }
  return text;
};
