import { SignUpStep } from '@/components/auth/types';
import { RECAPTCHA_SIGN_UP_ACTION } from '@/constants/auth';
import { ApiError } from '@/errors/ApiError';
import { SignUpEmailStepFormData } from '@/lib/schemas/authSchemas';
import { createContext, useContext, useState, ReactNode, useEffect } from 'react';
import { useGoogleReCaptcha } from 'react-google-recaptcha-v3';
import {
  useInitiateSignUpMutation,
  useSendOtpMutation,
  useVerifyOtpMutation,
} from './queries/useAuthMutations';

// 훅의 반환 타입을 정의 (React Context 사용 시 필요)
type SignUpFlowContextType = ReturnType<typeof useSignUpFlowLogic>;

// React Context를 생성한다.
const SignUpFlowContext = createContext<SignUpFlowContextType | undefined>(undefined);

const useSignUpFlowLogic = () => {
  const [step, setStep] = useState<SignUpStep>('email');
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    verificationCode: '',
  });
  const { executeRecaptcha } = useGoogleReCaptcha();

  const {
    mutate: initiateSignUp,
    isPending: isInitiating,
    error: initiateError,
    reset: resetInitiateSignUp,
  } = useInitiateSignUpMutation(() => setStep('verify_otp_code'));
  const { mutate: resendOtp, isPending: isResending, error: resendError } = useSendOtpMutation();
  const { mutate: verifyOtp, isPending: isVerifying, error: verifyError } = useVerifyOtpMutation();

  const isLoading = isInitiating || isVerifying || isResending;
  const combinedError = (initiateError || verifyError || resendError) as ApiError | null;

  useEffect(() => {
    if (combinedError && combinedError.status === 403 && combinedError.code === 'C0005') {
      setStep('recaptcha_v2_challenge');
      resetInitiateSignUp();
    }
  }, [combinedError, resetInitiateSignUp]);

  const updateFormData = (field: string, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const goToNextStep = () => {
    const steps: SignUpStep[] = ['email', 'password', 'recaptcha_v2_challenge', 'verify_otp_code'];
    const currentIndex = steps.indexOf(step);
    if (currentIndex < steps.length - 1) {
      setStep(steps[currentIndex + 1]);
    }
  };

  const goToPreviousStep = () => {
    const steps: SignUpStep[] = ['email', 'password', 'recaptcha_v2_challenge', 'verify_otp_code'];
    const currentIndex = steps.indexOf(step);
    if (currentIndex > 0) {
      setStep(steps[currentIndex - 1]);
      if (step === 'password') {
        setFormData((prev) => ({ ...prev, password: '' }));
      } else if (step === 'recaptcha_v2_challenge') {
        setFormData((prev) => ({ ...prev }));
      } else if (step === 'verify_otp_code') {
        setFormData((prev) => ({ ...prev, verificationCode: '' }));
      }
    }
  };

  const submitEmailStep = (data: SignUpEmailStepFormData) => {
    updateFormData('name', data.name);
    updateFormData('email', data.email);
    goToNextStep();
  };

  const submitPasswordStep = async (password: string | null) => {
    if (!executeRecaptcha) {
      return;
    }
    updateFormData('password', password || '');
    try {
      const recaptchaV3Token = await executeRecaptcha(RECAPTCHA_SIGN_UP_ACTION);
      initiateSignUp({
        name: formData.name,
        email: formData.email,
        password,
        recaptchaV3Token,
      });
    } catch (err) {
      console.error('reCAPTCHA execution error:', err);
    }
  };

  const submitRecaptchaV2 = (recaptchaV2Token: string) => {
    initiateSignUp({
      name: formData.name,
      email: formData.email,
      password: formData.password,
      recaptchaV2Token,
    });
  };

  const resendSignUpOtpCode = () => resendOtp({ email: formData.email });

  const updateFormDataAndVerify = (field: string, value: string) => {
    updateFormData(field, value);
    if (field === 'verificationCode' && value.length === 6) {
      verifyOtp({ email: formData.email, verificationCode: value });
    }
  };

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
    error: combinedError,
    initiateError,
    verifyError,
    resendError,
    updateFormData,
    goToNextStep,
    goToPreviousStep,
    submitEmailStep,
    submitPasswordStep,
    submitRecaptchaV2,
    resendSignUpOtpCode,
    updateFormDataAndVerify,
    startGoogleLogin,
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
