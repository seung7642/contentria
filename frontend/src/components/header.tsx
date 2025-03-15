'use client';

import Link from 'next/link';
import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation'; // App Router 사용

const Header = () => {
  const [user, setUser] = useState(null);
  const [mounted, setMounted] = useState(false);
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
  }, []);

  const handleLogout = () => {
    // 로그아웃 시 로컬 스토리지 데이터 삭제
    localStorage.removeItem('auth_token');
    localStorage.removeItem('userData');
    setUser(null);
    router.refresh(); // UI 갱신
    router.push('/'); // 홈 페이지로 이동
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
    <header className="flex items-center justify-between border-b px-6 py-5">
      <div className="w-24"></div>
      <h1 className="text-2xl font-bold">
        <Link href="/">Blog</Link>
      </h1>
      <div className="text-right">
        <Link
          href="/subscribe"
          className="mx-2 my-2 rounded-lg bg-indigo-500 px-4 py-3 text-sm text-white hover:bg-indigo-600"
        >
          Subscribe
        </Link>

        {user ? (
          <div className="inline-flex items-center">
            <span className="mr-3 font-medium">Welcome, {user.name}</span>
            <button
              onClick={handleLogout}
              className="my-2 rounded-lg bg-white px-4 py-3 text-sm text-gray-800 hover:bg-gray-200"
            >
              Sign out
            </button>
          </div>
        ) : (
          <Link
            href="/login"
            className="my-2 rounded-lg bg-white px-4 py-3 text-sm text-gray-800 hover:bg-gray-200"
          >
            Sign in
          </Link>
        )}
      </div>
    </header>
  );
};

export default Header;
