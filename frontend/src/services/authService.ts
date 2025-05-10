import apiClient from '@/lib/apiClient';
import { User } from '@/store/authStore';

interface BaseApiResponse {
  message?: string;
}

export interface LoginPayload {
  email: string;
  password?: string;
  code?: string;
}

export interface LoginResponse extends BaseApiResponse {
  user: User;
  token: string;
}

export interface RequestCodeResponse extends BaseApiResponse {
  // TODO: Define the structure of the response if needed
  isSuccess: boolean;
}

export interface CheckEmailPayload {
  email: string;
}
export interface CheckEmailResponse extends BaseApiResponse {
  isAvailable?: boolean;
}

export interface InitiateSignupPayload {
  email: string;
  name?: string;
  password?: string;
}
export interface InitiateSignupResponse extends BaseApiResponse {
  isSuccess: boolean;
}

export interface VerifyCodePayload {
  email: string;
  code: string;
}
export interface VerifyCodeResponse extends BaseApiResponse {
  user?: User;
  token?: string;
}

export interface ResendCodePayload {
  email: string;
}
export interface ResendCodeResponse extends BaseApiResponse {
  isSuccess: boolean;
}

export const authService = {
  async login(payload: LoginPayload): Promise<LoginResponse> {
    const endpoint = '/api/auth/login';
    return apiClient<LoginResponse>(endpoint, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
  },

  async requestLoginCode(email: string): Promise<RequestCodeResponse> {
    return apiClient<RequestCodeResponse>('/api/auth/login/request-code', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email }),
    });
  },

  async checkEmailAvailability(payload: CheckEmailPayload): Promise<CheckEmailResponse> {
    return apiClient<CheckEmailResponse>('/api/auth/signup/check-email', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
  },

  async initiateSignup(payload: InitiateSignupPayload): Promise<InitiateSignupResponse> {
    return apiClient<InitiateSignupResponse>('/api/auth/signup/initiate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
  },

  async verifySignupCode(payload: VerifyCodePayload): Promise<VerifyCodeResponse> {
    return apiClient<VerifyCodeResponse>('/api/auth/signup/verify-code', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
  },

  async resendSignupCode(payload: ResendCodePayload): Promise<ResendCodeResponse> {
    return apiClient<ResendCodeResponse>('/api/auth/signup/resend-code', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
  },

  async getMe(): Promise<User> {
    return apiClient<User>('/api/users/me', {
      method: 'GET',
    });
  },

  async logout(): Promise<BaseApiResponse> {
    return apiClient<BaseApiResponse>('/api/auth/logout', {
      method: 'POST', // recommended to use POST for preventing CSRF attacks
    });
  },
};
