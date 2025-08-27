import { DEFAULT_LOGGED_IN_REDIRECT_URL } from '@/constants/auth';
import { authService01 } from '@/services/authService01';
import { VerificationStepProps } from '@/types/signup';
import { useRouter } from 'next/navigation';
import React, { useCallback } from 'react';
import OtpInput from 'react-otp-input';

export const VerificationStep: React.FC<VerificationStepProps> = ({
  formData,
  onUpdateData,
  isLoading,
  error,
  setError,
  setIsLoading,
}) => {
  const router = useRouter();

  // 의존성 배열이 변경되지 않는 한, 리렌더링 시 매번 함수를 새로 생성하지 않고 재사용
  const handleVerificationCodeSubmit = useCallback(
    async (code: string) => {
      setIsLoading(true);
      setError(null);

      try {
        console.log('Verifying code:', code);
        await authService01.verifyOtpCode({ email: formData.email, verificationCode: code });
        router.replace(DEFAULT_LOGGED_IN_REDIRECT_URL);
      } catch (error: unknown) {
        console.error('Verification failed:', error);
        setError('Invalid or expired code. Please try again.');
      } finally {
        setIsLoading(false);
      }
    },
    [router, setError, setIsLoading, formData.email]
  );

  const handleCodeChange = (code: string) => {
    onUpdateData('verificationCode', code);
    if (code.length === 6 && !isLoading) {
      handleVerificationCodeSubmit(code);
    }
  };

  const requestNewCode = async () => {
    setError(null);
    setIsLoading(true);

    try {
      // TODO: 새 인증 코드 요청 API

      onUpdateData('verificationCode', '');
    } catch (error: unknown) {
      console.error('Failed to request new code:', error);
      setError('Failed to request new code. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <div className="mt-4 pl-8 text-sm text-gray-600">
        <p>Enter the code sent to</p>
        <span className="font-medium text-gray-900">{formData.email}</span>
      </div>
      <div className="mt-4 space-y-6">
        <OtpInput
          value={formData.verificationCode}
          onChange={handleCodeChange}
          numInputs={6}
          shouldAutoFocus={true}
          containerStyle="flex justify-between w-full"
          renderInput={({ style, ...props }) => {
            if (style) {
              delete (style as React.CSSProperties).width;
            }
            return (
              <input
                style={style}
                {...props}
                disabled={isLoading}
                className={`${props.className || ''} h-12 w-12 flex-shrink-0 rounded-md border border-gray-300 text-center text-xl text-gray-900 transition focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:cursor-not-allowed disabled:border-gray-200 disabled:bg-gray-100 disabled:text-gray-400`}
              />
            );
          }}
        />
        {error && <p className="text-center text-sm text-red-600">{error}</p>}

        <div className="mt-4 text-center">
          <button
            type="button"
            onClick={requestNewCode}
            disabled={isLoading}
            className="text-sm text-indigo-600 hover:text-indigo-500 disabled:opacity-50"
          >
            {isLoading ? 'Sending...' : "Didn't receive a code? Resend"}
          </button>
        </div>
      </div>
    </>
  );
};
