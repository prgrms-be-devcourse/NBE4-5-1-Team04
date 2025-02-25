package com.team4.project1.domain.item.controller;

import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.service.ItemService;
import com.team4.project1.global.dto.ResponseDto;
import com.team4.project1.global.exception.ItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

/**
 * 상품 관련 API를 처리하는 컨트롤러 클래스입니다.
 * 상품의 조회, 등록, 수정, 삭제와 관련된 기능을 제공합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ApiV1ItemController {

    private final ItemService itemService;

    /**
     * 상품 ID를 통해 상품 정보를 조회하는 API엔드포인트입니다.
     *
     * @param itemId 조회할 상품의 ID
     * @return {@Link ItemDto} 객체를 포함한 응답을 반환합니다.
     * @throws ItemNotFoundException 상품이 존재하지 않는 경우 예외 발생합니다.
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<ResponseDto<ItemDto>> item(@PathVariable Long itemId) {
        return ResponseEntity.ok(ResponseDto.ok(
                itemService.getItemById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId))
        ));
    }

    /**
     * 상품 목록을 조회, 검색 및 정렬 기능을 제공하는 API 엔드포인트입니다.
     *
     * @param sortBy  정렬기준(가격, 이름)
     * @param keyword 검색 키워드
     * @return 정렬되고, 검색된 상품 목록을 포함한 {@Link List<ItemDto>}객체를 반환합니다.
     */
    @GetMapping
    public ResponseEntity<ResponseDto<Page<ItemDto>>> sortedItems(
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "searchKeyword", required = false) String keyword,
            Pageable pageable
            ) {
        if (keyword == null) { keyword = ""; }
        Page<ItemDto> items = itemService.searchAllItemsSortedBy(sortBy, keyword, pageable);
        return ResponseEntity.ok(ResponseDto.ok(items));
    }

    /**
     * 새로운 상품을 등록하는 API 엔드포인트입니다.
     *
     * @param itemDto 등록할 상품의 정보
     * @return 등록된 상품을 담고 있는 {@Link ItemDto} 객체를 포함한 응답을 반환합니다.
     */
    @PostMapping
    public ResponseEntity<ResponseDto<ItemDto>> createItem(@RequestBody ItemDto itemDto) {
        Item item = itemService.addItem(itemDto.getName(), itemDto.getPrice(), itemDto.getStock());
        return ResponseEntity.ok(ResponseDto.ok(ItemDto.from(item)));
    }

    /**
     * 기존 상품의 정보를 수정하는 APi 엔드포인트입니다.
     *
     * @param id      수정할 상품의 ID
     * @param itemDto 수정할 상품의 정보
     * @return 수정된 상품 정보를 담고 있는 {@Link ItemDto} 객체를 포함한 응답을 반환합니다.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<ItemDto>> updateItem(@PathVariable("id") Long id, @RequestBody ItemDto itemDto) {
        ItemDto updatedItem = itemService.updateItem(id, itemDto);
        return ResponseEntity.ok(ResponseDto.ok(updatedItem));
    }

    /**
     * 특정 상품을 삭제하는 API 엔드포인트입니다.
     *
     * @param id 삭제할 상품의 ID
     * @return 삭제 완료 메시지를 담고 있는 응답을 반환합니다.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<String>> deleteItem(@PathVariable("id") Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok(ResponseDto.ok("삭제가 완료되었습니다."));
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Object> getItemImage(@PathVariable("id") Long id) {
        Resource resource = itemService.getItemImage(id);
        if (resource == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.notFound("해당 상품 이미지가 존재하지 않습니다. (상품: %d)".formatted(id)));
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<ResponseDto<String>> addImageToItem(@PathVariable("id") Long id, @RequestParam("file") MultipartFile file) {
        if (!Objects.equals(file.getContentType(), "image/jpeg")) {
            return ResponseEntity.badRequest().body(ResponseDto.badRequest("이미지는 JPEG 형식만 지원합니다."));
        }
        return ResponseEntity.ok(ResponseDto.ok(itemService.addImageToItem(id, file)));
    }
}
