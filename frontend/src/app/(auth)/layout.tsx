'use client';

import { useAuthRedirect } from '@/hooks/useAuthRedirect';
import { Loader2 } from 'lucide-react';
import { GoogleReCaptchaProvider } from 'react-google-recaptcha-v3';

interface AuthLayoutProps {
  children: React.ReactNode;
}

const AuthLayout = ({ children }: AuthLayoutProps) => {
  const isCheckingAuth = useAuthRedirect();
  const recaptchaSiteKey = process.env.NEXT_PUBLIC_RECAPTCHA_V3_SITE_KEY;

  if (isCheckingAuth) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gray-50">
        <Loader2 className="h-12 w-12 animate-spin text-indigo-600" />
      </div>
    );
  }

  const content = (
    <main className="h-screen overflow-hidden bg-gray-50">
      <div className="flex h-screen flex-col items-center justify-center bg-gray-50 px-4 pt-12 sm:px-6 lg:px-8">
        <div className="w-full max-w-md space-y-8">{children}</div>
        <div className="fixed bottom-12 left-0 right-0 text-center text-sm text-gray-500">
          By continuing, you agree to out{' '}
          <a href="/policy?tab=privacy" className="text-indigo-600 hover:text-indigo-500">
            Privacy Policy{' '}
          </a>
          and{' '}
          <a href="/ppolicy?tab=terms" className="text-indigo-600 hover:text-indigo-500">
            Terms of Service
          </a>
        </div>
      </div>
    </main>
  );

  if (!recaptchaSiteKey) {
    console.warn('reCAPTCHA V3 Site Key is not defined. Provider will not be used.');
    return content;
  }

  return (
    <GoogleReCaptchaProvider
      reCaptchaKey={recaptchaSiteKey}
      language="ko"
      scriptProps={{ async: true, defer: true }}
    >
      {content}
    </GoogleReCaptchaProvider>
  );
};

export default AuthLayout;
