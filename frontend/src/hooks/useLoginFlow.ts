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
  const [loginAttemptType, setLoginAttemptType] = useState<'password' | 'otp' | null>(null);

  const updateFormData = (field: keyof LoginFormData, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    setError(null);
  };

  const setCurrentStep = (newStep: LoginStep) => {
    setStep(newStep);
    setError(null);
  };

  const goToNextStep = () => {
    if (step === 'email') {
      setStep('password');
    }
  };

  const goToPreviousStep = () => {
    if (step === 'password' || step === 'recaptcha_v2_challenge') {
      setStep('email');
    } else if (step === 'verify_otp_code') {
      setStep('password');
    }
  };

  const resetForm = () => {
    setFormData({
      email: '',
      password: '',
      verificationCode: '',
    });
    setCurrentStep('email');
    setError(null);
    setIsLoading(false);
    setLoginAttemptType(null);
  };

  return {
    step,
    setStep: setCurrentStep,
    formData,
    updateFormData,
    resetForm,
    isLoading,
    setIsLoading,
    error,
    setError,
    goToNextStep,
    goToPreviousStep,
    loginAttemptType,
    setLoginAttemptType,
  };
};
