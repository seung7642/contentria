import { getMyBlogAction } from '@/actions/blog';
import { getRawUserProfileAction } from '@/actions/user';
import BlogSettingsForm from '@/components/dashboard/settings/BlogSettingsForm';
import ProfileForm from '@/components/dashboard/settings/ProfileForm';

export default async function SettingsPage() {
  const user = await getRawUserProfileAction();
  // (main) 레이아웃 가드가 블로그 존재를 보장하므로 첫 블로그를 사용
  const blogInfos = await getMyBlogAction();
  const blog = blogInfos?.[0];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight text-gray-900">설정</h1>
        <p className="text-muted-foreground">블로그와 계정 프로필 설정을 관리하세요.</p>
      </div>

      <div className="border-b border-gray-200" />

      {blog && <BlogSettingsForm blog={blog} />}

      <ProfileForm initialUser={user} />
    </div>
  );
}
