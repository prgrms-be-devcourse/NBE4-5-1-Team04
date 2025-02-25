import { components } from "@/lib/backend/apiV1/schema";
import ClientItemPage from "./ClientItemPage"; // 클라이언트 컴포넌트 불러오기

type ItemDto = components["schemas"]["ItemDto"];

export default async function ItemPage({
  params,
}: {
  params: { itemId: string };
}) {
  const API_URL =
    process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

  try {
    const response = await fetch(`${API_URL}/api/v1/items/${params.itemId}`, {
      cache: "no-store",
    });

    if (!response.ok) {
      throw new Error("Failed to fetch item");
    }

    const data = await response.json();
    const item: ItemDto | null = data?.data;

    if (!item) {
      throw new Error("Invalid data structure");
    }

    // 클라이언트 컴포넌트로 데이터 전달
    return <ClientItemPage item={item} />;
  } catch (error) {
    console.error("Error fetching item:", error);
    return (
      <div className="p-6 text-red-500 text-center">
        아이템을 찾을 수 없습니다.
      </div>
    );
  }
}
