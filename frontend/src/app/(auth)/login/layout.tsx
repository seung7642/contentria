import type { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'Login',
  description: 'Contentria 로그인',
};

export default function LoginLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return <main className="min-h-screen bg-gray-50">{children}</main>;
}
