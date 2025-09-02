'use client';

import Footer from '@/components/home/Footer';
import HomeHeader from '@/components/home/homeHeader';
import SkeletonHeader from '@/components/home/SkeletonHeader';
import SkeletonMain from '@/components/home/SkeletonMain';
import { useAuthStore } from '@/store/authStore';

export default function HomeLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
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
