'use client';

import Link from 'next/link';
import { useState, useEffect, useRef } from 'react';
import { useRouter } from 'next/navigation'; // App Router 사용
import { Home, LogOut, User } from 'lucide-react';

const Header = () => {
  const [user, setUser] = useState(null);
  const [mounted, setMounted] = useState(false);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);
  const router = useRouter();

  useEffect(() => {
    // 컴포넌트 마운트 시 로컬 스토리지 접근
    setMounted(true);

    // 사용자 정보 로드
    const token = localStorage.getItem('auth_token');
    const userData = localStorage.getItem('userData');

    if (token && userData) {
      try {
        setUser(JSON.parse(userData));
      } catch (e) {
        console.error('Failed to parse user data');
      }
    }

    // 드롭다운 외부 클릭 감지
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsDropdownOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const handleLogout = () => {
    // 로그아웃 시 로컬 스토리지 데이터 삭제
    localStorage.removeItem('auth_token');
    localStorage.removeItem('userData');
    setUser(null);
    router.refresh(); // UI 갱신
    router.push('/'); // 홈 페이지로 이동
  };

  const toggleDropdown = () => {
    setIsDropdownOpen(!isDropdownOpen);
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
    <header className="flex items-center justify-between border-b px-6 py-2">
      <div className="w-24">LOGO</div>
      <h1 className="text-2xl font-bold">
        <Link href="/">Blog</Link>
      </h1>
      <div className="flex gap-1 text-right">
        <Link
          href="/subscribe"
          className="my-2 rounded-lg bg-indigo-500 px-4 py-3 text-sm text-white hover:bg-indigo-600"
        >
          Subscribe
        </Link>

        {user ? (
          <div ref={dropdownRef}>
            {/* 프로필 버튼 */}
            <button
              onClick={toggleDropdown}
              className="overflow-hidden rounded-full border-2 border-gray-200 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              {user.profileImage ? (
                <img
                  src={user.profileImage}
                  alt={user.name}
                  className="h-full w-full object-cover"
                />
              ) : (
                <div className="bg-gray-200">
                  <User size={20} className="text-gray-600" />
                </div>
              )}
            </button>

            {/* 드롭다운 메뉴 */}
            {isDropdownOpen && (
              <div className="absolute right-0 top-full z-10 mt-2 w-64 rounded-md bg-white shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none">
                {/* 첫 번째 영역: 프로필 정보 */}
                <div className="border-b border-gray-100 px-4 py-3">
                  <div className="flex items-center">
                    <div className="h-10 w-10 overflow-hidden rounded-full border border-gray-200">
                      {user.profileImage ? (
                        <img
                          src={user.profileImage}
                          alt={user.name}
                          className="h-full w-full object-cover"
                        />
                      ) : (
                        <div className="flex h-full w-full items-center justify-center bg-gray-200">
                          <User size={20} className="text-gray-600" />
                        </div>
                      )}
                    </div>
                    <div className="ml-3 text-left">
                      <div className="text-sm font-medium text-gray-800">{user.name}</div>
                      <div className="text-xs text-gray-500">{user.email}</div>
                    </div>
                  </div>
                </div>

                {/* 두 번째 영역: 메뉴 항목 */}
                <div className="border-b border-gray-100">
                  <Link
                    href="/"
                    className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                    onClick={() => setIsDropdownOpen(false)}
                  >
                    <Home size={16} className="mr-3 text-gray-500" />
                    Home
                  </Link>
                </div>

                {/* 세 번째 영역: 로그아웃 */}
                <div>
                  <button
                    onClick={handleLogout}
                    className="flex w-full items-center px-4 py-2 text-sm text-gray-600 hover:bg-gray-100"
                  >
                    <LogOut size={16} className="mr-3 text-gray-500" />
                    Sign out
                  </button>
                </div>
              </div>
            )}
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
  );
};

export default Header;
