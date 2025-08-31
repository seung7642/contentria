'use client';

import { useState } from 'react';
import { DollarSign, PlusCircle, Settings, HelpCircle, TrendingUp, BarChart2 } from 'lucide-react';
import Link from 'next/link';

// 광고 카드 컴포넌트
const AdCard = ({ ad, onToggle }) => {
  return (
    <div className="rounded-lg border bg-white p-5 shadow-sm">
      <div className="flex items-center justify-between">
        <div className="flex items-center">
          <div
            className={`mr-3 rounded-full ${
              ad.enabled ? 'bg-green-100 text-green-600' : 'bg-gray-100 text-gray-600'
            } p-2`}
          >
            <DollarSign size={20} />
          </div>
          <h3 className="font-medium">{ad.title}</h3>
        </div>
        <div className="flex items-center">
          <span className="mr-2 text-sm text-gray-500">{ad.enabled ? '활성화' : '비활성화'}</span>
          <label className="relative inline-flex cursor-pointer items-center">
            <input
              type="checkbox"
              checked={ad.enabled}
              onChange={() => onToggle(ad.id)}
              className="peer sr-only"
            />
            <div className="peer h-6 w-11 rounded-full bg-gray-200 after:absolute after:left-[2px] after:top-[2px] after:h-5 after:w-5 after:rounded-full after:border after:border-gray-300 after:bg-white after:transition-all after:content-[''] peer-checked:bg-indigo-600 peer-checked:after:translate-x-full peer-checked:after:border-white peer-focus:ring-4 peer-focus:ring-indigo-300" />
          </label>
        </div>
      </div>

      <p className="mt-2 text-sm text-gray-600">{ad.description}</p>

      <div className="mt-4 flex items-center justify-between">
        <div className="text-sm text-gray-500">
          노출 위치: <span className="font-medium">{ad.location}</span>
        </div>
        <div className="flex items-center text-sm font-medium text-green-600">
          <DollarSign size={16} className="mr-1" />
          {ad.revenue}
        </div>
      </div>

      <div className="mt-3 grid grid-cols-2 gap-2">
        <button className="flex items-center justify-center rounded-lg border border-gray-300 px-3 py-1.5 text-sm hover:bg-gray-50">
          <Settings size={16} className="mr-1" />
          설정
        </button>
        <button className="flex items-center justify-center rounded-lg border border-gray-300 px-3 py-1.5 text-sm hover:bg-gray-50">
          <BarChart2 size={16} className="mr-1" />
          통계
        </button>
      </div>
    </div>
  );
};

