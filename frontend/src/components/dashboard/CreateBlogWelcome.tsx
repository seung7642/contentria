import { useCreateBlogMutation } from '@/hooks/queries/useUserMutations';
import { zodResolver } from '@hookform/resolvers/zod';
import { AlertTriangle, Loader2 } from 'lucide-react';
import { SubmitHandler, useForm } from 'react-hook-form';
import z from 'zod';
import InputWithAddon from '../ui/InputWithAddon';

const schema = z.object({
  slug: z
    .string()
    .min(3, '3자 이상 입력해주세요.')
    .max(30, '30자 이하로 입력해주세요.')
    .regex(/^[a-zA-Z0-9-_]+$/, '영문, 숫자, -, _ 문자만 사용할 수 있습니다.'),
});

type FormValues = z.infer<typeof schema>;

const CreateBlogWelcome = () => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
  });

  const { mutate, isPending, error } = useCreateBlogMutation();

  const onSubmit: SubmitHandler<FormValues> = (data) => {
    mutate(data);
  };

  return (
    <div className="rounded-lg bg-white p-6 shadow-sm">
      <div className="mx-auto max-w-xl text-center">
        {' '}
        {/* max-w-lg -> max-w-xl로 약간 넓게 */}
        <h2 className="text-3xl font-bold tracking-tight text-gray-900 sm:text-4xl">
          당신의 공간을 만들어보세요
        </h2>
        <p className="mt-4 text-lg leading-8 text-gray-600">
          멋진 아이디어를 세상과 공유할 준비가 되셨나요? 사용할 블로그 주소를 만들어주세요.
        </p>
        <form onSubmit={handleSubmit(onSubmit)} className="mt-10 space-y-6">
          {' '}
          {/* mt-8 -> mt-10 */}
          {/* InputWithAddon 컴포넌트 사용 */}
          <InputWithAddon
            id="slug"
            addon="https://contentria.com/@"
            placeholder="your-blog-name"
            autoFocus
            autoComplete="off"
            errorMessage={errors.slug?.message}
            {...register('slug')}
          />
          {error && (
            <div className="rounded-md bg-red-50 p-4 text-left">
              <div className="flex">
                <div className="flex-shrink-0">
                  <AlertTriangle className="h-5 w-5 text-red-400" aria-hidden="true" />
                </div>
                <div className="ml-3">
                  <h3 className="text-sm font-medium text-red-800">{error.message}</h3>
                </div>
              </div>
            </div>
          )}
          <button
            type="submit"
            disabled={isPending}
            className="flex w-full items-center justify-center rounded-lg border border-transparent bg-indigo-600 px-8 py-3.5 text-base font-semibold text-white shadow-sm hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:cursor-not-allowed disabled:bg-indigo-400" // py-3 -> py-3.5, font-medium -> font-semibold
          >
            {isPending ? (
              <>
                <Loader2 className="mr-2 h-5 w-5 animate-spin" />
                생성 중...
              </>
            ) : (
              '내 블로그 생성하기'
            )}
          </button>
        </form>
        <p className="mt-6 text-sm text-gray-500">
          {' '}
          {/* mt-4 -> mt-6 */}
          블로그 주소는 나중에 변경할 수 없으니 신중하게 선택해주세요.
        </p>
      </div>
    </div>
  );
};

export default CreateBlogWelcome;
