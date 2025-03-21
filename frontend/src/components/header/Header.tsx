// Header.tsx
'use client';

import Link from 'next/link';
import { useState, useEffect, useRef } from 'react';
import { useRouter } from 'next/navigation';

import ProfileDropdown from './ProfileDropdown';
import SubscribeModal from './SubscribeModal';
import UserAvatar from './UserAvatar';
import { User } from './types';

const Header = () => {
  const [user, setUser] = useState<User | null>(null);
  const [mounted, setMounted] = useState(false);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isSubscribeModalOpen, setIsSubscribeModalOpen] = useState(false);

  const dropdownRef = useRef<HTMLDivElement>(null);
  const router = useRouter();

  // 사용자 데이터 로드 및 이벤트 리스너 설정
  useEffect(() => {
    setMounted(true);

    // 사용자 정보 로드
    const loadUserData = () => {
      const token = localStorage.getItem('auth_token');
      const userData = localStorage.getItem('userData');

      if (token && userData) {
        try {
          setUser(JSON.parse(userData));
        } catch (e) {
          console.error('Failed to parse user data');
        }
      }
    };

    loadUserData();

    // 드롭다운 외부 클릭 처리
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsDropdownOpen(false);
      }
    };

    // ESC 키로 모달 닫기
    const handleEscKey = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        setIsSubscribeModalOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    document.addEventListener('keydown', handleEscKey);

    // 모달 열린 상태에서 스크롤 방지
    if (isSubscribeModalOpen) {
      document.body.style.overflow = 'hidden';
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
      document.removeEventListener('keydown', handleEscKey);
      document.body.style.overflow = 'unset';
    };
  }, [isSubscribeModalOpen]);

  // 로그아웃 처리
  const handleLogout = () => {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('userData');
    setUser(null);
    setIsDropdownOpen(false);
    router.refresh();
    router.push('/');
  };

  // 클라이언트 사이드 마운트 전에는 초기 UI 반환
  if (!mounted) {
    return (
      <header className="flex items-center justify-between border-b px-6 py-5">
        <div className="w-24"></div>
        <h1 className="text-2xl font-bold">Blog</h1>
        <div className="text-right">
          <span className="mx-2 my-2 rounded-lg bg-indigo-500 px-4 py-3 text-sm text-white">
            Subscribe
          </span>
          <span className="my-2 rounded-lg bg-white px-4 py-3 text-sm text-gray-800">Sign in</span>
        </div>
      </header>
    );
  }

  return (
    <>
      <header className="flex items-center justify-between border-b px-6 py-3">
        <div className="w-24"></div>
        <h1 className="text-2xl font-bold">
          <Link href="/">Blog</Link>
        </h1>

        <div className="flex items-center justify-between text-right">
          {/* 구독 버튼 */}
          <button
            onClick={() => setIsSubscribeModalOpen(true)}
            className="mx-2 rounded-lg bg-indigo-500 px-4 py-3 text-sm text-white hover:bg-indigo-600"
          >
            Subscribe
          </button>

          {/* 사용자 인증 영역 */}
          {user ? (
            <div ref={dropdownRef} className="relative">
              <UserAvatar user={user} onClick={() => setIsDropdownOpen(!isDropdownOpen)} />

              <ProfileDropdown
                user={user}
                isOpen={isDropdownOpen}
                onClose={() => setIsDropdownOpen(false)}
                onLogout={handleLogout}
              />
            </div>
          ) : (
            <Link
              href="/login"
              className="my-2 ml-2 rounded-lg bg-white px-4 py-3 text-sm text-gray-800 hover:bg-gray-200"
            >
              Sign in
            </Link>
          )}
        </div>
      </header>

      {/* 구독 모달 */}
      <SubscribeModal
        isOpen={isSubscribeModalOpen}
        onClose={() => setIsSubscribeModalOpen(false)}
      />

      {/* 애니메이션 스타일 */}
      <style jsx global>{`
        @keyframes modalFadeIn {
          from {
            opacity: 0;
            transform: translateY(-20px);
          }
          to {
            opacity: 1;
            transform: translateY(0);
          }
        }
      `}</style>
    </>
  );
};

export default Header;
