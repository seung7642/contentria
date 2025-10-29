import { getPostDetail } from '@/services/server/postService';
import { Metadata } from 'next';
import { notFound } from 'next/navigation';
import DOMPurify from 'dompurify';
import Markdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import rehypeSlug from 'rehype-slug';
import rehypeAutolinkHeadings from 'rehype-autolink-headings';
// import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
// import { prism } from 'react-syntax-highlighter/dist/esm/styles/prism';
import SyntaxHighlighter from 'react-syntax-highlighter';
import { github, dracula } from 'react-syntax-highlighter/dist/esm/styles/hljs';
import React from 'react';

interface PostDetailPageProps {
  params: Promise<{
    blogSlug: string;
    postSlug: string;
  }>;
}

// export async function generateMetadata({ params }: PostDetailPageProps): Promise<Metadata> {
//   const { blogSlug, postSlug } = await params;
//   const postData = await getPostDetail(blogSlug, postSlug);

//   if (!postData) {
//     return { title: '게시물을 찾을 수 없습니다.' };
//   }

//   return {
//     title: postData.post.title,
//     description: postData.post.metaDescription || '',
//   };
// }

export default async function PostDetailPage({ params }: PostDetailPageProps) {
  const { blogSlug, postSlug } = await params;
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

  return (
    <article className="prose max-w-none">
      {/* <h1 className="mb-4 text-4xl font-bold">{post.title}</h1> */}

      {/* 작성자 정보 및 발행일 */}
      {/* <div className="mb-8 flex items-center space-x-4 text-gray-500"> */}
      {/* <span>{author.username}</span> */}
      {/* <span>•</span> */}
      {/* <span>{new Date(post.publishedAt).toLocaleDateString()}</span> */}
      {/* </div> */}

      {/* 본문 내용 (HTML 렌더링) */}
      {/* <div
        className="prose max-w-none" // tailwindcss-typography 플러그인 필요
        dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(post.contentHtml) }}
      /> */}
      <Markdown
        remarkPlugins={[remarkGfm]}
        rehypePlugins={[rehypeSlug, [rehypeAutolinkHeadings, { behavior: 'wrap' }]]}
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
  );
}
