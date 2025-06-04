import Link from 'next/link';

export default function Footer() {
  return (
    <footer className="mt-auto border-t border-gray-200 bg-white py-6">
      <div className="mx-auto flex justify-between px-12 py-2">
        <div className="flex flex-col">
          <p className="mb-4 text-sm text-gray-500">
            © {new Date().getFullYear()} Blog Service. All rights reserved.
          </p>
          <p className="text-sm text-gray-500">
            이메일: <span className="hover:text-indigo-600">help@contentria.com</span>
          </p>
        </div>

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
        </div>
      </div>
    </footer>
  );
}
