import { getCategoriesAction } from '@/actions/category';
import { getUserProfileAction } from '@/actions/user';
import { PostEditorClient } from '@/components/dashboard/editor/PostEditorClient';
import { Metadata } from 'next';

export const metadata: Metadata = {
  title: '새 글 작성',
  description: '대시보드에서 새 블로그 글을 작성합니다.',
};

export default async function NewPostPage() {
  const user = await getUserProfileAction();
  const blogId = user?.blogs?.[0]?.id;
  if (!blogId) {
    return <div>블로그를 먼저 생성해주세요.</div>;
  }

  const categories = await getCategoriesAction();

  return <PostEditorClient categories={categories} blogId={blogId} />;
}
