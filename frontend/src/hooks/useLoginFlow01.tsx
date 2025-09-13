import { LoginFormData, LoginStep } from '@/components/auth/login/types';
import { PasswordStepFormData, LoginEmailStepFormData } from '@/lib/schemas/authSchemas';
import { ApiError } from '@/types/api/errors';
import { createContext, useContext, useEffect, useState } from 'react';
import { useGoogleReCaptcha } from 'react-google-recaptcha-v3';
import {
  useLoginWithPasswordMutation,
  useSendOtpMutation,
  useVerifyOtpMutation,
} from './queries/useAuthMutations';

type LoginFlowContextType = ReturnType<typeof useLoginFlowLogic>;

const LoginFlowContext = createContext<LoginFlowContextType | undefined>(undefined);

const useLoginFlowLogic = () => {
  const [step, setStep] = useState<LoginStep>('email');
  const [formData, setFormData] = useState<LoginFormData>({
    email: '',
    password: '',
    verificationCode: '',
  });
  const [loginAttemptType, setLoginAttemptType] = useState<'password' | 'otp' | null>(null);
  const { executeRecaptcha } = useGoogleReCaptcha();

  const {
    mutate: loginWithPassword,
    isPending: isLoggingIn,
    error: loginError,
  } = useLoginWithPasswordMutation();
  const {
    mutate: sendOtp,
    isPending: isSendingOtp,
    error: sendOtpError,
  } = useSendOtpMutation(() => setStep('verify_otp_code'));
  const { mutate: verifyOtp, isPending: isVerifying, error: verifyError } = useVerifyOtpMutation();

  const isLoading = isLoggingIn || isSendingOtp || isVerifying;
  const combinedError = (loginError || sendOtpError || verifyError) as ApiError | null;

  useEffect(() => {
    if (combinedError && combinedError.status === 403 && combinedError.code === 'C0005') {
      setStep('recaptcha_v2_challenge');
    }
  }, [combinedError]);

  const updateFormData = (field: keyof LoginFormData, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const goToPreviousStep = () => {
    if (step === 'password' || step === 'recaptcha_v2_challenge') {
      setStep('email');
    } else if (step === 'verify_otp_code') {
      setStep('password');
    }
  };

  const submitEmailStep = (data: LoginEmailStepFormData) => {
    console.log('Email submitted:', data.email);
    updateFormData('email', data.email);
    setStep('password');
  };

  const submitPasswordLogin = async (data: PasswordStepFormData) => {
    setLoginAttemptType('password');
    if (!executeRecaptcha) {
      return;
    }

    try {
      const recaptchaV3Token = await executeRecaptcha('password');
      loginWithPassword({
        email: formData.email,
        password: data.password,
        recaptchaV3Token,
      });
    } catch (e) {
      console.error('reCAPTCHA execution error:', e);
    }
  };

  const requestOtpLogin = async () => {
    setLoginAttemptType('otp');
    if (!executeRecaptcha) {
      return;
    }

    try {
      const recaptchaV3Token = await executeRecaptcha('otp');
      sendOtp({
        email: formData.email,
        recaptchaV3Token,
      });
    } catch (e) {
      console.error('reCAPTCHA execution error:', e);
    }
  };

  const verifyRecaptchaAndProceed = async (recaptchaV2Token: string) => {
    if (loginAttemptType === 'password') {
      loginWithPassword({
        email: formData.email,
        password: formData.password,
        recaptchaV2Token,
      });
    } else if (loginAttemptType === 'otp') {
      sendOtp({
        email: formData.email,
        recaptchaV2Token,
      });
    }
  };

  const updateFormDataAndVerify = (field: keyof LoginFormData, value: string) => {
    updateFormData(field, value);
    if (field === 'verificationCode' && value.length === 6) {
      verifyOtp({ email: formData.email, verificationCode: value });
    }
  };

  const resendLoginOtpCode = async () => sendOtp({ email: formData.email });

  const startGoogleLogin = () => {
    const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL;
    if (baseUrl) {
      window.location.href = `${baseUrl}/api/oauth2/authorization/google`;
    } else {
      console.error('API base URL is not configured.');
    }
  };

  return {
    step,
    formData,
    isLoading,
    error: combinedError instanceof ApiError && combinedError.status !== 403 ? combinedError : null,
    loginAttemptType,
    goToPreviousStep,
    submitEmailStep,
    submitPasswordLogin,
    requestOtpLogin,
    verifyRecaptchaAndProceed,
    updateFormDataAndVerify,
    resendLoginOtpCode,
    startGoogleLogin,
  };
};

export const LoginFlowProvider = ({ children }: { children: React.ReactNode }) => {
  const flow = useLoginFlowLogic();
  return <LoginFlowContext.Provider value={flow}>{children}</LoginFlowContext.Provider>;
};

export const useLoginFlow = (): LoginFlowContextType => {
  const context = useContext(LoginFlowContext);
  if (context === undefined) {
    throw new Error('useLoginFlow must be used within a LoginFlowProvider');
  }
  return context;
};
