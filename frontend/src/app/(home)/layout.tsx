import type { Metadata } from 'next';
import localFont from 'next/font/local';
import '@/app/globals.css'; // 전역 스타일
import Footer from '@/components/home/footer';
import HomeHeader from '@/components/home/homeHeader';

// Pretendard 폰트 설정
const pretendard = localFont({
  src: '../../../public/fonts/PretendardVariable.woff2', // 폰트 경로 확인
  display: 'swap',
  weight: '45 920',
  variable: '--font-pretendard', // CSS 변수로 폰트 정의
});

export const metadata: Metadata = {
  title: '나만의 스토리를 펼칠 블로그 플랫폼', // SEO 개선을 위한 타이틀
  description:
    '쉽고 강력한 기능으로 당신의 생각, 지식, 경험을 세상과 공유하세요. 지금 바로 무료로 시작해보세요.', // 상세 설명 추가
};

export default function HomeLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko" className={pretendard.variable}>
      {' '}
      {/* html 태그에 폰트 변수 적용 */}
      <body className="font-pretendard grid min-h-screen grid-rows-[auto_1fr_auto] bg-white antialiased">
        {' '}
        {/* body에 기본 폰트 적용 및 안티앨리어싱 */}
        <HomeHeader /> {/* 헤더 렌더링 */}
        {/* 👇 main 태그에 bg-white 클래스 추가 */}
        <main className="w-full overflow-auto bg-white">
          {children} {/* 페이지 콘텐츠 렌더링 */}
        </main>
        <Footer /> {/* 푸터 렌더링 */}
      </body>
    </html>
  );
}
