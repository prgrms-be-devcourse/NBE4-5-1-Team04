"use client";

import { useEffect, useState } from "react";
import { useRouter, useParams } from "next/navigation";
import { components } from "@/lib/backend/apiV1/schema";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
} from "@/components/ui/table";
import moment from "moment";

type OrderWithOrderItemsDto = components["schemas"]["OrderWithOrderItemsDto"];
type OrderItemDto = components["schemas"]["OrderItemDto"];

type ItemData = {
  id: number;
  name: string;
  price: number;
};

export default function OrderDetailPage() {
  const params = useParams();
  const router = useRouter();

  const [order, setOrder] = useState<OrderWithOrderItemsDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [itemDetails, setItemDetails] = useState<Record<number, ItemData>>({});

  useEffect(() => {
    fetchOrderDetail();
  }, [params.orderId]);

  // 🔹 주문 상세 정보 가져오기 + 상품 정보 매핑
  const fetchOrderDetail = async () => {
    if (!params.orderId) {
      setError("주문 ID가 없습니다.");
      setLoading(false);
      return;
    }

    const API_URL =
      process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
    const API_KEY =
      process.env.NEXT_PUBLIC_API_KEY || localStorage.getItem("apiKey");

    if (!API_KEY) {
      console.warn("API Key가 없습니다. 로그인 페이지로 이동합니다.");
      router.push("/login");
      return;
    }

    try {
      // 1️⃣ 주문 상세 정보 가져오기
      const response = await fetch(
        `${API_URL}/api/v1/orders/${params.orderId}`,
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
      const orderData: OrderWithOrderItemsDto = data.data;
      setOrder(orderData);

      // 2️⃣ 주문 내 모든 itemId 목록 가져오기
      const itemIds = orderData.orderedItems.map((item) => item.itemId);

      // 3️⃣ 상품 정보 한 번의 API 호출로 가져오기
      const itemsResponse = await fetch(`${API_URL}/api/v1/items`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${API_KEY}`,
        },
      });

      if (!itemsResponse.ok) {
        throw new Error("상품 정보를 불러오지 못했습니다.");
      }

      const itemsData = await itemsResponse.json();
      const allItems: ItemData[] = itemsData.data.content;

      // 4️⃣ itemId 기준으로 매핑
      const mappedItems: Record<number, ItemData> = {};
      allItems.forEach((item) => {
        mappedItems[item.id] = item;
      });

      setItemDetails(mappedItems);
    } catch (error) {
      setError("주문 정보를 가져오는 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card className="w-full max-w-2xl shadow-2xl rounded-xl bg-white">
      <CardHeader className="bg-gray-100 rounded-t-xl py-6">
        <CardTitle className="text-center text-3xl font-bold text-gray-800">
          주문 상세 정보
        </CardTitle>
      </CardHeader>
      <CardContent className="p-8">
        {loading ? (
          <p>로딩 중...</p>
        ) : error ? (
          <p>{error}</p>
        ) : (
          <div>
            <p className="mb-3">주문 ID: #{order?.id}</p>
            <p className="mb-3">
              주문 날짜: {moment(order?.date).format("YYYY-MM-DD HH:mm:ss")}
            </p>
            <p className="mb-3">
              총 금액: {order?.totalPrice?.toLocaleString()}원
            </p>

            {/* 🔹 주문 항목 목록 (Table) */}
            <Table className="mt-6">
              <TableHeader>
                <TableRow>
                  <TableHead>상품명</TableHead>
                  <TableHead>가격</TableHead>
                  <TableHead>수량</TableHead>
                  <TableHead>합계</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {order?.orderedItems?.map((item: OrderItemDto) => {
                  const itemInfo = itemDetails[item.itemId];
                  return (
                    <TableRow key={item.itemId}>
                      <TableCell>
                        {itemInfo ? itemInfo.name : "상품 정보 없음"}
                      </TableCell>
                      <TableCell>
                        {itemInfo
                          ? `${itemInfo.price.toLocaleString()} 원`
                          : "-"}
                      </TableCell>
                      <TableCell>{item.quantity}</TableCell>
                      <TableCell>
                        {itemInfo
                          ? `${(
                              itemInfo.price * item.quantity
                            ).toLocaleString()} 원`
                          : "-"}
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
