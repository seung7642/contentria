export type SignUpStep =
  | 'email'
  | 'password-creation'
  | 'recaptcha-v2-challenge'
  | 'verify-email-code';

export interface SignUpFormData {
  name: string;
  email: string;
  password: string;
  verificationCode: string;
}

export interface StepProps {
  formData: SignUpFormData;
  onUpdateData: (field: keyof SignUpFormData, value: string) => void;
  isLoading: boolean;
  error: string | null;
  setStep: (step: SignUpStep) => void;
  setIsLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
}

export interface EmailStepProps extends StepProps {
  goToNextStep: () => void;
}

export interface PasswordStepProps extends StepProps {
  goToNextStep: () => void;
  goToPreviousStep: () => void;
}

export interface RecaptchaV2StepProps extends StepProps {
  goToNextStep: () => void;
  goToPreviousStep: () => void;
}

export interface VerificationStepProps extends StepProps {
  goToPreviousStep: () => void;
  onComplete: () => void;
}
