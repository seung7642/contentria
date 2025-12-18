import DashboardHeader from '@/components/dashboard/DashboardHeader';
import Footer from '@/components/home/Footer';

export default function EditorLayout({ children }: { children: React.ReactNode }) {
  return (
    <>
      <div className="flex min-h-screen flex-col bg-gray-50 antialiased">
        <DashboardHeader />
        <main className="flex flex-1 flex-col">{children}</main>
      </div>
      <Footer />
    </>
  );
}
