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

export default function OrderDetailPage() {
  const params = useParams();
  const router = useRouter();

  const [order, setOrder] = useState<OrderWithOrderItemsDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
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
        setOrder(data.data as OrderWithOrderItemsDto);
      } catch (error) {
        setError("주문 정보를 가져오는 중 오류가 발생했습니다.");
      } finally {
        setLoading(false);
      }
    };

    fetchOrderDetail();
  }, [params.orderId]);

  return (
    <Card>
      <CardHeader>
        <CardTitle>주문 상세 정보</CardTitle>
      </CardHeader>
      <CardContent>
        {loading ? (
          <p>로딩 중...</p>
        ) : error ? (
          <p>{error}</p>
        ) : (
          <>
            <p>주문 ID: #{order?.id}</p>
            <p>
              주문 날짜: {moment(order?.date).format("YYYY-MM-DD HH:mm:ss")}
            </p>
            <p>총 금액: {order?.totalPrice?.toLocaleString()}원</p>
          </>
        )}
      </CardContent>
    </Card>
  );
}
