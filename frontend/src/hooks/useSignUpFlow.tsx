import { SignUpStep } from '@/components/auth/types';
import { RECAPTCHA_SIGN_UP_ACTION } from '@/constants/auth';
import { PATHS } from '@/constants/paths';
import { ApiError } from '@/errors/ApiError';
import { SignUpEmailStepFormData } from '@/lib/schemas/authSchemas';
import { authService } from '@/services/authService';
import { useAuthStore } from '@/store/authStore';
import { useRouter } from 'next/navigation';
import { createContext, useContext, useState, ReactNode } from 'react';
import { useGoogleReCaptcha } from 'react-google-recaptcha-v3';

// 훅의 반환 타입을 정의 (React Context 사용 시 필요)
type SignUpFlowContextType = ReturnType<typeof useSignUpFlowLogic>;

// React Context를 생성한다.
const SignUpFlowContext = createContext<SignUpFlowContextType | undefined>(undefined);

const useSignUpFlowLogic = () => {
  const steps: SignUpStep[] = ['email', 'password', 'recaptcha_v2_challenge', 'verify_otp_code'];
  const [step, setStep] = useState<SignUpStep>('email');
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    verificationCode: '',
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { executeRecaptcha } = useGoogleReCaptcha();
  const login = useAuthStore((state) => state.login);
  const router = useRouter();

  const updateFormData = (field: string, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    if (error) {
      setError(null);
    }
  };

  const setCurrentStep = (step: SignUpStep) => {
    setStep(step);
    setError(null);
  };

  const goToNextStep = () => {
    const currentIndex = steps.indexOf(step);
    if (currentIndex < steps.length - 1) {
      setStep(steps[currentIndex + 1]);
      setError(null);
    }
  };

  const goToPreviousStep = () => {
    const currentIndex = steps.indexOf(step);
    if (currentIndex > 0) {
      setStep(steps[currentIndex - 1]);
      setError(null);
      if (step === 'password') {
        setFormData((prev) => ({ ...prev, password: '' }));
      } else if (step === 'recaptcha_v2_challenge') {
        setFormData((prev) => ({ ...prev }));
      } else if (step === 'verify_otp_code') {
        setFormData((prev) => ({ ...prev, verificationCode: '' }));
      }
    }
  };

  const resetForm = () => {
    setFormData({
      name: '',
      email: '',
      password: '',
      verificationCode: '',
    });
    setCurrentStep('email');
    setError(null);
    setIsLoading(false);
  };

  const submitEmailStep = (data: SignUpEmailStepFormData) => {
    updateFormData('name', data.name);
    updateFormData('email', data.email);
    goToNextStep();
  };

  const initiateSignUp = async (recaptchaV2Token: string) => {
    setIsLoading(true);
    setError(null);

    try {
      const payload = {
        email: formData.email,
        name: formData.name,
        password: formData.password,
        recaptchaV2Token,
      };
      await authService.initiateSignUp(payload);
      setStep('verify_otp_code');
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

  const submitPasswordStep = async (password: string | null) => {
    if (!executeRecaptcha) {
      setError('reCAPTCHA is not ready. Please try again.');
      return;
    }

    setIsLoading(true);
    setError(null);
    updateFormData('password', password || '');

    try {
      const recaptchaToken = await executeRecaptcha(RECAPTCHA_SIGN_UP_ACTION);

      const payload = {
        name: formData.name,
        email: formData.email,
        password: password,
        recaptchaV3Token: recaptchaToken,
      };
      await authService.initiateSignUp(payload);
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

  const verifySignUpOtpCode = async (code: string) => {
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
      if (err instanceof ApiError) {
        setError(err.message);
      } else {
        setError('An unexpected error occurred. Please try again.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const resendSignUpOtpCode = async () => {
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
      verifySignUpOtpCode(value);
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
    submitEmailStep,
    initiateSignUp,
    submitPasswordStep,
    verifySignUpOtpCode,
    resendSignUpOtpCode,
    updateFormDataAndVerify,
  };
};

export const SignUpFlowProvider = ({ children }: { children: ReactNode }) => {
  const flow = useSignUpFlowLogic();
  return <SignUpFlowContext.Provider value={flow}>{children}</SignUpFlowContext.Provider>;
};

export const useSignUpFlow = (): SignUpFlowContextType => {
  const context = useContext(SignUpFlowContext);
  if (context === undefined) {
    throw new Error('useSignUpFlow must be used within a SignUpFlowProvider');
  }
  return context;
};
