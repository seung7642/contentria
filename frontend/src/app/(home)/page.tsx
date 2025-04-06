'use client'; // 클라이언트 컴포넌트로 전환

import Link from 'next/link';
import { Edit3, LayoutDashboard, Rss } from 'lucide-react';
import { TypeAnimation } from 'react-type-animation';
import { memo, useRef } from 'react';
import Footer from '@/components/home/footer';

// TypeAnimation 컴포넌트를 memo로 감싸서 불필요한 리렌더링 방지 (선택 사항)
const MemoizedTypeAnimation = memo(TypeAnimation);

export default function HomePage() {
  const h1Ref = useRef<HTMLHeadingElement>(null); // h1 요소 참조

  const triggerWaveAnimation = (element: HTMLSpanElement | null) => {
    if (!element) {
      return;
    }

    const wordToAnimate = '이야기';
    const textBefore = '당신의 ';
    const textAfter = '를 세상에 펼쳐보세요.';
    const wordSpans = wordToAnimate
      .split('')
      .map((char, index) => `<span style="--i: ${index};" class="wave-char">${char}</span>`)
      .join('');
    element.innerHTML = `${textBefore}${wordSpans}${textAfter}`;

    const headingElement = h1Ref.current;
    if (headingElement) {
      headingElement.classList.remove('animate-wave');
      setTimeout(() => {
        headingElement.classList.add('animate-wave');
      }, 10);
    }
  };

  return (
    <div className="flex min-h-screen flex-col">
      {/* Hero Section */}
      <section className="bg-gradient-to-br from-indigo-50 via-white to-white py-24 text-center md:py-32">
        <div className="container mx-auto max-w-4xl px-6">
          <h1
            ref={h1Ref}
            className="mb-5 whitespace-nowrap text-4xl font-extrabold tracking-tight text-gray-900 md:text-6xl"
          >
            <MemoizedTypeAnimation
              sequence={[
                1000,
                '당신의 이야기를 세상에 펼쳐보세요.',
                (element) => triggerWaveAnimation(element),
                5000,
              ]}
              wrapper="span"
              cursor={true}
              repeat={0}
              speed={200} // 이전 속도 유지
              className="inline-block"
            />
          </h1>
          {/* 👇 수정된 부제목 */}
          <p className="mb-10 text-lg text-gray-600 md:text-xl">
            당신의 이야기가 곧 당신의 브랜드가 됩니다.
          </p>
          <div className="flex flex-col items-center justify-center gap-4 sm:flex-row">
            <Link
              href="/login"
              className="inline-block rounded-lg bg-indigo-600 px-8 py-3.5 text-base font-semibold text-white shadow-md transition duration-300 ease-in-out hover:bg-indigo-700 hover:shadow-lg"
            >
              블로그 시작하기
            </Link>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="bg-white py-20 md:py-24">
        <div className="container mx-auto max-w-6xl px-6">
          {/* 👇 수정된 섹션 제목 */}
          <h2 className="mb-16 text-center text-3xl font-bold text-gray-800 md:text-4xl">
            쉽고 강력한 <span className="text-indigo-600">나만의 블로그</span>
          </h2>
          <div className="grid grid-cols-1 gap-10 md:grid-cols-3">
            {/* Feature 1: Easy Writing (변경 없음) */}
            <div className="transform rounded-xl border border-gray-100 bg-white p-8 text-center shadow-sm transition duration-300 hover:-translate-y-1 hover:shadow-lg">
              <div className="mb-6 inline-flex h-16 w-16 items-center justify-center rounded-full bg-indigo-100 text-indigo-600">
                <Edit3 size={32} strokeWidth={1.5} />
              </div>
              <h3 className="mb-3 text-xl font-semibold text-gray-900">직관적인 글쓰기</h3>
              <p className="text-gray-600">
                마크다운 지원, 실시간 미리보기. 글쓰기에만 집중할 수 있는 환경을 제공합니다.
              </p>
            </div>

            {/* 👇 Feature 2: Dashboard (수정됨) */}
            <div className="transform rounded-xl border border-gray-100 bg-white p-8 text-center shadow-sm transition duration-300 hover:-translate-y-1 hover:shadow-lg">
              {/* 아이콘 및 색상 변경 */}
              <div className="mb-6 inline-flex h-16 w-16 items-center justify-center rounded-full bg-blue-100 text-blue-600">
                <LayoutDashboard size={32} strokeWidth={1.5} /> {/* LayoutDashboard 아이콘 사용 */}
              </div>
              {/* 제목 변경 */}
              <h3 className="mb-3 text-xl font-semibold text-gray-900">통합 대시보드</h3>
              {/* 설명 변경 */}
              <p className="text-gray-600">
                글 관리, 방문자 통계 확인 등 블로그 운영에 필요한 모든 것을 한눈에 파악하고 관리할
                수 있습니다.
              </p>
            </div>

            {/* Feature 3: Blog Subscription (변경 없음) */}
            <div className="transform rounded-xl border border-gray-100 bg-white p-8 text-center shadow-sm transition duration-300 hover:-translate-y-1 hover:shadow-lg">
              <div className="mb-6 inline-flex h-16 w-16 items-center justify-center rounded-full bg-orange-100 text-orange-600">
                <Rss size={32} strokeWidth={1.5} />
              </div>
              <h3 className="mb-3 text-xl font-semibold text-gray-900">블로그 구독</h3>
              <p className="text-gray-600">
                독자들이 새 글 알림을 받을 수 있도록 이메일 구독 기능을 제공합니다. (추후 제공 예정)
              </p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
