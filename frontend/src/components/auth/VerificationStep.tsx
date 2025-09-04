import { PATHS } from '@/constants/paths';
import { authService } from '@/services/authService';
import { useRouter } from 'next/navigation';
import React, { useCallback } from 'react';
import OtpInput from 'react-otp-input';
import { VerifiableFormData, VerificationStepProps } from './types';

export const VerificationStep = <TFormData extends VerifiableFormData, TStep extends string>({
  formData,
  onUpdateData,
  isLoading,
  error,
  setError,
  setIsLoading,
}: VerificationStepProps<TFormData, TStep>) => {
  const router = useRouter();

  // 의존성 배열이 변경되지 않는 한, 리렌더링 시 매번 함수를 새로 생성하지 않고 재사용
  const handleVerificationCodeSubmit = useCallback(
    async (code: string) => {
      setIsLoading(true);
      setError(null);

      const result = await authService.verifyOtpCode({
        email: formData.email,
        verificationCode: code,
      });

      if (result.success) {
        router.replace(PATHS.DASHBOARD);
      } else {
        setError(result.error.message || 'Invalid or expired code. Please try again.');
      }

      setIsLoading(false);
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

    const result = await authService.requestVerificationCode({ email: formData.email });

    if (result.success) {
      // TODO: "새로운 코드를 전송했습니다." 같은 Toast 메시지를 보여주면 사용자 경험이 더 좋아진다.
      onUpdateData('verificationCode', '');
    } else {
      setError(result.error.message || 'Failed to request new code. Please try again.');
    }

    setIsLoading(false);
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
