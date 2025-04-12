import type { Metadata } from 'next';
import Footer from '@/components/home/footer';
import HomeHeader from '@/components/home/homeHeader';

export const metadata: Metadata = {
  title: '나만의 스토리를 펼칠 블로그 플랫폼',
  description:
    '쉽고 강력한 기능으로 당신의 생각, 지식, 경험을 세상과 공유하세요. 지금 바로 무료로 시작해보세요.',
};

export default function HomeLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <div className="grid min-h-screen grid-rows-[auto_1fr_auto] bg-white antialiased">
      <HomeHeader />
      <main className="w-full overflow-auto">{children}</main>
      <Footer />
    </div>
  );
}
