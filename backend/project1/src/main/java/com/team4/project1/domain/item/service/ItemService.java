package com.team4.project1.domain.item.service;

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

    public List<ItemDto> searchAllItemsSortedBy(String sortBy, String keyword) {
        List<Item> items;

        if ("price".equalsIgnoreCase(sortBy)) {
            items = itemRepository.findAllByNameContainingOrderByPriceAsc(keyword);
        }
        else if ("name".equalsIgnoreCase(sortBy)) {
            items = itemRepository.findAllByNameContainingOrderByNameAsc(keyword);
        }
        else {
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

    public Optional<ItemDto> getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        if (item == null) { return Optional.empty(); }
        return Optional.of(ItemDto.from(item));
    }

    public long count() {
        return itemRepository.count();
    }

    public Item addItem(String name, Integer price,Integer quantity) {
        Item item = Item.builder()
                .name(name)
                .price(price)
                .stock(quantity)
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
        return ItemDto.from(updatedItem);  // 수정된 아이템을 반환
    }

    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
        itemRepository.delete(item);
    }
}
