'use client';

import React from 'react';
import Link from 'next/link';
import {
  PlusCircle,
  FileText,
  BarChart2,
  Eye,
  MessageSquare,
  ThumbsUp,
  TrendingUp,
  Home,
  Settings,
} from 'lucide-react';

// 임시 데이터
const mockSession = {
  user: {
    id: 'user-123',
    name: '테스트 사용자',
    email: 'test@example.com',
    image: '/api/placeholder/40/40',
  },
};

const mockStats = {
  totalPosts: 12,
  totalViews: 1024,
  totalComments: 48,
  totalLikes: 95,
  recentPosts: [
    {
      id: '1',
      title: 'Next.js 14 살펴보기',
      views: 142,
      comments: 8,
      likes: 23,
      publishedAt: '2025-03-15',
    },
    {
      id: '2',
      title: 'React Server Components의 장점',
      views: 89,
      comments: 5,
      likes: 14,
      publishedAt: '2025-03-10',
    },
  ],
  viewsChart: [10, 15, 8, 12, 18, 20, 25],
  popularPosts: [
    { id: '1', title: 'Next.js 14 살펴보기', views: 142 },
    { id: '3', title: 'Tailwind CSS 활용 팁', views: 137 },
    { id: '4', title: '효과적인 React 커스텀 훅 만들기', views: 109 },
  ],
};

export default function DashboardPage() {
  // 실제 코드에서는 이 부분이 getServerSession 호출로 대체됩니다
  const session = mockSession;
  const stats = mockStats;

  return (
    <>
      <DashboardHeader user={session.user} />

      <div className="flex">
        <Sidebar />

        <main className="flex-1 p-6">
          <StatsSummary stats={stats} />

          <RecentPostsTable posts={stats.recentPosts} />

          <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
            <ViewsChart data={stats.viewsChart} />
            <PopularPosts posts={stats.popularPosts} />
          </div>
        </main>
      </div>
    </>
  );
}

// 대시보드 헤더 컴포넌트
function DashboardHeader({ user }) {
  return (
    <header className="border-b border-gray-200 bg-white shadow-sm">
      <div className="flex items-center justify-between px-6 py-3">
        <div className="flex items-center">
          <div className="mr-3 flex h-10 w-10 items-center justify-center rounded-full bg-blue-100 text-blue-600">
            {user.name.charAt(0).toUpperCase()}
          </div>
          <div>
            <h1 className="text-xl font-semibold text-gray-800">내 블로그 관리</h1>
            <p className="text-sm text-gray-500">{user.email}</p>
          </div>
        </div>
        <Link href="/" className="text-sm text-blue-600 hover:text-blue-800">
          블로그로 돌아가기
        </Link>
      </div>
    </header>
  );
}

// 사이드바 컴포넌트
function Sidebar() {
  return (
    <div className="min-h-screen w-64 border-r border-gray-200 bg-white shadow-sm">
      <div className="border-b border-gray-200 p-6">
        <h2 className="text-xl font-bold text-gray-800">대시보드</h2>
      </div>

      <nav className="mt-6 px-3">
        <div className="space-y-1">
          <SidebarLink href="/dashboard" icon={<Home size={20} />} text="대시보드" active={true} />
          <SidebarLink href="/dashboard/posts" icon={<FileText size={20} />} text="글 관리" />
          <SidebarLink href="/dashboard/settings" icon={<Settings size={20} />} text="내 설정" />
        </div>
      </nav>
    </div>
  );
}

// 사이드바 링크 컴포넌트
function SidebarLink({ href, icon, text, active = false }) {
  return (
    <Link
      href={href}
      className={`flex items-center rounded-md px-3 py-2 ${
        active ? 'bg-blue-50 text-blue-600' : 'text-gray-600 hover:bg-blue-50 hover:text-blue-600'
      }`}
    >
      <span className="mr-3">{icon}</span>
      <span className="truncate">{text}</span>
    </Link>
  );
}

// 대시보드 헤더 컴포넌트
function DashboardWelcome({ userName }) {
  return (
    <div className="mb-6 flex items-center justify-between">
      <div>
        <h1 className="text-2xl font-bold text-gray-800">대시보드</h1>
        <p className="mt-1 text-sm text-gray-500">
          안녕하세요, {userName}님! 오늘의 블로그 현황입니다.
        </p>
      </div>

      <Link
        href="/dashboard/posts/create"
        className="inline-flex items-center rounded-md bg-blue-600 px-4 py-2 text-white hover:bg-blue-700"
      >
        <PlusCircle size={18} className="mr-1" /> 새 글 작성
      </Link>
    </div>
  );
}

