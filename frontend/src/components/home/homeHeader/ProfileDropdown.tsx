import { useRef } from 'react';
import Link from 'next/link';
import { Home, LayoutDashboard, LogOut, User } from 'lucide-react';
import { ProfileDropdownProps } from './types';

const ProfileDropdown = ({ user, isOpen, onClose, onLogout }: ProfileDropdownProps) => {
  const dropdownRef = useRef<HTMLDivElement>(null);

  if (!isOpen) {
    return null;
  }

  return (
    <div
      ref={dropdownRef}
      className="absolute right-0 z-10 mt-2 w-64 rounded-md bg-white shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none"
    >
      {/* 프로필 정보 */}
      <div className="border-b border-gray-100 px-4 py-3">
        <div className="flex items-center">
          <div className="h-10 w-10 overflow-hidden rounded-full border border-gray-200">
            {user.profileImage ? (
              <img src={user.profileImage} alt={user.name} className="h-full w-full object-cover" />
            ) : (
              <div className="flex h-full w-full items-center justify-center bg-gray-200">
                <User size={20} classNametext-gray-600 />
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
          href="/"
          className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
          onClick={onClose}
        >
          <Home size={16} className="mr-3 text-gray-500" />
          Home
        </Link>

        <Link
          href="/dashboard"
          className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
          onClick={onClose}
        >
          <LayoutDashboard size={16} className="mr-3 text-gray-500" />
          Dashboard
        </Link>
      </div>

      {/* 로그아웃 */}
      <div>
        <button
          onClick={onLogout}
          className="flex w-full items-center px-4 py-2 text-sm text-gray-600 hover:bg-gray-100"
        >
          <LogOut size={16} className="mr-3 text-gray-500" />
          Sign out
        </button>
      </div>
    </div>
  );
};

export default ProfileDropdown;
