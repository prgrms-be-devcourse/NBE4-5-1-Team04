package com.team4.project1.domain.item.service;

import com.team4.project1.domain.customer.dto.CustomerDto;
import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.item.dto.ItemDto;
import com.team4.project1.domain.item.entity.Item;
import com.team4.project1.domain.item.repository.ItemRepository;
import com.team4.project1.global.exception.ItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public List<ItemDto> getAllItemsSorted(String sortBy) {
        List<Item> items;

        if ("price".equalsIgnoreCase(sortBy)) {
            items = itemRepository.findAllByOrderByPriceAsc();
        } else if ("name".equalsIgnoreCase(sortBy)) {
            items = itemRepository.findAllByOrderByNameAsc();
        } else {
            items = itemRepository.findAll();
        }

        return items.stream()
                .map(ItemDto::from)
                .collect(Collectors.toList());
    }

    public List<ItemDto> getAllItems() {
        return itemRepository.findAll().stream()
                .map(ItemDto::from)
                .collect(Collectors.toList());
    }




    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);  // Optional<Item> 반환
    }

    public long count() {
        return itemRepository.count();
    }

    public Item addItem(String name, Integer price) {
        Item item = Item.builder()
                .name(name)
                .price(price)
                .build();

        return itemRepository.save(item);
    }


    public ItemDto updateItem(Long id, ItemDto itemDto) {
        // 1. 기존 아이템을 찾습니다.
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));  // 아이템을 찾지 못하면 예외를 던짐

        // 2. ItemDto의 데이터를 Item 엔티티에 반영합니다.
        item.setName(itemDto.getName());
        item.setPrice(itemDto.getPrice());

        // 3. 수정된 아이템을 저장하고, ItemDto로 반환합니다.
        Item updatedItem = itemRepository.save(item);
        return new ItemDto(updatedItem);  // 수정된 아이템을 반환
    }
}