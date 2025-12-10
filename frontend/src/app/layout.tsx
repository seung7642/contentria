import type { Metadata } from 'next';
import localFont from 'next/font/local';
import './globals.css';
import ReactQueryProvider from '@/components/ReactQueryProvider';
import { getUserProfileAction } from '@/actions/user';
import { dehydrate, HydrationBoundary, QueryClient } from '@tanstack/react-query';
import { userKeys } from '@/hooks/queries/keys';

const pretendard = localFont({
  src: '../../public/fonts/PretendardVariable.woff2',
  display: 'swap',
  weight: '45 920',
  variable: '--font-pretendard',
});

export const metadata: Metadata = {
  title: {
    template: '%s | Contentria',
    default: 'Contentria',
  },
  description: 'Contentria is a blog platform',
};

export default async function RootLayout({ children }: { children: React.ReactNode }) {
  const queryClient = new QueryClient();
  await queryClient.prefetchQuery({
    queryKey: userKeys.profile(),
    queryFn: () => getUserProfileAction(false),
  });

  const dehydratedState = dehydrate(queryClient);

  return (
    <html lang="ko">
      <body className={pretendard.className}>
        <ReactQueryProvider>
          <HydrationBoundary state={dehydratedState}>{children}</HydrationBoundary>
        </ReactQueryProvider>
      </body>
    </html>
  );
}
