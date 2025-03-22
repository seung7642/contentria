import React from 'react';
import Link from 'next/link';
import { AlertTriangle } from 'lucide-react';

interface AccessDeniedProps {
  userEmail: string;
}

const AccessDenied: React.FC<AccessDeniedProps> = ({ userEmail }) => {
  return (
    <div className="flex min-h-[50vh] flex-col items-center justify-center text-center">
      <div className="mb-6 flex h-24 w-24 items-center justify-center rounded-full bg-red-100">
        <AlertTriangle size={48} className="text-red-600" />
      </div>
      <h1 className="mb-3 text-2xl font-bold text-gray-800">접근 권한이 없습니다.</h1>
      <p className="mb-6 max-w-md text-gray-600">
        죄송합니다. 현재 {userEmail} 계정으로는 대시보드에 접근할 수 없습니다. 서비스 관리자에게
        문의하시거나 권한이 있는 계정으로 로그인해 주세요.
      </p>
      <Link
        href="/"
        className="rounded-lg bg-indigo-600 px-5 py-2 text-white transition hover:bg-indigo-700"
      >
        홈으로 돌아가기
      </Link>
    </div>
  );
};

export default AccessDenied;
