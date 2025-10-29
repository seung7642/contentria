'use client';

import { useRef, useState } from 'react';
import {
  AdmonitionDirectiveDescriptor,
  BlockTypeSelect,
  BoldItalicUnderlineToggles,
  ChangeAdmonitionType,
  ChangeCodeMirrorLanguage,
  codeBlockPlugin,
  CodeMirrorEditor,
  codeMirrorPlugin,
  CodeToggle,
  ConditionalContents,
  CreateLink,
  diffSourcePlugin,
  DiffSourceToggleWrapper,
  DirectiveNode,
  directivesPlugin,
  EditorInFocus,
  frontmatterPlugin,
  headingsPlugin,
  HighlightToggle,
  InsertAdmonition,
  InsertCodeBlock,
  InsertFrontmatter,
  InsertTable,
  InsertThematicBreak,
  linkDialogPlugin,
  linkPlugin,
  listsPlugin,
  ListsToggle,
  markdownShortcutPlugin,
  MDXEditorMethods,
  quotePlugin,
  Separator,
  ShowSandpackInfo,
  StrikeThroughSupSubToggles,
  tablePlugin,
  thematicBreakPlugin,
  toolbarPlugin,
  UndoRedo,
} from '@mdxeditor/editor';
import { ForwardRefEditor } from '@/components/dashboard/ForwardRefEditor';
import { useAuthStore } from '@/store/authStore';

type AdmonitionKind = 'note' | 'tip' | 'danger' | 'info' | 'caution';

function whenInAdmonition(editorInFocus: EditorInFocus | null) {
  const node = editorInFocus?.rootNode;
  if (!node || node.getType() !== 'directive') {
    return false;
  }

  return ['note', 'tip', 'danger', 'info', 'caution'].includes(
    (node as DirectiveNode).getMdastNode().name as AdmonitionKind
  );
}

