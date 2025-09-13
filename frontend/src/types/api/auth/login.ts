import { User } from '@/types/user';

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
