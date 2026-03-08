import RecaptchaWrapper from '@/components/auth/RecaptchaWrapper';

export const metadata = {
  title: 'Auth',
  description: 'Authentication related pages',
};

export default function AuthLayout({ children }: { children: React.ReactNode }) {
  return (
    <RecaptchaWrapper>
      <main className="flex min-h-[100dvh] flex-col bg-gray-50">
        <div className="flex flex-1 flex-col items-center justify-center px-4 sm:px-6 lg:px-8">
          <div className="w-full max-w-md sm:-mt-20">{children}</div>
        </div>

        <div className="py-6 text-center text-sm text-gray-500">
          By continuing, you agree to out{' '}
          <a href="/policy?tab=privacy" className="text-indigo-600 hover:text-indigo-500">
            Privacy Policy
          </a>{' '}
          and{' '}
          <a href="/policy?tab=terms" className="text-indigo-600 hover:text-indigo-500">
            Terms of Service
          </a>
        </div>

        {/* <div className="flex h-screen flex-col items-center justify-center bg-gray-50 px-4 pt-12 sm:px-6 lg:px-8">
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
        </div> */}
      </main>
    </RecaptchaWrapper>
  );
}
