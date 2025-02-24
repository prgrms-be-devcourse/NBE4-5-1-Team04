"use client";

import { useState, useEffect } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
} from "@/components/ui/table";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBars, faSearch } from "@fortawesome/free-solid-svg-icons";
import Link from "next/link";

type ItemDto = {
  id: number;
  name: string;
  price: number;
  stock: number;
};

export default function ItemListPage() {
  const searchParams = useSearchParams();
  const router = useRouter();

  const [items, setItems] = useState<ItemDto[]>([]);
  const [searchValue, setSearchValue] = useState(
    searchParams.get("searchKeyword") || ""
  );
  const [sortBy, setSortBy] = useState(searchParams.get("sortBy") || "name"); // 기본값 "name"

  useEffect(() => {
    const fetchItems = async () => {
      const API_URL =
        process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
      const queryParams = new URLSearchParams();
      if (sortBy) queryParams.append("sortBy", sortBy);
      if (searchValue) queryParams.append("searchKeyword", searchValue);

      try {
        const response = await fetch(
          `${API_URL}/api/v1/items?${queryParams.toString()}`,
          {
            cache: "no-store",
          }
        );

        if (!response.ok) {
          throw new Error(
            `API 요청 실패: ${response.status} ${response.statusText}`
          );
        }

        const data = await response.json();
        setItems(data.data || []);
      } catch (error) {
        console.error("데이터 가져오기 실패:", error);
      }
    };

    fetchItems();
  }, [sortBy, searchValue]);

  // 검색 실행 함수
  const handleSearch = () => {
    router.push(`?searchKeyword=${searchValue}&sortBy=${sortBy}`);
  };

  // 정렬 방식 변경
  const handleSortChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const newSortBy = event.target.value;
    setSortBy(newSortBy);
    router.push(`?searchKeyword=${searchValue}&sortBy=${newSortBy}`);
  };

  return (
    <Card className="p-6 shadow-xl rounded-3xl w-full">
      {/* 헤더 */}
      <div className="grid grid-cols-2 items-center mb-4">
        <h1 className="pl-2 text-2xl font-bold text-gray-800 flex items-center">
          <FontAwesomeIcon icon={faBars} className="pr-3" />
          상품 목록
        </h1>
        {/* 검색 바 & 정렬 선택 드롭다운 */}
        <div className="flex justify-end gap-2">
          <select
            className="p-2 border rounded-lg"
            value={sortBy}
            onChange={handleSortChange}
          >
            <option value="name">이름순</option>
            <option value="price">가격순</option>
          </select>
          <Input
            type="text"
            placeholder="검색어를 입력하세요"
            className="p-2 border rounded-lg w-full max-w-md"
            value={searchValue}
            onChange={(e) => setSearchValue(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") handleSearch();
            }}
          />
          <Button onClick={handleSearch}>
            <FontAwesomeIcon icon={faSearch} className="mr-2" />
            검색
          </Button>
        </div>
      </div>

      {/* 상품 목록 테이블 */}
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
            items.map((item) => (
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
                  {item.price.toLocaleString()}원
                </TableCell>
                <TableCell className="text-center p-4 text-gray-800">
                  {item.stock}개
                </TableCell>
              </TableRow>
            ))
          ) : (
            <TableRow>
              <TableCell colSpan={3} className="text-center py-6 text-gray-500">
                데이터가 없습니다.
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </Card>
  );
}
