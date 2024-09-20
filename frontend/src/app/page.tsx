import Link from 'next/link';

const posts = [
  { id: '1', title: 'First Post', excerpt: 'This is the first post.' },
  { id: '2', title: 'Second Post', excerpt: 'This is the second post.' },
];

export default function Home() {
  return (
    <div>
      <h1>Blog Posts</h1>
      <ul>
        {posts.map((post) => (
          <li key={post.id} className="mb-6">
            <h2 className="mb-2 text-2xl font-semibold">
              <Link href={`/posts/$(post.id)`} className="text-blue-600 hover:underline">
                {post.title}
              </Link>
            </h2>
            <p className="text-gray-600">{post.excerpt}</p>
          </li>
        ))}
      </ul>
    </div>
  );
}
