import z from 'zod';

export const profileSchema = z.object({
  name: z.string().min(2, '이름은 2자 이상이어야 합니다.'),
});

export type ProfileFormValues = z.infer<typeof profileSchema>;
