'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useState } from 'react';
import { LayoutDashboard, FileText, Store, BarChart4, Settings, Menu, X, Home } from 'lucide-react';

const DashboardSidebar = () => {
  const pathname = usePathname();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const navItems = [
    { path: '/dashboard', label: '대시보드', icon: <LayoutDashboard size={20} /> },
    { path: '/dashboard/posts', label: '글 관리', icon: <FileText size={20} /> },
    { path: '/dashboard/ads', label: '광고 관리', icon: <Store size={20} /> },
    { path: '/dashboard/revenue', label: '수익 관리', icon: <BarChart4 size={20} /> },
    { path: '/dashboard/settings', label: '설정', icon: <Settings size={20} /> },
  ];

  return (
    <>
      {/* 모바일 메뉴 토글 버튼 */}
      <button
        onClick={() => setIsMobileMenuOpen(true)}
        className="fixed left-4 top-4 z-30 rounded-md bg-indigo-600 p-2 text-white md:hidden"
      >
        <Menu size={20} />
      </button>

      {/* 오버레이 */}
      {isMobileMenuOpen && (
        <div
          className="fixed inset-0 z-40 bg-black bg-opacity-50 md:hidden"
          onClick={() => setIsMobileMenuOpen(false)}
        ></div>
      )}

      {/* 사이드바 */}
      <aside
        className={`fixed inset-y-0 left-0 z-50 w-64 transform bg-white shadow-lg transition-transform duration-300 ease-in-out md:static md:translate-x-0 ${
          isMobileMenuOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <div className="flex h-16 items-center justify-between border-b px-4">
          <Link href="/dashboard" className="text-xl font-bold text-indigo-600">
            블로그 관리
          </Link>
          <button
            onClick={() => setIsMobileMenuOpen(false)}
            className="rounded p-1 hover:bg-gray-100 md:hidden"
          >
            <X size={20} />
          </button>
        </div>

        <div className="p-4">
          <Link
            href="/"
            className="flex items-center rounded-lg px-3 py-2 text-gray-600 hover:bg-gray-100"
          >
            <Home size={20} className="mr-3" />
            <span>블로그로 돌아가기</span>
          </Link>
        </div>

        <nav className="mt-2 px-3">
          <p className="mb-2 px-3 text-xs font-semibold uppercase text-gray-500">관리 메뉴</p>
          <ul className="space-y-1">
            {navItems.map((item) => (
              <li key={item.path}>
                <Link
                  href={item.path}
                  className={`flex items-center rounded-lg px-3 py-2 ${
                    pathname === item.path
                      ? 'bg-indigo-50 font-medium text-indigo-600'
                      : 'text-gray-600 hover:bg-gray-100'
                  }`}
                >
                  <span className="mr-3">{item.icon}</span>
                  <span>{item.label}</span>
                </Link>
              </li>
            ))}
          </ul>
        </nav>
      </aside>
    </>
  );
};

export default DashboardSidebar;
