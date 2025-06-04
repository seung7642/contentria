import { DEFAULT_LOGGED_IN_REDIRECT_URL } from '@/constants/auth';
import { authService } from '@/services/authService';
import { VerificationStepProps } from '@/types/signup';
import { useRouter } from 'next/navigation';
import React from 'react';
import CodeInput from '../CodeInput';
import BackButton from '@/components/ui/BackButton';

export const VerificationStep: React.FC<VerificationStepProps> = ({
  formData,
  onUpdateData,
  onBack,
  isLoading,
  error,
  setError,
  setIsLoading,
  onComplete,
}) => {
  const router = useRouter();

  const handleVerificationCodeSubmit = async (e?: React.FormEvent) => {
    if (e) {
      e.preventDefault();
    }
    setError(null);

    if (formData.verificationCode.length !== 6) {
      setError('Please enter the 6-digit code.');
      return;
    }

    setIsLoading(true);
    try {
      // await authService.verifySignupCode();
      onComplete();
      router.replace(DEFAULT_LOGGED_IN_REDIRECT_URL);
    } catch (error: unknown) {
      console.error('Verification failed:', error);
      setError('Invalid or expired code. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleCodeChange = (code: string) => {
    onUpdateData('verificationCode', code);
  };

  const handleCodeComplete = (code: string) => {
    onUpdateData('verificationCode', code);
    if (code.length === 6) {
      handleVerificationCodeSubmit();
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
      <BackButton onClick={onBack} />
      <div className="mt-4 pl-8 text-sm text-gray-600">
        <p>Enter the code sent to</p>
        <span className="font-medium text-gray-900">{formData.email}</span>
      </div>
      <form className="mt-4 space-y-6" onSubmit={handleVerificationCodeSubmit}>
        <CodeInput
          length={6}
          onChange={handleCodeChange}
          onComplete={handleCodeComplete}
          value={formData.verificationCode}
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
      </form>
    </>
  );
};
