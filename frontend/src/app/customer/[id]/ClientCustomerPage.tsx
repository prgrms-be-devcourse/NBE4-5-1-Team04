"use client";

import { useState, useEffect } from "react";
import { components } from "@/lib/backend/apiV1/schema"; // schema.d.ts 기반 타입

const BASE_URL = "http://localhost:8080/api/v1/customer"; // API 주소

export default function ClientCustomerPage({ userId }: { userId: string }) {
  const [customer, setCustomer] = useState<
    components["schemas"]["CustomerDto"] | null
  >(null);
  const [loading, setLoading] = useState(true); // 로딩 상태

  useEffect(() => {
    async function fetchCustomer() {
      const apiKey = localStorage.getItem("apiKey");
      if (!apiKey) {
        alert("로그인 정보가 없습니다.");
        return;
      }

      try {
        const res = await fetch(`${BASE_URL}/${userId}`, {
          method: "GET",
          headers: { Authorization: `Bearer ${apiKey}` },
        });

        if (!res.ok) throw new Error("사용자 정보를 불러오는 데 실패했습니다.");

        const data: components["schemas"]["ResponseDtoCustomerDto"] =
          await res.json();

        // data.data가 존재할 경우에만 setCustomer를 호출
        if (data.data) {
          setCustomer(data.data); // 사용자 정보 업데이트
        } else {
          alert("사용자 정보를 불러오는 데 실패했습니다.");
        }
      } catch (error) {
        console.error("고객 정보를 불러오는 중 오류 발생:", error);
        alert(error);
      } finally {
        setLoading(false); // 데이터 로딩 완료
      }
    }

    fetchCustomer();
  }, [userId]);

  if (loading) return <p>고객 정보를 불러오는 중...</p>;

  if (!customer) return <p>고객 정보를 찾을 수 없습니다.</p>;

  return (
    <div>
      <h1>{customer.name}님의 정보</h1>
      <p>아이디: {customer.username}</p>
      <p>이메일: {customer.email}</p>
    </div>
  );
}
