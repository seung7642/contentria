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

export interface VerifyOtpCodePayload {
  email: string;
  verificationCode: string;
}

export interface VerifyOtpCodeResponse {
  accessToken: string;
  user: User;
}
