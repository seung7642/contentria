import { User } from '../user';

export interface BaseApiResponse {
  message?: string;
}

export interface LoginPayload {
  email: string;
  password?: string;
  code?: string;
}

export interface LoginResponse {
  message?: string;
  user: User;
  token: string;
}

export interface RequestCodeResponse {
  message?: string;
  isSuccess: boolean;
}

export interface CheckEmailPayload {
  email: string;
}

export interface CheckEmailResponse {
  message?: string;
  isAvailable?: boolean;
}

export interface InitiateSignupPayload {
  email: string;
  name?: string;
  password?: string;
}

export interface InitiateSignupResponse {
  message?: string;
  isSuccess: boolean;
}

export interface VerifyCodePayload {
  email: string;
  code: string;
}

export interface VerifyCodeResponse {
  message?: string;
  user?: User;
  token?: string;
}

export interface ResendCodePayload {
  email: string;
}

export interface ResendCodeResponse {
  message?: string;
  isSuccess: boolean;
}
