'use client';

import AuthFormCard from '@/components/auth/AuthFormCard';
import EmailStep from '@/components/auth/login/EmailStep';
import PasswordStep from '@/components/auth/login/PasswordStep';
import { LoginFormData, LoginStep } from '@/components/auth/login/types';
import RecaptchaV2Step from '@/components/auth/RecaptchaV2Step';
import { BaseStepProps } from '@/components/auth/types';
import { VerificationStep } from '@/components/auth/VerificationStep';
import { PATHS } from '@/constants/paths';
import { useLoginFlow } from '@/hooks/useLoginFlow';
import { authService } from '@/services/authService';
import { LoginResponse } from '@/types/api/auth/login';
import { ApiResult } from '@/types/api/result';

const LoginPage = () => {
  const loginFlow = useLoginFlow();

  const handleRecaptchaVerify = async (v2Token: string): Promise<ApiResult<LoginResponse>> => {
    const loginData = {
      email: loginFlow.formData.email,
      password: loginFlow.formData.password,
      recaptchaV2Token: v2Token,
    };

    const result = await authService.loginWithPassword(loginData);
    if (result.success) {
      if (result.data.nextStep === 'complete') {
        window.location.href = PATHS.DASHBOARD;
      } else {
        loginFlow.setError('An unexpected server response. Please try again.');
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
    const commonProps: BaseStepProps<LoginFormData, LoginStep> = {
      formData: loginFlow.formData,
      onUpdateData: loginFlow.updateFormData,
      isLoading: loginFlow.isLoading,
      setIsLoading: loginFlow.setIsLoading,
      error: loginFlow.error,
      setError: loginFlow.setError,
      setStep: loginFlow.setStep,
    };

    switch (loginFlow.step) {
      case 'email':
        return <EmailStep {...commonProps} goToNextStep={loginFlow.goToNextStep} />;
      case 'password':
        return <PasswordStep {...commonProps} goToPreviousStep={loginFlow.goToPreviousStep} />;
      case 'recaptcha-v2-challenge':
        return (
          <RecaptchaV2Step
            {...commonProps}
            goToPreviousStep={loginFlow.goToPreviousStep}
            onVerify={handleRecaptchaVerify}
          />
        );
      case 'verify-email-code':
        return (
          <VerificationStep
            {...commonProps}
            goToPreviousStep={loginFlow.goToPreviousStep}
            onComplete={() => {}}
          />
        );
      default:
        return null;
    }
  };

  return <AuthFormCard title={getTitle()}>{renderCurrentStep()}</AuthFormCard>;
};

export default LoginPage;
