import Link from 'next/link';
import { Plus } from 'lucide-react';
import { getMyBlogAction } from '@/actions/blog';
import { PATHS } from '@/constants/paths';
import { notFound, redirect } from 'next/navigation';
import { getBlogPostsAction } from '@/actions/post';
import CustomPagination from '@/components/common/CustomPagination';
import { DataTable } from '@/components/common/DataTable';
import { columns } from '@/components/dashboard/posts/columns';

interface PostPageProps {
  searchParams: Promise<{
    page?: string;
  }>;
}

export default async function PostsPage({ searchParams }: PostPageProps) {
  const { page } = await searchParams;
  const currentPage = page ? Math.max(0, parseInt(page, 10) - 1) : 0;

  if (page && (isNaN(currentPage) || parseInt(page, 10) < 1)) {
    notFound();
  }

  const blogInfos = await getMyBlogAction();
  if (!blogInfos || blogInfos.length === 0) {
    redirect(PATHS.DASHBOARD);
  }

  const postsPage = await getBlogPostsAction(blogInfos[0].slug, {
    statuses: ['PUBLISHED', 'DRAFT'],
    page: currentPage,
    size: 10,
  });

  if (postsPage && currentPage >= postsPage.page.totalPages && postsPage.page.totalPages > 0) {
    notFound();
  }
  const initialPosts = postsPage?.content ?? [];
  const totalPages = postsPage?.page.totalPages ?? 0;

  return (
    <div className="space-y-6">
      {/* 헤더 영역 */}
      <div className="flex flex-col justify-between space-y-4 sm:flex-row sm:items-center sm:space-y-0">
        <h1 className="text-2xl font-bold text-gray-800">글 관리</h1>
        <Link
          href="/dashboard/posts/new"
          className="inline-flex items-center rounded-lg bg-indigo-600 px-4 py-2 text-white transition hover:bg-indigo-700"
        >
          <Plus size={18} className="mr-2" />새 글 작성
        </Link>
      </div>

      <DataTable columns={columns} data={initialPosts} />
      <div className="border-t border-gray-200 px-4 py-3 sm:px-6">
        <CustomPagination totalPages={totalPages} />
      </div>
    </div>
  );
}
