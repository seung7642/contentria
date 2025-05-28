export type SignUpStep = 'email' | 'password-creation' | 'verify-email-code';

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
  setIsLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
}

export interface EmailStepProps extends StepProps {
  onNext: () => void;
}

export interface PasswordStepProps extends StepProps {
  onNext: () => void;
  onBack: () => void;
}

export interface VerificationStepProps extends StepProps {
  onBack: () => void;
  onComplete: () => void;
}
