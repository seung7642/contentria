'use client';

import Link from 'next/link';
import Image from 'next/image';
import { ArrowRight, Edit3, LayoutDashboard, Rss } from 'lucide-react';
import { TypeAnimation } from 'react-type-animation';
import { memo, useEffect, useRef, useState } from 'react';
import { useAuthStore } from '@/store/authStore';
import { PATHS } from '@/constants/paths';

// TypeAnimation 컴포넌트를 memo로 감싸서 불필요한 리렌더링 방지 (선택 사항)
const MemoizedTypeAnimation = memo(TypeAnimation);

type Post = {
  id: string;
  title: string;
  excerpt: string;
  author: string;
  thumbnailUrl: string;
};

const PostCard = ({ id, title, excerpt, author, thumbnailUrl }: Post) => (
  <Link
    href={`/blog/${id}`} // 실제 게시물 경로로 연결
    className="group block overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm transition-all duration-300 hover:border-indigo-300 hover:shadow-lg"
  >
    <div className="relative h-48 w-full">
      <Image
        src={thumbnailUrl}
        alt={`${title} 썸네일`}
        layout="fill"
        objectFit="cover"
        className="transition-transform duration-500 group-hover:scale-105"
      />
    </div>
    <div className="p-6">
      <h3 className="mb-2 line-clamp-2 text-xl font-bold text-gray-900">{title}</h3>
      <p className="mb-4 line-clamp-3 text-gray-600">{excerpt}</p>
      <div className="text-sm font-medium text-indigo-600 group-hover:underline">{author}</div>
    </div>
  </Link>
);

const mockPosts: Post[] = [
  {
    id: '1',
    title: '나의 첫 Next.js 블로그 개발 회고',
    excerpt:
      'Next.js와 TypeScript를 사용하여 나만의 블로그를 만드는 과정은 정말 흥미로웠습니다. 특히 서버 컴포넌트와 클라이언트 컴포넌트의 차이를 이해하는 것이 중요했습니다.',
    author: '개발자A',
    thumbnailUrl: '/images/default-thumbnail-01.png', // public 폴더에 이미지가 있다고 가정
  },
  {
    id: '2',
    title: '주말 제주도 여행, 숨겨진 맛집 탐방기',
    excerpt:
      '사람들이 잘 모르는 제주도의 숨겨진 맛집들을 찾아 떠난 2박 3일간의 기록. 현지인만 아는 흑돼지 맛집부터 오션뷰 카페까지 모두 공개합니다.',
    author: '여행가B',
    thumbnailUrl: '/images/default-thumbnail-01.png',
  },
  {
    id: '3',
    title: '효과적인 재테크를 위한 5가지 원칙',
    excerpt:
      '사회초년생을 위한 현실적인 재테크 가이드. 저축부터 투자까지, 복잡한 금융 지식을 쉽게 풀어내어 당신의 경제적 자유를 돕습니다.',
    author: '재테크전문가C',
    thumbnailUrl: '/images/default-thumbnail-01.png',
  },
  {
    id: '4',
    title: '미니멀리즘: 비움으로써 채우는 삶',
    excerpt:
      '물건을 줄이는 것에서 시작해 삶의 본질에 집중하게 된 이야기. 미니멀리즘이 제 삶에 가져온 긍정적인 변화와 실천 방법들을 공유합니다.',
    author: '미니멀리스트D',
    thumbnailUrl: '/images/default-thumbnail-01.png',
  },
];

