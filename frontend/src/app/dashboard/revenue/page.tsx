
import { useState } from "react";
import {
  DollarSign,
  Calendar,
  Download,
  TrendingUp,
  BarChart2,
  ArrowUpRight,
  CreditCard,
  ChevronDown,
  Filter
} from "lucide-react";

// 수익 카드 컴포넌트
const RevenueCard = ({ title, amount, trend, icon, color = "indigo" }) => {
  const colorClasses = {
    indigo: "bg-indigo-100 text-indigo-600",
    green: "bg-green-100 text-green-600",
    blue: "bg-blue-100 text-blue-600",
    purple: "bg-purple-100 text-purple-600"
  };

  return (
    <div className="rounded-lg bg-white p-5 shadow-sm">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-gray-500">{title}</p>
          <h3 className="mt-1 text-2xl font-bold">{amount}</h3>
          <p className={`mt-2 flex items-center text-sm ${trend > 0 ? 'text-green-600' : 'text-red-600'}`}>
            <TrendingUp size={16} className="mr-1" />
            {trend > 0 ? '+' : ''}{trend}% 지난달 대비
          </p>
        </div>
        <div className={`rounded-full p-3 ${colorClasses[color]}`}>
          {icon}
        </div>
      </div>
    </div>
  );
};

// 수익 차트 컴포넌트 (월별)
const MonthlyRevenueChart = () => {
  // 데이터 - 실제 구현에서는 API로부터 가져올 것
  const monthNames = ['1월', '2월', '3월', '4월', '5월', '6월'];
  const data = [
    { month: '1월', adsense: 85000, affiliate: 32000, donation: 25000 },
    { month: '2월', adsense: 92000, affiliate: 38000, donation: 30000 },
    { month: '3월', adsense: 88000, affiliate: 35000, donation: 28000 },
    { month: '4월', adsense: 99000, affiliate: 42000, donation: 35000 },
    { month: '5월', adsense: 110000, affiliate: 48000, donation: 40000 },
    { month: '6월', adsense: 124900, affiliate: 52000, donation: 46000 },
  ];

  // 최대값 계산 (모든 수익원 합계의 최대값)
  const maxValue = Math.max(...data.map(item => item.adsense + item.affiliate + item.donation));

  return (
    <div className="h-64 w-full">
      <div className="flex h-[90%] items-end space-x-2 px-2">
        {data.map((item, index) => {
          const totalHeight = ((item.adsense + item.affiliate + item.donation) / maxValue) * 100;
          const adSenseHeight = (item.adsense / (item.adsense + item.affiliate + item.donation)) * totalHeight;
          const affiliateHeight = (item.affiliate / (item.adsense + item.affiliate + item.donation)) * totalHeight;
          const donationHeight = (item.donation / (item.adsense + item.affiliate + item.donation)) * totalHeight;

          return (
            <div key={index} className="flex flex-1 flex-col items-center">
              <div className="w-full" style={{ height: `${totalHeight}%` }}>
                <div className="h-[1%] w-full" style={{ height: `${adSenseHeight}%`, backgroundColor: '#4F46E5' }}></div>
                <div className="h-[1%] w-full" style={{ height: `${affiliateHeight}%`, backgroundColor: '#10B981' }}></div>
                <div className="h-[1%] w-full" style={{ height: `${donationHeight}%`, backgroundColor: '#F59E0B' }}></div>
              </div>
              <div className="mt-2 text-center text-xs text-gray-500">{item.month}</div>
            </div>
          );
        })}
      </div>

      <div className="mt-4 flex items-center justify-center space-x-4 text-xs">
        <div className="flex items-center">
          <div className="mr-1 h-3 w-3 rounded-full bg-indigo-500"></div>
          <span>AdSense</span>
        </div>
        <div className="flex items-center">
          <div className="mr-1 h-3 w-3 rounded-full bg-green-500"></div>
          <span>제휴 마케팅</span>
        </div>
        <div className="flex items-center">
          <div className="mr-1 h-3 w-3 rounded-full bg-yellow-500"></div>
          <span>후원</span>
        </div>
      </div>
    </div>
  );
};

