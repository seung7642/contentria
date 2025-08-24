import apiClient from '@/lib/apiClient';
import {
  InitiateSignUpPayload,
  InitiateSignUpResponse,
  RequestVerificationCodePayload,
  RequestVerificationCodeResponse,
  VerifyCodePayload,
  VerifyCodeResponse,
} from '@/types/api/auth';
import { ApiError, AuthError } from '@/types/api/errors';
import { User } from '@/types/user';

// Higher-order function to handle authentication errors
async function withAuthErrorHandling<T>(
  apiCall: () => Promise<T>,
  defaultErrorMessage: string = 'An unexpected authentication operation error occurred',
  defaultUnknownErrorMessage: string = 'An unknown value was thrown during an authentication operation.'
): Promise<T> {
  try {
    return await apiCall();
  } catch (error: unknown) {
    if (error instanceof AuthError) {
      throw error;
    } else if (error instanceof ApiError) {
      throw new AuthError(error.message, error.status, error.details);
    } else if (error instanceof Error) {
      throw new AuthError(error.message || defaultErrorMessage, 500, {
        _detailType: 'GenericError',
        name: error.name,
        message: error.message,
        stack: error.stack,
      });
    } else {
      throw new AuthError(defaultUnknownErrorMessage, 0, {
        _detailType: 'UnknownError',
        thrownValue: String(error),
      });
    }
  }
}

export const authService = {
  async initiateSignup(payload: InitiateSignUpPayload): Promise<InitiateSignUpResponse> {
    const endpoint = '/api/auth/signup/initiate';
    return withAuthErrorHandling(
      () =>
        apiClient<InitiateSignUpResponse>(endpoint, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload),
        }),
      'Failed to initiate signup due to an unexpected error.'
    );
  },

  async verifySignupCode(payload: VerifyCodePayload): Promise<VerifyCodeResponse> {
    const endpoint = '/api/auth/signup/verify-code';
    return withAuthErrorHandling(
      () =>
        apiClient<VerifyCodeResponse>(endpoint, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload),
        }),
      'Failed to verify signup code due to an unexpected error.'
    );
  },

  async requestVerificationCode(
    payload: RequestVerificationCodePayload
  ): Promise<RequestVerificationCodeResponse> {
    const endpoint = '/api/auth/signup/resend-code';
    return withAuthErrorHandling(
      () =>
        apiClient<RequestVerificationCodeResponse>(endpoint, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload),
        }),
      'Failed to resend signup code due to an unexpected error.'
    );
  },

  async getMe(): Promise<User> {
    const endpoint = '/api/auth/me';
    return withAuthErrorHandling(
      () =>
        apiClient<User>(endpoint, {
          method: 'GET',
        }),
      'Failed to fetch current user due to an unexpected error.'
    );
  },
};
