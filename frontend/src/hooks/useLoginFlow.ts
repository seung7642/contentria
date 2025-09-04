'use client';

import { LoginFormData, LoginStep } from '@/components/auth/login/types';
import { useState } from 'react';

export const useLoginFlow = () => {
  const [step, setStep] = useState<LoginStep>('email');
  const [formData, setFormData] = useState<LoginFormData>({
    email: '',
    password: '',
    verificationCode: '',
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const updateFormData = (field: keyof LoginFormData, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    setError(null);
  };

  const goToNextStep = () => {
    if (step === 'email') {
      setStep('password');
    }
  };

  const goToPreviousStep = () => {
    if (step === 'password' || step === 'recaptcha-v2-challenge') {
      setStep('email');
    } else if (step === 'verify-email-code') {
      setStep('password');
    }
  };

  return {
    step,
    setStep,
    formData,
    updateFormData,
    isLoading,
    setIsLoading,
    error,
    setError,
    goToNextStep,
    goToPreviousStep,
  };
};
