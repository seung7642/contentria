'use client';

import AuthFormCard from '@/components/auth/AuthFormCard';
import EmailStep from '@/components/auth/login/EmailStep';
import PasswordStep from '@/components/auth/login/PasswordStep';
import RecaptchaV2Step from '@/components/auth/RecaptchaV2Step';
import { VerificationStep } from '@/components/auth/VerificationStep';
import { PATHS } from '@/constants/paths';
import { useLoginFlow } from '@/hooks/useLoginFlow';
import { authService } from '@/services/authService';
import { useAuthStore } from '@/store/authStore';
import { User } from '@/types/user';
import { useRouter } from 'next/navigation';

const LoginPage = () => {
  const loginFlow = useLoginFlow();
  const router = useRouter();
  const setUser = useAuthStore((state) => state.setUser);

  const getTitle = () => (loginFlow.step === 'verify_otp_code' ? 'Verify your email' : 'Sign in');

  const handleRecaptchaVerify = async (v2Token: string) => {
    const { loginAttemptType } = loginFlow;

    let result;
    if (loginAttemptType === 'password') {
      result = await authService.loginWithPassword({
        email: loginFlow.formData.email,
        password: loginFlow.formData.password,
        recaptchaV2Token: v2Token,
      });
      if (result.success) {
        if (result.data.nextStep === 'complete') {
          window.location.href = PATHS.DASHBOARD;
        } else {
          loginFlow.setError('An unexpected server response. Please try again.');
        }
      } else {
        loginFlow.setError(result.error.message);
      }
    } else if (loginAttemptType === 'otp') {
      result = await authService.sendOtpCode({
        email: loginFlow.formData.email,
        recaptchaV2Token: v2Token,
      });
      if (result.success) {
        if (result.data.nextStep === 'enter_verification_code') {
          loginFlow.setStep('verify_otp_code');
        } else {
          loginFlow.setError('An unexpected server response. Please try again.');
        }
      } else {
        loginFlow.setError(result.error.message);
      }
    } else {
      loginFlow.setError('Login flow error: attempt type is unknown.');
    }
  };

  const handleVerificationCodeComplete = (user: User | null) => {
    setUser(user);
    router.replace(PATHS.DASHBOARD);
    loginFlow.resetForm();
  };

  const renderCurrentStep = () => {
    const commonProps = {
      formData: loginFlow.formData,
      onUpdateData: loginFlow.updateFormData,
      isLoading: loginFlow.isLoading,
      setIsLoading: loginFlow.setIsLoading,
      error: loginFlow.error,
      setError: loginFlow.setError,
      setStep: loginFlow.setStep,
      goToNextStep: loginFlow.goToNextStep,
      goToPreviousStep: loginFlow.goToPreviousStep,
      loginAttemptType: loginFlow.loginAttemptType,
      setLoginAttemptType: loginFlow.setLoginAttemptType,
    };

    switch (loginFlow.step) {
      case 'email':
        return <EmailStep {...commonProps} />;
      case 'password':
        return <PasswordStep {...commonProps} />;
      case 'recaptcha_v2_challenge':
        return <RecaptchaV2Step {...commonProps} onVerify={handleRecaptchaVerify} />;
      case 'verify_otp_code':
        return <VerificationStep {...commonProps} onComplete={handleVerificationCodeComplete} />;
      default:
        return null;
    }
  };

  return (
    <div className="flex min-h-screen items-start justify-center bg-gray-50 px-4 pt-60 sm:px-6 lg:px-8">
      <div className="w-full max-w-md space-y-8">
        <AuthFormCard title={getTitle()}>{renderCurrentStep()}</AuthFormCard>
      </div>
    </div>
  );
};

export default LoginPage;
