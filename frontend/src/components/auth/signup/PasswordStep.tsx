import BackButton from '@/components/ui/BackButton';
import Divider from '@/components/ui/divider';
import InputField from '@/components/ui/inputField';
import { authService } from '@/services/authService';
import { PasswordStepProps } from '@/types/signup';
import { Mail } from 'lucide-react';
import React from 'react';
import { useGoogleReCaptcha } from 'react-google-recaptcha-v3';

export const PasswordStep: React.FC<PasswordStepProps> = ({
  formData,
  onUpdateData,
  onNext,
  onBack,
  isLoading,
  error,
  setError,
  setIsLoading,
}) => {
  const { executeRecaptcha } = useGoogleReCaptcha();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!formData.password.trim()) {
      setError('Please enter a password that meets the requirements.');
      return;
    }

    if (formData.password.length < 8) {
      setError('Password must be at least 8 characters long.');
      return;
    }

    if (!executeRecaptcha) {
      setError('reCAPTCHA not ready. Please try again.');
      console.error('Execute recaptcha not yet available.');
      return;
    }

    setIsLoading(true);
    try {
      const recaptchaToken = await executeRecaptcha('signup_initiate');
      const signUpData = {
        name: formData.name,
        email: formData.email,
        password: formData.password,
        recaptchaV3Token: recaptchaToken,
      };
      console.log('Sign up data:', signUpData);

      // const response = await authService.initiateSignup(signUpData);
      // if (response.nextStep === 'verify_with_recaptcha_v2') {
      //   setError('High risk detected. Additional verification may be required.');
      //   return;
      // }

      onNext();
    } catch (error: unknown) {
      console.error('reCAPTCHA execution failed:', error);
      return;
    } finally {
      setIsLoading(false);
    }
  };

  const requestVerificationCode = async () => {
    setError(null);
    setIsLoading(true);

    try {
      await authService.requestVerificationCode({ email: formData.email });
      onNext();
    } catch (error: unknown) {
      console.error('Failed to send verification code:', error);
      setError('Failed to send verification code. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <BackButton onClick={onBack} />
      <form className="mt-8" onSubmit={handleSubmit}>
        <InputField
          id="email"
          label="Email"
          disabled={true}
          value={formData.email}
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
          placeholder="Enter your password"
          value={formData.password}
          onChange={(e) => onUpdateData('password', e.target.value)}
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
            onClick={requestVerificationCode}
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
};
