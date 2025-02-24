"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useParams } from "next/navigation";
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

// ✅ Swagger 스키마 기반 타입 정의
type OrderWithOrderItemsDto = components["schemas"]["OrderWithOrderItemsDto"];
type OrderItemDto = components["schemas"]["OrderItemDto"];
type ItemDto = components["schemas"]["ItemDto"];

export default function OrderDetailPage() {
  const params = useParams();
  const router = useRouter();

  const [order, setOrder] = useState<OrderWithOrderItemsDto | null>(null);
  const [items, setItems] = useState<ItemDto[]>([]);
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

      try {
        // ✅ 주문 상세 정보 가져오기
        const orderResponse = await fetch(
          `${API_URL}/api/v1/orders/${params.orderId}`,
          {
            cache: "no-store",
          }
        );

        if (!orderResponse.ok) {
          throw new Error(
            `API 요청 실패: ${orderResponse.status} ${orderResponse.statusText}`
          );
        }

        const orderData = await orderResponse.json();
        if (!orderData?.data) {
          throw new Error("잘못된 응답 구조입니다.");
        }
        setOrder(orderData.data as OrderWithOrderItemsDto);

        // ✅ 주문된 아이템들의 상세 정보 가져오기
        const itemIds =
          orderData.data.orderedItems?.map(
            (item: OrderItemDto) => item.itemId
          ) || [];
        if (itemIds.length > 0) {
          const itemsResponse = await fetch(
            `${API_URL}/api/v1/items?ids=${itemIds.join(",")}`
          );
          if (!itemsResponse.ok) {
            throw new Error("상품 정보를 불러오는 데 실패했습니다.");
          }
          const itemsData = await itemsResponse.json();
          setItems(itemsData.data || []);
        }
      } catch (error) {
        setError("주문 정보를 가져오는 중 오류가 발생했습니다.");
      } finally {
        setLoading(false);
      }
    };

    fetchOrderDetail();
  }, [params.orderId]);

  if (loading)
    return <div className="p-6 text-gray-500 text-center">로딩 중...</div>;
  if (error) return <div className="p-6 text-red-500 text-center">{error}</div>;
  if (!order)
    return (
      <div className="p-6 text-gray-500 text-center">주문 정보가 없습니다.</div>
    );

  return (
    <Card className="max-w-2xl mx-auto p-6 shadow-xl rounded-3xl">
      <CardHeader>
        <CardTitle className="text-2xl font-bold">주문 상세 정보</CardTitle>
      </CardHeader>
      <CardContent className="space-y-6">
        <div className="text-lg text-gray-700 space-y-2">
          <p>
            <strong>주문 ID:</strong> #{order.id}
          </p>
          <p>
            <strong>주문 날짜:</strong>{" "}
            {moment(order.date).format("YYYY-MM-DD HH:mm:ss")}
          </p>
          <p>
            <strong>총 금액:</strong> {order.totalPrice?.toLocaleString()}원
          </p>
          <p>
            <strong>배송 상태:</strong> {order.deliveryStatus ?? "UNKNOWN"}
          </p>
        </div>

        <Table className="rounded-xl overflow-hidden border border-gray-500">
          <TableHeader>
            <TableRow className="bg-gray-100 text-gray-700">
              <TableHead className="text-left p-4 font-semibold">
                상품명
              </TableHead>
              <TableHead className="text-center p-4 font-semibold">
                가격
              </TableHead>
              <TableHead className="text-center p-4 font-semibold">
                수량
              </TableHead>
              <TableHead className="text-center p-4 font-semibold">
                합계
              </TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {order.orderedItems && order.orderedItems.length > 0 ? (
              order.orderedItems.map(
                (orderItem: OrderItemDto, index: number) => {
                  const itemDetail = items.find(
                    (item) => item.id === orderItem.itemId
                  );
                  return (
                    <TableRow
                      key={index}
                      className="border-b hover:bg-gray-50 transition"
                    >
                      <TableCell className="text-left p-4 text-gray-800">
                        {itemDetail?.name ?? "상품 정보 없음"}
                      </TableCell>
                      <TableCell className="text-center p-4 text-gray-800">
                        {itemDetail?.price
                          ? itemDetail.price.toLocaleString() + "원"
                          : "가격 정보 없음"}
                      </TableCell>
                      <TableCell className="text-center p-4 text-gray-800">
                        {orderItem.quantity}개
                      </TableCell>
                      <TableCell className="text-center p-4 text-gray-800">
                        {itemDetail?.price && orderItem.quantity
                          ? (
                              itemDetail.price * orderItem.quantity
                            ).toLocaleString()
                          : "0"}
                        원
                      </TableCell>
                    </TableRow>
                  );
                }
              )
            ) : (
              <TableRow>
                <TableCell
                  colSpan={4}
                  className="text-center py-6 text-gray-500"
                >
                  주문된 상품이 없습니다.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}
