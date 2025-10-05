import Sidebar from '@/components/blog/Sidebar';
import { getBlogLayout } from '@/services/server/blogService';

interface SidebarSlotProps {
  params: Promise<{
    blogSlug: string;
  }>;
}

export default async function SidebarSlot({ params }: SidebarSlotProps) {
  const { blogSlug } = await params;

  const layoutData = await getBlogLayout(blogSlug);

  if (!layoutData) {
    return null;
  }

  const { blog, owner, categories } = layoutData;
  if (!categories || categories.length === 0) {
    return null;
  }

  return <Sidebar blog={blog} owner={owner} categories={categories} />;
}
