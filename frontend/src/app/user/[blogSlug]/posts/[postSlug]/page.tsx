import { Metadata } from 'next';
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
import { getPostDetailAction } from '@/actions/post';
import { notFound } from 'next/navigation';
import GithubSlugger from 'github-slugger';
import { toString } from 'mdast-util-to-string';
import AnalyticsTracker from '@/components/analytics/AnalyticsTracker';

interface PostDetailPageProps {
  params: Promise<{
    blogSlug: string;
    postSlug: string;
  }>;
}

export async function generateMetadata({ params }: PostDetailPageProps): Promise<Metadata> {
  const { blogSlug, postSlug } = await params;
  const postData = await getPostDetailAction(blogSlug, postSlug);

  if (!postData) {
    return { title: '게시물을 찾을 수 없습니다.' };
  }

  return {
    title: postData.post.title,
    description: postData.post.metaDescription || '',
  };
}

async function getHeadingsFromMarkdown(markdown: string): Promise<Heading[]> {
  const headings: Heading[] = [];
  const processor = unified().use(remarkParse);
  const tree = processor.parse(markdown);

  const slugger = new GithubSlugger();

  visit(tree, 'heading', (node) => {
    if (node.depth >= 1 && node.depth <= 2) {
      const text = toString(node);
      const id = slugger.slug(text);

      headings.push({
        id,
        text,
        level: node.depth,
      });
    }
  });

  return headings;
}

export default async function PostDetailPage({ params }: PostDetailPageProps) {
  const { blogSlug, postSlug } = await params;
  const postDetailResponse = await getPostDetailAction(blogSlug, postSlug);

  if (!postDetailResponse) {
    console.log('Post not found, invoking notFound()');
    notFound();
  }

  const { post, author, blogId } = postDetailResponse;
  const { contentMarkdown } = post;
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

  const headings = await getHeadingsFromMarkdown(contentMarkdown);
  console.log('Extracted headings:', headings);

  return (
    <>
      <AnalyticsTracker blogId={blogId} postId={post.id} />

      <div className="relative mx-auto flex w-full max-w-7xl justify-center">
        <div className="w-full max-w-4xl">
          <div className="mb-8">
            <h1 className="mb-4 text-4xl font-bold text-gray-900">{post.title}</h1>
            <div className="flex items-center space-x-3 text-sm text-gray-500">
              <span className="font-semibold text-gray-800">{author.username}</span>
              <span>•</span>
              <span>{new Date(post.publishedAt).toLocaleDateString('ko-KR')}</span>
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
              {contentMarkdown}
            </Markdown>
          </article>
        </div>
        <div className="ml-8 hidden w-64 flex-shrink-0 lg:block">
          <TableOfContents headings={headings} />
        </div>
      </div>
    </>
  );
}
