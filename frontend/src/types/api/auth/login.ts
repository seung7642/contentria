import { User } from '@/types/user';

export interface LoginPayload {
  email: string;
  password: string | null;
  recaptchaV3Token?: string;
  recaptchaV2Token?: string | null;
}

export interface LoginResponse {
  message?: string;
  user?: User;
  token?: string;
  nextStep: string;
}
