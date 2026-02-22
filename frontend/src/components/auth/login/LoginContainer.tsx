'use client';

import { LoginFlowProvider, useLoginFlow } from '@/hooks/useLoginFlow';
import AuthFormCard from '../AuthFormCard';
import VerificationStep from '../VerificationStep';
import RecaptchaV2Step from '../RecaptchaV2Step';
import PasswordStep from './PasswordStep';
import EmailStep from './EmailStep';

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

export default function LoginContainer() {
  return (
    <LoginFlowProvider>
      <LoginFlow />
    </LoginFlowProvider>
  );
}
