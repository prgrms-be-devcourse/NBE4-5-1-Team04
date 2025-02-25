"use client";

import { useState, useEffect } from "react";
import { components } from "@/lib/backend/apiV1/schema";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import Image from "next/image";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCartShopping,
  faMinus,
  faPlus,
} from "@fortawesome/free-solid-svg-icons";

type ItemDto = components["schemas"]["ItemDto"];

export default function ClientItemPage({ item }: { item: ItemDto }) {
  const [orderCount, setOrderCount] = useState(1);
  const [loading, setLoading] = useState(false);
  const [latestOrderId, setLatestOrderId] = useState<number | null>(null);

  // 🛒 개수 증가
  const handleIncrease = () => setOrderCount((prev) => prev + 1);

  // 🛒 개수 감소 (최소 1개)
  const handleDecrease = () =>
    setOrderCount((prev) => (prev > 1 ? prev - 1 : 1));

  // 🛒 수량 직접 입력 핸들러
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = parseInt(e.target.value, 10);
    setOrderCount(isNaN(value) || value < 1 ? 1 : value);
  };

  // 🛒 총 금액 계산
  const totalPrice = (item.price ? item.price : 0) * orderCount;

  const API_URL =
    process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

  useEffect(() => {
    fetchLatestOrderId();
  }, []);

  // 🔹 가장 최신의 TEMPORARY 상태 orderId 가져오기
  const fetchLatestOrderId = async () => {
    try {
      const apiKey = localStorage.getItem("apiKey");
      const response = await fetch(
        `${API_URL}/api/v1/orders?status=TEMPORARY`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            ...(apiKey && { Authorization: `Bearer ${apiKey}` }),
          },
        }
      );

      if (!response.ok)
        throw new Error("최신 장바구니 주문을 가져오지 못했습니다.");

      const data = await response.json();
      const orders = data.data.content;
      if (orders.length === 0) {
        setLatestOrderId(null);
        return;
      }

      // 🔹 가장 최근 orderId 찾기
      const latestOrder = orders.reduce(
        (prev, curr) => (prev.id > curr.id ? prev : curr),
        orders[0]
      );
      setLatestOrderId(latestOrder.id);
    } catch (error) {
      console.error("최신 장바구니 주문 가져오기 실패:", error);
    }
  };

  // 🔹 장바구니 추가 요청
  const addToCart = async () => {
    setLoading(true);
    try {
      const apiKey = localStorage.getItem("apiKey");

      // 🔹 요청 본문 데이터
      const requestBody = {
        itemId: item.id,
        quantity: orderCount,
      };

      const url = latestOrderId
        ? `${API_URL}/api/v1/orders/${latestOrderId}` // 기존 주문에 추가
        : `${API_URL}/api/v1/orders`; // 새 주문 생성

      console.log(requestBody);

      const response = await fetch(url, {
        method: latestOrderId ? "PUT" : "POST",
        headers: {
          "Content-Type": "application/json",
          ...(apiKey && { Authorization: `Bearer ${apiKey}` }),
        },
        body: JSON.stringify(latestOrderId ? requestBody : [requestBody]), // 새 주문 생성 시 배열 필요
      });

      if (!response.ok) throw new Error("장바구니 추가 실패");

      alert("✅ 장바구니에 추가되었습니다!");
    } catch (error) {
      console.error("장바구니 추가 오류:", error);
      alert("❌ 장바구니 추가에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card className="card">
      <div className="flex flex-col md:flex-row">
        <div className="w-full h-auto md:w-1/2 rounded-lg flex overflow-hidden items-center justify-center">
          <Image
            src={item.imageUri || `${API_URL}/api/v1/items/${item.id}/image`}
            alt={item.name || "상품 이미지"}
            width={0}
            height={0}
            sizes="100vw"
            className="w-full h-auto object-contain object-center"
            priority
          />
        </div>

        <div className="w-full md:w-1/2 p-6">
          <CardHeader>
            <CardTitle className="text-2xl font-bold">{item.name}</CardTitle>
            <p className="text-sm text-gray-500">Coffee</p>
          </CardHeader>

          <CardContent className="space-y-4">
            <p className="text-gray-700">
              재고 현황 : {item.stock || "재고 정보 누락"}
            </p>

            <div className="border-t pt-4">
              <h3 className="text-lg font-semibold mb-3">제품 가격 정보</h3>
              <div className="grid grid-cols-2 gap-2 text-gray-600 text-sm mb-5">
                <div>개당 가격</div>
                <div className="text-right">{item.price} 원</div>
              </div>
            </div>

            {/* 🔹 개수 조절 UI */}
            <div className="flex items-center gap-3 border-t pt-4">
              <button
                onClick={handleDecrease}
                className="px-2 py-1 bg-gray-200 rounded-md hover:bg-gray-300 transition"
              >
                <FontAwesomeIcon icon={faMinus} />
              </button>
              <input
                type="number"
                value={orderCount}
                onChange={handleInputChange}
                className="w-16 text-center border rounded-md py-1"
                min="1"
              />
              <button
                onClick={handleIncrease}
                className="px-2 py-1 bg-gray-200 rounded-md hover:bg-gray-300 transition"
              >
                <FontAwesomeIcon icon={faPlus} />
              </button>
            </div>

            {/* 🔹 총 금액 표시 */}
            <div className="bg-gray-100 p-3 rounded-md text-gray-700 text-sm">
              <span className="font-semibold">
                총 금액: {totalPrice.toLocaleString()} 원
              </span>
            </div>

            {/* 장바구니 추가 버튼 */}
            <button
              onClick={addToCart}
              className="w-full mt-4 bg-black text-white py-2 rounded-md hover:bg-gray-800 transition"
              disabled={loading}
            >
              <FontAwesomeIcon icon={faCartShopping} className="pr-2" />
              {loading ? "추가 중..." : "장바구니 추가"}
            </button>
          </CardContent>
        </div>
      </div>
    </Card>
  );
}
