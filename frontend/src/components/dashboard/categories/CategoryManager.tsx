'use client';

import { Button } from '@/components/ui/button';
import { CategoryResponse } from '@/types/api/category';
import {
  closestCenter,
  DndContext,
  DragEndEvent,
  KeyboardSensor,
  PointerSensor,
  useSensor,
  useSensors,
} from '@dnd-kit/core';
import {
  arrayMove,
  SortableContext,
  sortableKeyboardCoordinates,
  verticalListSortingStrategy,
} from '@dnd-kit/sortable';
import { Plus, RotateCcw, Save } from 'lucide-react';
import { useState } from 'react';
import SortableItem from './SortableItem';

interface CategoryManagerProps {
  initialCategories: CategoryResponse[];
  blogSlug: string;
}

export default function CategoryManager({ initialCategories, blogSlug }: CategoryManagerProps) {
  const [items, setItems] = useState<CategoryResponse[]>(initialCategories);
  const [isSaving, setIsSaving] = useState(false);

  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    })
  );

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;

    if (over && active.id !== over.id) {
      setItems((items) => {
        const oldIndex = items.findIndex((item) => item.id === active.id);
        const newIndex = items.findIndex((item) => item.id === over.id);
        return arrayMove(items, oldIndex, newIndex);
      });
    }
  };

  const handleAddCategory = () => {
    const newCategory: CategoryResponse = {
      id: `new-${Date.now()}`,
      name: '새 카테고리',
      slug: '',
      parentId: null,
      level: 0,
      postCount: 0,
    };
    setItems([...items, newCategory]);
  };

  const handleUpdateName = (id: string, newName: string) => {
    setItems(items.map((item) => (item.id === id ? { ...item, name: newName } : item)));
  };

  const handleRemove = (id: string) => {
    if (window.confirm('정말 삭제하시겠습니까?')) {
      setItems(items.filter((item) => item.id !== id));
    }
  };

  // 들여쓰기 (오른쪽 이동 -> 자식 만들기)
  const handleIndent = (id: string) => {
    const index = items.findIndex((item) => item.id === id);
    if (index === 0) {
      return;
    }

    const prevItem = items[index - 1];

    if (prevItem.level >= 1) {
      alert('더 이상 하위 단계로 이동할 수 없습니다.');
      return;
    }

    setItems((prevItems) => {
      const newItems = [...prevItems];
      newItems[index] = {
        ...newItems[index],
        level: prevItem.level + 1,
        parentId: prevItem.id,
      };
      return newItems;
    });
  };

  // 내어쓰기 (왼쪽 이동 -> 부모로 만들기)
  const handleOutdent = (id: string) => {
    setItems((prevItems) => {
      const newItems = prevItems.map((item) => {
        if (item.id === id) {
          return {
            ...item,
            level: 0,
            parentId: null,
          };
        }
        return item;
      });
      return newItems;
    });
  };

  const handleSave = async () => {
    setIsSaving(true);
    try {
      // 서버에는 순서(index) 정보와 부모 정보를 정리해서 보냄
      const payload = items.map((item, index) => ({
        id: item.id.startsWith('new-') ? null : item.id,
        name: item.name,
        parentId: item.level === 0 ? null : item.parentId,
        order: index,
      }));

      // await updateCategoriesAction(blogSlug, payload);
      alert('카테고리가 성공적으로 저장되었습니다.');
    } catch (error) {
      console.error('Failed to save categories:', error);
      alert('카테고리 저장에 실패했습니다. 다시 시도해주세요.');
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-center justify-between border-b pb-4">
        <Button
          onClick={handleAddCategory}
          variant="outline"
          className="bg-indigo-50 text-indigo-200 hover:bg-indigo-100"
        >
          <Plus size={16} className="mr-2" />
        </Button>
        <div className="flex gap-2">
          <Button variant="ghost" onClick={() => window.location.reload()}>
            <RotateCcw size={16} className="mr-2" />
          </Button>
          <Button
            onClick={handleSave}
            disabled={isSaving}
            className="bg-indigo-600 hover:bg-indigo-700"
          >
            <Save size={16} className="mr-2" />
            {isSaving ? '저장 중...' : '변경사항 저장'}
          </Button>
        </div>
      </div>

      <div className="min-h-[300px] rounded bg-gray-50 p-4">
        <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
          <SortableContext
            items={items.map((item) => item.id)}
            strategy={verticalListSortingStrategy}
          >
            {items.length === 0 ? (
              <div className="flex h-32 items-center justify-center text-gray-400">
                카테고리가 없습니다.
              </div>
            ) : (
              items.map((item) => (
                <SortableItem
                  key={item.id}
                  category={item}
                  onRemove={handleRemove}
                  onUpdateName={handleUpdateName}
                  onIndent={handleIndent}
                  onOutdent={handleOutdent}
                />
              ))
            )}
          </SortableContext>
        </DndContext>
      </div>
    </div>
  );
}
