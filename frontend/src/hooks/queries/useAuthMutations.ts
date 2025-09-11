import { PATHS } from '@/constants/paths';
import { authService } from '@/services/authService';
import { useAuthStore } from '@/store/authStore';
import { LoginPayload, SendOtpPayload } from '@/types/api/auth/login';
import { InitiateSignUpPayload, VerifyOtpCodePayload } from '@/types/api/auth/signUp';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';

/**
 * 패스워드로 로그인하는 Mutation
 * 성공 시, 유저 정보를 갱신하고 대시보드로 리다이렉트한다.
 */
export const useLoginWithPasswordMutation = () => {
  const queryClient = useQueryClient();
  const router = useRouter();
  const login = useAuthStore((state) => state.login);

  return useMutation({
    mutationFn: (payload: LoginPayload) => authService.loginWithPassword(payload),
    onSuccess: (data) => {
      login(data.user, data.accessToken);
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
  return useMutation({
    mutationFn: (payload: InitiateSignUpPayload) => authService.initiateSignUp(payload),
    onSuccess: () => {
      onSuccessCallback?.();
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

  return useMutation({
    mutationFn: (payload: VerifyOtpCodePayload) => authService.verifyOtpCode(payload),
    onSuccess: (data) => {
      login(data.user, data.accessToken);
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
  return useMutation({
    mutationFn: (payload: SendOtpPayload) => authService.sendOtpCode(payload),
    onSuccess: () => {
      onSuccessCallback?.();
    },
  });
};
