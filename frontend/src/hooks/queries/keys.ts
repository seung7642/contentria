// Query Key는 React Query에서 캐싱 및 상태 관리를 위해 사용되는 고유 식별자입니다.
// 그리고 이 Query Key들은 한 곳에서 관리되어야 Mutation 후 관련된 Query들을 쉽게 무효화하거나 갱신할 수 있습니다.
// 다음은 예시입니다.
export const authKeys = {
  all: ['auth'] as const,
  user: () => [...authKeys.all, 'user'] as const,
  session: () => [...authKeys.all, 'session'] as const,
};

export const userKeys = {
  all: ['user'] as const,
  profile: () => [...userKeys.all, 'profile'] as const,
};
