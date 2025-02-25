// app/post/ClientPostPage.tsx
"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { components } from "@/lib/backend/apiV1/schema"; // schema.d.ts 기반 타입

const BASE_URL = "http://localhost:8080/api/v1/customer"; // API 경로

export default function ClientPostPage() {
  const [form, setForm] = useState<components["schemas"]["JoinReqBody"]>({
    username: "",
    password: "",
    name: "",
    email: "",
  });

  const router = useRouter();

  async function handleRegister(event: React.FormEvent) {
    event.preventDefault();

    try {
      const res = await fetch(BASE_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });

      if (!res.ok) throw new Error("회원가입 실패");

      alert("회원가입 성공!");
      router.push("/customer/login");
    } catch (error) {
      alert("회원가입 실패: " + error);
    }
  }

  return (
    <div>
      <form onSubmit={handleRegister}>
        <input
          type="text"
          placeholder="아이디"
          value={form.username}
          onChange={(e) => setForm({ ...form, username: e.target.value })}
          required
        />
        <input
          type="password"
          placeholder="비밀번호"
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
          required
        />
        <input
          type="text"
          placeholder="이름"
          value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })}
          required
        />
        <input
          type="email"
          placeholder="이메일"
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })}
          required
        />
        <button type="submit">가입하기</button>
      </form>
    </div>
  );
}
