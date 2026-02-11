import Link from 'next/link';
import { Plus, Edit, Eye, Trash } from 'lucide-react';
import { getMyBlogAction } from '@/actions/blog';
import { PATHS } from '@/constants/paths';
import { redirect } from 'next/navigation';
import { getBlogPostsAction } from '@/actions/post';

export default async function PostsPage() {
  const blogInfos = await getMyBlogAction();
  if (!blogInfos || blogInfos.length === 0) {
    redirect(PATHS.DASHBOARD);
  }

  const posts = await getBlogPostsAction(blogInfos[0].slug, {
    statuses: ['PUBLISHED', 'DRAFT'],
    page: 0,
    size: 10,
  });

  return (
    <div className="space-y-6">
      {/* 헤더 영역 */}
      <div className="flex flex-col justify-between space-y-4 sm:flex-row sm:items-center sm:space-y-0">
        <h1 className="text-2xl font-bold text-gray-800">글 관리</h1>
        <Link
          href="/dashboard/posts/create"
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
              {posts && posts.page.totalElements > 0 ? (
                posts.content.map((post) => (
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
                        <button className="rounded p-1 text-blue-600 hover:bg-blue-100">
                          <Eye size={18} />
                        </button>
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
                <p>게시물이 없습니다.</p>
              )}
            </tbody>
          </table>
        </div>

        {/* 페이지네이션 */}
        <div className="border-t border-gray-200 px-4 py-3 sm:px-6">
          <div className="flex items-center justify-between">
            <div className="text-sm text-gray-700">
              총 <span className="font-medium">{posts?.page.totalElements}</span>개의 글
            </div>
            <div className="flex items-center space-x-2">
              <button className="rounded-md border border-gray-300 bg-white px-3 py-1 text-sm hover:bg-gray-50">
                이전
              </button>
              <button className="rounded-md bg-indigo-600 px-3 py-1 text-sm text-white hover:bg-indigo-700">
                1
              </button>
              <button className="rounded-md border border-gray-300 bg-white px-3 py-1 text-sm hover:bg-gray-50">
                다음
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
