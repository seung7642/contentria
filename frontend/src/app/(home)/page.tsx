'use client'; // 클라이언트 컴포넌트로 전환

import Link from 'next/link';
import { Feather, Edit3, Share2 } from 'lucide-react';
import { TypeAnimation } from 'react-type-animation'; // 라이브러리 import
import { memo } from 'react'; // memo import

// TypeAnimation 컴포넌트를 memo로 감싸서 불필요한 리렌더링 방지 (선택 사항)
const MemoizedTypeAnimation = memo(TypeAnimation);

export default function HomePage() {
  return (
    <div className="flex flex-col">
      {/* Hero Section */}
      <section className="bg-gradient-to-br from-indigo-50 via-white to-white py-24 text-center md:py-32">
        <div className="container mx-auto max-w-4xl px-6">
          {/* 헤드라인: whitespace-nowrap 추가 및 TypeAnimation 적용 */}
          <h1 className="mb-5 whitespace-nowrap text-4xl font-extrabold tracking-tight text-gray-900 md:text-6xl">
            <MemoizedTypeAnimation
              sequence={[
                // 첫 번째 문구 타이핑
                '당신의 이야기를 세상에 펼쳐보세요.',
                1500, // 1.5초 대기
                // 첫 번째 문구 지우고 두 번째 문구 타이핑 (속도 약간 느리게)
                () => document.querySelector('.type-animation-cursor')?.remove(), // 커서 깜빡임 방지용 임시 제거
                '', // 내용 지우기 (빠르게 지워짐)
                '쉽고 빠르게 시작할 수 있습니다.',
                1500, // 1.5초 대기
                // 두 번째 문구 지우고 첫 번째 문구 다시 타이핑 (이번엔 스타일 적용)
                () => document.querySelector('.type-animation-cursor')?.remove(), // 커서 깜빡임 방지용 임시 제거
                '', // 내용 지우기
                '당신의 이야기를 세상에 펼쳐보세요.', // 스타일 없이 먼저 타이핑 완료
                (element) => {
                  // 타이핑 완료 후 콜백 실행: "이야기" 부분 스타일 적용
                  if (element) {
                    element.innerHTML =
                      '당신의 <span class="text-indigo-600">이야기</span>를 세상에 펼쳐보세요.';
                    // 커서를 다시 생성 (라이브러리가 자동으로 관리해주기도 함)
                    const cursor = document.createElement('span');
                    cursor.className = 'type-animation-cursor';
                    cursor.innerHTML = '|'; // 또는 다른 커서 모양
                    // element.appendChild(cursor); // 필요시 커서 수동 추가
                  }
                },
                5000, // 마지막 문구를 5초간 보여줌
              ]}
              wrapper="span"
              cursor={true}
              repeat={0} // 반복 안 함
              speed={60} // 타이핑 속도 약간 느리게 (기존 50 -> 60)
              deletionSpeed={80} // 지우는 속도
              className="inline-block" // 스타일 적용 및 레이아웃 안정성 위해
            />
          </h1>
          <p className="mb-10 text-lg text-gray-600 md:text-xl">
            간편하게 시작하는 나만의 블로그. 지금 바로 시작하세요.
          </p>
          <div className="flex flex-col items-center justify-center gap-4 sm:flex-row">
            <Link
              href="/login" // 로그인 또는 회원가입 페이지 경로
              className="inline-block rounded-lg bg-indigo-600 px-8 py-3.5 text-base font-semibold text-white shadow-md transition duration-300 ease-in-out hover:bg-indigo-700 hover:shadow-lg"
            >
              블로그 시작하기
            </Link>
          </div>
        </div>
      </section>

      {/* Features Section (이하 동일) */}
      <section className="bg-white py-20 md:py-24">
        <div className="container mx-auto max-w-6xl px-6">
          <h2 className="mb-16 text-center text-3xl font-bold text-gray-800 md:text-4xl">
            블로깅, <span className="text-indigo-600">더 쉽고 강력하게</span>
          </h2>
          <div className="grid grid-cols-1 gap-10 md:grid-cols-3">
            {/* Feature 1: Easy Writing */}
            <div className="transform rounded-xl border border-gray-100 bg-white p-8 text-center shadow-sm transition duration-300 hover:-translate-y-1 hover:shadow-lg">
              <div className="mb-6 inline-flex h-16 w-16 items-center justify-center rounded-full bg-indigo-100 text-indigo-600">
                <Edit3 size={32} strokeWidth={1.5} />
              </div>
              <h3 className="mb-3 text-xl font-semibold text-gray-900">직관적인 글쓰기</h3>
              <p className="text-gray-600">
                마크다운 지원, 실시간 미리보기. 글쓰기에만 집중할 수 있는 환경을 제공합니다.
              </p>
            </div>
            {/* Feature 2: Customization (Future) */}
            <div className="transform rounded-xl border border-gray-100 bg-white p-8 text-center shadow-sm transition duration-300 hover:-translate-y-1 hover:shadow-lg">
              <div className="mb-6 inline-flex h-16 w-16 items-center justify-center rounded-full bg-green-100 text-green-600">
                <Feather size={32} strokeWidth={1.5} />
              </div>
              <h3 className="mb-3 text-xl font-semibold text-gray-900">나만의 스타일</h3>
              <p className="text-gray-600">
                다양한 테마와 커스텀 도메인으로 개성있는 블로그를 만들어보세요. (곧 제공 예정)
              </p>
            </div>
            {/* Feature 3: Easy Sharing */}
            <div className="transform rounded-xl border border-gray-100 bg-white p-8 text-center shadow-sm transition duration-300 hover:-translate-y-1 hover:shadow-lg">
              <div className="mb-6 inline-flex h-16 w-16 items-center justify-center rounded-full bg-sky-100 text-sky-600">
                <Share2 size={32} strokeWidth={1.5} />
              </div>
              <h3 className="mb-3 text-xl font-semibold text-gray-900">손쉬운 공유</h3>
              <p className="text-gray-600">
                클릭 한 번으로 소셜 미디어에 글을 공유하고 더 많은 독자와 만나보세요.
              </p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
