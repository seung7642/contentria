import Link from 'next/link';
import { Plus, Edit, Eye, Trash } from 'lucide-react';
import { getMyBlogAction } from '@/actions/blog';
import { PATHS } from '@/constants/paths';
import { notFound, redirect } from 'next/navigation';
import { getBlogPostsAction } from '@/actions/post';
import CustomPagination from '@/components/common/CustomPagination';

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

      {/* 게시글 테이블 */}
      <div className="rounded-lg border bg-white shadow-sm">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th
                  scope="col"
                  className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500"
                >
                  제목
                </th>
                <th
                  scope="col"
                  className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500"
                >
                  상태
                </th>
                <th
                  scope="col"
                  className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500"
                >
                  작성일
                </th>
                <th
                  scope="col"
                  className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500"
                >
                  조회수
                </th>
                <th
                  scope="col"
                  className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500"
                >
                  댓글
                </th>
                <th
                  scope="col"
                  className="px-6 py-3 text-center text-xs font-medium uppercase text-gray-500"
                >
                  작업
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200 bg-white">
              {initialPosts.length > 0 ? (
                initialPosts.map((post) => (
                  <tr key={post.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4">
                      <div className="font-medium text-gray-900">{post.title}</div>
                    </td>
                    <td className="px-6 py-4">
                      <span
                        className={`inline-flex rounded-full px-2 py-1 text-xs font-medium ${
                          post.status === 'PUBLISHED'
                            ? 'bg-green-100 text-green-800'
                            : 'bg-yellow-100 text-yellow-800'
                        }`}
                      >
                        {post.status === 'PUBLISHED' ? '발행됨' : '임시저장'}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-500">{post.createdAt}</td>
                    <td className="px-6 py-4 text-sm text-gray-500">0</td>
                    <td className="px-6 py-4 text-sm text-gray-500">0</td>
                    <td className="px-6 py-4 text-center text-sm font-medium">
                      <div className="flex items-center justify-center space-x-2">
                        <Link
                          href={`/dashboard/posts/${post.id}`}
                          className="rounded p-1 text-indigo-600 hover:bg-indigo-100"
                        >
                          <Edit size={18} />
                        </Link>
                        <button className="rounded p-1 text-red-600 hover:bg-red-100">
                          <Trash size={18} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={6} className="px-6 py-4 text-center text-sm text-gray-500">
                    게시물이 없습니다.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* 페이지네이션 */}
        <div className="border-t border-gray-200 px-4 py-3 sm:px-6">
          <CustomPagination totalPages={totalPages} />
        </div>
      </div>
    </div>
  );
}
