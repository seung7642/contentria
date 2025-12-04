'use client';

import { useAuthRedirect } from '@/hooks/useAuthRedirect';
import { Loader2 } from 'lucide-react';

export default function AuthGuard({ children }: { children: React.ReactNode }) {
  const isCheckingAuth = useAuthRedirect();

  if (isCheckingAuth) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gray-50">
        <Loader2 className="h-12 w-12 animate-spin text-indigo-600" />
      </div>
    );
  }

  return <>{children}</>;
}
