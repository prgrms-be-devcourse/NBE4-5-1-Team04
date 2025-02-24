"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

interface Customer {
  id: number;
  username: string;
  name: string;
  email: string;
  apiKey: string;
}

export default function CustomerDetail({ params }: { params: { id: string } }) {
  const [customer, setCustomer] = useState<Customer | null>(null);
  const [error, setError] = useState<string | null>(null);
  const router = useRouter();
  const { id } = params;

  useEffect(() => {
    if (!id) return;

    const fetchCustomer = async () => {
      try {
        const response = await fetch(`/api/v1/customer/${id}`);
        if (!response.ok) {
          throw new Error("고객 정보를 불러오는 데 실패했습니다.");
        }
        const data = await response.json();
        setCustomer(data.data); // 백엔드의 ResponseDto 구조에 따라 `.data` 접근
      } catch (err) {
        setError((err as Error).message);
      }
    };

    fetchCustomer();
  }, [id]);

  if (error) {
    return <p className="text-red-500">{error}</p>;
  }

  if (!customer) {
    return <p>고객 정보를 불러오는 중...</p>;
  }

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold">고객 정보</h1>
      <p>
        <strong>ID:</strong> {customer.id}
      </p>
      <p>
        <strong>이름:</strong> {customer.name}
      </p>
      <p>
        <strong>이메일:</strong> {customer.email}
      </p>
      <p>
        <strong>API Key:</strong> {customer.apiKey}
      </p>

      <button
        className="mt-4 p-2 bg-blue-500 text-white rounded"
        onClick={() => router.push("/customer")}
      >
        뒤로 가기
      </button>
    </div>
  );
}
