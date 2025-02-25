"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { components } from "@/lib/backend/apiV1/schema";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";

const BASE_URL = "http://localhost:8080/api/v1/customer";

interface Props {
  userId: string;
}

export default function ClientCustomerUpdatePage({ userId }: Props) {
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
          headers: { Authorization: `Bearer ${apiKey}` },
        });

        if (!res.ok) {
          throw new Error("사용자 정보를 불러오는 데 실패했습니다.");
        }

        const data: components["schemas"]["ResponseDtoCustomerDto"] =
          await res.json();

        if (data?.data) setCustomer(data.data);
        else alert("사용자 정보가 없습니다.");
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
      const res = await fetch(`${BASE_URL}/${userId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${apiKey}`,
        },
        body: JSON.stringify(customer),
      });

      if (!res.ok) throw new Error("정보 수정에 실패했습니다.");

      alert("정보가 성공적으로 수정되었습니다.");
      router.push(`/customer/${userId}`);
    } catch (error) {
      console.error("정보 수정 실패:", error);
      alert(error);
    }
  };

  if (loading)
    return <p className="text-center text-gray-500">정보를 불러오는 중...</p>;
  if (!customer)
    return (
      <p className="text-center text-red-500">
        사용자 정보를 찾을 수 없습니다.
      </p>
    );

  return (
    <Card className="w-full max-w-sm shadow-2xl rounded-xl bg-white">
      <CardHeader>
        <CardTitle className="text-xl font-bold text-center">
          정보 수정
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          <div>
            <Label htmlFor="username">아이디</Label>
            <Input
              id="username"
              name="username"
              value={customer.username}
              disabled
            />
          </div>
          <div>
            <Label htmlFor="password">비밀번호</Label>
            <Input
              id="password"
              name="password"
              type="password"
              value={customer.password}
              onChange={handleChange}
            />
          </div>
          <div>
            <Label htmlFor="name">이름</Label>
            <Input
              id="name"
              name="name"
              value={customer.name}
              onChange={handleChange}
            />
          </div>
          <div>
            <Label htmlFor="email">이메일</Label>
            <Input
              id="email"
              name="email"
              type="email"
              value={customer.email}
              onChange={handleChange}
            />
          </div>
          <Separator className="my-4" />
          <Button className="w-full" onClick={handleSubmit}>
            수정하기
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}
