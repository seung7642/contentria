'use client';

import { useEffect, useRef } from 'react';
import { useAuthStore } from '@/store/authStore';
import { User } from '@/types/user';

interface AuthInitializerProps {
  initialUser: User | null;
}

const AuthInitializer = ({ initialUser }: AuthInitializerProps) => {
  // 초기화가 한 번만 실행되도록 useRef 사용
  const initialized = useRef(false);

  useEffect(() => {
    // 아직 초기화되지 않았고, initialUser 값이 (서버로부터) 로드되었을 때
    if (!initialized.current) {
      // Zustand 스토어의 초기화 액션 호출
      useAuthStore.getState().initializeAuth(initialUser);
      initialized.current = true; // 초기화 완료 플래그 설정
      console.log('AuthInitializer: Zustand store initialized.');
    }
  }, [initialUser]);

  // 이 컴포넌트는 시각적인 UI를 렌더링하지 않음
  return null;
};

export default AuthInitializer;
