import { getCategoriesAction } from '@/actions/category';
import { getUserProfileAction } from '@/actions/user';
import CategoryManager from '@/components/dashboard/categories/CategoryManager';
import { PATHS } from '@/constants/paths';
import { Metadata } from 'next';
import { redirect } from 'next/navigation';

export const metadata: Metadata = {
  title: '카테고리 관리 | 대시보드',
};

export default async function CategoriesPage() {
  const user = await getUserProfileAction();
  if (!user?.blogs || user.blogs.length === 0) {
    redirect(PATHS.DASHBOARD);
  }

  const blogSlug = user.blogs[0].slug;
  const categories = await getCategoriesAction();

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-800">카테고리 관리</h1>
        <p className="mt-1 text-sm text-gray-500">카테고리 구조를 편집하고 순서를 변경합니다.</p>
      </div>

      <div className="rounded-lg border bg-white p-6 shadow-sm">
        <CategoryManager initialCategories={categories} blogSlug={blogSlug} />
      </div>
    </div>
  );
}
