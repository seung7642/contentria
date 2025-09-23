'use client';

import { Smartphone, X } from 'lucide-react';
import { FormEvent, useEffect, useState } from 'react';

interface SubscribeModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const SubscribeModal = ({ isOpen, onClose }: SubscribeModalProps) => {
  const [email, setEmail] = useState('');
  const [status, setStatus] = useState<'idle' | 'loading' | 'success' | 'error'>('idle');
  const [errorMessage, setErrorMessage] = useState('');

  // 모달이 닫힐 때 상태 초기화
  useEffect(() => {
    if (!isOpen) {
      setTimeout(() => {
        setEmail('');
        setStatus('idle');
        setErrorMessage('');
      }, 300); // 애니메이션 시간과 맞춤
    }
  }, [isOpen]);

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!email) {
      setErrorMessage('이메일을 입력해주세요.');
      setStatus('error');
      return;
    }

    setStatus('loading');
    setErrorMessage('');

    try {
      // 실제 API 호출 로직
      // 예: await subscribeToNewsletter(email);
      // 지금은 1초 딜레이로 시뮬레이션한다.
      await new Promise((resolve) => setTimeout(resolve, 1000));

      setStatus('success');
    } catch (error) {
      setStatus('error');
      setErrorMessage('구독 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
      console.error('Subscription failed:', error);
    }
  };

  if (!isOpen) {
    return null;
  }

  return (
    // Backdrop
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm"
      onClick={onClose}
      role="dialog"
      aria-modal="true"
      aria-labelledby="subscribe-modal-title"
    >
      {/* Modal Panel */}
      <div
        className="relative w-full max-w-md rounded-lg bg-white p-8 shadow-xl"
        onClick={(e) => e.stopPropagation()} // 모달 내부 클릭 시 닫히지 않도록 방지
      >
        {/* Close Button */}
        <button
          className="absolute right-4 top-4 rounded-full p-1 text-gray-500 hover:bg-gray-100"
          onClick={onClose}
          aria-label="닫기"
        >
          <X size={24} />
        </button>

        <div className="text-center">
          <h2 id="subscribe-modal-title" className="text-2xl font-bold text-gray-900">
            새 소식 구독하기
          </h2>
          <p className="mt-2 text-gray-600">가장 먼저 새로운 글과 소식을 이메일로 받아보세요.</p>
        </div>

        {status === 'success' ? (
          <div className="mt-6 text-center">
            <p className="text-lg font-semibold text-green-600">✅ 구독이 완료되었습니다!</p>
            <p className="mt-2 text-gray-600">감사합니다. 좋은 글로 보답하겠습니다.</p>
            <button
              className="mt-6 w-full rounded-md bg-gray-200 px-4 py-2 font-semibold text-gray-800 hover:bg-gray-300"
              onClick={onClose}
            >
              닫기
            </button>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="mt-6 space-y-4">
            <div>
              <label htmlFor="email-input" className="sr-only">
                Email address
              </label>
              <input
                id="email-input"
                type="text"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="your-email@example.com"
                className="w-full rounded-md border-gray-300 px-4 py-2 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                disabled={status === 'loading'}
              />
            </div>
            {status === 'error' && <p className="text-sm text-red-500">{errorMessage}</p>}
            <button
              type="submit"
              className="w-full rounded-md bg-indigo-600 px-4 py-2 font-semibold text-white shadow-sm hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-50"
              disabled={status === 'loading'}
            >
              {status === 'loading' ? '처리 중...' : '구독하기'}
            </button>
          </form>
        )}
      </div>
    </div>
  );
};

export default SubscribeModal;
