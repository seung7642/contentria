'use client';

import BackButton from '@/components/ui/BackButton';
import { RecaptchaV2StepProps } from '@/components/auth/types';
import { useEffect, useRef } from 'react';
import ReCAPTCHA from 'react-google-recaptcha';
import { ShieldCheck } from 'lucide-react';

const RecaptchaV2Step = <TFormData, TStep extends string>({
  goToPreviousStep,
  isLoading,
  error,
  setError,
  onVerify,
}: RecaptchaV2StepProps<TFormData, TStep>) => {
  const recaptchaV2Ref = useRef<ReCAPTCHA>(null);
  const siteKeyV2 = process.env.NEXT_PUBLIC_RECAPTCHA_V2_CHECKBOX_SITE_KEY;

  useEffect(() => {
    if (error) {
      recaptchaV2Ref.current?.reset();
    }
  }, [error]);

  const handleV2TokenSubmit = async (v2Token: string | null) => {
    if (!v2Token) {
      return;
    }
    await onVerify(v2Token);
  };

  const handleOnExpired = () => {
    setError('reCAPTCHA challenge expired. Please refresh or try again.');
  };

  const handleOnError = () => {
    setError('Failed to load reCAPTCHA. Please check your network or try again later.');
  };

  if (!siteKeyV2) {
    return (
      <>
        <BackButton onClick={goToPreviousStep} />
        <p className="mt-4 text-center text-red-500">
          reCAPTCHA v2 is not configured. Please contack support.
        </p>
      </>
    );
  }

  return (
    <>
      <BackButton onClick={goToPreviousStep} />
      <div className="mt-4 text-center">
        <ShieldCheck className="mx-auto h-12 w-12 text-gray-400" />
        <h2 className="mt-2 text-lg font-medium text-gray-900">Security Check</h2>
        <p className="mt-2 text-sm text-gray-600">
          Before continuing, we need to be sure you are human.
        </p>
      </div>
      <div className="mt-8 flex justify-center">
        {isLoading ? (
          <div className="text-center">
            <div className="h-20 w-20 animate-spin rounded-full border-4 border-solid border-indigo-600 border-t-transparent"></div>
            <p className="mt-2 text-sm text-gray-600">Verifying...</p>
          </div>
        ) : (
          <ReCAPTCHA
            ref={recaptchaV2Ref}
            sitekey={siteKeyV2}
            onChange={handleV2TokenSubmit}
            onErrored={handleOnError}
            onExpired={handleOnExpired}
            hl="ko"
          />
        )}
      </div>
      {error && <p className="mt-4 text-center text-sm text-red-600">{error}</p>}
    </>
  );
};

export default RecaptchaV2Step;
