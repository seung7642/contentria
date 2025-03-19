'use client';

import { useState } from 'react';
import {
  BarChart2,
  TrendingUp,
  Users,
  Eye,
  FileText,
  MessageSquare,
  ThumbsUp,
  ArrowRight,
} from 'lucide-react';
import Link from 'next/link';

// 통계 카드 컴포넌트
const StatCard = ({ icon, title, value, trend, trendUp }) => (
  <div className="rounded-lg bg-white p-5 shadow-sm">
    <div className="flex items-start justify-between">
      <div>
        <p className="text-sm font-medium text-gray-500">{title}</p>
        <p className="mt-1 text-2xl font-bold text-gray-800">{value}</p>
      </div>
      <div className="rounded-full bg-indigo-50 p-3 text-indigo-600">{icon}</div>
    </div>
    <div
      className={`mt-3 flex items-center text-sm ${trendUp ? 'text-green-600' : 'text-red-600'}`}
    >
      <TrendingUp size={16} className="mr-1" />
      <span>{trend}</span>
    </div>
  </div>
);

// 트래픽 차트 컴포넌트 (간단한 막대 그래프)
const TrafficChart = () => {
  const data = [35, 55, 41, 67, 22, 43, 21, 33, 45, 31, 87, 65, 35];
  const maxValue = Math.max(...data);

  return (
    <div className="h-64 w-full">
      <div className="flex h-[90%] items-end space-x-1">
        {data.map((value, index) => (
          <div
            key={index}
            className="relative flex-1 self-end"
            style={{ height: `${(value / maxValue) * 100}%` }}
          >
            <div
              className="absolute inset-x-0 bottom-0 rounded-t bg-indigo-500 hover:bg-indigo-600"
              style={{ height: '100%' }}
            />
          </div>
        ))}
      </div>
      <div className="mt-2 flex justify-between text-xs text-gray-500">
        <span>3월 1일</span>
        <span>3월 15일</span>
      </div>
    </div>
  );
};

// 인기 게시물 아이템
const PopularPostItem = ({ title, views, badge }) => (
  <div className="flex items-center border-b border-gray-100 py-3">
    <div className="mr-2 flex-shrink-0">
      <div className="flex h-10 w-10 items-center justify-center rounded-full bg-indigo-100 text-indigo-600">
        {badge}
      </div>
    </div>
    <div className="flex-1 truncate">
      <p className="truncate font-medium">{title}</p>
      <p className="text-xs text-gray-500">{views} 조회</p>
    </div>
  </div>
);

// 최근 수익 아이템
const RecentRevenueItem = ({ source, amount, date }) => (
  <div className="flex justify-between border-b border-gray-100 py-3">
    <div>
      <p className="font-medium">{source}</p>
      <p className="text-xs text-gray-500">{date}</p>
    </div>
    <p className="font-semibold text-green-600">{amount}</p>
  </div>
);