const NewPostPage = () => {
  const editorRef = useRef<MDXEditorMethods>(null); // MDXEditor의 인스턴스에 접근하기 위한 ref 생성
  const [title, setTitle] = useState('');
  const user = useAuthStore((state) => state.user);

  const initialMarkdown = `
여기에 **마크다운** 문법을 사용하여 글을 작성할 수 있습니다.

- 리스트 아이템 1
- 리스트 아이템 2
- 리스트 아이템 3

> 인용문 예시입니다.

[MDXEditor 공식 문서](https://mdxeditor.com)

\`\`\`js
// 코드 블록 예시
function helloWorld() {
  console.log("Hello, world!");
}
\`\`\`

### 다음은 테이블 예시입니다.

| 이름  | 나이 | 성별 |
| --- | -- | -- |
| OOO | 20 | 남  |
| OOO | 50 | 여  |


\n\n
해당 내용은 샘플 마크다운 내용입니다. 당신의 이야기로 수정해보세요!
`;

  const handleEditorAreaClick = (e: React.MouseEvent) => {
    const target = e.target as HTMLElement;

    // 툴바 클릭은 무시
    if (target.closest('.mdxeditor-toolbar')) {
      return;
    }

    // contenteditable 내부 클릭은 무시
    if (
      target.getAttribute('contenteditable') === 'true' ||
      target.closest('[contenteditable="true"]')
    ) {
      return;
    }

    editorRef.current?.focus();

    // 약간의 지연 후 커서 위치 조정 (focus가 완료된 후)
    setTimeout(() => {
      const editorContainer = target.closest('.mdxeditor') as HTMLElement;
      const editableEl = editorContainer?.querySelector('[contenteditable="true"]') as HTMLElement;

      if (editableEl) {
        const clickY = e.clientY;

        // 실제 콘텐츠의 마지막 위치 찾기
        const allElements = Array.from(editableEl.querySelectorAll('*'));
        const lastVisibleElement = allElements
          .reverse()
          .find((el) => el.textContent?.trim() && el.getBoundingClientRect().height > 0);

        if (lastVisibleElement) {
          const lastRect = lastVisibleElement.getBoundingClientRect();

          // 콘텐츠 아래 영역 클릭 시 커서를 끝으로
          if (clickY > lastRect.bottom + 10) {
            // 10px 여유
            const range = document.createRange();
            const sel = window.getSelection();
            range.selectNodeContents(editableEl);
            range.collapse(false);
            sel?.removeAllRanges();
            sel?.addRange(range);
          }
        }
      }
    }, 10);
  };

  const handleSave = () => {
    console.log(editorRef.current?.getMarkdown());
    editorRef.current?.setMarkdown('새로운 마크다운 내용');
    console.log(editorRef.current?.getMarkdown());
  };

  return (
    <div className="mx-auto flex w-full max-w-6xl flex-1 flex-col p-4 md:p-8">
      <div className="mb-4">
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="제목을 입력하세요"
          className="w-full rounded-md border border-gray-300 bg-white px-4 py-3 text-2xl font-semibold placeholder-gray-400 transition focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
        />
      </div>
      <div
        className="flex flex-1 cursor-text flex-col rounded-md border bg-white [&_.mdxeditor-toolbar]:cursor-default"
        onClick={handleEditorAreaClick}
      >
        <ForwardRefEditor
          ref={editorRef}
          markdown={initialMarkdown}
          onChange={console.log}
          spellCheck={false}
          className="prose max-w-none flex-1"
          plugins={[
            headingsPlugin({ allowedHeadingLevels: [1, 2, 3] }),
            quotePlugin(),
            listsPlugin(),
            thematicBreakPlugin(),
            linkPlugin(),
            linkDialogPlugin(),
            tablePlugin(),
            markdownShortcutPlugin(),
            // the default code block language to insert when user clicks the "insert code block" button
            codeBlockPlugin({
              defaultCodeBlockLanguage: 'js',
              codeBlockEditorDescriptors: [
                { priority: -10, match: (_) => true, Editor: CodeMirrorEditor },
              ],
            }),
            codeMirrorPlugin({
              codeBlockLanguages: {
                js: 'JavaScript',
                ts: 'TypeScript',
                html: 'HTML',
                css: 'CSS',
                java: 'Java',
                kotlin: 'Kotlin',
                bash: 'Bash',
              },
            }),
            // the viewMode parameter lets you switch the editor to diff or source mode.
            // you can get the diffMarkdown from your backend and pass it here.
            diffSourcePlugin({ diffMarkdown: 'An older version', viewMode: 'rich-text' }),
            toolbarPlugin({
              toolbarContents: () => (
                <DiffSourceToggleWrapper>
                  <ConditionalContents
                    options={[
                      {
                        when: (editor) => editor?.editorType === 'codeblock',
                        contents: () => <ChangeCodeMirrorLanguage />,
                      },
                      {
                        when: (editor) => editor?.editorType === 'sandpack',
                        contents: () => <ShowSandpackInfo />,
                      },
                      {
                        fallback: () => (
                          <>
                            <UndoRedo />
                            <Separator />
                            <BoldItalicUnderlineToggles />
                            <CodeToggle />
                            <HighlightToggle />
                            <Separator />
                            <StrikeThroughSupSubToggles />
                            <Separator />
                            <ListsToggle />
                            <Separator />

                            <ConditionalContents
                              options={[
                                {
                                  when: whenInAdmonition,
                                  contents: () => <ChangeAdmonitionType />,
                                },
                                { fallback: () => <BlockTypeSelect /> },
                              ]}
                            />

                            <Separator />
                            <CreateLink />
                            <Separator />
                            <InsertTable />
                            <InsertThematicBreak />
                            <Separator />
                            <InsertCodeBlock />
                            <ConditionalContents
                              options={[
                                {
                                  when: (editorInFocus) => !whenInAdmonition(editorInFocus),
                                  contents: () => (
                                    <>
                                      <Separator />
                                      <InsertAdmonition />
                                    </>
                                  ),
                                },
                              ]}
                            />
                            <Separator />
                            <InsertFrontmatter />
                          </>
                        ),
                      },
                    ]}
                  />
                </DiffSourceToggleWrapper>
              ),
            }),
            directivesPlugin({ directiveDescriptors: [AdmonitionDirectiveDescriptor] }),
            frontmatterPlugin(),
            markdownShortcutPlugin(),
          ]}
        />
      </div>
      <div className="mt-6 flex justify-end">
        <button
          onClick={handleSave}
          className="rounded-md bg-indigo-500 px-6 py-2 font-semibold text-white transition hover:bg-indigo-700"
        >
          저장하기
        </button>
      </div>
    </div>
  );
};

export default NewPostPage;
