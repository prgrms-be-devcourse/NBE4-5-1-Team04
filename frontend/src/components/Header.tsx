"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";

/**
 * 로그인 여부(localStorage.userId 존재 여부)에 따라
 * - 미로그인: "로그인 / 회원가입"
 * - 로그인됨: "내 정보 / 로그아웃"
 * 을 보여주는 헤더
 */
export default function Header() {
  const router = useRouter();
  const [userId, setUserId] = useState<string | null>(null);

  useEffect(() => {
    // 컴포넌트 마운트 시 localStorage에서 userId, apiKey 등을 읽어 로그인 상태 체크
    const storedUserId = localStorage.getItem("userId");
    if (storedUserId) {
      setUserId(storedUserId);
    }
  }, []);

  // 로그아웃 버튼 클릭
  function handleLogout() {
    localStorage.removeItem("userId");
    localStorage.removeItem("apiKey");
    alert("로그아웃 되었습니다.");
    router.push("/");
  }

  return (
    <header className="flex justify-between items-center px-8 py-4 border-b shadow-md">
      {/* 왼쪽 로고/타이틀 */}
      <Link href="/" className="flex items-center gap-2">
        <span className="text-lg font-semibold">Coffee Platform</span>
      </Link>

      {/* 오른쪽 메뉴 */}
      <div className="flex gap-4">
        {userId ? (
          /** 로그인 된 상태 */
          <>
            <Link
              href={`/customer/${userId}`}
              className="text-sm text-gray-700 hover:underline"
            >
              내 정보
            </Link>
            <button
              onClick={handleLogout}
              className="text-sm text-gray-700 hover:underline"
            >
              로그아웃
            </button>
          </>
        ) : (
          /** 미로그인 상태 */
          <>
            <Link
              href="/customer/login"
              className="text-sm text-gray-700 hover:underline"
            >
              로그인
            </Link>
            <Link
              href="/customer/signup"
              className="text-sm text-gray-700 hover:underline"
            >
              회원가입
            </Link>
          </>
        )}
      </div>
    </header>
  );
}
