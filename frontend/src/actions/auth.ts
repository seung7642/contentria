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
import { ApiError } from '@/types/api/errors';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

export interface SerializedError {
  name: string;
  message: string;
  status?: number;
  code?: string;
  error?: string;
  timestamp?: string;
  path?: string;
  details?: Record<string, string>;
}

export type ActionResult<T> =
  | { success: true; data: T }
  | { success: false; error: SerializedError };

export async function logoutAction() {
  try {
    await apiServer.post('/api/auth/logout', {}, { requireAuth: true });
  } catch (error) {
    console.error('Logout failed on backend:', error);
  }

  const cookieStore = await cookies();
  cookieStore.delete('accessToken');
  cookieStore.delete('refreshToken');

  redirect('/');
}

export async function initiateSignUpAction(
  payload: InitiateSignUpPayload
): Promise<ActionResult<InitiateSignUpResponse>> {
  try {
    const data = await apiServer.post<InitiateSignUpResponse>(
      '/api/auth/signup/initiate',
      payload,
      {
        requireAuth: false,
      }
    );
    return { success: true, data };
  } catch (error) {
    console.error('[initiateSignUpAction] Error in initiateSignUpAction:', error);
    if (error instanceof ApiError) {
      return {
        success: false,
        error: {
          name: error.name,
          message: error.message,
          status: error.status,
          code: error.code,
          error: error.error,
          timestamp: error.timestamp,
          path: error.path,
          details: error.details,
        },
      };
    }
    return {
      success: false,
      error: {
        name: error instanceof Error ? error.name : 'UnknownError',
        message: error instanceof Error ? error.message : 'Unknown error',
        status: 500,
        code: 'INTERNAL_ERROR',
      },
    };
  }
}

export async function verifyOtpCodeAction(
  payload: VerifyOtpCodePayload
): Promise<ActionResult<VerifyOtpCodeResponse>> {
  try {
    const data = await apiServer.post<VerifyOtpCodeResponse>('/api/auth/verify-code', payload, {
      requireAuth: false,
    });
    return { success: true, data };
  } catch (error) {
    console.error('[verifyOtpCodeAction] Error in verifyOtpCodeAction:', error);
    if (error instanceof ApiError) {
      return {
        success: false,
        error: {
          name: error.name,
          message: error.message,
          status: error.status,
          code: error.code,
          error: error.error,
          timestamp: error.timestamp,
          path: error.path,
          details: error.details,
        },
      };
    }
    return {
      success: false,
      error: {
        name: error instanceof Error ? error.name : 'UnknownError',
        message: error instanceof Error ? error.message : 'Unknown error',
        status: 500,
        code: 'INTERNAL_ERROR',
      },
    };
  }
}

export async function loginWithPasswordAction(
  payload: LoginPayload
): Promise<ActionResult<LoginResponse>> {
  try {
    const data = await apiServer.post<LoginResponse>('/api/auth/login', payload, {
      requireAuth: false,
    });
    return { success: true, data };
  } catch (error) {
    console.error('[loginWithPasswordAction] Error in loginWithPasswordAction:', error);
    if (error instanceof ApiError) {
      return {
        success: false,
        error: {
          name: error.name,
          message: error.message,
          status: error.status,
          code: error.code,
          error: error.error,
          timestamp: error.timestamp,
          path: error.path,
          details: error.details,
        },
      };
    }
    return {
      success: false,
      error: {
        name: error instanceof Error ? error.name : 'UnknownError',
        message: error instanceof Error ? error.message : 'Unknown error',
        status: 500,
        code: 'INTERNAL_ERROR',
      },
    };
  }
}

export async function sendOtpCodeAction(
  payload: SendOtpPayload
): Promise<ActionResult<SendOtpResponse>> {
  try {
    const data = await apiServer.post<SendOtpResponse>('/api/auth/send-otp', payload, {
      requireAuth: false,
    });
    return { success: true, data };
  } catch (error) {
    console.error('[sendOtpCodeAction] Error in sendOtpCodeAction:', error);
    if (error instanceof ApiError) {
      return {
        success: false,
        error: {
          name: error.name,
          message: error.message,
          status: error.status,
          code: error.code,
          error: error.error,
          timestamp: error.timestamp,
          path: error.path,
          details: error.details,
        },
      };
    }
    return {
      success: false,
      error: {
        name: error instanceof Error ? error.name : 'UnknownError',
        message: error instanceof Error ? error.message : 'Unknown error',
        status: 500,
        code: 'INTERNAL_ERROR',
      },
    };
  }
}
