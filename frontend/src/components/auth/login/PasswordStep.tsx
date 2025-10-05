import BackButton from '@/components/common/BackButton';
import Divider from '@/components/common/Divider';
import InputField from '@/components/common/InputField';
import { PasswordStepFormData, passwordStepSchema } from '@/lib/schemas/authSchemas';
import { zodResolver } from '@hookform/resolvers/zod';
import { Mail } from 'lucide-react';
import Link from 'next/link';
import { SubmitHandler, useForm } from 'react-hook-form';
import { useLoginFlow } from '@/hooks/useLoginFlow';

const PasswordStep = () => {
  const {
    formData,
    isLoading,
    error,
    loginAttemptType,
    goToPreviousStep,
    submitPasswordLogin,
    requestOtpLogin,
  } = useLoginFlow();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<PasswordStepFormData>({
    resolver: zodResolver(passwordStepSchema),
    defaultValues: { password: formData.password || '' },
  });

  const processPasswordSubmit: SubmitHandler<PasswordStepFormData> = async (data) => {
    submitPasswordLogin(data);
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
        {error && <p className="mt-4 text-center text-sm text-red-600">{error.message}</p>}
        <div className="mt-8">
          <button
            type="submit"
            disabled={isLoading}
            className="group relative flex w-full justify-center rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {isLoading && loginAttemptType === 'password' ? 'Signing in...' : 'Sign in'}
          </button>
        </div>
      </form>

      <div className="mt-6">
        <Divider text={'OR'} />
        <div className="mt-6">
          <button
            type="button"
            onClick={requestOtpLogin}
            disabled={isLoading}
            className="group relative flex w-full items-center justify-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
          >
            <Mail className="mr-2 h-4 w-4" />
            {isLoading && loginAttemptType === 'otp' ? 'Sending...' : 'Email sign-in code'}
          </button>
        </div>
      </div>
    </>
  );
};

export default PasswordStep;
