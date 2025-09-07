import { cookieManager } from '@/lib/cookieManager';
import { User } from '@/types/user';
import { create } from 'zustand';

interface AuthState {
  user: User | null;
  accessToken: string | null;
  isLoading: boolean;
  isAuthenticated: boolean;

  login: (user: User, accessToken: string) => void;
  setAccessToken: (accessToken: string) => void;
  setUser: (user: User | null) => void;
  setLoading: (loading: boolean) => void;
  initializeAuth: (user: User | null) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  accessToken: null,
  isLoading: true,
  isAuthenticated: false,

  login: (user, accessToken) =>
    set({
      user,
      accessToken,
      isAuthenticated: true,
      isLoading: false,
    }),
  setAccessToken: (accessToken) => set({ accessToken }),
  setUser: (user) => set({ user: user, isAuthenticated: !!user }),
  setLoading: (loading) => set({ isLoading: loading }),
  initializeAuth: (user) =>
    set({ user: user, accessToken: null, isAuthenticated: !!user, isLoading: false }),
  logout: () => {
    cookieManager.clearAuthCookies();
    set({ user: null, accessToken: null, isAuthenticated: false, isLoading: false });
    console.log('[Auth] User logged out.');
  },
}));
