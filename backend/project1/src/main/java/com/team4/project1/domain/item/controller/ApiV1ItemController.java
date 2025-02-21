package com.team4.project1.domain.item.controller;

import com.team4.project1.domain.customer.dto.CustomerDto;
import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.service.CustomerService;
import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.service.ItemService;
import com.team4.project1.global.exception.CustomerNotFoundException;
import com.team4.project1.global.exception.ItemNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ApiV1ItemController {

    private final ItemService itemService;


    @GetMapping
    public List<ItemDto> items(@RequestParam(value = "sortBy", required = false) String sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            return itemService.getAllItems();
        } else {
            return itemService.getAllItemsSorted(sortBy);
        }
    }


    @GetMapping("/{itemId}")
    public ItemDto item(@PathVariable Long itemId) {
        return itemService.getItemById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
    }



    @PutMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable("id") Long id, @RequestBody ItemDto itemDto) {
        Item updated = itemService.updateItem(id, itemDto);
        return ResponseEntity.ok(new ItemDto(updated));
    }
}
