import Divider from '@/components/ui/divider';
import InputField from '@/components/ui/inputField';
import { EmailStepProps } from '@/types/signup';
import { Mail } from 'lucide-react';
import React from 'react';
import GoogleLoginButton from '../googleLoginButton';
import Link from 'next/link';

export const EmailStep: React.FC<EmailStepProps> = ({
  formData,
  onUpdateData,
  onNext,
  isLoading,
  error,
  setError,
}) => {
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!formData.name.trim()) {
      setError('Please enter your name.');
      return;
    }

    if (!formData.email.trim()) {
      setError('Please enter your email address.');
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      setError('Please enter a valid email address.');
      return;
    }

    onNext();
  };

  return (
    <>
      <form className="mt-2 space-y-6" onSubmit={handleSubmit}>
        <InputField
          id="name"
          name="name"
          type="text"
          placeholder="Your name"
          value={formData.name}
          onChange={(e) => onUpdateData('name', e.target.value)}
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
          value={formData.email}
          onChange={(e) => onUpdateData('email', e.target.value)}
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
};
