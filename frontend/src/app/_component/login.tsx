import { OAuth2Client } from 'google-auth-library';
import { useState } from 'react';

const CLIENT_ID = '';
const client = new OAuth2Client(CLIENT_ID);

export default function Login() {
  const [isOpen, setIsOpen] = useState(false);

  const handleGoogleLogin = async () => {
    try {
      const authUrl = await client.generateAuthUrl({
        scope: [
          'https://www.googleapis.com/auth/userinfo.email',
          'https://www.googleapis.com/auth/userinfo.profile',
        ],
        access_type: 'offline',
        prompt: 'consent',
      });

      const popup = window.open(authUrl, 'Google Login', 'width=500,height=600');

      if (popup) {
        const checkPopup = setInterval(() => {
          if (popup.closed) {
            clearInterval(checkPopup);
            setIsOpen(false);
          }
        }, 1000);
      }
    } catch (error) {
      console.error('Error during Google login:', error);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-100">
      <button onClick={() => setIsOpen(true)}>Login</button>
      {isOpen && (
        <div className="flex items-center justify-center">
          <div className="rounded-lg bg-white p-8 shadow-lg">
            <h2 className="mb-4 text-2xl font-bold">Login</h2>
            <button onClick={handleGoogleLogin} className="w-full">
              Login with Google
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
