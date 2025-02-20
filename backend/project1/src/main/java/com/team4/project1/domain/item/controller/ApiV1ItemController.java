package com.team4.project1.domain.item.controller;

import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ApiV1ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> items() {
         return itemService.getAllItems();
    }
    //전체 상품 목록 가격순 오름차순 정렬
    @GetMapping("/sorted")
    public List<ItemDto> getItemsSorted() {
        return itemService.getAllItemsSorted();
    }

    @GetMapping("/{itemId}")
    public ItemDto item(@PathVariable Integer itemId) {
        return itemService.getItemById(itemId).orElse(null);
    }


}
