import Link from 'next/link';

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
    <aside className="m-2 w-64 overflow-auto rounded border border-gray-200 bg-gray-100 p-4">
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
    </aside>
  );
};

export default Sidebar;
