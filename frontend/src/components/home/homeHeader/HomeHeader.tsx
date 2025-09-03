'use client';

import Link from 'next/link';
import { useState, useEffect, useRef } from 'react';

import ProfileDropdown from './ProfileDropdown';
import UserAvatar from './UserAvatar';
import { useAuthStore } from '@/store/authStore';
import { PATHS } from '@/constants/paths';

const HomeHeader = () => {
  const { user, isAuthenticated, logout } = useAuthStore();

  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  return (
    <header className="border-b shadow-sm">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-3">
        <div className="w-24"></div>
        <h1 className="text-2xl font-bold">
          <Link href="/">Blog</Link>
        </h1>

        <div className="flex items-center space-x-2">
          <div>
            {isAuthenticated && user ? (
              <div ref={dropdownRef} className="relative">
                <button
                  type="button"
                  className="focus-ring-2 rounded-full transition-opacity duration-200 hover:opacity-90 focus:outline-none focus:ring-indigo-500 focus:ring-offset-2"
                  onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                >
                  <UserAvatar user={user} size="md" />
                </button>
                <ProfileDropdown
                  user={user}
                  isOpen={isDropdownOpen}
                  onClose={() => setIsDropdownOpen(false)}
                  onLogout={logout}
                />
              </div>
            ) : (
              <Link
                href={PATHS.LOGIN}
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
