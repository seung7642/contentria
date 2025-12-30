import Image from 'next/image';
import CategoryItem from './CategoryItem';
import { BlogSummary } from '@/types/api/blogs';
import { getHighResGoogleProfileImage } from '@/lib/imageUtil';
import { UserSummaryResponse } from '@/types/api/user';
import { CategoryResponse } from '@/types/api/category';
import { buildCategoryTree } from '@/lib/categoryTree';

interface SidebarProps {
  blog: BlogSummary;
  owner: UserSummaryResponse;
  categories: CategoryResponse[];
}

export default function Sidebar({ blog, owner, categories }: SidebarProps) {
  const profileImageUrl =
    getHighResGoogleProfileImage(owner.pictureUrl, 256) ?? '/images/default-profile.png';
  const blogDescription = blog.description ?? '블로그에 오신 것을 환영합니다.';

  const categoryTree = buildCategoryTree(categories, blog.slug);

  return (
    <aside className="m-2 flex w-full max-w-xs flex-col overflow-auto rounded border border-gray-200 bg-gray-50 p-4 shadow-sm lg:max-w-sm">
      {/* 프로필 박스 */}
      <div className="mb-6 flex flex-col items-center border bg-white p-4">
        <div className="relative mb-4 flex h-48 w-48 justify-center overflow-hidden rounded-lg bg-gray-300 md:h-56 md:w-56">
          <Image
            src={profileImageUrl}
            alt="profile"
            fill // 부모 요소에 맞게 이미지를 채운다. (부모 요소에 position: relative 필요)
            style={{ objectFit: 'cover' }} // 이미지 비율을 유지하며 채운다.
            sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw" // 반응형 이미지 크기 설정
            priority // 사이드바 이미지는 중요하므로 우선적으로 로드한다.
            className="rounded-lg"
          />
        </div>
        <h2 className="mb-2 text-lg">닉네임</h2>
        <p className="text-center text-sm text-gray-600">{blogDescription}</p>
      </div>

      {/* 카테고리 박스 */}
      {categories.length > 0 && (
        <div className="mb-6 border bg-white p-4">
          <h2 className="mb-4 text-center text-xl font-bold text-indigo-600">카테고리</h2>
          <ul>
            {categoryTree.map((node) => (
              <CategoryItem key={node.id} category={node} blogSlug={blog.slug} />
            ))}
          </ul>
        </div>
      )}

      {/* 아이콘 박스 */}
      <div className="mt-auto border-t border-gray-200 pt-4">
        <div className="flex justify-around">
          <a
            href="https://github.com"
            target="_blank"
            rel="noopener noreferrer"
            className="text-gray-600 hover:text-gray-900"
          >
            <Image src="/icons/github.svg" alt="GitHub" width={24} height={24} />
          </a>
          <a href="mailto:seung7642@gmail.com" className="text-gray-600 hover:text-gray-900">
            <Image src="/icons/gmail.svg" alt="Email" width={24} height={24} />
          </a>
          <a
            href="/rss.xml"
            target="_blank"
            rel="noopener noreferrer"
            className="text-gray-600 hover:text-gray-900"
          >
            <Image src="/icons/rss.svg" alt="RSS Feed" width={24} height={24} />
          </a>
        </div>
      </div>
    </aside>
  );
}
