import apiClient from '@/lib/apiClient';
import {
  LoginPayload,
  LoginResponse,
  SendOtpPayload,
  SendOtpResponse,
} from '@/types/api/auth/login';
import {
  InitiateSignUpPayload,
  InitiateSignUpResponse,
  VerifyOtpCodePayload,
  VerifyOtpCodeResponse,
} from '@/types/api/auth/signUp';

export const authService = {
  async initiateSignUp(payload: InitiateSignUpPayload): Promise<InitiateSignUpResponse> {
    const { data } = await apiClient.post<InitiateSignUpResponse>(
      '/api/auth/signup/initiate',
      payload
    );
    return data;
  },

  async verifyOtpCode(payload: VerifyOtpCodePayload): Promise<VerifyOtpCodeResponse> {
    const { data } = await apiClient.post<VerifyOtpCodeResponse>('/api/auth/verify-code', payload);
    return data;
  },

  async loginWithPassword(payload: LoginPayload): Promise<LoginResponse> {
    const { data } = await apiClient.post<LoginResponse>('/api/auth/login', payload);
    return data;
  },

  async sendOtpCode(payload: SendOtpPayload): Promise<SendOtpResponse> {
    const { data } = await apiClient.post<SendOtpResponse>('/api/auth/send-otp', payload);
    return data;
  },
};
