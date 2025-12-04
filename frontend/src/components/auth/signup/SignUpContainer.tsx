'use client';

import { SignUpFlowProvider, useSignUpFlow } from '@/hooks/useSignUpFlow';
import AuthFormCard from '../AuthFormCard';
import { VerificationStep } from '../VerificationStep';
import RecaptchaV2Step from '../RecaptchaV2Step';
import { PasswordStep } from './PasswordStep';
import { EmailStep } from './EmailStep';

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

export default function SignUpContainer() {
  return (
    <SignUpFlowProvider>
      <SignUpFlow />
    </SignUpFlowProvider>
  );
}
