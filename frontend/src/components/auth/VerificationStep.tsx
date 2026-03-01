import React from 'react';
import OtpInput from 'react-otp-input';

interface VerificationStepProps {
  email: string;
  verificationCode: string;
  isLoading: boolean;
  error: string | null;
  onCodeChange: (code: string) => void;
  onResendCode: () => void;
}

export default function VerificationStep({
  email,
  verificationCode,
  isLoading,
  error,
  onCodeChange,
  onResendCode,
}: VerificationStepProps) {
  return (
    <>
      <div className="mt-4 pl-8 text-sm text-gray-600">
        <p>Enter the code sent to</p>
        <span className="font-medium text-gray-900">{email}</span>
      </div>
      <div className="mt-4 space-y-6">
        <OtpInput
          value={verificationCode}
          onChange={onCodeChange}
          numInputs={6}
          shouldAutoFocus={true}
          containerStyle="flex justify-between w-full"
          renderInput={({ style, ...props }) => {
            if (style) {
              delete (style as React.CSSProperties).width;
            }
            return (
              <input
                style={style}
                {...props}
                disabled={isLoading}
                className={`${props.className || ''} h-12 w-12 flex-shrink-0 rounded-md border border-gray-300 text-center text-xl text-gray-900 transition focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:cursor-not-allowed disabled:border-gray-200 disabled:bg-gray-100 disabled:text-gray-400`}
              />
            );
          }}
        />
        {error && <p className="text-center text-sm text-red-600">{error}</p>}

        {/* <div className="mt-4 text-center">
          <button
            type="button"
            onClick={onResendCode}
            disabled={isLoading}
            className="text-sm text-indigo-600 hover:text-indigo-500 disabled:opacity-50"
          >
            {isLoading ? 'Sending...' : "Didn't receive a code? Resend"}
          </button>
        </div> */}
      </div>
    </>
  );
}
