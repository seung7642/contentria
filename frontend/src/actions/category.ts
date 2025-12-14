export async function getCategoriesAction(): Promise<Category[]> {
  return [
    { id: '1', name: '기술', slug: 'technology', parentId: null, level: 0 },
    { id: '2', name: '백엔드', slug: 'backend', parentId: '1', level: 1 },
    { id: '3', name: '프론트엔드', slug: 'frontend', parentId: '1', level: 1 },

    { id: '4', name: '여행', slug: 'travel', parentId: null, level: 0 },
    { id: '5', name: '일상', slug: 'daily-life', parentId: null, level: 0 },
    { id: '6', name: '맛집', slug: 'food', parentId: '5', level: 1 },
  ];
}
