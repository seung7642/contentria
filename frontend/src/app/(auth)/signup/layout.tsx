'use client';

import type { Metadata } from 'next';
import { GoogleReCaptchaProvider } from 'react-google-recaptcha-v3';

const metadata: Metadata = {
  title: 'Sign Up',
  description: 'Contentria 회원가입',
};

export default function SignUpLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const recaptchaSiteKey = process.env.NEXT_PUBLIC_RECAPTCHA_V3_SITE_KEY;
  console.log('reCAPTCHA V3 Site Key:', recaptchaSiteKey);

  if (!recaptchaSiteKey) {
    console.warn('reCAPTCHA V3 Site Key is not defined.');
    return <main className="min-h-screen bg-gray-50">{children}</main>;
  }

  return (
    <GoogleReCaptchaProvider
      reCaptchaKey={recaptchaSiteKey}
      // language="ko"
      // scriptProps={{ async: true, defer: true }}
    >
      <main className="min-h-screen bg-gray-50">{children}</main>
    </GoogleReCaptchaProvider>
  );
}
