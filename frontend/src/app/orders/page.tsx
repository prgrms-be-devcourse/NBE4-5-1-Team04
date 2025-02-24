"use client";

import { useState, useEffect } from "react";
import { useSearchParams, useRouter } from "next/navigation";
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

type OrderDto = {
  id: number;
  date: string;
  totalPrice: number;
  deliveryStatus: string;
};

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
      const customerId = 1; // ✅ cust_id 추가 (실제 로그인된 사용자 ID로 변경 필요)

      const queryParams = new URLSearchParams();
      queryParams.append("cust_id", customerId.toString()); // ✅ cust_id 추가
      if (sortBy) queryParams.append("sortBy", sortBy);
      if (searchValue) queryParams.append("searchKeyword", searchValue);

      try {
        const response = await fetch(
          `${API_URL}/api/v1/orders?${queryParams.toString()}`,
          {
            cache: "no-store",
          }
        );

        if (!response.ok) {
          const errorText = await response.text();
          console.error(
            `API 요청 실패: ${response.status} ${response.statusText}`,
            errorText
          );
          throw new Error(
            `API 요청 실패: ${response.status} ${response.statusText}\n${errorText}`
          );
        }

        const data = await response.json();
        setOrders(data.data || []);
      } catch (error) {
        console.error("데이터 가져오기 실패:", error);
      }
    };

    fetchOrders();
  }, [sortBy, searchValue]);

  // 검색 실행 함수
  const handleSearch = () => {
    router.push(`?searchKeyword=${searchValue}&sortBy=${sortBy}`);
  };

  // 정렬 방식 변경
  const handleSortChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const newSortBy = event.target.value;
    setSortBy(newSortBy);
    router.push(`?searchKeyword=${searchValue}&sortBy=${newSortBy}`);
  };

  return (
    <Card className="p-6 shadow-xl rounded-3xl w-full">
      {/* 헤더 */}
      <div className="grid grid-cols-2 items-center mb-4">
        <h1 className="pl-2 text-2xl font-bold text-gray-800 flex items-center">
          <FontAwesomeIcon icon={faBars} className="pr-3" />
          주문 목록
        </h1>
        {/* 검색 바 & 정렬 선택 드롭다운 */}
        <div className="flex justify-end gap-2">
          <select
            className="p-2 border rounded-lg"
            value={sortBy}
            onChange={handleSortChange}
          >
            <option value="date">주문 날짜순</option>
            <option value="totalPrice">총 금액순</option>
            <option value="deliveryStatus">배송 상태순</option>
          </select>
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

      {/* 주문 목록 테이블 */}
      <Table className="rounded-xl overflow-hidden border border-gray-500">
        <TableHeader>
          <TableRow className="bg-gray-100 text-gray-700">
            <TableHead className="text-left p-4 font-semibold">
              주문 ID
            </TableHead>
            <TableHead className="text-center p-4 font-semibold">
              주문 날짜
            </TableHead>
            <TableHead className="text-center p-4 font-semibold">
              총 금액
            </TableHead>
            <TableHead className="text-center p-4 font-semibold">
              배송 상태
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {orders.length > 0 ? (
            orders.map((order) => (
              <TableRow
                key={order.id}
                className="border-b hover:bg-gray-50 transition cursor-pointer"
              >
                <TableCell className="text-left p-4 text-gray-800">
                  <Link
                    href={`/orders/${order.id}`}
                    className="block w-full h-full"
                  >
                    #{order.id}
                  </Link>
                </TableCell>
                <TableCell className="text-center p-4 text-gray-800">
                  {moment(order.date).format("YYYY-MM-DD HH:mm:ss")}
                </TableCell>
                <TableCell className="text-center p-4 text-gray-800">
                  {order.totalPrice.toLocaleString()}원
                </TableCell>
                <TableCell className="text-center p-4 text-gray-800">
                  {order.deliveryStatus}
                </TableCell>
              </TableRow>
            ))
          ) : (
            <TableRow>
              <TableCell colSpan={4} className="text-center py-6 text-gray-500">
                주문 내역이 없습니다.
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </Card>
  );
}
