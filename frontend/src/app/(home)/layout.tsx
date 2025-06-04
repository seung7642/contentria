import Footer from '@/components/home/Footer';
import HomeHeader from '@/components/home/homeHeader';

export default function HomeLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <div className="grid min-h-screen grid-rows-[auto_1fr_auto] bg-white antialiased">
      <HomeHeader />
      <main className="w-full overflow-auto">{children}</main>
      <Footer />
    </div>
  );
}
