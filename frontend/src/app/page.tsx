import PostCard from './_component/postCard';

const posts = [
  {
    id: '1',
    title: '첫 번째 글',
    subTitle: 'An interesting journey begins',
    thumbnail: '/images/default-thumbnail.png',
    createdAt: '2024-01-01T10:10:10Z',
    commentCount: 10,
    likeCount: 3,
  },
  {
    id: '2',
    title: '두 번째 글',
    subTitle: 'Continuing the adventure',
    thumbnail: '/images/default-thumbnail.png',
    createdAt: '2024-01-01T10:10:10Z',
    commentCount: 10,
    likeCount: 3,
  },
];

export default function Home() {
  return (
    <div>
      <h1 className="mb-6 text-3xl font-bold">글 목록</h1>
      <div className="space-y-6">
        {posts.map((post) => (
          <PostCard post={post} />
        ))}
      </div>
    </div>
  );
}
