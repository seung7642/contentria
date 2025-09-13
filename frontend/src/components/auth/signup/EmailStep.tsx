import Divider from '@/components/ui/Divider';
import InputField from '@/components/ui/InputField';
import React from 'react';
import GoogleLoginButton from '../GoogleLoginButton';
import Link from 'next/link';
import { SubmitHandler, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { SignUpEmailStepFormData, signUpEmailStepSchema } from '@/lib/schemas/authSchemas';
import { useSignUpFlow } from '@/hooks/useSignUpFlow';

export const EmailStep = () => {
  const { formData, isLoading, error, submitEmailStep } = useSignUpFlow();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<SignUpEmailStepFormData>({
    resolver: zodResolver(signUpEmailStepSchema),
    mode: 'onBlur',
    defaultValues: {
      name: formData.name,
      email: formData.email,
    },
  });

  const onSubmit: SubmitHandler<SignUpEmailStepFormData> = (data) => {
    console.log('Signup Email click');
    submitEmailStep(data);
  };

  return (
    <>
      <form className="mt-2 space-y-6" onSubmit={handleSubmit(onSubmit)}>
        <InputField
          id="name"
          label="Name"
          type="text"
          placeholder="Your name"
          autoComplete="name"
          isRounded="both"
          {...register('name')}
          errorMessage={errors.name?.message}
        />
        <InputField
          id="email"
          label="Email"
          type="email"
          placeholder="Your email address"
          autoComplete="email"
          isRounded="both"
          {...register('email')}
          errorMessage={errors.email?.message}
        />
        {error && <p className="text-center text-sm text-red-600">{error.message}</p>}
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
};