export default function HomePage() {
  const { isAuthenticated } = useAuthStore();
  const h1Ref = useRef<HTMLHeadingElement>(null); // h1 요소 참조

  const [posts, setPosts] = useState<Post[]>([]);

  useEffect(() => {
    // TODO: 나중에 실제 API를 호출하는 로직으로 대체 필요
    // 예: const fetchedPosts = await postService.getPopularPosts();
    setPosts(mockPosts);
  }, []);

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
    <div className="flex min-h-screen flex-col bg-white">
      {/* 1. Hero Section (기존과 유사) */}
      <section className="bg-gradient-to-br from-indigo-50 via-white to-white py-24 text-center md:py-32">
        <div className="container mx-auto max-w-4xl px-6">
          <h1
            ref={h1Ref}
            className="mb-5 text-4xl font-extrabold tracking-tight text-gray-900 md:text-6xl"
          >
            <MemoizedTypeAnimation
              sequence={[
                1000,
                '당신의 이야기를 세상에 펼쳐보세요.',
                (el) => triggerWaveAnimation(el),
                5000,
              ]}
              wrapper="span"
              cursor={true}
              repeat={0}
              speed={200}
              className="inline-block whitespace-nowrap"
            />
          </h1>
          <p className="mb-10 text-lg text-gray-600 md:text-xl">
            당신의 이야기가 곧 당신의 브랜드가 됩니다.
          </p>
          <div className="flex flex-col items-center justify-center gap-4 sm:flex-row">
            {isAuthenticated ? (
              <>
                <Link
                  href={PATHS.DASHBOARD}
                  className="inline-flex items-center justify-center rounded-lg bg-indigo-600 px-8 py-3.5 text-base font-semibold text-white shadow-md transition duration-300 ease-in-out hover:bg-indigo-700 hover:shadow-lg"
                >
                  대시보드로 이동
                </Link>
                <Link
                  href={PATHS.NEW_POST}
                  className="inline-flex items-center justify-center rounded-lg border border-gray-300 bg-white px-8 py-3.5 text-base font-semibold text-gray-700 shadow-sm transition duration-300 ease-in-out hover:bg-gray-50"
                >
                  새 글 작성
                </Link>
              </>
            ) : (
              <Link
                href={PATHS.LOGIN}
                className="inline-flex items-center justify-center rounded-lg bg-indigo-600 px-8 py-3.5 text-base font-semibold text-white shadow-md transition duration-300 ease-in-out hover:bg-indigo-700 hover:shadow-lg"
              >
                블로그 시작하기
              </Link>
            )}
          </div>
        </div>
      </section>

      {/* 2. ✨ Content Feed Section (신규) ✨
      <section className="bg-gray-50 py-20 md:py-24">
        <div className="container mx-auto max-w-6xl px-6">
          <div className="mb-16 text-center">
            <h2 className="text-3xl font-bold text-gray-800 md:text-4xl">모두가 주목하는 이야기</h2>
            <p className="mt-4 text-lg text-gray-600">지금, 가장 인기 있는 글들을 만나보세요.</p>
          </div>
          <div className="grid grid-cols-1 gap-8 md:grid-cols-2 lg:grid-cols-2">
            {posts.map((post) => (
              <PostCard key={post.id} {...post} />
            ))}
          </div>
        </div>
      </section> */}

      {/* 3. Features Section (레이아웃 순서 변경) */}
      <section className="bg-white py-20 md:py-24">
        <div className="container mx-auto max-w-6xl px-6">
          <h2 className="mb-16 text-center text-3xl font-bold text-gray-800 md:text-4xl">
            쉽고 강력한 <span className="text-indigo-600">나만의 블로그</span>
          </h2>
          <div className="grid grid-cols-1 gap-10 md:grid-cols-3">
            <div className="transform rounded-xl border border-gray-100 p-8 text-center shadow-sm transition duration-300 hover:-translate-y-1 hover:shadow-lg">
              <div className="mb-6 inline-flex h-16 w-16 items-center justify-center rounded-full bg-indigo-100 text-indigo-600">
                <Edit3 size={32} strokeWidth={1.5} />
              </div>
              <h3 className="mb-3 text-xl font-semibold text-gray-900">직관적인 글쓰기</h3>
              <p className="text-gray-600">
                마크다운 지원, 실시간 미리보기. 글쓰기에만 집중할 수 있는 환경을 제공합니다.
              </p>
            </div>
            <div className="transform rounded-xl border border-gray-100 p-8 text-center shadow-sm transition duration-300 hover:-translate-y-1 hover:shadow-lg">
              <div className="mb-6 inline-flex h-16 w-16 items-center justify-center rounded-full bg-blue-100 text-blue-600">
                <LayoutDashboard size={32} strokeWidth={1.5} />
              </div>
              <h3 className="mb-3 text-xl font-semibold text-gray-900">통합 대시보드</h3>
              <p className="text-gray-600">
                글 관리, 방문자 통계 등 블로그 운영에 필요한 모든 것을 한눈에 파악하고 관리할 수
                있습니다.
              </p>
            </div>
            <div className="transform rounded-xl border border-gray-100 p-8 text-center shadow-sm transition duration-300 hover:-translate-y-1 hover:shadow-lg">
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

      {/* 4. ✨ Final CTA Section (신규) ✨ */}
      {/* <section className="bg-indigo-700">
        <div className="container mx-auto max-w-4xl px-6 py-20 text-center">
          <h2 className="text-3xl font-bold text-white md:text-4xl">이제, 당신의 차례입니다</h2>
          <p className="mx-auto mt-4 max-w-2xl text-lg text-indigo-200">
            세상에 단 하나뿐인 당신의 이야기를 공유하고 새로운 기회를 만들어보세요.
          </p>
          <div className="mt-10 flex justify-center">
            <Link
              href={isAuthenticated ? PATHS.NEW_POST : PATHS.LOGIN}
              className="inline-flex items-center justify-center gap-2 rounded-lg bg-white px-8 py-3.5 text-base font-semibold text-indigo-600 shadow-lg transition duration-300 ease-in-out hover:bg-gray-100"
            >
              <span>{isAuthenticated ? '지금 바로 글쓰기' : '무료로 시작하기'}</span>
              <ArrowRight size={18} />
            </Link>
          </div>
        </div>
      </section> */}
    </div>
  );
}
