import Image from 'next/image';
import Link from 'next/link';

const posts = [
  {
    id: '1',
    title: '첫 번째 글',
    subtitle: 'An interesting journey begins',
    thumbnail: '/images/default-thumbnail.png',
  },
  {
    id: '2',
    title: '두 번째 글',
    subtitle: 'Continuing the adventure',
    thumbnail: '/images/default-thumbnail.png',
  },
  {
    id: '3',
    title: '세 번째 글',
    subtitle: 'An interesting journey begins',
    thumbnail: '/images/default-thumbnail.png',
  },
  {
    id: '4',
    title: '네 번째 글',
    subtitle: 'Continuing the adventure',
    thumbnail: '/images/default-thumbnail.png',
  },
  {
    id: '5',
    title: '다섯 번째 글',
    subtitle: 'An interesting journey begins',
    thumbnail: '/images/default-thumbnail.png',
  },
  {
    id: '6',
    title: '여섯 번째 글',
    subtitle: 'Continuing the adventure',
    thumbnail: '/images/default-thumbnail.png',
  },
];

export default function Home() {
  return (
    <div>
      <h1 className="mb-6 text-3xl font-bold">글 목록</h1>
      <div className="space-y-6">
        {posts.map((post) => (
          <Link href={`/posts/${post.id}`} key={post.id} className="block">
            <div className="flex h-32 overflow-hidden rounded-lg bg-white shadow-md">
              <div className="w-1/6 border-r p-4">
                <Image
                  src={post.thumbnail}
                  alt={`Thumbnail for ${post.title}`}
                  width={100}
                  height={100}
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
