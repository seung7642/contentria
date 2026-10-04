import DashboardNav from './DashboardNav';
import DashboardProfileCard from './DashboardProfileCard';
import { BlogInfo } from '@/types/api/blogs';

interface DashboardSidebarProps {
  blogInfos: BlogInfo[] | null;
}

/**
 * Desktop-only sidebar: two stacked cards on the gray page background —
 * a profile card (avatar/nickname/blog address) and the management menu.
 * On mobile the same composition is served by DashboardMobileNav (header Sheet).
 */
export default function DashboardSidebar({ blogInfos }: DashboardSidebarProps) {
  const firstBlogSlug = blogInfos && blogInfos.length > 0 ? blogInfos[0].slug : null;

  return (
    <aside className="hidden w-64 flex-col gap-4 md:flex">
      {/* overflow-hidden: 정사각 프로필 이미지가 카드 상단 모서리 라운드를 따르게 */}
      <div className="overflow-hidden rounded-lg border bg-white shadow-sm">
        <DashboardProfileCard blogSlug={firstBlogSlug} />
      </div>

      {/* flex-1: 메뉴 카드가 남은 세로 영역을 가득 채운다 */}
      <div className="flex-1 rounded-lg border bg-white pb-4 shadow-sm">
        <DashboardNav blogSlug={firstBlogSlug} />
      </div>
    </aside>
  );
}
