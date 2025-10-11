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

  const initialMarkdown = `
# 머릿말

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
`;

  const handleSave = () => {
    console.log(editorRef.current?.getMarkdown());
    editorRef.current?.setMarkdown('새로운 마크다운 내용');
    console.log(editorRef.current?.getMarkdown());
  };

  return (
    <div className="mx-auto flex max-w-6xl flex-col justify-center p-4 md:p-8">
      <h1 className="mb-6 text-3xl font-bold">새 포스트 작성</h1>
      <div className="justify-center rounded-md border bg-white">
        <ForwardRefEditor
          ref={editorRef}
          markdown={initialMarkdown}
          onChange={console.log}
          className="prose flex-1"
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
