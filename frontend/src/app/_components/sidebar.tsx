import Image from 'next/image';
import CategoryItem from './categoryItem';

export type Category = {
  id: string;
  name: string;
  children?: Category[];
};

const categories: Category[] = [
  {
    id: '프로젝트',
    name: '프로젝트',
    children: [
      {
        id: 'web',
        name: '웹 개발',
        children: [
          { id: 'frontend', name: '프론트엔드' },
          { id: 'backend', name: '백엔드' },
        ],
      },
      {
        id: 'mobile',
        name: '모바일 앱',
      },
    ],
  },
  {
    id: '생활',
    name: '생활',
    children: [
      { id: 'daily', name: '일상' },
      { id: 'travel', name: '여행' },
    ],
  },
];

const Sidebar: React.FC = () => {
  return (
    <aside className="m-2 flex w-80 flex-col overflow-auto rounded border border-gray-200 bg-gray-100 p-4">
      {/* 프로필 박스 */}
      <div className="mb-6 rounded-lg bg-white p-4 shadow">
        <div className="flex flex-col items-center">
          <div className="mb-4 flex h-56 w-56 items-center justify-center rounded-lg bg-gray-300">
            <Image
              src={`/images/default-profile.png`}
              alt="profile"
              width={250}
              height={250}
              className="rounded-lg"
            />
          </div>
          <h2 className="mb-2 text-lg">이승호</h2>
          <p className="text-center text-sm text-gray-600">반갑습니다.</p>
        </div>
      </div>

      {/* 카테고리 박스 */}
      <div className="mb-6 rounded-lg bg-white p-4 shadow">
        <h2 className="mb-4 text-center text-xl font-bold">글 분류</h2>
        <ul>
          {categories.map((category) => (
            // <li key={category.id} className="mb-2">
            //   <Link href={`/category/${category.id}`} className="text-blue-300 hover:underline">
            //     {category.name}
            //   </Link>
            // </li>
            <CategoryItem key={category.id} category={category} />
          ))}
        </ul>
      </div>

      {/* 아이콘 박스 */}
      <div className="mt-auto border-t border-gray-200 pt-4">
        <div className="flex justify-around">
          <a
            href="https://github.com/seung7642"
            target="_blank"
            rel="noopener noreferrer"
            className="text-gray-600 hover:text-gray-900"
          >
            <Image src="/icons/github.svg" alt="GitHub" width={24} height={24} />
          </a>
          <a href="mailto:seung7642@gmail.com" className="text-gray-600 hover:text-gray-900">
            <Image src="/icons/gmail.svg" alt="Email" width={24} height={24} />
          </a>
          <a
            href="/rss.xml"
            target="_blank"
            rel="noopener noreferrer"
            className="text-gray-600 hover:text-gray-900"
          >
            <Image src="/icons/rss.svg" alt="RSS Feed" width={24} height={24} />
          </a>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
