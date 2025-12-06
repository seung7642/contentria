import HomeLayoutClient from '@/components/home/HomeLayoutClient';
import { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'Home',
  description: 'Welcome to the Home Page',
};

export default function HomeLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return <HomeLayoutClient>{children}</HomeLayoutClient>;
}
