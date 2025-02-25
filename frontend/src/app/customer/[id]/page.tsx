// src/app/customer/[id]/page.tsx
import Link from "next/link";
import ClientCustomerPage from "./ClientCustomerPage";
import ClientCustomerUpdatePage from "./update/ClientCustomerUpdatePage";

export default function Page({ params }: { params: { id: string } }) {
  return (
    <div>
      <ClientCustomerPage userId={params.id} />
      {/* 수정 페이지로 이동하는 링크 추가 */}
      <Link href={`/customer/${params.id}/update`}>정보 수정</Link>
    </div>
  );
}
