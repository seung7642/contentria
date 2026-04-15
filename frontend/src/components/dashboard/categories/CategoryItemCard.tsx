'use client';

import { CategoryResponse } from '@/types/api/category';
import type { DraggableSyntheticListeners } from '@dnd-kit/core';
import {
  ChevronLeft,
  ChevronRight,
  CornerDownRight,
  FileText,
  Folder,
  GripVertical,
  Plus,
  Trash2,
} from 'lucide-react';

interface CategoryItemCardProps {
  category: CategoryResponse;
  isOverlay?: boolean; // 드래그 중인 잔상(오버레이)인지 여부
  canIndent?: boolean;
  canOutdent?: boolean;
  showAddSubCategoryButton?: boolean;
  onRemove?: (id: string) => void;
  onUpdateName?: (id: string, name: string) => void;
  onIndent?: (id: string) => void;
  onOutdent?: (id: string) => void;
  onAddSubCategory?: (parentId: string) => void;
  dragHandleProps?: DraggableSyntheticListeners;
}

export default function CategoryItemCard({
  category,
  isOverlay,
  canIndent,
  canOutdent,
  showAddSubCategoryButton,
  onRemove,
  onUpdateName,
  onIndent,
  onOutdent,
  onAddSubCategory,
  dragHandleProps,
}: CategoryItemCardProps) {
  return (
    <div
      className={`group mb-2 flex items-center gap-2 rounded-md border bg-white p-3 ${isOverlay ? 'scale-105 shadow-xl ring-2 ring-indigo-500' : 'border-gray-200 hover:border-indigo-300'}`}
      style={{ marginLeft: isOverlay ? 0 : `${category.level * 32}px` }}
    >
      <div {...dragHandleProps} className="cursor-grab text-gray-400 hover:text-gray-700">
        <GripVertical size={20} />
      </div>

      {category.level > 0 ? (
        <CornerDownRight size={18} className="text-gray-400" />
      ) : (
        <Folder size={18} className="text-indigo-500" />
      )}

      <input
        value={category.name}
        onChange={(e) => onUpdateName?.(category.id, e.target.value)}
        className="flex-1 rounded bg-transparent px-2 py-1 text-sm font-medium text-gray-700 outline-none focus:bg-gray-50 focus:ring-1 focus:ring-indigo-500"
        placeholder="카테고리명"
        readOnly={isOverlay}
      />

      <div className="flex items-center gap-1">
        <div className="mr-2 flex items-center text-xs text-gray-400">
          <FileText size={12} className="mr-1" />
          {category.postCount}
        </div>

        {!isOverlay && (
          <div className="flex items-center gap-1 opacity-0 transition-opacity group-hover:opacity-100">
            {showAddSubCategoryButton && (
              <button
                onClick={() => onAddSubCategory?.(category.id)}
                className="p-1 text-gray-400 hover:text-indigo-600"
              >
                <Plus size={18} />
              </button>
            )}

            <div className="mx-1 h-4 w-px bg-gray-200"></div>

            <button
              onClick={() => onOutdent?.(category.id)}
              disabled={!canOutdent}
              className="p-1 text-gray-400 hover:text-indigo-600 disabled:opacity-20"
            >
              <ChevronLeft size={18} />
            </button>
            <button
              onClick={() => onIndent?.(category.id)}
              disabled={!canIndent}
              className="p-1 text-gray-400 hover:text-indigo-600 disabled:opacity-20"
            >
              <ChevronRight size={18} />
            </button>

            <div className="mx-1 h-4 w-px bg-gray-200"></div>

            <button
              onClick={() => onRemove?.(category.id)}
              className="p-1 text-red-400 hover:text-red-600"
            >
              <Trash2 size={18} />
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