// 도넛 차트 컴포넌트 (수익원 분석)
const RevenueSourcesChart = () => {
  // SVG 도넛 차트 파라미터
  const radius = 60;
  const strokeWidth = 30;
  const viewBoxSize = (radius + strokeWidth) * 2;
  const center = viewBoxSize / 2;

  // 데이터와 색상
  const data = [
    { label: 'AdSense', value: 56, color: '#4F46E5' },  // indigo-600
    { label: '제휴 마케팅', value: 23, color: '#10B981' },  // green-500
    { label: '후원', value: 21, color: '#F59E0B' },  // yellow-500
  ];

  // 원형 차트의 세그먼트 계산
  let totalValue = data.reduce((acc, item) => acc + item.value, 0);
  let cumulativePercent = 0;
  const segments = data.map(item => {
    const startPercent = cumulativePercent;
    const percent = item.value / totalValue;
    cumulativePercent += percent;

    return {
      ...item,
      percent,
      startAngle: startPercent * 360,
      endAngle: cumulativePercent * 360
    };
  });

  // SVG 경로 생성 함수
  const describeArc = (x, y, radius, startAngle, endAngle) => {
    const start = polarToCartesian(x, y, radius, endAngle);
    const end = polarToCartesian(x, y, radius, startAngle);
    const largeArcFlag = endAngle - startAngle <= 180 ? "0" : "1";

    return [
      "M", start.x, start.y,
      "A", radius, radius, 0, largeArcFlag, 0, end.x, end.y
    ].join(" ");
  };

  const polarToCartesian = (centerX, centerY, radius, angleInDegrees) => {
    const angleInRadians = (angleInDegrees - 90) * Math.PI / 180.0;
    return {
      x: centerX + (radius * Math.cos(angleInRadians)),
      y: centerY + (radius * Math.sin(angleInRadians))
    };
  };

  return (
    <div className="flex items-center justify-center">
      <div className="relative">
        <svg width="180" height="180" viewBox={`0 0 ${viewBoxSize} ${viewBoxSize}`}>
          {segments.map((segment, i) => (
            <path
              key={i}
              d={describeArc(
                center,
                center,
                radius,
                segment.startAngle,
                segment.endAngle
              )}
              fill="none"
              stroke={segment.color}
              strokeWidth={strokeWidth}
              strokeLinecap="round"
            />
          ))}
        </svg>

        <div className="absolute inset-0 flex flex-col items-center justify-center">
          <p className="text-2xl font-bold">₩222,900</p>
          <p className="text-xs text-gray-500">총 수익</p>
        </div>
      </div>

      <div className="ml-8">
        {data.map((item, i) => (
          <div key={i} className="mb-2 flex items-center">
            <div className="mr-2 h-3 w-3 rounded-full" style={{ backgroundColor: item.color }}></div>
            <div className="mr-6 min-w-[80px] text-sm">{item.label}</div>
            <div className="text-sm font-medium">{item.value}%</div>
          </div>
        ))}
      </div>
    </div>
  );
};

