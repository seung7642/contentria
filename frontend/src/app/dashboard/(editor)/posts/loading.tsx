import { Skeleton } from '@/components/ui/skeleton';

export default function EditorLoading() {
  return (
    // PostEditorClient와 동일한 최상위 컨테이너 스타일 적용
    <div className="mx-auto flex w-full max-w-6xl flex-1 animate-pulse flex-col p-4 md:p-8">
      {/* 1. 카테고리 선택 영역 (Select 박스) */}
      <div className="mb-4 flex items-center space-x-4">
        {/* w-full md:w-60 크기에 맞춘 스켈레톤 */}
        <Skeleton className="h-[42px] w-full rounded-md md:w-60" />
      </div>

      {/* 2. 제목 입력 영역 (Input) */}
      <div className="mb-4">
        {/* px-4 py-3 text-2xl 높이에 맞춘 대략적인 크기 (h-[60px] 정도) */}
        <Skeleton className="h-[60px] w-full rounded-md" />
      </div>

      {/* 3. 에디터 영역 (Main Editor Area) */}
      <div className="flex flex-1 flex-col rounded-md border bg-white">
        {/* 에디터 툴바 영역 (상단) */}
        <div className="flex items-center gap-2 border-b p-2">
          {/* 툴바 버튼들 흉내내기 */}
          <Skeleton className="h-8 w-8 rounded-md" />
          <Skeleton className="h-8 w-8 rounded-md" />
          <div className="mx-1 h-6 w-px bg-gray-200" /> {/* 구분선 */}
          <Skeleton className="h-8 w-8 rounded-md" />
          <Skeleton className="h-8 w-8 rounded-md" />
          <Skeleton className="h-8 w-8 rounded-md" />
          <div className="mx-1 h-6 w-px bg-gray-200" />
          <Skeleton className="h-8 w-24 rounded-md" /> {/* 좀 긴 버튼 */}
        </div>

        {/* 에디터 본문 영역 */}
        <div className="flex-1 space-y-4 p-4">
          <Skeleton className="h-4 w-3/4" />
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-5/6" />
          <div className="pt-4" />
          <Skeleton className="h-32 w-full rounded-md" /> {/* 이미지나 코드블럭 느낌 */}
        </div>
      </div>

      {/* 4. 하단 버튼 영역 (Footer Buttons) */}
      <div className="mt-6 flex items-center justify-between">
        {/* 나가기 버튼 */}
        <Skeleton className="h-10 w-24 rounded-lg" />

        {/* 저장/출간 버튼 그룹 */}
        <div className="flex items-center justify-end space-x-3">
          <Skeleton className="h-10 w-24 rounded-md" /> {/* 임시저장 */}
          <Skeleton className="h-10 w-24 rounded-md" /> {/* 출간하기 */}
        </div>
      </div>
    </div>
  );
}
