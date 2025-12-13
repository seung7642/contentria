export async function getCategoriesAction(): Promise<Category[]> {
  return [
    { id: 1, name: '기술', slug: 'technology' },
    { id: 2, name: '백엔드', slug: 'backend' },
    { id: 3, name: '여행', slug: 'travel' },
  ];
}
