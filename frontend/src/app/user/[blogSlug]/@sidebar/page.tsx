import { getBlogLayoutAction } from '@/actions/blog';
import Sidebar from '@/components/blog/Sidebar';

interface SidebarSlotProps {
  params: Promise<{
    blogSlug: string;
  }>;
}

export default async function SidebarSlot({ params }: SidebarSlotProps) {
  const { blogSlug } = await params;

  const layoutData = await getBlogLayoutAction(blogSlug);

  if (!layoutData) {
    return null;
  }

  const { blog, owner, categories } = layoutData;
  if (!categories || categories.length === 0) {
    return null;
  }

  return <Sidebar blog={blog} owner={owner} categories={categories} />;
}
