'use client';

import AuthFormCard from '@/components/auth/AuthFormCard';
import { EmailStep } from '@/components/auth/signup/EmailStep';
import { PasswordStep } from '@/components/auth/signup/PasswordStep';
import RecaptchaV2Step from '@/components/auth/RecaptchaV2Step';
import { VerificationStep } from '@/components/auth/VerificationStep';
import { SignUpFlowProvider, useSignUpFlow } from '@/hooks/useSignUpFlow01';

const SignUpFlow = () => {
  const signUpFlow = useSignUpFlow();

  const getTitle = () => (signUpFlow.step === 'verify_otp_code' ? 'Verify your email' : 'Sign up');

  const renderCurrentStep = () => {
    switch (signUpFlow.step) {
      case 'email':
        return <EmailStep />;
      case 'password':
        return <PasswordStep />;
      case 'recaptcha_v2_challenge':
        return (
          <RecaptchaV2Step
            isLoading={signUpFlow.isLoading}
            error={signUpFlow.error?.message || null}
            goToPreviousStep={signUpFlow.goToPreviousStep}
            onVerify={signUpFlow.submitRecaptchaV2}
            onError={(errorMessage) => console.error('reCAPTCHA error:', errorMessage)}
          />
        );
      case 'verify_otp_code':
        return (
          <VerificationStep
            email={signUpFlow.formData.email}
            verificationCode={signUpFlow.formData.verificationCode}
            isLoading={signUpFlow.isLoading}
            error={signUpFlow.error?.message || null}
            onCodeChange={(code) => signUpFlow.updateFormDataAndVerify('verificationCode', code)}
            onResendCode={() => signUpFlow.resendSignUpOtpCode()}
          />
        );
      default:
        return null;
    }
  };

  return <AuthFormCard title={getTitle()}>{renderCurrentStep()}</AuthFormCard>;
};

const SignUpPage = () => {
  return (
    <SignUpFlowProvider>
      <div className="flex min-h-screen items-start justify-center bg-gray-50 px-4 pt-60 sm:px-6 lg:px-8">
        <div className="w-full max-w-md space-y-8">
          <SignUpFlow />
        </div>
      </div>
    </SignUpFlowProvider>
  );
};

export default SignUpPage;
