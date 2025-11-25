import { User } from '@/types/user';

// ===============================================
// Login 관련 타입들
// ===============================================
export interface LoginPayload {
  email: string;
  password: string | null;
  recaptchaV3Token?: string;
  recaptchaV2Token?: string | null;
}

export interface LoginResponse {
  user: User;
}

export interface SendOtpPayload {
  email: string;
  recaptchaV3Token?: string;
  recaptchaV2Token?: string | null;
}

export interface SendOtpResponse {
  message?: string;
}

// ===============================================
// Sign Up 관련 타입들
// ===============================================
export interface InitiateSignUpPayload {
  email: string;
  name: string;
  password: string | null;
  recaptchaV3Token?: string;
  recaptchaV2Token?: string | null;
}

export interface InitiateSignUpResponse {
  status: string;
}

export interface VerifyOtpCodePayload {
  email: string;
  verificationCode: string;
}

export interface VerifyOtpCodeResponse {
  user: User;
}
