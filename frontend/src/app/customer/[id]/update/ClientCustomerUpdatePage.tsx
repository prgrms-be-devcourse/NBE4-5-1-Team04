"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { components } from "@/lib/backend/apiV1/schema"; // 백엔드 스키마(타입) 임포트

const BASE_URL = "http://localhost:8080/api/v1/customer";

export default function ClientCustomerUpdatePage({
  userId,
}: {
  userId: string;
}) {
  const router = useRouter();

  const [customer, setCustomer] = useState<
    components["schemas"]["CustomerDto"] | null
  >(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchCustomer() {
      const apiKey = localStorage.getItem("apiKey");
      if (!apiKey) {
        alert("로그인 정보가 없습니다.");
        router.push("/login");
        return;
      }

      try {
        const res = await fetch(`${BASE_URL}/${userId}`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${apiKey}`,
          },
        });

        if (!res.ok) {
          throw new Error("사용자 정보를 불러오는 데 실패했습니다.");
        }

        // response: ResponseDto<CustomerDto>
        const data: components["schemas"]["ResponseDtoCustomerDto"] =
          await res.json();
        if (data && data.data) {
          setCustomer(data.data);
        } else {
          alert("사용자 정보가 없습니다.");
        }
      } catch (error) {
        console.error("데이터 불러오기 실패", error);
        alert(error);
      } finally {
        setLoading(false);
      }
    }

    fetchCustomer();
  }, [userId, router]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!customer) return;
    setCustomer({ ...customer, [e.target.name]: e.target.value });
  };

  const handleSubmit = async () => {
    if (!customer) return;

    const apiKey = localStorage.getItem("apiKey");
    if (!apiKey) {
      alert("로그인 정보가 없습니다.");
      return;
    }

    try {
      // PUT /api/v1/customer/{id}
      const res = await fetch(`${BASE_URL}/${userId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${apiKey}`,
        },
        body: JSON.stringify(customer), // password 포함해서 전송
      });

      if (!res.ok) {
        // 에러 응답도 확인
        const errorText = await res.text();
        console.error("Response status:", res.status);
        console.error("Response body:", errorText);
        throw new Error("정보 수정에 실패했습니다.");
      }

      alert("정보가 성공적으로 수정되었습니다.");
      router.push(`/customer/${userId}`);
    } catch (error) {
      console.error("정보 수정 실패:", error);
      alert(error);
    }
  };

  if (loading) return <p>정보를 불러오는 중입니다...</p>;
  if (!customer) return <p>사용자 정보를 찾을 수 없습니다.</p>;

  return (
    <div>
      <h1>정보 수정</h1>
      <div>
        <label htmlFor="username">아이디</label>
        <input
          id="username"
          name="username"
          type="text"
          value={customer.username}
          onChange={handleChange}
          disabled
        />
      </div>
      <div>
        <label htmlFor="password">비밀번호</label>
        <input
          id="password"
          name="password"
          type="text"
          value={customer.password}
          onChange={handleChange}
        />
      </div>
      <div>
        <label htmlFor="name">이름</label>
        <input
          id="name"
          name="name"
          type="text"
          value={customer.name}
          onChange={handleChange}
        />
      </div>
      <div>
        <label htmlFor="email">이메일</label>
        <input
          id="email"
          name="email"
          type="email"
          value={customer.email}
          onChange={handleChange}
        />
      </div>
      <button onClick={handleSubmit}>수정하기</button>
    </div>
  );
}
