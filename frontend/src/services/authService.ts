import apiClient from '@/lib/apiClient';
import {
  CheckEmailPayload,
  CheckEmailResponse,
  InitiateSignupPayload,
  InitiateSignupResponse,
  LoginPayload,
  LoginResponse,
  RequestCodeResponse,
  ResendCodePayload,
  ResendCodeResponse,
  VerifyCodePayload,
  VerifyCodeResponse,
} from '@/types/api/auth';

interface BaseApiResponse {
  message?: string;
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
