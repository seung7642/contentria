'use client';

import CodeInput from '@/components/auth/codeInput';
import GoogleLoginButton from '@/components/auth/googleLoginButton';
import Divider from '@/components/ui/divider';
import InputField from '@/components/ui/inputField';
import { DEFAULT_LOGGED_IN_REDIRECT_URL } from '@/constants/auth';
import apiClient from '@/lib/apiClient';
import { useAuthStore, User } from '@/store/authStore';
import { ArrowLeft, Loader2, Mail, Lock } from 'lucide-react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';

type SignUpStep = 'email' | 'password-creation' | 'verify-email-code';

const SignUpPage = () => {
  const router = useRouter();
  const user = useAuthStore((state) => state.user);
  const setUser = useAuthStore((state) => state.setUser);

  const [step, setStep] = useState<SignUpStep>('email');
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [verificationCode, setVerificationCode] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isCheckingAuth, setIsCheckingAuth] = useState(true);

  // Check if user is already logged in
  useEffect(() => {
    const checkAuthStatus = async () => {
      if (user) {
        router.replace(DEFAULT_LOGGED_IN_REDIRECT_URL);
        return;
      }

      try {
        const response = await apiClient<User>('/api/users/me');
        setUser(response);
        router.replace(DEFAULT_LOGGED_IN_REDIRECT_URL);
      } catch (error) {
        setIsCheckingAuth(false);
      }
    };

    checkAuthStatus();
  }, [router, user, setUser]);

  const handleEmailSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    if (!email) {
      setError('Please enter your email address.');
      return;
    }
    // TODO: Add email validation

    setIsLoading(true);
    try {
      // TODO: Call API to check if email is already registered (e.g., /api/auth/signup/check-email)

      setStep('password-creation');
    } catch (error) {
      setError('This email is already taken or invalid.');
    } finally {
      setIsLoading(false);
    }
  };

  const handlePasswordSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    if (!password) {
      // TODO: Check password policy (e.g., length, complexity)
      setError('Please enter a password that meets the requirements.');
      return;
    }

    setIsLoading(true);
    try {
      // TODO: Call API to send Sign Up information (email, password) and request verification code (e.g., /api/auth/signup/initiate)

      setStep('verify-email-code');
    } catch (error) {
      setError('Failed to initiate sign up. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleVerificationCodeSubmit = async (e?: React.FormEvent) => {
    if (e) {
      e.preventDefault();
    }
    setError(null);
    if (verificationCode.length !== 6) {
      setError('Please enter the 6-digit code.');
      return;
    }

    setIsLoading(true);
    try {
      // TODO: Call API to verify the verification code (e.g., /api/auth/signup/verify-code)

      // If successful, redirect to the login page or automatically log in the user
      router.replace(DEFAULT_LOGGED_IN_REDIRECT_URL);
    } catch (error) {
      setError('Invalid or expired code. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const requestEmailCode = () => {
    setError(null);
    setIsLoading(true);
    console.log('Requesting email sign-in code for:', email);
    // TODO: 여기에 이메일 인증 코드 발송 API 호출 구현
    // fakeApiCall({ email, type: 'send_code' }).then(() => setStep('code')).catch(err => setError(err.message)).finally(() => setIsLoading(false));

    // 임시 처리
    setTimeout(() => {
      setIsLoading(false);
    }, 500);
  };

  const goBack = () => {
    setError(null);
    if (step === 'password-creation') {
      setStep('email');
      setPassword('');
      setConfirmPassword('');
    } else if (step === 'verify-email-code') {
      setStep('password-creation');
      setVerificationCode('');
    }
  };

  const renderEmailStep = () => (
    <>
      <form className="mt-2 space-y-6" onSubmit={handleEmailSubmit}>
        <InputField
          id="name"
          name="name"
          type="text"
          placeholder="Your name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          autoComplete="name"
          isRounded="both"
          label="Name"
          required
        />
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
          Already have an account?{' '}
          <Link href="/login" className="font-medium text-indigo-600 hover:text-indigo-500">
            Sign in
          </Link>
        </p>
      </div>
    </>
  );

  const renderPasswordCreationStep = () => (
    <>
      {renderBackButton()}
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

        <div className="mb-1 mt-6 flex items-center justify-between">
          <label htmlFor="password" className="block text-sm font-medium text-gray-700">
            Password
          </label>
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
            {isLoading ? 'Processing...' : 'Continue'}
          </button>
        </div>
      </form>

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
            {isLoading ? 'Sending...' : 'Continue with email code'}
          </button>
        </div>
      </div>
    </>
  );

  const renderVerifyCodeStep = () => (
    <>
      {renderBackButton()}
      <div className="mt-4 pl-8 text-sm text-gray-600">
        <p>Enter the code sent to</p>
        <span className="font-medium text-gray-900">{email}</span>
      </div>
      <form className="mt-4 space-y-6" onSubmit={handleVerificationCodeSubmit}>
        <CodeInput
          length={6}
          onChange={setCode}
          onComplete={(code) => {
            setVerificationCode(code);
            if (code.length === 6) {
              handleVerificationCodeSubmit();
            } else {
              setError('Please enter the 6-digit code.');
            }
          }}
        />
        {error && <p className="text-center text-sm text-red-600">{error}</p>}
      </form>
    </>
  );

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

  // if (isCheckingAuth) {
  //   return (
  //     <div className="flex min-h-screen items-center justify-center bg-gray-50">
  //       <Loader2 className="h-8 w-8 animate-spin text-indigo-600" />
  //     </div>
  //   );
  // }

  return (
    <div className="flex min-h-screen items-start justify-center bg-gray-50 px-4 pt-48 sm:px-6 lg:px-8">
      <div className="w-full max-w-md space-y-8">
        <h2 className="text-center text-3xl font-extrabold text-gray-900">
          {step === 'verify-email-code' ? 'Verify your email' : 'Sign up'}
        </h2>
        <div className="relative rounded-lg bg-white p-8 shadow-md">
          {step === 'email' && renderEmailStep()}
          {step === 'password-creation' && renderPasswordCreationStep()}
          {step === 'verify-email-code' && renderVerifyCodeStep()}
        </div>
      </div>
      <div className="fixed bottom-12 left-0 right-0 text-center text-sm text-gray-500">
        By continuing, you agree to our{' '}
        <a href="/policy?tab=privacy" className="text-indigo-600 hover:text-indigo-500">
          Privacy Policy
        </a>{' '}
        and{' '}
        <a href="/policy?tab=terms" className="text-indigo-600 hover:text-indigo-500">
          Terms of Service
        </a>
      </div>
    </div>
  );
};

export default SignUpPage;
