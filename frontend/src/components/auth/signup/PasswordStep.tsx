import BackButton from '@/components/ui/BackButton';
import Divider from '@/components/ui/Divider';
import InputField from '@/components/ui/InputField';
import { RECAPTCHA_SIGN_UP_ACTION } from '@/constants/auth';
import { PasswordStepFormData, passwordStepSchema } from '@/lib/schemas/authSchemas';
import { PasswordStepProps } from '@/types/signup';
import { zodResolver } from '@hookform/resolvers/zod';
import { Info, Mail } from 'lucide-react';
import React, { useMemo, useState } from 'react';
import { useGoogleReCaptcha } from 'react-google-recaptcha-v3';
import { SubmitHandler, useForm } from 'react-hook-form';
import PasswordPolicyTooltip from './PasswordPolicyTooltip';
import { authService } from '@/services/authService';

interface PolicyItem {
  id: string;
  text: string;
  isValid: (password: string) => boolean;
}

export const PasswordStep: React.FC<PasswordStepProps> = ({
  formData,
  onUpdateData,
  goToPreviousStep,
  isLoading,
  error: apiError,
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
  // const [isPasswordSubmitClick, setIsPasswordSubmitClick] = useState(false);
  // const [isRequestCodeClick, setIsRequestCodeClick] = useState(false);
  const [submissionType, setSubmissionType] = useState<'with_password' | 'without_password' | null>(
    null
  );

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

  const handleInitiateSignUp = async (password: string | null) => {
    onUpdateData('password', password || '');
    setApiError(null);
    setIsLoading(true);

    setSubmissionType(password ? 'with_password' : 'without_password');

    if (!executeRecaptcha) {
      setApiError('reCAPTCHA is not ready. Please try again.');
      setIsLoading(false);
      setSubmissionType(null);
      return;
    }

    const recaptchaToken = await executeRecaptcha(RECAPTCHA_SIGN_UP_ACTION);

    const signUpData = {
      name: formData.name,
      email: formData.email,
      password: password,
      recaptchaV3Token: recaptchaToken,
    };

    const result = await authService.initiateSignUp(signUpData);

    if (result.success) {
      const { nextStep } = result.data;
      if (nextStep == 'verify_with_recaptcha_v2') {
        setStep('recaptcha-v2-challenge');
      } else if (nextStep == 'enter_verification_code') {
        setStep('verify-email-code');
      } else {
        setApiError('An unexpected server response. Please try again.');
      }
    } else {
      setApiError(result.error.message);
    }

    setIsLoading(false);
    setSubmissionType(null);
  };

  const processPasswordSubmit: SubmitHandler<PasswordStepFormData> = (data) => {
    handleInitiateSignUp(data.password);
  };

  const processWithoutPassword = () => {
    handleInitiateSignUp(null);
  };

  // RHF 유효성 검사 통과 후 실행될 함수
  // const processPasswordSubmit: SubmitHandler<PasswordStepFormData> = async (data) => {
  //   onUpdateData('password', data.password);
  //   setApiError(null);

  //   if (!executeRecaptcha) {
  //     setApiError('reCAPTCHA not ready. Please try again.');
  //     console.error('Execute recaptcha not yet available.');
  //     return;
  //   }

  //   setIsLoading(true);
  //   setIsPasswordSubmitClick(true);
  //   try {
  //     const recaptchaToken = await executeRecaptcha(RECAPTCHA_SIGN_UP_ACTION);
  //     const signUpData = {
  //       name: formData.name,
  //       email: formData.email,
  //       password: formData.password,
  //       recaptchaV3Token: recaptchaToken,
  //     };

  //     const response = await authService.initiateSignUp(signUpData);
  //     if (response.nextStep === 'verify_with_recaptcha_v2') {
  //       setStep('recaptcha-v2-challenge');
  //     } else if (response.nextStep === 'enter_verification_code') {
  //       setStep('verify-email-code');
  //     } else {
  //       setApiError('An unexpected error occurred. Please try again.');
  //     }
  //   } catch (error: unknown) {
  //     console.error('reCAPTCHA execution or signUp API request failed:', error);
  //     return;
  //   } finally {
  //     setIsLoading(false);
  //     setIsPasswordSubmitClick(false);
  //   }
  // };

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
                className="rounded-full p-0.5 text-gray-400 hover:text-gray-600 focus:outline-none"
                aria-label="Show password policy"
                aria-expanded={isPolicyVisible}
              >
                <Info size={16} />
              </button>
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
            errorMessage={errors.password?.message}
          />
        </div>

        {apiError && <p className="text-center text-sm text-red-600">{apiError}</p>}
        <div className="mt-8">
          <button
            type="submit"
            disabled={isLoading}
            className="group relative flex w-full justify-center rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {isLoading && submissionType === 'with_password' ? 'Processing...' : 'Continue'}
          </button>
        </div>
      </form>

      <div className="mt-6">
        <Divider text={'OR'} />
        <div className="mt-6">
          <button
            type="button"
            onClick={processWithoutPassword}
            disabled={isLoading}
            className="group relative flex w-full items-center justify-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
          >
            <Mail className="mr-2 h-4 w-4" />
            {isLoading && submissionType === 'without_password'
              ? 'Sending...'
              : 'Continue with email code'}
          </button>
        </div>
      </div>
    </>
  );
};
