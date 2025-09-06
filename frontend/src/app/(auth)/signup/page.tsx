'use client';

import AuthFormCard from '@/components/auth/AuthFormCard';
import { EmailStep } from '@/components/auth/signup/EmailStep';
import { PasswordStep } from '@/components/auth/signup/PasswordStep';
import RecaptchaV2Step from '@/components/auth/RecaptchaV2Step';
import { VerificationStep } from '@/components/auth/VerificationStep';
import { useSignUpFlow } from '@/hooks/useSignUpFlow';
import { authService } from '@/services/authService';
import { User } from '@/types/user';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/store/authStore';
import { PATHS } from '@/constants/paths';

const SignUpPage = () => {
  const signUpFlow = useSignUpFlow();
  const router = useRouter();
  const setUser = useAuthStore((state) => state.setUser);

  const getTitle = () => (signUpFlow.step === 'verify_otp_code' ? 'Verify your email' : 'Sign up');

  const handleRecaptchaVerify = async (v2Token: string) => {
    const signUpData = {
      email: signUpFlow.formData.email,
      name: signUpFlow.formData.name,
      password: signUpFlow.formData.password,
      recaptchaV2Token: v2Token,
    };

    const result = await authService.initiateSignUp(signUpData);
    if (result.success) {
      if (result.data.nextStep === 'enter_verification_code') {
        signUpFlow.setStep('verify_otp_code');
      } else {
        signUpFlow.setError('An unexpected server response. Please try again.');
      }
    } else {
      signUpFlow.setError(result.error.message);
    }
  };

  const handleVerificationCodeComplete = (user: User | null) => {
    setUser(user);
    router.replace(PATHS.DASHBOARD);
    signUpFlow.resetForm();
  };

  const renderCurrentStep = () => {
    const commonProps = {
      formData: signUpFlow.formData,
      onUpdateData: signUpFlow.updateFormData,
      isLoading: signUpFlow.isLoading,
      setIsLoading: signUpFlow.setIsLoading,
      error: signUpFlow.error,
      setError: signUpFlow.setError,
      setStep: signUpFlow.setStep,
      goToNextStep: signUpFlow.goToNextStep,
      goToPreviousStep: signUpFlow.goToPreviousStep,
    };

    switch (signUpFlow.step) {
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

export default SignUpPage;
