import apiClient from '@/lib/apiClient';
import { LoginPayload, LoginResponse, SendOtpPayload, SendOtpResponse } from '@/types/api/auth';
import {
  InitiateSignUpPayload,
  InitiateSignUpResponse,
  VerifyOtpCodePayload,
  VerifyOtpCodeResponse,
} from '@/types/api/auth';

export const authService = {
  // 타입스크립트의 Promise<T> 제네릭은 오직 "성공했을 때(resolve) 반환되는 값의 타입(T)"만을 정의한다.
  // 자바와 달리, 타입스크립트는 함수가 실패했을 때(reject) 어떤 에러를 던지는지 타입으로 정의하는 문법 자체가 없다.
  // 사용하는 쪽에서 try-catch나 onError 콜백을 사용할 때 타입 가드(Type Guard)를 사용해야 한다.
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
