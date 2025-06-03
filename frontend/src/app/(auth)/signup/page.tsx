'use client';

import AuthFormCard from '@/components/auth/AuthFormCard';
import { EmailStep } from '@/components/auth/signup/EmailStep';
import { PasswordStep } from '@/components/auth/signup/PasswordStep';
import RecaptchaV2Step from '@/components/auth/signup/RecaptchaV2Step';
import { VerificationStep } from '@/components/auth/signup/VerificationStep';
import { useSignUpFlow } from '@/hooks/useSignUpFlow';

const SignUpPage = () => {
  const signUpFlow = useSignUpFlow();

  const getTitle = () => {
    switch (signUpFlow.step) {
      case 'verify-email-code':
        return 'Verify your email';
      default:
        return 'Sign up';
    }
  };

  const renderCurrentStep = () => {
    const commonProps = {
      setStep: signUpFlow.setStep,
      formData: signUpFlow.formData,
      onUpdateData: signUpFlow.updateFormData,
      isLoading: signUpFlow.isLoading,
      setIsLoading: signUpFlow.setIsLoading,
      error: signUpFlow.error,
      setError: signUpFlow.setError,
      goToNextStep: signUpFlow.goToNextStep,
      goToPreviousStep: signUpFlow.goToPreviousStep,
    };

    switch (signUpFlow.step) {
      case 'email':
        return <EmailStep {...commonProps} />;
      case 'password-creation':
        return <PasswordStep {...commonProps} />;
      case 'recaptcha-v2-challenge':
        return <RecaptchaV2Step {...commonProps} />;
      case 'verify-email-code':
        return <VerificationStep {...commonProps} onComplete={signUpFlow.resetForm} />;
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