// 광고 수익 차트
const AdRevenueChart = () => {
  // 간단한 막대 그래프를 위한 데이터
  const data = [
    { name: '헤더 배너', value: 45200 },
    { name: '사이드바', value: 32800 },
    { name: '글 중간', value: 18500 },
    { name: '글 하단', value: 28400 },
    { name: '팝업', value: 12000 },
  ];

  const maxValue = Math.max(...data.map((item) => item.value));
  const colors = ['#4F46E5', '#10B981', '#F59E0B', '#06B6D4', '#8B5CF6'];

  return (
    <div className="h-64 w-full">
      <div className="flex h-[90%] items-end space-x-6 px-2">
        {data.map((item, index) => (
          <div key={index} className="flex flex-1 flex-col items-center">
            <div
              className="w-full rounded-t"
              style={{
                height: `${(item.value / maxValue) * 100}%`,
                backgroundColor: colors[index % colors.length],
              }}
            />
            <div className="mt-2 text-center">
              <div className="text-xs font-medium" style={{ color: colors[index % colors.length] }}>
                {(item.value / 1000).toFixed(1)}K
              </div>
              <div className="mt-1 text-xs text-gray-500">{item.name}</div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

// 광고 키워드 컴포넌트
const AdKeyword = ({ keyword, performance }) => {
  let badgeColor = 'bg-gray-100 text-gray-600';

  if (performance === 'high') {
    badgeColor = 'bg-green-100 text-green-600';
  } else if (performance === 'medium') {
    badgeColor = 'bg-blue-100 text-blue-600';
  } else if (performance === 'low') {
    badgeColor = 'bg-yellow-100 text-yellow-600';
  }

  return <div className={`rounded-full px-3 py-1 text-sm ${badgeColor}`}>{keyword}</div>;
};

export default function AdsPage() {
  // 광고 데이터 상태
  const [ads, setAds] = useState([
    {
      id: 1,
      title: '헤더 배너',
      description: '모든 페이지 상단에 노출되는 광고입니다.',
      location: '모든 페이지 헤더',
      enabled: true,
      revenue: '₩45,200',
    },
    {
      id: 2,
      title: '사이드바 광고',
      description: '블로그 사이드바에 노출되는 광고입니다.',
      location: '블로그 사이드바',
      enabled: true,
      revenue: '₩32,800',
    },
    {
      id: 3,
      title: '글 중간 광고',
      description: '긴 글에서 중간에 삽입되는 광고입니다.',
      location: '게시글 본문 중간',
      enabled: true,
      revenue: '₩18,500',
    },
    {
      id: 4,
      title: '글 하단 광고',
      description: '글 하단에 노출되는 광고입니다.',
      location: '게시글 하단',
      enabled: true,
      revenue: '₩28,400',
    },
    {
      id: 5,
      title: '팝업 광고',
      description: '일정 시간 후 표시되는 팝업 광고입니다.',
      location: '모든 페이지',
      enabled: false,
      revenue: '₩0',
    },
  ]);

  // 광고 활성화/비활성화 토글
  const toggleAdStatus = (adId) => {
    setAds(ads.map((ad) => (ad.id === adId ? { ...ad, enabled: !ad.enabled } : ad)));
  };

  return (
    <div className="space-y-6">
      {/* 헤더 영역 */}
      <div className="flex flex-col justify-between space-y-4 sm:flex-row sm:items-center sm:space-y-0">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">광고 관리</h1>
          <p className="mt-1 text-sm text-gray-500">
            블로그에 표시되는 광고를 관리하고 수익을 확인하세요
          </p>
        </div>
        <button className="inline-flex items-center rounded-lg bg-indigo-600 px-4 py-2 text-white hover:bg-indigo-700">
          <PlusCircle size={18} className="mr-2" />새 광고 추가
        </button>
      </div>

      {/* 요약 카드 */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <div className="rounded-lg bg-white p-5 shadow-sm">
          <h3 className="text-lg font-medium">총 광고 수익</h3>
          <div className="mt-2 flex items-baseline">
            <span className="text-2xl font-bold">₩124,900</span>
            <span className="ml-2 text-sm text-green-600">+12.5% 지난달 대비</span>
          </div>
          <div className="mt-4 flex items-center text-sm text-gray-500">
            <TrendingUp size={16} className="mr-1 text-green-600" />
            지속적으로 증가 중
          </div>
        </div>
        <div className="rounded-lg bg-white p-5 shadow-sm">
          <h3 className="text-lg font-medium">활성 광고</h3>
          <div className="mt-2 flex items-baseline">
            <span className="text-2xl font-bold">{ads.filter((ad) => ad.enabled).length}</span>
            <span className="ml-2 text-sm text-gray-500">/ {ads.length} 광고</span>
          </div>
          <div className="mt-4 flex items-center text-sm text-gray-500">
            최대 효율을 위해 모든 광고를 활성화하세요
          </div>
        </div>
        <div className="rounded-lg bg-white p-5 shadow-sm">
          <h3 className="text-lg font-medium">평균 클릭률</h3>
          <div className="mt-2 flex items-baseline">
            <span className="text-2xl font-bold">3.2%</span>
            <span className="ml-2 text-sm text-yellow-600">-0.5% 지난달 대비</span>
          </div>
          <div className="mt-4 flex items-center text-sm text-gray-500">
            <HelpCircle size={16} className="mr-1 text-blue-600" />
            <Link href="#" className="text-blue-600 hover:underline">
              클릭률 개선 방법 알아보기
            </Link>
          </div>
        </div>
      </div>

      {/* 광고 리스트 및 수익 차트 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
        <div className="lg:col-span-2">
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            {ads.map((ad) => (
              <AdCard key={ad.id} ad={ad} onToggle={toggleAdStatus} />
            ))}
          </div>
        </div>

        <div className="space-y-6">
          <div className="rounded-lg bg-white p-5 shadow-sm">
            <h3 className="mb-4 text-lg font-medium">광고 수익 분석</h3>
            <AdRevenueChart />
          </div>

          <div className="rounded-lg bg-white p-5 shadow-sm">
            <h3 className="mb-4 text-lg font-medium">인기 광고 키워드</h3>
            <div className="flex flex-wrap gap-2">
              <AdKeyword keyword="개발" performance="high" />
              <AdKeyword keyword="프로그래밍" performance="high" />
              <AdKeyword keyword="리액트" performance="medium" />
              <AdKeyword keyword="JavaScript" performance="medium" />
              <AdKeyword keyword="UI/UX" performance="medium" />
              <AdKeyword keyword="디자인" performance="low" />
              <AdKeyword keyword="백엔드" performance="low" />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
