import Image from 'next/image';
import Link from 'next/link';

const posts = [
  {
    id: '1',
    title: 'First Post',
    subtitle: 'An interesting journey begins',
    thumbnail: '/images/default-thumbnail.png',
  },
  {
    id: '2',
    title: 'Second Post',
    subtitle: 'Continuing the adventure',
    thumbnail: '/images/default-thumbnail.png',
  },
];

export default function Home() {
  return (
    <div>
      <h1 className="mb-6 text-3xl font-bold">Blog Posts</h1>
      <div className="space-y-6">
        {posts.map((post) => (
          <Link href={`/posts/${post.id}`} key={post.id} className="block">
            <div className="flex h-48 overflow-hidden rounded-lg bg-white shadow-md">
              <div className="relative w-1/3">
                <Image
                  src={post.thumbnail}
                  alt={`Thumbnail for ${post.title}`}
                  layout="fill"
                  objectFit="cover"
                />
              </div>
              <div className="flex w-2/3 flex-col justify-center p-4">
                <h2 className="mb-2 text-2xl font-semibold">{post.title}</h2>
                <p className="text-gray-600">{post.subtitle}</p>
              </div>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}
