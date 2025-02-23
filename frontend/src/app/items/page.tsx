import { components } from "@/lib/backend/apiV1/schema";

type ItemDto = components["schemas"]["ItemDto"];
type ResponseDtoListItemDto = components["schemas"]["ResponseDtoListItemDto"];

export default async function Page({
  searchParams,
}: {
  searchParams: {
    sortBy?: string;
    searchKeyword?: string;
  };
}) {
  const { sortBy = "", searchKeyword = "" } = searchParams;

  const API_URL =
    process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

  const queryParams = new URLSearchParams();
  if (sortBy) queryParams.append("sortBy", sortBy);
  if (searchKeyword) queryParams.append("searchKeyword", searchKeyword);

  const response = await fetch(
    `${API_URL}/api/v1/items?${queryParams.toString()}`,
    {
      cache: "no-store",
    }
  );

  if (!response.ok) {
    throw new Error("에러");
  }

  const data = await response.json();
  console.log("Fetched Data:", data);

  const items = data.data || [];

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">상품 목록</h1>
      <ul className="space-y-4">
        {items.map((item: any) => (
          <li key={item.id} className="p-4 border rounded-lg shadow">
            <h2 className="text-xl font-semibold">{item.name}</h2>
            <p className="text-gray-600">Price: {item.price}원</p>
            <p className="text-gray-500">Stock: {item.stock}개</p>
          </li>
        ))}
      </ul>
    </div>
  );
}
