import LoginContainer from '@/components/auth/login/LoginContainer';

export default function LoginPage() {
  return (
    <div className="flex min-h-screen items-start justify-center bg-gray-50 px-4 pt-60 sm:px-6 lg:px-8">
      <div className="w-full max-w-md space-y-8">
        <LoginContainer />
      </div>
    </div>
  );
}
