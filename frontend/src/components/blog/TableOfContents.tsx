'use client';

import { Heading } from '@/types/common';
import { useEffect, useState } from 'react';

interface TableOfContentsProps {
  headings: Heading[];
}

const TableOfContents = ({ headings }: TableOfContentsProps) => {
  const [activeId, setActiveId] = useState<string>('');

  useEffect(() => {
    const handleScroll = () => {
      let currentId = '';
      for (const heading of headings) {
        const element = document.getElementById(heading.id);
        if (element && element.getBoundingClientRect().top < 150) {
          currentId = heading.id;
        } else {
          break;
        }
      }
      setActiveId(currentId);
    };

    window.addEventListener('scroll', handleScroll);
    handleScroll(); // 초기 로드 시 실행

    return () => window.removeEventListener('scroll', handleScroll);
  }, [headings]);

  if (headings.length == 0) {
    return null;
  }

  return (
    <aside className="sticky top-24 max-h-[calc(100vh-6rem)] overflow-y-auto">
      <h2 className="mb-4 text-sm font-semibold uppercase tracking-wide text-gray-700">
        On this page
      </h2>
      <ul className="space-y-2">
        {headings.map((heading) => (
          <li key={heading.id} style={{ paddingLeft: `${(heading.level - 1) * 1}rem` }}>
            <a
              href={`#${heading.id}`}
              className={`block text-sm transition-colors hover:text-gray-900 ${
                activeId === heading.id ? 'font-semibold text-indigo-600' : 'text-gray-500'
              }`}
            >
              {heading.text}
            </a>
          </li>
        ))}
      </ul>
    </aside>
  );
};

export default TableOfContents;
