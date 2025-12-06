import CallbackClient from '@/components/auth/login/callback/CallbackClient';
import { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'Google Login Callback',
  robots: 'noindex, nofollow', // Prevent indexing by search engines
};

const GoogleLoginCallbackPage = () => {
  return <CallbackClient />;
};

export default GoogleLoginCallbackPage;
