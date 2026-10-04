'use client';

import { useRef } from 'react';
import { useAuthStore } from '@/store/authStore';
import { User } from '@/types/api/user';

interface AuthInitializerProps {
  children: React.ReactNode;
  initialUser: User | null;
}

const AuthInitializer = ({ children, initialUser }: AuthInitializerProps) => {
  const initialized = useRef(false);

  if (!initialized.current) {
    useAuthStore.getState().initializeAuth(initialUser);
    initialized.current = true;
  }

  return <>{children}</>;
};

export default AuthInitializer;
