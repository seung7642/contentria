'use client';

import Link from 'next/link';
import { useState, useEffect, useRef } from 'react';
import { useRouter } from 'next/navigation'; // App Router 사용
import { Home, LayoutDashboard, LogOut, User, X, Mail } from 'lucide-react';

const Header = () => {
  const [user, setUser] = useState(null);
  const [mounted, setMounted] = useState(false);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  const [isSubscribeModalOpen, setIsSubscribeModalOpen] = useState(false);
  const [email, setEmail] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [subscribeSuccess, setSubscribeSuccess] = useState(false);

  const dropdownRef = useRef(null);
  const modalRef = useRef(null);
  const router = useRouter();

  useEffect(() => {
    // 컴포넌트 마운트 시 로컬 스토리지 접근
    setMounted(true);

    // 사용자 정보 로드
    const token = localStorage.getItem('auth_token');
    const userData = localStorage.getItem('userData');

    if (token && userData) {
      try {
        setUser(JSON.parse(userData));
      } catch (e) {
        console.error('Failed to parse user data');
      }
    }

    // 드롭다운 외부 클릭 감지
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsDropdownOpen(false);
      }

      if (isSubscribeModalOpen && modalRef.current && !modalRef.current.contains(event.target)) {
        closeSubscribeModal();
      }
    };

    document.addEventListener('mousedown', handleClickOutside);

    // ESC 키로 모달 닫기
    const handleEscKey = (event) => {
      if (event.key === 'Escape') {
        closeSubscribeModal();
      }
    };

    document.addEventListener('keydown', handleEscKey);

    // 모달 열린 상태에서 스크롤 방지
    if (isSubscribeModalOpen) {
      document.body.style.overflow = 'hidden';
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
      document.removeEventListener('keydown', handleEscKey);
      document.body.style.overflow = 'unset';
    };
  }, [isSubscribeModalOpen]);

  const handleLogout = () => {
    // 로그아웃 시 로컬 스토리지 데이터 삭제
    localStorage.removeItem('auth_token');
    localStorage.removeItem('userData');
    setUser(null);
    router.refresh(); // UI 갱신
    router.push('/'); // 홈 페이지로 이동
  };

  const toggleDropdown = () => {
    setIsDropdownOpen(!isDropdownOpen);
  };

  const openSubscribeModal = () => {
    setIsSubscribeModalOpen(true);
    setSubscribeSuccess(false);
  };

  const closeSubscribeModal = () => {
    setIsSubscribeModalOpen(false);
    // 성공 후 일정 시간이 지나면 상태 초기화
    setTimeout(() => {
      setSubscribeSuccess(false);
      setEmail('');
    }, 300);
  };

  const handleSubscribe = async (e) => {
    e.preventDefault();
    if (!email || !email.includes('@')) {
      return;
    }
    setIsSubmitting(true);
    try {
      // 여기에 실제 API 호출 코드가 들어갈 수 있습니다.
      // await fetch('/api/subscribe', { method: 'POST', body: JSON.stringify({ email }) });

      // 성공 시 임시 대기 (실제 구현에서는 제거)
      await new Promise((resolve) => setTimeout(resolve, 800));

      setSubscribeSuccess(true);
      setIsSubmitting(false);

      // 3초 후 모달 닫기
      setTimeout(() => {
        closeSubscribeModal();
      }, 3000);
    } catch (error) {
      console.error('구독 처리 중 오류 발생:', error);
      setIsSubmitting(false);
    }
  };

  // 클라이언트 사이드 마운트 전에는 초기 UI 반환
  if (!mounted) {
    return (
      <header className="flex items-center justify-between border-b px-6 py-5">
        <div className="w-24"></div>
        <h1 className="text-2xl font-bold">Blog</h1>
        <div className="text-right">
          <span className="mx-2 my-2 rounded-lg bg-indigo-500 px-4 py-3 text-sm text-white">
            Subscribe
          </span>
          <span className="my-2 rounded-lg bg-white px-4 py-3 text-sm text-gray-800">Sign in</span>
        </div>
      </header>
    );
  }

  return (
    <>
      <header className="flex items-center justify-between border-b px-6 py-3">
        <div className="w-24"></div>
        <h1 className="text-2xl font-bold">
          <Link href="/">Blog</Link>
        </h1>
        <div className="flex items-center justify-between text-right">
          <button
            onClick={openSubscribeModal}
            className="mx-2 rounded-lg bg-indigo-500 px-4 py-3 text-sm text-white hover:bg-indigo-600"
          >
            Subscribe
          </button>

          {user ? (
            <div ref={dropdownRef} className="relative">
              {/* 프로필 버튼 */}
              <button
                onClick={toggleDropdown}
                className="mx-2 flex h-10 w-10 items-center justify-center overflow-hidden rounded-full border-2 border-gray-200 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              >
                {user.profileImage ? (
                  <img
                    src={user.profileImage}
                    alt={user.name}
                    className="h-full w-full object-cover"
                  />
                ) : (
                  <div className="flex h-full w-full items-center justify-center bg-gray-200">
                    <User size={24} className="text-gray-600" />
                  </div>
                )}
              </button>

              {/* 드롭다운 메뉴 */}
              {isDropdownOpen && (
                <div className="absolute right-0 z-10 mt-2 w-64 rounded-md bg-white shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none">
                  {/* 첫 번째 영역: 프로필 정보 */}
                  <div className="border-b border-gray-100 px-4 py-3">
                    <div className="flex items-center">
                      <div className="h-10 w-10 overflow-hidden rounded-full border border-gray-200">
                        {user.profileImage ? (
                          <img
                            src={user.profileImage}
                            alt={user.name}
                            className="h-full w-full object-cover"
                          />
                        ) : (
                          <div className="flex h-full w-full items-center justify-center bg-gray-200">
                            <User size={20} className="text-gray-600" />
                          </div>
                        )}
                      </div>
                      <div className="ml-3 text-left">
                        <div className="text-sm font-medium text-gray-800">{user.name}</div>
                        <div className="text-xs text-gray-500">{user.email}</div>
                      </div>
                    </div>
                  </div>

                  {/* 두 번째 영역: 메뉴 항목 */}
                  <div className="border-b border-gray-100">
                    <Link
                      href="/"
                      className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                      onClick={() => setIsDropdownOpen(false)}
                    >
                      <Home size={16} className="mr-3 text-gray-500" />
                      Home
                    </Link>

                    <Link
                      href="/dashboard"
                      className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                      onClick={() => setIsDropdownOpen(false)}
                    >
                      <LayoutDashboard size={16} className="mr-3 text-indigo-500" />
                      Dashboard
                    </Link>
                  </div>

                  {/* 세 번째 영역: 로그아웃 */}
                  <div>
                    <button
                      onClick={handleLogout}
                      className="flex w-full items-center px-4 py-2 text-sm text-gray-600 hover:bg-gray-100"
                    >
                      <LogOut size={16} className="mr-3 text-gray-500" />
                      Sign out
                    </button>
                  </div>
                </div>
              )}
            </div>
          ) : (
            <Link
              href="/login"
              className="my-2 ml-2 rounded-lg bg-white px-4 py-3 text-sm text-gray-800 hover:bg-gray-200"
            >
              Sign in
            </Link>
          )}
        </div>
      </header>

      {/* 구독 모달 */}
      {isSubscribeModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4">
          <div
            ref={modalRef}
            className="w-full max-w-md transform rounded-lg bg-white p-6 shadow-xl transition-all duration-300 ease-in-out"
            style={{
              animation: 'modalFadeIn 0.3s ease-out',
            }}
          >
            {!subscribeSuccess ? (
              <>
                <div className="mb-4 flex items-center justify-between">
                  <h3 className="text-lg font-semibold text-gray-900">뉴스레터 구독하기</h3>
                  <button
                    onClick={closeSubscribeModal}
                    className="rounded-full p-1 text-gray-400 hover:bg-gray-100 hover:text-gray-600"
                  >
                    <X size={20} />
                  </button>
                </div>

                <div className="mb-6">
                  <p className="text-gray-600">
                    최신 블로그 포스트와 유용한 정보를 이메일로 받아보세요. 주 1회 발송됩니다.
                  </p>
                </div>

                <form onSubmit={handleSubscribe}>
                  <div className="mb-4">
                    <div className="relative">
                      <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                        <Mail size={18} className="text-gray-400" />
                      </div>
                      <input
                        type="email"
                        className="w-full rounded-lg border border-gray-300 py-2 pl-10 pr-4 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                        placeholder="이메일 주소"
                        required
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                      />
                    </div>
                  </div>

                  <div className="flex justify-end space-x-2">
                    <button
                      type="button"
                      onClick={closeSubscribeModal}
                      className="rounded-lg border border-gray-300 px-4 py-2 text-sm text-gray-700 transition-colors hover:bg-gray-50"
                    >
                      취소
                    </button>
                    <button
                      type="submit"
                      disabled={isSubmitting}
                      className={`rounded-lg bg-indigo-600 px-4 py-2 text-sm text-white transition-colors ${
                        isSubmitting ? 'cursor-not-allowed opacity-70' : 'hover:bg-indigo-700'
                      }`}
                    >
                      {isSubmitting ? '처리 중...' : '구독하기'}
                    </button>
                  </div>
                </form>
              </>
            ) : (
              <div className="py-4 text-center">
                <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-green-100">
                  <svg
                    className="h-8 w-8 text-green-500"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth="2"
                      d="M5 13l4 4L19 7"
                    ></path>
                  </svg>
                </div>
                <h3 className="mb-2 text-lg font-semibold text-gray-900">구독이 완료되었습니다!</h3>
                <p className="text-gray-600">
                  {email}로 확인 메일을 보냈습니다. 곧 최신 소식을 전해드리겠습니다.
                </p>
              </div>
            )}
          </div>
        </div>
      )}

      {/* 애니메이션을 위한 글로벌 스타일 */}
      <style jsx global>{`
        @keyframes modalFadeIn {
          from {
            opacity: 0;
            transform: translateY(-20px);
          }
          to {
            opacity: 1;
            transform: translateY(0);
          }
        }
      `}</style>
    </>
  );
};

export default Header;
