"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { components } from "@/lib/backend/apiV1/schema"; // 백엔드 스키마(타입) 임포트

const BASE_URL = "http://localhost:8080/api/v1/customer"; // 실제 API 경로

interface Props {
  userId: string;
}

export default function ClientCustomerUpdatePage({ userId }: Props) {
  const router = useRouter();

  // 백엔드로부터 받아올 CustomerDto 타입
  const [customer, setCustomer] = useState<
    components["schemas"]["CustomerDto"] | null
  >(null);

  const [loading, setLoading] = useState(true);

  /** 1) 마운트 시 사용자 정보 불러오기 */
  useEffect(() => {
    async function fetchCustomer() {
      const apiKey = localStorage.getItem("apiKey");
      if (!apiKey) {
        alert("로그인 정보가 없습니다.");
        router.push("/login"); // 로그인 페이지로 보낼 수도 있음
        return;
      }

      try {
        const res = await fetch(`${BASE_URL}/${userId}`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${apiKey}`, // 필요한 헤더
          },
        });

        if (!res.ok) {
          throw new Error("사용자 정보를 불러오는 데 실패했습니다.");
        }

        // ResponseDto<CustomerDto> 구조
        const data: components["schemas"]["ResponseDtoCustomerDto"] =
          await res.json();

        // data.data가 존재할 경우만
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

  /** 2) input onChange 핸들러 */
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!customer) return;

    // name, email만 수정 가능하도록 (username, password는 수정 불가라고 가정)
    setCustomer({
      ...customer,
      [e.target.name]: e.target.value,
    });
  };

  /** 3) 수정하기 버튼 클릭 시 PUT 요청 */
  const handleSubmit = async () => {
    if (!customer) return;

    const apiKey = localStorage.getItem("apiKey");
    if (!apiKey) {
      alert("로그인 정보가 없습니다.");
      return;
    }

    try {
      const res = await fetch(`${BASE_URL}/${userId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${apiKey}`, // 필요한 헤더
        },
        body: JSON.stringify(customer),
      });

      if (!res.ok) {
        throw new Error("정보 수정에 실패했습니다.");
      }

      const data: components["schemas"]["ResponseDtoCustomerDto"] =
        await res.json();

      if (data && data.data) {
        alert("정보가 성공적으로 수정되었습니다.");
        // 성공 시 다시 /customer/[id] 페이지로 이동
        router.push(`/customer/${userId}`);
      }
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
      {/* username(아이디)는 수정 불가로 두고 disabled 처리 */}
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
      {/* 비밀번호는 수정 폼에 없음 (또는 별도 구현) */}
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
      <br />
      <button onClick={handleSubmit}>수정하기</button>
    </div>
  );
}
