import Link from 'next/link';

export default function Footer() {
  return (
    <footer className="mt-auto border-t border-gray-200 bg-white py-6">
      <div className="container mx-auto max-w-6xl px-6">
        <div className="flex flex-col items-center gap-4">
          <div className="flex items-center space-x-6">
            <Link
              href="/policy?tab=privacy"
              className="text-sm text-gray-500 hover:text-indigo-600"
            >
              개인정보처리방침
            </Link>
            <Link href="/policy?tab=terms" className="text-sm text-gray-500 hover:text-indigo-600">
              서비스 이용약관
            </Link>
          </div>
          <div className="flex flex-col items-center gap-2 sm:flex-row sm:gap-6">
            <p className="text-sm text-gray-500">
              © {new Date().getFullYear()} Blog Service. All rights reserved.
            </p>
            <a
              href="mailto:contact@example.com"
              className="text-sm text-gray-500 hover:text-indigo-600"
            >
              seung7642@gmail.com
            </a>
          </div>
        </div>
      </div>
    </footer>
  );
}
