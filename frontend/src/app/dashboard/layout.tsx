// src/app/dashboard/layout.tsx

'use client';

import React from 'react'; // React import 추가
// UserProvider와 useUser 훅 import
import { UserProvider, useUser } from '@/context/UserContext'; // 실제 경로로 수정

// 가정: DashboardHeader, DashboardSidebar, AccessDenied 컴포넌트가 존재함
import DashboardHeader from '@/components/dashboard/DashboardHeader'; // 실제 경로로 수정
import DashboardSidebar from '@/components/dashboard/DashboardSidebar'; // 실제 경로로 수정
import { pretendard } from '@/styles/fonts'; // 폰트 설정 가정

// 내부 컨텐츠 렌더링을 담당할 컴포넌트
function DashboardInternalContent({ children }: { children: React.ReactNode }) {
  // 이 컴포넌트는 UserProvider 내부에 렌더링되므로 useUser() 사용 가능
  const { user, isLoading, error } = useUser();

  if (isLoading) {
    return (
      <div className="flex h-48 w-full items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent"></div>
        <span className="ml-3">사용자 정보 로딩 중...</span>
      </div>
    );
  }

  if (error || !user) {
    // 에러 발생 또는 사용자 정보 로드 실패 시
    return (
      <div className="w-full px-4 py-6 text-center">
        <p className="text-red-600">
          {error?.message === 'Unauthorized'
            ? '로그인이 필요하거나 세션이 만료되었습니다.'
            : '사용자 정보를 불러오는 중 오류가 발생했습니다.'}
        </p>
        {/* 필요시 로그인 버튼 등 추가 */}
      </div>
    );
  }

  // 로딩 완료 및 사용자 정보 로드 성공 시
  return (
    <div className="flex flex-col justify-center md:flex-row">
      <DashboardSidebar /> {/* 필요시 user 정보 전달 */}
      <div className="max-w-5xl flex-1 px-4 py-6 md:px-6">{children}</div>
    </div>
  );
}

// DashboardLayout 정의
export default function DashboardLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  // DashboardLayout 자체에서는 useUser()를 직접 호출하지 않습니다.
  // 상태 관리 및 로직은 UserProvider에 위임합니다.

  return (
    // UserProvider가 최상위에서 실제 컨텐츠 렌더링 로직을 감쌉니다.
    <UserProvider>
      {/* <html>, <body>는 루트 레이아웃에만 위치해야 합니다. */}
      <div className={`flex min-h-screen flex-col ${pretendard.className} bg-gray-50`}>
        {/* Header는 Provider 내부 또는 외부에 위치 가능 */}
        {/* Provider 내부에 위치시키면 Header에서도 useUser() 사용 가능 */}
        <DashboardHeader />
        <div className="mx-auto mt-10 w-full max-w-7xl flex-1">
          {/* 실제 컨텐츠는 UserProvider 내부에서 렌더링되는 DashboardInternalContent가 처리 */}
          <DashboardInternalContent>{children}</DashboardInternalContent>
        </div>
        {/* Footer 등 */}
      </div>
    </UserProvider>
  );
}
