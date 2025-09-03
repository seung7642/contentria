import { LoginResponse } from '@/types/api/auth/login';
import {
  BaseStepProps,
  StepWithNext,
  RecaptchaV2StepProps01 as BaseRecaptchaV2StepProps,
  VerificationStepProps01 as BaseVerificationStepProps,
} from '../types';

// 1. Login 플로우에서 사용할 스텝들
export type LoginStep = 'email' | 'password' | 'recaptcha-v2-challenge' | 'verify-email-code';

// 2. Login 폼이 가지는 전체 데이터 구조
export interface LoginFormData {
  email: string;
  password: string;
  verificationCode: string;
}

// 3. 공통 타입을 Login 전용으로 구체화
export type LoginEmailStepProps = StepWithNext<LoginFormData, LoginStep>;

// Login의 PasswordStep은 다음 단계로 가는 기능이 없음
export type LoginPasswordStepProps = BaseStepProps<LoginFormData, LoginStep> & {
  goToPreviousStep: () => void;
};

export type LoginRecaptchaV2StepProps = BaseRecaptchaV2StepProps<
  LoginFormData,
  LoginStep,
  LoginResponse
>;

export type LoginVerificationStepProps = BaseVerificationStepProps<LoginFormData, LoginStep>;
