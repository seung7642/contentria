import BackButton from '@/components/ui/BackButton';
import Divider from '@/components/ui/Divider';
import InputField from '@/components/ui/InputField';
import { PATHS } from '@/constants/paths';
import { PasswordStepFormData, passwordStepSchema } from '@/lib/schemas/authSchemas';
import { authService } from '@/services/authService';
import { zodResolver } from '@hookform/resolvers/zod';
import { Mail } from 'lucide-react';
import Link from 'next/link';
import { useState } from 'react';
import { useGoogleReCaptcha } from 'react-google-recaptcha-v3';
import { SubmitHandler, useForm } from 'react-hook-form';
import { LoginPasswordStepProps } from './types';
import { useRouter } from 'next/navigation';

const PasswordStep = ({
  formData,
  onUpdateData,
  isLoading,
  setIsLoading,
  error,
  setError,
  goToPreviousStep,
  setStep,
  setLoginAttemptType,
}: LoginPasswordStepProps) => {
  const router = useRouter();
  const { executeRecaptcha } = useGoogleReCaptcha();
  const [submissionType, setSubmissionType] = useState<
    'login_with_password' | 'login_with_otp' | null
  >(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<PasswordStepFormData>({
    resolver: zodResolver(passwordStepSchema),
    defaultValues: { password: formData.password || '' },
  });

  const processPasswordSubmit: SubmitHandler<PasswordStepFormData> = async (data) => {
    onUpdateData('password', data.password);
    setError(null);
    setIsLoading(true);
    setSubmissionType('login_with_password');

    if (!executeRecaptcha) {
      setError('reCAPTCHA is not ready. Please try again.');
      setIsLoading(false);
      return;
    }
    const recaptchaV3Token = await executeRecaptcha('login_with_password');

    const result = await authService.loginWithPassword({
      email: formData.email,
      password: data.password,
      recaptchaV3Token,
    });

    if (result.success) {
      router.replace(PATHS.DASHBOARD);
    } else {
      if (result.error.status === 403 && result.error.code === 'C0005') {
        setLoginAttemptType('password');
        setStep('recaptcha_v2_challenge');
      } else {
        setError(result.error.message);
      }
    }

    setIsLoading(false);
    setSubmissionType(null);
  };

  const processWithOtpCode = async () => {
    setError(null);
    setIsLoading(true);
    setSubmissionType('login_with_otp');

    if (!executeRecaptcha) {
      setError('reCAPTCHA is not ready. Please try again.');
      setIsLoading(false);
      return;
    }
    const recaptchaV3Token = await executeRecaptcha('login_with_otp');

    const result = await authService.sendOtpCode({
      email: formData.email,
      recaptchaV3Token,
    });

    if (result.success) {
      setStep('verify_otp_code');
    } else {
      if (result.error.status === 403 && result.error.code === 'C0005') {
        setLoginAttemptType('otp');
        setStep('recaptcha_v2_challenge');
      } else {
        setError(result.error.message);
      }
    }

    setIsLoading(false);
    setSubmissionType(null);
  };

  return (
    <>
      <BackButton onClick={goToPreviousStep} />
      <form className="mt-8" onSubmit={handleSubmit(processPasswordSubmit)}>
        <InputField
          id="email-display"
          type="email"
          placeholder="Your email address"
          disabled
          value={formData.email}
        />

        <div className="mb-1 mt-6 flex items-center justify-between">
          <label htmlFor="password" className="block text-sm font-medium text-gray-700">
            Password
          </label>
          <Link
            href="/forgot-password"
            className="text-sm font-medium text-indigo-600 hover:text-indigo-500"
          >
            Forgot your password?
          </Link>
        </div>
        <InputField
          id="password"
          type="password"
          placeholder="Enter your password"
          autoComplete="current-password"
          {...register('password')}
          errorMessage={errors.password?.message}
        />
        {error && <p className="mt-4 text-center text-sm text-red-600">{error}</p>}
        <div className="mt-8">
          <button
            type="submit"
            disabled={isLoading}
            className="group relative flex w-full justify-center rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {isLoading && submissionType === 'login_with_password' ? 'Signing in...' : 'Sign in'}
          </button>
        </div>
      </form>

      <div className="mt-6">
        <Divider text={'OR'} />
        <div className="mt-6">
          <button
            type="button"
            onClick={processWithOtpCode}
            disabled={isLoading}
            className="group relative flex w-full items-center justify-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
          >
            <Mail className="mr-2 h-4 w-4" />
            {isLoading && submissionType === 'login_with_otp' ? 'Sending...' : 'Email sign-in code'}
          </button>
        </div>
      </div>
    </>
  );
};

export default PasswordStep;
