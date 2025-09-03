'use client';

import AuthFormCard from '@/components/auth/AuthFormCard';
import EmailStep from '@/components/auth/login/EmailStep';
import PasswordStep from '@/components/auth/login/PasswordStep';
import RecaptchaV2Step from '@/components/auth/RecaptchaV2Step';
import { VerificationStep } from '@/components/auth/VerificationStep';
import { PATHS } from '@/constants/paths';
import { useLoginFlow } from '@/hooks/useLoginFlow';
import { authService } from '@/services/authService';
import { LoginResponse } from '@/types/api/auth/login';
import { ApiResult } from '@/types/api/result';

const LoginPage = () => {
  const loginFlow = useLoginFlow();

  const handleRecaptchaVerify = async (v2Token: string): Promise<ApiResult<LoginResponse>> => {
    const result = await authService.login({
      email: loginFlow.formData.email,
      password: loginFlow.formData.password,
      recaptchaV2Token: v2Token,
    });

    if (result.success) {
      if (result.data.nextStep === 'complete') {
        window.location.href = PATHS.DASHBOARD;
      } else if (result.data.nextStep) {
        loginFlow.setStep(result.data.nextStep);
      }
    } else {
      if (result.error.code === 'AUTH0002') {
        loginFlow.setStep('password');
      }
      loginFlow.setError(result.error.message);
    }
    return result;
  };

  const getTitle = () => 'Sign in';

  const renderCurrentStep = () => {
    const commonProps = {
      formData: loginFlow.formData,
      onUpdateData: loginFlow.updateFormData,
      isLoading: loginFlow.isLoading,
      setIsLoading: loginFlow.setIsLoading,
      error: loginFlow.error,
      setError: loginFlow.setError,
      goToPreviousStep: loginFlow.goToPreviousStep,
      setStep: loginFlow.setStep,
    };

    switch (loginFlow.step) {
      case 'email':
        return <EmailStep {...commonProps} goToNextStep={loginFlow.goToNextStep} />;
      case 'password':
        return <PasswordStep {...commonProps} />;
      case 'recaptcha-v2-challenge':
        return <RecaptchaV2Step {...commonProps} onVerify={handleRecaptchaVerify} />;
      case 'verify-email-code':
        return <VerificationStep {...commonProps} onComplete={() => {}} />;
      default:
        return null;
    }
  };

  return <AuthFormCard title={getTitle()}>{renderCurrentStep()}</AuthFormCard>;
};

export default LoginPage;
