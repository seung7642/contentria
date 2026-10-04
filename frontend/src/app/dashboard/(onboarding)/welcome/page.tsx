import { redirect } from 'next/navigation';
import CreateBlogWelcome from '@/components/dashboard/CreateBlogWelcome';
import { getMyBlogAction } from '@/actions/blog';

export default async function WelcomePage() {
  const blogInfos = await getMyBlogAction();

  // Reverse guard: users who already own a blog don't belong on the onboarding screen.
  if (blogInfos && blogInfos.length > 0) {
    redirect('/dashboard');
  }

  return <CreateBlogWelcome />;
}
