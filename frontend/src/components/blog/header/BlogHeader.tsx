// components/blog/BlogHeader.tsx (파일명을 더 명확하게 변경하는 것을 추천합니다)
'use client';

import { useState, useRef, useEffect } from 'react';
import Link from 'next/link';
import Image from 'next/image';
import { useRouter } from 'next/navigation';

import { useAuthStore } from '@/store/authStore';
import ProfileDropdown from '@/components/dashboard/header/ProfileDropdown';
import UserAvatar from '@/components/dashboard/header/UserAvatar';
import { useUiStore } from '@/store/uiStore';

interface BlogHeaderProps {
  blogName: string;
  blogSlug: string;
}

const BlogHeader = ({ blogName, blogSlug }: BlogHeaderProps) => {
  // ✨ 1. 중앙 상태 관리(Zustand)를 사용하여 사용자 정보를 가져옵니다.
  const user = useAuthStore((state) => state.user);
  const setUser = useAuthStore((state) => state.setUser);
  const openSubscribeModal = useUiStore((state) => state.openSubscribeModal);

  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const profileRef = useRef<HTMLDivElement>(null);
  const router = useRouter();

  // ✨ 2. DashboardHeader와 동일한 방식으로 드롭다운 외부 클릭을 감지합니다.
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (profileRef.current && !profileRef.current.contains(event.target as Node)) {
        setIsProfileOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleLogout = () => {
    // 실제 API 로그아웃 호출이 있다면 여기에 추가
    setUser(null);
    setIsProfileOpen(false);
    router.push('/'); // 로그아웃 후 홈으로 이동
    // localStorage는 store 내부나 API 요청 로직에서 처리하는 것이 더 좋습니다.
  };

  return (
    <header className="sticky top-0 z-30 w-full border-b bg-white/80 shadow-sm backdrop-blur-sm">
      <div className="flex items-center justify-between px-4 py-3 sm:px-6">
        {/* 왼쪽 영역: 로고 및 블로그 이름 */}
        <div className="flex min-w-0 items-center">
          {/* ✨ 3. 로고 추가 (next/image로 최적화) */}
          <Link href="/" className="flex flex-shrink-0 items-center space-x-2">
            <Image
              src="/contentria-icon.png"
              alt="Contentria 로고"
              width={32} // 헤더에 맞는 적절한 크기 지정
              height={32}
              priority // 헤더 로고는 항상 중요
            />
            <span className="text-lg font-bold text-gray-800">Contentria</span>
          </Link>
        </div>

        {/* 오른쪽 영역: 버튼 및 프로필 */}
        <div className="flex items-center space-x-2 md:space-x-4">
          <button
            onClick={openSubscribeModal}
            className="hidden rounded-md bg-indigo-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600 sm:block"
          >
            구독하기
          </button>

          {/* ✨ 4. 사용자 인증 상태에 따른 UI 분기 처리 */}
          {user ? (
            <div className="relative" ref={profileRef}>
              <button
                onClick={() => setIsProfileOpen(!isProfileOpen)}
                className="relative flex h-9 w-9 items-center justify-center overflow-hidden rounded-full focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
                aria-label="프로필 메뉴 열기"
              >
                <UserAvatar user={user} />
              </button>

              {isProfileOpen && (
                <ProfileDropdown
                  user={user}
                  onClose={() => setIsProfileOpen(false)}
                  onLogout={handleLogout}
                />
              )}
            </div>
          ) : (
            <Link
              href="/login"
              className="rounded-md px-3 py-2 text-sm font-semibold text-gray-700 hover:bg-gray-100"
            >
              로그인
            </Link>
          )}
        </div>
      </div>
    </header>
  );
};

export default BlogHeader;
