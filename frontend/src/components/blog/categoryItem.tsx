'use client';

import { useState } from 'react';
import Link from 'next/link';
import { Category } from './sidebar';
import { ChevronDown, ChevronRight } from 'lucide-react';

const CategoryItem: React.FC<{ category: Category; depth?: number }> = ({
  category,
  depth = 0,
}) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const hasChildren = category.children && category.children.length > 0;
  const paddingClasses = ['pl-4', 'pl-8', 'pl-12'];
  const paddingClass = paddingClasses[depth] || 'pl-16';
  return (
    <li>
      <div
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
      )}
    </li>
  );
};

export default CategoryItem;
