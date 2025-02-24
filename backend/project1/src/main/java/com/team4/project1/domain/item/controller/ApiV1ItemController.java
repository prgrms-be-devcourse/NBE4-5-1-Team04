package com.team4.project1.domain.item.controller;

import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.service.ItemService;
import com.team4.project1.global.dto.ResponseDto;
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


    @GetMapping("/{itemId}")
    public ResponseEntity<ResponseDto<ItemDto>> item(@PathVariable Long itemId) {
        return ResponseEntity.ok(ResponseDto.ok(
                itemService.getItemById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId))
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
        Item item = itemService.addItem(itemDto.getName(), itemDto.getPrice(), itemDto.getStock());
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
}
