"use client";

import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { components } from "@/lib/backend/apiV1/schema"; // schema.d.ts 기반 타입
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Form } from "@/components/ui/form";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faLock } from "@fortawesome/free-solid-svg-icons";

const BASE_URL = "http://localhost:8080/api/v1/customer/login"; // 정확한 로그인 API 경로 설정

export default function Page() {
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm<components["schemas"]["LoginReqBody"]>();
  const router = useRouter();

  async function handleLogin(data: components["schemas"]["LoginReqBody"]) {
    try {
      const res = await fetch(BASE_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });

      if (!res.ok) throw new Error("로그인 실패");

      const responseData: components["schemas"]["LoginResBody"] =
        await res.json();

      // 로그인 성공 시, apiKey와 userId를 로컬 스토리지에 저장
      localStorage.setItem("apiKey", responseData.apiKey || "");
      localStorage.setItem("userId", responseData.item?.id?.toString() || "");

      alert("로그인 성공!");
      router.push(`/customer/${responseData.item?.id}`);
    } catch (error) {
      setError("username", {
        type: "manual",
        message: "로그인 실패: 아이디 또는 비밀번호를 확인하세요.",
      });
    }
  }

  return (
    <Card className="w-full max-w-sm shadow-lg rounded-lg">
      <CardHeader className="my-4">
        <CardTitle className="text-center text-2xl font-bold">
          <FontAwesomeIcon icon={faLock} />
        </CardTitle>
      </CardHeader>
      <CardContent>
        <Form onSubmit={handleSubmit(handleLogin)} className="space-y-4">
          <div className="mb-4">
            <Label htmlFor="username">아이디</Label>
            <Input
              id="username"
              type="text"
              placeholder="아이디 입력"
              {...register("username", { required: "아이디를 입력하세요" })}
            />
            {errors.username && (
              <p className="text-red-500 text-sm">{errors.username.message}</p>
            )}
          </div>

          <div className="mb-4">
            <Label htmlFor="password">비밀번호</Label>
            <Input
              id="password"
              type="password"
              placeholder="비밀번호 입력"
              {...register("password", { required: "비밀번호를 입력하세요" })}
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  handleSubmit(handleLogin)();
                }
              }}
            />
            {errors.password && (
              <p className="text-red-500 text-sm">{errors.password.message}</p>
            )}
          </div>
          <Button
            type="submit"
            className="w-full mt-2"
            onClick={handleSubmit(handleLogin)}
          >
            로그인
          </Button>
        </Form>
      </CardContent>
    </Card>
  );
}
