'use client';

import AuthFormCard from '@/components/auth/AuthFormCard';
import EmailStep from '@/components/auth/login/EmailStep';
import PasswordStep from '@/components/auth/login/PasswordStep';
import RecaptchaV2Step from '@/components/auth/RecaptchaV2Step';
import { VerificationStep } from '@/components/auth/VerificationStep';
import { LoginFlowProvider, useLoginFlow } from '@/hooks/useLoginFlow01';

const LoginFlow = () => {
  const loginFlow = useLoginFlow();

  const getTitle = () => (loginFlow.step === 'verify_otp_code' ? 'Verify your email' : 'Sign in');

  const renderCurrentStep = () => {
    switch (loginFlow.step) {
      case 'email':
        return <EmailStep />;
      case 'password':
        return <PasswordStep />;
      case 'recaptcha_v2_challenge':
        return (
          <RecaptchaV2Step
            isLoading={loginFlow.isLoading}
            error={loginFlow.error?.message || null}
            goToPreviousStep={loginFlow.goToPreviousStep}
            onVerify={loginFlow.verifyRecaptchaAndProceed}
            onError={(errorMessage) => console.error('reCAPTCHA error:', errorMessage)}
          />
        );
      case 'verify_otp_code':
        return (
          <VerificationStep
            email={loginFlow.formData.email}
            verificationCode={loginFlow.formData.verificationCode}
            isLoading={loginFlow.isLoading}
            error={loginFlow.error?.message || null}
            onCodeChange={(code) => loginFlow.updateFormDataAndVerify('verificationCode', code)}
            onResendCode={() => loginFlow.resendLoginOtpCode()}
          />
        );
      default:
        return null;
    }
  };

  return <AuthFormCard title={getTitle()}>{renderCurrentStep()}</AuthFormCard>;
};

const LoginPage = () => {
  return (
    <LoginFlowProvider>
      <div className="flex min-h-screen items-start justify-center bg-gray-50 px-4 pt-60 sm:px-6 lg:px-8">
        <div className="w-full max-w-md space-y-8">
          <LoginFlow />
        </div>
      </div>
    </LoginFlowProvider>
  );
};

export default LoginPage;
