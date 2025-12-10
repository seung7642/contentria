'use server';

import apiServer from '@/lib/apiServer';
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
    await apiServer.post('/api/auth/logout', {}, { requireAuth: true });
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
  return await apiServer.post<InitiateSignUpResponse>('/api/auth/signup/initiate', payload, {
    requireAuth: false,
  });
}

export async function verifyOtpCodeAction(
  payload: VerifyOtpCodePayload
): Promise<VerifyOtpCodeResponse> {
  return await apiServer.post<VerifyOtpCodeResponse>('/api/auth/verify-code', payload, {
    requireAuth: false,
  });
}

export async function loginWithPasswordAction(payload: LoginPayload): Promise<LoginResponse> {
  return await apiServer.post<LoginResponse>('/api/auth/login', payload, {
    requireAuth: false,
  });
}

export async function sendOtpCodeAction(payload: SendOtpPayload): Promise<SendOtpResponse> {
  return await apiServer.post<SendOtpResponse>('/api/auth/send-otp', payload, {
    requireAuth: false,
  });
}
