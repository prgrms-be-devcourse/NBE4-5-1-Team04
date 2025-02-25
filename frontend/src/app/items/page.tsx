"use client";

import { useState, useEffect } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
} from "@/components/ui/table";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationPrevious,
  PaginationNext,
  PaginationLink,
} from "@/components/ui/pagination";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSearch, faBars } from "@fortawesome/free-solid-svg-icons";
import Link from "next/link";

type ItemDto = {
  id: number;
  name: string;
  price: number;
  stock: number;
  imageUri: string;
};

export default function ItemListPage() {
  const searchParams = useSearchParams();
  const router = useRouter();

  const [items, setItems] = useState<ItemDto[]>([]);
  const [searchValue, setSearchValue] = useState(
    searchParams.get("searchKeyword") || ""
  );
  const [sortBy, setSortBy] = useState(searchParams.get("sortBy") || "name");
  const [page, setPage] = useState(Number(searchParams.get("page")) || 0);
  const [totalPages, setTotalPages] = useState(1);
  const pageSize = 5; // 페이지당 표시할 개수

  useEffect(() => {
    const fetchItems = async () => {
      const API_URL =
        process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

      const queryParams = new URLSearchParams();
      queryParams.append("page", page.toString());
      queryParams.append("size", pageSize.toString());
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

        const result = await response.json();
        setItems(result.data?.content || []);
        setTotalPages(result.data?.totalPages || 1);
      } catch (error) {
        console.error("데이터 가져오기 실패:", error);
      }
    };

    fetchItems();
  }, [sortBy, searchValue, page]);

  // 🔹 검색 실행 함수
  const handleSearch = () => {
    setPage(0); // 검색 시 첫 페이지로 이동
    router.push(`?searchKeyword=${searchValue}&sortBy=${sortBy}&page=0`);
  };

  // 🔹 정렬 변경 함수
  const handleSortChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const newSortBy = event.target.value;
    setSortBy(newSortBy);
    setPage(0); // 정렬 변경 시 첫 페이지로 이동
    router.push(`?searchKeyword=${searchValue}&sortBy=${newSortBy}&page=0`);
  };

  // 🔹 페이지 변경 함수
  const handlePageChange = (newPage: number) => {
    if (newPage >= 0 && newPage < totalPages) {
      setPage(newPage);
      router.push(
        `?searchKeyword=${searchValue}&sortBy=${sortBy}&page=${newPage}`
      );
    }
  };

  return (
    <Card className="p-6 shadow-xl rounded-3xl w-full">
      {/* 헤더 */}
      <div className="grid grid-cols-2 items-center mb-4">
        <h1 className="text-2xl font-bold text-gray-800">
          <FontAwesomeIcon icon={faBars} className="pr-3" />
          상품 목록
        </h1>
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
            <FontAwesomeIcon icon={faSearch} className="mr-1" />
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

      {/* Pagination */}
      <Pagination className="mt-6">
        <PaginationContent>
          <PaginationItem>
            <PaginationPrevious
              onClick={() => handlePageChange(page - 1)}
              // disabled={page === 0}
            />
          </PaginationItem>

          {Array.from({ length: totalPages }).map((_, index) => (
            <PaginationItem key={index}>
              <PaginationLink
                isActive={page === index}
                onClick={() => handlePageChange(index)}
              >
                {index + 1}
              </PaginationLink>
            </PaginationItem>
          ))}

          <PaginationItem>
            <PaginationNext
              onClick={() => handlePageChange(page + 1)}
              // disabled={page >= totalPages - 1}
            />
          </PaginationItem>
        </PaginationContent>
      </Pagination>
    </Card>
  );
}
