import { getMyBlogAction } from '@/actions/blog';
import { getCategoriesAction } from '@/actions/category';
import { getPostDetailAction, getPostDetailByIdAction } from '@/actions/post';
import { PostEditorClient } from '@/components/dashboard/editor/PostEditorClient';
import { Metadata } from 'next';

export async function generateMetadata({
  params,
}: {
  params: { postId: string };
}): Promise<Metadata> {
  return {
    title: '게시글 수정',
    description: '작성한 게시글을 수정합니다.',
  };
}

interface EditPostPageProps {
  params: Promise<{
    postId: string;
  }>;
}

export default async function EditPostPage({ params }: EditPostPageProps) {
  const { postId } = await params;

  const [blogInfos, categories, postDetail] = await Promise.all([
    getMyBlogAction(),
    getCategoriesAction(),
    getPostDetailByIdAction(postId),
  ]);

  const blogId = blogInfos?.[0]?.id;
  if (!blogId) {
    return <div>블로그를 먼저 생성해주세요.</div>;
  }

  if (!postDetail) {
    return <div>게시글을 찾을 수 없습니다.</div>;
  }

  return <PostEditorClient blogId={blogId} categories={categories} initialData={postDetail} />;
}
