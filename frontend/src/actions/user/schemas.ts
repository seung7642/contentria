import z from 'zod';

export const updateUserProfileRequestSchema = z.object({
  username: z
    .string()
    .min(2, '사용자 이름은 최소 2자 이상이어야 합니다.')
    .max(30, '사용자 이름은 최대 30자 이하여야 합니다.')
    .trim(),
  nickname: z
    .string()
    .min(2, '닉네임은 최소 2자 이상이어야 합니다.')
    .max(30, '닉네임은 최대 30자 이하여야 합니다.')
    .trim(),
});

export type UpdateUserProfileRequest = z.infer<typeof updateUserProfileRequestSchema>;
