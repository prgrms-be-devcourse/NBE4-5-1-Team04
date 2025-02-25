"use client";

import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import Link from "next/link";
import { useEffect, useState } from "react";

import { config } from "@fortawesome/fontawesome-svg-core";
import "@fortawesome/fontawesome-svg-core/styles.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faMugHot } from "@fortawesome/free-solid-svg-icons";

config.autoAddCss = false;

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

// export const metadata: Metadata = {
//   title: "Coffee Platform",
//   description: "Your go-to coffee marketplace.",
// };

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const [user, setUser] = useState<{
    apiKey: string | null;
    userId: string | null;
    userName: string | null;
  } | null>(null);

  useEffect(() => {
    const updateUserData = () => {
      const apiKey = localStorage.getItem("apiKey");
      const userId = localStorage.getItem("userId");
      const userName = localStorage.getItem("userName");
      if (apiKey && userId) {
        setUser({ apiKey, userId, userName });
      } else {
        setUser(null);
      }
    };

    updateUserData();
    window.addEventListener("storage", updateUserData);
    return () => window.removeEventListener("storage", updateUserData);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("apiKey");
    localStorage.removeItem("userId");
    localStorage.removeItem("userName");
    setUser(null);

    alert("로그아웃 되었습니다.");
    location.href = "/items";
  };

  return (
    <html lang="en">
      <body className="min-h-[100dvh] flex flex-col">
        <header className="flex justify-between items-center px-8 py-4 border-b shadow-md">
          <Link href="/items" className="flex items-center gap-2">
            <FontAwesomeIcon icon={faMugHot} />
            <span className="text-lg font-semibold">Coffee Platform</span>
          </Link>
          <div className="flex gap-4">
            {user ? (
              <>
                <span className="text-sm font-semibold">
                  {user.userId || "사용자"} 님 환영합니다!
                </span>
                <Link
                  href={`/customer/${user.userId}`}
                  className="text-sm text-gray-700 hover:underline"
                >
                  마이페이지
                </Link>
                <button
                  onClick={handleLogout}
                  className="text-sm text-red-500 hover:underline"
                >
                  로그아웃
                </button>
              </>
            ) : (
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
        <div className="p-6 flex flex-col items-center flex-1">{children}</div>
        <footer className="text-xs text-gray-500 text-center p-4">
          <p>DevCourse First Project © 2025 Team04. All rights reserved.</p>
          <p>
            Beyond the paused time, beginner backend developers running once
            again.
          </p>
        </footer>
      </body>
    </html>
  );
}
