'use server';

import apiServer01 from '@/lib/apiServer01';
import {
  InitiateSignUpPayload,
  InitiateSignUpResponse,
  LoginPayload,
  LoginResponse,
  SendOtpPayload,
  SendOtpResponse,
  VerifyOtpCodePayload,
  VerifyOtpCodeResponse,
} from '@/types/api/auth';
import { cookies } from 'next/headers';

export async function logoutAction() {
  try {
    await apiServer01.post('/api/auth/logout', {}, { requireAuth: true });
  } catch (error) {
    console.error('Logout failed on backend:', error);
  }

  const cookieStore = await cookies();
  cookieStore.delete('accessToken');
  cookieStore.delete('refreshToken');
}

export async function initiateSignUpAction(
  payload: InitiateSignUpPayload
): Promise<InitiateSignUpResponse> {
  return await apiServer01.post<InitiateSignUpResponse>('/api/auth/signup/initiate', payload, {
    requireAuth: false,
  });
}

export async function verifyOtpCodeAction(
  payload: VerifyOtpCodePayload
): Promise<VerifyOtpCodeResponse> {
  return await apiServer01.post<VerifyOtpCodeResponse>('/api/auth/verify-code', payload, {
    requireAuth: false,
  });
}

export async function loginWithPasswordAction(payload: LoginPayload): Promise<LoginResponse> {
  return await apiServer01.post<LoginResponse>('/api/auth/login', payload, {
    requireAuth: false,
  });
}

export async function sendOtpCodeAction(payload: SendOtpPayload): Promise<SendOtpResponse> {
  return await apiServer01.post<SendOtpResponse>('/api/auth/send-otp', payload, {
    requireAuth: false,
  });
}
