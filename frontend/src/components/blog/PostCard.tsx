'use client';

import { formatPostDate } from '@/lib/dateFormatUtil';
import { ThumbsUp } from 'lucide-react';
import Image from 'next/image';
import Link from 'next/link';

export interface PostSummary {
  id: string;
  slug: string;
  title: string;
  summary: string;
  metaTitle: string | null;
  metaDescription: string | null;
  featuredImageUrl: string | null;
  publishedAt: string;
  categoryName: string | null;
  likeCount: number;
  viewCount: number;
}

interface PostCardProps {
  blogSlug: string;
  post: PostSummary;
}

const PostCard = ({ blogSlug, post }: PostCardProps) => {
  return (
    <Link href={`/@${blogSlug}/${post.id}`} key={post.id} className="block">
      <div className="flex h-32 overflow-hidden rounded-lg bg-white shadow-md transition-all duration-300 hover:-translate-y-1 hover:shadow-lg">
        <div className="flex w-1/6 flex-col items-center border-r p-4">
          <Image
            src={post.featuredImageUrl ?? '/images/default-thumbnail-01.png'}
            alt={`Thumbnail for ${post.title}`}
            width={80}
            height={80}
          />
        </div>
        <div className="flex w-5/6 flex-col justify-center p-4">
          <div>
            <div className="mb-2 flex items-start justify-between">
              <h2 className="mb-2 text-2xl font-semibold">{post.title}</h2>
              <span className="text-sm text-gray-500">{formatPostDate(post.publishedAt)}</span>
            </div>
            {/* <p className="text-gray-600">{post.summary}</p> */}
          </div>
          <div className="flex justify-end space-x-4">
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
