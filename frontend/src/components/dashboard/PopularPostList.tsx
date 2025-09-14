import { PopularPost } from '@/services/dashboardService';
import Link from 'next/link';
import React from 'react';

interface PopularPostListProps {
  posts: PopularPost[];
}

const PopularPostList = ({ posts }: PopularPostListProps) => {
  if (!posts || posts.length === 0) {
    return (
      <div className="flex h-[200px] items-center justify-center rounded-lg bg-gray-50">
        <p className="text-sm text-gray-500">아직 인기 게시글이 없습니다.</p>
      </div>
    );
  }
  return (
    <div className="space-y-1">
      {posts.map((post, index) => (
        // 4. (UX 개선) 각 항목을 클릭 가능한 링크로 만듭니다. (경로는 실제 구조에 맞게 수정 필요)
        <Link
          href={`/blog/${post.id}`}
          key={post.id}
          className="block rounded-lg transition-colors hover:bg-gray-50"
        >
          <div className="flex items-center p-2">
            <div className="mr-4 flex-shrink-0">
              {/* 5. (UI 로직 분리) badge 데이터 대신 index를 이용해 순위를 렌더링합니다. */}
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-indigo-100 font-bold text-indigo-600">
                {index + 1}
              </div>
            </div>
            <div className="flex-1 truncate">
              <p className="truncate font-medium text-gray-800">{post.title}</p>
              {/* 6. (UX 개선) 숫자에 쉼표를 추가하여 가독성을 높입니다. */}
              <p className="text-xs text-gray-500">{post.views.toLocaleString()} views</p>
            </div>
          </div>
        </Link>
      ))}
    </div>
  );
};

export default PopularPostList;
