'use client';

import React, { useState } from 'react';
import { Mail, Lock } from 'lucide-react';
import InputField from '@/components/ui/inputField';
import Divider from '@/components/ui/divider';
import GoogleLoginButton from '@/components/auth/googleLoginButton';

const LoginPage = () => {
  // 상태 관리
  const [isSignInPage, setIsSignInPage] = useState(true);
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });

  // 이벤트 핸들러
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    console.log(isSignInPage ? 'Logging in...' : 'Signing up...', formData);
    // 여기에 로그인 또는 회원가입 API 호출을 구현
  };

  return (
    <div className="flex min-h-screen items-start justify-center bg-gray-50 px-4 pt-48 sm:px-6 lg:px-8">
      <div className="w-full max-w-md space-y-8">
        {/* 헤더 */}
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            {isSignInPage ? 'Sign in to your account' : 'Create a new account'}
          </h2>
        </div>

        {/* 폼 */}
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          <div className="space-y-px">
            <InputField
              id="email-address"
              name="email"
              type="email"
              icon={Mail}
              placeholder="Email address"
              value={formData.email}
              onChange={handleInputChange}
              autoComplete="email"
              isRounded="top"
            />
            <InputField
              id="password"
              name="password"
              type="password"
              icon={Lock}
              placeholder="Password"
              value={formData.password}
              onChange={handleInputChange}
              autoComplete="current-password"
              isRounded="bottom"
            />
          </div>

          <div>
            <button
              type="submit"
              className="group relative flex w-full justify-center rounded-md border border-transparent bg-indigo-500 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-600 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
            >
              {isSignInPage ? 'Sign in' : 'Sign up'}
            </button>
          </div>

          {/* 추가 옵션 */}
          <div className="flex items-center justify-between">
            <div className="flex items-center">
              <input
                id="remember-me"
                name="remember-me"
                type="checkbox"
                className="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
              />
              <label htmlFor="remember-me" className="ml-2 block text-sm text-gray-900">
                Remember me
              </label>
            </div>

            {isSignInPage && (
              <div className="text-sm">
                <a href="#" className="font-medium text-indigo-600 hover:text-indigo-500">
                  Forgot your password?
                </a>
              </div>
            )}
          </div>
        </form>

        {/* Google 로그인 */}
        <div className="mt-6">
          <Divider text="Or continue with" />
          <div className="mt-6">
            <GoogleLoginButton />
          </div>
        </div>

        {/* 계정 전환 */}
        <div className="mt-4 text-center">
          <p className="text-sm text-gray-600">
            {isSignInPage ? "Don't have an account?" : 'Already have an account?'}
            <button
              type="button"
              className="ml-1 font-medium text-indigo-600 transition duration-150 ease-in-out hover:text-indigo-500 focus:underline focus:outline-none"
              onClick={() => setIsSignInPage(!isSignInPage)}
            >
              {isSignInPage ? 'Sign up' : 'Sign in'}
            </button>
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
