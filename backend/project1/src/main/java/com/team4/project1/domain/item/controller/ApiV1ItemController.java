package com.team4.project1.domain.item.controller;

import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.service.ItemService;
import com.team4.project1.global.dto.ResponseDto;
import com.team4.project1.global.exception.ItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ApiV1ItemController {

    private final ItemService itemService;


    @GetMapping("/{itemId}")
    public ResponseEntity<ResponseDto<ItemDto>> item(@PathVariable Long itemId) {
        return ResponseEntity.ok(ResponseDto.ok(
                itemService.getItemById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId)
                )
        ));
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<ItemDto>>> sortedItems(
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "searchKeyword", required = false) String keyword
            ) {
        if (keyword == null) { keyword = ""; }
        if (sortBy == null || sortBy.isEmpty() && keyword.isEmpty()) {
            return ResponseEntity.ok(ResponseDto.ok(itemService.getAllItems()));
        }
        else {
            return ResponseEntity.ok(ResponseDto.ok(itemService.searchAllItemsSortedBy(sortBy, keyword)));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDto<ItemDto>> createItem(@RequestBody ItemDto itemDto) {
        Item item = itemService.addItem(itemDto.getName(), itemDto.getPrice(),itemDto.getStock());
        return ResponseEntity.ok(ResponseDto.ok(ItemDto.from(item)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<ItemDto>> updateItem(@PathVariable("id") Long id, @RequestBody ItemDto itemDto) {
        ItemDto updatedItem = itemService.updateItem(id, itemDto);
        return ResponseEntity.ok(ResponseDto.ok(updatedItem));
    }

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
