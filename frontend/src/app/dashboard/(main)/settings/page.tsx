import { getUserProfileAction } from '@/actions/user';
import ProfileForm from '@/components/dashboard/settings/ProfileForm';

export default async function SettingsPage() {
  const user = await getUserProfileAction();

  if (!user) {
    return <div>사용자 정보를 가져올 수 없습니다.</div>;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight text-gray-900">설정</h1>
        <p className="text-muted-foreground">계정 및 프로필 설정을 관리하세요.</p>
      </div>

      <div className="border-b border-gray-200" />

      <ProfileForm initialUser={user} />
    </div>
  );
}
