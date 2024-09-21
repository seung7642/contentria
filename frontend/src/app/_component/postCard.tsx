import { MessageCircle, ThumbsUp } from 'lucide-react';
import Image from 'next/image';
import Link from 'next/link';

interface Post {
  id: string;
  title: string;
  subTitle: string;
  thumbnail: string;
  createdAt: string;
  commentCount: number;
  likeCount: number;
}

interface PostCardProps {
  post: Post;
}

const PostCard = ({ post }: PostCardProps) => {
  return (
    <Link href={`/posts/${post.id}`} key={post.id} className="block">
      <div className="flex h-32 overflow-hidden rounded-lg bg-white shadow-md transition-all duration-300 hover:-translate-y-1 hover:shadow-lg">
        <div className="w-1/6 border-r p-4">
          <Image
            src={post.thumbnail}
            alt={`Thumbnail for ${post.title}`}
            width={100}
            height={100}
          />
        </div>
        <div className="flex w-5/6 flex-col justify-center p-4">
          <div>
            <div className="mb-2 flex items-start justify-between">
              <h2 className="mb-2 text-2xl font-semibold">{post.title}</h2>
              <span className="text-sm text-gray-500">{post.createdAt}</span>
            </div>
            <p className="text-gray-600">{post.subTitle}</p>
          </div>
          <div className="flex justify-end space-x-4">
            <div className="flex items-center text-gray-500">
              <MessageCircle size={18} className="mr-1" />
              <span>{post.commentCount}</span>
            </div>
            <div className="flex items-center text-gray-500">
              <ThumbsUp size={18} className="mr-1" />
              <span>{post.likeCount}</span>
            </div>
          </div>
        </div>
      </div>
    </Link>
  );
};

export default PostCard;
