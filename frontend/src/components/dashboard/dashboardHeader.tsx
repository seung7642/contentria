'use client';

import { useState, useRef, useEffect } from 'react';
import Link from 'next/link';
import { Search, Bell, LogOut, User, Home, Settings } from 'lucide-react';

const DashboardHeader = () => {
  const [isSearchVisible, setIsSearchVisible] = useState(false);
  const [isNotificationOpen, setIsNotificationOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const notificationRef = useRef(null);
  const profileRef = useRef(null);

  // 가상의 사용자 데이터 - 실제로는 서버에서 가져오거나 context에서 관리할 것
  const user = {
    name: '홍길동',
    email: 'user@example.com',
    profileImage: null, // 프로필 이미지가 없는 경우
  };

  // 알림 목록 - 실제로는 API에서 가져올 것
  const notifications = [
    { id: 1, text: '새 댓글이 달렸습니다.', time: '방금 전' },
    { id: 2, text: '광고 수익이 입금되었습니다.', time: '3시간 전' },
    { id: 3, text: '블로그 방문자가 증가했습니다.', time: '어제' },
  ];

  // 외부 클릭 감지
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (notificationRef.current && !notificationRef.current.contains(event.target)) {
        setIsNotificationOpen(false);
      }
      if (profileRef.current && !profileRef.current.contains(event.target)) {
        setIsProfileOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  return (
    <header className="sticky top-0 z-10 w-full border-b bg-white shadow-sm">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-3">
        {/* 왼쪽 영역: 블로그 제목 & 검색 전환 */}
        <div className="flex items-center md:w-64">
          <Link href="/dashboard" className="text-xl font-bold md:hidden">
            Blog
          </Link>

          <div className={`relative ${isSearchVisible ? 'block' : 'hidden'} md:block`}>
            <div className="absolute inset-y-0 left-0 flex items-center pl-3">
              <Search size={18} className="text-gray-400" />
            </div>
            <input
              type="text"
              placeholder="검색..."
              className="w-full rounded-lg border border-gray-300 py-2 pl-10 pr-4 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500 md:w-64"
            />
          </div>

          {!isSearchVisible && (
            <button
              onClick={() => setIsSearchVisible(true)}
              className="rounded-full p-2 hover:bg-gray-100 md:hidden"
            >
              <Search size={20} />
            </button>
          )}
        </div>

        {/* 중앙 영역: 블로그 제목 (데스크톱에서만) */}
        <h1 className="hidden text-2xl font-bold md:block">Blog</h1>

        {/* 오른쪽 영역: 버튼 및 프로필 */}
        <div className="flex items-center space-x-2">
          {/* 블로그로 돌아가기 버튼 */}
          <Link
            href="/"
            className="mx-2 hidden items-center rounded-lg bg-white px-4 py-2 text-sm text-gray-800 hover:bg-gray-100 md:flex"
          >
            <Home size={18} className="mr-2" />
            블로그로 돌아가기
          </Link>

          {/* 알림 버튼 */}
          <div className="relative" ref={notificationRef}>
            <button
              onClick={() => setIsNotificationOpen(!isNotificationOpen)}
              className="relative rounded-full p-2 hover:bg-gray-100"
            >
              <Bell size={20} />
              <span className="absolute right-1 top-1 h-2 w-2 rounded-full bg-red-500"></span>
            </button>

            {isNotificationOpen && (
              <div className="absolute right-0 mt-2 w-80 rounded-lg border bg-white py-2 shadow-lg">
                <div className="border-b px-4 py-2">
                  <h3 className="font-medium">알림</h3>
                </div>
                <div className="max-h-96 overflow-auto">
                  {notifications.map((notification) => (
                    <div key={notification.id} className="border-b px-4 py-3 hover:bg-gray-50">
                      <p className="text-sm">{notification.text}</p>
                      <p className="text-xs text-gray-500">{notification.time}</p>
                    </div>
                  ))}
                </div>
                <div className="px-4 py-2 text-center">
                  <button className="text-sm text-indigo-600 hover:text-indigo-800">
                    모든 알림 보기
                  </button>
                </div>
              </div>
            )}
          </div>

          {/* 프로필 드롭다운 */}
          <div className="relative" ref={profileRef}>
            <button
              onClick={() => setIsProfileOpen(!isProfileOpen)}
              className="flex h-10 w-10 items-center justify-center overflow-hidden rounded-full border-2 border-gray-200 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              {user.profileImage ? (
                <img
                  src={user.profileImage}
                  alt={user.name}
                  className="h-full w-full object-cover"
                />
              ) : (
                <div className="flex h-full w-full items-center justify-center bg-gray-200">
                  <User size={24} className="text-gray-600" />
                </div>
              )}
            </button>

            {isProfileOpen && (
              <div className="absolute right-0 z-10 mt-2 w-64 rounded-md bg-white shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none">
                {/* 프로필 정보 */}
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

                {/* 메뉴 항목 */}
                <div className="border-b border-gray-100">
                  <Link
                    href="/dashboard/settings"
                    className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                    onClick={() => setIsProfileOpen(false)}
                  >
                    <Settings size={16} className="mr-3 text-gray-500" />
                    계정 설정
                  </Link>
                  <Link
                    href="/"
                    className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                    onClick={() => setIsProfileOpen(false)}
                  >
                    <Home size={16} className="mr-3 text-gray-500" />
                    블로그로 이동
                  </Link>
                </div>

                {/* 로그아웃 */}
                <div>
                  <button
                    onClick={() => {
                      // 로그아웃 로직
                      setIsProfileOpen(false);
                    }}
                    className="flex w-full items-center px-4 py-2 text-sm text-gray-600 hover:bg-gray-100"
                  >
                    <LogOut size={16} className="mr-3 text-gray-500" />
                    로그아웃
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

export default DashboardHeader;
