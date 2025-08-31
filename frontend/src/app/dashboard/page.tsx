'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import {
  FileText,
  Eye,
  MessageSquare,
  ThumbsUp,
  ArrowRight,
  BarChart2,
  Users,
  Loader2,
} from 'lucide-react';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts';

// 컴포넌트 분리
import StatCard from '@/components/dashboard/StatCard';
import PopularPostList from '@/components/dashboard/PopularPostList';
import RevenueList from '@/components/dashboard/RevenueList';
import QuickActions from '@/components/dashboard/QuickActions';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/store/authStore';
import { DEFAULT_LOGGED_OUT_REDIRECT_URL } from '@/constants/auth';
import { userService } from '@/services/userService';

// 타입 정의
type TimeRange = '2weeks' | '30days' | '90days';
type ChartData = {
  date: string;
  visitors: number;
};

type ChartDataMap = {
  [key in TimeRange]: ChartData[];
};

// 차트 컴포넌트 타입 정의
interface TrafficChartProps {
  timeRange: TimeRange;
}

// 트래픽 차트 컴포넌트 (꺾은선 그래프)
const TrafficChart: React.FC<TrafficChartProps> = ({ timeRange = '2weeks' }) => {
  // 차트 데이터
  const chartData: ChartDataMap = {
    '2weeks': [
      { date: '03/01', visitors: 35 },
      { date: '03/02', visitors: 55 },
      { date: '03/03', visitors: 41 },
      { date: '03/04', visitors: 67 },
      { date: '03/05', visitors: 22 },
      { date: '03/06', visitors: 43 },
      { date: '03/07', visitors: 21 },
      { date: '03/08', visitors: 33 },
      { date: '03/09', visitors: 45 },
      { date: '03/10', visitors: 31 },
      { date: '03/11', visitors: 87 },
      { date: '03/12', visitors: 65 },
      { date: '03/13', visitors: 35 },
      { date: '03/14', visitors: 48 },
    ],
    '30days': Array(30)
      .fill(null)
      .map((_, i) => ({
        date: `03/${String(i + 1).padStart(2, '0')}`,
        visitors: Math.floor(Math.random() * 100) + 20,
      })),
    '90days': Array(12)
      .fill(null)
      .map((_, i) => ({
        date: `2024/${String(i + 1).padStart(2, '0')}`,
        visitors: Math.floor(Math.random() * 200) + 50,
      })),
  };

  return (
    <ResponsiveContainer width="100%" height={250}>
      <LineChart data={chartData[timeRange]} margin={{ top: 10, right: 10, left: 0, bottom: 10 }}>
        <CartesianGrid strokeDasharray="3 3" vertical={false} />
        <XAxis dataKey="date" tick={{ fontSize: 12 }} tickCount={5} />
        <YAxis tick={{ fontSize: 12 }} />
        <Tooltip />
        <Line
          type="monotone"
          dataKey="visitors"
          stroke="#4f46e5"
          strokeWidth={2.5}
          dot={{ r: 3 }}
          activeDot={{ r: 5 }}
        />
      </LineChart>
    </ResponsiveContainer>
  );
};

// 대시보드 페이지 타입 정의
interface Stats {
  totalPosts: number;
  totalViews: number;
  totalComments: number;
  totalLikes: number;
}

interface PopularPost {
  id: number;
  title: string;
  views: number;
  badge: string;
}

interface RevenueItem {
  source: string;
  amount: string;
  date: string;
}

interface QuickAction {
  id: number;
  title: string;
  href: string;
  icon: React.ReactNode;
  color: string;
}

const DashboardPage = () => {
  const router = useRouter();
  const user = useAuthStore((state) => state.user);
  const setUser = useAuthStore((state) => state.setUser);

  const [timeRange, setTimeRange] = useState<TimeRange>('2weeks');
  const [isCheckingAuth, setIsCheckingAuth] = useState(true); // 인증 상태 확인 중

  useEffect(() => {
    const checkAuthStatus = async () => {
      if (user) {
        console.log('[Dashboard Page] User found in store. Redirecting...');
        setIsCheckingAuth(false);
        return;
      }

      console.log('[Dashboard Page] Checking auth status via API...');
      try {
        const fetchedUser = await userService.getMe();
        console.log('[Dashboard Page] API check successful. User logged in. Redirecting...');
        setUser(fetchedUser);
        setIsCheckingAuth(false);
      } catch (apiError) {
        console.log(
          '[Dashboard Page] API check failed or user not logged in. Staying on login page.',
          apiError
        );
        router.replace(DEFAULT_LOGGED_OUT_REDIRECT_URL);
      }
    };

    checkAuthStatus();
  }, [user, setUser, router]);

  // 테스트 데이터 (실제로는 API에서 가져옴)
  const stats: Stats = {
    totalPosts: 24,
    totalViews: 3824,
    totalComments: 128,
    totalLikes: 256,
  };

  const popularPosts: PopularPost[] = [
    { id: 1, title: 'Next.js 14와 React Server Components', views: 152, badge: '1' },
    { id: 2, title: '효율적인 상태 관리 전략', views: 128, badge: '2' },
    { id: 3, title: '모바일 최적화를 위한 디자인 팁', views: 98, badge: '3' },
  ];

  const revenueData: RevenueItem[] = [
    { source: 'Google AdSense', amount: '₩15,200', date: '2024-03-15' },
    { source: '제휴 마케팅', amount: '₩8,500', date: '2024-03-10' },
    { source: '후원', amount: '₩5,000', date: '2024-03-08' },
  ];

  const quickActions: QuickAction[] = [
    {
      id: 1,
      title: '새 글 작성',
      href: '/dashboard/posts/create',
      icon: <FileText size={18} />,
      color: 'bg-indigo-100 text-indigo-600',
    },
    {
      id: 2,
      title: '광고 관리',
      href: '/dashboard/ads',
      icon: <BarChart2 size={18} />,
      color: 'bg-green-100 text-green-600',
    },
    {
      id: 3,
      title: '게시글 관리',
      href: '/dashboard/posts',
      icon: <Eye size={18} />,
      color: 'bg-blue-100 text-blue-600',
    },
    {
      id: 4,
      title: '수익 분석',
      href: '/dashboard/revenue',
      icon: <Users size={18} />,
      color: 'bg-purple-100 text-purple-600',
    },
  ];

  if (isCheckingAuth) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gray-50">
        <Loader2 className="h-8 w-8 animate-spin text-indigo-600" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* 환영 헤더 */}
      <div className="mb-6 flex flex-col justify-between space-y-4 sm:flex-row sm:items-center sm:space-y-0">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">대시보드</h1>
          <p className="mt-1 text-sm text-gray-500">
            안녕하세요, 관리자님! 오늘의 블로그 현황입니다.
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
          <TrafficChart timeRange={timeRange} />
        </div>

        {/* 인기 글 */}
        <div className="rounded-lg bg-white p-5 shadow-sm">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-lg font-semibold">인기 게시글</h2>
            <Link href="/dashboard/posts" className="text-sm text-indigo-600 hover:text-indigo-800">
              전체보기
            </Link>
          </div>
          <PopularPostList posts={popularPosts} />
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
          <RevenueList items={revenueData} />
          <div className="mt-4 flex justify-between">
            <span className="font-medium">이번 달 총액</span>
            <span className="font-semibold text-green-600">₩28,700</span>
          </div>
        </div>

        {/* 빠른 작업 */}
        <div className="rounded-lg bg-white p-5 shadow-sm">
          <h2 className="mb-4 text-lg font-semibold">빠른 작업</h2>
          <QuickActions actions={quickActions} />
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;
