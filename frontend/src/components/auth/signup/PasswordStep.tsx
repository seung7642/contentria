import BackButton from '@/components/common/BackButton';
import Divider from '@/components/common/Divider';
import InputField from '@/components/common/InputField';
import { PasswordStepFormData, passwordStepSchema } from '@/lib/schemas/authSchemas';
import { zodResolver } from '@hookform/resolvers/zod';
import { Info, Mail } from 'lucide-react';
import React, { useMemo, useState } from 'react';
import { SubmitHandler, useForm } from 'react-hook-form';
import PasswordPolicyTooltip from './PasswordPolicyTooltip';
import { useSignUpFlow } from '@/hooks/useSignUpFlow';

interface PolicyItem {
  id: string;
  text: string;
  isValid: (password: string) => boolean;
}

export const PasswordStep = () => {
  const { formData, goToPreviousStep, error, isLoading, submitPasswordStep } = useSignUpFlow();
  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm<PasswordStepFormData>({
    resolver: zodResolver(passwordStepSchema),
    defaultValues: {
      password: formData.password,
    },
  });

  const currentPassword = watch('password', formData.password);
  const [isPolicyVisible, setIsPolicyVisible] = useState(false);
  const [submissionType, setSubmissionType] = useState<'password' | 'otp' | null>(null);

  const passwordPolicies = useMemo(
    (): PolicyItem[] => [
      { id: 'length', text: '최소 10자 이상', isValid: (pw) => pw.length >= 10 },
      { id: 'number', text: '숫자 포함', isValid: (pw) => /[0-9]/.test(pw) },
      { id: 'lowercase', text: '소문자 포함', isValid: (pw) => /[a-z]/.test(pw) },
      { id: 'uppercase', text: '대문자 포함', isValid: (pw) => /[A-Z]/.test(pw) },
      { id: 'special', text: '특수문자 포함', isValid: (pw) => /[^a-zA-Z0-9]/.test(pw) },
    ],
    []
  );

  const processPasswordSubmit: SubmitHandler<PasswordStepFormData> = (data) => {
    setSubmissionType('password');
    submitPasswordStep(data.password);
  };

  const processWithoutPassword = () => {
    setSubmissionType('otp');
    submitPasswordStep(null);
  };

  return (
    <>
      <BackButton onClick={goToPreviousStep} />
      <form className="mt-8" onSubmit={handleSubmit(processPasswordSubmit)}>
        <InputField
          id="email-display-password"
          label="Email"
          type="email"
          disabled
          value={formData.email}
          placeholder="Email address"
          className="mb-6"
        />

        <div className="mt-6">
          {' '}
          <div className="mb-1 flex items-center space-x-1">
            <label htmlFor="password" className="block text-sm font-medium text-gray-700">
              Password
            </label>
            <div className="relative flex items-center">
              <button
                type="button"
                onMouseEnter={() => setIsPolicyVisible(true)}
                onMouseLeave={() => setIsPolicyVisible(false)}
                onFocus={() => setIsPolicyVisible(true)}
                onBlur={() => setIsPolicyVisible(false)}
                className="rounded-full p-0.5 text-gray-400 hover:text-gray-600 focus:outline-none"
                aria-label="Show password policy"
                aria-expanded={isPolicyVisible}
              >
                <Info size={16} />
              </button>
              <PasswordPolicyTooltip
                policies={passwordPolicies}
                currentPasswordValue={currentPassword || ''}
                isVisible={isPolicyVisible}
              />
            </div>
          </div>
          <InputField
            id="password"
            type="password"
            placeholder="Enter your password"
            autoComplete="new-password"
            isRounded="both"
            {...register('password')}
            errorMessage={errors.password?.message}
          />
        </div>

        {error && <p className="text-center text-sm text-red-600">{error.message}</p>}
        <div className="mt-8">
          <button
            type="submit"
            disabled={isLoading}
            className="group relative flex w-full justify-center rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {isLoading && submissionType === 'password' ? 'Processing...' : 'Continue'}
          </button>
        </div>
      </form>

      <div className="mt-6">
        <Divider text={'OR'} />
        <div className="mt-6">
          <button
            type="button"
            onClick={processWithoutPassword}
            disabled={isLoading}
            className="group relative flex w-full items-center justify-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50"
          >
            <Mail className="mr-2 h-4 w-4" />
            {isLoading && submissionType === 'otp' ? 'Sending...' : 'Continue with email code'}
          </button>
        </div>
      </div>
    </>
  );
};
