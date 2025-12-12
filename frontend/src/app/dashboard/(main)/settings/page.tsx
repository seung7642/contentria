'use client';

import { useEffect } from 'react';
import { useForm, SubmitHandler } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Loader2 } from 'lucide-react';
import InputField from '@/components/common/InputField';

import { useUserProfile } from '@/hooks/queries/useUserQuery';
import { useUpdateUserProfileMutation } from '@/hooks/mutations/useUserMutations';

// 1. 설정 폼의 유효성 검사를 위한 Zod 스키마
const profileSchema = z.object({
  name: z.string().min(2, '이름은 2자 이상이어야 합니다.'),
});

type ProfileFormData = z.infer<typeof profileSchema>;

// 2. 재사용 가능한 설정 섹션 카드 UI 컴포넌트
const SettingsCard = ({
  title,
  description,
  children,
  footer,
}: {
  title: string;
  description: string;
  children: React.ReactNode;
  footer: React.ReactNode;
}) => (
  <div className="divide-y divide-gray-200 rounded-lg bg-white shadow-sm">
    <div className="px-4 py-5 sm:px-6">
      <h2 className="text-lg font-medium leading-6 text-gray-900">{title}</h2>
      <p className="mt-1 max-w-2xl text-sm text-gray-500">{description}</p>
    </div>
    <div className="px-4 py-5 sm:p-6">{children}</div>
    <div className="bg-gray-50 px-4 py-4 sm:px-6">{footer}</div>
  </div>
);

// 3. 메인 설정 페이지 컴포넌트
const SettingsPage = () => {
  const { data: user, isPending: isUserLoading } = useUserProfile();

  // 4. 프로필 업데이트를 위한 useMutation 훅 사용
  const { mutate: updateProfile, isPending: isUpdating } = useUpdateUserProfileMutation();

  const {
    register,
    handleSubmit,
    reset,
    formState: { isDirty }, // isDirty는 폼이 수정되었는지 여부를 추적합니다.
  } = useForm<ProfileFormData>({
    resolver: zodResolver(profileSchema),
  });

  // 5. useUserQuery로 사용자 데이터를 성공적으로 가져오면, form의 기본값을 설정합니다.
  useEffect(() => {
    if (user) {
      reset({ name: user.name });
    }
  }, [user, reset]);

  const onSubmit: SubmitHandler<ProfileFormData> = (data) => {
    updateProfile(data);
  };

  if (isUserLoading) {
    return (
      <div className="flex justify-center p-10">
        <Loader2 className="h-8 w-8 animate-spin text-gray-400" />
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-3xl space-y-8">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">계정 설정</h1>
        <p className="mt-1 text-sm text-gray-500">프로필 정보 및 계정 설정을 관리하세요.</p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)}>
        <SettingsCard
          title="프로필"
          description="이 정보는 블로그에 공개적으로 표시될 수 있습니다."
          footer={
            <div className="flex justify-end">
              <button
                type="submit"
                disabled={!isDirty || isUpdating}
                className="inline-flex items-center justify-center rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
              >
                {isUpdating && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                변경사항 저장
              </button>
            </div>
          }
        >
          <div className="space-y-4">
            <InputField
              id="name"
              label="이름"
              type="text"
              placeholder="name"
              {...register('name')}
            />
            <InputField
              id="email"
              label="이메일"
              type="email"
              placeholder="email"
              value={user?.email || ''}
              disabled
            />
          </div>
        </SettingsCard>
      </form>
      {/* 여기에 계정 삭제, 보안 설정 등 다른 카드들이 추가될 수 있습니다. */}
    </div>
  );
};

export default SettingsPage;
