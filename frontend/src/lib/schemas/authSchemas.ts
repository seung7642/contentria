import z from 'zod';

export const emailStepSchema = z.object({
  name: z.string().min(1, { message: 'Please enter your name.' }),
  email: z
    .string()
    .min(1, { message: 'Please enter your email address.' })
    .email({ message: 'Please enter a valid email address.' }),
});

export const passwordStepSchema = z.object({
  password: z
    .string()
    .min(10, { message: '비밀번호는 최소 10자 이상이어야 합니다.' })
    .max(128, { message: '비밀번호는 최대 128자까지 설정 가능합니다.' }) // 선택적 최대 길이
    .regex(/[a-z]/, { message: '비밀번호에는 최소 1개의 소문자가 포함되어야 합니다.' })
    .regex(/[A-Z]/, { message: '비밀번호에는 최소 1개의 대문자가 포함되어야 합니다.' })
    .regex(/[0-9]/, { message: '비밀번호에는 최소 1개의 숫자가 포함되어야 합니다.' })
    .regex(/[^a-zA-Z0-9]/, { message: '비밀번호에는 최소 1개의 특수문자가 포함되어야 합니다.' }), // 특수문자 요구 시 주석 해제
});

export type EmailStepFormData = z.infer<typeof emailStepSchema>;
export type PasswordStepFormData = z.infer<typeof passwordStepSchema>;
