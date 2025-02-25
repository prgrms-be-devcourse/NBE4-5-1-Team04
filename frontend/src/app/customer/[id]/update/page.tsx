"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation"; // useParams로 params 가져오기
import ClientCustomerUpdatePage from "../update/ClientCustomerUpdatePage";

export default function UpdatePage() {
  const [userId, setUserId] = useState<string | null>(null);

  const params = useParams(); // params를 useParams로 불러오기

  useEffect(() => {
    if (params && params.id) {
      // params.id가 배열이 아닐 경우에만 값을 설정
      if (Array.isArray(params.id)) {
        // 배열인 경우 첫 번째 값을 사용
        setUserId(params.id[0]);
      } else {
        setUserId(params.id);
      }
    }
  }, [params]);

  if (!userId) {
    return <p>로딩 중...</p>;
  }

  return (
    <div>
      <h1>회원 정보 수정</h1>
      <ClientCustomerUpdatePage userId={userId} />
    </div>
  );
}
