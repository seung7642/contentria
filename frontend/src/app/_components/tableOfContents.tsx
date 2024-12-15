interface TableOfContentsProps {
  headings: { id: string; text: string; level: number }[];
}

const TableOfContents: React.FC<TableOfContentsProps> = ({ headings }) => {
  if (headings.length == 0) {
    return null;
  }

  return (
    <aside className="w-64 overflow-auto border border-gray-200 bg-gray-100 p-4">
      <h2 className="mb-4 text-xl font-bold">목차</h2>
      <ul>
        {headings.map((heading) => (
          <li key={heading.id} className={`ml-$((heading.level - 1) * 4) mb-2`}>
            <a href={`#${heading.id}`} className="text-blue-500 hover:underline">
              {heading.text}
            </a>
          </li>
        ))}
      </ul>
    </aside>
  );
};

export default TableOfContents;
