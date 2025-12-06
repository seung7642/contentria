'use client';

import { useAuthStore } from '@/store/authStore';
import Footer from './Footer';
import SkeletonMain from './SkeletonMain';
import SkeletonHeader from './SkeletonHeader';
import HomeHeader from './homeHeader';

export default function HomeLayoutClient({ children }: { children: React.ReactNode }) {
  const isLoading = useAuthStore((state) => state.isLoading);

  if (isLoading) {
    return (
      <div className="grid min-h-screen grid-rows-[auth_1fr_auto] bg-white antialiased">
        <SkeletonHeader />
        <SkeletonMain />
        <Footer />
      </div>
    );
  }

  return (
    <div className="grid min-h-screen grid-rows-[auto_1fr_auto] bg-white antialiased">
      <HomeHeader />
      <main className="w-full overflow-auto">{children}</main>
      <Footer />
    </div>
  );
}
