'use client';

import React from 'react';

const GoogleLoginButton: React.FC = () => {
  const handleGoogleLoginClick = () => {
    const client_id = process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID;
    const redirect_uri = process.env.NEXT_PUBLIC_GOOGLE_REDIRECT_URI; // 백엔드 콜백 URL
    const response_type = 'code';
    const scope = 'openid email profile';

    const params = new URLSearchParams({
      client_id: client_id || '',
      redirect_uri: redirect_uri || '',
      response_type: response_type,
      scope: scope,
      // state: 'your_random_state_string', // CSRF 보호를 위해 state 파라미터 사용 권장
    });

    const googleLoginUrl = `https://accounts.google.com/o/oauth2/v2/auth?${params.toString()}`;
    window.location.href = googleLoginUrl;
  };

  return (
    <button
      onClick={handleGoogleLoginClick}
      className="/* 높이 조정: py-2.5에서 py-2로 변경 */ /* 텍스트 크기 조정: text-base에서 text-sm으로 변경 */ flex w-full cursor-pointer items-center justify-center rounded border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm transition-colors duration-150 ease-in-out hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-60"
      style={{ minHeight: '36px' }} // 높이 조정: 40px에서 36px로 변경
    >
      {/* 구글 공식 로고 SVG */}
      <svg
        className="mr-2 h-4 w-4" // 크기 조정: mr-3 h-5 w-5에서 mr-2 h-4 w-4로 변경
        viewBox="0 0 24 24"
        xmlns="http://www.w3.org/2000/svg"
      >
        <path
          d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
          fill="#4285F4"
        />
        <path
          d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
          fill="#34A853"
        />
        <path
          d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
          fill="#FBBC05"
        />
        <path
          d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
          fill="#EA4335"
        />
      </svg>
      {/* 버튼 텍스트 */}
      <span>Sign in with Google</span>
    </button>
  );
};

export default GoogleLoginButton;
