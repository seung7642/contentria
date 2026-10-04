export const AUTH_MESSAGES = {
  EMAIL_REQUIRED: 'Please enter your email address.',
  PASSWORD_REQUIRED: 'Please enter your password.',
  CODE_REQUIRED: (length: number) => `Please enter the ${length}-digit code.`,
  PASSWORDS_NO_MATCH: 'Passwords do not match',
  SIGNUP_SUCCESS_LOGINH_PROMPT: 'Sign up successful! Please sign in.',
  VERIFICATION_CODE_SENT: 'A new verification code has been sent to your email.',
} as const;
