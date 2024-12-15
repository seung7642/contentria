import Link from 'next/link';

const Header = () => {
  return (
    <header className="flex items-center justify-between border-b px-6 py-5">
      <div className="w-24"></div>
      <h1 className="text-2xl font-bold">
        <Link href="/">Blog</Link>
      </h1>
      <div className="text-right">
        <Link
          href="/subscribe"
          className="mx-2 my-2 rounded-lg bg-indigo-500 px-4 py-3 text-sm text-white hover:bg-indigo-600"
        >
          Subscribe
        </Link>
        <Link
          href="/login"
          className="my-2 rounded-lg bg-white px-4 py-3 text-sm text-gray-800 hover:bg-gray-200"
        >
          Sign in
        </Link>
      </div>
    </header>
  );
};

export default Header;
