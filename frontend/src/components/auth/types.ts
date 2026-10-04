import { Dispatch, SetStateAction } from 'react';
import { VerifyOtpCodeResponse } from '@/types/api/auth';

export type SignUpStep = 'email' | 'password' | 'recaptcha_v2_challenge' | 'verify_otp_code';

// 제네릭을 사용하여 어떤 폼 데이터(TFormData)와 스텝(TStep)이든 받을 수 있는 기본 스텝 Props
export interface BaseStepProps<TFormData, TStep extends string> {
  formData: TFormData;
  onUpdateData: <K extends keyof TFormData>(field: K, value: TFormData[K]) => void;
  resetForm: () => void;
  isLoading: boolean;
  setIsLoading: Dispatch<SetStateAction<boolean>>;
  error: string | null;
  setError: Dispatch<SetStateAction<string | null>>;
  setStep: (step: TStep) => void;
}

// 다음 단계로 가능 기능이 있는 스텝
export interface StepWithNext<TFormData, TStep extends string>
  extends BaseStepProps<TFormData, TStep> {
  goToNextStep: () => void;
}

// 이전 단계로 가는 기능이 있는 스텝
export interface StepWithPrevious<TFormData, TStep extends string>
  extends BaseStepProps<TFormData, TStep> {
  goToPreviousStep: () => void;
}

// 앞/뒤로 모두 이동 가능한 스텝
export interface StepWithNavigation<TFormData, TStep extends string>
  extends StepWithNext<TFormData, TStep>,
    StepWithPrevious<TFormData, TStep> {}

// reCAPTCHA V2 챌린지를 위한 스텝. API 응답 타입을 제네릭으로 받음
export interface RecaptchaV2StepProps<TFormData, TStep extends string>
  extends StepWithPrevious<TFormData, TStep> {
  onVerify: (v2Token: string) => void;
}

// 이메일 코드 검증을 위한 스텝
export interface VerificationStepProps<TFormData, TStep extends string>
  extends StepWithPrevious<TFormData, TStep> {
  onComplete: (data: VerifyOtpCodeResponse) => void;
}

export interface VerifiableFormData {
  email: string;
  verificationCode: string;
}
