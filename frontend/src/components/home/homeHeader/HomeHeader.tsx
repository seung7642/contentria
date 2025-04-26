// Header.tsx
'use client';

import Link from 'next/link';
import { useState, useEffect, useRef } from 'react';
import { useRouter } from 'next/navigation';

import ProfileDropdown from './ProfileDropdown';
import UserAvatar from './UserAvatar';
import { User } from './types';

const HomeHeader = () => {
  const [user, setUser] = useState<User | null>(null);
  const [mounted, setMounted] = useState(false);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  const dropdownRef = useRef<HTMLDivElement>(null);
  const router = useRouter();

  // 사용자 데이터 로드 및 이벤트 리스너 설정
  useEffect(() => {
    setMounted(true);

    // 사용자 정보 로드
    const loadUserData = () => {
      const token = localStorage.getItem('access_token');
      const userData = localStorage.getItem('userData');

      if (token && userData) {
        try {
          setUser(JSON.parse(userData));
        } catch (e: unknown) {
          if (e instanceof SyntaxError) {
            console.error(`JSON 파싱 오류 Failed to parse user data: ${e.message}`);
          } else if (e instanceof Error) {
            console.error(`사용자 데이터를 불러오는 중 오류 발생: ${e.message}`);
          } else {
            console.error(`알 수 없는 오류 발생: ${e}`);
          }
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

    document.addEventListener('mousedown', handleClickOutside);

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
      document.body.style.overflow = 'unset';
    };
  }, []);

  // 로그아웃 처리
  const handleLogout = () => {
    localStorage.removeItem('access_token');
    localStorage.removeItem('userData');
    setUser(null);
    setIsDropdownOpen(false);
    router.refresh();
    router.push('/');
  };

  return (
    <header className="border-b shadow-sm">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-3">
        <div className="w-24"></div>
        <h1 className="text-2xl font-bold">
          <Link href="/">Blog</Link>
        </h1>

        <div className="flex items-center space-x-2">
          <div>
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
                className="my-2 ml-2 rounded-lg bg-indigo-500 px-4 py-3 text-sm font-medium text-white shadow-sm transition-colors duration-200 hover:bg-indigo-600 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
              >
                Sign in
              </Link>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

export default HomeHeader;
