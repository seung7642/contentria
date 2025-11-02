'use client';

import { useState } from 'react';
import Link from 'next/link';
import { ChevronDown, ChevronRight } from 'lucide-react';
import { CategoryNode } from '@/types/api/blogs';

interface CategoryItemProps {
  category: CategoryNode;
}

const CategoryItem = ({ category }: CategoryItemProps) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const hasChildren = category.children && category.children.length > 0;

  return (
    <li className="my-1 text-sm">
      {/* <div
        className={`mb-2 flex items-center justify-between rounded-lg bg-white px-4 py-2 hover:shadow-lg ${paddingClass}`}
      >
        <Link href={`/category/${category.id}`} className="text-gray-700 hover:underline">
          {category.name}
        </Link>
        {hasChildren && (
          <button
            onClick={() => setIsExpanded(!isExpanded)}
            className="text-gray-600 hover:text-gray-900 focus:outline-none"
          >
            {isExpanded ? <ChevronDown size={20} /> : <ChevronRight size={20} />}
          </button>
        )}
      </div>
      {hasChildren && isExpanded && (
        <ul>
          {category.children!.map((childCategory) => (
            <CategoryItem key={childCategory.id} category={childCategory} depth={depth + 1} />
          ))}
        </ul>
      )} */}

      <div className="5 group flex items-center justify-between rounded p-1 hover:bg-gray-100">
        <Link href={`/category/${category.slug}`} className="flex flex-grow items-center">
          <span className="font-medium text-gray-700 group-hover:text-blue-600">
            {category.name}
          </span>
          {/* 게시글 수를 뱃지 형태로 표시하여 가독성 향상 */}
          <span className="5 ml-2 rounded-full bg-indigo-100 px-2 py-0 text-xs font-semibold text-gray-600">
            {category.postCount}
          </span>
        </Link>

        {/* 확장/축소 상태를 제어하는 버튼 영역 */}
        {hasChildren && (
          <button
            onClick={() => setIsExpanded(!isExpanded)}
            className="ml-2 flex-shrink-0 p-1 text-gray-500 hover:text-gray-900 focus:outline-none"
            aria-label={`${category.name} 하위 카테고리 ${isExpanded ? '접기' : '펴기'}`}
          >
            {isExpanded ? <ChevronDown size={16} /> : <ChevronRight size={16} />}
          </button>
        )}
      </div>

      {/* 하위 카테고리 (재귀적 렌더링) */}
      {hasChildren && isExpanded && (
        <ul className="ml-4 border-l border-gray-200 pl-3 pt-1">
          {category.children.map((childCategory) => (
            <CategoryItem key={childCategory.id ?? childCategory.slug} category={childCategory} />
          ))}
        </ul>
      )}
    </li>
  );
};

export default CategoryItem;
