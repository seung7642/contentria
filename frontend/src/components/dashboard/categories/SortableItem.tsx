'use client';

import { useSortable } from '@dnd-kit/sortable';
import { CategoryResponse } from '@/types/api/category';
import { CSS } from '@dnd-kit/utilities';
import {
  ChevronLeft,
  ChevronRight,
  CornerDownRight,
  FolderOpen,
  GripVertical,
  Trash2,
} from 'lucide-react';

interface SortableItemProps {
  category: CategoryResponse;
  onRemove: (id: string) => void;
  onUpdateName: (id: string, name: string) => void;
  onIndent: (id: string) => void;
  onOutdent: (id: string) => void;
}

export default function SortableItem({
  category,
  onRemove,
  onUpdateName,
  onIndent,
  onOutdent,
}: SortableItemProps) {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({
    id: category.id,
  });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    marginLeft: `${category.level * 24}px`,
    opacity: isDragging ? 0.5 : 1,
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      className={`group mb-2 flex items-center gap-2 rounded-md border bg-white p-2 hover:border-indigo-300 ${isDragging ? 'z-50 shadow-xl' : 'border-gray-200'}`}
    >
      <div {...attributes} {...listeners} className="cursor-grab text-gray-400 hover:text-gray-700">
        <GripVertical size={20} />
      </div>

      {category.level > 0 && <CornerDownRight size={16} className="text-gray-300" />}

      <FolderOpen size={18} className="text-indigo-500" />

      <input
        value={category.name}
        onChange={(e) => onUpdateName(category.id, e.target.value)}
        className="flex-1 rounded bg-transparent px-2 py-1 text-sm outline-none focus:bg-gray-50 focus:ring-1 focus:ring-indigo-500"
      />

      <div className="flex items-center gap-1 opacity-0 transition-opacity group-hover:opacity-100">
        <button
          onClick={() => onOutdent(category.id)}
          disabled={category.level === 0}
          className="rounded p-1 text-gray-400 hover:bg-gray-100 hover:text-gray-700 disabled:opacity-30"
          title="상위로 이동"
        >
          <ChevronLeft size={16} />
        </button>
        <button
          onClick={() => onIndent(category.id)}
          className="rounded p-1 text-gray-400 hover:bg-gray-100 hover:text-gray-700"
          title="하위로 이동"
        >
          <ChevronRight size={16} />
        </button>

        <div className="mx-1 h-4 w-px bg-gray-200"></div>

        <button
          onClick={() => onRemove(category.id)}
          className="rounded p-1 text-red-400 hover:bg-red-50 hover:text-red-600"
          title="삭제"
        >
          <Trash2 size={16} />
        </button>
      </div>

      <span className="text-xs text-gray-400">({category.postCount})</span>
    </div>
  );
}
