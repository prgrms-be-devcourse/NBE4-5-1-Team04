"use client";

import { useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Loader2 } from "lucide-react";

const API_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

type OrderItemDto = {
  itemId: number;
  name: string;
  price: number;
  quantity: number;
};

type OrderWithOrderItemsDto = {
  id: number;
  date: string;
  totalPrice: number;
  deliveryStatus: string;
  orderedItems: OrderItemDto[];
};

type ItemData = {
  id: number;
  name: string;
  price: number;
};

export default function CartPage() {
  const [cartOrder, setCartOrder] = useState<OrderWithOrderItemsDto | null>(
    null
  );
  const [loading, setLoading] = useState(true);
  const [isConfirming, setIsConfirming] = useState(false);

  useEffect(() => {
    fetchCartOrder();
  }, []);

  // 🛒 장바구니 주문 불러오기
  const fetchCartOrder = async () => {
    setLoading(true);
    try {
      const API_KEY = localStorage.getItem("apiKey");

      // 1️⃣ 최신 orderId 가져오기
      const response = await fetch(
        `${API_URL}/api/v1/orders?status=TEMPORARY`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            ...(API_KEY && { Authorization: `Bearer ${API_KEY}` }),
          },
        }
      );

      if (!response.ok) throw new Error("장바구니 정보를 불러오지 못했습니다.");

      const data = await response.json();
      const orders: OrderWithOrderItemsDto[] = data.data.content;

      if (orders.length === 0) {
        setCartOrder(null);
        setLoading(false);
        return;
      }

      // 🔹 최신 주문 찾기 (가장 높은 id)
      const latestOrder = orders.reduce(
        (prev, curr) => (prev.id > curr.id ? prev : curr),
        orders[0]
      );

      // 2️⃣ 최신 주문 상세 정보 가져오기
      const orderResponse = await fetch(
        `${API_URL}/api/v1/orders/${latestOrder.id}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            ...(API_KEY && { Authorization: `Bearer ${API_KEY}` }),
          },
        }
      );

      if (!orderResponse.ok)
        throw new Error("주문 상세 정보를 불러오지 못했습니다.");

      const orderData = await orderResponse.json();
      const cartData: OrderWithOrderItemsDto = orderData.data;

      // 3️⃣ 한 번의 호출로 모든 상품 정보 가져오기
      const itemsResponse = await fetch(`${API_URL}/api/v1/items`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          ...(API_KEY && { Authorization: `Bearer ${API_KEY}` }),
        },
      });

      if (!itemsResponse.ok)
        throw new Error("상품 정보를 불러오지 못했습니다.");

      const itemsData = await itemsResponse.json();
      const allItems: ItemData[] = itemsData.data.content; // 상품 목록

      // 4️⃣ 주문 항목과 상품 정보 매칭하여 업데이트
      const updatedOrderItems = cartData.orderedItems.map((orderItem) => {
        const itemInfo = allItems.find((item) => item.id === orderItem.itemId);
        return {
          ...orderItem,
          name: itemInfo ? itemInfo.name : "상품 정보 없음",
          price: itemInfo ? itemInfo.price : 0,
        };
      });

      setCartOrder({ ...cartData, orderedItems: updatedOrderItems });
    } catch (error) {
      console.error("장바구니 불러오기 실패:", error);
    } finally {
      setLoading(false);
    }
  };

  // 🛒 주문 확정 요청
  const confirmOrder = async () => {
    if (!cartOrder) return;
    setIsConfirming(true);

    try {
      const API_KEY = localStorage.getItem("apiKey");

      const response = await fetch(
        `${API_URL}/api/v1/orders/${cartOrder.id}/confirm`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            ...(API_KEY && { Authorization: `Bearer ${API_KEY}` }),
          },
        }
      );

      if (!response.ok) throw new Error("주문을 확정하는 데 실패했습니다.");

      alert("✅ 주문이 완료되었습니다!");
      location.href = "/orders/list"; // 주문 내역 페이지로 이동
    } catch (error) {
      console.error("주문 확정 오류:", error);
      alert("❌ 주문 확정에 실패했습니다.");
    } finally {
      setIsConfirming(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <Loader2 className="animate-spin w-6 h-6 text-gray-500" />
      </div>
    );
  }

  if (!cartOrder) {
    return (
      <p className="text-center text-gray-500">장바구니가 비어 있습니다.</p>
    );
  }

  const totalAmount = cartOrder.orderedItems.reduce(
    (sum, item) => sum + (item.price ?? 0) * item.quantity,
    0
  );

  return (
    <Card className="card">
      <CardHeader>
        <CardTitle className="text-xl font-bold">장바구니</CardTitle>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>상품명</TableHead>
              <TableHead>가격</TableHead>
              <TableHead>수량</TableHead>
              <TableHead>합계</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {cartOrder.orderedItems.map((item) => (
              <TableRow key={item.itemId}>
                <TableCell>{item.name}</TableCell>
                <TableCell>{item.price} 원</TableCell>
                <TableCell>{item.quantity}</TableCell>
                <TableCell>{item.price * item.quantity} 원</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>

        <div className="flex justify-between items-center mt-6">
          <span className="text-lg font-semibold">
            총 금액: {totalAmount.toLocaleString()} 원
          </span>
          <Button
            className="bg-black text-white px-4 py-2 rounded-md hover:bg-gray-800 transition"
            onClick={confirmOrder}
            disabled={isConfirming}
          >
            {isConfirming ? "주문 처리 중..." : "주문하기"}
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}
