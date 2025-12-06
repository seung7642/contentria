'use client';

import { PATHS } from '@/constants/paths';
import { ApiError } from '@/types/api/errors';
import { useAuthStore } from '@/store/authStore';
import { LoginPayload, LoginResponse, SendOtpPayload, SendOtpResponse } from '@/types/api/auth';
import {
  InitiateSignUpPayload,
  InitiateSignUpResponse,
  VerifyOtpCodePayload,
  VerifyOtpCodeResponse,
} from '@/types/api/auth';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import { useRouter } from 'next/navigation';
import {
  initiateSignUpAction,
  loginWithPasswordAction,
  sendOtpCodeAction,
  verifyOtpCodeAction,
} from '@/actions/auth';

/**
 * 패스워드로 로그인하는 Mutation
 * 성공 시, 유저 정보를 갱신하고 대시보드로 리다이렉트한다.
 */
export const useLoginWithPasswordMutation = () => {
  const queryClient = useQueryClient();
  const router = useRouter();
  const login = useAuthStore((state) => state.login);

  return useMutation<LoginResponse, ApiError | AxiosError, LoginPayload>({
    mutationFn: (payload: LoginPayload) => loginWithPasswordAction(payload),
    onSuccess: (data) => {
      login(data.user);
      queryClient.invalidateQueries({ queryKey: ['user', 'me'] });
      router.replace(PATHS.DASHBOARD);
    },
  });
};

/**
 * 회원가입을 시작하는 뮤테이션 (v3 또는 v2 reCAPTCHA 사용)
 * onSuccess 콜백을 통해 특정 UI 로직을 실행할 수 있다.
 */
export const useInitiateSignUpMutation = (onSuccessCallback?: () => void) => {
  return useMutation<InitiateSignUpResponse, ApiError | AxiosError, InitiateSignUpPayload>({
    mutationFn: (payload: InitiateSignUpPayload) => initiateSignUpAction(payload),
    onSuccess: () => {
      onSuccessCallback?.();
    },
    onError: (error, variables, context) => {
      console.error('Error during initiateSignUp:', error, variables, context);
    },
  });
};

/**
 * OTP 코드를 검증하는 뮤테이션
 * 성공 시, 유저 정보를 갱신하고 대시보드로 리다이렉트한다.
 */
export const useVerifyOtpMutation = () => {
  const queryClient = useQueryClient();
  const router = useRouter();
  const login = useAuthStore((state) => state.login);

  return useMutation<VerifyOtpCodeResponse, ApiError | AxiosError, VerifyOtpCodePayload>({
    mutationFn: (payload: VerifyOtpCodePayload) => verifyOtpCodeAction(payload),
    onSuccess: (data) => {
      login(data.user);
      queryClient.invalidateQueries({ queryKey: ['user', 'me'] });
      router.replace(PATHS.DASHBOARD);
    },
  });
};

/**
 * OTP 코드를 요청(전송)하는 뮤테이션
 * onSuccess 콜백을 통해 특정 UI 로직을 실행할 수 있다.
 */
export const useSendOtpMutation = (onSuccessCallback?: () => void) => {
  return useMutation<SendOtpResponse, ApiError | AxiosError, SendOtpPayload>({
    mutationFn: (payload: SendOtpPayload) => sendOtpCodeAction(payload),
    onSuccess: () => {
      onSuccessCallback?.();
    },
  });
};
