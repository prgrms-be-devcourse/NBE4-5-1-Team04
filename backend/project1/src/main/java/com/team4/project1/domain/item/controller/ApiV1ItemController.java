package com.team4.project1.domain.item.controller;

import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.service.ItemService;
import com.team4.project1.global.exception.ItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ApiV1ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto item(@PathVariable Integer itemId) {
        return itemService.getItemById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    @GetMapping
    public List<ItemDto> sortedItems(
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "searchKeyword", required = false) String keyword
            ) {
        if (keyword == null) { keyword = ""; }
        if (sortBy == null || sortBy.isEmpty() && keyword.isEmpty()) {
            return itemService.getAllItems();
        }
        else {
            return itemService.searchAllItemsSortedBy(sortBy, keyword);
        }
    }
}
