"use client";

import { useState, useEffect } from "react";
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
import { faBars, faSearch } from "@fortawesome/free-solid-svg-icons";
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

  useEffect(() => {
    const fetchOrders = async () => {
      const API_URL =
        process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
      const API_KEY =
        process.env.NEXT_PUBLIC_API_KEY || localStorage.getItem("apiKey");

      if (!API_KEY) {
        console.error("API Key가 없습니다. 로그인 페이지로 이동합니다.");
        router.push("/login");
        return;
      }

      const storedUser = localStorage.getItem("user");
      const customerId = storedUser ? JSON.parse(storedUser).id : null;
      if (!customerId) return;

      const queryParams = new URLSearchParams();
      queryParams.append("cust_id", customerId.toString());
      if (sortBy) queryParams.append("sortBy", sortBy);
      if (searchValue) queryParams.append("searchKeyword", searchValue);

      try {
        const response = await fetch(
          `${API_URL}/api/v1/orders?${queryParams.toString()}`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${API_KEY}`,
            },
            cache: "no-store",
          }
        );

        if (!response.ok) {
          throw new Error(
            `API 요청 실패: ${response.status} ${response.statusText}`
          );
        }

        const data = await response.json();
        if (!data?.data || !Array.isArray(data.data)) {
          throw new Error("Invalid API response structure");
        }

        setOrders(data.data as OrderDto[]);
      } catch (error) {
        console.error("데이터 가져오기 실패:", error);
      }
    };

    fetchOrders();
  }, [sortBy, searchValue]);

  const handleSearch = () => {
    router.push(`?searchKeyword=${searchValue}&sortBy=${sortBy}`);
  };

  return (
    <Card className="p-6 shadow-xl rounded-3xl w-full">
      <div className="grid grid-cols-2 items-center mb-4">
        <h1 className="pl-2 text-2xl font-bold text-gray-800 flex items-center">
          <FontAwesomeIcon icon={faBars} className="pr-3" />
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
              if (e.key === "Enter") handleSearch();
            }}
          />
          <Button onClick={handleSearch}>
            <FontAwesomeIcon icon={faSearch} className="mr-2" />
            검색
          </Button>
        </div>
      </div>

      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>주문 ID</TableHead>
            <TableHead>주문 날짜</TableHead>
            <TableHead>총 금액</TableHead>
            <TableHead>배송 상태</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {orders.length > 0 ? (
            orders.map((order) => (
              <TableRow key={order.id}>
                <TableCell>
                  <Link href={`/orders/${order.id}`}>#{order.id}</Link>
                </TableCell>
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
