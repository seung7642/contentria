import BackButton from '@/components/ui/BackButton';
import { authService } from '@/services/authService';
import { RecaptchaV2StepProps } from '@/types/signup';
import { ShieldCheck } from 'lucide-react';
import { useRef } from 'react';
import ReCAPTCHA from 'react-google-recaptcha';

const RecaptchaV2Step: React.FC<RecaptchaV2StepProps> = ({
  setStep,
  formData,
  goToPreviousStep,
  isLoading,
  setIsLoading,
  error,
  setError,
}) => {
  const recaptchaV2Ref = useRef<ReCAPTCHA>(null);
  const siteKeyV2 = process.env.NEXT_PUBLIC_RECAPTCHA_V2_CHECKBOX_SITE_KEY;

  const handleV2TokenSubmit = async (v2Token: string | null) => {
    if (!v2Token) {
      return;
    }

    setIsLoading(true);
    setError(null);

    const signUpDataWithV2 = {
      email: formData.email,
      name: formData.name,
      password: formData.password,
      recaptchaV2Token: v2Token,
    };

    const result = await authService.initiateSignUp(signUpDataWithV2);
    if (result.success) {
      if (result.data.nextStep === 'enter_verification_code') {
        setStep('verify-email-code');
      } else {
        setError('An unexpected server response. Please try again.');
        recaptchaV2Ref?.current?.reset();
      }
    } else {
      setError(result.error.message);
      recaptchaV2Ref.current?.reset();
    }

    setIsLoading(false);
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