export default function DashboardPage() {
  const [userName] = useState('관리자');

  // 실제 구현에서는 이 부분이 데이터 fetching으로 대체됩니다
  const stats = {
    totalPosts: 24,
    totalViews: 3824,
    totalComments: 128,
    totalLikes: 256,
  };

  return (
    <div className="space-y-6">
      {/* 환영 헤더 */}
      <div className="mb-6 flex flex-col justify-between space-y-4 sm:flex-row sm:items-center sm:space-y-0">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">대시보드</h1>
          <p className="mt-1 text-sm text-gray-500">
            안녕하세요, {userName}님! 오늘의 블로그 현황입니다.
          </p>
        </div>
        <Link
          href="/dashboard/posts/create"
          className="inline-flex items-center rounded-lg bg-indigo-600 px-4 py-2 text-white transition hover:bg-indigo-700"
        >
          새 글 작성
        </Link>
      </div>

      {/* 통계 카드 */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard
          icon={<FileText size={24} />}
          title="총 게시글"
          value={stats.totalPosts}
          trend="+3 지난 주"
          trendUp={true}
        />
        <StatCard
          icon={<Eye size={24} />}
          title="총 조회수"
          value={stats.totalViews}
          trend="+12% 지난 달"
          trendUp={true}
        />
        <StatCard
          icon={<MessageSquare size={24} />}
          title="총 댓글"
          value={stats.totalComments}
          trend="+8% 지난 달"
          trendUp={true}
        />
        <StatCard
          icon={<ThumbsUp size={24} />}
          title="총 좋아요"
          value={stats.totalLikes}
          trend="-3% 지난 달"
          trendUp={false}
        />
      </div>

      {/* 콘텐츠 그리드 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {/* 트래픽 차트 */}
        <div className="rounded-lg bg-white p-5 shadow-sm">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-lg font-semibold">트래픽 현황</h2>
            <select className="rounded-md border border-gray-300 px-2 py-1 text-sm">
              <option>지난 2주</option>
              <option>지난 30일</option>
              <option>지난 90일</option>
            </select>
          </div>
          <TrafficChart />
        </div>

        {/* 인기 글 */}
        <div className="rounded-lg bg-white p-5 shadow-sm">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-lg font-semibold">인기 게시글</h2>
            <Link href="/dashboard/posts" className="text-sm text-indigo-600 hover:text-indigo-800">
              전체보기
            </Link>
          </div>
          <div className="space-y-1">
            <PopularPostItem title="Next.js 14와 React Server Components" views="152" badge="1" />
            <PopularPostItem title="효율적인 상태 관리 전략" views="128" badge="2" />
            <PopularPostItem title="모바일 최적화를 위한 디자인 팁" views="98" badge="3" />
          </div>
          <Link
            href="/dashboard/posts"
            className="mt-4 flex items-center justify-center text-sm text-indigo-600 hover:text-indigo-800"
          >
            모든 게시글 보기 <ArrowRight size={16} className="ml-1" />
          </Link>
        </div>

        {/* 최근 수익 */}
        <div className="rounded-lg bg-white p-5 shadow-sm">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-lg font-semibold">최근 수익</h2>
            <Link
              href="/dashboard/revenue"
              className="text-sm text-indigo-600 hover:text-indigo-800"
            >
              수익 관리
            </Link>
          </div>
          <div>
            <RecentRevenueItem source="Google AdSense" amount="₩15,200" date="2024-03-15" />
            <RecentRevenueItem source="제휴 마케팅" amount="₩8,500" date="2024-03-10" />
            <RecentRevenueItem source="후원" amount="₩5,000" date="2024-03-08" />
          </div>
          <div className="mt-4 flex justify-between">
            <span className="font-medium">이번 달 총액</span>
            <span className="font-semibold text-green-600">₩28,700</span>
          </div>
        </div>

        {/* 빠른 작업 */}
        <div className="rounded-lg bg-white p-5 shadow-sm">
          <h2 className="mb-4 text-lg font-semibold">빠른 작업</h2>
          <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
            <Link
              href="/dashboard/posts/create"
              className="flex items-center rounded-lg border border-gray-200 p-3 transition hover:bg-gray-50"
            >
              <div className="mr-3 rounded-full bg-indigo-100 p-2 text-indigo-600">
                <FileText size={18} />
              </div>
              <span>새 글 작성</span>
            </Link>
            <Link
              href="/dashboard/ads"
              className="flex items-center rounded-lg border border-gray-200 p-3 transition hover:bg-gray-50"
            >
              <div className="mr-3 rounded-full bg-green-100 p-2 text-green-600">
                <BarChart2 size={18} />
              </div>
              <span>광고 관리</span>
            </Link>
            <Link
              href="/dashboard/posts"
              className="flex items-center rounded-lg border border-gray-200 p-3 transition hover:bg-gray-50"
            >
              <div className="mr-3 rounded-full bg-blue-100 p-2 text-blue-600">
                <Eye size={18} />
              </div>
              <span>게시글 관리</span>
            </Link>
            <Link
              href="/dashboard/revenue"
              className="flex items-center rounded-lg border border-gray-200 p-3 transition hover:bg-gray-50"
            >
              <div className="mr-3 rounded-full bg-purple-100 p-2 text-purple-600">
                <Users size={18} />
              </div>
              <span>수익 분석</span>
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
