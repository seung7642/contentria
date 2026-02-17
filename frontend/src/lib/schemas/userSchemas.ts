import z from 'zod';

export const profileSchema = z.object({
  username: z.string().min(2, '이름은 2자 이상이어야 합니다.'),
  nickname: z.string().min(2, '닉네임은 2자 이상이어야 합니다.'),
});

export type ProfileFormValues = z.infer<typeof profileSchema>;
