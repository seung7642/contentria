'use client';

import { LoginFormData, LoginStep } from '@/components/auth/login/types';
import { PATHS } from '@/constants/paths';
import { SignUpEmailStepFormData, PasswordStepFormData } from '@/lib/schemas/authSchemas';
import { authService } from '@/services/authService';
import { useAuthStore } from '@/store/authStore';
import { ApiError } from '@/types/api/errors';
import { useRouter } from 'next/navigation';
import { createContext, useContext, useState } from 'react';
import { useGoogleReCaptcha } from 'react-google-recaptcha-v3';

type LoginFlowContextType = ReturnType<typeof useLoginFlowLogic>;

const LoginFlowContext = createContext<LoginFlowContextType | undefined>(undefined);

const useLoginFlowLogic = () => {
  const [step, setStep] = useState<LoginStep>('email');
  const [formData, setFormData] = useState<LoginFormData>({
    email: '',
    password: '',
    verificationCode: '',
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loginAttemptType, setLoginAttemptType] = useState<'password' | 'otp' | null>(null);
  const router = useRouter();
  const login = useAuthStore((state) => state.login);
  const { executeRecaptcha } = useGoogleReCaptcha();

  const updateFormData = (field: string, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    setError(null);
  };

  const setCurrentStep = (newStep: LoginStep) => {
    setStep(newStep);
    setError(null);
  };

  const goToNextStep = () => {
    if (step === 'email') {
      setStep('password');
    }
  };

  const goToPreviousStep = () => {
    if (step === 'password' || step === 'recaptcha_v2_challenge') {
      setStep('email');
    } else if (step === 'verify_otp_code') {
      setStep('password');
    }
  };

  const resetForm = () => {
    setFormData({
      email: '',
      password: '',
      verificationCode: '',
    });
    setCurrentStep('email');
    setError(null);
    setIsLoading(false);
    setLoginAttemptType(null);
  };

  const submitEmailStep = (data: SignUpEmailStepFormData) => {
    updateFormData('email', data.email);
    setStep('password');
  };

  const submitPasswordLogin = async (data: PasswordStepFormData) => {
    setLoginAttemptType('password');
    setIsLoading(true);
    setError(null);
    if (!executeRecaptcha) {
      setError('reCAPTCHA is not ready. Please try again.');
      setIsLoading(false);
      return;
    }

    try {
      const recaptchaV3Token = await executeRecaptcha('password');
      const result = await authService.loginWithPassword({
        email: formData.email,
        password: data.password,
        recaptchaV3Token,
      });
      login(result.user, result.accessToken);
      router.replace(PATHS.DASHBOARD);
    } catch (err) {
      if (err instanceof ApiError && err.status === 403 && err.code === 'C0005') {
        setStep('recaptcha_v2_challenge');
      } else if (err instanceof ApiError) {
        setError(err.message);
      } else {
        setError('An unexpected error occurred. Please try again.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const requestOtpLogin = async () => {
    setLoginAttemptType('otp');
    setIsLoading(true);
    setError(null);
    if (!executeRecaptcha) {
      setError('reCAPTCHA is not ready.');
      setIsLoading(false);
      return;
    }

    try {
      const recaptchaV3Token = await executeRecaptcha('otp');
      await authService.sendOtpCode({
        email: formData.email,
        recaptchaV3Token,
      });
      setStep('verify_otp_code');
    } catch (err) {
      if (err instanceof ApiError && err.status === 403 && err.code === 'C0005') {
        setStep('recaptcha_v2_challenge');
      } else if (err instanceof ApiError) {
        setError(err.message);
      } else {
        setError('An unexpected error occurred. Please try again.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const verifyRecaptchaAndProceed = async (v2Token: string) => {
    setIsLoading(true);
    setError(null);
    try {
      if (loginAttemptType === 'password') {
        const result = await authService.loginWithPassword({
          email: formData.email,
          password: formData.password,
          recaptchaV2Token: v2Token,
        });
        login(result.user, result.accessToken);
        router.replace(PATHS.DASHBOARD);
      } else if (loginAttemptType === 'otp') {
        await authService.sendOtpCode({
          email: formData.email,
          recaptchaV2Token: v2Token,
        });
        setStep('verify_otp_code');
      }
    } catch (err) {
      if (err instanceof ApiError && err.status === 403 && err.code === 'C0005') {
        setError('reCAPTCHA verification failed. Please try again.');
      } else if (err instanceof ApiError) {
        setStep('password');
        setError(err.message);
      } else {
        setError('An unexpected error occurred.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const verifyLoginCode = async (code: string) => {
    setIsLoading(true);
    setError(null);
    try {
      const result = await authService.verifyOtpCode({
        email: formData.email,
        verificationCode: code,
      });
      login(result.user, result.accessToken);
      router.replace(PATHS.DASHBOARD);
    } catch (err) {
      if (err instanceof ApiError) setError(err.message);
      else setError('Invalid or expired code.');
    } finally {
      setIsLoading(false);
    }
  };

  const resendLoginOtpCode = async () => {
    setIsLoading(true);
    setError(null);
    try {
      await authService.sendOtpCode({ email: formData.email });
    } catch (err) {
      if (err instanceof ApiError) {
        setError(err.message);
      } else {
        setError('An unexpected error occurred. Please try again.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const updateFormDataAndVerify = (field: string, value: string) => {
    updateFormData(field, value);
    if (field === 'verificationCode' && value.length === 6) {
      verifyLoginCode(value);
    }
  };

  return {
    step,
    setStep: setCurrentStep,
    formData,
    updateFormData,
    resetForm,
    isLoading,
    setIsLoading,
    error,
    setError,
    goToNextStep,
    goToPreviousStep,
    loginAttemptType,
    setLoginAttemptType,
    submitEmailStep,
    submitPasswordLogin,
    requestOtpLogin,
    verifyRecaptchaAndProceed,
    verifyLoginCode,
    resendLoginOtpCode,
    updateFormDataAndVerify,
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
