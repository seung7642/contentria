'use client';

import React, { useState, useEffect } from 'react';
import { useSearchParams, useRouter, usePathname } from 'next/navigation';

export default function PolicyPage() {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const initialTab = searchParams.get('tab') as 'privacy' | 'terms' | null;
  const [activeTab, setActiveTab] = useState<'privacy' | 'terms'>(
    initialTab === 'terms' ? 'terms' : 'privacy'
  );

  useEffect(() => {
    if (initialTab === 'terms') {
      setActiveTab('terms');
    }
  }, [initialTab]);

  const handleTabClick = (tab: 'privacy' | 'terms') => {
    setActiveTab(tab);
    const params = new URLSearchParams(searchParams.toString());
    params.set('tab', tab);
    router.push(`${pathname}?${params.toString()}`);
  };

  return (
    <div className="flex min-h-screen flex-col">
      <section className="bg-gradient-to-br from-indigo-50 via-white to-white py-12" />

      <div className="container mx-auto max-w-4xl px-6 py-12">
        <div className="mb-8 flex space-x-4 border-b">
          <button
            onClick={() => handleTabClick('privacy')}
            className={`border-b-2 px-4 py-2 text-sm font-semibold ${
              activeTab === 'privacy'
                ? 'border-indigo-600 text-indigo-600'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            개인정보처리방침
          </button>
          <button
            onClick={() => handleTabClick('terms')}
            className={`border-b-2 px-4 py-2 text-sm font-semibold ${
              activeTab === 'terms'
                ? 'border-indigo-600 text-indigo-600'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            서비스 이용약관
          </button>
        </div>

        <div className="prose prose-indigo mx-auto">
          {activeTab === 'privacy' ? (
            <>
              <h1 className="text-3xl font-bold text-gray-900">개인정보처리방침</h1>
              <p className="text-gray-600">최종 수정일: 2024년 3월 20일</p>

              <section className="mt-8">
                <h2 className="text-2xl font-semibold text-gray-900">1. 개인정보의 처리 목적</h2>
                <p className="mt-4 text-gray-700">
                  블로그 서비스는 다음의 목적을 위하여 개인정보를 처리합니다. 처리하고 있는
                  개인정보는 다음의 목적 이외의 용도로는 이용되지 않으며, 이용 목적이 변경되는
                  경우에는 개인정보 보호법 제18조에 따라 별도의 동의를 받는 등 필요한 조치를 이행할
                  예정입니다.
                </p>
                <ul className="mt-4 list-disc pl-6 text-gray-700">
                  <li>
                    회원 가입 및 관리: 회원 가입의사 확인, 회원제 서비스 제공에 따른 본인 식별·인증,
                    회원자격 유지·관리, 서비스 부정이용 방지, 각종 고지·통지, 고충처리 목적으로
                    개인정보를 처리합니다.
                  </li>
                  <li>
                    재화 또는 서비스 제공: 서비스 제공, 콘텐츠 제공, 맞춤 서비스 제공, 본인인증을
                    목적으로 개인정보를 처리합니다.
                  </li>
                  <li>
                    마케팅 및 광고에의 활용: 신규 서비스(제품) 개발 및 맞춤 서비스 제공, 이벤트 및
                    광고성 정보 제공 및 참여기회 제공, 서비스의 유효성 확인, 접속빈도 파악 또는
                    회원의 서비스 이용에 대한 통계 등을 목적으로 개인정보를 처리합니다.
                  </li>
                </ul>
              </section>

              <section className="mt-8">
                <h2 className="text-2xl font-semibold text-gray-900">
                  2. 처리하는 개인정보의 항목
                </h2>
                <p className="mt-4 text-gray-700">
                  블로그 서비스는 다음의 개인정보 항목을 처리하고 있습니다.
                </p>
                <ul className="mt-4 list-disc pl-6 text-gray-700">
                  <li>필수항목: 이메일 주소, 비밀번호</li>
                  <li>선택항목: 프로필 이미지, 닉네임</li>
                </ul>
              </section>

              <section className="mt-8">
                <h2 className="text-2xl font-semibold text-gray-900">
                  3. 개인정보의 처리 및 보유 기간
                </h2>
                <p className="mt-4 text-gray-700">
                  블로그 서비스는 법령에 따른 개인정보 보유·이용기간 또는 정보주체로부터 개인정보를
                  수집 시에 동의받은 개인정보 보유·이용기간 내에서 개인정보를 처리·보유합니다.
                </p>
                <ul className="mt-4 list-disc pl-6 text-gray-700">
                  <li>회원 가입 및 관리: 회원 탈퇴 시까지</li>
                  <li>재화 또는 서비스 제공: 재화·서비스 공급완료 및 요금결제·정산 완료 시까지</li>
                </ul>
              </section>

              <section className="mt-8">
                <h2 className="text-2xl font-semibold text-gray-900">4. 개인정보의 제3자 제공</h2>
                <p className="mt-4 text-gray-700">
                  블로그 서비스는 원칙적으로 정보주체의 개인정보를 제1조에서 명시한 범위 내에서
                  처리하며, 정보주체의 사전 동의 없이는 본래의 목적 범위를 초과하여 처리하거나
                  제3자에게 제공하지 않습니다. 다만, 다음의 경우에는 예외로 합니다.
                </p>
                <ul className="mt-4 list-disc pl-6 text-gray-700">
                  <li>정보주체로부터 별도의 동의를 받은 경우</li>
                  <li>법률에 특별한 규정이 있는 경우</li>
                  <li>
                    정보주체 또는 그 법정대리인이 의사표시를 할 수 없는 상태에 있거나 주소불명
                    등으로 사전 동의를 받을 수 없는 경우로서 명백히 정보주체 또는 제3자의 급박한
                    생명, 신체, 재산의 이익을 위하여 필요하다고 인정되는 경우
                  </li>
                  <li>
                    통계작성 및 학술연구 등의 목적을 위하여 필요한 경우로서 특정 개인을 알아볼 수
                    없는 형태로 개인정보를 제공하는 경우
                  </li>
                </ul>
              </section>

              <section className="mt-8">
                <h2 className="text-2xl font-semibold text-gray-900">5. 개인정보의 파기</h2>
                <p className="mt-4 text-gray-700">
                  블로그 서비스는 개인정보 보유기간의 경과, 처리목적 달성 등 개인정보가 불필요하게
                  되었을 때에는 지체 없이 해당 개인정보를 파기합니다. 파기절차 및 방법은 다음과
                  같습니다.
                </p>
                <ul className="mt-4 list-disc pl-6 text-gray-700">
                  <li>
                    파기절차: 불필요한 개인정보 및 개인정보파일은 개인정보 보호책임자의 책임 하에
                    내부방침 절차에 따라 다음과 같이 처리하고 있습니다.
                  </li>
                  <li>
                    파기방법: 전자적 파일 형태의 정보는 기록을 재생할 수 없는 기술적 방법을
                    사용합니다.
                  </li>
                </ul>
              </section>

              <section className="mt-8">
                <h2 className="text-2xl font-semibold text-gray-900">6. 개인정보 보호책임자</h2>
                <p className="mt-4 text-gray-700">
                  블로그 서비스는 개인정보 처리에 관한 업무를 총괄해서 책임지고, 개인정보 처리와
                  관련한 정보주체의 불만처리 및 피해구제 등을 위하여 아래와 같이 개인정보
                  보호책임자를 지정하고 있습니다.
                </p>
                <ul className="mt-4 list-disc pl-6 text-gray-700">
                  <li>개인정보 보호책임자: 홍길동</li>
                  <li>연락처: privacy@example.com</li>
                </ul>
              </section>

              <section className="mt-8">
                <h2 className="text-2xl font-semibold text-gray-900">7. 개인정보 처리방침 변경</h2>
                <p className="mt-4 text-gray-700">
                  이 개인정보 처리방침은 시행일로부터 적용되며, 법령 및 방침에 따른 변경내용의 추가,
                  삭제 및 정정이 있는 경우에는 변경사항의 시행 7일 전부터 공지사항을 통하여 고지할
                  것입니다.
                </p>
              </section>
            </>
          ) : (
            <>
              <h1 className="text-3xl font-bold text-gray-900">서비스 이용약관</h1>
              <p className="text-gray-600">최종 수정일: 2024년 3월 20일</p>

              <section className="mt-8">
                <h2 className="text-2xl font-semibold text-gray-900">제1조 (목적)</h2>
                <p className="mt-4 text-gray-700">
                  이 약관은 블로그 서비스(이하 "서비스")를 제공하는 회사(이하 "회사")와 이를
                  이용하는 회원(이하 "회원") 간의 권리와 의무, 책임사항 및 기타 필요한 사항을
                  규정함을 목적으로 합니다.
                </p>
              </section>

              <section className="mt-8">
                <h2 className="text-2xl font-semibold text-gray-900">제2조 (정의)</h2>
                <p className="mt-4 text-gray-700">
                  이 약관에서 사용하는 용어의 정의는 다음과 같습니다.
                </p>
                <ul className="mt-4 list-disc pl-6 text-gray-700">
                  <li>"서비스"라 함은 회사가 제공하는 블로그 서비스를 말합니다.</li>
                  <li>
                    "회원"이라 함은 서비스에 접속하여 이 약관에 따라 회사와 이용계약을 체결하고
                    회사가 제공하는 서비스를 이용하는 고객을 말합니다.
                  </li>
                  <li>
                    "아이디(ID)"라 함은 회원의 식별과 서비스 이용을 위하여 회원이 정하고 회사가
                    승인하는 문자와 숫자의 조합을 말합니다.
                  </li>
                  <li>
                    "비밀번호"라 함은 회원이 부여 받은 아이디와 일치되는 회원임을 확인하고
                    비밀보호를 위해 회원 자신이 정한 문자 또는 숫자의 조합을 말합니다.
                  </li>
                </ul>
              </section>

              <section className="mt-8">
                <h2 className="text-2xl font-semibold text-gray-900">제3조 (약관의 게시와 개정)</h2>
                <p className="mt-4 text-gray-700">
                  회사는 이 약관의 내용을 회원이 쉽게 알 수 있도록 서비스 초기 화면에 게시합니다.
                  회사는 "약관의 규제에 관한 법률", "정보통신망 이용촉진 및 정보보호 등에 관한 법률"
                  등 관련법을 위배하지 않는 범위에서 이 약관을 개정할 수 있습니다.
                </p>
              </section>

              <section className="mt-8">
                <h2 className="text-2xl font-semibold text-gray-900">제4조 (이용계약 체결)</h2>
                <p className="mt-4 text-gray-700">
                  이용계약은 회원이 되고자 하는 자(이하 "가입신청자")가 약관의 내용에 대하여 동의를
                  한 다음 회원가입 신청을 하고 회사가 이러한 신청에 대하여 승낙함으로써 체결됩니다.
                </p>
              </section>

              <section className="mt-8">
                <h2 className="text-2xl font-semibold text-gray-900">
                  제5조 (서비스의 제공 및 변경)
                </h2>
                <p className="mt-4 text-gray-700">
                  회사는 회원에게 아래와 같은 서비스를 제공합니다.
                </p>
                <ul className="mt-4 list-disc pl-6 text-gray-700">
                  <li>블로그 서비스</li>
                  <li>콘텐츠 작성 및 관리 서비스</li>
                  <li>
                    기타 회사가 추가 개발하거나 다른 회사와의 제휴계약 등을 통해 회원에게 제공하는
                    일체의 서비스
                  </li>
                </ul>
              </section>

              <section className="mt-8">
                <h2 className="text-2xl font-semibold text-gray-900">제6조 (서비스의 중단)</h2>
                <p className="mt-4 text-gray-700">
                  회사는 컴퓨터 등 정보통신설비의 보수점검, 교체 및 고장, 통신의 두절 등의 사유가
                  발생한 경우에는 서비스의 제공을 일시적으로 중단할 수 있습니다. 이 경우 회사는
                  제9조에 정한 방법으로 회원에게 통지합니다.
                </p>
              </section>

              <section className="mt-8">
                <h2 className="text-2xl font-semibold text-gray-900">제7조 (회원의 의무)</h2>
                <p className="mt-4 text-gray-700">회원은 다음 행위를 하여서는 안 됩니다.</p>
                <ul className="mt-4 list-disc pl-6 text-gray-700">
                  <li>신청 또는 변경 시 허위 내용의 등록</li>
                  <li>타인의 정보 도용</li>
                  <li>회사가 게시한 정보의 변경</li>
                  <li>회사가 정한 정보 이외의 정보(컴퓨터 프로그램 등) 등의 송신 또는 게시</li>
                  <li>회사와 다른 회원의 저작권 등 지적재산권에 대한 침해</li>
                  <li>회사와 다른 회원의 명예를 손상시키거나 업무를 방해하는 행위</li>
                  <li>
                    외설 또는 폭력적인 메시지, 화상, 음성, 기타 공서양속에 반하는 정보를 서비스에
                    공개 또는 게시하는 행위
                  </li>
                  <li>회사의 동의 없이 영리를 목적으로 서비스를 사용하는 행위</li>
                  <li>기타 불법적이거나 부당한 행위</li>
                </ul>
              </section>

              <section className="mt-8">
                <h2 className="text-2xl font-semibold text-gray-900">제8조 (회사의 의무)</h2>
                <p className="mt-4 text-gray-700">
                  회사는 관련법과 이 약관이 금지하거나 미풍양속에 반하는 행위를 하지 않으며,
                  계속적이고 안정적으로 서비스를 제공하기 위하여 최선을 다하여 노력합니다.
                </p>
              </section>

              <section className="mt-8">
                <h2 className="text-2xl font-semibold text-gray-900">제9조 (개인정보보호)</h2>
                <p className="mt-4 text-gray-700">
                  회사는 서비스를 제공하기 위하여 관련 법령의 규정에 따라 회원으로부터 필요한
                  개인정보를 수집합니다. 회사는 수집된 개인정보를 개인정보처리방침에 따라 적절히
                  관리, 보호합니다.
                </p>
              </section>

              <section className="mt-8">
                <h2 className="text-2xl font-semibold text-gray-900">제10조 (책임제한)</h2>
                <p className="mt-4 text-gray-700">
                  회사는 천재지변 또는 이에 준하는 불가항력으로 인하여 서비스를 제공할 수 없는
                  경우에는 서비스 제공에 관한 책임이 면제됩니다. 회사는 회원의 귀책사유로 인한
                  서비스 이용의 장애에 대하여는 책임을 지지 않습니다.
                </p>
              </section>

              <section className="mt-8">
                <h2 className="text-2xl font-semibold text-gray-900">제11조 (분쟁해결)</h2>
                <p className="mt-4 text-gray-700">
                  회사는 회원으로부터 제출되는 불만처리 및 피해구제요청을 신속하게 처리하기 위하여
                  필요한 인력 및 시스템을 구비하고 있습니다. 회사는 회원으로부터 제출되는 불만사항
                  및 의견을 최대한 신속하게 처리합니다. 다만, 신속한 처리가 곤란한 경우에는 회원에게
                  그 사유와 처리일정을 통보합니다.
                </p>
              </section>

              <section className="mt-8">
                <h2 className="text-2xl font-semibold text-gray-900">
                  제12조 (준거법 및 관할법원)
                </h2>
                <p className="mt-4 text-gray-700">
                  이 약관의 해석 및 회사와 회원 간의 분쟁에 대하여는 대한민국의 법을 적용하며, 본
                  분쟁으로 인한 소송은 민사소송법상의 관할을 가지는 대한민국의 법원에 제기합니다.
                </p>
              </section>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
