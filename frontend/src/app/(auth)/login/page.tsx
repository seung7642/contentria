'use client';

import React, { useState } from 'react';
import { Mail, Lock, ArrowLeft } from 'lucide-react';
import InputField from '@/components/ui/InputField';
import Divider from '@/components/ui/Divider';
import GoogleLoginButton from '@/components/auth/GoogleLoginButton';
import CodeInput from '@/components/auth/CodeInput'; // 새로 만들 컴포넌트
import Link from 'next/link';
import AuthFormCard from '@/components/auth/AuthFormCard';

// 단계 정의
type AuthStep = 'email' | 'password' | 'code' | 'signup-details';

const LoginPage = () => {
  const [step, setStep] = useState<AuthStep>('email');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [code, setCode] = useState('');
  const [isLoading, setIsLoading] = useState(false); // API 로딩 상태 (선택적)
  const [error, setError] = useState<string | null>(null); // 에러 메시지 표시용

  const handleEmailSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError(null); // 이전 에러 초기화

    // TODO: 이메일 유효성 검사 추가
    if (!email) {
      setError('Please enter your email address.');
      return;
    }
    setIsLoading(true); // 로딩 시작

    // 실제 API 호출 예시 (백엔드에서 이메일 존재 여부 확인 등)
    // fakeApiCall().then(() => {
    //   if (authMode === 'signin') {
    //     // TODO: 백엔드 응답에 따라 이메일이 존재하면 비밀번호 단계로
    //     setStep('password');
    //   } else {
    //      // 회원가입 시 상세 정보 단계로
    //      setStep('signup-details');
    //   }
    // }).catch(err => setError(err.message)).finally(() => setIsLoading(false));

    // 임시 로직: 바로 다음 단계로 이동
    setTimeout(() => {
      setStep('password');
      setIsLoading(false);
    }, 500); // 네트워크 지연 시뮬레이션
  };

  const handlePasswordSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    // TODO: 비밀번호 유효성 검사 추가
    if (!password) {
      setError('Please enter your password.');
      return;
    }
    setIsLoading(true);
    console.log('Attempting sign in with:', email, password);
    // TODO: 여기에 이메일/비밀번호 로그인 API 호출 구현
    // fakeApiCall({ email, password }).then(user => { /* 로그인 성공 처리 */ }).catch(err => setError(err.message)).finally(() => setIsLoading(false));

    // 임시 성공 처리
    setTimeout(() => {
      alert('Sign in successful (simulation)');
      setIsLoading(false);
      // 로그인 성공 후 리디렉션 등
    }, 1000);
  };

  const handleCodeSubmit = () => {
    setError(null);

    setIsLoading(true);
    console.log('Attempting sign in with email code:', email, code);

    // TODO: 여기에 이메일/코드 로그인 API 호출 구현
    // fakeApiCall({ email, code }).then(user => { /* 로그인 성공 처리 */ }).catch(err => setError(err.message)).finally(() => setIsLoading(false));

    // 임시 성공 처리
    setTimeout(() => {
      alert('Sign in with code successful (simulation)');
      setIsLoading(false);
      // 로그인 성공 후 리디렉션 등
    }, 1000);
  };

  const requestEmailCode = () => {
    setError(null);
    setIsLoading(true);
    console.log('Requesting email sign-in code for:', email);
    // TODO: 여기에 이메일 인증 코드 발송 API 호출 구현
    // fakeApiCall({ email, type: 'send_code' }).then(() => setStep('code')).catch(err => setError(err.message)).finally(() => setIsLoading(false));

    // 임시 처리
    setTimeout(() => {
      setStep('code');
      setIsLoading(false);
    }, 500);
  };

  const goBack = () => {
    setError(null); // 에러 초기화
    if (step === 'password') {
      setStep('email');
      setPassword('');
      setCode('');
    } else if (step === 'code') {
      setStep('password');
      setCode('');
    } else if (step === 'signup-details') {
      setStep('email'); // 회원가입 상세에서 이메일 단계로
      setPassword('');
      setConfirmPassword('');
    }
  };

  const renderBackButton = () => (
    <button
      type="button"
      onClick={goBack}
      className="absolute left-4 top-4 text-gray-500 hover:text-gray-700"
      aria-label="Go back"
    >
      <ArrowLeft size={20} />
    </button>
  );

  const renderEmailStep = () => (
    <>
      <form className="mt-8 space-y-6" onSubmit={handleEmailSubmit}>
        <InputField
          id="email"
          name="email"
          type="email"
          icon={Mail}
          placeholder="Your email address"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          autoComplete="email"
          isRounded="both"
          label="Email"
          required
        />
        {error && <p className="text-center text-sm text-red-600">{error}</p>}
        <div>
          <button
            type="submit"
            disabled={isLoading}
            className="group relative flex w-full justify-center rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {isLoading ? 'Processing...' : 'Continue'}
          </button>
        </div>
      </form>

      <div className="mt-6">
        <Divider text="OR" />
        <div className="mt-6">
          <GoogleLoginButton />
        </div>
      </div>

      <div className="mt-4 text-center">
        <p className="text-sm text-gray-600">
          Don't have an account?{' '}
          <Link href="/signup" className="font-medium text-indigo-600 hover:text-indigo-500">
            Sign up
          </Link>
        </p>
      </div>
    </>
  );

  const renderPasswordStep = () => (
    <>
      {renderBackButton()}
      {/* 이메일 표시 및 변경 옵션 */}
      <form className="mt-8" onSubmit={handlePasswordSubmit}>
        <InputField
          id="email"
          label="Email"
          disabled={true}
          value={email}
          name="email"
          type="email"
          placeholder="Email address"
        />

        {/* Password 라벨과 Forgot password 링크 */}
        <div className="mb-1 mt-6 flex items-center justify-between">
          <label htmlFor="password" className="block text-sm font-medium text-gray-700">
            Password
          </label>
          <a href="#" className="text-sm font-medium text-indigo-600 hover:text-indigo-500">
            Forgot your password?
          </a>
        </div>
        <InputField
          id="password"
          name="password"
          type="password"
          icon={Lock}
          placeholder="Enter your password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          autoComplete="current-password"
          isRounded="both"
          required
        />
        {error && <p className="text-center text-sm text-red-600">{error}</p>}
        <div className="mt-8">
          <button
            type="submit"
            disabled={isLoading}
            className="group relative flex w-full justify-center rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {isLoading ? 'Signing in...' : 'Sign in'}
          </button>
        </div>
      </form>

      {/* 이메일 코드로 로그인 옵션 */}
      <div className="mt-6">
        <Divider text={'OR'} />
        <div className="mt-6">
          <button
            type="button"
            onClick={requestEmailCode}
            disabled={isLoading}
            className="group relative flex w-full items-center justify-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
          >
            <Mail className="mr-2 h-4 w-4" />
            {isLoading ? 'Sending...' : 'Email sign-in code'}
          </button>
        </div>
      </div>
    </>
  );

  const renderCodeStep = () => (
    <>
      {renderBackButton()}
      <div className="mt-4 pl-8 text-sm text-gray-600">
        <p>Enter the code sent to</p>
        <span className="font-medium text-gray-900">{email}</span>
      </div>
      <form className="mt-4 space-y-6" onSubmit={handleCodeSubmit}>
        <CodeInput
          length={6}
          onChange={setCode}
          onComplete={(code) => {
            setCode(code);
            if (code.length === 6) {
              handleCodeSubmit();
            } else {
              setError('Please enter the 6-digit code.');
            }
          }}
        />
        {error && <p className="text-center text-sm text-red-600">{error}</p>}
      </form>
    </>
  );

  return (
    <div className="flex min-h-screen items-start justify-center bg-gray-50 px-4 pt-64 sm:px-6 lg:px-8">
      <div className="w-full max-w-md space-y-8">
        <AuthFormCard title={step === 'code' ? 'Verify your email' : 'Sign in'}>
          {step === 'email' && renderEmailStep()}
          {step === 'password' && renderPasswordStep()}
          {step === 'code' && renderCodeStep()}
        </AuthFormCard>
      </div>
    </div>
  );
};

export default LoginPage;
