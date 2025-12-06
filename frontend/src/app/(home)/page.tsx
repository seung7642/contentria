import { Edit3, LayoutDashboard, Rss } from 'lucide-react';
import HeroSection from '@/components/home/HeroSection';

export default function HomePage() {
  return (
    <div className="flex min-h-screen flex-col bg-white">
      <HeroSection />

      <section className="bg-white py-20 md:py-24">
        <div className="container mx-auto max-w-6xl px-6">
          <h2 className="mb-16 text-center text-3xl font-bold text-gray-800 md:text-4xl">
            쉽고 강력한 <span className="text-indigo-600">나만의 블로그</span>
          </h2>
          <div className="grid grid-cols-1 gap-10 md:grid-cols-3">
            <div className="transform rounded-xl border border-gray-100 p-8 text-center shadow-sm transition duration-300 hover:-translate-y-1 hover:shadow-lg">
              <div className="mb-6 inline-flex h-16 w-16 items-center justify-center rounded-full bg-indigo-100 text-indigo-600">
                <Edit3 size={32} strokeWidth={1.5} />
              </div>
              <h3 className="mb-3 text-xl font-semibold text-gray-900">직관적인 글쓰기</h3>
              <p className="text-gray-600">
                마크다운 지원, 실시간 미리보기. 글쓰기에만 집중할 수 있는 환경을 제공합니다.
              </p>
            </div>
            <div className="transform rounded-xl border border-gray-100 p-8 text-center shadow-sm transition duration-300 hover:-translate-y-1 hover:shadow-lg">
              <div className="mb-6 inline-flex h-16 w-16 items-center justify-center rounded-full bg-blue-100 text-blue-600">
                <LayoutDashboard size={32} strokeWidth={1.5} />
              </div>
              <h3 className="mb-3 text-xl font-semibold text-gray-900">통합 대시보드</h3>
              <p className="text-gray-600">
                글 관리, 방문자 통계 등 블로그 운영에 필요한 모든 것을 한눈에 파악하고 관리할 수
                있습니다.
              </p>
            </div>
            <div className="transform rounded-xl border border-gray-100 p-8 text-center shadow-sm transition duration-300 hover:-translate-y-1 hover:shadow-lg">
              <div className="mb-6 inline-flex h-16 w-16 items-center justify-center rounded-full bg-orange-100 text-orange-600">
                <Rss size={32} strokeWidth={1.5} />
              </div>
              <h3 className="mb-3 text-xl font-semibold text-gray-900">블로그 구독</h3>
              <p className="text-gray-600">
                독자들이 새 글 알림을 받을 수 있도록 이메일 구독 기능을 제공합니다. (추후 제공 예정)
              </p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
