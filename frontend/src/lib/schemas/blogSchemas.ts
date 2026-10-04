import { z } from 'zod';

/** 블로그 설정(제목/설명) 폼 — 백엔드 UpdateBlogSettingsRequest의 제약과 동일하게 유지할 것 */
export const blogSettingsSchema = z.object({
  title: z
    .string()
    .min(1, '블로그 제목을 입력해주세요.')
    .max(255, '블로그 제목은 255자 이하로 입력해주세요.'),
  description: z.string().max(500, '블로그 설명은 500자 이하로 입력해주세요.'),
});

export type BlogSettingsFormValues = z.infer<typeof blogSettingsSchema>;
