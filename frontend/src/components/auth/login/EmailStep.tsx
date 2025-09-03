'use client';

import { Divider } from '@/components/ui/Divider';
import InputField from '@/components/ui/InputField';
import { EmailStepFormData, emailStepSchema } from '@/lib/schemas/authSchemas';
import { zodResolver } from '@hookform/resolvers/zod';
import { SubmitHandler, useForm } from 'react-hook-form';
import GoogleLoginButton from '../GoogleLoginButton';
import Link from 'next/link';
import { PATHS } from '@/constants/paths';
import { LoginEmailStepProps } from './types';

const EmailStep: React.FC<LoginEmailStepProps> = ({ formData, onUpdateData, goToNextStep }) => {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<EmailStepFormData>({
    resolver: zodResolver(emailStepSchema),
    defaultValues: { email: formData.email },
  });

  const processEmail: SubmitHandler<EmailStepFormData> = async (data) => {
    onUpdateData('email', data.email);
    goToNextStep();
  };

  return (
    <>
      <form className="mt-8 space-y-6" onSubmit={handleSubmit(processEmail)}>
        <InputField
          id="email"
          type="email"
          label="Email"
          placeholder="Your email address"
          autoComplete="email"
          isRounded="both"
          required
          {...register('email')}
          errorMessage={errors.email?.message}
        />
        <div>
          <button
            type="submit"
            disabled={isSubmitting}
            className="fnt-medium group relative flex w-full justify-center rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-sm text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {isSubmitting ? 'Processing...' : 'Continue'}
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
          <Link href={PATHS.SIGNUP} className="font-medium text-indigo-600 hover:text-indigo-500">
            Sign up
          </Link>
        </p>
      </div>
    </>
  );
};

export default EmailStep;
