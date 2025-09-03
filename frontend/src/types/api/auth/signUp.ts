import { User } from '../../user';

export interface InitiateSignUpPayload {
  email: string;
  name: string;
  password: string | null;
  recaptchaV3Token?: string;
  recaptchaV2Token?: string | null;
}

export interface InitiateSignUpResponse {
  status: string;
  nextStep: string;
}

export interface VerifyCodePayload {
  email: string;
  code: string;
}

export interface VerifyCodeResponse {
  message?: string;
  user?: User;
  token?: string;
}

export interface RequestVerificationCodePayload {
  email: string;
}

export interface RequestVerificationCodeResponse {
  message?: string;
  isSuccess: boolean;
}

export interface VerifyOtpCodePayload {
  email: string;
  verificationCode: string;
}

export interface VerifyOtpCodeResponse {
  message?: string;
  user?: User;
  token?: string;
}
