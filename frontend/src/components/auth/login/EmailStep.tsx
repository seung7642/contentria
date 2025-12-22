import { Divider } from '@/components/common/Divider';
import InputField from '@/components/common/InputField';
import { LoginEmailStepFormData, loginEmailStepSchema } from '@/lib/schemas/authSchemas';
import { zodResolver } from '@hookform/resolvers/zod';
import { SubmitHandler, useForm } from 'react-hook-form';
import GoogleLoginButton from '../GoogleLoginButton';
import Link from 'next/link';
import { PATHS } from '@/constants/paths';
import { useLoginFlow } from '@/hooks/useLoginFlow';

export default function EmailStep() {
  const { formData, isLoading, error, submitEmailStep, startGoogleLogin } = useLoginFlow();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginEmailStepFormData>({
    resolver: zodResolver(loginEmailStepSchema),
    defaultValues: { email: formData.email },
  });

  const onSubmit: SubmitHandler<LoginEmailStepFormData> = (data) => {
    console.log('Email submitted click');
    submitEmailStep(data);
  };

  return (
    <>
      <form className="mt-8 space-y-6" onSubmit={handleSubmit(onSubmit)}>
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
        {error && <p className="text-center text-sm text-red-600">{error.message}</p>}
        <div>
          <button
            type="submit"
            disabled={isLoading}
            className="fnt-medium group relative flex w-full justify-center rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-sm text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {isLoading ? 'Processing...' : 'Continue'}
          </button>
        </div>
      </form>
      <div className="mt-6">
        <Divider text="OR" />
        <div className="mt-6">
          <GoogleLoginButton onClick={startGoogleLogin} />
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
}
