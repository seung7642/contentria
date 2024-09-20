import Link from 'next/link';
import Image from 'next/image';

type Category = {
  id: string;
  name: string;
};

const categories: Category[] = [
  { id: '프로젝트', name: '프로젝트' },
  { id: '생활', name: '생활' },
];

const Sidebar: React.FC = () => {
  return (
    <aside className="m-2 flex w-64 flex-col overflow-auto rounded border border-gray-200 bg-gray-100 p-4">
      <div className="flex-grow">
        <h2 className="mb-4 text-xl font-bold">카테고리</h2>
        <ul>
          {categories.map((category) => (
            <li key={category.id} className="mb-2">
              <Link href={`/category/${category.id}`} className="text-blue-300 hover:underline">
                {category.name}
              </Link>
            </li>
          ))}
        </ul>
      </div>
      <div className="mt-auto border-t border-gray-200 pt-4">
        <div className="flex justify-around">
          <a
            href="https://github.com/yourusername"
            target="_blank"
            rel="noopener noreferrer"
            className="text-gray-600 hover:text-gray-900"
          >
            <Image src="/icons/github.svg" alt="GitHub" width={24} height={24} />
          </a>
          <a href="mailto:your.email@example.com" className="text-gray-600 hover:text-gray-900">
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
