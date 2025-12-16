import DashboardContent from '@/components/dashboard/DashboardContent';
import CreateBlogWelcome from '@/components/dashboard/CreateBlogWelcome';
import { getUserProfileAction } from '@/actions/user';
import { dehydrate, HydrationBoundary, QueryClient } from '@tanstack/react-query';
import {
  getDashboardStatsAction,
  getPopularPostsAction,
  getTrafficDataAction,
} from '@/actions/dashboard';

export default async function DashboardPage() {
  const user = await getUserProfileAction();

  if (!user?.blogs || user.blogs.length === 0) {
    return <CreateBlogWelcome />;
  }

  const slug = user.blogs[0].slug;
  const queryClient = new QueryClient();

  await Promise.all([
    queryClient.prefetchQuery({
      queryKey: ['dashboard', 'stats', slug],
      queryFn: () => getDashboardStatsAction(slug),
    }),
    queryClient.prefetchQuery({
      queryKey: ['dashboard', 'popularPosts', slug],
      queryFn: () => getPopularPostsAction(slug),
    }),
    queryClient.prefetchQuery({
      queryKey: ['dashboard', 'traffic', slug, '2weeks'],
      queryFn: () => getTrafficDataAction(slug, '2weeks'),
    }),
  ]);

  return (
    <HydrationBoundary state={dehydrate(queryClient)}>
      <DashboardContent user={user} />
    </HydrationBoundary>
  );
}
