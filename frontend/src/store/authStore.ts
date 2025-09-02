import { cookieManager } from '@/lib/cookieManager';
import { User } from '@/types/user';
import { create } from 'zustand';

interface AuthState {
  user: User | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  setUser: (user: User | null) => void;
  setLoading: (loading: boolean) => void;
  initializeAuth: (user: User | null) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  isLoading: true,
  isAuthenticated: false,

  setUser: (user) => set({ user: user, isAuthenticated: !!user }),
  setLoading: (loading) => set({ isLoading: loading }),
  initializeAuth: (user) => set({ user: user, isAuthenticated: !!user, isLoading: false }),
  logout: () => {
    cookieManager.clearAuthCookies();
    set({ user: null, isAuthenticated: false, isLoading: false });
    console.log('[Auth] User logged out, cookies cleared.');
  },
}));
