"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { components } from "@/lib/backend/apiV1/schema"; // schema.d.ts 기반 타입
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";

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

  return (
    <Card className="w-full max-w-2xl shadow-2xl rounded-xl bg-white">
      <CardHeader className="bg-gray-100 rounded-t-xl py-6">
        <CardTitle className="text-center text-3xl font-bold text-gray-800">
          고객 정보
        </CardTitle>
      </CardHeader>
      <CardContent className="p-8">
        {loading ? (
          <Skeleton className="h-32 w-full rounded-lg" />
        ) : customer ? (
          <div className="space-y-8">
            <p className="text-2xl font-semibold text-gray-900">
              {customer.name}님의 정보
            </p>
            <div className="bg-gray-100 p-6 rounded-lg">
              <p className="text-gray-700 font-medium text-lg">
                아이디: <span className="font-normal">{customer.username}</span>
              </p>
              <p className="text-gray-700 font-medium text-lg">
                이메일: <span className="font-normal">{customer.email}</span>
              </p>
            </div>
            <Button asChild className="py-3 font-semibold">
              <Link href={`/customer/${userId}/update`}>정보 수정</Link>
            </Button>
          </div>
        ) : (
          <p className="text-red-500 text-center text-xl font-medium">
            고객 정보를 찾을 수 없습니다.
          </p>
        )}
      </CardContent>
    </Card>
  );
}
