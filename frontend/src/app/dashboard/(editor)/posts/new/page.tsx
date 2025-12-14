import { getCategoriesAction } from '@/actions/category';
import { PostEditorClient } from '@/components/dashboard/editor/PostEditorClient';
import { Metadata } from 'next';

export const metadata: Metadata = {
  title: '새 글 작성',
  description: '대시보드에서 새 블로그 글을 작성합니다.',
};

export default async function NewPostPage() {
  const categories = await getCategoriesAction();

  return <PostEditorClient categories={categories} />;
}
