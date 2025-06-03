import BackButton from '@/components/ui/BackButton';
import Divider from '@/components/ui/divider';
import InputField from '@/components/ui/inputField';
import { RECAPTCHA_SIGN_UP_ACTION } from '@/constants/auth';
import { PasswordStepFormData, passwordStepSchema } from '@/lib/schemas/authSchemas';
import { authService } from '@/services/authService';
import { PasswordStepProps } from '@/types/signup';
import { zodResolver } from '@hookform/resolvers/zod';
import { CheckCircle2, Info, Mail, XCircle } from 'lucide-react';
import React, { useMemo, useState } from 'react';
import { useGoogleReCaptcha } from 'react-google-recaptcha-v3';
import { SubmitHandler, useForm } from 'react-hook-form';
import PasswordPolicyTooltip from './PasswordPolicyTooltip';

interface PolicyItem {
  id: string;
  text: string;
  isValid: (password: string) => boolean;
}

export const PasswordStep: React.FC<PasswordStepProps> = ({
  formData,
  onUpdateData,
  goToNextStep,
  goToPreviousStep,
  isLoading,
  error: apiError, // react-hook-form(RHF) errors와 구분하기 위해 이름 변경
  setError: setApiError,
  setIsLoading,
  setStep,
}) => {
  const { executeRecaptcha } = useGoogleReCaptcha();
  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm<PasswordStepFormData>({
    resolver: zodResolver(passwordStepSchema),
    defaultValues: {
      password: formData.password,
    },
  });

  const currentPassword = watch('password', formData.password);
  const [isPolicyVisible, setIsPolicyVisible] = useState(false);

  // 비밀번호 정책 목록 및 검증 함수 (useMemo로 최적화 가능)
  const passwordPolicies = useMemo(
    (): PolicyItem[] => [
      { id: 'length', text: '최소 10자 이상', isValid: (pw) => pw.length >= 10 },
      { id: 'number', text: '숫자 포함', isValid: (pw) => /[0-9]/.test(pw) },
      { id: 'lowercase', text: '소문자 포함', isValid: (pw) => /[a-z]/.test(pw) },
      { id: 'uppercase', text: '대문자 포함', isValid: (pw) => /[A-Z]/.test(pw) },
      { id: 'special', text: '특수문자 포함', isValid: (pw) => /[^a-zA-Z0-9]/.test(pw) },
    ],
    []
  );

  // RHF 유효성 검사 통과 후 실행될 함수
  const processPasswordSubmit: SubmitHandler<PasswordStepFormData> = async (data) => {
    onUpdateData('password', data.password);
    setApiError(null);

    if (!executeRecaptcha) {
      setApiError('reCAPTCHA not ready. Please try again.');
      console.error('Execute recaptcha not yet available.');
      return;
    }

    setIsLoading(true);
    try {
      const recaptchaToken = await executeRecaptcha(RECAPTCHA_SIGN_UP_ACTION);
      const signUpData = {
        name: formData.name,
        email: formData.email,
        password: formData.password,
        recaptchaV3Token: recaptchaToken,
      };

      const response = await authService.initiateSignup(signUpData);
      if (response.nextStep === 'verify_with_recaptcha_v2') {
        setStep('recaptcha-v2-challenge');
      } else if (response.nextStep === 'enter_verification_code') {
        setStep('verify-email-code');
      } else {
        setApiError('An unexpected error occurred. Please try again.');
      }
    } catch (error: unknown) {
      console.error('reCAPTCHA execution failed:', error);
      return;
    } finally {
      setIsLoading(false);
    }
  };

  const requestVerificationCode = async () => {
    setApiError(null);
    setIsLoading(true);

    // TODO: 이 기능도 reCAPTCHA v3로 보호
    try {
      await authService.requestVerificationCode({ email: formData.email });
      goToNextStep();
    } catch (error: unknown) {
      console.error('Failed to send verification code:', error);
      setApiError('Failed to send verification code. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <BackButton onClick={goToPreviousStep} />
      <form className="mt-8" onSubmit={handleSubmit(processPasswordSubmit)}>
        <InputField
          id="email-display-password"
          label="Email"
          type="email"
          disabled
          value={formData.email}
          placeholder="Email address"
          className="mb-6"
        />

        <div className="mt-6">
          {' '}
          {/* Password 섹션 전체를 감싸는 div가 필요할 수 있음 */}
          <div className="mb-1 flex items-center space-x-1">
            <label htmlFor="password" className="block text-sm font-medium text-gray-700">
              Password
            </label>
            <div className="relative flex items-center">
              <button
                type="button"
                onMouseEnter={() => setIsPolicyVisible(true)}
                onMouseLeave={() => setIsPolicyVisible(false)}
                onFocus={() => setIsPolicyVisible(true)}
                onBlur={() => setIsPolicyVisible(false)}
                className="focus:outline-none rounded-full p-0.5 text-gray-400 hover:text-gray-600 focus:outline-none focus:ring-2"
                aria-label="Show password policy"
                aria-expanded={isPolicyVisible} // 팝오버 확장 상태 알림
              >
                <Info size={16} />
              </button>
              {/* 조건부 렌더링은 여기서, 내용은 PasswordPolicyTooltip 컴포넌트가 담당 */}
              <PasswordPolicyTooltip
                policies={passwordPolicies}
                currentPasswordValue={currentPassword || ''}
                isVisible={isPolicyVisible}
              />
            </div>
          </div>
          <InputField
            id="password"
            type="password"
            placeholder="Enter your password"
            autoComplete="new-password"
            isRounded="both"
            {...register('password')}
          />
        </div>

        {apiError && <p className="text-center text-sm text-red-600">{apiError}</p>}
        <div className="mt-8">
          <button
            type="submit"
            disabled={isLoading}
            className="group relative flex w-full justify-center rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {isLoading ? 'Processing...' : 'Continue'}
          </button>
        </div>
      </form>

      <div className="mt-6">
        <Divider text={'OR'} />
        <div className="mt-6">
          <button
            type="button"
            onClick={requestVerificationCode}
            disabled={isLoading}
            className="group relative flex w-full items-center justify-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
          >
            <Mail className="mr-2 h-4 w-4" />
            {isLoading ? 'Sending...' : 'Continue with email code'}
          </button>
        </div>
      </div>
    </>
  );
};
