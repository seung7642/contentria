export interface Page<T> {
  content: T[];
  page: PageMetadata;
}

export interface PageMetadata {
  size: number;
  totalElements: number;
  totalPages: number;
  number: number;
}