// 월별 요약 아코디언 아이템
const MonthSummaryItem = ({ month, total, isOpen, onClick }) => {
  return (
    <div className="border-b">
      <button
        className="flex w-full items-center justify-between px-4 py-3 text-left hover:bg-gray-50"
        onClick={onClick}
      >
        <div>
          <span className="font-medium">{month}</span>
        </div>
        <div className="flex items-center">
          <span className="mr-4 font-medium text-green-600">{total}</span>
          <ChevronDown
            size={18}
            className={`transform transition-transform ${isOpen ? 'rotate-180' : ''}`}
          />
        </div>
      </button>

      {isOpen && (
        <div className="border-t bg-gray-50 px-4 py-3">
          <div className="flex justify-between py-2">
            <span className="text-sm text-gray-600">AdSense</span>
            <span className="font-medium">₩88,400</span>
          </div>
          <div className="flex justify-between py-2">
            <span className="text-sm text-gray-600">제휴 마케팅</span>
            <span className="font-medium">₩42,000</span>
          </div>
          <div className="flex justify-between py-2">
            <span className="text-sm text-gray-600">후원</span>
            <span className="font-medium">₩36,000</span>
          </div>
          <div className="mt-2 flex justify-end">
            <button className="flex items-center text-sm text-indigo-600 hover:text-indigo-800">
              <BarChart2 size={16} className="mr-1" />
              상세 보기
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default function RevenuePage() {
  const [selectedMonth, setSelectedMonth] = useState(0);

  // 데이터
  const monthSummaries = [
    { month: '2024년 6월', total: '₩166,400' },
    { month: '2024년 5월', total: '₩148,000' },
    { month: '2024년 4월', total: '₩132,500' },
    { month: '2024년 3월', total: '₩124,000' },
  ];

  return (
    <div className="space-y-6">
      {/* 헤더 영역 */}
      <div className="flex flex-col justify-between space-y-4 sm:flex-row sm:items-center sm:space-y-0">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">수익 관리</h1>
          <p className="mt-1 text-sm text-gray-500">
            블로그 수익을 분석하고 관리하세요
          </p>
        </div>
        <div className="flex flex-col space-y-2 sm:flex-row sm:space-x-2 sm:space-y-0">
          <button className="inline-flex items-center rounded-lg border border-gray-300 bg-white px-4 py-2 hover:bg-gray-50">
            <Calendar size={18} className="mr-2" />
            기간 설정
          </button>
          <button className="inline-flex items-center rounded-lg border border-gray-300 bg-white px-4 py-2 hover:bg-gray-50">
            <Download size={18} className="mr-2" />
            보고서 다운로드
          </button>
        </div>
      </div>

      {/* 수익 요약 카드 */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <RevenueCard
          title="이번 달 총 수익"
          amount="₩166,400"
          trend={12.4}
          icon={<DollarSign size={24} />}
          color="indigo"
        />
        <RevenueCard
          title="AdSense 수익"
          amount="₩88,400"
          trend={6.8}
          icon={<BarChart2 size={24} />}
          color="green"
        />
        <RevenueCard
          title="제휴 마케팅"
          amount="₩42,000"
          trend={15.2}
          icon={<ArrowUpRight size={24} />}
          color="blue"
        />
        <RevenueCard
          title="후원 수익"
          amount="₩36,000"
          trend={4.5}
          icon={<CreditCard size={24} />}
          color="purple"
        />
      </div>

      {/* 차트 및 분석 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-5">
        {/* 월별 수익 차트 */}
        <div className="rounded-lg bg-white p-5 shadow-sm lg:col-span-3">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-lg font-semibold">월별 수익 추이</h2>
            <div className="flex items-center space-x-2">
              <button className="flex items-center rounded-lg border border-gray-300 bg-white px-3 py-1.5 text-sm hover:bg-gray-50">
                <Filter size={16} className="mr-1" />
                필터
              </button>
              <select className="rounded-lg border border-gray-300 px-3 py-1.5 text-sm">
                <option>최근 6개월</option>
                <option>최근 12개월</option>
                <option>올해</option>
                <option>작년</option>
              </select>
            </div>
          </div>
          <MonthlyRevenueChart />
        </div>

        {/* 수익원 분석 */}
        <div className="rounded-lg bg-white p-5 shadow-sm lg:col-span-2">
          <h2 className="mb-4 text-lg font-semibold">수익원 분석</h2>
          <RevenueSourcesChart />
        </div>
      </div>

      {/* 월별 요약 및 수익 상위 게시글 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-5">
        {/* 월별 요약 */}
        <div className="rounded-lg bg-white shadow-sm lg:col-span-2">
          <div className="border-b px-4 py-3">
            <h2 className="font-semibold">월별 수익 요약</h2>
          </div>
          <div className="divide-y">
            {monthSummaries.map((item, index) => (
              <MonthSummaryItem
                key={index}
                month={item.month}
                total={item.total}
                isOpen={selectedMonth === index}
                onClick={() => setSelectedMonth(selectedMonth === index ? -1 : index)}
              />
            ))}
          </div>
        </div>

        {/* 수익 상위 게시글 */}
        <div className="rounded-lg bg-white shadow-sm lg:col-span-3">
          <div className="border-b px-4 py-3">
            <h2 className="font-semibold">수익 상위 게시글</h2>
          </div>
          <div className="divide-y">
            <div className="px-4 py-3">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="font-medium">Next.js 14와 React Server Components</h3>
                  <p className="text-sm text-gray-500">2024년 3월 15일 게시</p>
                </div>
                <span className="font-medium text-green-600">₩12,400</span>
              </div>
              <div className="mt-2 flex items-center">
                <div className="text-xs text-gray-500">
                  <span className="mr-3">조회수: 1,523</span>
                  <span>클릭률: 4.2%</span>
                </div>
              </div>
            </div>
            <div className="px-4 py-3">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="font-medium">효율적인 상태 관리 전략</h3>
                  <p className="text-sm text-gray-500">2024년 3월 10일 게시</p>
                </div>
                <span className="font-medium text-green-
