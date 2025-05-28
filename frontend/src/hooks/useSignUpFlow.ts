import { SignUpStep } from '@/types/signup';
import { useState } from 'react';

export const useSignUpFlow = () => {
  const [step, setStep] = useState<SignUpStep>('email');
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    verificationCode: '',
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const updateFormData = (field: string, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    if (error) {
      setError(null);
    }
  };

  const goToNextStep = () => {
    const steps: SignUpStep[] = ['email', 'password-creation', 'verify-email-code'];
    const currentIndex = steps.indexOf(step);
    if (currentIndex < steps.length - 1) {
      setStep(steps[currentIndex + 1]);
      setError(null);
    }
  };

  const goToPreviousStep = () => {
    const steps: SignUpStep[] = ['email', 'password-creation', 'verify-email-code'];
    const currentIndex = steps.indexOf(step);
    if (currentIndex > 0) {
      setStep(steps[currentIndex - 1]);
      setError(null);
      if (step === 'password-creation') {
        setFormData((prev) => ({ ...prev, password: '' }));
      } else if (step === 'verify-email-code') {
        setFormData((prev) => ({ ...prev, verificationCode: '' }));
      }
    }
  };

  const resetForm = () => {
    setFormData({
      name: '',
      email: '',
      password: '',
      verificationCode: '',
    });
    setStep('email');
    setError(null);
    setIsLoading(false);
  };

  return {
    step,
    formData,
    isLoading,
    error,
    setIsLoading,
    setError,
    updateFormData,
    goToNextStep,
    goToPreviousStep,
    resetForm,
  };
};
