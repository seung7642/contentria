'use client';

import React, { useEffect, useRef } from 'react';

// Google 계정 타입 정의
interface GoogleCredentialResponse {
  credential: string;
  select_by: string;
  client_id: string;
}

// 버튼 렌더링 옵션 타입
interface GoogleButtonOptions {
  type: 'standard' | 'icon';
  theme?: 'outline' | 'filled_blue' | 'filled_black';
  size?: 'large' | 'medium' | 'small';
  text?: 'signin_with' | 'signup_with' | 'continue_with' | 'signin';
  shape?: 'rectangular' | 'pill' | 'circle' | 'square';
  logo_alignment?: 'left' | 'center';
  width?: string | number;
  locale?: string;
}

// Google 객체 타입 정의
interface GoogleAccountsType {
  id: {
    initialize: (config: {
      client_id: string;
      callback: (response: GoogleCredentialResponse) => void;
      auto_select?: boolean;
      cancel_on_tap_outside?: boolean;
    }) => void;
    renderButton: (element: HTMLElement, options: GoogleButtonOptions) => void;
    prompt: () => void;
    disableAutoSelect: () => void;
    cancel: () => void;
  };
}

// Window 인터페이스에 Google 속성 추가
declare global {
  interface Window {
    google?: {
      accounts: GoogleAccountsType;
    };
  }
}

const GoogleLoginButton: React.FC = () => {
  const googleButtonRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    // Google Identity Services 스크립트 로드
    const loadGoogleScript = () => {
      if (typeof window !== 'undefined' && !window.google) {
        const script = document.createElement('script');
        script.src = 'https://accounts.google.com/gsi/client';
        script.async = true;
        script.defer = true;
        document.head.appendChild(script);

        script.onload = initializeGoogleButton;
        return () => {
          document.head.removeChild(script);
        };
      } else if (window.google) {
        initializeGoogleButton();
      }
    };

    // Google 버튼 초기화
    const initializeGoogleButton = () => {
      if (window.google && googleButtonRef.current) {
        window.google.accounts.id.initialize({
          client_id: process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID,
          callback: handleCredentialResponse,
          auto_select: false,
          cancel_on_tap_outside: true,
        });

        window.google.accounts.id.renderButton(googleButtonRef.current, {
          type: 'standard',
          theme: 'outline',
          size: 'large',
          text: 'signin_with',
          shape: 'rectangular',
          logo_alignment: 'left',
          width: googleButtonRef.current.clientWidth,
        });
      }
    };

    loadGoogleScript();

    // 컴포넌트 언마운트 시 Google Identity 정리
    return () => {
      if (window.google) {
        window.google.accounts.id.cancel();
      }
    };
  }, []);

  // Google에서 응답 처리
  const handleCredentialResponse = async (response: GoogleCredentialResponse) => {
    // // 디버깅용 콘솔 출력
    console.log('Google 응답 전체:', response);
    console.log('Google 인증 토큰(JWT):', response.credential);

    // JWT 디코딩하여 페이로드 확인 (선택 사항)
    try {
      const payload = JSON.parse(atob(response.credential.split('.')[1]));
      console.log('JWT 페이로드 (디코딩):', payload);
    } catch (error) {
      console.error('JWT 디코딩 오류:', error);
    }

    if (response.credential) {
      // 백엔드로 ID 토큰 전송
      await sendTokenToBackend(response.credential);
    }
  };

  // 백엔드로 토큰 전송
  const sendTokenToBackend = async (credential: string): Promise<void> => {
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/auth/google/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ credential }),
      });

      if (response.ok) {
        // 백엔드에서 반환하는 데이터 형식 정의
        const data: {
          token: string;
          userId: number;
          name: string;
          email: string;
        } = await response.json();

        // JWT 토큰 저장
        localStorage.setItem('auth_token', data.token);

        // 사용자 정보 저장
        const userData = {
          id: data.userId,
          name: data.name,
          email: data.email,
        };
        localStorage.setItem('userData', JSON.stringify(userData));

        window.location.href = '/'; // 로그인 후 리디렉션 페이지
      } else {
        console.error('Login failed');
      }
    } catch (error) {
      console.error('Error during authentication', error);
    }
  };

  return (
    <div
      ref={googleButtonRef}
      className="flex w-full justify-center"
      data-auto-select="false"
    ></div>
  );
};

export default GoogleLoginButton;
