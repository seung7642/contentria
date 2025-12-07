'use client';

import {
  useDashboadStatsQuery,
  usePopularPostsQuery,
  useTrafficChartQuery,
} from '@/hooks/queries/useDashboardQueries';
import { useUserProfile } from '@/hooks/queries/useUserQuery';
import Link from 'next/link';
import { useState } from 'react';
import PopularPostList from './PopularPostList';
import { ArrowRight, Eye, FileText, Loader2, MessageSquare, ThumbsUp } from 'lucide-react';
import TrafficChart from './TrafficChart';
import StatCard from './StatCard';
import { TimeRange } from '@/types/api/dashboard';

// const revenueData: RevenueItem[] = [
//   { source: 'Google AdSense', amount: '₩15,200', date: '2024-03-15' },
//   { source: '제휴 마케팅', amount: '₩8,500', date: '2024-03-10' },
//   { source: '후원', amount: '₩5,000', date: '2024-03-08' },
// ];

const DashboardContent = () => {
  const [timeRange, setTimeRange] = useState<TimeRange>('2weeks');
  const { data: user } = useUserProfile();
  const slug = user?.slugs?.[0];

  const { data: stats, isLoading: isStatsLoading } = useDashboadStatsQuery(slug!);
  const { data: popularPosts, isLoading: isPostsLoading } = usePopularPostsQuery(slug!);
  const { data: trafficData, isLoading: isTrafficLoading } = useTrafficChartQuery(slug!, timeRange);

  return (
    <div className="space-y-6">
      {/* 환영 헤더 */}
      <div className="mb-6 flex flex-col justify-between space-y-4 sm:flex-row sm:items-center sm:space-y-0">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">대시보드</h1>
          <p className="mt-1 text-sm text-gray-500">
            안녕하세요, {user?.name || '관리자'}님! 오늘의 블로그 현황입니다.
          </p>
        </div>
        <Link
          href="/dashboard/posts/new"
          className="inline-flex items-center rounded-lg bg-indigo-600 px-4 py-2 text-white transition hover:bg-indigo-700"
        >
          새 글 작성
        </Link>
      </div>

      {/* 통계 카드 */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        {isStatsLoading ? (
          Array.from({ length: 4 }).map((_, i) => (
            <div key={i} className="h-32 w-full animate-pulse rounded-lg bg-gray-200" />
          ))
        ) : (
          <>
            <StatCard
              icon={<FileText size={24} />}
              title="오늘 방문자 수"
              value={stats?.todayVisitors ?? 0}
              trend="+3 지난 주"
              trendUp={true}
            />
            <StatCard
              icon={<Eye size={24} />}
              title="최근 7일간 방문자 수"
              value={stats?.weekVisitors ?? 0}
              trend="+12% 지난 달"
              trendUp={true}
            />
            <StatCard
              icon={<MessageSquare size={24} />}
              title="최근 7일간 새 댓글 수"
              value={stats?.weekNewComments ?? 0}
              trend="+8% 지난 달"
              trendUp={true}
            />
            <StatCard
              icon={<ThumbsUp size={24} />}
              title="전체 구독자 수"
              value={stats?.totalSubscribers ?? 0}
              trend="-3% 지난 달"
              trendUp={false}
            />
          </>
        )}
      </div>

      {/* 콘텐츠 그리드 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {/* 트래픽 차트 */}
        <div className="rounded-lg bg-white p-5 shadow-sm">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-lg font-semibold">트래픽 현황</h2>
            <select
              className="rounded-md border border-gray-300 px-2 py-1 text-sm"
              value={timeRange}
              onChange={(e) => setTimeRange(e.target.value as TimeRange)}
            >
              <option value="2weeks">지난 2주</option>
              <option value="30days">지난 30일</option>
              <option value="90days">지난 90일</option>
            </select>
          </div>
          {isTrafficLoading ? (
            <div className="flex h-[250px] items-center justify-center">
              <Loader2 className="h-8 w-8 animate-spin text-indigo-500" />
            </div>
          ) : (
            <TrafficChart data={trafficData || []} />
          )}
        </div>

        {/* 인기 글 */}
        <div className="rounded-lg bg-white p-5 shadow-sm">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-lg font-semibold">인기 게시글</h2>
            <Link href="/dashboard/posts" className="text-sm text-indigo-600 hover:text-indigo-800">
              전체보기
            </Link>
          </div>
          {isPostsLoading ? (
            <div className="flex h-[200px] items-center justify-center">
              <Loader2 className="h-8 w-8 animate-spin text-indigo-500" />
            </div>
          ) : (
            <>
              <PopularPostList posts={popularPosts || []} />
              <Link
                href="/dashboard/posts"
                className="mt-4 flex items-center justify-center text-sm text-indigo-600 hover:text-indigo-800"
              >
                모든 게시글 보기 <ArrowRight size={16} className="ml-1" />
              </Link>
            </>
          )}
        </div>

        {/* 최근 수익 */}
        {/* <div className="rounded-lg bg-white p-5 shadow-sm">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-lg font-semibold">최근 수익</h2>
            <Link
              href="/dashboard/revenue"
              className="text-sm text-indigo-600 hover:text-indigo-800"
            >
              수익 관리
            </Link>
          </div>
          <RevenueList items={revenueData} />
          <div className="mt-4 flex justify-between">
            <span className="font-medium">이번 달 총액</span>
            <span className="font-semibold text-green-600">₩28,700</span>
          </div>
        </div> */}
      </div>
    </div>
  );
};

export default DashboardContent;
