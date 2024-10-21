import { MDXRemote } from 'next-mdx-remote/rsc';
import fs from 'fs';
import path from 'path';
import rehypeHighlight from 'rehype-highlight';

export default async function Post({ params }: { params: { id: string } }) {
  const { id } = params;
  const postContent = await getPostContent(id);

  return (
    <div>
      <div>
        <MDXRemote
          source={postContent}
          options={{
            mdxOptions: {
              rehypePlugins: [rehypeHighlight],
            },
          }}
        />
      </div>
    </div>
  );
}

async function getPostContent(id: string): Promise<string> {
  const filePath = path.join(process.cwd(), 'src', 'posts', `${id}.mdx`);
  const fileContent = await fs.promises.readFile(filePath, 'utf8');
  return fileContent;
}
