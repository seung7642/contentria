import {
  StepWithNext,
  StepWithNavigation,
  RecaptchaV2StepProps as BaseRecaptchaV2StepProps,
  VerificationStepProps as BaseVerificationStepProps,
} from '../types';

// 1. SignUp 플로우에서 사용할 스텝들
export type SignUpStep = 'email' | 'password' | 'recaptcha_v2_challenge' | 'verify_otp_code';

// 2. SignUp 폼이 가지는 전체 데이터 구조
export interface SignUpFormData {
  name: string;
  email: string;
  password: string;
  verificationCode: string;
}

// 3. 공통 타입을 SignUp 전용으로 구체화
export type SignUpEmailStepProps = StepWithNext<SignUpFormData, SignUpStep>;

export type SignUpPasswordStepProps = StepWithNavigation<SignUpFormData, SignUpStep>;

export type SignUpRecaptchaV2StepProps = BaseRecaptchaV2StepProps<SignUpFormData, SignUpStep>;

export type SignUpVerificationStepProps = BaseVerificationStepProps<SignUpFormData, SignUpStep>;
