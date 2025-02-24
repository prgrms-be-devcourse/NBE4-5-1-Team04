import { components } from "@/lib/backend/apiV1/schema";
import Link from "next/link";
import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
} from "@/components/ui/table";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBars } from "@fortawesome/free-solid-svg-icons";

// API 데이터 타입 지정
type ItemDto = components["schemas"]["ItemDto"];

type Props = {
  searchParams: {
    sortBy?: string;
    searchKeyword?: string;
  };
};

export default async function Page({ searchParams }: Props) {
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
  const items = data.data || [];

  return (
    <Card className="card">
      <div className="grid grid-cols-2 items-center mb-4">
        <h1 className="pl-2 text-2xl font-bold text-gray-800 flex items-center">
          <FontAwesomeIcon icon={faBars} className="pr-3" />
          상품 목록
        </h1>
        <div className="flex justify-end gap-2">
          <Input
            type="text"
            placeholder="검색어를 입력하세요"
            className="p-2 border rounded-lg w-full max-w-md"
          />
          <Button>검색</Button>
        </div>
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
              재고
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {items.length > 0 ? (
            items.map((item: any) => (
              <TableRow
                key={item.id}
                className="border-b hover:bg-gray-50 transition cursor-pointer"
              >
                <TableCell className="text-left p-4 text-gray-800">
                  <Link
                    href={`/items/${item.id}`}
                    className="block w-full h-full"
                  >
                    {item.name}
                  </Link>
                </TableCell>
                <TableCell className="text-center p-4 text-gray-800">
                  <Link
                    href={`/items/${item.id}`}
                    className="block w-full h-full"
                  >
                    {item.price}원
                  </Link>
                </TableCell>
                <TableCell className="text-center p-4 text-gray-800">
                  <Link
                    href={`/items/${item.id}`}
                    className="block w-full h-full"
                  >
                    {item.stock}개
                  </Link>
                </TableCell>
              </TableRow>
            ))
          ) : (
            <TableRow>
              <TableCell colSpan={3} className="text-center py-6 text-gray-500">
                🛑 데이터가 없습니다.
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </Card>
  );
}
