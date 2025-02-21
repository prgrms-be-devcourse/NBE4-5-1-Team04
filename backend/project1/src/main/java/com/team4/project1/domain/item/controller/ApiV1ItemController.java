package com.team4.project1.domain.item.controller;

import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.service.ItemService;
import com.team4.project1.global.exception.ItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ApiV1ItemController {

    private final ItemService itemService;

    // 아이템 목록 조회 (정렬 옵션을 파라미터로 받음)
    @GetMapping
    public List<ItemDto> items(@RequestParam(value = "sortBy", required = false) String sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            return itemService.getAllItems();
        } else {
            return itemService.getAllItemsSorted(sortBy);
        }
    }

    // 아이템 조회 (아이디로 조회)
    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> item(@PathVariable Long id) {
        ItemDto itemDto = itemService.getItemById(id)
                .map(ItemDto::new)
                .orElseThrow(() -> new ItemNotFoundException(id));
        return ResponseEntity.ok(itemDto);
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestBody ItemDto itemDto) {
        Item item = itemService.addItem(itemDto.getName(), itemDto.getPrice());
        return ResponseEntity.ok(ItemDto.from(item));
    }


    // 아이템 수정
    @PutMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable("id") Long id, @RequestBody ItemDto itemDto) {
        ItemDto updatedItem = itemService.updateItem(id, itemDto);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
public ResponseEntity<String> deleteItem(@PathVariable("id") Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok("삭제가 완료되었습니다.");
    }
}
