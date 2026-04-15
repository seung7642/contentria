'use client';

import { useState, useRef, useEffect, useCallback } from 'react';
import Link from 'next/link';
import { Search, Home } from 'lucide-react';
import { useRouter } from 'next/navigation';
import UserAvatar from './header/UserAvatar';
import ProfileDropdown from './header/ProfileDropdown';
import { useUserProfile } from '@/hooks/queries/useUserQuery';
import { useQueryClient } from '@tanstack/react-query';
import { userKeys } from '@/hooks/queries/keys';
import { logoutAction } from '@/actions/auth';
import { PATHS } from '@/constants/paths';

interface DashboardHeaderProps {
  blogSlug: string | null;
}

export default function DashboardHeader({ blogSlug }: DashboardHeaderProps) {
  const isBlogLinkActive = blogSlug !== null;
  const { data: user } = useUserProfile();
  const queryClient = useQueryClient();
  const router = useRouter();

  const [isSearchVisible, setIsSearchVisible] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);

  const profileRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        profileRef.current &&
        !(profileRef.current as HTMLElement).contains(event.target as Node)
      ) {
        setIsProfileOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleLogout = useCallback(async () => {
    queryClient.setQueryData(userKeys.profile(), null);
    setIsProfileOpen(false);

    try {
      await logoutAction();
      router.replace(PATHS.HOME);
    } catch (e) {
      console.error('Logout error:', e);
      router.replace(PATHS.HOME);
    }
  }, [queryClient, router]);

  return (
    <header className="sticky top-0 z-10 w-full border-b bg-white shadow-sm">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-3">
        {/* 왼쪽 영역: 블로그 제목 & 검색 전환 */}
        <div className="flex items-center md:w-64">
          <Link href="/dashboard" className="text-xl font-bold md:hidden">
            Contentria
          </Link>

          {/* <div className={`relative ${isSearchVisible ? 'block' : 'hidden'} md:block`}>
            <div className="absolute inset-y-0 left-0 flex items-center pl-3">
              <Search size={18} className="text-gray-400" />
            </div>
            <input
              type="text"
              placeholder="검색..."
              className="w-full rounded-lg border border-gray-300 py-2 pl-10 pr-4 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500 md:w-64"
            />
          </div> */}

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
        <h1 className="hidden text-2xl font-bold md:block">Contentria</h1>

        {/* 오른쪽 영역: 버튼 및 프로필 */}
        <div className="flex items-center space-x-2">
          {/* 블로그로 돌아가기 버튼 */}
          <Link
            href={blogSlug ? `/@${blogSlug}` : '/dashboard'}
            target={isBlogLinkActive ? '_blank' : '_self'}
            className="mx-2 hidden items-center rounded-lg bg-white px-4 py-2 text-sm text-gray-800 hover:bg-gray-100 md:flex"
          >
            <Home size={18} className="mr-2" />
            블로그로 돌아가기
          </Link>

          {/* 알림 버튼 */}
          {/* <div className="relative" ref={notificationRef}>
            <button
              onClick={() => setIsNotificationOpen(!isNotificationOpen)}
              className="relative rounded-full p-2 hover:bg-gray-100"
              aria-label={`알림 ${notifications.length > 0 ? '있음' : '없음'}`}
              aria-haspopup="true"
              aria-expanded={isNotificationOpen}
            >
              <Bell size={20} />
              {notifications.length > 0 && (
                <span className="absolute right-1 top-1 h-2 w-2 rounded-full bg-red-500"></span>
              )}
            </button>

            {isNotificationOpen && <NotificationDropdown notifications={notifications} />}
          </div> */}

          {/* 프로필 드롭다운 */}
          <div className="relative" ref={profileRef}>
            <button
              onClick={() => setIsProfileOpen(!isProfileOpen)}
              className="relative flex h-10 w-10 items-center justify-center overflow-hidden rounded-full border-2 border-gray-200 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <UserAvatar user={user} size={36} />
            </button>

            {isProfileOpen && (
              <ProfileDropdown
                user={user}
                onClose={() => setIsProfileOpen(false)}
                onLogout={handleLogout}
              />
            )}
          </div>
        </div>
      </div>
    </header>
  );
}
