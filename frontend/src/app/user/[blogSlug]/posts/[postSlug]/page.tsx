import { getPostDetail } from '@/services/postService';
import { Metadata } from 'next';
import { notFound } from 'next/navigation';
import Markdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import rehypeSlug from 'rehype-slug';
import rehypeAutolinkHeadings from 'rehype-autolink-headings';
import rehypeShiftHeading from 'rehype-shift-heading';
import SyntaxHighlighter from 'react-syntax-highlighter';
import { dracula } from 'react-syntax-highlighter/dist/esm/styles/hljs';
import React from 'react';
import { Heading } from '@/types/common';
import { unified } from 'unified';
import remarkParse from 'remark-parse';
import { visit } from 'unist-util-visit';
import TableOfContents from '@/components/blog/TableOfContents';

interface PostDetailPageProps {
  params: {
    blogSlug: string;
    postSlug: string;
  };
}

export async function generateMetadata({ params }: PostDetailPageProps): Promise<Metadata> {
  const { blogSlug, postSlug } = params;
  const postData = await getPostDetail(blogSlug, postSlug);

  if (!postData) {
    return { title: '게시물을 찾을 수 없습니다.' };
  }

  return {
    title: postData.post.title,
    description: postData.post.metaDescription || '',
  };
}

const getHeadingsFromMarkdown = async (markdown: string): Promise<Heading[]> => {
  const headings: Heading[] = [];
  const processor = unified().use(remarkParse);
  const tree = processor.parse(markdown);

  visit(tree, 'heading', (node) => {
    const textNode = node.children[0];
    if (node.depth >= 1 && node.depth <= 2 && textNode && 'value' in textNode) {
      // slug는 rehype-slug가 생성하는 방식과 동일하게 만든다.
      const id = textNode.value
        .toLowerCase()
        .replace(/\s+/g, '-')
        .replace(/[^\w-]+/g, '');
      headings.push({
        id,
        text: textNode.value,
        level: node.depth,
      });
    }
  });

  return headings;
};

export default async function PostDetailPage({ params }: PostDetailPageProps) {
  const { blogSlug, postSlug } = params;
  // const postDetailResponse = await getPostDetail(blogSlug, postSlug);

  // if (!postDetailResponse) {
  //   console.log('Post not found, invoking notFound()');
  //   notFound();
  // }

  // const { post } = postDetailResponse;
  const markdown = `
# 이것은 H1 제목입니다

이것은 GFM (GitHub Flavored Markdown) 테이블입니다:

| Feature | Support |
| --- | --- |
| Tables | ✔ |
| Task lists | ✔ |
| Strikethrough | ✔ |

## 이것은 H2 제목입니다 (ID가 생성됩니다)

- [x] 완료된 작업
- [ ] 미완료 작업

~~취소선~~
<script>alert('XSS 공격!');</script>
### 코드 하이라이팅 (H3)

\`rehype-highlight\`가 이 코드 블록을 처리합니다.

\`\`\`javascript
// 언어를 명시했습니다 (javascript)
function greet(name) {
  console.log(\`Hello, \${name}!\`);
}
greet('World');
\`\`\`

\`\`\`
// 언어를 명시하지 않으면 자동 감지합니다.
<div class="test">
  <p>HTML code</p>
</div>
\`\`\`

---

[링크](https://www.google.com)
  `;

  const headings = await getHeadingsFromMarkdown(markdown);

  return (
    <div className="relative mx-auto flex w-full max-w-7xl justify-center">
      <div className="w-full max-w-4xl">
        <div className="mb-8">
          <h1 className="mb-4 text-4xl font-bold text-gray-900">글 제목</h1>
          <div className="flex items-center space-x-3 text-sm text-gray-500">
            <span className="font-semibold text-gray-800">작성자 이름</span>
            <span>•</span>
            <span>{new Date().toLocaleDateString('ko-KR')}</span>
          </div>
        </div>
        <hr className="mb-8" />

        <article className="prose max-w-none flex-shrink-0">
          <Markdown
            remarkPlugins={[remarkGfm]}
            rehypePlugins={[
              rehypeSlug,
              [rehypeAutolinkHeadings, { behavior: 'wrap' }],
              [rehypeShiftHeading, { shift: 1 }],
            ]}
            components={{
              code(props) {
                const { children, className, node, ref, ...rest } = props;
                const match = /language-(\w+)/.exec(className || '');
                return match ? (
                  <SyntaxHighlighter
                    {...rest}
                    PreTag="div"
                    children={String(children).replace(/\n$/, '')}
                    language={match[1]}
                    style={dracula}
                  />
                ) : (
                  <code {...rest} className={className}>
                    {children}
                  </code>
                );
              },
            }}
          >
            {markdown}
          </Markdown>
        </article>
      </div>
      {headings.length > 0 && (
        <div className="ml-8 hidden w-64 flex-shrink-0 lg:block">
          <TableOfContents headings={headings} />
        </div>
      )}
    </div>
  );
}
