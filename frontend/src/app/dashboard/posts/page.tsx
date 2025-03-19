import { useState } from 'react';
import Link from 'next/link';
import { Search, Filter, Plus, Edit, Eye, Trash, AlertCircle } from 'lucide-react';

// 필터 컴포넌트
const FilterDropdown = ({ isOpen, onClose }) => {
  if (!isOpen) return null;

  return (
    <div className="absolute right-0 top-full z-10 mt-2 w-64 rounded-lg border bg-white p-4 shadow-lg">
      <h3 className="mb-3 font-medium">필터</h3>

      <div className="mb-3">
        <label className="mb-1 block text-sm font-medium text-gray-700">상태</label>
        <select className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm">
          <option>모든 상태</option>
          <option>발행됨</option>
          <option>임시저장</option>
        </select>
      </div>

      <div className="mb-3">
        <label className="mb-1 block text-sm font-medium text-gray-700">정렬</label>
        <select className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm">
          <option>최신순</option>
          <option>오래된순</option>
          <option>조회수 높은순</option>
          <option>댓글 많은순</option>
        </select>
      </div>

      <div className="mt-4 flex justify-end space-x-2">
        <button
          className="rounded-lg border px-3 py-1 text-sm text-gray-600 hover:bg-gray-50"
          onClick={onClose}
        >
          취소
        </button>
        <button
          className="rounded-lg bg-indigo-600 px-3 py-1 text-sm text-white hover:bg-indigo-700"
          onClick={onClose}
        >
          적용
        </button>
      </div>
    </div>
  );
};

// 삭제 확인 모달
const DeleteConfirmModal = ({ isOpen, onClose, onConfirm }) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
      <div className="w-full max-w-md rounded-lg bg-white p-6 shadow-xl">
        <div className="mb-4 flex items-center">
          <AlertCircle className="mr-3 text-red-500" size={24} />
          <h3 className="text-lg font-medium">게시글 삭제</h3>
        </div>
        <p className="mb-4 text-gray-600">
          이 게시글을 정말 삭제하시겠습니까? 삭제된 게시글은 복구할 수 없습니다.
        </p>
        <div className="flex justify-end space-x-3">
          <button
            className="rounded-lg border border-gray-300 px-4 py-2 text-gray-700 hover:bg-gray-50"
            onClick={onClose}
          >
            취소
          </button>
          <button
            className="rounded-lg bg-red-600 px-4 py-2 text-white hover:bg-red-700"
            onClick={() => {
              onConfirm();
              onClose();
            }}
          >
            삭제
          </button>
        </div>
      </div>
    </div>
  );
};

export default function PostsPage() {
  const [isFilterOpen, setIsFilterOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedPostId, setSelectedPostId] = useState(null);

  // 샘플 데이터
  const [posts, setPosts] = useState([
    {
      id: '1',
      title: 'Next.js 14와 React Server Components',
      status: 'published',
      date: '2024-03-15',
      views: 152,
      comments: 8,
    },
    {
      id: '2',
      title: '효율적인 상태 관리 전략',
      status: 'published',
      date: '2024-03-10',
      views: 128,
      comments: 5,
    },
    {
      id: '3',
      title: '모바일 최적화를 위한 디자인 팁',
      status: 'published',
      date: '2024-03-08',
      views: 98,
      comments: 3,
    },
    {
      id: '4',
      title: '커스텀 훅 작성 가이드 (임시저장)',
      status: 'draft',
      date: '2024-03-05',
      views: 0,
      comments: 0,
    },
  ]);

  const handleDeletePost = () => {
    setPosts(posts.filter((post) => post.id !== selectedPostId));
  };

  const openDeleteModal = (id) => {
    setSelectedPostId(id);
    setIsDeleteModalOpen(true);
  };

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

      {/* 검색 및 필터 */}
      <div className="flex flex-col space-y-4 sm:flex-row sm:items-center sm:justify-between sm:space-y-0">
        <div className="relative w-full sm:max-w-xs">
          <div className="absolute inset-y-0 left-0 flex items-center pl-3">
            <Search size={18} className="text-gray-400" />
          </div>
          <input
            type="text"
            placeholder="글 제목 검색..."
            className="w-full rounded-lg border border-gray-300 py-2 pl-10 pr-4 focus:border-indigo-500 focus:outline-none focus:ring-1 focus:ring-indigo-500"
          />
        </div>

        <div className="relative">
          <button
            onClick={() => setIsFilterOpen(!isFilterOpen)}
            className="inline-flex items-center rounded-lg border border-gray-300 bg-white px-4 py-2 hover:bg-gray-50"
          >
            <Filter size={18} className="mr-2" />
            필터
          </button>
          <FilterDropdown isOpen={isFilterOpen} onClose={() => setIsFilterOpen(false)} />
        </div>
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
              {posts.map((post) => (
                <tr key={post.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4">
                    <div className="font-medium text-gray-900">{post.title}</div>
                  </td>
                  <td className="px-6 py-4">
                    <span
                      className={`inline-flex rounded-full px-2 py-1 text-xs font-medium ${
                        post.status === 'published'
                          ? 'bg-green-100 text-green-800'
                          : 'bg-yellow-100 text-yellow-800'
                      }`}
                    >
                      {post.status === 'published' ? '발행됨' : '임시저장'}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-500">{post.date}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{post.views.toLocaleString()}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{post.comments}</td>
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
                      <button
                        className="rounded p-1 text-red-600 hover:bg-red-100"
                        onClick={() => openDeleteModal(post.id)}
                      >
                        <Trash size={18} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* 페이지네이션 */}
        <div className="border-t border-gray-200 px-4 py-3 sm:px-6">
          <div className="flex items-center justify-between">
            <div className="text-sm text-gray-700">
              총 <span className="font-medium">{posts.length}</span>개의 글
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

      {/* 삭제 확인 모달 */}
      <DeleteConfirmModal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        onConfirm={handleDeletePost}
      />
    </div>
  );
}
