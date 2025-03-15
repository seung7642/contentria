// JWT 관련 함수들
export const saveToken = (token: string): void => {
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

export const logout = (): void => {
  // Google Identity Services 로그아웃
  if (typeof window !== 'undefined' && window.google?.accounts?.id) {
    window.google.accounts.id.disableAutoSelect();
  }
  localStorage.removeItem('auth_token');
  window.location.href = '/';
};

// 유틸리티 함수 (필요시 활용)
export const generateRandomString = (length: number): string => {
  const possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let text = '';
  for (let i = 0; i < length; i++) {
    text += possible.charAt(Math.floor(Math.random() * possible.length));
  }
  return text;
};
