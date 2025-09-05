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
import { ApiError, ErrorResponse } from '@/types/api/errors';
import { ApiResult } from '@/types/api/result';
import axios from 'axios';

async function withAuthErrorHandling<T>(apiCall: () => Promise<T>): Promise<ApiResult<T>> {
  try {
    const data = await apiCall();
    return { success: true, data };
  } catch (error) {
    let apiError: ApiError;

    if (axios.isAxiosError(error) && error.response?.data) {
      apiError = new ApiError(error.response.data as ErrorResponse);
    } else {
      const fallbackResponse: ErrorResponse = {
        timestamp: new Date().toISOString(),
        status: 0,
        error: '',
        message:
          error instanceof Error ? error.message : 'An unexpected client-side error occurred.',
        path: '',
        code: 'CLIENT0000',
      };
      apiError = new ApiError(fallbackResponse);
    }

    return { success: false, error: apiError };
  }
}

export const authService = {
  async initiateSignUp(payload: InitiateSignUpPayload): Promise<ApiResult<InitiateSignUpResponse>> {
    return withAuthErrorHandling(async () => {
      const { data } = await apiClient.post<InitiateSignUpResponse>(
        '/api/auth/signup/initiate',
        payload
      );
      return data;
    });
  },

  async verifyOtpCode(payload: VerifyOtpCodePayload): Promise<ApiResult<VerifyOtpCodeResponse>> {
    return withAuthErrorHandling(async () => {
      const { data } = await apiClient.post<VerifyOtpCodeResponse>(
        '/api/auth/verify-code',
        payload
      );
      return data;
    });
  },

  async loginWithPassword(payload: LoginPayload): Promise<ApiResult<LoginResponse>> {
    return withAuthErrorHandling(async () => {
      const { data } = await apiClient.post<LoginResponse>('/api/auth/login', payload);
      return data;
    });
  },

  async sendOtpCode(payload: SendOtpPayload): Promise<ApiResult<SendOtpResponse>> {
    return withAuthErrorHandling(async () => {
      const { data } = await apiClient.post<SendOtpResponse>('/api/auth/send-otp', payload);
      return data;
    });
  },
};
