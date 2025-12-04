import SignUpContainer from '@/components/auth/signup/SignUpContainer';

const SignUpPage = () => {
  return (
    <div className="flex min-h-screen items-start justify-center bg-gray-50 px-4 pt-60 sm:px-6 lg:px-8">
      <div className="w-full max-w-md space-y-8">
        <SignUpContainer />
      </div>
    </div>
  );
};

export default SignUpPage;
