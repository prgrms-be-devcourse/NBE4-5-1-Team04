"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { components } from "@/lib/backend/apiV1/schema"; // schema.d.ts 기반 타입
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Form } from "@/components/ui/form";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUser } from "@fortawesome/free-solid-svg-icons";

const BASE_URL = "http://localhost:8080/api/v1/customer"; // API 경로

export default function Page() {
  const [form, setForm] = useState<components["schemas"]["JoinReqBody"]>({
    username: "",
    password: "",
    name: "",
    email: "",
  });

  const router = useRouter();

  async function handleRegister(event: React.FormEvent) {
    event.preventDefault();
    console.log(form);

    try {
      const res = await fetch(BASE_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });

      console.log(res);

      if (!res.ok) throw new Error("회원가입 실패");

      alert("회원가입 성공!");
      location.href = "/customer/login";
    } catch (error) {
      alert("회원가입 실패: " + error);
    }
  }

  return (
    <Card className="w-full max-w-sm shadow-lg rounded-lg">
      <CardHeader className="my-4">
        <CardTitle className="text-center text-2xl font-bold">
          <FontAwesomeIcon icon={faUser} />
        </CardTitle>
      </CardHeader>
      <CardContent className="p-6">
        <Form>
          <form onSubmit={handleRegister} className="space-y-4">
            <div>
              <Label htmlFor="username">아이디</Label>
              <Input
                id="username"
                type="text"
                placeholder="아이디 입력"
                value={form.username}
                onChange={(e) => setForm({ ...form, username: e.target.value })}
                required
              />
            </div>
            <div>
              <Label htmlFor="password">비밀번호</Label>
              <Input
                id="password"
                type="password"
                placeholder="비밀번호 입력"
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                required
              />
            </div>
            <div>
              <Label htmlFor="name">이름</Label>
              <Input
                id="name"
                type="text"
                placeholder="이름 입력"
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
                required
              />
            </div>
            <div>
              <Label htmlFor="email">이메일</Label>
              <Input
                id="email"
                type="email"
                placeholder="이메일 입력"
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
                required
              />
            </div>
            <Button type="submit" className="w-full mt-2">
              가입하기
            </Button>
          </form>
        </Form>
      </CardContent>
    </Card>
  );
}
