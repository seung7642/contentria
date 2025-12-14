interface Category {
  id: string;
  name: string;
  slug: string;
  parentId: string | null;
  level: number; // 0 for top-level categories, 1 for sub-categories, etc.
}
