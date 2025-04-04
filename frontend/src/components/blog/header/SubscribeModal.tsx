import { useState, useEffect, useRef } from 'react';
import { X, Mail } from 'lucide-react';
import { SubscribeModalProps } from './types';

const SubscribeModal = ({ isOpen, onClose }: SubscribeModalProps) => {
  const [email, setEmail] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [subscribeSuccess, setSubscribeSuccess] = useState(false);
  const modalRef = useRef<HTMLDivElement>(null);

  const handleSubscribe = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !email.includes('@')) {
      return;
    }

    setIsSubmitting(true);
    try {
      // 실제 API 호출 구현 (현재는 시뮬레이션)
      await new Promise((resolve) => setTimeout(resolve, 800));
      setSubscribeSuccess(true);

      // 3초 후 모달 닫기
      setTimeout(() => onClose(), 3000);
    } catch (error) {
      console.error('구독 처리 중 오류 발생:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  // 모달이 닫힐 때 상태 초기화
  useEffect(() => {
    if (!isOpen) {
      setTimeout(() => {
        setSubscribeSuccess(false);
        setEmail('');
      }, 300);
    }
  }, [isOpen]);

  if (!isOpen) {
    return null;
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4">
      <div
        ref={modalRef}
        className="w-full max-w-md transform rounded-lg bg-white p-6 shadow-xl transition-all duration-300 ease-in-out"
        style={{ animation: 'modalFadeIn 0.3s ease-out' }}
      >
        {!subscribeSuccess ? (
          <>
            <div className="mb-4 flex items-center justify-between">
              <h3 className="text-lg font-semibold text-gray-900">뉴스레터 구독하기</h3>
              <button
                onClick={onClose}
                className="rounded-full p-1 text-gray-400 hover:bg-gray-100 hover:text-gray-600"
              >
                <X size={20} />
              </button>
            </div>

            <div className="mb-6">
              <p className="text-gray-600">
                최신 블로그 포스트와 유용한 정보를 이메일로 받아보세요.
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
                  onClick={onClose}
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
  );
};

export default SubscribeModal;
