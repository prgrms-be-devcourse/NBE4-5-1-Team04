import { components } from "@/lib/backend/apiV1/schema";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import Image from "next/image";

type ItemDto = components["schemas"]["ItemDto"];

export default async function ItemPage({
  params,
}: {
  params: { itemId: string };
}) {
  const API_URL =
    process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

  let item: ItemDto | null = null;

  try {
    const response = await fetch(`${API_URL}/api/v1/items/${params.itemId}`, {
      cache: "no-store",
    });

    if (!response.ok) {
      throw new Error("Failed to fetch item");
    }

    const data = await response.json();
    item = data?.data;

    if (!item) {
      throw new Error("Invalid data structure");
    }
  } catch (error) {
    console.error("Error fetching item:", error);
    return (
      <div className="p-6 text-red-500 text-center">
        아이템을 찾을 수 없습니다.
      </div>
    );
  }

  return (
    <Card className="p-6 shadow-xl rounded-3xl w-full">
      <CardHeader>
        <CardTitle className="text-2xl font-bold">상품 정보</CardTitle>
      </CardHeader>
      <CardContent className="flex flex-col items-center space-y-4">
        {/* 이미지 영역 */}
        <div className="w-full max-w-xs h-48 bg-gray-200 rounded-lg flex items-center overflow-hidden">
          <Image
            src={item.imageUrl || "/placeholder.png"} // 기본 이미지 설정
            alt={item.name}
            width={300}
            height={200}
            className="object-cover"
            priority
          />
        </div>
        <div className="text-center">
          <h2 className="text-xl font-semibold">{item.name}</h2>
          <p className="text-gray-600">
            가격: {item.price?.toLocaleString()}원
          </p>
          <p className="text-gray-500">재고: {item.stock}개</p>
        </div>
      </CardContent>
    </Card>
  );
}
