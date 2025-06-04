import React from 'react';

interface Post {
  id: number;
  title: string;
  views: number;
  badge: string;
}

interface PopularPostListProps {
  posts: Post[];
}

const PopularPostList: React.FC<PopularPostListProps> = ({ posts }) => {
  return (
    <div className="space-y-1">
      {posts.map((post) => (
        <div key={post.id} className="flex items-center border-b border-gray-100 py-3">
          <div className="mr-2 flex-shrink-0">
            <div className="flex h-10 w-10 items-center justify-center rounded-full bg-indigo-100 text-indigo-600">
              {post.badge}
            </div>
          </div>
          <div className="flex-1 truncate">
            <p className="truncate font-medium">{post.title}</p>
            <p className="text-xs text-gray-500">{post.views} 조회</p>
          </div>
        </div>
      ))}
    </div>
  );
};

export default PopularPostList;