// 통계 요약 컴포넌트
function StatsSummary({ stats }) {
  return (
    <>
      <DashboardWelcome userName={mockSession.user.name} />

      <div className="mb-6 grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-4">
        <StatCard
          icon={<FileText size={24} className="text-blue-500" />}
          title="총 게시글"
          value={stats.totalPosts}
          trend="+2 지난 주"
          trendUp={true}
        />

        <StatCard
          icon={<Eye size={24} className="text-green-500" />}
          title="총 조회수"
          value={stats.totalViews}
          trend="+15% 지난 달"
          trendUp={true}
        />

        <StatCard
          icon={<MessageSquare size={24} className="text-purple-500" />}
          title="총 댓글"
          value={stats.totalComments}
          trend="+8 지난 달"
          trendUp={true}
        />

        <StatCard
          icon={<ThumbsUp size={24} className="text-red-500" />}
          title="총 좋아요"
          value={stats.totalLikes}
          trend="+12 지난 달"
          trendUp={true}
        />
      </div>
    </>
  );
}

// 통계 카드 컴포넌트
function StatCard({ icon, title, value, trend, trendUp }) {
  return (
    <div className="rounded-lg bg-white p-6 shadow">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm font-medium text-gray-500">{title}</p>
          <p className="mt-1 text-2xl font-bold text-gray-800">{value}</p>
        </div>
        <div>{icon}</div>
      </div>
      <div
        className={`mt-3 flex items-center text-sm ${trendUp ? 'text-green-600' : 'text-red-600'}`}
      >
        <TrendingUp size={16} className="mr-1" />
        <span>{trend}</span>
      </div>
    </div>
  );
}

// 최근 작성한 글 테이블 컴포넌트
function RecentPostsTable({ posts }) {
  return (
    <div className="mb-6 rounded-lg bg-white p-6 shadow">
      <div className="mb-4 flex items-center justify-between">
        <h2 className="text-lg font-semibold text-gray-800">최근 작성한 글</h2>
        <Link href="/dashboard/posts" className="text-sm text-blue-600 hover:text-blue-800">
          모든 글 보기
        </Link>
      </div>

      <div className="overflow-x-auto">
        <table className="min-w-full">
          <thead>
            <tr className="border-b border-gray-200">
              <th className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                제목
              </th>
              <th className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                작성일
              </th>
              <th className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                조회수
              </th>
              <th className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                댓글
              </th>
              <th className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                좋아요
              </th>
            </tr>
          </thead>
          <tbody>
            {posts.map((post) => (
              <tr key={post.id} className="hover:bg-gray-50">
                <td className="px-4 py-3">
                  <Link
                    href={`/dashboard/posts/${post.id}/edit`}
                    className="text-blue-600 hover:text-blue-800"
                  >
                    {post.title}
                  </Link>
                </td>
                <td className="px-4 py-3 text-gray-500">{post.publishedAt}</td>
                <td className="px-4 py-3 text-gray-500">{post.views}</td>
                <td className="px-4 py-3 text-gray-500">{post.comments}</td>
                <td className="px-4 py-3 text-gray-500">{post.likes}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

// 조회수 추이 그래프 컴포넌트
function ViewsChart({ data }) {
  return (
    <div className="rounded-lg bg-white p-6 shadow">
      <h2 className="mb-4 text-lg font-semibold text-gray-800">조회수 추이</h2>
      <div className="flex h-64 items-end space-x-2">
        {data.map((value, index) => (
          <ChartBar
            key={index}
            value={value}
            maxValue={Math.max(...data)}
            label={`${index + 1}일`}
          />
        ))}
      </div>
    </div>
  );
}

// 차트 막대 컴포넌트
function ChartBar({ value, maxValue, label }) {
  const height = `${(value / maxValue) * 100}%`;

  return (
    <div className="flex flex-1 flex-col items-center">
      <div className="w-full rounded-t bg-blue-500" style={{ height }}></div>
      <div className="mt-1 text-xs text-gray-500">{label}</div>
    </div>
  );
}

// 인기있는 글 컴포넌트
function PopularPosts({ posts }) {
  return (
    <div className="rounded-lg bg-white p-6 shadow">
      <h2 className="mb-4 text-lg font-semibold text-gray-800">인기있는 글</h2>
      <div className="space-y-4">
        {posts.map((post, index) => (
          <PopularPostItem key={post.id} post={post} ranking={index + 1} />
        ))}
      </div>
    </div>
  );
}

// 인기 게시물 아이템 컴포넌트
function PopularPostItem({ post, ranking }) {
  return (
    <div className="flex items-center">
      <div className="mr-3 flex h-8 w-8 flex-shrink-0 items-center justify-center rounded-full bg-gray-100 font-semibold text-gray-700">
        {ranking}
      </div>
      <div className="min-w-0 flex-1">
        <p className="truncate text-sm font-medium text-gray-900">{post.title}</p>
        <p className="text-xs text-gray-500">{post.views} 조회</p>
      </div>
    </div>
  );
}
