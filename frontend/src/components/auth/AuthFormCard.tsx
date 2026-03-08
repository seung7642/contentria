import Image from 'next/image';
import { Card, CardContent } from '../ui/card';

interface AuthFormCardProps {
  title: string;
  children: React.ReactNode;
}

export default function AuthFormCard({ title, children }: AuthFormCardProps) {
  return (
    <div className="flex w-full flex-col items-center">
      <div className="mb-6 flex justify-center">
        <Image
          src="/contentria-icon.png" // public 폴더의 이미지 경로
          alt="Contentria Logo"
          width={500} // 원본 이미지의 너비
          height={500} // 원본 이미지의 높이
          className="h-24 w-24 object-contain" // object-contain으로 이미지 비율 왜곡 방지
          priority // 페이지에서 중요한 이미지이므로 먼저 로드하도록 설정
        />
      </div>

      <h2 className="mb-8 text-center text-3xl font-extrabold text-gray-900">{title}</h2>

      <Card className="w-full border-transparent bg-transparent shadow-none sm:border-gray-200 sm:bg-white sm:shadow-md">
        <CardContent className="p-0 sm:p-8">{children}</CardContent>
      </Card>
    </div>

    // <>
    //   <div className="flex justify-center">
    //     <Image
    //       src="/contentria-icon.png" // public 폴더의 이미지 경로
    //       alt="Contentria Logo"
    //       width={500} // 원본 이미지의 너비
    //       height={500} // 원본 이미지의 높이
    //       className="h-24 w-24" // 화면에 보여질 크기 (Tailwind 클래스). 원하는 크기로 조절하세요!
    //       priority // 페이지에서 중요한 이미지이므로 먼저 로드하도록 설정
    //     />
    //   </div>
    //   <h2 className="text-center text-3xl font-extrabold text-gray-900">{title}</h2>
    //   <div className="relative mt-8 rounded-lg bg-white p-8 shadow-md">{children}</div>
    // </>
  );
}
