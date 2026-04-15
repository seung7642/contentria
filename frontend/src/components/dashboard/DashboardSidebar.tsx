'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useState } from 'react';
import { LayoutDashboard, FileText, Settings, Menu, X, Home, FolderTree } from 'lucide-react';
import { useUserProfile } from '@/hooks/queries/useUserQuery';
import SidebarMenuItem from './DashboardSidebarMenuItem';

import { BlogInfo } from '@/types/api/blogs';

interface DashboardSidebarProps {
  blogInfos: BlogInfo[] | null;
}

export default function DashboardSidebar({ blogInfos }: DashboardSidebarProps) {
  const firstBlogSlug = blogInfos && blogInfos.length > 0 ? blogInfos[0].slug : null;
  const isBlogLinkActive = firstBlogSlug !== null;
  const pathname = usePathname();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const { isPending: isUserLoading } = useUserProfile();
  const hasBlog = (blogInfos?.length ?? 0) > 0 ? true : false;

  const navItems = [
    {
      path: '/dashboard',
      label: '대시보드',
      icon: <LayoutDashboard size={20} />,
      requiresBlog: false,
    },
    {
      path: '/dashboard/posts',
      label: '글 관리',
      icon: <FileText size={20} />,
      requiresBlog: true,
    },
    {
      path: '/dashboard/categories',
      label: '카테고리 관리',
      icon: <FolderTree size={20} />,
      requiresBlog: true,
    },
    // { path: '/dashboard/ads', label: '광고 관리', icon: <Store size={20} /> },
    // { path: '/dashboard/revenue', label: '수익 관리', icon: <BarChart4 size={20} /> },
    {
      path: '/dashboard/settings',
      label: '설정',
      icon: <Settings size={20} />,
      requiresBlog: true,
    },
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
            href={firstBlogSlug ? `/@${firstBlogSlug}` : '/dashboard'}
            target={isBlogLinkActive ? '_blank' : '_self'}
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
                <SidebarMenuItem
                  path={item.path}
                  label={item.label}
                  icon={item.icon}
                  isActive={pathname === item.path}
                  disabled={isUserLoading ? item.requiresBlog : item.requiresBlog && !hasBlog}
                />
              </li>
            ))}
          </ul>
        </nav>
      </aside>
    </>
  );
}
