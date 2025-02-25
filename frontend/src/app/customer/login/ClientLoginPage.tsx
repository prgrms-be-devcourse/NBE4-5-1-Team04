"use client"; // <-- Add this at the top of your file

import { useState } from "react";
import { useRouter } from "next/navigation";
import { components } from "@/lib/backend/apiV1/schema"; // schema.d.ts 기반 타입
import "../../../styles/login.css"; // CSS 파일 import

const BASE_URL = "http://localhost:8080/api/v1/customer/login"; // 정확한 로그인 API 경로 설정

export default function ClientLoginPage() {
  const [form, setForm] = useState<components["schemas"]["LoginReqBody"]>({
    username: "",
    password: "",
  });

  const router = useRouter();

  async function handleLogin(event: React.FormEvent) {
    event.preventDefault();

    try {
      const res = await fetch(BASE_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });

      if (!res.ok) throw new Error("로그인 실패");

      const data: components["schemas"]["LoginResBody"] = await res.json();

      // 로그인 성공 시, apiKey와 userId를 로컬 스토리지에 저장
      localStorage.setItem("apiKey", data.apiKey || "");
      localStorage.setItem("userId", data.item?.id?.toString() || "");

      alert("로그인 성공!");
      router.push(`/customer/${data.item?.id}`);
    } catch (error) {
      alert("로그인 실패: " + error);
    }
  }

  return (
    <div>
      <h2>로그인</h2>
      <form onSubmit={handleLogin}>
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
        <button type="submit">로그인</button>
      </form>
    </div>
  );
}
