"use client";

import React, { useState, useEffect } from "react"; // ✅ React import 추가
import { useSearchParams, useRouter } from "next/navigation";
import { components } from "@/lib/backend/apiV1/schema";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
} from "@/components/ui/table";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTruck, faSearch } from "@fortawesome/free-solid-svg-icons";
import Link from "next/link";
import moment from "moment";

type OrderDto = components["schemas"]["OrderWithOrderItemsDto"];

export default function OrderListPage() {
  const searchParams = useSearchParams();
  const router = useRouter();

  const [orders, setOrders] = useState<OrderDto[]>([]);
  const [searchValue, setSearchValue] = useState(
    searchParams.get("searchKeyword") || ""
  );
  const [sortBy, setSortBy] = useState(searchParams.get("sortBy") || "date");
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const checkAuthentication = () => {
      const API_KEY = localStorage.getItem("apiKey");

      if (!API_KEY) {
        console.error("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
        router.push("/login");
        return;
      }

      setIsAuthenticated(true);
    };

    checkAuthentication();
  }, [router]); // ✅ router 의존성 추가

  useEffect(() => {
    if (!isAuthenticated) return;

    const fetchOrders = async () => {
      const API_URL =
        process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
      const API_KEY = localStorage.getItem("apiKey");

      try {
        const response = await fetch(`${API_URL}/api/v1/orders`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${API_KEY}`,
          },
          cache: "no-store",
        });

        if (!response.ok) {
          throw new Error(
            `API 요청 실패: ${response.status} ${response.statusText}`
          );
        }

        const data = await response.json();
        console.log(data);
        setOrders(data.data.content as OrderDto[]);
      } catch (error) {
        console.error("데이터 가져오기 실패:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, [isAuthenticated, sortBy, searchValue]);

  const handleSearch = (
    event:
      | React.MouseEvent<HTMLButtonElement>
      | React.KeyboardEvent<HTMLInputElement>
  ) => {
    event.preventDefault();
    router.push(`?searchKeyword=${searchValue}&sortBy=${sortBy}`);
  };

  if (!isAuthenticated) {
    return <p className="text-center text-lg">로그인 확인 중...</p>;
  }

  if (loading) {
    return <p className="text-center text-lg">로딩 중...</p>;
  }

  return (
    <Card className="p-6 shadow-xl rounded-3xl w-full">
      <div className="grid grid-cols-2 items-center mb-4">
        <h1 className="pl-2 text-2xl font-bold text-gray-800 flex items-center">
          <FontAwesomeIcon icon={faTruck} className="pr-3" />
          주문 목록
        </h1>
        <div className="flex justify-end gap-2">
          <Input
            type="text"
            placeholder="검색어를 입력하세요"
            className="p-2 border rounded-lg w-full max-w-md"
            value={searchValue}
            onChange={(e) => setSearchValue(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                e.preventDefault();
                handleSearch(e);
              }
            }}
          />
          <Button onClick={handleSearch}>
            <FontAwesomeIcon icon={faSearch} className="mr-2" />
            검색
          </Button>
        </div>
      </div>

      <Table className="rounded-xl overflow-hidden">
        <TableHeader>
          <TableRow className="bg-gray-100 text-gray-700">
            <TableHead>주문 ID</TableHead>
            <TableHead>주문 날짜</TableHead>
            <TableHead>총 금액</TableHead>
            <TableHead>배송 상태</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {orders.length > 0 ? (
            orders.map((order) => (
              <TableRow
                key={order.id}
                onClick={() => router.push(`/orders/${order.id}`)}
              >
                <TableCell>#{order.id}</TableCell>
                <TableCell>
                  {moment(order.date).format("YYYY-MM-DD HH:mm:ss")}
                </TableCell>
                <TableCell>{order.totalPrice?.toLocaleString()}원</TableCell>
                <TableCell>{order.deliveryStatus ?? "UNKNOWN"}</TableCell>
              </TableRow>
            ))
          ) : (
            <TableRow>
              <TableCell colSpan={4} className="text-center">
                주문 내역이 없습니다.
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </Card>
  );
}
