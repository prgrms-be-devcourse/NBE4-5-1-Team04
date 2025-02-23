import { components } from "@/lib/backend/apiV1/schema";

type ItemDto = components["schemas"]["ItemDto"];

export default async function ItemPage({
  params,
}: {
  params: { itemId: string };
}) {
  const API_URL =
    process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

  const response = await fetch(`${API_URL}/api/v1/items/${params.itemId}`, {
    cache: "no-store",
  });

  if (!response.ok) {
    return <div className="p-6 text-red-500">아이템을 찾을 수 없습니다.</div>;
  }

  const data = await response.json();
  const item: ItemDto = data.data;

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">상품 정보</h1>
      <div className="p-4 border rounded-lg shadow">
        <h2 className="text-xl font-semibold">{item.name}</h2>
        <p className="text-gray-600">가격: {item.price}원</p>
        <p className="text-gray-500">재고: {item.stock}개</p>
      </div>
    </div>
  );
}
