import apiClient from '@/lib/apiClient';
import {
  BaseApiResponse,
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
  async login(payload: LoginPayload): Promise<LoginResponse> {
    const endpoint = '/api/auth/login';
    return withAuthErrorHandling(
      () =>
        apiClient<LoginResponse>(endpoint, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload),
        }),
      'An unexpected error occurred during login.'
    );
  },

  async requestLoginCode(email: string): Promise<RequestCodeResponse> {
    const endpoint = '/api/auth/login/request-code';
    return withAuthErrorHandling(
      () =>
        apiClient<RequestCodeResponse>(endpoint, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email }),
        }),
      'Failed to request login code due to an unexpected error.'
    );
  },

  async checkEmailAvailability(payload: CheckEmailPayload): Promise<CheckEmailResponse> {
    const endpoint = '/api/auth/signup/check-email';
    return withAuthErrorHandling(
      () =>
        apiClient<CheckEmailResponse>(endpoint, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload),
        }),
      'Failed to check email availability due to an unexpected error.'
    );
  },

  async initiateSignup(payload: InitiateSignupPayload): Promise<InitiateSignupResponse> {
    const endpoint = '/api/auth/signup/initiate';
    return withAuthErrorHandling(
      () =>
        apiClient<InitiateSignupResponse>(endpoint, {
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

  async resendSignupCode(payload: ResendCodePayload): Promise<ResendCodeResponse> {
    const endpoint = '/api/auth/signup/resend-code';
    return withAuthErrorHandling(
      () =>
        apiClient<ResendCodeResponse>(endpoint, {
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

  async logout(): Promise<BaseApiResponse> {
    const endpoint = '/api/auth/logout';
    return withAuthErrorHandling(
      () =>
        apiClient<BaseApiResponse>(endpoint, {
          method: 'POST', // recommended to use POST for preventing CSRF attacks
        }),
      'Logout operation failed unexpectedly.'
    );
  },
};
