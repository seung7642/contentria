import Footer from '@/components/home/Footer';
import SkeletonHeader from '@/components/home/SkeletonHeader';
import SkeletonMain from '@/components/home/SkeletonMain';

export default function Loading() {
  return (
    <div className="grid min-h-screen grid-rows-[auth_1fr_auto] bg-white antialiased">
      <SkeletonHeader />
      <SkeletonMain />
      <Footer />
    </div>
  );
}
