import { User } from '@/types/user';
import { create } from 'zustand';

interface AuthState {
  user: User | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  // 상태 업데이트 함수들 (action)
  setUser: (user: User | null) => void;
  setLoading: (loading: boolean) => void;
  initializeAuth: (user: User | null) => void; // 초기화 액션
}

// Zustand 스토어 생성
export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  isLoading: true,
  isAuthenticated: false,

  // action 구현
  setUser: (user) => set({ user: user, isAuthenticated: !!user }),
  setLoading: (loading) => set({ isLoading: loading }),

  // 서버에서 가져온 초기 데이터로 스토어 상태를 설정하는 action
  initializeAuth: (user) => set({ user: user, isAuthenticated: !!user, isLoading: false }),
}));
